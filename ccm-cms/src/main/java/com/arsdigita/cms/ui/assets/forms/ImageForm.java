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
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.cms.ui.assets.AssetPane;
import com.arsdigita.cms.ui.assets.AssetSearchWidget;
import com.arsdigita.globalization.GlobalizedMessage;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.assets.BinaryAsset;
import org.librecms.assets.ExternalAudioAsset;
import org.librecms.assets.Image;
import org.librecms.assets.LegalMetadata;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetRepository;

import java.util.Optional;

/**
 *
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 */
public class ImageForm extends BinaryAssetForm {

    private TextField width;
    private TextField height;
    private AssetSearchWidget assetSearchWidget;

    public ImageForm(final AssetPane assetPane) {
        super(assetPane);
    }

    @Override
    protected void addWidgets() {

        width = new TextField("width-text");
        height = new TextField("height-text");
        assetSearchWidget = new AssetSearchWidget("legal-metadata", LegalMetadata.class);

        add(new Label(new GlobalizedMessage(
                "cms.ui.assets.image.width.label",
                CmsConstants.CMS_BUNDLE
        )));
        add(width);

        add(new Label(new GlobalizedMessage(
                "cms.ui.assets.image.height.label",
                CmsConstants.CMS_BUNDLE
        )));
        add(height);

        add(new Label(new GlobalizedMessage(
                "cms.ui.assets.image.legal_metadata.label",
                CmsConstants.CMS_BUNDLE
        )));
        add(assetSearchWidget);
    }

    @Override
    protected void initForm(PageState state, Optional<Asset> selectedAsset) {

        super.initForm(state, selectedAsset);

        if (selectedAsset.isPresent()) {

            Image image = (Image) selectedAsset.get();

            width.setValue(state,
                    Long.toString(image.getWidth()));
            height.setValue(state,
                    Long.toString(image.getHeight()));
            final LegalMetadata legalMetadata = image
                    .getLegalMetadata();
            if (legalMetadata != null) {
                assetSearchWidget.setValue(state, legalMetadata.getObjectId());
            }
        }
    }

    @Override
    protected Asset createAsset(final FormSectionEvent event)
            throws FormProcessException {

        final Image image = (Image) super.createAsset(event);

        final PageState state = event.getPageState();

        image.setHeight(Long.parseLong((String) height.getValue(state)));
        image.setWidth(Long.parseLong((String) width.getValue(state)));
        updateData(image, state);

        return image;
    }

    @Override
    protected void updateAsset(final Asset asset,
                               final FormSectionEvent event)
            throws FormProcessException {

        super.updateAsset(asset, event);

        final PageState state = event.getPageState();

        final Image image = (Image) asset;

        image.setHeight(Long.parseLong((String) height.getValue(state)));
        image.setWidth(Long.parseLong((String) width.getValue(state)));

        updateData(image, state);
    }

    protected void updateData(final Image image,
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

            image.setLegalMetadata(legalMetadata);
        }
    }

    @Override
    protected BinaryAsset createBinaryAsset(final PageState state) {
        return new Image();
    }
}
