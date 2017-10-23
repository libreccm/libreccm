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

import org.librecms.assets.LegalMetadata;
import org.librecms.assets.VideoAsset;
import org.librecms.contentsection.Asset;

import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * Renderer for {@link VideoAsset}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@AssetRenderer(renders = VideoAsset.class)
public class VideoAssetRenderer extends BinaryAssetRenderer {

    @Inject
    @AssetRenderer(renders = LegalMetadata.class)
    private AbstractAssetRenderer legalMetadataRenderer;

    /**
     * Renders the provided {@link VideoAsset}. The following properties a put
     * into {@code result}:
     *
     * <pre>
     *  {
     *      width: {@link VideoAsset#getWidth()}
     *      height: {@link VideoAsset#getHeight()}
     *      legalMetadata: {@link VideoAsset#getLegalMetadata()}
     *  }
     * </pre>
     *
     * @param asset    The asset to render.
     * @param language The current language.
     * @param result   The map into which the result is put.
     */
    @Override
    protected void renderAsset(final Asset asset,
                               final Locale language,
                               final Map<String, Object> result) {

        super.renderAsset(asset, language, result);

        final VideoAsset videoAsset;
        if (asset instanceof VideoAsset) {
            videoAsset = (VideoAsset) asset;
        } else {
            return;
        }

        result.put("width", videoAsset.getWidth());
        result.put("height", videoAsset.getHeight());
        result.put("legalMetadata",
                   legalMetadataRenderer.render(videoAsset.getLegalMetadata(),
                                                language));
    }

}
