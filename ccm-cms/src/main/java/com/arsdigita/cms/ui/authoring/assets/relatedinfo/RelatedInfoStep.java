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
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.ResettableContainer;
import com.arsdigita.cms.ui.authoring.assets.AttachmentListSelectionModel;
import com.arsdigita.cms.ui.authoring.assets.AttachmentSelectionModel;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.AttachmentList;
import org.librecms.contentsection.ItemAttachment;
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
    private final AuthoringKitWizard authoringKitWizard;
    private final StringParameter selectedLanguageParam;

    private final AttachmentListSelectionModel selectedListModel;
    private final AttachmentListSelectionModel moveListModel;

    private final RelatedInfoListTable listTable;
    private final RelatedInfoListForm listForm;
    private final ActionLink addListLink;
    private final ActionLink listToFirstLink;

    private final AttachmentSelectionModel selectedAttachmentModel;
    private final AttachmentSelectionModel moveAttachmentModel;

    private final AttachmentsTable attachmentsTable;
    private final RelatedInfoAttachAssetForm attachAssetForm;
    private final ActionLink attachAssetLink;
    private final ActionLink attachmentToFirstLink;

    public RelatedInfoStep(final ItemSelectionModel itemSelectionModel,
                           final AuthoringKitWizard authoringKitWizard,
                           final StringParameter selectedLanguage) {

        super();

        this.itemSelectionModel = itemSelectionModel;
        this.authoringKitWizard = authoringKitWizard;
        this.selectedLanguageParam = selectedLanguage;

        selectedListModel = new AttachmentListSelectionModel(
            "selected-attachment-list");
        moveListModel = new AttachmentListSelectionModel(
            "move-attachment-list-model");

        listTable = new RelatedInfoListTable(this,
                                             itemSelectionModel,
                                             selectedListModel, moveListModel,
                                             selectedLanguageParam);
        listForm = new RelatedInfoListForm(this,
                                           itemSelectionModel,
                                           selectedListModel,
                                           selectedLanguageParam);

        addListLink = new ActionLink(new Label(new GlobalizedMessage(
            "cms.ui.authoring.assets.related_info_step.add_list",
            CmsConstants.CMS_BUNDLE)));
        addListLink.addActionListener(event -> {
            showListEditForm(event.getPageState());
        });

        listToFirstLink = new ActionLink(new GlobalizedMessage(
            "cms.ui.authoring.assets.related_info_step.attachment_list"
                + ".move_to_beginning",
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

        selectedAttachmentModel = new AttachmentSelectionModel(
            "selected-attachment-model");
        moveAttachmentModel = new AttachmentSelectionModel(
            "move-attachment-model");

        attachmentsTable = new AttachmentsTable(this, itemSelectionModel,
                                                selectedListModel,
                                                selectedAttachmentModel,
                                                moveAttachmentModel,
                                                selectedLanguageParam);
        attachAssetForm = new RelatedInfoAttachAssetForm(this,
                                                         itemSelectionModel,
                                                         selectedListModel,
                                                         selectedLanguageParam);

        attachAssetLink = new ActionLink(new GlobalizedMessage(
            "cms.ui.authoring.assets.related_info_step.attach_asset",
            CmsConstants.CMS_BUNDLE));
        attachAssetLink.addActionListener(event -> {
            showAttachAssetForm(event.getPageState());
        });

        attachmentToFirstLink = new ActionLink(new GlobalizedMessage(
            "cms.ui.authoring.assets.related_info_step.attachment.move_to_first",
            CmsConstants.CMS_BUNDLE));
        attachmentToFirstLink.addActionListener(event -> {
            final PageState state = event.getPageState();
            final ItemAttachment<?> toMove = moveAttachmentModel
                .getSelectedAttachment(state);

            final RelatedInfoStepController controller = CdiUtil
                .createCdiUtil()
                .findBean(RelatedInfoStepController.class);

            controller.moveToFirst(selectedListModel
                .getSelectedAttachmentList(state), toMove);

            moveAttachmentModel.clearSelection(state);
        });

        moveAttachmentModel.addChangeListener(event -> {

            final PageState state = event.getPageState();

            if (moveAttachmentModel.getSelectedKey(state) == null) {
                attachAssetLink.setVisible(state, true);
                attachmentToFirstLink.setVisible(state, false);
            } else {
                attachAssetLink.setVisible(state, false);
                attachmentToFirstLink.setVisible(state, true);
            }
        });

        final BoxPanel panel = new BoxPanel(BoxPanel.VERTICAL);

        panel.add(addListLink);
        panel.add(listToFirstLink);
        panel.add(listTable);
        panel.add(listForm);

        panel.add(attachAssetLink);
        panel.add(attachmentToFirstLink);
        panel.add(attachmentsTable);
        panel.add(attachAssetForm);

        super.add(panel);
    }

    @Override
    public void register(final Page page) {

        super.register(page);

        page.addComponentStateParam(this, selectedListModel.getStateParameter());
        page.addComponentStateParam(this, moveListModel.getStateParameter());
        page.addComponentStateParam(this,
                                    selectedAttachmentModel.getStateParameter());
        page.addComponentStateParam(this,
                                    moveAttachmentModel.getStateParameter());

        page.setVisibleDefault(listTable, true);
        page.setVisibleDefault(listForm, false);
        page.setVisibleDefault(addListLink, true);
        page.setVisibleDefault(listToFirstLink, false);
        page.setVisibleDefault(attachmentsTable, false);
        page.setVisibleDefault(attachAssetForm, false);
        page.setVisibleDefault(attachAssetLink, false);
        page.setVisibleDefault(attachmentToFirstLink, false);
    }

    protected void showAttachmentListTable(final PageState state) {

        listTable.setVisible(state, true);
        addListLink.setVisible(state, true);
        listForm.setVisible(state, false);
        listToFirstLink.setVisible(state, false);
        attachmentsTable.setVisible(state, false);
        attachAssetForm.setVisible(state, false);
        attachAssetLink.setVisible(state, false);
        attachmentToFirstLink.setVisible(state, false);
    }

    void showListEditForm(final PageState state) {

        listTable.setVisible(state, false);
        listForm.setVisible(state, true);
        addListLink.setVisible(state, false);
        listToFirstLink.setVisible(state, false);
        attachmentsTable.setVisible(state, false);
        attachAssetForm.setVisible(state, false);
        attachAssetLink.setVisible(state, false);
        attachmentToFirstLink.setVisible(state, false);
    }

    void showAttachmentsTable(final PageState state) {

        listTable.setVisible(state, false);
        listForm.setVisible(state, false);
        addListLink.setVisible(state, false);
        listToFirstLink.setVisible(state, false);
        attachmentsTable.setVisible(state, true);
        attachAssetForm.setVisible(state, false);
        attachAssetLink.setVisible(state, true);
        attachmentToFirstLink.setVisible(state, false);
    }

    void showAttachAssetForm(final PageState state) {
        listTable.setVisible(state, false);
        listForm.setVisible(state, false);
        addListLink.setVisible(state, false);
        listToFirstLink.setVisible(state, false);
        attachmentsTable.setVisible(state, false);
        attachAssetForm.setVisible(state, true);
        attachAssetLink.setVisible(state, false);
        attachmentToFirstLink.setVisible(state, false);
    }

}
