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

import org.librecms.assets.ExternalAudioAsset;
import org.librecms.assets.LegalMetadata;
import org.librecms.contentsection.AssetRepository;

import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@IsControllerForAssetType(ExternalAudioAsset.class)
public class ExternalAudioAssetFormController
    extends AbstractBookmarkFormController<ExternalAudioAsset> {

    protected static final String LEGAL_METADATA_ID = "legalMetadataId";

    @Inject
    private AssetRepository assetRepository;

    @Override
    public ExternalAudioAsset createAsset() {
        return new ExternalAudioAsset();
    }

    @Override
    protected Map<String, Object> getAssetData(final ExternalAudioAsset asset,
                                               final Locale selectedLocale) {

        final Map<String, Object> data = super.getAssetData(asset,
                                                            selectedLocale);

        final LegalMetadata legalMetadata = asset.getLegalMetadata();
        if (legalMetadata != null) {
            data.put(LEGAL_METADATA_ID, legalMetadata.getObjectId());
        }

        return data;
    }

    @Override
    public void updateAssetProperties(final ExternalAudioAsset asset,
                                      final Locale selectedLocale,
                                      final Map<String, Object> data) {

        super.updateAssetProperties(asset, selectedLocale, data);

        if (data.containsKey(LEGAL_METADATA_ID)) {

            final long legalMetadataId = (long) data.get(LEGAL_METADATA_ID);
            final LegalMetadata legalMetadata = assetRepository
                .findById(legalMetadataId, LegalMetadata.class)
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                "No LegalMetadata with ID %d found.", legalMetadataId)));

            asset.setLegalMetadata(legalMetadata);
        }
    }

}
