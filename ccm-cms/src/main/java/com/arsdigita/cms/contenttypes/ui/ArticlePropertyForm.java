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
import com.arsdigita.bebop.PageState;
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

import java.util.Locale;
import java.util.Objects;

/**
 * Form to edit the basic properties of an article. This form can be extended to
 * create forms for Article subclasses.
 */
public class ArticlePropertyForm extends GenericArticlePropertyForm
    implements FormProcessListener, FormInitListener, FormSubmissionListener {

    public static final String LEAD = "lead";

    private final ArticlePropertiesStep propertiesStep;
    private final StringParameter selectedLanguageParam;

    /**
     * Creates a new form to edit the Article object specified by the item
     * selection model passed in.
     *
     * @param itemModel The ItemSelectionModel to use to obtain the Article to
     *                  work on
     * @param selectedLanguageParam
     */
    public ArticlePropertyForm(final ItemSelectionModel itemModel,
                               final StringParameter selectedLanguageParam) {
        this(itemModel, null, selectedLanguageParam);
    }

    /**
     * Creates a new form to edit the Article object specified by the item
     * selection model passed in.
     *
     * @param itemModel      The ItemSelectionModel to use to obtain the Article
     *                       to work on
     * @param propertiesStep The ArticlePropertiesStep which controls this form.
     * @param selectedLanguageParam
     */
    public ArticlePropertyForm(
        final ItemSelectionModel itemModel,
        final ArticlePropertiesStep propertiesStep,
        final StringParameter selectedLanguageParam) {
        
        super(itemModel, propertiesStep, selectedLanguageParam);
        
        Objects.requireNonNull(selectedLanguageParam);
        
        this.propertiesStep = propertiesStep;
        this.selectedLanguageParam = selectedLanguageParam;
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
        final FormData data = event.getFormData();
        final PageState state = event.getPageState();
        final Article article = (Article) super.initBasicWidgets(event);

        final String selectedLanguage = (String) state
            .getValue(selectedLanguageParam);
        final Locale selectedLocale;
        if (selectedLanguage == null) {
            selectedLocale = KernelConfig.getConfig().getDefaultLocale();
        } else {
            selectedLocale = new Locale(selectedLanguage);
        }

        data.put(LEAD, article.getDescription().getValue(selectedLocale));
    }

    /**
     * Cancels streamlined editing.
     */
    @Override
    public void submitted(final FormSectionEvent event) {
        if (propertiesStep != null && getSaveCancelSection().getCancelButton()
            .isSelected(event.getPageState())) {
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
        final FormData data = event.getFormData();
        final PageState state = event.getPageState();

        final Article article = (Article) super.processBasicWidgets(event);

        // save only if save button was pressed
        if (article != null
                && getSaveCancelSection().getSaveButton()
                .isSelected(event.getPageState())) {

            final String selectedLanguage = (String) state
                .getValue(selectedLanguageParam);
            final Locale selectedLocale;
            if (selectedLanguage == null) {
                selectedLocale = KernelConfig.getConfig().getDefaultLocale();
            } else {
                selectedLocale = new Locale(selectedLanguage);
            }

            article
                .getDescription()
                .addValue(selectedLocale, (String) data.get(LEAD));

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
