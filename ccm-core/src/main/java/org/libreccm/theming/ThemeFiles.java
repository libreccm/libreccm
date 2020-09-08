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
package org.libreccm.theming;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * JAX-RS endpoint for serving files from a theme.
 * 
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/files/{theme}")
public class ThemeFiles {

    @Inject
    private Themes themes;

    @GET
    @Path("/{path}")
    public Response getFile(@PathParam("theme") final String theme,
                            @PathParam("path") final String path) {

        final ThemeInfo info = themes
            .getTheme(theme, ThemeVersion.LIVE)
            .orElseThrow(() -> new NotFoundException(String
            .format("Theme \"%s\" does not exist.", theme)));

        final InputStream inputStream = themes
            .getFileFromTheme(info, path)
            .orElseThrow(() -> new NotFoundException(String
            .format("The file \"%s\" does exist in the theme \"%s\".",
                    path,
                    theme)));

        final MediaType mediaType = getMediaTypeFromPath(path);

        final BufferedReader reader = new BufferedReader(
            new InputStreamReader(inputStream));
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            int value = reader.read();
            while (value != -1) {
                outputStream.write(value);
                value = reader.read();
            }
        } catch (IOException ex) {
            throw new WebApplicationException(ex);
        }
        
        final byte[] data = outputStream.toByteArray();
        return Response.ok(data, mediaType).build();
    }

    private MediaType getMediaTypeFromPath(final String path) {

        if (path.endsWith(".css")) {
            return new MediaType("text", "css");
        } else {
            return MediaType.WILDCARD_TYPE;
        }

    }

}
