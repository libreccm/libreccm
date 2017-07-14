/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.authoring.article;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;

import org.librecms.contenttypes.Article;

import com.arsdigita.cms.ui.authoring.BasicPageForm;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.ContentItemRepository;

/**
 * Form to edit the basic properties of an article. This form can be extended to
 * create forms for Article subclasses.
 */
public class GenericArticlePropertyForm extends BasicPageForm
    implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private GenericArticlePropertiesStep propertiesStep;

    /**
     * Creates a new form to edit the Article object specified by the item
     * selection model passed in.
     *
     * @param itemModel The ItemSelectionModel to use to obtain the Article to
     *                  work on
     * @param selectedLanguageParam
     */
    public GenericArticlePropertyForm(
        final ItemSelectionModel itemModel,
        final StringParameter selectedLanguageParam) {

        this(itemModel, null, selectedLanguageParam);
    }

    /**
     * Creates a new form to edit the GenericArticle object specified by the
     * item selection model passed in.
     *
     * @param itemModel The ItemSelectionModel to use to obtain the
     *                  GenericArticle to work on
     * @param step      The GenericArticlePropertiesStep which controls this
     *                  form.
     * @param selectedLanguageParam
     */
    public GenericArticlePropertyForm(
        final ItemSelectionModel itemModel,
        final GenericArticlePropertiesStep step,
        final StringParameter selectedLanguageParam) {

        super(ID, itemModel, selectedLanguageParam);
        propertiesStep = step;
        addSubmissionListener(this);
    }

    /**
     * Adds widgets to the form.
     */
    @Override
    protected void addWidgets() {
        super.addWidgets();
    }

    @Override
    public void validate(final FormSectionEvent event)
        throws FormProcessException {

        super.validate(event);
    }

    /**
     * Form initialisation hook. Fills widgets with data.
     *
     * @param event
     */
    @Override
    public void init(final FormSectionEvent event) {
        // Do some initialization hook stuff
        final FormData data = event.getFormData();
        final Article article = (Article) super.initBasicWidgets(event);
    }

    /**
     * Cancels streamlined editing.
     *
     * @param event
     */
    @Override
    public void submitted(final FormSectionEvent event) {
        if (propertiesStep != null
                && getSaveCancelSection().getCancelButton().isSelected(event
                .getPageState())) {
            propertiesStep.cancelStreamlinedCreation(event.getPageState());
        }
    }

    /**
     * Form processing hook. Saves Event object.
     *
     * @param event
     */
    @Override
    public void process(final FormSectionEvent event) {

        final Article article = (Article) super.processBasicWidgets(event);

        // save only if save button was pressed
        if (article != null
                && getSaveCancelSection().getSaveButton().isSelected(event
                .getPageState())) {

            final ContentItemRepository itemRepo = CdiUtil
                .createCdiUtil()
                .findBean(ContentItemRepository.class);

            itemRepo.save(article);
        }
        if (propertiesStep != null) {
            propertiesStep.maybeForwardToNextStep(event.getPageState());
        }
    }

}
