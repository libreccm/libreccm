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
package com.arsdigita.cms.ui.authoring.assets.relatedinfo;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.assets.AssetSearchWidget;
import com.arsdigita.cms.ui.authoring.assets.AttachmentListSelectionModel;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.UnexpectedErrorException;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetRepository;
import org.librecms.contentsection.AttachmentList;
import org.librecms.contentsection.ItemAttachmentManager;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class RelatedInfoAttachAssetForm
    extends Form
    implements FormInitListener,
               FormProcessListener,
               FormSubmissionListener {

    private final RelatedInfoStep relatedInfoStep;
    private final ItemSelectionModel itemSelectionModel;
    private final AttachmentListSelectionModel listSelectionModel;
    private final StringParameter selectedLanguageParameter;

    private final AssetSearchWidget searchWidget;
    private final SaveCancelSection saveCancelSection;

    public RelatedInfoAttachAssetForm(
        final RelatedInfoStep relatedInfoStep,
        final ItemSelectionModel itemSelectionModel,
        final AttachmentListSelectionModel listSelectionModel,
        final StringParameter selectedLangugeParam) {

        super("relatedinfo-attach-asset-form");

        this.relatedInfoStep = relatedInfoStep;
        this.itemSelectionModel = itemSelectionModel;
        this.listSelectionModel = listSelectionModel;
        this.selectedLanguageParameter = selectedLangugeParam;

        searchWidget = new AssetSearchWidget("asset-search-widget");
        super.add(searchWidget);
        saveCancelSection = new SaveCancelSection();
        super.add(saveCancelSection);

        super.addInitListener(this);
        super.addProcessListener(this);
        super.addSubmissionListener(this);
    }

    @Override
    public void register(final Page page) {
        super.register(page);
        
        page.addComponentStateParam(this, itemSelectionModel.getStateParameter());
        page.addComponentStateParam(this, listSelectionModel.getStateParameter());
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {
        if (listSelectionModel.getSelectedKey(event.getPageState()) == null) {
            throw new UnexpectedErrorException("The selected list null. "
                                                   + "This should not happen.");
        }
    }

    @Override
    public void process(final FormSectionEvent event) throws
        FormProcessException {

        final PageState state = event.getPageState();

        if (listSelectionModel.getSelectedKey(state) == null) {
            throw new UnexpectedErrorException("The selected list null. "
                                                   + "This should not happen.");
        }

        final Object value = searchWidget.getValue(state);
        if (value != null) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ItemAttachmentManager attachmentManager = cdiUtil
                .findBean(ItemAttachmentManager.class);
            final AssetRepository assetRepo = cdiUtil
                .findBean(AssetRepository.class);
            final Asset asset = assetRepo
                .findById((long) value)
                .orElseThrow(() -> new UnexpectedErrorException(String
                .format("No Asset with ID %d in the database.", value)));

            final AttachmentList list = listSelectionModel
                .getSelectedAttachmentList(state);

            attachmentManager.attachAsset(asset, list);
        }

        relatedInfoStep.showAttachmentListTable(state);
    }

    @Override
    public void submitted(final FormSectionEvent event) throws
        FormProcessException {

        final PageState state = event.getPageState();

        listSelectionModel.clearSelection(state);
        relatedInfoStep.showAttachmentListTable(state);
    }

}
