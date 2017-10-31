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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

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
    private static final String THEME_MANIFEST_JSON = THEMES_DIR
                                                          + "/%s/theme.json";
    private static final String THEME_MANIFEST_XML = THEMES_DIR
                                                         + "/%s/theme.xml";

    @Inject
    private ThemeManifestUtil manifestUtil;

    @Override
    public List<ThemeInfo> getThemes() {

        final Reflections reflections = new Reflections(
            new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("themes"))
                .setScanners(new ResourcesScanner()));
        final Set<String> resources = reflections
            .getResources(Pattern.compile("themes/.*/theme.json"));
        LOGGER.debug("Found resources:");
        for (final String resource : resources) {
            LOGGER.debug("\t{}", resource);
        }

        final URL themesUrl = StaticThemeProvider.class.getResource(THEMES_DIR);

        if (themesUrl == null) {
            throw new UnexpectedErrorException(
                "Static themes directory does not"
                    + "exist in class path. Something is wrong.");
        }

        File directory;
        try {
            directory = new File(themesUrl.toURI());
        } catch (URISyntaxException ex) {
            throw new UnexpectedErrorException(ex);
        } catch (IllegalArgumentException ex) {
            directory = null;
        }

        final List<String> themeDirs = new ArrayList<>();
        if (directory != null && directory.exists()) {
            final String[] files = directory.list();
            for (final String file : files) {
                themeDirs.add(file);
            }
        } else {
            final String jarPath = themesUrl
                .getFile()
                .replaceFirst("[.]jar[!].*", ".jar")
                .replaceFirst("file:", "");

            final JarFile jarFile;
            try {
                jarFile = new JarFile(jarPath);
            } catch (IOException ex) {
                throw new UnexpectedErrorException(ex);
            }
            final Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
                final JarEntry jarEntry = jarEntries.nextElement();
                final String jarEntryName = jarEntry.getName();

                if (jarEntryName.startsWith("themes")
                        && jarEntryName.length() > "themes/".length()
                        && jarEntryName.endsWith("/")) {
                    themeDirs.add(jarEntryName);
                }
            }
        }

        return themeDirs
            .stream()
            .filter(dirPath -> isThemeDir(dirPath))
            .map(dirPath -> loadThemeManifest(dirPath))
            .map(manifest -> generateThemeInfo(manifest))
            .collect(Collectors.toList());
    }

    private boolean isThemeDir(final String dirPath) {

        Objects.requireNonNull(dirPath);

        final URL manifestJsonUrl = StaticThemeProvider.class.getResource(
            String.format(THEME_MANIFEST_JSON, dirPath));
        final URL manifestXmlUrl = StaticThemeProvider.class.getResource(
            String.format(THEME_MANIFEST_XML, dirPath));

        return (manifestJsonUrl != null) || (manifestXmlUrl != null);
    }

    private ThemeManifest loadThemeManifest(final String dirPath) {

        Objects.requireNonNull(dirPath);

        final URL manifestJsonUrl = StaticThemeProvider.class.getResource(
            String.format(THEME_MANIFEST_JSON, dirPath));
        final URL manifestXmlUrl = StaticThemeProvider.class.getResource(
            String.format(THEME_MANIFEST_XML, dirPath));

        final URL manifestUrl;
        if (manifestJsonUrl != null) {
            manifestUrl = manifestJsonUrl;
        } else if (manifestXmlUrl != null) {
            manifestUrl = manifestXmlUrl;
        } else {
            throw new IllegalArgumentException(String
                .format("Path \"%s\" does not point to a valid theme manifest.",
                        dirPath));
        }

        final ThemeManifest themeManifest;
        try (final InputStream inputStream = manifestUrl.openStream()) {

            themeManifest = manifestUtil.loadManifest(inputStream,
                                                      manifestUrl.toString());

        } catch (IOException ex) {
            throw new UnexpectedErrorException(ex);
        }

        return themeManifest;
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

        if (isThemeDir(theme)) {
            return Optional.of(generateThemeInfo(loadThemeManifest(theme)));
        } else {
            return Optional.empty();
        }

    }

    @Override
    public boolean providesTheme(final String theme,
                                 final ThemeVersion version) {

        Objects.requireNonNull(theme);

        return isThemeDir(theme);

    }

    @Override
    public List<ThemeFileInfo> listThemeFiles(final String theme,
                                              final ThemeVersion version,
                                              final String path) {

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
        //No op in this implementation.
    }

}
