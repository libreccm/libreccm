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

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.cms.ui.assets.AbstractAssetForm;
import com.arsdigita.cms.ui.assets.AssetPane;
import com.arsdigita.globalization.GlobalizedMessage;

import org.librecms.CmsConstants;
import org.librecms.assets.LegalMetadata;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class LegalMetadataForm extends AbstractAssetForm<LegalMetadata> {

    private TextArea rightsHolder;
    private TextArea rights;
    private TextArea publisher;
    private TextArea creator;

    public LegalMetadataForm(final AssetPane assetPane) {
        super(assetPane);
    }

    @Override
    protected void addWidgets() {

        final BoxPanel panel = new BoxPanel(BoxPanel.VERTICAL);

        panel.add(new Label(new GlobalizedMessage(
            "cms.ui.assets.legalmetadata.rightsholder",
            CmsConstants.CMS_BUNDLE)));
        rightsHolder = new TextArea("legalmetadata-rightsholder");
        panel.add(rightsHolder);

        panel.add(new Label(new GlobalizedMessage(
            "cms.ui.assets.legalmetadata.rights",
            CmsConstants.CMS_BUNDLE)));
        rights = new TextArea("legalmetadata-rights");
        panel.add(rights);

        panel.add(new Label(new GlobalizedMessage(
            "cms.ui.assets.legalmetadata.publisher",
            CmsConstants.CMS_BUNDLE)));
        publisher = new TextArea("legalmetadata-rights");
        panel.add(publisher);

        panel.add(new Label(new GlobalizedMessage(
            "cms.ui.assets.legalmetadata.creator",
            CmsConstants.CMS_BUNDLE)));
        creator = new TextArea("legalmetadata-creator");
        panel.add(creator);

        add(panel);
    }

    @Override
    protected void initForm(final PageState state,
                            final Map<String, Object> data) {
        
        super.initForm(state, data);

        if (getSelectedAssetId(state) != null) {

            rightsHolder.setValue(
                state,
                data.get(LegalMetadataFormController.RIGHTS_HOLDER));
            rights.setValue(state,
                            data.get(LegalMetadataFormController.RIGHTS));
            publisher.setValue(state,
                               data.get(LegalMetadataFormController.PUBLISHER));
            creator.setValue(state,
                             data.get(LegalMetadataFormController.CREATOR));
        }
    }

    @Override
    protected void showLocale(final PageState state) {

        final Long selectedAssetId = getSelectedAssetId(state);

        if (selectedAssetId != null) {

            final Map<String, Object> data = getController()
                .getAssetData(selectedAssetId,
                              LegalMetadata.class,
                              getSelectedLocale(state));

            rights.setValue(state,
                            data.get(LegalMetadataFormController.RIGHTS));
        }
    }

    @Override
    protected Class<LegalMetadata> getAssetClass() {
        return LegalMetadata.class;
    }

    @Override
    protected Map<String, Object> collectData(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();
        final Map<String, Object> data = new HashMap<>();

        data.put(LegalMetadataFormController.CREATOR, creator.getValue(state));
        data.put(LegalMetadataFormController.PUBLISHER,
                 publisher.getValue(state));
        data.put(LegalMetadataFormController.RIGHTS,
                 rights.getValue(state));
        data.put(LegalMetadataFormController.RIGHTS_HOLDER,
                 rightsHolder.getValue(state));

        return data;
    }
}
