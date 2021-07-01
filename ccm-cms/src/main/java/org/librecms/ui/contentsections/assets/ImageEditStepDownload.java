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
package org.librecms.ui.contentsections.assets;

import org.libreccm.security.AuthorizationRequired;
import org.librecms.assets.BinaryAssetDataService;
import org.librecms.assets.Image;
import org.librecms.assets.ImageService;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.ui.contentsections.ContentSectionsUi;

import java.io.IOException;
import java.io.OutputStream;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path(MvcAssetEditSteps.PATH_PREFIX + "image-edit-download")
public class ImageEditStepDownload {

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private BinaryAssetDataService dataService;

    @Inject
    private ContentSectionsUi sectionsUi;

    @Inject
    private ImageService imageService;

    @GET
    @Path("/")
    @AuthorizationRequired
    public Response downloadFile(
        @PathParam(MvcAssetEditSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAssetEditSteps.ASSET_PATH_PATH_PARAM_NAME)
        final String assetPath,
        @QueryParam("width") @DefaultValue("-1")
        final int width,
        @QueryParam("height") @DefaultValue("-1")
        final int height
    ) {
        final ContentSection contentSection = sectionsUi
            .findContentSection(sectionIdentifier)
            .orElseThrow(
                () -> new WebApplicationException(
                    Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(
                            String.format(
                                "ContentSection %s not found.",
                                sectionIdentifier
                            )
                        ).build()
                )
            );

        final Asset asset = assetRepo
            .findByPath(contentSection, assetPath)
            .orElseThrow(
                () -> new WebApplicationException(
                    Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(
                            String.format(
                                "No asset for path %s found in section %s.",
                                assetPath,
                                contentSection.getLabel()
                            )
                        )
                        .build()
                )
            );

        if (!(asset instanceof Image)) {
            throw new WebApplicationException(
                Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(
                        String.format(
                            "No image for path %s found in section %s.",
                            assetPath,
                            contentSection.getLabel()
                        )
                    )
                    .build()
            );
        }

        final Image image = (Image) asset;
        final StreamingOutput streamingOutput;
        if (width == -1 && height == -1) {
            streamingOutput = new StreamingOutput() {

                @Override
                public void write(final OutputStream outputStream)
                    throws IOException, WebApplicationException {
                    dataService.copyDataToOutputStream(image, outputStream);
                }

            };
        } else {
            final byte[] scaled = imageService.scaleImage(image, width, height);
            streamingOutput = new StreamingOutput() {

                @Override
                public void write(final OutputStream outputStream)
                    throws IOException, WebApplicationException {
                    outputStream.write(scaled);
                }

            };
        }

        return Response
            .ok()
            .entity(streamingOutput)
            .header("Content-Type", image.getMimeType())
            .header(
                "Content-Disposition",
                String.format(
                    "attachment; filename=\"%s\"",
                    image.getFileName()
                )
            )
            .build();

    }

}
