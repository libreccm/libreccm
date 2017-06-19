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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.StringParameter;

import com.arsdigita.cms.ItemSelectionModel;

import org.librecms.contenttypes.Event;

import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.CmsConstants;
import org.librecms.contenttypes.EventConfig;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Optional;

/**
 * Authoring step to view/edit the simple attributes of the Event content type
 * (and its subclasses).
 *
 * The attributes edited are {@code name}, {@code title}, {@code lead},
 * {@code startdate}, {@code starttime}, {@code end date},
 * {@code endtime},{@code event date} (literal description of date),
 * {@code location}, {@code main contributor} {@code event type},
 * {@code map link}, and {@code cost}.
 *
 * This authoring step replaces the {@code com.arsdigita.ui.authoring.PageEdit}
 * step for this type.
 */
public class EventPropertiesStep extends SimpleEditStep {

    /**
     * The name of the editing sheet added to this step
     */
    public static String EDIT_SHEET_NAME = "edit";

    /**
     *
     * @param itemSelectionModel
     * @param authoringKitWizard
     * @param selectedLanguageParam
     */
    public EventPropertiesStep(final ItemSelectionModel itemSelectionModel,
                               final AuthoringKitWizard authoringKitWizard,
                               final StringParameter selectedLanguageParam) {

        super(itemSelectionModel, authoringKitWizard, selectedLanguageParam);

        setDefaultEditKey(EDIT_SHEET_NAME);
        BasicPageForm editSheet;

        editSheet = new EventPropertyForm(itemSelectionModel, this);
        add(EDIT_SHEET_NAME,
            new GlobalizedMessage("cms.ui.edit", CmsConstants.CMS_BUNDLE),
            new WorkflowLockedComponentAccess(editSheet, itemSelectionModel),
            editSheet.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getEventPropertySheet(itemSelectionModel,
                                                  selectedLanguageParam));
    }

    /**
     * Returns a component that displays the properties of the Event specified
     * by the ItemSelectionModel passed in.
     *
     * @param itemSelectionModel The ItemSelectionModel to use
     *
     * @return A component to display the state of the basic properties of the
     *         release
     *
     */
    public static Component getEventPropertySheet(
        final ItemSelectionModel itemSelectionModel,
        final StringParameter selectedLanguageParam) {

        final DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
            itemSelectionModel,
            false,
            selectedLanguageParam);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ConfigurationManager confManager = cdiUtil
            .findBean(ConfigurationManager.class);
        final EventConfig eventConfig = confManager
            .findConfiguration(EventConfig.class);

        sheet.add(new GlobalizedMessage("cms.contenttypes.ui.title",
                                        CmsConstants.CMS_BUNDLE),
                  "title");
        sheet.add(new GlobalizedMessage("cms.contenttypes.ui.name",
                                        CmsConstants.CMS_BUNDLE),
                  "name");
        sheet.add(new GlobalizedMessage("cms.contenttypes.ui.event.lead",
                                        CmsConstants.CMS_BUNDLE),
                  "lead");

        sheet.add(new GlobalizedMessage("cms.contenttypes.ui.event.start_time",
                                        CmsConstants.CMS_BUNDLE),
                  "startDate",
                  new DateTimeAttributeFormatter());

        sheet.add(new GlobalizedMessage("cms.contenttypes.ui.event.end_time",
                                        CmsConstants.CMS_BUNDLE),
                  "endDate",
                  new DateTimeAttributeFormatter());
        if (!eventConfig.isHideDateDescription()) {
            sheet.add(
                new GlobalizedMessage(
                    "cms.contenttypes.ui.event.date_description",
                    CmsConstants.CMS_BUNDLE),
                "eventDate");
        }

        sheet.add(new GlobalizedMessage("cms.contenttypes.ui.event.location",
                                        CmsConstants.CMS_BUNDLE),
                  "location");

        if (!eventConfig.isHideMainContributor()) {
            sheet.add(
                new GlobalizedMessage(
                    "cms.contenttypes.ui.event.main_contributor",
                    CmsConstants.CMS_BUNDLE),
                "mainContributor");
        }
        if (!eventConfig.isHideEventType()) {
            sheet.add(
                new GlobalizedMessage("cms.contenttypes.ui.event.event_type",
                                      CmsConstants.CMS_BUNDLE),
                "eventType");
        }
        if (!eventConfig.isHideLinkToMap()) {
            sheet.add(
                new GlobalizedMessage("cms.contenttypes.ui.event.link_to_map",
                                      CmsConstants.CMS_BUNDLE),
                "mapLink");
        }
        if (!eventConfig.isHideCost()) {
            sheet.add(new GlobalizedMessage("cms.contenttypes.ui.event.cost",
                                            CmsConstants.CMS_BUNDLE),
                      "cost");
        }
        return sheet;
    }

    /**
     * Private class which implements an AttributeFormatter interface for date
     * values. Its format(...) class returns a string representation for either
     * a false or a true value.
     */
    private static class DateTimeAttributeFormatter
        implements DomainObjectPropertySheet.AttributeFormatter {

        /**
         * Constructor, does nothing.
         */
        public DateTimeAttributeFormatter() {
        }

        /**
         * Formatter for the value of an attribute.
         *
         * It currently relays on the prerequisite that the passed in property
         * attribute is in fact a date property. No type checking yet!
         *
         * Note: the format method has to be executed at each page request. Take
         * care to properly adjust globalization and localization here!
         *
         * @param obj       Object containing the attribute to format.
         * @param attribute Name of the attribute to retrieve and format
         * @param state     PageState of the request
         *
         * @return A String representation of the retrieved boolean attribute of
         *         the domain object.
         */
        public String format(final Object obj,
                             final String attribute,
                             final PageState state) {

            if (obj != null && obj instanceof Event) {

                final Event event = (Event) obj;
                final BeanInfo beanInfo;
                try {
                    beanInfo = Introspector.getBeanInfo(Event.class);
                } catch (IntrospectionException ex) {
                    throw new UnexpectedErrorException(ex);
                }
                final Optional<PropertyDescriptor> propertyDescriptor = Arrays
                    .stream(beanInfo.getPropertyDescriptors())
                    .filter(current -> attribute.equals(current.getName()))
                    .findAny();

                if (propertyDescriptor.isPresent()) {

                    final GlobalizationHelper globalizationHelper = CdiUtil
                        .createCdiUtil().findBean(GlobalizationHelper.class);

                    final Method readMethod = propertyDescriptor
                        .get()
                        .getReadMethod();

                    final Object result;
                    try {
                        result = readMethod.invoke(obj);
                    } catch (IllegalAccessException
                             | IllegalArgumentException
                             | InvocationTargetException ex) {
                        throw new UnexpectedErrorException(ex);
                    }

                    return DateFormat
                        .getDateTimeInstance(
                            DateFormat.LONG,
                            DateFormat.SHORT,
                            globalizationHelper.getNegotiatedLocale())
                        .format(result);

                } else {
                    return (String) new GlobalizedMessage(
                        "cms.ui.unknown",
                        CmsConstants.CMS_BUNDLE)
                        .localize();
                }

            } else {

                return (String) new GlobalizedMessage("cms.ui.unknown",
                                                      CmsConstants.CMS_BUNDLE)
                    .localize();
            }
        }

    }

}
