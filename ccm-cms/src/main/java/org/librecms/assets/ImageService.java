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
package org.librecms.assets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.core.UnexpectedErrorException;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.enterprise.context.Dependent;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * Utility class for working with images (JPEG, PNG, GIF).
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Dependent
public class ImageService {

    private static final Logger LOGGER = LogManager.getLogger(
        ImageService.class
    );

    public ImageProperties getImageProperties(final Image image) {
        final InputStream inputStream = image.getDataAsInputStream();
        return getImageProperties(inputStream);
    }

    public ImageProperties getImageProperties(final InputStream inputStream) {
        final BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(inputStream);
        } catch (IOException ex) {
            throw new UnexpectedErrorException(ex);
        }

        final ImageProperties imageProperties = new ImageProperties();
        imageProperties.setHeight(bufferedImage.getHeight());
        imageProperties.setWidth(bufferedImage.getWidth());

        return imageProperties;
    }

    public byte[] scaleImage(
        final Image image, final int toWidth, final int toHeight
    ) {
        final InputStream inputStream = image.getDataAsInputStream();
        return scaleImage(inputStream, toWidth, toHeight);

    }

    public byte[] scaleImage(
        final InputStream inputStream, final int toWidth, final int toHeight
    ) {
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
                LOGGER.error(
                    "No suitable image reader available"
                );
                throw new UnexpectedErrorException();
            }
            imageReader.setInput(imageInputStream);
            bufferedImage = imageReader.read(imageReader.getMinIndex());
            imageFormat = imageReader.getFormatName();
        } catch (IOException ex) {
            LOGGER.error("Failed to load image.");
            LOGGER.error(ex);
            throw new UnexpectedErrorException();
        }

        final java.awt.Image scaledImage = scaleImage(
            bufferedImage, toWidth, toHeight
        );

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
            LOGGER.error("Failed to render scaled variant of image");
            LOGGER.error(ex);
            throw new UnexpectedErrorException(
                "Failed to render scaled variant of image"
            );
        }

        return outputStream.toByteArray();

    }

    private java.awt.Image scaleImage(
        final BufferedImage image, 
        final float scaleToWidth,
        final float scaleToHeight
    ) {
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
