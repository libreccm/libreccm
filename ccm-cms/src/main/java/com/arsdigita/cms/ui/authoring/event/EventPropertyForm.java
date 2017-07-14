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
package com.arsdigita.cms.ui.authoring.event;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.Time;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.TimeParameter;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.cms.ItemSelectionModel;

import org.librecms.contenttypes.Event;

import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SelectedLanguageUtil;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contenttypes.EventConfig;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

/**
 * Form to edit the basic properties of an {@link Event} object.
 *
 * Used by {@link EventPropertiesStep} authoring kit step.
 *
 * This form can be extended to create forms for Event subclasses.
 *
 */
public class EventPropertyForm
    extends BasicPageForm
    implements FormProcessListener,
               FormInitListener,
               FormSubmissionListener {

    private final static Logger LOGGER = LogManager
        .getLogger(EventPropertyForm.class);

    /**
     * Name of this form
     */
    public static final String ID = "event_edit";

    private EventPropertiesStep eventPropertiesStep;
    /**
     * event date parameter name
     */
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    public static final String EVENT_DATE = "eventDate";
    /**
     * location parameter name
     */
    public static final String LOCATION = "location";
    /**
     * lead parameter name
     */
    public static final String LEAD = "lead";
    /**
     * Main contributor parameter name
     */
    public static final String MAIN_CONTRIBUTOR = "main_contributor";
    /**
     * Event type parameter name
     */
    public static final String EVENT_TYPE = "event_type";
    /**
     * Map link parameter name
     */
    public static final String MAP_LINK = "map_link";
    /**
     * cost parameter name
     */
    public static final String COST = "cost";

    private final StringParameter selectedLanguageParam;

    /* DateWidgets have to be accessible later on */
    private com.arsdigita.bebop.form.Date startDateField;
    private com.arsdigita.bebop.form.Date endDateField;

    /**
     * Creates a new form to edit the Event object specified by the item
     * selection model passed in.
     *
     * @param itemSelectionModel    The ItemSelectionModel to use to obtain the
     *                              Event to work on
     * @param selectedLanguageParam
     *
     */
    public EventPropertyForm(final ItemSelectionModel itemSelectionModel,
                             final StringParameter selectedLanguageParam) {
        this(itemSelectionModel, null, selectedLanguageParam);
    }

    /**
     * Creates a new form to edit the Event object specified by the item
     * selection model passed in.
     *
     * @param itemSelectionModel    The ItemSelectionModel to use to obtain the
     *                              Event to work on
     * @param eventPropertiesStep   The EventPropertiesStep which controls this
     *                              form.
     * @param selectedLanguageParam
     *
     */
    public EventPropertyForm(final ItemSelectionModel itemSelectionModel,
                             final EventPropertiesStep eventPropertiesStep,
                             final StringParameter selectedLanguageParam) {

        super(ID, itemSelectionModel, selectedLanguageParam);

        Objects.requireNonNull(selectedLanguageParam);

        this.eventPropertiesStep = eventPropertiesStep;
        this.selectedLanguageParam = selectedLanguageParam;
        addSubmissionListener(this);
    }

    /**
     * Adds widgets to the form.
     *
     */
    @Override
    protected void addWidgets() {

        super.addWidgets();

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ConfigurationManager confManager = cdiUtil
            .findBean(ConfigurationManager.class);
        final EventConfig eventConfig = confManager
            .findConfiguration(EventConfig.class);

        /* Summary (lead) */
        final ParameterModel leadParam = new StringParameter(LEAD);
        if (!eventConfig.isLeadTextOptional()) {
            leadParam.addParameterListener(new NotNullValidationListener());
        }
        final TextArea lead = new TextArea(leadParam);
        lead.setLabel(new GlobalizedMessage("cms.contenttypes.ui.event.lead",
                                            CmsConstants.CMS_BUNDLE));
        lead.setCols(50);
        lead.setRows(5);
        add(lead);

        /* Start date and time */
        final ParameterModel eventStartDateParam = new DateParameter(START_DATE);
        eventStartDateParam
            .addParameterListener(new NotNullValidationListener());
        // Use bebop date instead of java.util.date
        startDateField = new com.arsdigita.bebop.form.Date(eventStartDateParam);
        startDateField.setLabel(new GlobalizedMessage(
            "cms.contenttypes.ui.event.start_date",
            CmsConstants.CMS_BUNDLE));
        // Set the upper und lower boundary of the year select box
        startDateField
            .setYearRange(
                eventConfig.getStartYear(),
                GregorianCalendar.getInstance().get(Calendar.YEAR)
                    + eventConfig.getEndYearDelta());
        add(startDateField);

        final ParameterModel eventStartTimeParam = new TimeParameter(START_TIME);
        if (!eventConfig.isStartTimeOptional()) {
            eventStartTimeParam.addParameterListener(
                new NotNullValidationListener());
        }
        final Time startTime = new Time(eventStartTimeParam);
        startTime
            .setLabel(
                new GlobalizedMessage("cms.contenttypes.ui.event.start_time",
                                      CmsConstants.CMS_BUNDLE));
        add(startTime);

        /* End date and time */
        final ParameterModel eventEndDateParam = new DateParameter(END_DATE);
        // Use bebop date instead of java.util.date
        endDateField = new com.arsdigita.bebop.form.Date(eventEndDateParam);
        endDateField
            .setLabel(
                new GlobalizedMessage("cms.contenttypes.ui.event.end_date",
                                      CmsConstants.CMS_BUNDLE));
        endDateField
            .setHint(
                new GlobalizedMessage("cms.contenttypes.ui.event.end_date_hint",
                                      CmsConstants.CMS_BUNDLE));
        endDateField
            .setYearRange(eventConfig.getStartYear(),
                          GregorianCalendar.getInstance().get(Calendar.YEAR)
                              + eventConfig.getEndYearDelta());
        add(endDateField);

        final ParameterModel eventEndTimeParam = new TimeParameter(END_TIME);
        final Time endTime = new Time(eventEndTimeParam);
        endTime
            .setLabel(
                new GlobalizedMessage("cms.contenttypes.ui.event.end_time",
                                      CmsConstants.CMS_BUNDLE));
        endTime
            .setHint(
                new GlobalizedMessage("cms.contenttypes.ui.event.end_time_hint",
                                      CmsConstants.CMS_BUNDLE));
        add(endTime);


        /* optional additional / literal date description */
        if (!eventConfig.isHideDateDescription()) {
            final ParameterModel eventDateParam
                                     = new StringParameter(EVENT_DATE);
            if (eventConfig.isUseHtmlDateDescription()) {
                CMSDHTMLEditor eventDate = new CMSDHTMLEditor(eventDateParam);
                eventDate.setLabel(
                    new GlobalizedMessage(
                        "cms.contenttypes.ui.event.date_description",
                        CmsConstants.CMS_BUNDLE));
                eventDate.setHint(new GlobalizedMessage(
                    "cms.contenttypes.ui.event.date_description_hint",
                    CmsConstants.CMS_BUNDLE));
                eventDate.setCols(40);
                eventDate.setRows(8);
                add(eventDate);
            } else {
                eventDateParam.addParameterListener(
                    new StringInRangeValidationListener(0, 100));
                TextArea eventDate = new TextArea(eventDateParam);
                eventDate
                    .setLabel(new GlobalizedMessage(
                        "cms.contenttypes.ui.event.date_description",
                        CmsConstants.CMS_BUNDLE));
                eventDate.setHint(new GlobalizedMessage(
                    "cms.contenttypes.ui.event.date_description_hint",
                    CmsConstants.CMS_BUNDLE));
                eventDate.setCols(50);
                eventDate.setRows(2);
                add(eventDate);
            }
        }


        /* extensive description of location */
        final ParameterModel locationParam = new StringParameter(LOCATION);
        final CMSDHTMLEditor location = new CMSDHTMLEditor(locationParam);
        location
            .setLabel(
                new GlobalizedMessage("cms.contenttypes.ui.event.location",
                                      CmsConstants.CMS_BUNDLE));
        location
            .setHint(new GlobalizedMessage(
                "cms.contenttypes.ui.event.location_hint",
                CmsConstants.CMS_BUNDLE));
        location.setCols(40);
        location.setRows(8);
        add(location);


        /* optional: main contributor */
        if (!eventConfig.isHideMainContributor()) {
            final ParameterModel mainContributorParam
                                     = new StringParameter(MAIN_CONTRIBUTOR);
            final CMSDHTMLEditor mainContributor
                                     = new CMSDHTMLEditor(mainContributorParam);
            mainContributor.setLabel(
                new GlobalizedMessage(
                    "cms.contenttypes.ui.event.main_contributor",
                    CmsConstants.CMS_BUNDLE));
            mainContributor.setHint(
                new GlobalizedMessage(
                    "cms.contenttypes.ui.event.main_contributor_hint",
                    CmsConstants.CMS_BUNDLE));
            mainContributor.setCols(40);
            mainContributor.setRows(10);
            add(mainContributor);
        }


        /* optional: event type */
        if (!eventConfig.isHideEventType()) {
            final ParameterModel eventTypeParam
                                     = new StringParameter(EVENT_TYPE);
            final TextField eventType = new TextField(eventTypeParam);
            eventType.setLabel(
                new GlobalizedMessage(
                    "cms.contenttypes.ui.event.event_type",
                    CmsConstants.CMS_BUNDLE));
            eventType.setHint(
                new GlobalizedMessage(
                    "cms.contenttypes.ui.event.event_type_hint",
                    CmsConstants.CMS_BUNDLE));
            eventType.setSize(30);
            eventType.setMaxLength(30);
            add(eventType);
        }


        /* optional: link to map */
        if (!eventConfig.isHideLinkToMap()) {
            final ParameterModel mapLinkParam = new StringParameter(MAP_LINK);
            final TextArea mapLink = new TextArea(mapLinkParam);
            mapLink.setLabel(new GlobalizedMessage(
                "cms.contenttypes.ui.event.link_to_map",
                CmsConstants.CMS_BUNDLE));
            mapLink.setHint(new GlobalizedMessage(
                "cms.contenttypes.ui.event.link_to_map_hint",
                CmsConstants.CMS_BUNDLE));
            mapLink.setCols(40);
            mapLink.setRows(2);
            add(mapLink);
        }


        /* optional: costs */
        if (!eventConfig.isHideCost()) {
            final ParameterModel costParam = new TrimmedStringParameter(COST);
            final TextField cost = new TextField(costParam);
            cost.setLabel(
                new GlobalizedMessage(
                    "cms.contenttypes.ui.event.cost",
                    CmsConstants.CMS_BUNDLE));
            cost.setHint(new GlobalizedMessage(
                "cms.contenttypes.ui.event.cost_hint",
                CmsConstants.CMS_BUNDLE));
            cost.setSize(30);
            cost.setMaxLength(30);
            add(cost);
        }

    }

    /**
     *
     * @param event
     *
     * @throws FormProcessException
     */
    @Override
    public void validate(final FormSectionEvent event)
        throws FormProcessException {

        super.validate(event);

        final FormData data = event.getFormData();
        java.util.Date startDate = data.getDate(START_DATE);
        java.util.Date endDate = data.getDate(END_DATE);

        if (endDate != null) {
            if (startDate == null || startDate.compareTo(endDate) > 0) {
                throw new FormProcessException(
                    "End date must be after start date",
                    new GlobalizedMessage(
                        "cms.contenttypes.ui.event.end_date_after_start_date",
                        CmsConstants.CMS_BUNDLE)
                );
            }
        }
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
        final PageState state = event.getPageState();
        final Event item = (Event) super.initBasicWidgets(event);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ConfigurationManager confManager = cdiUtil
            .findBean(ConfigurationManager.class);
        final EventConfig eventConfig = confManager
            .findConfiguration(EventConfig.class);

        // Start date should always be set
        final java.util.Date startDate;
        if (item.getStartDate() == null) {
            // new Date is initialised to current time
            startDate = new java.util.Date();
        } else {
            startDate = item.getStartDate();
        }
        startDateField.addYear(startDate);

        // End date can be null
        final java.util.Date endDate;
        if (item.getEndDate() == null) {
            // new Date is initialised to current time
            endDate = new java.util.Date();
        } else {
            endDate = item.getEndDate();
        }
        endDateField.addYear(endDate);

        final Locale selectedLocale = SelectedLanguageUtil
            .selectedLocale(state, selectedLanguageParam);

        data.put(LEAD, item.getDescription().getValue(selectedLocale));
        data.put(START_DATE, startDate);
        data.put(START_TIME, startDate);
        data.put(END_DATE, endDate);
        data.put(END_TIME, endDate);
        if (!eventConfig.isHideDateDescription()) {
            data.put(EVENT_DATE,
                     item.getEventDate().getValue(selectedLocale));
        }
        data.put(LOCATION, item.getLocation().getValue(selectedLocale));
        if (!eventConfig.isHideMainContributor()) {
            data.put(MAIN_CONTRIBUTOR,
                     item.getMainContributor().getValue(selectedLocale));
        }
        if (!eventConfig.isHideEventType()) {
            data.put(EVENT_TYPE,
                     item.getEventType().getValue(selectedLocale));
        }
        if (!eventConfig.isHideLinkToMap()) {
            data.put(MAP_LINK, item.getMapLink());
        }
        if (!eventConfig.isHideCost()) {
            data.put(COST,
                     item.getCost().getValue(selectedLocale));
        }
    }

    /**
     * Cancels streamlined editing.
     *
     * @param event
     */
    @Override
    public void submitted(final FormSectionEvent event) {
        if (eventPropertiesStep != null
                && getSaveCancelSection()
                .getCancelButton()
                .isSelected(event.getPageState())) {
            eventPropertiesStep.cancelStreamlinedCreation(event.getPageState());
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

        final Event item = (Event) super.processBasicWidgets(event);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ConfigurationManager confManager = cdiUtil
            .findBean(ConfigurationManager.class);
        final EventConfig eventConfig = confManager
            .findConfiguration(EventConfig.class);
        final PageState state = event.getPageState();

        // save only if save button was pressed
        if (item != null
                && getSaveCancelSection().getSaveButton().isSelected(state)) {

            final Locale selectedLocale = SelectedLanguageUtil
                .selectedLocale(state, selectedLanguageParam);

            final java.util.Date startDate = (java.util.Date) data
                .get(START_DATE);
            final java.util.Date startTime = (java.util.Date) data
                .get(START_TIME);
            final java.util.Date endDate = (java.util.Date) data.get(END_DATE);
            final java.util.Date endTime = (java.util.Date) data.get(END_TIME);

            final Calendar startDateCal = Calendar.getInstance();
            final Calendar startTimeCal = Calendar.getInstance();
            final Calendar endDateCal = Calendar.getInstance();
            final Calendar endTimeCal = Calendar.getInstance();
            startDateCal.setTime(startDate);
            startTimeCal.setTime(startTime);
            endDateCal.setTime(endDate);
            endTimeCal.setTime(endTime);

            final int startYear = startDateCal.get(Calendar.YEAR);
            final int startMonth = startDateCal.get(Calendar.MONTH);
            final int startDay = startDateCal.get(Calendar.DAY_OF_MONTH);
            final int startHour = startTimeCal.get(Calendar.HOUR_OF_DAY);
            final int startMinute = startTimeCal.get(Calendar.MINUTE);

            final int endYear = endDateCal.get(Calendar.YEAR);
            final int endMonth = endDateCal.get(Calendar.MONTH);
            final int endDay = endDateCal.get(Calendar.DAY_OF_MONTH);
            final int endHour = endTimeCal.get(Calendar.HOUR_OF_DAY);
            final int endMinute = endTimeCal.get(Calendar.MINUTE);

            final Calendar startDateTimeCal = Calendar.getInstance();
            final Calendar endDateTimeCal = Calendar.getInstance();

            startDateTimeCal.set(Calendar.YEAR, startYear);
            startDateTimeCal.set(Calendar.MONTH, startMonth);
            startDateTimeCal.set(Calendar.DAY_OF_MONTH, startDay);
            startDateTimeCal.set(Calendar.HOUR_OF_DAY, startHour);
            startDateTimeCal.set(Calendar.MINUTE, startMinute);

            endDateTimeCal.set(Calendar.YEAR, endYear);
            endDateTimeCal.set(Calendar.MONTH, endMonth);
            endDateTimeCal.set(Calendar.DAY_OF_MONTH, endDay);
            endDateTimeCal.set(Calendar.HOUR_OF_DAY, endHour);
            endDateTimeCal.set(Calendar.MINUTE, endMinute);

            final java.util.Date startDateTime = startDateTimeCal.getTime();
            final java.util.Date endDateTime = endDateTimeCal.getTime();

            item.setStartDate(startDateTime);
            item.setEndDate(endDateTime);
            //date_description
            if (!eventConfig.isHideDateDescription()) {
                item.getEventDate().addValue(selectedLocale,
                                             (String) data.get(EVENT_DATE));
            }

            if (!eventConfig.isHideMainContributor()) {
                item
                    .getMainContributor()
                    .addValue(selectedLocale,
                              (String) data.get(MAIN_CONTRIBUTOR));
            }
            if (!eventConfig.isHideEventType()) {
                item
                    .getEventType()
                    .addValue(selectedLocale,
                              (String) data.get(EVENT_TYPE));
            }
            if (!eventConfig.isHideLinkToMap()) {
                item.setMapLink((String) data.get(MAP_LINK));
            }
            item.getLocation().addValue(selectedLocale,
                                        (String) data.get(LOCATION));
            item.getDescription().addValue(selectedLocale,
                                           (String) data.get(LEAD));
            if (!eventConfig.isHideCost()) {
                item.getCost().addValue(selectedLocale,
                                        (String) data.get(COST));
            }

            final ContentItemRepository itemRepo = cdiUtil
                .findBean(ContentItemRepository.class);

            itemRepo.save(item);
        }
        if (eventPropertiesStep != null) {
            eventPropertiesStep.maybeForwardToNextStep(event.getPageState());
        }
    }

}
