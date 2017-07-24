/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.librecms.contentsection.rs;

import org.librecms.assets.Image;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/{content-section}/images/")
public class Images {

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private AssetRepository assetRepo;

    @GET
    @Path("/{path:.+}")
    public Response getImage(
        @PathParam("content-section") final String sectionName,
        @PathParam("path") final String path) {

        final Optional<ContentSection> section = sectionRepo
            .findByLabel(sectionName);
        if (!section.isPresent()) {
            return Response
                .status(Response.Status.NOT_FOUND)
                .entity(String.format("No content section \"%s\" available.",
                                      sectionName))
                .build();
        }

        final Optional<Asset> asset = assetRepo.findByPath(section.get(), 
                                                           path);

        if (asset.isPresent()) {
            if (asset.get() instanceof Image) {
                final Image image = (Image) asset.get();
                final byte[] data = image.getData();

                return Response
                    .ok(String.format(
                        "Requested image \"%s\" in content section \"%s\"",
                        path,
                        section.get().getLabel()),
                        "text/plain")
                    .build();
            } else {
                return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(String
                        .format("The asset found at the requested path \"%s\" "
                                    + "is not an image.",
                                path))
                    .build();
            }
        } else {
            return Response
                .status(Response.Status.NOT_FOUND)
                .entity(String
                    .format("The requested image \"%s\" does not exist.",
                            path))
                .build();
        }

//        final Response.ResponseBuilder builder = Response
//            .ok(String.format(
//                "Requested image \"%s\" from folder \"%s\" in content section \"%s\"",
//                imageName,
//                folderPath,
//                section.get().getLabel()),
//                "text/plain");
//
//        return builder.build();
    }

}
