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

import com.arsdigita.ui.admin.importexport.ImportExportMonitor;

import org.libreccm.core.CoreConstants;
import org.libreccm.imexport.AbstractEntityImExporter;
import org.libreccm.imexport.EntityImExporterTreeNode;
import org.libreccm.imexport.ImportExport;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;

import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;

/**
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
    private ImportExportMonitor importExportMonitor;

    @Inject
    private Models models;

    @GET
    @Path("/")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String getImExportDashboard() {
        return "org/libreccm/ui/admin/imexport/imexport.xhtml";
    }

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
                .collect(Collectors.toList())
        );

        return "org/libreccm/ui/admin/imexport/export.xhtml";
    }
    
    @GET
    @Path("/import")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String importEntities() {
        throw new NotFoundException();
    }

}
