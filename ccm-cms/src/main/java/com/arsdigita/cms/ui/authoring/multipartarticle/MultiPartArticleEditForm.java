/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.ui.authoring.multipartarticle;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.parameters.StringParameter;

import org.librecms.contentsection.Folder;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.SelectedLanguageUtil;

import org.librecms.contenttypes.MultiPartArticle;

import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.FolderManager;
import org.librecms.contentsection.FolderRepository;

import java.util.Locale;
import java.util.Optional;

/**
 * Worker class to create the multipart article's edit form.
 *
 */
public class MultiPartArticleEditForm extends MultiPartArticleForm
    implements FormSubmissionListener {

    private final ItemSelectionModel itemSelectionModel;
    private final SimpleEditStep editStep;
    private final StringParameter selectedLanguageParam;

    /**
     * Constructor.
     *
     * @param itemSelectionModel
     * @param editStep
     * @param selectedLanguageParam
     */
    public MultiPartArticleEditForm(final ItemSelectionModel itemSelectionModel,
                                    final SimpleEditStep editStep,
                                    final StringParameter selectedLanguageParam) {

        super("MultiPartArticleEditForm",
              itemSelectionModel,
              selectedLanguageParam);

        addSubmissionListener(this);
        this.itemSelectionModel = itemSelectionModel;
        this.editStep = editStep;
        this.selectedLanguageParam = selectedLanguageParam;
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {
        super.initBasicWidgets(event);
    }

    /**
     * Cancels streamlined editing.
     *
     * @param event
     */
    @Override
    public void submitted(final FormSectionEvent event) {

        if (getSaveCancelSection()
            .getCancelButton()
            .isSelected(event.getPageState())) {
            editStep.cancelStreamlinedCreation(event.getPageState());
        }
    }

    @Override
    public void process(final FormSectionEvent event)
        throws FormProcessException {

        final MultiPartArticle article = super.processBasicWidgets(event);

        final PageState state = event.getPageState();

//        final MultiPartArticle article = (MultiPartArticle) itemSelectionModel
//            .getSelectedObject(state);
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentItemRepository itemRepo = cdiUtil
            .findBean(ContentItemRepository.class);
        itemRepo.save(article);

        editStep.maybeForwardToNextStep(event.getPageState());
    }

    @Override
    public void validate(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();
        final FormData data = event.getFormData();

        final MultiPartArticle article = (MultiPartArticle) itemSelectionModel
            .getSelectedObject(state);

        final Locale selectedLocale = SelectedLanguageUtil
            .selectedLocale(state, selectedLanguageParam);

        final String newName = (String) data.get(MultiPartArticleForm.NAME);
        final MultiPartArticleFormController controller = CdiUtil
            .createCdiUtil()
            .findBean(MultiPartArticleFormController.class);
        final String oldName = controller.getName(article, selectedLocale);

        final boolean valid;
        if (newName.equalsIgnoreCase(oldName)) {
            valid = true;
        } else {
            final Optional<Folder> folder = controller.getArticleFolder(article);
            if (folder.isPresent()) {
                valid = validateNameUniqueness(folder.get(), event);
            } else {
                valid = true;
            }
        }

        if (!valid) {
            throw new FormProcessException(
                "An item with name already exists",
                new GlobalizedMessage(
                    "cms.contenttypes.ui.mparticle.an_item_with_name_already_exists",
                    CmsConstants.CMS_BUNDLE));
        }
    }

    private Folder getParentFolder(final MultiPartArticle article) {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final FolderRepository folderRepo = cdiUtil
            .findBean(FolderRepository.class);
        final FolderManager folderManager = cdiUtil
            .findBean(FolderManager.class);

        final ContentItemManager itemManager = cdiUtil
            .findBean(ContentItemManager.class);

        final Optional<Folder> folder = itemManager.getItemFolder(article);

        if (folder.isPresent()) {
            return folder.get();
        } else {
            return null;
        }
    }

}
