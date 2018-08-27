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
package org.libreccm.theming;

import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.files.CcmFiles;
import org.libreccm.files.DirectoryNotEmptyException;
import org.libreccm.files.FileAccessException;
import org.libreccm.files.FileDoesNotExistException;
import org.libreccm.files.InsufficientPermissionsException;
import org.libreccm.theming.manifest.ThemeManifest;
import org.libreccm.theming.manifest.ThemeManifestUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class FileSystemThemeProvider implements ThemeProvider {

    private static final long serialVersionUID = 1L;

    private static final String BASE_PATH = "/themes";
    private static final String DRAFT_THEMES_PATH = "/themes" + "/draft";
    private static final String LIVE_THEMES_PATH = "/themes" + "/live";

    private static final String THEME_JSON = "%s/theme.json";
    private static final String THEME_XML = "%s/theme.xml";

    @Inject
    private CcmFiles ccmFiles;

    @Inject
    private ThemeManifestUtil manifestUtil;

    @Inject
    private ThemeFileInfoUtil themeFileInfoUtil;

    @Override
    public List<ThemeInfo> getThemes() {

        try {

            if (!ccmFiles.isDirectory(BASE_PATH)
                    || !ccmFiles.isDirectory(DRAFT_THEMES_PATH)) {

                return Collections.emptyList();
            }

            return ccmFiles
                .listFiles(DRAFT_THEMES_PATH)
                .stream()
                .map(themePath -> readInfo(themePath))
                .filter(info -> info.isPresent())
                .map(info -> info.get())
                .collect(Collectors.toList());

        } catch (FileAccessException
                     | FileDoesNotExistException
                     | InsufficientPermissionsException ex) {

            throw new UnexpectedErrorException(ex);
        }
    }

    @Override
    public List<ThemeInfo> getLiveThemes() {

        try {
            if (!ccmFiles.isDirectory(BASE_PATH)
                    || !ccmFiles.isDirectory(DRAFT_THEMES_PATH)) {

                return Collections.emptyList();
            }

            return ccmFiles
                .listFiles(LIVE_THEMES_PATH)
                .stream()
                .map(themePath -> readInfo(themePath))
                .filter(info -> info.isPresent())
                .map(info -> info.get())
                .collect(Collectors.toList());
        } catch (FileAccessException
                     | FileDoesNotExistException
                     | InsufficientPermissionsException ex) {

            throw new UnexpectedErrorException(ex);
        }
    }

    @Override
    public Optional<ThemeInfo> getThemeInfo(final String theme,
                                            final ThemeVersion version) {

        final String themePath = createThemePath(theme, version);
        return readInfo(themePath);
    }

    @Override
    public boolean providesTheme(final String theme,
                                 final ThemeVersion version) {

        final String themePath = createThemePath(theme, version);

        try {
            return ccmFiles.existsFile(themePath);
        } catch (FileAccessException | InsufficientPermissionsException ex) {
            throw new UnexpectedErrorException(ex);
        }
    }

    @Override
    public List<ThemeFileInfo> listThemeFiles(final String theme,
                                              final ThemeVersion version,
                                              final String path) {

        final String themePath = createThemePath(theme, version);
        final String filePath = String.join(themePath, path, "/");

        try {

            return ccmFiles
                .listFiles(filePath)
                .stream()
                .map(currentPath -> buildThemeFileInfo(currentPath))
                .collect(Collectors.toList());

        } catch (FileAccessException
                     | FileDoesNotExistException
                     | InsufficientPermissionsException ex) {

            throw new UnexpectedErrorException(ex);
        }
    }

    @Override
    public Optional<InputStream> getThemeFileAsStream(
        final String theme, final ThemeVersion version, final String path) {

        final String themePath = createThemePath(theme, version);
        final String filePath = String.join(theme, path, "/");

        try {
            if (ccmFiles.existsFile(path)) {
                return Optional.empty();
            } else {
                return Optional.of(ccmFiles.createInputStream(filePath));
            }
        } catch (FileAccessException
                     | FileDoesNotExistException
                     | InsufficientPermissionsException ex) {

            throw new UnexpectedErrorException(ex);
        }
    }

    @Override
    public OutputStream getOutputStreamForThemeFile(final String theme,
                                                    final String path) {

        final String themePath = createThemePath(theme, ThemeVersion.DRAFT);
        final String filePath = String.join(themePath, path, "/");

        try {

            return ccmFiles.createOutputStream(filePath);

        } catch (FileAccessException
                     | InsufficientPermissionsException ex) {

            throw new UnexpectedErrorException(ex);
        }
    }

    @Override
    public void deleteThemeFile(final String theme, final String path) {

        final String themePath = createThemePath(theme, ThemeVersion.DRAFT);
        final String filePath = String.join(themePath, path, "/");

        try {
            ccmFiles.deleteFile(filePath, true);
        } catch (FileAccessException
                     | FileDoesNotExistException
                     | DirectoryNotEmptyException
                     | InsufficientPermissionsException ex) {

            throw new UnexpectedErrorException(ex);
        }

    }

    @Override
    public boolean supportsChanges() {
        return true;
    }

    @Override
    public boolean supportsDraftThemes() {
        return true;
    }

    @Override
    public void publishTheme(final String theme) {

        final String draftThemePath = createThemePath(theme,
                                                      ThemeVersion.DRAFT);
        final String liveThemePath = createThemePath(theme,
                                                     ThemeVersion.LIVE);
        final String liveThemePathTmp = String.format("%_tmp", liveThemePath);

        try {
            ccmFiles.copyFile(draftThemePath, liveThemePathTmp, true);
            if (ccmFiles.existsFile(liveThemePath)) {
                ccmFiles.deleteFile(liveThemePath, true);
            }
            
            ccmFiles.moveFile(liveThemePathTmp, liveThemePath);
        } catch (DirectoryNotEmptyException
                 | FileAccessException 
                 | FileDoesNotExistException
                 | InsufficientPermissionsException ex) {
            throw new UnexpectedErrorException();
        } 
    }

    private String createThemePath(final String theme,
                                   final ThemeVersion version) {

        switch (version) {

            case DRAFT:
                return String.format(DRAFT_THEMES_PATH, theme);
            case LIVE:
                return String.format(LIVE_THEMES_PATH, theme);
            default:
                throw new IllegalArgumentException(String
                    .format("Illegal argument for ThemeVersion \"%s\".",
                            version));
        }
    }

    private Optional<ThemeInfo> readInfo(final String themePath) {

        final ThemeManifest manifest;
        try {
            final InputStream inputStream = ccmFiles
                .createInputStream(String.format(THEME_JSON,
                                                 themePath));
            if (ccmFiles.existsFile(String.format(THEME_JSON,
                                                  themePath))) {

                manifest = manifestUtil.loadManifest(inputStream, "theme.json");
            } else if (ccmFiles.existsFile(String.format(THEME_XML,
                                                         themePath))) {
                manifest = manifestUtil.loadManifest(inputStream, "theme.xml");
            } else {
                return Optional.empty();
            }
        } catch (FileAccessException
                     | FileDoesNotExistException
                     | InsufficientPermissionsException ex) {

            throw new UnexpectedErrorException(ex);
        }

        final ThemeInfo themeInfo = new ThemeInfo();
        themeInfo.setManifest(manifest);

        return Optional.of(themeInfo);
    }

    private ThemeFileInfo buildThemeFileInfo(final String filePath) {

        final ThemeFileInfo fileInfo = new ThemeFileInfo();

        try {
            fileInfo.setDirectory(ccmFiles.isDirectory(filePath));
            fileInfo.setMimeType(ccmFiles.getMimeType(filePath));
            fileInfo.setName(filePath);
            fileInfo.setWritable(true);

            return fileInfo;
        } catch (FileAccessException
                     | FileDoesNotExistException
                     | InsufficientPermissionsException ex) {

            throw new UnexpectedErrorException(ex);
        }
    }

}
