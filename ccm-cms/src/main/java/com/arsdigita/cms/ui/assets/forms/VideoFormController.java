/*
 * Copyright (C) 2019 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.assets.forms;

import com.arsdigita.cms.ui.assets.IsControllerForAssetType;
import com.arsdigita.cms.ui.assets.forms.AbstractBinaryAssetFormController;

import org.librecms.contentsection.AssetRepository;

import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.librecms.assets.LegalMetadata;
import org.librecms.assets.VideoAsset;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@IsControllerForAssetType(VideoAsset.class)
public class VideoFormController
    extends AbstractBinaryAssetFormController<VideoAsset> {

    protected static final String LEGAL_METADATA_ID = "legalMetadataId";
    protected static final String HEIGHT = "height";
    protected static final String WIDTH = "width";

    @Inject
    private AssetRepository assetRepository;

    @Override
    protected Map<String, Object> getAssetData(final VideoAsset asset,
                                               final Locale selectedLocale) {

        final Map<String, Object> data = super.getAssetData(asset,
                                                            selectedLocale);

        data.put(WIDTH, asset.getWidth());
        data.put(HEIGHT, asset.getHeight());

        if (asset.getLegalMetadata() != null) {
            final long legalMetadataId = asset.getLegalMetadata().getObjectId();
            data.put(LEGAL_METADATA_ID, legalMetadataId);
        }

        return data;
    }

    @Override
    public void updateAssetProperties(final VideoAsset asset,
                                      final Locale selectedLocale,
                                      final Map<String, Object> data) {

        super.updateAssetProperties(asset, selectedLocale, data);

        if (data.containsKey(WIDTH)) {
            final long width = (long) data.get(WIDTH);
            asset.setWidth(width);
        }

        if (data.containsKey(HEIGHT)) {
            final long height = (long) data.get(HEIGHT);
            asset.setHeight(height);
        }

        if (data.containsKey(LEGAL_METADATA_ID)) {

            final LegalMetadata legalMetadata = assetRepository
                .findById((long) data.get(LEGAL_METADATA_ID),
                          LegalMetadata.class)
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                "No LegalMetadata with ID %d found",
                data.get(LEGAL_METADATA_ID)
            )));
            asset.setLegalMetadata(legalMetadata);
        }
    }

    @Override
    public VideoAsset createAsset() {
        return new VideoAsset();
    }

}
