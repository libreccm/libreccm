/*
 * Copyright (C) 2017 LibreCCM Foundation.
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

import static org.libreccm.theming.ThemeConstants.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.theming.manifest.ThemeManifest;
import org.libreccm.theming.manifest.ThemeManifestUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * A theme provider implementation which serves themes from the class path
 * ({@code /themes)}. This implementation does not support changes to the
 * theme(s) and files.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class StaticThemeProvider implements ThemeProvider {

    private static final Logger LOGGER = LogManager
        .getLogger(StaticThemeProvider.class);

    /**
     * Path the the static themes.
     */
    private static final String THEMES_DIR = "/themes";

    @Inject
    private ThemeFileInfoUtil themeFileInfoUtil;

    @Inject
    private ThemeManifestUtil themeManifests;

    @Override
    public List<ThemeInfo> getThemes() {

        LOGGER.debug("Retrieving info about all static themes...");

        try (final FileSystem jarFileSystem = FileSystems.newFileSystem(
            getJarUri(), Collections.emptyMap())) {

            final Path themesPath = jarFileSystem.getPath(THEMES_DIR);
            if (!Files.isDirectory(themesPath)) {
                LOGGER.warn(THEMES_DIR + " is not a directory. Returning "
                                + "empty list.");
                return Collections.emptyList();
            }

            return Files
                .list(themesPath)
                .filter(this::isTheme)
                .map(this::generateThemeInfo)
                .collect(Collectors.toList());

        } catch (IOException ex) {
            throw new UnexpectedErrorException(ex);
        }
    }

    @Override
    public List<ThemeInfo> getLiveThemes() {
        return getThemes();
    }

    @Override
    public Optional<ThemeInfo> getThemeInfo(final String theme,
                                            final ThemeVersion version) {

        Objects.requireNonNull(theme);
        Objects.requireNonNull(version);

        if (theme.matches("\\s*")) {
            throw new IllegalArgumentException(
                "The name of the theme can't be empty.");
        }

        LOGGER.debug("Trying to find static theme \"{}\"...",
                     theme);

        try (final FileSystem jarFileSystem = FileSystems
            .newFileSystem(getJarUri(), Collections.emptyMap())) {

            final Path themePath = jarFileSystem
                .getPath(String.format(THEMES_DIR + "/%s", theme));

            if (isTheme(themePath)) {
                return Optional.of(generateThemeInfo(themePath));
            } else {
                return Optional.empty();
            }

        } catch (IOException ex) {
            throw new UnexpectedErrorException(ex);
        }
    }

    @Override
    public boolean providesTheme(final String theme,
                                 final ThemeVersion version) {

        Objects.requireNonNull(theme);
        Objects.requireNonNull(version);

        if (theme.isEmpty() || theme.matches("\\s*")) {
            throw new IllegalArgumentException(
                "The name of the theme can't be empty.");
        }

        LOGGER.debug("Determining if there is static theme \"{}\"...",
                     theme);

        try (final FileSystem jarFileSystem = FileSystems
            .newFileSystem(getJarUri(), Collections.emptyMap())) {

            final Path themePath = jarFileSystem
                .getPath(String.format(THEMES_DIR + "/%s", theme));

            LOGGER.debug("Is there a static theme \"{}\": {}",
                         theme,
                         isTheme(themePath));
            return isTheme(themePath);

        } catch (IOException ex) {
            throw new UnexpectedErrorException(ex);
        }
    }

    @Override
    public List<ThemeFileInfo> listThemeFiles(final String theme,
                                              final ThemeVersion version,
                                              final String path) {

        Objects.requireNonNull(theme);
        Objects.requireNonNull(version);
        Objects.requireNonNull(path);

        if (theme.isEmpty() || theme.matches("\\s*")) {
            throw new IllegalArgumentException(
                "The name of the theme can't be empty.");
        }

        final String pathToDir;
        if ("".equals(path)) {
            pathToDir = "/";
        } else {
            pathToDir = path;
        }

        LOGGER.debug("Listing all files in path \"{]\" of theme \"{}\"...",
                     path,
                     theme);

        final List<ThemeFileInfo> infos;
        try (final FileSystem jarFileSystem = FileSystems
            .newFileSystem(getJarUri(), Collections.emptyMap())) {

            final Path themePath = jarFileSystem
                .getPath(String.format(THEMES_DIR + "/%s", theme));

            if (!isTheme(themePath)) {
                throw new IllegalArgumentException(String
                    .format("Theme \"%s\" does not exist.",
                            theme));
            }

            final Path dirPath = themePath.resolve(pathToDir);
            if (Files.exists(dirPath)) {

                if (Files.isDirectory(dirPath)) {

                    try (final Stream<Path> stream = Files.list(dirPath)) {
                        infos = stream
                            .map(themeFileInfoUtil::buildThemeInfo)
                            .collect(Collectors.toList());
                    }
                } else {
                    infos = new ArrayList<>();
                    infos.add(themeFileInfoUtil.buildThemeInfo(dirPath));
                }
            } else {
                throw new IllegalArgumentException(String
                    .format("No file/directory \"%s\" in theme \"%s\".",
                            path,
                            theme));
            }

        } catch (IOException ex) {
            throw new UnexpectedErrorException(ex);
        }

        LOGGER.debug("Files in path \"{}\" of static theme \"{}\": {}",
                     pathToDir,
                     theme,
                     Objects.toString(infos));
        return infos;
    }

    @Override
    public Optional<InputStream> getThemeFileAsStream(final String theme,
                                                      final ThemeVersion version,
                                                      final String path) {
        Objects.requireNonNull(theme);
        Objects.requireNonNull(version);
        Objects.requireNonNull(path);

        if (theme.isEmpty() || theme.matches("\\s*")) {
            throw new IllegalArgumentException(
                "The name of the theme can't be empty.");
        }

        if (path.isEmpty() || path.matches("\\s*")) {
            throw new IllegalArgumentException(
                "The name of the theme can't be empty.");
        }

        try (final FileSystem jarFileSystem = FileSystems
            .newFileSystem(getJarUri(), Collections.emptyMap())) {

            final Path themePath = jarFileSystem
                .getPath(String.format(THEMES_DIR + "/%s", theme));

            final Path filePath;
            if (path.charAt(0) == '/') {
                filePath = themePath.resolve(path.substring(1));
            } else {
                filePath = themePath.resolve(path);
            }

            if (!Files.isRegularFile(filePath)) {
                throw new IllegalArgumentException(String
                    .format("The provided path \"%s\" in theme \"%s\" points "
                                + "not to a regular file.",
                            path,
                            theme));
            }

            if (Files.exists(filePath)) {
                return Optional.of(Files.newInputStream(filePath));
            } else {
                return Optional.empty();
            }

        } catch (IOException ex) {
            throw new UnexpectedErrorException(ex);
        }

    }

    @Override
    public OutputStream getOutputStreamForThemeFile(final String theme,
                                                    final String path) {

        throw new UnsupportedOperationException("Not supported by this"
                                                    + " implemetentation");
    }

    @Override
    public boolean supportsChanges() {
        return false;
    }

    @Override
    public boolean supportsDraftThemes() {
        return false;
    }

    @Override
    public void publishTheme(final String theme) {
        LOGGER.info("StaticThemeProvider#publishTheme(String) called, but "
                        + "StaticThemeProvider does not support draft/live "
                        + "themes.");
    }

    private URI getJarUri() {

        LOGGER.debug("Getting URI of JAR...");

        final String themesUrl = getClass().getResource(THEMES_DIR).toString();
        LOGGER.debug("Full URL of " + THEMES_DIR + " directory: {}", themesUrl);

        final int index = themesUrl.indexOf('!');
        final String pathToJar = themesUrl.substring(0, index);

        final URI uri = URI.create(pathToJar);
        LOGGER.debug("URI to JAR is \"%s\".", uri.toString());
        return uri;
    }

    private boolean isTheme(final Path path) {

        Objects.requireNonNull(path);

        if (!Files.isDirectory(path)) {
            return false;
        }

        final Path manifestPathJson = path.resolve(THEME_MANIFEST_JSON);
        final Path manifestPathXml = path.resolve(THEME_MANIFEST_XML);

        return Files.exists(manifestPathJson) || Files.exists(manifestPathXml);
    }

    private ThemeInfo generateThemeInfo(final Path path) {

        Objects.requireNonNull(path);

        if (!Files.exists(path)) {
            throw new IllegalArgumentException(String
                .format("The provided path \"%s\" does "
                            + "not exist.",
                        path.toString()));
        }

        final Path manifestPathJson = path.resolve(THEME_MANIFEST_JSON);
        final Path manifestPathXml = path.resolve(THEME_MANIFEST_XML);

        final ThemeManifest manifest;
        if (Files.exists(manifestPathJson)) {
            manifest = themeManifests.loadManifest(manifestPathJson);
        } else if (Files.exists(manifestPathXml)) {
            manifest = themeManifests.loadManifest(manifestPathXml);
        } else {
            throw new IllegalArgumentException(String
                .format("The provided path \"%s\" does "
                            + "contain a theme manifest file.",
                        path.toString()));
        }

        final ThemeInfo themeInfo = new ThemeInfo();
        themeInfo.setVersion(ThemeVersion.LIVE);
        themeInfo.setProvider(getClass());
        themeInfo.setManifest(manifest);

        return themeInfo;
    }

}
