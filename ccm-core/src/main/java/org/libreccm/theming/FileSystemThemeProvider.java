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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.files.CcmFiles;
import org.libreccm.files.CcmFilesNotConfiguredException;
import org.libreccm.files.DirectoryNotEmptyException;
import org.libreccm.files.FileAccessException;
import org.libreccm.files.FileAlreadyExistsException;
import org.libreccm.files.FileDoesNotExistException;
import org.libreccm.files.InsufficientPermissionsException;
import org.libreccm.theming.manifest.ThemeManifest;
import org.libreccm.theming.manifest.ThemeManifestUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * {@link ThemeProvider} implementation that loads themes from the file system
 * using {@link CcmFiles}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class FileSystemThemeProvider implements ThemeProvider {

    private static final long serialVersionUID = 1L;
    
    private static final Logger LOGGER = LogManager.getLogger(
        FileSystemThemeProvider.class
    );

    private static final String BASE_PATH = "/themes";
    private static final String DRAFT_THEMES_PATH = BASE_PATH + "/draft";
    private static final String LIVE_THEMES_PATH = BASE_PATH + "/live";

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
        } catch(CcmFilesNotConfiguredException ex) {
            LOGGER.warn(ex);
            return Collections.emptyList();
        }
    }

    @Override
    public List<ThemeInfo> getLiveThemes() {

        try {
            if (!ccmFiles.isDirectory(BASE_PATH)
                    || !ccmFiles.isDirectory(LIVE_THEMES_PATH)) {

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
        } catch(CcmFilesNotConfiguredException ex) {
            LOGGER.warn(ex);
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<ThemeInfo> getThemeInfo(final String theme,
                                            final ThemeVersion version) {

        return readInfo(theme);
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
    public ThemeInfo createTheme(final String themeName) {

        Objects.requireNonNull(themeName);

        if (themeName.isEmpty() || themeName.matches("\\s*")) {
            throw new IllegalArgumentException(
                "The name of a theme can't be empty.");
        }

        try {
            ccmFiles.createDirectory(String.format(DRAFT_THEMES_PATH + "/%s",
                                                   themeName));
        } catch (FileAccessException
                     | FileAlreadyExistsException
                     | InsufficientPermissionsException ex) {
            throw new UnexpectedErrorException(ex);
        }

        final ThemeManifest manifest = new ThemeManifest();
        manifest.setName(themeName);

        final OutputStream outputStream;
        try {
            outputStream = ccmFiles.createOutputStream(
                String.format(DRAFT_THEMES_PATH + "/%s/"
                                  + ThemeConstants.THEME_MANIFEST_JSON,
                              themeName));
        } catch (FileAccessException
                     | InsufficientPermissionsException ex) {
            throw new UnexpectedErrorException(ex);
        }

        try (final OutputStreamWriter writer = new OutputStreamWriter(
            outputStream, StandardCharsets.UTF_8)) {
            writer
                .append(manifestUtil
                    .serializeManifest(manifest,
                                       ThemeConstants.THEME_MANIFEST_JSON));
            writer.flush();
        } catch (IOException ex) {
            throw new UnexpectedErrorException(ex);
        }

        return getThemeInfo(themeName, ThemeVersion.DRAFT).get();
    }

    @Override
    public void deleteTheme(final String themeName) {

        Objects.requireNonNull(themeName);

        if (themeName.isEmpty() || themeName.matches("\\s*")) {
            throw new IllegalArgumentException(
                "The name of a theme can't be empty.");
        }

        final Optional<ThemeInfo> liveTheme = getLiveThemes()
            .stream()
            .filter(theme -> theme.getName().equals(themeName))
            .findAny();

        if (liveTheme.isPresent()) {
            throw new IllegalArgumentException(String
                .format("The theme \"%s\" is live and can't be deleted.",
                        themeName));
        }

        try {
            ccmFiles.deleteFile(String.format(DRAFT_THEMES_PATH + "/%s",
                                              themeName),
                                true);
        } catch (FileAccessException
                     | FileDoesNotExistException
                     | DirectoryNotEmptyException
                     | InsufficientPermissionsException ex) {
            throw new UnexpectedErrorException(ex);
        }
    }

    @Override
    public List<ThemeFileInfo> listThemeFiles(final String theme,
                                              final ThemeVersion version,
                                              final String path) {

        final String themePath = createThemePath(theme, version);
        final String filePath;
        if ("/".equals(path)) {
            filePath = String.join("", themePath, path);
        } else {
            filePath = String.join("/", themePath, path);
        }

        try {
            if (ccmFiles.isDirectory(filePath)) {
                return ccmFiles
                    .listFiles(filePath)
                    .stream()
                    .map(currentPath -> buildThemeFileInfo(
                    themePath,
                    String.join("/", path, currentPath)))
                    .collect(Collectors.toList());
            } else {
                final List<ThemeFileInfo> result = new ArrayList<>();
                final ThemeFileInfo fileInfo = buildThemeFileInfo(
                    themePath, path);
                result.add(fileInfo);

                return result;
            }

        } catch (FileAccessException
                     | FileDoesNotExistException
                     | InsufficientPermissionsException ex) {

            throw new UnexpectedErrorException(ex);
        }
    }

    @Override
    public Optional<ThemeFileInfo> getThemeFileInfo(
        final String theme, final ThemeVersion version, final String path) {

        final String themePath = createThemePath(theme, version);
        final String filePath = String.join("/", themePath, path);

        try {
            if (ccmFiles.existsFile(filePath)) {

                return Optional.of(buildThemeFileInfo(themePath, path));

            } else {
                return Optional.empty();
            }
        } catch (FileAccessException | InsufficientPermissionsException ex) {
            throw new UnexpectedErrorException(ex);
        }
    }

    @Override
    public Optional<InputStream> getThemeFileAsStream(
        final String theme, final ThemeVersion version, final String path) {

        final String themePath = createThemePath(theme, version);
        final String filePath = String.join("/", themePath, path);

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
        final String filePath = String.join("/", themePath, path);

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
        final String filePath = String.join("/", themePath, path);

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
        final String liveThemePathTmp = String.format("%s_tmp", liveThemePath);

        try {
            if (!ccmFiles.existsFile(LIVE_THEMES_PATH)) {
                ccmFiles.createDirectory(LIVE_THEMES_PATH);
            }
        } catch (FileAccessException
                     | InsufficientPermissionsException
                     | FileAlreadyExistsException ex) {
            throw new UnexpectedErrorException(ex);
        }

        try {

            ccmFiles.createDirectory(liveThemePathTmp);

            ccmFiles.copyFile(draftThemePath, liveThemePathTmp, true);
            if (ccmFiles.existsFile(liveThemePath)) {
                ccmFiles.deleteFile(liveThemePath, true);
            }

            ccmFiles.moveFile(liveThemePathTmp, liveThemePath);

        } catch (DirectoryNotEmptyException
                     | FileAccessException
                     | FileAlreadyExistsException
                     | FileDoesNotExistException
                     | InsufficientPermissionsException ex) {
            throw new UnexpectedErrorException(ex);
        }
    }

    @Override
    public void unpublishTheme(final String theme) {

        final String liveThemePath = createThemePath(theme,
                                                     ThemeVersion.LIVE);
        try {
            ccmFiles.deleteFile(liveThemePath, true);
        } catch (DirectoryNotEmptyException
                     | FileAccessException
                     | FileDoesNotExistException
                     | InsufficientPermissionsException ex) {
            throw new UnexpectedErrorException(ex);
        }
    }

    private String createThemePath(final String theme,
                                   final ThemeVersion version) {

        switch (version) {

            case DRAFT:
                return String.format(DRAFT_THEMES_PATH + "/%s", theme);
            case LIVE:
                return String.format(LIVE_THEMES_PATH + "/%s", theme);
            default:
                throw new IllegalArgumentException(String
                    .format("Illegal argument for ThemeVersion \"%s\".",
                            version));
        }
    }

    private Optional<ThemeInfo> readInfo(final String themeName) {

        final ThemeManifest manifest;
        try {

            final String jsonPath = String.format(
                DRAFT_THEMES_PATH + "/" + THEME_JSON, themeName);
            final String xmlPath = String.format(
                DRAFT_THEMES_PATH + "/" + THEME_XML, themeName);

            if (ccmFiles.existsFile(jsonPath)) {
                final InputStream inputStream = ccmFiles
                    .createInputStream(jsonPath);
                manifest = manifestUtil.loadManifest(inputStream, "theme.json");
            } else if (ccmFiles.existsFile(xmlPath)) {
                final InputStream inputStream = ccmFiles
                    .createInputStream(xmlPath);
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

    private ThemeFileInfo buildThemeFileInfo(final String themePath,
                                             final String filePath) {

        final String path;
        if (themePath.endsWith("/")
            || filePath.startsWith("/")) {
            path = String.join("", themePath, filePath);
        } else {
            path= String.join("/", themePath, filePath);
        }
        
        final String name;
        if (path.startsWith(("/"))) {
            name = path;
        } else {
            name = String.format("/%s", path);
        }

        final ThemeFileInfo fileInfo = new ThemeFileInfo();

        try {
            fileInfo.setDirectory(ccmFiles.isDirectory(path));
            fileInfo.setMimeType(ccmFiles.getMimeType(path));
            fileInfo.setName(name);
            fileInfo.setWritable(true);

            return fileInfo;
        } catch (FileAccessException
                     | FileDoesNotExistException
                     | InsufficientPermissionsException ex) {

            throw new UnexpectedErrorException(ex);
        }
    }

}
