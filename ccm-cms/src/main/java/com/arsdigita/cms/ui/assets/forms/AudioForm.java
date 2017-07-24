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
package com.arsdigita.cms.ui.assets.forms;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ui.assets.AssetPane;
import com.arsdigita.cms.ui.assets.AssetSearchWidget;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.assets.AudioAsset;
import org.librecms.assets.BinaryAsset;
import org.librecms.assets.LegalMetadata;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetRepository;

import java.util.Optional;

/**
 *
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class AudioForm extends AbstractBinaryAssetForm<AudioAsset> {

    private AssetSearchWidget assetSearchWidget;

    public AudioForm(final AssetPane assetPane) {
        super(assetPane);
    }

    @Override
    protected void addWidgets() {

        assetSearchWidget = new AssetSearchWidget("legal-metadata", LegalMetadata.class);

        add(new Label(new GlobalizedMessage(
                "cms.ui.assets.audio.legal_metadata.label",
                CmsConstants.CMS_BUNDLE
        )));
        add(assetSearchWidget);
    }

    @Override
    protected void initForm(final PageState state, 
                            final Optional<AudioAsset> selectedAsset) {

        super.initForm(state, selectedAsset);

        if (selectedAsset.isPresent()) {

            final AudioAsset audioAsset = selectedAsset.get();

            final LegalMetadata legalMetadata = audioAsset
                    .getLegalMetadata();
            if (legalMetadata != null) {
                assetSearchWidget.setValue(state, legalMetadata.getObjectId());
            }
        }
    }

    @Override
    protected Class<AudioAsset> getAssetClass() {
        return AudioAsset.class;
    }
    
//    @Override
//    protected Asset createAsset(final FormSectionEvent event)
//            throws FormProcessException {
//
//        final AudioAsset audioAsset = (AudioAsset) super.createAsset(event);
//
//        final PageState state = event.getPageState();
//
//        updateData(audioAsset, state);
//
//        return audioAsset;
//    }

    @Override
    protected void updateAsset(final Asset asset,
                               final FormSectionEvent event)
            throws FormProcessException {

        super.updateAsset(asset, event);

        final PageState state = event.getPageState();

        final AudioAsset audioAsset = (AudioAsset) asset;

        updateData(audioAsset, state);
    }

    protected void updateData(final AudioAsset audioAsset,
                              final PageState state) {

        final Long legalMetadataId = (Long) assetSearchWidget.getValue(state);
        if (legalMetadataId != null) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final AssetRepository assetRepo = cdiUtil.findBean(
                    AssetRepository.class);
            final LegalMetadata legalMetadata = (LegalMetadata) assetRepo
                    .findById(legalMetadataId)
                    .orElseThrow(() -> new IllegalArgumentException(String.format(
                            "No LegalMetadata asset with ID %d in the database.",
                            legalMetadataId)));

            audioAsset.setLegalMetadata(legalMetadata);
        }
    }

//    @Override
//    protected BinaryAsset createBinaryAsset(final PageState state) {
//        return new AudioAsset();
//    }
}
