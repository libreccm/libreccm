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
import org.librecms.assets.VideoAsset;
import org.librecms.assets.LegalMetadata;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetRepository;

import java.util.Map;

/**
 *
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 */
public class VideoForm extends AbstractBinaryAssetForm<VideoAsset> {

    private TextField width;
    private TextField height;
    private AssetSearchWidget assetSearchWidget;

    public VideoForm(final AssetPane assetPane) {
        super(assetPane);
    }

    @Override
    protected void addWidgets() {

        width = new TextField("width-text");
        height = new TextField("height-text");
        assetSearchWidget = new AssetSearchWidget("legal-metadata",
                                                  LegalMetadata.class);

        add(new Label(new GlobalizedMessage(
            "cms.ui.assets.video.width.label",
            CmsConstants.CMS_BUNDLE
        )));
        add(width);

        add(new Label(new GlobalizedMessage(
            "cms.ui.assets.video.height.label",
            CmsConstants.CMS_BUNDLE
        )));
        add(height);

        add(new Label(new GlobalizedMessage(
            "cms.ui.assets.video.legal_metadata.label",
            CmsConstants.CMS_BUNDLE
        )));
        add(assetSearchWidget);
    }

    @Override
    protected void initForm(final PageState state,
                            final Map<String, Object> data) {

        super.initForm(state, data);

        if (getSelectedAssetId(state) != null) {

            final long widthValue = (long) data.get(VideoFormController.WIDTH);
            final long heightValue = (long) data
                .get(VideoFormController.HEIGHT);

            width.setValue(state, Long.toString(widthValue));
            height.setValue(state, Long.toString(heightValue));

            if (data.containsKey(VideoFormController.LEGAL_METADATA_ID)) {

                assetSearchWidget.setValue(
                    state,
                    data.containsKey(VideoFormController.LEGAL_METADATA_ID));

            }
        }
    }

    @Override
    protected Class<VideoAsset> getAssetClass() {
        return VideoAsset.class;
    }

    @Override
    protected Map<String, Object> collectData(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

        final Map<String, Object> data = super.collectData(event);

        data.put(VideoFormController.WIDTH, width.getValue(state));
        data.put(VideoFormController.HEIGHT, height.getValue(state));

        if (assetSearchWidget.getValue(state) != null) {

            data.put(VideoFormController.LEGAL_METADATA_ID,
                     assetSearchWidget.getValue(state));

        }

        return data;
    }

}
