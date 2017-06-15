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
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;

import org.librecms.contenttypes.News;

import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contenttypes.NewsConfig;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Form to edit the basic properties of a {@link News} item. These are name,
 * title, item date and reference code. Used by {@link NewsPropertiesStep}
 * authoring kit step.
 *
 * This form can be extended to create forms for NewsItem subclasses.
 *
 */
public class NewsPropertyForm extends BasicPageForm
    implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private NewsPropertiesStep propertiesStep;
    /**
     * lead parameter name
     */
    public static final String LEAD = "lead";
    /**
     * Item date parameter name
     */
    public static final String NEWS_DATE = "news_date";
    public static final String IS_HOMEPAGE = "isHomepage";
    /**
     * Name of this form
     */
    public static final String ID = "news_item_edit";

    private com.arsdigita.bebop.form.Date releaseDateSelector;

    /**
     * Creates a new form to edit the NewsItem object specified by the item
     * selection model passed in.
     *
     * @param itemSelectionModel The ItemSelectionModel to use to obtain the
     *                           NewsItem to work on
     */
    public NewsPropertyForm(final ItemSelectionModel itemSelectionModel) {
        this(itemSelectionModel, null);
    }

    /**
     * Creates a new form to edit the NewsItem object specified by the item
     * selection model passed in.
     *
     * @param itemSelectionModel The ItemSelectionModel to use to obtain the
     *                           NewsItem to work on
     * @param propertiesStep     The NewsPropertiesStep which controls this
                           form.
     */
    public NewsPropertyForm(final ItemSelectionModel itemSelectionModel,
                                final NewsPropertiesStep propertiesStep) {
        super(ID, itemSelectionModel);
        this.propertiesStep = propertiesStep;
        addSubmissionListener(this);
    }

    /**
     * Adds widgets to the form.
     */
    @Override
    protected void addWidgets() {

        super.addWidgets();

        final ParameterModel leadParam = new StringParameter(LEAD);
        final TextArea lead = new TextArea(leadParam);
        lead.setLabel(new GlobalizedMessage(
            "cms.contenttypes.ui.newsitem.lead",
            CmsConstants.CMS_BUNDLE));
        lead.setCols(50);
        lead.setRows(5);
        add(lead);

        final ConfigurationManager confManager = CdiUtil
            .createCdiUtil()
            .findBean(ConfigurationManager.class);
        final NewsConfig newsConfig = confManager
            .findConfiguration(NewsConfig.class);

        final int startYear = newsConfig.getStartYear();
        final int endYearDelta = newsConfig.getEndYearDelta();
        final int currentYear = GregorianCalendar
            .getInstance()
            .get(Calendar.YEAR);
        final int endYear = currentYear + endYearDelta;

        final ParameterModel newsDateParam = new DateParameter(NEWS_DATE);
        newsDateParam.addParameterListener(new NotNullValidationListener());
        releaseDateSelector = new com.arsdigita.bebop.form.Date(newsDateParam);
        releaseDateSelector.setYearRange(startYear, endYear);
        releaseDateSelector.setLabel(new GlobalizedMessage(
            "cms.contenttypes.ui.newsitem.date",
            CmsConstants.CMS_BUNDLE));
        add(releaseDateSelector);
    }

    /**
     * Form initialisation hook. Fills widgets with data.
     *
     * @param event
     */
    @Override
    public void init(final FormSectionEvent event) {
        final FormData data = event.getFormData();
        final News item = (News) super.initBasicWidgets(event);

        // set a default item date, if none set
        java.util.Date releaseDate = item.getReleaseDate();
        if (releaseDate == null) {
            // new Date is initialised to current time
            releaseDate = new java.util.Date();
        }

        releaseDateSelector.addYear(releaseDate);
        data.put(NEWS_DATE, releaseDate);
        data.put(LEAD, item.getDescription());
    }

    /**
     * Cancels streamlined editing.
     *
     * @param event
     */
    @Override
    public void submitted(final FormSectionEvent event) {
        if (propertiesStep != null
                && getSaveCancelSection()
                .getCancelButton()
                .isSelected(event.getPageState())) {
            propertiesStep.cancelStreamlinedCreation(event.getPageState());
        }
    }

    /**
     * Form processing hook. Saves NewsItem object.
     *
     * @param event
     */
    @Override
    public void process(final FormSectionEvent event) {

        FormData data = event.getFormData();

        final News item = (News) super.processBasicWidgets(event);

        // save only if save button was newsed
        if (item != null
                && getSaveCancelSection()
                .getSaveButton()
                .isSelected(event.getPageState())) {

            item.setReleaseDate((java.util.Date) data.get(NEWS_DATE));
            item.getDescription().addValue(
                KernelConfig.getConfig().getDefaultLocale(),
                (String) data.get(LEAD));

            final ContentItemRepository itemRepo = CdiUtil
                .createCdiUtil()
                .findBean(ContentItemRepository.class);

            itemRepo.save(item);
        }
        if (propertiesStep != null) {
            propertiesStep.maybeForwardToNextStep(event.getPageState());
        }
    }

}
