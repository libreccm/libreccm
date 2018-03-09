/*
 * Copyright (C) 2018 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.libreccm.theming.db;

import org.libreccm.security.RequiresPrivilege;
import org.libreccm.theming.ThemeConstants;
import org.libreccm.theming.ThemeVersion;
import org.libreccm.theming.ThemingPrivileges;
import org.libreccm.theming.manifest.ThemeManifest;
import org.libreccm.theming.manifest.ThemeManifestUtil;

import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Provides methods for managing themes stored in the database.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ThemeManager {

    @Inject
    private ThemeManifestUtil manifestUtil;

    @Inject
    private ThemeRepository themeRepository;

    @Inject
    private ThemeFileRepository themeFileRepository;

    /**
     * Creates a new theme, including the root directory and a theme manifest
     * file.
     *
     * @see ThemeManifest
     *
     * @param name The name of the theme.
     *
     * @return The new theme.
     */
    @RequiresPrivilege(ThemingPrivileges.ADMINISTER_THEMES)
    @Transactional(Transactional.TxType.REQUIRED)
    public Theme createTheme(final String name) {

        Objects.requireNonNull(name);

        if (name.matches("\\s*")) {
            throw new IllegalArgumentException(
                "The name of a theme can't be empty.");
        }

        final Theme theme = new Theme();
        theme.setName(name);
        theme.setVersion(ThemeVersion.DRAFT);

        final Directory root = new Directory();
        root.setName(name);
        root.setPath("/");
        root.setTheme(theme);

        final ThemeManifest manifest = new ThemeManifest();
        manifest.setName(name);

        final DataFile manifestFile = new DataFile();
        manifestFile.setName(ThemeConstants.THEME_MANIFEST_JSON);
        manifestFile.setPath(String.format("/%s",
                                           ThemeConstants.THEME_MANIFEST_JSON));
        manifestFile.setTheme(theme);

        final String manifestData = manifestUtil
            .serializeManifest(manifest, ThemeConstants.THEME_MANIFEST_JSON);
        manifestFile.setData(manifestData.getBytes());
        root.addFile(manifestFile);

        themeRepository.save(theme);
        themeFileRepository.save(root);
        themeFileRepository.save(manifestFile);

        return theme;
    }

    /**
     * Deletes a theme. The theme must be published.
     *
     * @param theme The theme to delete.
     */
    @RequiresPrivilege(ThemingPrivileges.ADMINISTER_THEMES)
    @Transactional(Transactional.TxType.REQUIRED)
    public void deleteTheme(final Theme theme) {

        Objects.requireNonNull(theme);

        if (isLive(theme)) {
            throw new IllegalArgumentException(String
                .format("The theme \"%s\" is live and can't be deleted.",
                        theme.getName()));
        }

        themeRepository.delete(theme);
    }

    /**
     * Checks if a theme has a live version.
     *
     * @param theme The theme.
     *
     * @return {@code true} if there is a live version of the provided theme,
     *         {@code false} otherwise.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public boolean isLive(final Theme theme) {

        Objects.requireNonNull(theme);

        return themeRepository
            .findThemeByUuid(theme.getUuid(), ThemeVersion.LIVE)
            .isPresent();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Theme getDraftTheme(final Theme theme) {

        Objects.requireNonNull(theme);

        if (theme.getVersion() == ThemeVersion.DRAFT) {
            return theme;
        } else {
            return themeRepository
                .findThemeByUuid(theme.getUuid(), ThemeVersion.DRAFT)
                .orElseThrow(() -> new IllegalArgumentException(String
                .format("No draft theme with UUID \"%s\" in the database.",
                        theme.getUuid())));
        }

    }

    /**
     * Retrieves the live version of a theme.
     *
     * @param theme The theme.
     *
     * @return An {@link Optional} containing the live version of the provided
     *         theme or an empty {@link Optional} if the theme has no live
     *         version.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<Theme> getLiveTheme(final Theme theme) {

        Objects.requireNonNull(theme);

        return themeRepository
            .findThemeByUuid(theme.getUuid(), ThemeVersion.LIVE);
    }

    /**
     * Publishes a theme. This method will delete the current live version of
     * the theme (if there is a live version) and create a new live version by
     * copying all files from the draft version.
     *
     * @param theme The theme to publish. If the live version of a theme is
     *              passed here the method will lookup the draft version first.
     *
     */
    @RequiresPrivilege(ThemingPrivileges.ADMINISTER_THEMES)
    @Transactional(Transactional.TxType.REQUIRED)
    public void publishTheme(final Theme theme) {

        Objects.requireNonNull(theme);

        final Theme draftTheme;
        if (theme.getVersion() == ThemeVersion.DRAFT) {
            draftTheme = theme;
        } else {
            draftTheme = getDraftTheme(theme);
        }

        if (isLive(draftTheme)) {
            unpublishTheme(draftTheme);
        }

        final Theme liveTheme = new Theme();
        liveTheme.setName(draftTheme.getName());
        liveTheme.setUuid(draftTheme.getUuid());
        liveTheme.setVersion(ThemeVersion.LIVE);

        final Directory liveRoot = new Directory();
        liveRoot.setName(draftTheme.getRootDirectory().getName());
        liveRoot.setPath(draftTheme.getRootDirectory().getPath());
        liveRoot.setUuid(draftTheme.getRootDirectory().getUuid());
        liveRoot.setTheme(theme);
        liveRoot.setVersion(ThemeVersion.LIVE);

        themeRepository.save(liveTheme);
        themeFileRepository.save(liveRoot);

        draftTheme
            .getRootDirectory()
            .getFiles()
            .forEach(file -> publishFile(liveTheme, liveRoot, file));

        throw new UnsupportedOperationException();
    }

    private void publishFile(final Theme liveTheme,
                             final Directory liveParent,
                             final ThemeFile draftFile) {

        Objects.requireNonNull(liveParent);
        Objects.requireNonNull(draftFile);

        if (liveParent.getVersion() != ThemeVersion.LIVE) {
            throw new IllegalArgumentException("Parent directory is not live.");
        }

        if (draftFile.getVersion() != ThemeVersion.DRAFT) {
            throw new IllegalArgumentException("File to publish is not draft.");
        }

        if (draftFile instanceof Directory) {

            final Directory draftDirectory = (Directory) draftFile;

            final Directory liveDirectory = new Directory();
            liveDirectory.setName(draftDirectory.getName());
            liveDirectory.setPath(draftDirectory.getPath());
            liveDirectory.setParent(liveParent);
            liveDirectory.setUuid(draftDirectory.getUuid());
            liveDirectory.setVersion(ThemeVersion.LIVE);
            liveDirectory.setTheme(liveTheme);

            themeFileRepository.save(liveDirectory);

            draftDirectory
                .getFiles()
                .forEach(file -> publishFile(liveTheme, liveDirectory, file));

        } else if (draftFile instanceof DataFile) {

            final DataFile draftDataFile = (DataFile) draftFile;

            final DataFile liveDataFile = new DataFile();
            liveDataFile.setCreationDate(draftDataFile.getCreationDate());
            liveDataFile.setData(draftDataFile.getData());
            liveDataFile.setLastModified(draftDataFile.getLastModified());
            liveDataFile.setName(draftDataFile.getName());
            liveDataFile.setParent(liveParent);
            liveDataFile.setPath(draftDataFile.getPath());
            liveDataFile.setSize(draftDataFile.getSize());
            liveDataFile.setType(draftDataFile.getType());
            liveDataFile.setUuid(draftDataFile.getUuid());
            liveDataFile.setTheme(liveTheme);
            liveDataFile.setVersion(ThemeVersion.LIVE);

            themeFileRepository.save(liveDataFile);
        } else {
            throw new IllegalArgumentException(String
                .format("Don't know how handle file type \"%s\".",
                        draftFile.getClass().getName()));
        }
    }

    /**
     * Unpublishes a theme by deleting the live version of the theme. If the
     * theme is not published the method will return without doing anything.
     *
     * @param theme The theme to unpublish.
     */
    @RequiresPrivilege(ThemingPrivileges.ADMINISTER_THEMES)
    @Transactional(Transactional.TxType.REQUIRED)
    public void unpublishTheme(final Theme theme) {

        Objects.requireNonNull(theme);

        if (!isLive(theme)) {
            return;
        }

        final Theme liveTheme = getLiveTheme(theme).get();
        final Directory liveRoot = liveTheme.getRootDirectory();

        liveRoot
            .getFiles()
            .forEach(file -> unpublishFile(file));
    }

    private void unpublishFile(final ThemeFile themeFile) {

        Objects.requireNonNull(themeFile);
        if (themeFile.getVersion() != ThemeVersion.LIVE) {
            throw new IllegalArgumentException(
                "Only live files can be unpublished.");
        }

        if (themeFile instanceof DataFile) {
            themeFileRepository.delete(themeFile);
        } else if (themeFile instanceof Directory) {
            final Directory directory = (Directory) themeFile;
            directory
                .getFiles()
                .forEach(file -> unpublishFile(file));
            themeFileRepository.delete(themeFile);
        } else {
            throw new IllegalArgumentException(String
                .format("Don't know how handle file type \"%s\".",
                        themeFile.getClass().getName()));
        }

    }

}
