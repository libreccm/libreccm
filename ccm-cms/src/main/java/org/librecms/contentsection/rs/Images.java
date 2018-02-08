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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.librecms.assets.Image;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/{content-section}/images/")
public class Images {

    private static final Logger LOGGER = LogManager.getLogger(Images.class);

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private AssetRepository assetRepo;

    @GET
    @Path("/uuid-{uuid}")
    public Response getImageByUuid(
        @PathParam("content-section")
        final String sectionName,
        @PathParam("uuid")
        final String uuid,
        @QueryParam("width")
        @DefaultValue("-1")
        final String widthParam,
        @QueryParam("height")
        @DefaultValue("-1")
        final String heightParam) {

        final Optional<Asset> asset = assetRepo
            .findByUuidAndType(uuid, Image.class);

        if (asset.isPresent()) {
            if (asset.get() instanceof Image) {
                return loadImage((Image) asset.get(), widthParam, heightParam);
            } else {
                return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(String
                        .format("The asset with the requested UUID \"%s\" "
                                    + "is not an image.",
                                uuid))
                    .build();
            }
        } else {
            return Response
                .status(Response.Status.NOT_FOUND)
                .entity(String
                    .format("The requested image \"%s\" does not exist.",
                            uuid))
                .build();
        }
    }

    @GET
    @Path("/uuid-{uuid}/properties")
    public Response getImagePropertiesByUuid(
        @PathParam("content-section") final String sectionName,
        @PathParam("uuid") final String uuid) {

        final Optional<Asset> asset = assetRepo.findByUuidAndType(uuid,
                                                                  Image.class);

        if (asset.isPresent()) {
            if (asset.get() instanceof Image) {
                return readImageProperties((Image) asset.get());
            } else {
                return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(String
                        .format("The asset with the requested UUID \"%s\" "
                                    + "is not an image.",
                                uuid))
                    .build();
            }
        } else {
            return Response
                .status(Response.Status.NOT_FOUND)
                .entity(String
                    .format("The requested image \"%s\" does not exist.",
                            uuid))
                .build();
        }
    }

    /**
     * Return the image requested by the provided content section and path.
     *
     * The URL for an image contains the content section and the path to the
     * image. If there is no image for the provided content section and path an
     * 404 error is returned.
     *
     * This method also accepts two parameters which can be specified as query
     * parameters on the URL: {@code width} and {@code height}. If one or both
     * are provided the image is scaled before it is send the the user agent
     * which requested the image. The method preserves the aspect ratio of the
     * image. If the parameters have different scale factors meaning that the
     * aspect ratio of the image would not be preserved the parameter with the
     * smallest difference to one if used. The other parameter is ignored and
     * replaced with a value which preserves the aspect ratio.
     *
     * @param sectionName The name of the content section which contains the
     *                    image.
     * @param path        The path to the image.
     * @param widthParam  The width to scale the image. If the value is 0 or
     *                    less or the value is not a valid integer it parameter
     *                    is ignored.
     * @param heightParam The height to scale the image. If the value is 0 or
     *                    less or the value is not a valid integer it parameter
     *                    is ignored.
     *
     * @return A {@link Response} containing the scaled image or an error value.
     */
    @GET
    @Path("/{path:^(?!uuid).+$}")
    public Response getImage(
        @PathParam("content-section")
        final String sectionName,
        @PathParam("path")
        final String path,
        @QueryParam("width")
        @DefaultValue("-1")
        final String widthParam,
        @QueryParam("height")
        @DefaultValue("-1")
        final String heightParam) {

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
                return loadImage((Image) asset.get(), widthParam, heightParam);
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
    }

    /**
     * Provides several properties of an image to a user agent as JSON.
     *
     * @param sectionName The name of the content section which contains the
     *                    image.
     * @param path        The path to the image.
     *
     * @return A {@link Response} with the informations about the requested
     *         image.
     */
    @GET
    @Path("/{path:^(?!uuid).+$}/properties")
    public Response getImageProperties(
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
                return readImageProperties((Image) asset.get());
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
    }

    /**
     * Helper method for loading the image from the {@link Image} asset entity.
     *
     * This method also does the scaling of the image.
     *
     * @param image       The image asset containing the image.
     * @param widthParam  The value of the width parameter.
     * @param heightParam The value of the height parameter.
     *
     * @return The {@link Response} for sending the (scaled) image to the
     *         requesting user agent.
     */
    private Response loadImage(final Image image,
                               final String widthParam,
                               final String heightParam) {

        final byte[] data = image.getData();
        final String mimeType = image.getMimeType().toString();

        final InputStream inputStream = new ByteArrayInputStream(data);
        final BufferedImage bufferedImage;
        final String imageFormat;
        try {
            final ImageInputStream imageInputStream = ImageIO
                .createImageInputStream(inputStream);
            final Iterator<ImageReader> readers = ImageIO
                .getImageReaders(imageInputStream);
            final ImageReader imageReader;
            if (readers.hasNext()) {
                imageReader = readers.next();
            } else {
                LOGGER.error("No image reader for image {} (UUID: {}) "
                                 + "available.",
                             image.getDisplayName(),
                             image.getUuid());
                return Response.serverError().build();
            }
            imageReader.setInput(imageInputStream);
            bufferedImage = imageReader.read(0);
            imageFormat = imageReader.getFormatName();
        } catch (IOException ex) {
            LOGGER.error("Failed to load image {} (UUID: {}).",
                         image.getDisplayName(),
                         image.getUuid());
            LOGGER.error(ex);
            return Response.serverError().build();
        }

        // Yes, this is correct. The parameters provided in the URL
        // are expected to be integers. The private scaleImage method
        // works with floats to be accurate (divisions are performed 
        // with the values for width and height)
        final int width = parseScaleParameter(widthParam, "width");
        final int height = parseScaleParameter(heightParam, "height");
        final java.awt.Image scaledImage = scaleImage(bufferedImage,
                                                      width,
                                                      height);

        final ByteArrayOutputStream outputStream
                                        = new ByteArrayOutputStream();
        final BufferedImage bufferedScaledImage = new BufferedImage(
            scaledImage.getWidth(null),
            scaledImage.getHeight(null),
            bufferedImage.getType());
        bufferedScaledImage
            .getGraphics()
            .drawImage(scaledImage, 0, 0, null);
        try {
            ImageIO
                .write(bufferedScaledImage, imageFormat, outputStream);
        } catch (IOException ex) {
            LOGGER.error("Failed to render scaled variant of image {} "
                             + "(UUID: {}).",
                         image.getDisplayName(),
                         image.getUuid());
            LOGGER.error(ex);
            return Response.serverError().build();
        }

        return Response
            .ok(outputStream.toByteArray(), mimeType)
            .build();
    }

