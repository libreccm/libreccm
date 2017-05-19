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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;


import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;

import org.arsdigita.cms.CMSConfig;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contenttypes.Article;

/**
 * Form to edit the basic properties of an article. This form can be extended to
 * create forms for Article subclasses.
 */
public class ArticlePropertyForm extends GenericArticlePropertyForm
    implements FormProcessListener, FormInitListener, FormSubmissionListener {

    public static final String LEAD = "lead";

    private ArticlePropertiesStep propertyStep;

    /**
     * Creates a new form to edit the Article object specified by the item
     * selection model passed in.
     *
     * @param itemModel The ItemSelectionModel to use to obtain the Article to
     *                  work on
     */
    public ArticlePropertyForm(final ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    /**
     * Creates a new form to edit the Article object specified by the item
     * selection model passed in.
     *
     * @param itemModel The ItemSelectionModel to use to obtain the Article to
     *                  work on
     * @param step      The ArticlePropertiesStep which controls this form.
     */
    public ArticlePropertyForm(final ItemSelectionModel itemModel,
                               final ArticlePropertiesStep step) {
        super(itemModel, step);
        propertyStep = step;
        addSubmissionListener(this);
    }

    /**
     * Adds widgets to the form.
     */
    @Override
    protected void addWidgets() {
        super.addWidgets();

        ParameterModel leadParam = new StringParameter(LEAD);
        if (CMSConfig.getConfig().isMandatoryDescriptions()) {
            leadParam.addParameterListener(
                new NotEmptyValidationListener(
                    new GlobalizedMessage(
                        "cms.contenttypes.ui.description_missing",
                        CmsConstants.CMS_BUNDLE)));
        }
        leadParam
            .addParameterListener(new StringInRangeValidationListener(0,
                                                                      1000));
        TextArea lead = new TextArea(leadParam);
        lead.setLabel(new GlobalizedMessage("cms.contenttypes.ui.lead",
                                            CmsConstants.CMS_BUNDLE));
        lead.setHint(new GlobalizedMessage("cms.contenttypes.ui.lead_hint",
                                           CmsConstants.CMS_BUNDLE));
        lead.setCols(40);
        lead.setRows(5);
        add(lead);
    }

    @Override
    public void validate(final FormSectionEvent event) throws
        FormProcessException {
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
        FormData data = event.getFormData();
        final Article article = (Article) super.initBasicWidgets(event);

        data.put(LEAD, article.getDescription());
    }

    /**
     * Cancels streamlined editing.
     */
    @Override
    public void submitted(final FormSectionEvent event) {
        if (propertyStep != null && getSaveCancelSection().getCancelButton()
            .isSelected(event.getPageState())) {
            propertyStep.cancelStreamlinedCreation(event.getPageState());
        }
    }

    /**
     * Form processing hook. Saves Event object.
     *
     * @param event
     */
    @Override
    public void process(final FormSectionEvent event) {
        FormData data = event.getFormData();

        Article article = (Article) super.processBasicWidgets(event);

        // save only if save button was pressed
        if (article != null
                && getSaveCancelSection().getSaveButton()
                .isSelected(event.getPageState())) {

            article.getDescription().addValue(KernelConfig.getConfig()
                .getDefaultLocale(), (String) data.get(LEAD));

            final ContentItemRepository itemRepo = CdiUtil
                .createCdiUtil()
                .findBean(ContentItemRepository.class);
            itemRepo.save(article);
        }
        if (propertyStep != null) {
            propertyStep.maybeForwardToNextStep(event.getPageState());
        }
    }

}
