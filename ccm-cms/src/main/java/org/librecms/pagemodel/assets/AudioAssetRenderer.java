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

import org.librecms.assets.AudioAsset;
import org.librecms.assets.LegalMetadata;
import org.librecms.contentsection.Asset;

import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * Renderer for {@link AudioAsset}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@AssetRenderer(renders = AudioAsset.class)
public class AudioAssetRenderer extends BinaryAssetRenderer {

    @Inject
    @AssetRenderer(renders = LegalMetadata.class)
    private AbstractAssetRenderer legalMetadataRenderer;

    /**
     * Renders to provided {@link AudioAsset}. The following properties are put
     * into {@code result}:
     *
     * <pre>
     *  {
     *      "legamMetadata": {@link AudioAsset#getLegalMetadata()}.
     *  }
     * </pre>
     *
     * The associated {@link LegalMetadata} asset is rendered using
     * {@link LegalMetadataRenderer} and the result is put {@code result} under
     * the key {@code legalMetadata}.
     *
     * @param asset    The asset to render.
     * @param language The current language.
     * @param result   The map into which the result is placed.
     */
    @Override
    protected void renderAsset(final Asset asset,
                               final Locale language,
                               final Map<String, Object> result) {

        super.renderAsset(asset, language, result);

        final AudioAsset audioAsset;
        if (asset instanceof AudioAsset) {
            audioAsset = (AudioAsset) asset;
        } else {
            return;
        }

        result.put("legalMetadata",
                   legalMetadataRenderer.render(audioAsset.getLegalMetadata(),
                                                language));
    }

}
