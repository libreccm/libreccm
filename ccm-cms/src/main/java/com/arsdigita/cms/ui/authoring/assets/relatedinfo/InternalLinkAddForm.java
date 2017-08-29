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
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.assets.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.assets.AttachmentListSelectionModel;

import org.libreccm.cdi.utils.CdiUtil;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class InternalLinkAddForm
    extends Form
    implements FormInitListener,
               FormProcessListener,
               FormSubmissionListener {

    private final RelatedInfoStep relatedInfoStep;
    private final ItemSelectionModel itemSelectionModel;
    private final AttachmentListSelectionModel listSelectionModel;
    private final StringParameter selectedLanguageParam;

    private final TextField titleField;
//    private final TextArea descriptionArea;
    private final ItemSearchWidget itemSearchWidget;
    private final SaveCancelSection saveCancelSection;

    public InternalLinkAddForm(
        final RelatedInfoStep relatedInfoStep,
        final ItemSelectionModel itemSelectionModel,
        final AttachmentListSelectionModel listSelectionModel,
        final StringParameter selectedLanguageParam) {

        super("relatedinfo-attach-internallink-form");

        this.relatedInfoStep = relatedInfoStep;
        this.itemSelectionModel = itemSelectionModel;
        this.listSelectionModel = listSelectionModel;
        this.selectedLanguageParam = selectedLanguageParam;

        titleField = new TextField("link-title");
//        descriptionArea = new TextArea("link-description");
        itemSearchWidget = new ItemSearchWidget("link-item-search");
        saveCancelSection = new SaveCancelSection();
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {

    }

    @Override
    public void process(final FormSectionEvent event) throws
        FormProcessException {

        final PageState state = event.getPageState();

        if (saveCancelSection.getSaveButton().isSelected(state)) {

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final RelatedInfoStepController controller = cdiUtil
                .findBean(RelatedInfoStepController.class);

            controller.createInternalLink(
                listSelectionModel.getSelectedAttachmentList(state),
                (Long) itemSearchWidget.getValue(state),
                (String) titleField.getValue(state),
                (String) state.getValue(selectedLanguageParam));

            relatedInfoStep.showAttachmentsTable(state);
        }
    }

    @Override
    public void submitted(final FormSectionEvent event) throws
        FormProcessException {
        
        if (saveCancelSection.getCancelButton().isSelected(event.getPageState())) {
            relatedInfoStep.showAttachmentsTable(event.getPageState());
        }
        
    }

}
