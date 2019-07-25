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

import org.librecms.CmsConstants;
import org.librecms.assets.ExternalAudioAsset;
import org.librecms.assets.LegalMetadata;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 */
public class ExternalAudioAssetForm
    extends AbstractBookmarkForm<ExternalAudioAsset> {

    private AssetSearchWidget assetSearchWidget;

    public ExternalAudioAssetForm(final AssetPane assetPane) {
        super(assetPane);
    }

    @Override
    public void addWidgets() {
        super.addWidgets();

        add(new Label(new GlobalizedMessage(
            "cms.ui.assets.external_audio_asset.legal_metadata.label",
            CmsConstants.CMS_BUNDLE)));
        assetSearchWidget = new AssetSearchWidget("legal-metadata",
                                                  LegalMetadata.class);
        add(assetSearchWidget);
    }

    @Override
    protected void initForm(final PageState state,
                            final Map<String, Object> data) {

        super.initForm(state, data);

        final Long selectedAssetId = getSelectedAssetId(state);

        if (selectedAssetId != null) {

            if (data.containsKey(
                ExternalAudioAssetFormController.LEGAL_METADATA_ID)) {

                final long legalMetadataId = (long) data
                    .get(ExternalAudioAssetFormController.LEGAL_METADATA_ID);

                assetSearchWidget.setValue(state, legalMetadataId);
            }
        }
    }

    @Override
    protected Class<ExternalAudioAsset> getAssetClass() {
        return ExternalAudioAsset.class;
    }

    @Override
    protected Map<String, Object> collectData(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

        final Map<String, Object> data = new HashMap<>();
        if (assetSearchWidget.getValue(state) != null) {
            
            data.put(ExternalAudioAssetFormController.LEGAL_METADATA_ID,
                     assetSearchWidget.getValue(state));
        }

        return data;
    }

}
