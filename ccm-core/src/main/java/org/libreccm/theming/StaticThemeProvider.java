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
import org.libreccm.theming.manifest.ThemeManifest;
import org.libreccm.theming.manifest.ThemeManifestUtil;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class StaticThemeProvider implements ThemeProvider {

    private static final long serialVersionUID = 7174370298224448067L;
    private static final Logger LOGGER = LogManager.getLogger(
        StaticThemeProvider.class);

    private static final String THEMES_DIR = "/themes";
    private static final String THEMES_PACKAGE = "themes";
    private static final String THEME_MANIFEST_JSON_PATH = THEMES_DIR
                                                               + "/%s/theme.json";
    private static final String THEME_MANIFEST_XML_PATH = THEMES_DIR
                                                              + "/%s/theme.xml";
    private static final String THEME_MANIFEST_JSON = "theme.json";
    private static final String THEME_MANIFEST_XML = "theme.xml";

    @Inject
    private ThemeManifestUtil manifestUtil;

    @Inject
    private ThemeFileInfoUtil themeFileInfoUtil;

    @Override
    public List<ThemeInfo> getThemes() {

        LOGGER.debug("Retrieving static themes...");

        final Reflections reflections = new Reflections(
            new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(""))
                .setScanners(new ResourcesScanner()
                //                    .filterResultsBy(new FilterBuilder()
                //                        .include(THEMES_PACKAGE)
                //                        .include(THEMES_PACKAGE + "/([\\w\\d\\s\\.]*)/theme.json")
                //                        .include(THEMES_PACKAGE + "/([\\w\\d\\s\\.]*)/theme.xml")
                //                    .exclude(THEMES_PACKAGE + "(.*)/(.*)")
                //                    )
                ));

        final Set<String> jsonThemes = reflections
            .getResources(Pattern.compile(THEME_MANIFEST_JSON));
        final Set<String> xmlThemes = reflections
            .getResources(Pattern.compile(THEME_MANIFEST_XML));
        final List<String> themes = new ArrayList<>();
        themes.addAll(jsonThemes
            .stream()
            .filter(themePackage -> {
                return themePackage
                    .matches(THEMES_PACKAGE + "/([\\w\\d\\s\\.])*/theme.json");
            })
            //            .map(themePackage -> {
            //                return themePackage
            //                    .substring((THEMES_PACKAGE + "/").length(),
            //                               ("/" + THEME_MANIFEST_JSON).length() - 1);
            //            })
            .collect(Collectors.toList()));
        themes.addAll(xmlThemes
            .stream()
            .filter(themePackage -> {
                return themePackage
                    .matches(THEMES_PACKAGE + "/([\\w\\d\\s\\.])*/theme.xml");
            })
            //            .map(themePackage -> {
            //                return themePackage
            //                    .substring((THEMES_PACKAGE + "/").length(),
            //                               ("/" + THEME_MANIFEST_XML).length() - 1);
            //            })
            .collect(Collectors.toList()));
        Collections.sort(themes);

        LOGGER.debug("Found static themes:");
        themes.forEach(theme -> LOGGER.debug("\t{}", theme));

        for (final String theme : themes) {
            final InputStream inputStream = StaticThemeProvider.class
                .getResourceAsStream(String.format("/%s", theme));
            final ThemeManifest manifest = manifestUtil
                .loadManifest(inputStream,
                              theme);
            LOGGER.debug("Got manifest: {}", Objects.toString(manifest));
        }

        return themes
            .stream()
            .map(theme -> loadThemeManifest(theme))
            .map(manifest -> generateThemeInfo(manifest))
            .collect(Collectors.toList());

    }

