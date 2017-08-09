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
package com.arsdigita.cms.ui.authoring.assets.images;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.ResettableContainer;
import com.arsdigita.cms.ui.authoring.assets.ItemAttachmentSelectionModel;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ItemAttachment;
import org.librecms.ui.authoring.ContentItemAuthoringStep;

import java.util.Locale;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ContentItemAuthoringStep(
    labelBundle = CmsConstants.CMS_BUNDLE,
    labelKey = "image_step.label",
    descriptionBundle = CmsConstants.CMS_BUNDLE,
    descriptionKey = "image_step.description")
public class ImageStep extends ResettableContainer {

    public static final String IMAGES_ATTACHMENT_LIST = ".images";

    private final LongParameter moveAttachmentParam;
    private final ItemAttachmentSelectionModel moveAttachmentModel;

    private final Label assignedImagesHeader;
    private final ControlLink addImageLink;
    private final ActionLink beginLink;
    private final Table assignedImagesTable;
    private final Label addImageHeader;
    private final AvailableImages availableImages;
    private final ControlLink cancelAddImage;

    public ImageStep(final ItemSelectionModel itemSelectionModel,
                     final AuthoringKitWizard authoringKitWizard,
                     final StringParameter selectedLanguageParam) {

        super();

        moveAttachmentParam = new LongParameter("moveAttachment");
        moveAttachmentModel = new ItemAttachmentSelectionModel(
            moveAttachmentParam);

        assignedImagesHeader = new Label(event -> {
            final PageState state = event.getPageState();
            final Label target = (Label) event.getTarget();

            final ContentItem selectedItem = itemSelectionModel
                .getSelectedItem(state);
            final String selectedLanguage = (String) state
                .getValue(selectedLanguageParam);
            final Locale selectedLocale = new Locale(selectedLanguage);
            final String title;
            if (selectedItem.getTitle().hasValue(selectedLocale)) {
                title = selectedItem.getTitle().getValue(selectedLocale);
            } else {
                title = selectedItem.getTitle().getValue(KernelConfig
                    .getConfig()
                    .getDefaultLocale());
            }

            target.setLabel(new GlobalizedMessage(
                "cms.ui.authoring.assets.imagestep.heading",
                CmsConstants.CMS_BUNDLE,
                new String[]{title}));
        });
        assignedImagesHeader.setClassAttr("");

        addImageHeader = new Label(event -> {
            final PageState state = event.getPageState();
            final Label target = (Label) event.getTarget();

            final ContentItem selectedItem = itemSelectionModel
                .getSelectedItem(state);
            final String selectedLanguage = (String) state
                .getValue(selectedLanguageParam);
            final Locale selectedLocale = new Locale(selectedLanguage);
            final String title;
            if (selectedItem.getTitle().hasValue(selectedLocale)) {
                title = selectedItem.getTitle().getValue(selectedLocale);
            } else {
                title = selectedItem.getTitle().getValue(KernelConfig
                    .getConfig()
                    .getDefaultLocale());
            }

            target.setLabel(new GlobalizedMessage(
                "cms.ui.authoring.assets.imagestep.add_heading",
                CmsConstants.CMS_BUNDLE,
                new String[]{title}));
        });

        addImageLink = new ActionLink(new Label(new GlobalizedMessage(
            "cms.ui.authoring.assets.imagestep.assigned_images.add_image",
            CmsConstants.CMS_BUNDLE)));
        addImageLink.addActionListener(event -> {
            showAvailableImages(event.getPageState());
        });

        beginLink = new ActionLink(new GlobalizedMessage(
            "cms.ui.authoring.assets.imagestep.assigned_images.move_to_beginning",
            CmsConstants.CMS_BUNDLE));

        beginLink.addActionListener(event -> {
            final PageState state = event.getPageState();
            final ItemAttachment<?> toMove = moveAttachmentModel
                .getSelectedAttachment(state);

            final ImageStepController controller = CdiUtil
                .createCdiUtil()
                .findBean(ImageStepController.class);

            controller.moveToFirst(toMove);
            
            moveAttachmentModel.setSelectedKey(state, null);
        });

        assignedImagesTable = new AssignedImagesTable(itemSelectionModel,
                                                      moveAttachmentModel,
                                                      selectedLanguageParam);

        cancelAddImage = new ControlLink(new Label(new GlobalizedMessage(
            "cms.ui.authoring.assets.imagestep.assigned_images.cancel_add_image",
            CmsConstants.CMS_BUNDLE)));
        cancelAddImage.addActionListener(event -> {
            showAssignedImages(event.getPageState());
        });

        availableImages = new AvailableImages(this,
                                              itemSelectionModel,
                                              selectedLanguageParam);

        final BoxPanel panel = new BoxPanel(BoxPanel.VERTICAL);
        panel.add(assignedImagesHeader);
        panel.add(addImageLink);
        panel.add(beginLink);
        panel.add(assignedImagesTable);
        panel.add(addImageHeader);
        panel.add(availableImages);
        super.add(panel);

        moveAttachmentModel.addChangeListener(event -> {

            final PageState state = event.getPageState();

            if (moveAttachmentModel.getSelectedKey(state) == null) {
                addImageLink.setVisible(state, true);
                beginLink.setVisible(state, false);
            } else {
                addImageLink.setVisible(state, false);
                beginLink.setVisible(state, true);
            }
        });
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.setVisibleDefault(assignedImagesHeader, true);
        page.setVisibleDefault(addImageLink, true);
        page.setVisibleDefault(beginLink, false);
        page.setVisibleDefault(assignedImagesTable, true);
        page.setVisibleDefault(addImageHeader, false);
        page.setVisibleDefault(cancelAddImage, false);
        page.setVisibleDefault(availableImages, false);
        
        page.addComponentStateParam(assignedImagesTable, moveAttachmentParam);
    }

    protected void showAssignedImages(final PageState state) {
        assignedImagesHeader.setVisible(state, true);
        addImageLink.setVisible(state, true);
        assignedImagesTable.setVisible(state, true);
        addImageHeader.setVisible(state, false);

        availableImages.setVisible(state, false);
    }

    protected void showAvailableImages(final PageState state) {
        assignedImagesHeader.setVisible(state, false);
        addImageLink.setVisible(state, false);
        assignedImagesTable.setVisible(state, false);
        addImageHeader.setVisible(state, true);
        cancelAddImage.setVisible(state, true);
        availableImages.setVisible(state, true);
    }

}