    /**
     * Helper method for reading the image properties and converting them into
     * an JSON response.
     *
     * @param image The image which properties are read.
     *
     * @return A {@link Response} with the image properties as JSON.
     */
    private Response readImageProperties(final Image image) {

        final byte[] data = image.getData();
        final String mimeType = image.getMimeType().toString();

        final InputStream inputStream = new ByteArrayInputStream(data);
        final BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(inputStream);
        } catch (IOException ex) {
            LOGGER.error("Failed to load image {} (UUID: {}).",
                         image.getDisplayName(),
                         image.getUuid());
            LOGGER.error(ex);
            return Response.serverError().build();
        }

        final String imageProperties = String
            .format("{%n"
                        + "    \"name\": \"%s\",%n"
                        + "    \"filename\": \"%s\",%n"
                        + "    \"mimetype\": \"%s\",%n"
                        + "    \"width\": %d,%n"
                        + "    \"height\": %d%n"
                        + "}",
                    image.getDisplayName(),
                    image.getFileName(),
                    mimeType,
                    bufferedImage.getWidth(),
                    bufferedImage.getHeight());

        return Response
            .ok(imageProperties, "application/json")
            .build();
    }

    /**
     * Helper method for parsing the parameters for scaling an image into
     * integers.
     *
     * @param parameterValue The value to parse as integer.
     * @param parameter      The name of the parameter (used for logging
     *                       output).
     *
     * @return The integer value of the parameter or -1 if the provided value is
     *         not a valid integer.
     */
    private int parseScaleParameter(final String parameterValue,
                                    final String parameter) {
        try {
            return Integer.parseInt(parameterValue);
        } catch (NumberFormatException ex) {
            LOGGER.warn("Provided value \"{}\" for parameter \"{}\" is "
                            + "not an integer. Ignoring value.",
                        parameterValue,
                        parameter);
            LOGGER.warn(ex);
            return -1;
        }
    }

    /**
     * Helper method for scaling the image while preserving the aspect ratio of
     * the image.
     *
     * @param image         The image to scale.
     * @param scaleToWidth  The width to which is scaled.
     * @param scaleToHeight The height the which image is scaled.
     *
     * @return The scaled image.
     */
    private java.awt.Image scaleImage(final BufferedImage image,
                                      final float scaleToWidth,
                                      final float scaleToHeight) {

        final float originalWidth = image.getWidth();
        final float originalHeight = image.getHeight();
        final float originalAspectRatio = originalWidth / originalHeight;

        if (scaleToWidth > 0 && scaleToHeight > 0) {
            //Check if parameters preserve aspectRatio. If not use the smaller
            //scale factor.

            final float scaleToAspectRatio = scaleToWidth / scaleToHeight;
            if (Math.abs(scaleToAspectRatio - originalAspectRatio) < 0.009f) {
                // Scale the image.

                return image.getScaledInstance(Math.round(scaleToWidth),
                                               Math.round(scaleToHeight),
                                               java.awt.Image.SCALE_SMOOTH);
            } else {
                //Use the scale factor nearer to one for both dimensions
                final float scaleFactorWidth = scaleToWidth / originalWidth;
                final float scaleFactorHeight = scaleToHeight / originalHeight;
                final float differenceWidth = Math.abs(scaleFactorWidth - 1);
                final float differenceHeight = Math.abs(scaleFactorHeight - 1);

                final float scaleFactor;
                if (differenceWidth < differenceHeight) {
                    scaleFactor = scaleFactorWidth;
                } else {
                    scaleFactor = scaleFactorHeight;
                }

                return scaleImage(image,
                                  originalWidth * scaleFactor,
                                  originalHeight * scaleFactor);
            }

        } else if (scaleToWidth > 0 && scaleToHeight <= 0) {
            //Calculate the height to which to image is scaled based on the 
            //scale factor for the width
            final float scaleFactor = scaleToWidth / originalWidth;
            final float height = originalHeight * scaleFactor;

            return scaleImage(image, scaleToWidth, height);
        } else if (scaleToWidth <= 0 && scaleToHeight >= 0) {
            //Calculate the width to which to image is scaled based on the 
            //scale factor for the height
            final float scaleFactor = scaleToHeight / originalHeight;
            final float width = originalWidth * scaleFactor;

            return scaleImage(image, width, scaleToHeight);
        } else {
            //Return the image as is.
            return image;
        }
    }

}