//    private boolean isThemeDir(final String dirPath) {
//
//        Objects.requireNonNull(dirPath);
//
//        final URL manifestJsonUrl = StaticThemeProvider.class.getResource(
//            String.format(THEME_MANIFEST_JSON_PATH, dirPath));
//        final URL manifestXmlUrl = StaticThemeProvider.class.getResource(
//            String.format(THEME_MANIFEST_XML_PATH, dirPath));
//
//        return (manifestJsonUrl != null) || (manifestXmlUrl != null);
//    }
    private ThemeManifest loadThemeManifest(final String manifestPath) {

        Objects.requireNonNull(manifestPath);

        final String pathToManifest;
        if (manifestPath.startsWith("/")) {
            pathToManifest = manifestPath;
        } else {
            pathToManifest = String.format("/%s", manifestPath);
        }

        final ThemeManifest manifest;
        try (final InputStream inputStream = StaticThemeProvider.class
            .getResourceAsStream(pathToManifest)) {

            manifest = manifestUtil.loadManifest(inputStream, manifestPath);

        } catch (IOException ex) {
            throw new UnexpectedErrorException(ex);
        }

        return manifest;
    }

    private ThemeInfo generateThemeInfo(final ThemeManifest manifest) {

        Objects.requireNonNull(manifest);

        final ThemeInfo info = new ThemeInfo();
        info.setManifest(manifest);
        info.setProvider(getClass());
        info.setVersion(ThemeVersion.LIVE);

        return info;
    }

    @Override
    public List<ThemeInfo> getLiveThemes() {
        return getThemes();
    }

    @Override
    public Optional<ThemeInfo> getThemeInfo(final String theme,
                                            final ThemeVersion version) {

        Objects.requireNonNull(theme);

        final String manifestJsonPath = String.format("/" + THEMES_PACKAGE
                                                          + "/%s/"
                                                          + THEME_MANIFEST_JSON,
                                                      theme);
        final String manifestXmlPath = String.format("/" + THEMES_PACKAGE
                                                         + "/%s/"
                                                         + THEME_MANIFEST_XML,
                                                     theme);

        final URL manifestJsonUrl = StaticThemeProvider.class
            .getResource(manifestJsonPath);
        final URL manifestXmlUrl = StaticThemeProvider.class
            .getResource(manifestXmlPath);

        if (manifestJsonUrl != null) {
            return Optional
                .of(generateThemeInfo(loadThemeManifest(manifestJsonPath)));
        }

        if (manifestXmlUrl != null) {
            return Optional
                .of(generateThemeInfo(loadThemeManifest(manifestXmlPath)));
        }

        return Optional.empty();
    }

    @Override
    public boolean providesTheme(final String theme,
                                 final ThemeVersion version) {

        Objects.requireNonNull(theme);

        final String manifestJsonPath = String.format("/" + THEMES_PACKAGE
                                                          + "/%s/"
                                                          + THEME_MANIFEST_JSON,
                                                      theme);
        final String manifestXmlPath = String.format("/" + THEMES_PACKAGE
                                                         + "/%s/"
                                                         + THEME_MANIFEST_XML,
                                                     theme);

        final URL manifestJsonUrl = StaticThemeProvider.class
            .getResource(manifestJsonPath);
        final URL manifestXmlUrl = StaticThemeProvider.class
            .getResource(manifestXmlPath);

        return manifestJsonUrl != null || manifestXmlUrl != null;
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

        final String[] pathTokens = path.split("/");

        final String indexFilePath = String.format("/"
                                                       + THEMES_PACKAGE
                                                       + "/%s/theme-index.json",
                                                   theme);
        final InputStream stream = getClass()
            .getResourceAsStream(indexFilePath);
        final JsonReader reader = Json.createReader(stream);
        final JsonObject indexObj = reader.readObject();
        final JsonArray filesArray = indexObj.getJsonArray("files");
        filesArray
            .forEach(value -> LOGGER.warn(value.toString()));

        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<InputStream> getThemeFileAsStream(final String theme,
                                                      final ThemeVersion version,
                                                      final String path) {

        Objects.requireNonNull(theme);
        Objects.requireNonNull(path);

        if (theme.isEmpty()
                || theme.matches("\\s*")) {
            throw new IllegalArgumentException(
                "The name of the theme can't be empty.");
        }
        if (path.isEmpty()
                || path.matches("\\s*")) {
            throw new IllegalArgumentException("The path can't be empty.");
        }

        final String filePath;
        if (path.startsWith("/")) {
            filePath = String.format("/" + THEMES_PACKAGE + "/%s/%s",
                                     theme,
                                     path.substring(1));
        } else {
            filePath = String.format("/" + THEMES_PACKAGE + "/%s/%s",
                                     theme,
                                     path);
        }

        final InputStream stream = getClass().getResourceAsStream(filePath);
        if (stream == null) {
            return Optional.empty();
        } else {
            return Optional.of(stream);
        }
    }

    @Override
    public OutputStream getOutputStreamForThemeFile(final String theme,
                                                    final String path) {

        throw new UnsupportedOperationException(String
            .format("This implementation of %s interface does not support "
                        + "changes to the theme files.",
                    ThemeProvider.class.getName()));
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
        //No op in this implementation.
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

}
