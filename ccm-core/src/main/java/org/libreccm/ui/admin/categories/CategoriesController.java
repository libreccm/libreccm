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
@Path("/categorymanager/categories")
public class CategoriesController {

    @GET
    @Path("/{categoryIdentifier}")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String getCategory(
        @PathParam("categoryIdentifier") final String categoryIdentifier
    ) {
        throw new UnsupportedOperationException();
    }

    @GET
    @Path("/{categoryIdentifier}/edit")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String editCategory(
        @PathParam("categoryIdentifier") final String categoryIdentifier
    ) {
        throw new UnsupportedOperationException();
    }

    @GET
    @Path("/{categoryIdentifier}/subcategories/new")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String newSubCategory(
        @PathParam("categoryIdentifier") final String categoryIdentifier
    ) {
        throw new UnsupportedOperationException();
    }

    @POST
    @Path("/{categoryIdentifier}/subcategories/move")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String moveSubCategory(
        @PathParam("categoryIdentifier") final String categoryIdentifier
    ) {
        throw new UnsupportedOperationException();
    }

    @POST
    @Path("/{categoryIdentifier}/subcategories/remove")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String removeSubCategory(
        @PathParam("categoryIdentifier") final String categoryIdentifier
    ) {
        throw new UnsupportedOperationException();
    }

    @POST
    @Path("/{categoryIdentifier}/title/add")
    @AuthorizationRequired
    public String addTitle(
        @PathParam("categorySystemIdentifier")
        final String categorySystemIdentifier
    ) {
        throw new UnsupportedOperationException();
    }

    @POST
    @Path("/{categoryIdentifier}/title/${locale}/edit")
    @AuthorizationRequired
    public String editTitle(
        @PathParam("categorySystemIdentifier")
        final String categorySystemIdentifier,
        @PathParam("locale") final String localeParam
    ) {
        throw new UnsupportedOperationException();
    }

    @POST
    @Path("/{categoryIdentifier}/title/${locale}/remove")
    @AuthorizationRequired
    public String removeTitle(
        @PathParam("categorySystemIdentifier")
        final String categorySystemIdentifier,
        @PathParam("locale") final String localeParam
    ) {
        throw new UnsupportedOperationException();
    }

    @POST
    @Path("/{categoryIdentifier}description/add")
    @AuthorizationRequired
    public String addDescription(
        @PathParam("categorySystemIdentifier")
        final String categorySystemIdentifier
    ) {
        throw new UnsupportedOperationException();
    }

    @POST
    @Path("/{categoryIdentifier}/description/${locale}/edit")
    @AuthorizationRequired
    public String editDescription(
        @PathParam("categorySystemIdentifier")
        final String categorySystemIdentifier,
        @PathParam("locale") final String localeParam
    ) {
        throw new UnsupportedOperationException();
    }

    @POST
    @Path("/{categoryIdentifier}/description/${locale}/remove")
    @AuthorizationRequired
    public String removeDescription(
        @PathParam("categorySystemIdentifier")
        final String categorySystemIdentifier,
        @PathParam("locale") final String localeParam
    ) {
        throw new UnsupportedOperationException();
    }

}
