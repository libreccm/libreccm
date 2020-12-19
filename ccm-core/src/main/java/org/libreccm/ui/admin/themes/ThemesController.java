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
package org.libreccm.ui.admin.themes;

import org.libreccm.core.CoreConstants;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.theming.Themes;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Primary controller for the UI for managing themes.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/themes")
public class ThemesController {

    @Inject
    private Themes themes;

    @Inject
    private Models models;

    /**
     * Show all available themes.
     *
     * @return The template to use.
     */
    @GET
    @Path("/")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String getThemes() {
        return "org/libreccm/ui/admin/themes/themes.xhtml";
    }

    /**
     * Create a new theme.
     *
     * @param themeName    The name of the new theme.
     * @param providerName The provider of the new theme.
     *
     * @return Redirect to the list of available themes.
     */
    @POST
    @Path("/new")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String createTheme(
        @FormParam("themeName") final String themeName,
        @FormParam("providerName") final String providerName
    ) {
        themes.createTheme(themeName, providerName);

        return "redirect:themes/";
    }

    /**
     * (Re-)Publish a theme.
     *
     * @param themeName The theme to (re-)publish.
     *
     * @return Redirect to the list of themes.
     */
    @POST
    @Path("/{themeName}/publish")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String publishTheme(final String themeName) {
        themes.publishTheme(themeName);

        return "redirect:themes/";
    }

    /**
     * Unpublish a theme.
     *
     * @param themeName The theme to unpublish.
     *
     * @return Redirect to the list of themes.
     */
    @POST
    @Path("/{themeName}/unpublish")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String unpublishTheme(final String themeName) {
        themes.unpublishTheme(themeName);

        return "redirect:themes/";
    }

    /**
     * Delete a theme.
     *
     * @param themeName The theme to delete.
     *
     * @return Redirect to the list of themes.
     */
    @POST
    @Path("/{themeName}/delete")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String deleteTheme(@PathParam("themeName") final String themeName) {
        themes.deleteTheme(themeName);

        return "redirect:themes/";
    }

}
