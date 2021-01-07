/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.libreccm.theming.mvc;

import org.libreccm.theming.ThemeFileInfo;
import org.libreccm.theming.ThemeProvider;
import org.libreccm.theming.ThemeVersion;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * Provides access to the resources/assets of themes.
 *
 * @see ThemeResources
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/")
public class ThemeResourceProvider {

    /**
     * Injection point for the available {@link ThemeProvider}s.
     */
    @Inject
    @Any
    private Instance<ThemeProvider> providers;

    /**
     * Serves a resources from a theme. This endpoint is mounted at
     * {@code /@themes/{theme}/{themeVersion}/{path:.+}}. If the provided theme
     * is not found, the provided theme is not available in the requested
     * version, or if the theme does not provide the requested resource the
     * endpoint will response with a 404 response. If the response is found, a
     * response with the asset and the correct mime type is returned.
     *
     * @param themeName         The name of the theme providing the resource.
     * @param themeVersionParam The version of the theme to use (either
     *                          {@code LIVE} or {@code DRAFT}.
     * @param pathParam         The path of the resource to serve.
     *
     * @return A response with the resource and the correct mime type, or a 404
     *         response if the resource, the theme version or the theme is not
     *         available.
     */
    @GET
    @Path("/{theme}/{themeVersion}/{path:.+}")
    public Response getThemeFile(
        @PathParam("theme") final String themeName,
        @PathParam("themeVersion") final String themeVersionParam,
        @PathParam("path") final String pathParam
    ) {
        final Optional<ThemeProvider> provider = findProvider(themeName);
        final ThemeVersion themeVersion = ThemeVersion.valueOf(
            themeVersionParam
        );

        if (provider.isPresent()) {
            final Optional<ThemeFileInfo> fileInfo = provider
                .get()
                .getThemeFileInfo(themeName, themeVersion, pathParam);

            if (fileInfo.isPresent()) {
                final ThemeFileInfo themeFileInfo = fileInfo.get();
                if (themeFileInfo.isDirectory()) {
                    return Response.status(Response.Status.FORBIDDEN).build();
                } else {
                    final Optional<InputStream> inputStream = provider
                        .get()
                        .getThemeFileAsStream(
                            themeName, themeVersion, pathParam
                        );
                    if (inputStream.isPresent()) {
                        final InputStream inStream = inputStream.get();
                        return Response
                            .ok(inStream)
                            .type(themeFileInfo.getMimeType())
                            .build();
                    } else {
                        return Response
                            .status(Response.Status.NOT_FOUND)
                            .entity(
                                String.format(
                                    "File \"%s\" does not exist in version of "
                                        + "theme %s.",
                                    pathParam,
                                    themeVersion,
                                    themeName
                                )
                            )
                            .build();
                    }
                }
            } else {
                return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(
                        String.format(
                            "File \"%s\" does not exist in the %s "
                                + "version of theme %s.",
                            pathParam,
                            themeVersion,
                            themeName
                        )
                    )
                    .build();
            }
        } else {
            return Response
                .status(Response.Status.NOT_FOUND)
                .entity(String.format("Theme \"%s\" does not exist.",
                                      themeName))
                .build();
        }
    }

    /**
     * Helper method for finding the provider of a theme.
     *
     * @param forTheme The theme.
     *
     * @return An {@link Optional} with the provider of the theme. If there is
     *         no matching provider, an empty {@link Optional} is returned.
     */
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
