/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.ui.admin.imexport;

import org.libreccm.core.CoreConstants;
import org.libreccm.imexport.AbstractEntityImExporter;
import org.libreccm.imexport.EntityImExporterTreeNode;
import org.libreccm.imexport.Exportable;
import org.libreccm.imexport.ImportExport;
import org.libreccm.imexport.ImportManifest;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Controller for the Import/Export UI.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/imexport")
public class ImExportController {

    @Inject
    private ImportExport importExport;

    @Inject
    private ImportExportTaskManager taskManager;

    @Inject
    private Models models;

    /**
     * Provides the main page with an overview of all running import/export
     * processes.
     *
     * @return
     */
    @GET
    @Path("/")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String getImExportDashboard() {
        return "org/libreccm/ui/admin/imexport/imexport.xhtml";
    }

    /**
     * UI for starting exports.
     *
     * @return The template to use.
     */
    @GET
    @Path("/export")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String exportEntities() {
        models.put(
            "exportableEntities",
            importExport
                .getExportableEntityTypes()
                .stream()
                .map(EntityImExporterTreeNode::getEntityImExporter)
                .map(AbstractEntityImExporter::getEntityClass)
                .map(Class::getName)
                .sorted()
                .collect(
                    Collectors.toMap(
                        clazz -> clazz,
                        clazz -> clazz,
                        this::noDuplicateKeys,
                        TreeMap::new
                    )
                )
        //.collect(Collectors.toList())
        );

        return "org/libreccm/ui/admin/imexport/export.xhtml";
    }

    /**
     * Starts an export.
     *
     * @param selectedEntitiesParam The entity types selected for export.
     * @param exportName            The name of the export archive.
     *
     * @return Redirect to the main import/export page.
     */
    @POST
    @Path("/export")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String exportEntities(
        @FormParam("selectedEntities") final String[] selectedEntitiesParam,
        @FormParam("exportName") final String exportName
    ) {
        final Set<String> selectedEntities = Arrays
            .stream(selectedEntitiesParam)
            .collect(Collectors.toSet());

        final Set<EntityImExporterTreeNode> selectedNodes = importExport
            .getExportableEntityTypes()
            .stream()
            .filter(
                node -> selectedEntities.contains(
                    node.getEntityImExporter().getEntityClass().getName()
                )
            )
            .collect(Collectors.toSet());

        final Set<EntityImExporterTreeNode> exportNodes = addRequiredEntities(
            new HashSet<>(selectedNodes)
        );

        final Set<Class<? extends Exportable>> exportTypes = exportNodes
            .stream()
            .map(node -> node.getEntityImExporter().getEntityClass())
            .collect(Collectors.toSet());

        taskManager.exportEntities(exportTypes, exportName);

        return "redirect:imexport";
    }

    /**
     * Displays the import page that allows to select a import archive.
     *
     * @return The template to use.
     */
    @GET
    @Path("/import")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String importEntities() {
        models.put(
            "importArchives",
            importExport
                .listAvailableImportArchivies()
                .stream()
                .map(this::buildImportOption)
                .sorted()
                .collect(
                    Collectors.toMap(
                        ImportOption::getImportName,
                        ImportOption::getLabel
                    )
                )
        );
        return "org/libreccm/ui/admin/imexport/import.xhtml";
    }

    /**
     * Execute an import.
     *
     * @param importArchive The name of the import archive to use.
     *
     * @return Redirect to to the main import/export page.
     */
    @POST
    @Path("/import")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String importEntities(
        @FormParam("archive") final String importArchive
    ) {
        taskManager.importEntities(importArchive);

        return "redirect:imexport";
    }

    /**
     * Merge function for {@link Collectors#toMap(java.util.function.Function, java.util.function.Function, java.util.function.BinaryOperator, java.util.function.Supplier).
     *
     * @param str1 First key
     * @param str2 Second key
     *
     * @return First key.
     *
     * @throws RuntimeException if both keys are equal.
     */
    private String noDuplicateKeys(final String str1, final String str2) {
        if (str1.equals(str2)) {
            throw new RuntimeException("No duplicate keys allowed.");
        } else {
            return str1;
        }
    }

    /**
     * Helper method for adding required entities to an export task. Some entity
     * types require also other entity types. This method traverses through the
     * selected entity types of an export and adds required entity types if
     * necessary.
     *
     * @param selectedNodes The selected entity types.
     *
     * @return The final list of exported types.
     */
    private Set<EntityImExporterTreeNode> addRequiredEntities(
        final Set<EntityImExporterTreeNode> selectedNodes
    ) {
        boolean foundRequiredNodes = false;
        final Set<EntityImExporterTreeNode> exportNodes = new HashSet<>(
            selectedNodes
        );
        for (final EntityImExporterTreeNode node : selectedNodes) {
            if (node.getDependsOn() != null
                    && !node.getDependsOn().isEmpty()
                    && !exportNodes.containsAll(node.getDependsOn())) {
                exportNodes.addAll(node.getDependsOn());
                foundRequiredNodes = true;
            }
        }

        if (foundRequiredNodes) {
            return addRequiredEntities(exportNodes);
        } else {
            return exportNodes;
        }
    }

    /**
     * Helper function to build an
     * {@link org.libreccm.ui.admin.imexport.ImportOption} instance from a
     * {@link org.libreccm.imexport.ImportManifest}.
     *
     * @param manifest The manifest to map to a
     *                 {@link org.libreccm.ui.admin.imexport.ImportOption}.
     *
     * @return An {@link org.libreccm.ui.admin.imexport.ImportOption} instance.
     */
    private ImportOption buildImportOption(final ImportManifest manifest) {
        return new ImportOption(
            manifest.getImportName(),
            String.format(
                "%s from server %s created on %s with types %s",
                manifest.getImportName(),
                manifest.getOnServer(),
                DateTimeFormatter.ISO_DATE_TIME.withZone(
                    ZoneOffset.systemDefault()
                ).format(manifest.getCreated().toInstant()),
                manifest.getTypes().stream().collect(Collectors.joining(", "))
            )
        );
    }

}
