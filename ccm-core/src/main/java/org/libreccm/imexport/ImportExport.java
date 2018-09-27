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
package org.libreccm.imexport;

import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.files.CcmFiles;
import org.libreccm.files.CcmFilesConfiguration;
import org.libreccm.files.FileAccessException;
import org.libreccm.files.FileDoesNotExistException;
import org.libreccm.files.InsufficientPermissionsException;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 * Central service for importing and exporting entities.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ImportExport {

    @Inject
    private CcmFiles ccmFiles;

    @Inject
    @Any
    private Instance<EntityImExporter<?>> imExporters;

    /**
     * Exports the provided entities. The export will be written to a to the
     * {@code exports} directory in the CCM files directory. If {@code split} is
     * {@code false} a file with the name provided by {@link exportName} will be
     * generated. Otherwise a directory with the provided name will be
     * generated. All files will be placed into that directory. For the main
     * file the provided name will be used.
     *
     *
     * @param entities   The entities to export.
     * @param exportName The name file to which the export is written.
     *
     * @see CcmFilesConfiguration#dataPath
     */
    public void exportEntities(final List<Exportable> entities,
                               final String exportName) {

        throw new UnsupportedOperationException();
    }

    /**
     * Imports all entities from the files in the {@link imports} directory
     * inside the CCM files data directory. The data to import can either be a
     * file with the provided name or a directory with the provided name. If it
     * is a directory the entry file must also use the provided name.
     *
     * If an entity which is part of the import already exists in the database
     * the values from the import are used to update the entity.
     *
     * @param importName The name of the import.
     *
     * @see CcmFilesConfiguration#dataPath
     */
    public void importEntities(final String importName) {

        final String importsPath = String.format("imports/%s", importName);

        try {
            if (!ccmFiles.isDirectory(importsPath)) {

                throw new IllegalArgumentException(String.format(
                    "No imports with name \"%s\" available.",
                    importName));
            }
        } catch (FileAccessException
                     | FileDoesNotExistException
                     | InsufficientPermissionsException ex) {

            throw new UnexpectedErrorException(ex);
        }

        final String manifestPath = String.format("%s/ccm-export.json",
                                                  importsPath);
        try (final InputStream manifestInputStream = ccmFiles
            .createInputStream(importsPath)) {

            final JsonReader manifestReader = Json
                .createReader(manifestInputStream);
            final JsonObject manifest = manifestReader.readObject();

        } catch (IOException
                     | FileDoesNotExistException
                     | FileAccessException
                     | InsufficientPermissionsException ex) {

        }

        throw new UnsupportedOperationException();

    }

    public List<ImportManifest> listAvailableImportArchivies() {

        final List<String> importArchivePaths;
        try {
            importArchivePaths = ccmFiles.listFiles("imports");
        } catch (FileAccessException
                     | FileDoesNotExistException
                     | InsufficientPermissionsException ex) {

            throw new UnexpectedErrorException(ex);
        }

        return importArchivePaths.
            stream()
            .filter(this::isImportArchive)
            .map(this::createImportManifest)
            .collect(Collectors.toList());
    }

    private boolean isImportArchive(final String path) {

        final String manifestPath = String.format("imports/%s/ccm-export.json",
                                                  path);

        final boolean result;
        try {
            result = ccmFiles.existsFile(manifestPath);
        } catch (FileAccessException | InsufficientPermissionsException ex) {

            throw new UnexpectedErrorException(ex);
        }

        return result;
    }

    private ImportManifest createImportManifest(final String path) {

        final String manifestPath = String.format("imports/%s/ccm-export.json",
                                                  path);

        final InputStream inputStream;
        try {
            inputStream = ccmFiles.createInputStream(manifestPath);
        } catch (FileAccessException
                     | FileDoesNotExistException
                     | InsufficientPermissionsException ex) {

            throw new UnexpectedErrorException(ex);
        }

        final JsonReader reader = Json.createReader(inputStream);
        final JsonObject manifestJson = reader.readObject();

        if (!manifestJson.containsKey("created")) {
            throw new IllegalArgumentException(String.format(
                "The manifest file \"%s\" is malformed. "
                    + "Key \"created\" is missing.",
                manifestPath));
        }

        if (!manifestJson.containsKey("onServer")) {
            throw new IllegalArgumentException(String.format(
                "The manifest file \"%s\" is malformed. "
                    + "Key \"onServer\" is missing.",
                manifestPath));
        }

        if (!manifestJson.containsKey("types")) {
            throw new IllegalArgumentException(String.format(
                "The manifest file \"%s\" is malformed. "
                    + "Key \"types\" is missing.",
                manifestPath));
        }

        final LocalDateTime created = LocalDateTime
            .parse(manifestJson.getString("created"));
        final String onServer = manifestJson.getString("onServer");
        final List<String> types = manifestJson.getJsonArray("types")
            .stream()
            .map(value -> value.toString())
            .collect(Collectors.toList());

        return new ImportManifest(
            Date.from(created.atZone(ZoneId.of("UTC")).toInstant()),
            onServer,
            types);
    }

}
