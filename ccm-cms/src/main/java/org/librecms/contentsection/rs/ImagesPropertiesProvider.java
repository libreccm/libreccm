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
package org.librecms.contentsection.rs;

import org.libreccm.core.UnexpectedErrorException;
import org.librecms.assets.Image;
import org.librecms.contentsection.Asset;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ProvidesPropertiesForAssetType(Image.class)
public class ImagesPropertiesProvider implements AssetPropertiesProvider {

    @Override
    public void addProperties(final Asset asset, 
                              final JsonObjectBuilder builder) {
        
        Objects.requireNonNull(asset);
        Objects.requireNonNull(builder);
        
        if (!(asset instanceof Image)) {
            throw new IllegalArgumentException(String
                .format("\"%s\" only supports assets of type \"%s\". Check "
                    + "the qualifier annotation on \"%s\".",
                        getClass().getName(),
                        Image.class.getName(),
                        getClass().getName()));
        }
        
        final Image image = (Image) asset;
        final byte[] data = image.getData();
        final InputStream inputStream = new ByteArrayInputStream(data);
        final BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(inputStream);
        } catch(IOException ex) {
            throw new UnexpectedErrorException(ex);
        }
        
        builder
            .add("name", image.getDisplayName())
            .add("filename", image.getFileName())
            .add("mimetype", image.getMimeType().toString())
            .add("width", bufferedImage.getWidth())
            .add("height", bufferedImage.getHeight())
            .add("size", data.length);
    }
    
}
