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

import org.librecms.assets.ExternalVideoAsset;
import org.librecms.assets.LegalMetadata;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ExternalVideoAssetForm
    extends AbstractBookmarkForm<ExternalVideoAsset> {

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
    }

    @Override
    protected void initForm(final PageState state,
                            final Map<String, Object> data) {

        super.initForm(state, data);

        final Long selectedAssetId = getSelectedAssetId(state);
        if (selectedAssetId != null) {

            if (data.containsKey(
                ExternalVideoAssetFormController.LEGAL_METADATA_ID)) {
                assetSearchWidget.setValue(
                    state,
                    data.get(ExternalVideoAssetFormController.LEGAL_METADATA_ID));
            }
        }
    }

    @Override
    protected Class<ExternalVideoAsset> getAssetClass() {
        return ExternalVideoAsset.class;
    }

    @Override
    protected Map<String, Object> collectData(final FormSectionEvent event)
        throws FormProcessException {

        final Map<String, Object> data = new HashMap<>();
        final PageState state = event.getPageState();

        if (assetSearchWidget.getValue(state) != null) {
            data.put(ExternalAudioAssetFormController.LEGAL_METADATA_ID,
                     assetSearchWidget.getValue(state));
        }

        return data;
    }

}
