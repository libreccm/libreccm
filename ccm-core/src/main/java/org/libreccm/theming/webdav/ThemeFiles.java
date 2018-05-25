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
package org.libreccm.theming.webdav;

import org.libreccm.theming.ThemeFileInfo;
import org.libreccm.webdav.methods.PROPFIND;

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

import org.libreccm.theming.ThemeInfo;
import org.libreccm.theming.ThemeVersion;
import org.libreccm.theming.Themes;
import org.libreccm.webdav.ResponseStatus;
import org.libreccm.webdav.methods.LOCK;
import org.libreccm.webdav.methods.UNLOCK;
import org.libreccm.webdav.xml.elements.ActiveLock;
import org.libreccm.webdav.xml.elements.Collection;
import org.libreccm.webdav.xml.elements.Depth;
import org.libreccm.webdav.xml.elements.HRef;
import org.libreccm.webdav.xml.elements.LockInfo;
import org.libreccm.webdav.xml.elements.LockRoot;
import org.libreccm.webdav.xml.elements.LockToken;
import org.libreccm.webdav.xml.elements.MultiStatus;
import org.libreccm.webdav.xml.elements.Prop;
import org.libreccm.webdav.xml.elements.PropStat;
import org.libreccm.webdav.xml.elements.Status;
import org.libreccm.webdav.xml.elements.TimeOut;
import org.libreccm.webdav.xml.elements.WebDavResponse;
import org.libreccm.webdav.xml.properties.DisplayName;
import org.libreccm.webdav.xml.properties.GetContentLength;
import org.libreccm.webdav.xml.properties.GetContentType;
import org.libreccm.webdav.xml.properties.LockDiscovery;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/{theme}")
public class ThemeFiles {

    @Inject
    private HttpServletRequest request;

    @Inject
    private ServletContext servletContext;

    @Inject
    private Themes themes;

    @Inject
    private ThemeFilesLockManager lockManager;

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

    @LOCK
    @Path("/{path}")
    public Prop lock(@PathParam("theme")
        final String theme,
                     @PathParam("path")
                     final String path,
                     final LockInfo lockInfo) {

        final String lockedPath = String.format("%s/path",
                                                theme,
                                                path);

        try {
            final String lockToken = lockManager.lockFile(lockedPath);

            return new Prop(new LockDiscovery(
                new ActiveLock(lockInfo.getLockScope(),
                               lockInfo.getLockType(),
                               Depth.ZERO,
                               lockInfo.getOwner(),
                               new TimeOut(3600),
                               new LockToken(new HRef(String
                                   .format("opaquelocktoken:%s",
                                           lockToken))),
                               new LockRoot(new HRef(lockedPath)))));

        } catch (AlreadyLockedException ex) {
            throw new WebApplicationException(
                ResponseStatus.LOCKED.getStatusCode());
        }
    }

    @OPTIONS
    public Response options() {

        return Response
            .noContent()
            .header("DAV", "1, 2")
            .header("Allow",
                    "GET,DELETE,MOVE,COPY,PROPFIND,OPTIONS,HEAD,PUT,PROPPATCH,"
                        + "LOCK,UNLOCK")
            .build();
    }

    @PROPFIND
    @Path("/{path}")
    public Response propfind(@PathParam("theme") final String theme,
                             @PathParam("path") final String path,
                             @Context final UriInfo uriInfo,
                             @Context final Providers providers) {

        final ThemeInfo themeInfo = themes
            .getTheme(theme, ThemeVersion.DRAFT)
            .orElseThrow(() -> new NotFoundException(String.format(
            "No theme with name \"%s\" exists.",
            theme)));

        final List<ThemeFileInfo> fileInfos = themes.listThemesFiles(themeInfo,
                                                                     path);
        final MultiStatus result;
        if (fileInfos.isEmpty()) {
            throw new NotFoundException(String.format(
                "No file \"%s\" in theme \"%s\".",
                path,
                theme));
        } else if (fileInfos.size() == 1) {

            final ThemeFileInfo fileInfo = fileInfos.get(0);

            result = new MultiStatus(buildWebDavResponse(fileInfo, uriInfo));

        } else {
            final WebDavResponse folder = new WebDavResponse(
                new HRef(uriInfo.getRequestUri()),
                null,
                null,
                null,
                new PropStat(new Prop(new DisplayName(path),
                                      Collection.COLLECTION),
                             new Status(javax.ws.rs.core.Response.Status.OK)));

            final List<WebDavResponse> responses = new LinkedList<>();
            responses.add(folder);

            final List<WebDavResponse> fileResponses = fileInfos
                .stream()
                .map(fileInfo -> buildWebDavResponse(path, fileInfo))
                .collect(Collectors.toList());
            responses.addAll(fileResponses);

            result = new MultiStatus(responses.toArray(new WebDavResponse[]{}));
        }

        return Response
            .status(ResponseStatus.MULTI_STATUS)
            .entity(result)
            .build();
    }

    @UNLOCK
    @Path("/{path}")
    public Response unlock(
        @PathParam("theme")
        final String theme,
        @PathParam("path")
        final String path,
        @HeaderParam("Lock-Tocken") final String lockToken) {

        lockManager.unlock(lockToken);
        
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    private WebDavResponse buildWebDavResponse(final ThemeFileInfo fileInfo,
                                               final UriInfo uriInfo) {

        final PropStat propStat;
        if (fileInfo.isDirectory()) {
            propStat = new PropStat(
                new Prop(new DisplayName(fileInfo.getName())),
                new Status(javax.ws.rs.core.Response.Status.OK));
        } else {
            propStat = new PropStat(
                new Prop(new DisplayName(fileInfo.getName()),
                         new GetContentLength(fileInfo.getSize()),
                         new GetContentType(fileInfo.getMimeType())),
                new Status(javax.ws.rs.core.Response.Status.OK));
        }

        final WebDavResponse response = new WebDavResponse(
            new HRef(uriInfo.getRequestUri()),
            null,
            null,
            null,
            propStat);

        return response;
    }

    private WebDavResponse buildWebDavResponse(final String basePath,
                                               final ThemeFileInfo fileInfo) {

        final PropStat propStat;
        if (fileInfo.isDirectory()) {
            propStat = new PropStat(
                new Prop(new DisplayName(fileInfo.getName())),
                new Status(javax.ws.rs.core.Response.Status.OK));
        } else {
            propStat = new PropStat(
                new Prop(new DisplayName(fileInfo.getName()),
                         new GetContentLength(fileInfo.getSize()),
                         new GetContentType(fileInfo.getMimeType())),
                new Status(javax.ws.rs.core.Response.Status.OK));
        }

        final WebDavResponse response = new WebDavResponse(
            new HRef(String.format("%s://%s:%d%s/DAV/themes/%s/%s",
                                   request.getScheme(),
                                   request.getServerName(),
                                   request.getServerPort(),
                                   servletContext.getContextPath(),
                                   basePath,
                                   fileInfo.getName())),
            null,
            null,
            null,
            propStat);

        return response;
    }

    private MediaType getMediaTypeFromPath(final String path) {

        if (path.endsWith(".css")) {
            return new MediaType("text", "css");
        } else {
            return MediaType.WILDCARD_TYPE;
        }

    }

}
