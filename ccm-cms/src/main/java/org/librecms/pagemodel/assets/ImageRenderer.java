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
package org.librecms.pagemodel.assets;

import org.librecms.assets.Image;
import org.librecms.assets.LegalMetadata;
import org.librecms.contentsection.Asset;

import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@AssetRenderer(renders = Image.class)
@RequestScoped
public class ImageRenderer extends BinaryAssetRenderer {

    @Inject
    @AssetRenderer(renders = LegalMetadata.class)
    private AbstractAssetRenderer legalMetadataRenderer;

    @Override
    protected void renderAsset(final Asset asset,
                               final Locale language,
                               final Map<String, Object> result) {

        super.renderAsset(asset, language, result);

        final Image image;
        if (asset instanceof Image) {
            image = (Image) asset;
        } else {
            return;
        }

        result.put("width", image.getWidth());
        result.put("height", image.getHeight());
        result.put("legalMetadata",
                   legalMetadataRenderer.render(image.getLegalMetadata(),
                                                language));
    }

}
