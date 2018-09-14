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
package org.libreccm.theming.manager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.theming.ThemeInfo;
import org.libreccm.theming.ThemeProvider;
import org.libreccm.theming.ThemeVersion;
import org.libreccm.theming.ThemingPrivileges;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonWriter;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/")
public class Themes implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(Themes.class);

    @Inject
    @Any
    private Instance<ThemeProvider> providers;

    @Inject
    private Themes themes;

    @GET
    @Path("/providers")
    @Produces(MediaType.APPLICATION_JSON)
    //@AuthorizationRequired
    @RequiresPrivilege(ThemingPrivileges.ADMINISTER_THEMES)
    public String getThemeProviders() {

        final List<ThemeProvider> providersList = new ArrayList<>();
        providers
            .forEach(provider -> providersList.add(provider));

        final JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        providersList
            .stream()
            .filter(provider -> provider.supportsChanges()
                                    && provider.supportsDraftThemes())
            .map(this::getProviderName)
            .forEach(jsonArrayBuilder::add);

        final StringWriter writer = new StringWriter();
        final JsonWriter jsonWriter = Json.createWriter(writer);

        jsonWriter.writeArray(jsonArrayBuilder.build());

        return writer.toString();
    }

    @GET
    @Path("/themes")
    @Produces(MediaType.APPLICATION_JSON)
    //@AuthorizationRequired
    @RequiresPrivilege(ThemingPrivileges.ADMINISTER_THEMES)
    public List<ThemeInfo> getAvailableThemes() {

        final List<ThemeInfo> availableThemes = new ArrayList<>();
        for (final ThemeProvider provider : providers) {
            if (provider.supportsChanges() && provider.supportsDraftThemes()) {
                availableThemes.addAll(provider.getThemes());
            }
        }

        return availableThemes;
    }

    @GET
    @Path("/themes/{theme}")
    @Produces(MediaType.APPLICATION_JSON)
    //@AuthorizationRequired
    @RequiresPrivilege(ThemingPrivileges.EDIT_THEME)
    public ThemeInfo getTheme(@PathParam("theme") final String themeName) {

        for (final ThemeProvider provider : providers) {
            if (provider.providesTheme(themeName, ThemeVersion.DRAFT)) {
                return provider
                    .getThemeInfo(themeName, ThemeVersion.DRAFT)
                    .orElseThrow(() -> new WebApplicationException(
                    Response.Status.NOT_FOUND));
            }
        }

        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @PUT
    @Path("/themes/{theme}")
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    //@AuthorizationRequired
    @RequiresPrivilege(ThemingPrivileges.ADMINISTER_THEMES)
    public ThemeInfo createTheme(
        @PathParam("theme") final String themeName,
        @QueryParam("provider") final String providerName) {

        Objects.requireNonNull(themeName);
        Objects.requireNonNull(providerName);

        if (themeName.isEmpty() || themeName.matches("\\s*")) {
            throw new WebApplicationException("No name for new theme provided.",
                                              Response.Status.BAD_REQUEST);
        }

        if (providerName.isEmpty() || providerName.matches("\\s*")) {
            throw new WebApplicationException(
                "No provider for new theme provided.",
                Response.Status.BAD_REQUEST);
        }

        final Class<ThemeProvider> providerClass;
        try {
            providerClass = (Class<ThemeProvider>) Class.forName(providerName);
        } catch (ClassNotFoundException ex) {
            throw new WebApplicationException(
                String.format("No provider with name \"%s\" available.",
                              providerName),
                Response.Status.INTERNAL_SERVER_ERROR);
        }
        final ThemeProvider provider = providers.select(providerClass).get();

        return provider.createTheme(themeName);
    }

    @DELETE
    @Path("/themes/{theme}")
    //@AuthorizationRequired
    @RequiresPrivilege(ThemingPrivileges.ADMINISTER_THEMES)
    public void deleteTheme(@PathParam("theme") final String themeName) {

        Objects.requireNonNull(themeName);

        final Optional<ThemeProvider> provider = findProvider(themeName);

        if (provider.isPresent()) {

            provider.get().deleteTheme(themeName);

        } else {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @POST
    @Path("/themes/{theme}/live")
    //@AuthorizationRequired
    @RequiresPrivilege(ThemingPrivileges.ADMINISTER_THEMES)
    public void publishTheme(@PathParam("theme") final String themeName) {

        final Optional<ThemeProvider> provider = findProvider(themeName);

        if (provider.isPresent()) {

            provider.get().publishTheme(themeName);

        } else {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

    }

    @DELETE
    @Path("/themes/{theme}/live")
    //@AuthorizationRequired
    @RequiresPrivilege(ThemingPrivileges.ADMINISTER_THEMES)
    public void unPublishTheme(@PathParam("theme") final String themeName) {

        final Optional<ThemeProvider> provider = findProvider(themeName);

        if (provider.isPresent()) {

            provider.get().unpublishTheme(themeName);

        } else {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

    }

    private String getProviderName(final ThemeProvider provider) {

        if (provider
            .getClass()
            .getCanonicalName()
            .toLowerCase()
            .contains("$proxy")) {

            final String name = provider.getClass().getCanonicalName();
            return name.substring(0, name.toLowerCase().indexOf("$proxy"));

        } else {
            return provider.getClass().getName();
        }

    }

    private Optional<ThemeProvider> findProvider(final String forTheme) {

        final List<ThemeProvider> providersList = new ArrayList<>();
        providers
            .forEach(provider -> providersList.add(provider));

        return providersList
            .stream()
            .filter(current -> current.providesTheme(forTheme,
                                                     ThemeVersion.DRAFT))
            .findAny();
    }

}
