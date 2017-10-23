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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.core.UnexpectedErrorException;

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

/**
 * A theme provider implementation which serves themes from the class path
 * ({@code /themes)}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class StaticThemeProvider implements ThemeProvider {

    private static final Logger LOGGER = LogManager
        .getLogger(StaticThemeProvider.class);

    private static final String THEMES_DIR = "/themes";
    private static final String THEME_XML = "theme.xml";
    private static final String THEME_JSON = "theme.json";

    @Override
    public List<ThemeInfo> getThemes() {

        LOGGER.debug("Retrieving info about all static themes...");

        final List<ThemeInfo> themeInfos = new ArrayList<>();
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
    public Optional<ThemeInfo> getThemeInfo(String theme, ThemeVersion version) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean providesTheme(String theme, ThemeVersion version) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ThemeFileInfo> listThemeFiles(String theme, ThemeVersion version,
                                              String path) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional<InputStream> getThemeFileAsStream(String theme,
                                                      ThemeVersion version,
                                                      String path) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public OutputStream getOutputStreamForThemeFile(String theme, String path) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

        final Path manifestPathJson = path.resolve(THEME_JSON);
        final Path manifestPathXml = path.resolve(THEME_XML);

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

        final Path manifestPathJson = path.resolve(THEME_JSON);
        final Path manifestPathXml = path.resolve(THEME_XML);

        if (Files.exists(manifestPathJson)) {
            return generateThemeInfoFromJson(manifestPathJson);
        } else if (Files.exists(manifestPathXml)) {
            return generateThemeInfoFromXml(manifestPathXml);
        } else {
            throw new IllegalArgumentException(String
                .format("The provided path \"%s\" does "
                            + "contain a theme manifest file.",
                        path.toString()));
        }
    }

    private ThemeInfo generateThemeInfoFromJson(final Path path) {
        throw new UnsupportedOperationException();
    }

    private ThemeInfo generateThemeInfoFromXml(final Path path) {
        throw new UnsupportedOperationException();
    }

}
