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
package org.libreccm.ui.admin.categories;

import org.libreccm.core.CoreConstants;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;

import javax.enterprise.context.RequestScoped;
import javax.mvc.Controller;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/categorymanager")
public class CategorySystemsController {

    @GET
    @Path("/")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String getCategoryManager() {
        return getCategorySystems();
    }

    @GET
    @Path("/categorysystems")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String getCategorySystems() {
        // ToDo
        return "org/libreccm/ui/admin/categories/categorysystems.xhtml";
    }

    @GET
    @Path("/categorysystems/{categorySystemIdentifier}/details")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String getCategorySystem(
        @PathParam("categorySystemIdentifier")
        final String categorySystemIdentifier
    ) {
        // ToDo
        return "org/libreccm/ui/admin/categories/categorysystem-details.xhtml";
    }

    @GET
    @Path("/categorysystems/new")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String newCategorySystem(
    ) {
        // ToDo
        return "org/libreccm/ui/admin/categories/categorysystem-form.xhtml";
    }

    @GET
    @Path("/categorysystems/{categorySystemIdentifier}/edit")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String editCategorySystem(
        @PathParam("categorySystemIdentifier")
        final String categorySystemIdentifier
    ) {
        // ToDo
        return "org/libreccm/ui/admin/categories/categorysystem-form.xhtml";
    }

    @POST
    @Path("/categorysystems/{categorySystemIdentifier}/delete")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String deleteCategorySystem(
        @PathParam("categorySystemIdentifier")
        final String categorySystemIdentifier
    ) {
        // ToDo
        return "redirect:categorymanager/categorysystems";
    }
    
    @POST
    @Path("/categorysystems/{categorySystemIdentifier}/owners/add")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String addOwner(
        @PathParam("categorySystemIdentifier")
        final String categorySystemIdentifier
    ) {
        // ToDo
        return String.format(
            "redirect:categorymanager/categorysystems/%s",
            categorySystemIdentifier
        );
    }
    
    @POST
    @Path("/categorysystems/{categorySystemIdentifier}/owners/remove")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String removeOwner(
        @PathParam("categorySystemIdentifier")
        final String categorySystemIdentifier
    ) {
        // ToDo
        return String.format(
            "redirect:categorymanager/categorysystems/%s",
            categorySystemIdentifier
        );
    }

}
