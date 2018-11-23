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
import org.libreccm.files.FileAlreadyExistsException;
import org.libreccm.files.FileDoesNotExistException;
import org.libreccm.files.InsufficientPermissionsException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import javax.enterprise.util.AnnotationLiteral;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonWriter;

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
    private Instance<AbstractEntityImExporter<?>> imExporters;

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

        final JsonObjectBuilder manifestBuilder = Json.createObjectBuilder();
        manifestBuilder.add("created",
                            LocalDateTime.now(ZoneId.of("UTC")).toString());
        try {
            manifestBuilder.add("onServer",
                                InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException ex) {
            throw new UnexpectedErrorException(ex);
        }

        final JsonArrayBuilder typesArrayBuilder = Json.createArrayBuilder();

        final Set<String> types = entities
            .stream()
            .map(entity -> entity.getClass().getName())
            .collect(Collectors.toSet());

        final Map<String, List<Exportable>> typeEntityMap = new HashMap<>();
        try {
            ccmFiles.createDirectory(String.format("exports/%s", exportName));

            for (final String type : types) {
                ccmFiles.createDirectory(String.format("exports/%s/%s",
                                                       exportName,
                                                       type));
                typesArrayBuilder.add(type);

                final List<Exportable> entitiesOfType = entities
                    .stream()
                    .filter(entity -> entity.getClass().getName().equals(type))
                    .collect(Collectors.toList());

                typeEntityMap.put(type, entitiesOfType);
            }

            manifestBuilder.add("types", typesArrayBuilder);
            final OutputStream manifestOutputStream = ccmFiles
                .createOutputStream(String.format("exports/%s/ccm-export.json",
                                                  exportName));
            try (JsonWriter manifestWriter = Json.
                createWriter(manifestOutputStream)) {

                manifestWriter.writeObject(manifestBuilder.build());
            }

        } catch (FileAccessException
                 | FileAlreadyExistsException
                 | InsufficientPermissionsException ex) {
            throw new UnexpectedErrorException(ex);
        }

        for (final Map.Entry<String, List<Exportable>> entry
                 : typeEntityMap.entrySet()) {

            createExportedEntities(exportName,
                                   entry.getKey(),
                                   entry.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    private JsonArrayBuilder createExportedEntities(
        final String exportName,
        final String type,
        final List<Exportable> entities) {

        final JsonArrayBuilder filesArrayBuilder = Json.createArrayBuilder();

        final Class<? extends Exportable> clazz;
        try {
            clazz = (Class<? extends Exportable>) Class.forName(type);
        } catch (ClassNotFoundException ex) {
            throw new UnexpectedErrorException(ex);
        }

        final Instance<AbstractEntityImExporter<?>> instance = imExporters
            .select(new ProcessesLiteral(clazz));

        final AbstractEntityImExporter<?> imExporter;
        if (instance.isUnsatisfied()) {
            throw new UnexpectedErrorException(String.format(
                "No EntityImExporter for entity type \"%s\" available.",
                type));
        } else if (instance.isAmbiguous()) {
            throw new UnexpectedErrorException(String.format(
                "Instance reference for EntityImExporter for entity "
                    + "type \"%s\" is ambiguous.",
                type));
        } else {
            imExporter = instance.get();
        }

        for (Exportable entity : entities) {

            final String filename = String.format("%s.json", entity.getUuid());
            final OutputStream outputStream;
            try {
                outputStream = ccmFiles.createOutputStream(String.format(
                    "exports/%s/%s/%s",
                    exportName,
                    type,
                    filename));
                filesArrayBuilder.add(filename);
            } catch (FileAccessException
                     | InsufficientPermissionsException ex) {
                throw new UnexpectedErrorException(ex);
            }

            final String exportedEntity;
            try {
                exportedEntity = imExporter.exportEntity(entity);
            } catch (ExportException ex) {
                throw new UnexpectedErrorException(ex);
            }
            try (final OutputStreamWriter writer = new OutputStreamWriter(
                outputStream, StandardCharsets.UTF_8)) {

                writer.write(exportedEntity);

            } catch (IOException ex) {
                throw new UnexpectedErrorException(ex);
            }
//            try (JsonWriter writer = Json.createWriter(outputStream)) {
//                writer.writeObject(exportedEntity);
//            }
        }

        return filesArrayBuilder;
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

        final List<AbstractEntityImExporter<?>> imExportersList
                                                    = new ArrayList<>();
        imExporters.forEach(imExporter -> imExportersList.add(imExporter));

        try {
            final EntityImExporterTreeManager treeManager
                                                  = new EntityImExporterTreeManager();
            final List<EntityImExporterTreeNode> tree = treeManager
                .generateTree(imExportersList);
            final List<EntityImExporterTreeNode> orderedNodes = treeManager
                .orderImExporters(tree);

            final ImportManifest manifest = createImportManifest(importName);

            final List<EntityImExporterTreeNode> importers = orderedNodes
                .stream()
                .filter(node -> filterImporters(manifest, node))
                .collect(Collectors.toList());

            importers
                .stream()
                .map(EntityImExporterTreeNode::getEntityImExporter)
                .forEach(imExporter -> importEntitiesOfType(importName,
                                                            imExporter));
        } catch (DependencyException ex) {
            throw new UnexpectedErrorException(ex);
        }
    }

    private boolean filterImporters(final ImportManifest manifest,
                                    final EntityImExporterTreeNode node) {

        final AbstractEntityImExporter<?> imExporter = node
            .getEntityImExporter();
        final String type = imExporter.getEntityClass().getName();

        return manifest.getTypes().contains(type);
    }

    private void importEntitiesOfType(
        final String importName,
        final AbstractEntityImExporter<?> entityImExporter) {

        final String type = entityImExporter.getEntityClass().getName();

        try (final InputStream tocInputStream = ccmFiles
            .createInputStream(String.format("imports/%s/%s/%s.json",
                                             importName,
                                             type,
                                             type))) {

            final JsonReader tocReader = Json.createReader(tocInputStream);
            final JsonObject toc = tocReader.readObject();
            final JsonArray files = toc.getJsonArray("files");

            files.forEach(value -> importEntity(importName,
                                                type,
                                                ((JsonString) value).getString(),
                                                entityImExporter));

        } catch (IOException
                 | FileDoesNotExistException
                 | FileAccessException
                 | InsufficientPermissionsException ex) {

            throw new UnexpectedErrorException(ex);
        }
    }

    private void importEntity(final String importName,
                              final String type,
                              final String fileName,
                              final AbstractEntityImExporter<?> imExporter) {

        final String filePath = String.format("imports/%s/%s/%s",
                                              importName,
                                              type,
                                              fileName);
        try (final InputStream inputStream
                                   = ccmFiles.createInputStream(filePath)) {

            final String data = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

//            final JsonReader reader = Json.createReader(inputStream);
//            final JsonObject data = reader.readObject();
            imExporter.importEntity(data);

        } catch (IOException
                 | FileDoesNotExistException
                 | FileAccessException
                 | InsufficientPermissionsException
                 | ImportExpection ex) {
            throw new UnexpectedErrorException(ex);
        }

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

        try (final InputStream inputStream = ccmFiles
            .createInputStream(manifestPath)) {

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
//            final List<String> types = manifestJson.getJsonArray("types")
//                .stream()
//                .map(value -> value.toString())
//                .collect(Collectors.toList());
            final JsonArray typesArray = manifestJson.getJsonArray("types");
            final List<String> types = new ArrayList<>();
            for(int i = 0; i < typesArray.size(); i++) {
                types.add(typesArray.getString(i));
            }

            return new ImportManifest(
                Date.from(created.atZone(ZoneId.of("UTC")).toInstant()),
                onServer,
                types);
        } catch (IOException
                 | FileAccessException
                 | FileDoesNotExistException
                 | InsufficientPermissionsException ex) {

            throw new UnexpectedErrorException(ex);
        }
    }

    private static class ProcessesLiteral
        extends AnnotationLiteral<Processes>
        implements Processes {

        private static final long serialVersionUID = 1L;

        private final Class<? extends Exportable> value;

        private ProcessesLiteral(final Class<? extends Exportable> value) {
            this.value = value;
        }

        @Override
        public Class<? extends Exportable> value() {

            return value;
        }

    }

}
