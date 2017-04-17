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
import com.arsdigita.cms.ui.assets.AssetPane;
import com.arsdigita.cms.ui.assets.AssetSearchWidget;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.assets.Bookmark;

import java.util.Objects;
import java.util.Optional;

import org.librecms.assets.ExternalVideoAsset;
import org.librecms.assets.LegalMetadata;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetRepository;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ExternalVideoAssetForm extends BookmarkForm {

    private AssetSearchWidget assetSearchWidget;

    public ExternalVideoAssetForm(final AssetPane assetPane) {
        super(assetPane);
    }

    @Override
    public void addWidgets() {
        super.addWidgets();

        add(new Label(new GlobalizedMessage(
            "cms.ui.assets.external_video_asset.legal_metadata.label",
            CmsConstants.CMS_BUNDLE)));
        assetSearchWidget = new AssetSearchWidget("legal-metadata",
                                                  LegalMetadata.class);
        add(assetSearchWidget);

        //ToDo
    }

    @Override
    protected void initForm(final PageState state,
                            final Optional<Asset> selectedAsset) {
        super.initForm(state, selectedAsset);

        if (selectedAsset.isPresent()) {
            final ExternalVideoAsset externalVideoAsset
                                         = (ExternalVideoAsset) selectedAsset
                    .get();

            final LegalMetadata legalMetadata = externalVideoAsset
                .getLegalMetadata();
            if (legalMetadata != null) {
                assetSearchWidget.setValue(state, legalMetadata.getObjectId());
            }
        }
    }

    @Override
    protected Asset createAsset(final PageState state)
        throws FormProcessException {

        Objects.requireNonNull(state);

        final ExternalVideoAsset externalVideoAsset = new ExternalVideoAsset();

        updateData((Bookmark) externalVideoAsset, state);
        updateData(externalVideoAsset, state);

        return externalVideoAsset;
    }

    protected void updateData(final ExternalVideoAsset externalVideoAsset,
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

            externalVideoAsset.setLegalMetadata(legalMetadata);
        }
    }

    @Override
    protected void updateAsset(final Asset asset, final PageState state) {

        Objects.requireNonNull(asset);
        Objects.requireNonNull(state);

        if (!(asset instanceof ExternalVideoAsset)) {
            throw new IllegalArgumentException(String.format(
                "Provided asset is not an instance of class (or sub class of) "
                    + "'%s' but is an instance of class '%s'",
                ExternalVideoAsset.class.getName(),
                asset.getClass().getName()));
        }

        final ExternalVideoAsset externalVideoAsset = (ExternalVideoAsset) asset;

        updateData((Bookmark) externalVideoAsset, state);
        updateData(externalVideoAsset, state);
    }

}
