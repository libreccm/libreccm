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

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.ResettableContainer;
import com.arsdigita.cms.ui.authoring.assets.AttachmentListSelectionModel;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.AttachmentList;
import org.librecms.ui.authoring.ContentItemAuthoringStep;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ContentItemAuthoringStep(
    labelBundle = CmsConstants.CMS_BUNDLE,
    labelKey = "related_info_step.label",
    descriptionBundle = CmsConstants.CMS_BUNDLE,
    descriptionKey = "related_info_step.description")
public class RelatedInfoStep extends ResettableContainer {

    private final ItemSelectionModel itemSelectionModel;
    private final AttachmentListSelectionModel selectedListModel;
    private final AttachmentListSelectionModel moveListModel;
    private final AuthoringKitWizard authoringKitWizard;
    private final StringParameter selectedLanguageParam;

    private final RelatedInfoListTable listTable;
    private final RelatedInfoListForm listForm;
    private final ActionLink addListLink;
    private final ActionLink listToFirstLink;

    public RelatedInfoStep(final ItemSelectionModel itemSelectionModel,
                           final AuthoringKitWizard authoringKitWizard,
                           final StringParameter selectedLanguage) {

        super();

        this.itemSelectionModel = itemSelectionModel;
        this.authoringKitWizard = authoringKitWizard;
        this.selectedLanguageParam = selectedLanguage;

        selectedListModel = new AttachmentListSelectionModel(
            "selected-attachment-list");
        moveListModel = new AttachmentListSelectionModel("move-attachment-list");

        listTable = new RelatedInfoListTable(this,
                                             itemSelectionModel,
                                             selectedListModel, moveListModel,
                                             selectedLanguageParam);
        listForm = new RelatedInfoListForm(this,
                                           itemSelectionModel,
                                           selectedListModel,
                                           selectedLanguageParam);

        super.add(listTable);
        super.add(listForm);

        addListLink = new ActionLink(new Label(new GlobalizedMessage(
            "cms.ui.authoring.assets.related_info_step.add_list",
            CmsConstants.CMS_BUNDLE)));
        addListLink.addActionListener(event -> {
            showListEditForm(event.getPageState());
        });

        listToFirstLink = new ActionLink(new GlobalizedMessage(
            "cms.ui.authoring.assets.related_info_step.move_to_beginning",
            CmsConstants.CMS_BUNDLE));
        listToFirstLink.addActionListener(event -> {
            final PageState state = event.getPageState();
            final AttachmentList toMove = moveListModel
                .getSelectedAttachmentList(state);

            final RelatedInfoStepController controller = CdiUtil
                .createCdiUtil()
                .findBean(RelatedInfoStepController.class);

            controller.moveToFirst(itemSelectionModel.getSelectedItem(state),
                                   toMove);

            moveListModel.clearSelection(state);
        });

        moveListModel.addChangeListener(event -> {

            final PageState state = event.getPageState();

            if (moveListModel.getSelectedKey(state) == null) {
                addListLink.setVisible(state, true);
                listToFirstLink.setVisible(state, false);
            } else {
                addListLink.setVisible(state, false);
                listToFirstLink.setVisible(state, true);
            }
        });
    }

    @Override
    public void register(final Page page) {

        super.register(page);

        page.addComponentStateParam(this, selectedListModel.getStateParameter());
        page.addComponentStateParam(this, moveListModel.getStateParameter());

        page.setVisibleDefault(listTable, true);
        page.setVisibleDefault(listForm, false);
        page.setVisibleDefault(addListLink, true);
        page.setVisibleDefault(listToFirstLink, false);
    }

    protected void showAttachmentListTable(final PageState state) {

        listTable.setVisible(state, true);
        addListLink.setVisible(state, true);
        listForm.setVisible(state, false);
        listToFirstLink.setVisible(state, false);
    }

    void showListEditForm(final PageState state) {

        listTable.setVisible(state, false);
        listForm.setVisible(state, true);
        addListLink.setVisible(state, false);
        listToFirstLink.setVisible(state, false);
    }

    void showAttachmentsTable(final PageState state) {
        
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
