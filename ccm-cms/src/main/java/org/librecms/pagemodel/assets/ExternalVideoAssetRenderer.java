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

import org.librecms.assets.ExternalVideoAsset;
import org.librecms.assets.LegalMetadata;
import org.librecms.contentsection.Asset;

import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * Renderer for {@link ExternalVideoAsset}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@AssetRenderer(renders = ExternalVideoAsset.class)
public class ExternalVideoAssetRenderer extends BookmarkRenderer {

    @Inject
    @AssetRenderer(renders = LegalMetadata.class)
    private AbstractAssetRenderer legalMetadataRenderer;

    /**
     * Renders the provided {@link ExternalVideoAsset}. In addition to the data
     * put into {@code result} by the {@link BookmarkRenderer} the following
     * properties are put into the map:
     *
     * <pre>
     *  {
     *      "legalMetadata": {@link ExternalVideoAsset#getLegalMetadata()}
     *  }
     * </pre>
     *
     * The associated {@link LegalMetadata} asset is rendered using the
     * {@link LegalMetadata} and the result is put into the map under the key
     * {@code legalMetadata}.
     *
     * @param asset    The asset to render.
     * @param language The current language
     * @param result   The map into which the result is put.
     */
    @Override
    protected void renderAsset(final Asset asset,
                               final Locale language,
                               final Map<String, Object> result) {

        super.renderAsset(asset, language, result);

        final ExternalVideoAsset externalVideoAsset;
        if (asset instanceof ExternalVideoAsset) {
            externalVideoAsset = (ExternalVideoAsset) asset;
        } else {
            return;
        }

        result.put("legalMetadata",
                   legalMetadataRenderer
                       .render(externalVideoAsset.getLegalMetadata(), language));
    }

}
