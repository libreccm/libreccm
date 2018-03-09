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

import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.theming.ThemeConstants;
import org.libreccm.theming.ThemeFileInfo;
import org.libreccm.theming.ThemeInfo;
import org.libreccm.theming.ThemeProvider;
import org.libreccm.theming.ThemeVersion;
import org.libreccm.theming.manifest.ThemeManifest;
import org.libreccm.theming.manifest.ThemeManifestUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * An implementation of {@link ThemeProvider} which serves themes from the
 * database.
 *
 * Supports all operations.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class DatabaseThemeProvider implements ThemeProvider {

    private static final long serialVersionUID = -8661840420214119753L;

    @Inject
    private ThemeFileManager fileManager;

    @Inject
    private ThemeFileRepository fileRepository;

    @Inject
    private ThemeManifestUtil manifestUtil;

    @Inject
    private ThemeManager themeManager;

    @Inject
    private ThemeRepository themeRepository;

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public List<ThemeInfo> getThemes() {

        return themeRepository
            .findAll(ThemeVersion.DRAFT)
            .stream()
            .map(this::createThemeInfo)
            .sorted()
            .collect(Collectors.toList());
    }

    @Override
    public List<ThemeInfo> getLiveThemes() {

        return themeRepository
            .findAll(ThemeVersion.LIVE)
            .stream()
            .map(this::createThemeInfo)
            .sorted()
            .collect(Collectors.toList());
    }

    @Override
    public Optional<ThemeInfo> getThemeInfo(final String themeName,
                                            final ThemeVersion version) {

        return themeRepository
            .findThemeByName(themeName, version)
            .map(this::createThemeInfo);
    }

    @Override
    public boolean providesTheme(final String theme,
                                 final ThemeVersion version) {

        return themeRepository
            .findThemeByName(theme, version)
            .isPresent();
    }

    @Override
    public List<ThemeFileInfo> listThemeFiles(final String themeName,
                                              final ThemeVersion version,
                                              final String path) {

        final Theme theme = themeRepository
            .findThemeByName(path, version)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Theme \"%s\" in the database.", themeName)));

        final Optional<ThemeFile> themeFile = fileRepository
            .findByPath(theme, path, version);

        final List<ThemeFileInfo> result = new ArrayList<>();
        if (themeFile.isPresent()) {
            if (themeFile.get() instanceof DataFile) {

                result.add(themeFile.map(this::createThemeFileInfo).get());

            } else if (themeFile.get() instanceof Directory) {

                final Directory directory = (Directory) themeFile.get();
                result.addAll(directory
                    .getFiles()
                    .stream()
                    .map(this::createThemeFileInfo)
                    .collect(Collectors.toList()));
            } else {
                throw new IllegalArgumentException(String
                    .format("Unknown type \"%s\".",
                            themeFile.get().getClass().getName()));
            }
        }

        return result;
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
        return true;
    }

    @Override
    public boolean supportsDraftThemes() {
        return true;
    }

    @Override
    public void publishTheme(final String themeName) {

        themeRepository
            .findThemeByName(themeName, ThemeVersion.DRAFT)
            .ifPresent(themeManager::publishTheme);
    }

    private ThemeInfo createThemeInfo(final Theme theme) {

        Objects.requireNonNull(theme);

        final Optional<ThemeFile> manifestFileJson = fileRepository
            .findByNameAndParent(ThemeConstants.THEME_MANIFEST_JSON,
                                 theme.getRootDirectory());
        final Optional<ThemeFile> manifestFileXml = fileRepository
            .findByNameAndParent(ThemeConstants.THEME_MANIFEST_XML,
                                 theme.getRootDirectory());

        final DataFile manifestFile;
        final String filename;
        if (manifestFileJson.isPresent()) {
            manifestFile = (DataFile) manifestFileJson.get();
            filename = ThemeConstants.THEME_MANIFEST_JSON;
        } else if (manifestFileXml.isPresent()) {
            manifestFile = (DataFile) manifestFileXml.get();
            filename = ThemeConstants.THEME_MANIFEST_XML;
        } else {
            throw new IllegalArgumentException(String
                .format("No manifest file found for theme \"%s\".",
                        theme.getName()));
        }

        try (final InputStream inputStream = new ByteArrayInputStream(
            manifestFile.getData())) {
            final ThemeManifest manifest = manifestUtil
                .loadManifest(inputStream, filename);
            final ThemeInfo themeInfo = new ThemeInfo();
            themeInfo.setManifest(manifest);
            themeInfo.setProvider(getClass());
            themeInfo.setVersion(theme.getVersion());

            return themeInfo;
        } catch (IOException ex) {
            throw new UnexpectedErrorException(ex);
        }
    }

    private ThemeFileInfo createThemeFileInfo(final ThemeFile file) {

        final ThemeFileInfo fileInfo = new ThemeFileInfo();

        fileInfo.setName(file.getName());
        fileInfo.setWritable(true);

        if (file instanceof DataFile) {

            final DataFile dataFile = (DataFile) file;

            fileInfo.setDirectory(false);
            fileInfo.setMimeType(dataFile.getType());
            fileInfo.setSize(dataFile.getSize());

        }

        if (file instanceof Directory) {
            fileInfo.setDirectory(true);
        }

        return fileInfo;
    }

}
