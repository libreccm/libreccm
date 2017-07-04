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

import org.librecms.contenttypes.News;

import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.CmsConstants;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * Authoring step to edit the simple attributes of the News content type (and
 * its subclasses). The attributes edited are {@code name}, {@code title},
 * {@code lead} and {@code item date}. This authoring step replaces the
 * {@link com.arsdigita.ui.authoring.PageEdit} step for this type.
 *
 * @see com.arsdigita.cms.contenttypes.NewsItem
 *
 */
public class NewsPropertiesStep extends SimpleEditStep {

    /**
     * The name of the editing sheet added to this step
     */
    public static String EDIT_SHEET_NAME = "edit";

    public NewsPropertiesStep(final ItemSelectionModel itemModel,
                              final AuthoringKitWizard parent,
                              final StringParameter selectedLanguageParam) {

        super(itemModel, parent, selectedLanguageParam);
        
        Objects.requireNonNull(selectedLanguageParam); 

        setDefaultEditKey(EDIT_SHEET_NAME);
        BasicPageForm editSheet;

        editSheet = new NewsPropertyForm(itemModel, this, selectedLanguageParam);
        add(EDIT_SHEET_NAME,
            new GlobalizedMessage("cms.ui.edit", CmsConstants.CMS_BUNDLE),
            new WorkflowLockedComponentAccess(editSheet, itemModel),
            editSheet.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getNewsDomainObjectPropertySheet(
            itemModel,
            selectedLanguageParam));
    }

    /**
     * Returns a component that displays the properties of the NewsItem
     * specified by the ItemSelectionModel passed in.
     *
     * @param itemModel The ItemSelectionModel to use
     * @param selectedLanguageParam
     *
     * @pre itemModel != null
     * @return A component to display the state of the basic properties of the
     *         item
     *
     */
    public static Component getNewsDomainObjectPropertySheet(
        final ItemSelectionModel itemModel,
        final StringParameter selectedLanguageParam) {

        final DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
            itemModel,
            false,
            selectedLanguageParam);

        sheet.add(new GlobalizedMessage("cms.contenttypes.ui.title",
                                        CmsConstants.CMS_BUNDLE),
                  "title");
        sheet.add(new GlobalizedMessage("cms.contenttypes.ui.name",
                                        CmsConstants.CMS_BUNDLE),
                  "name");
        sheet.add(new GlobalizedMessage("cms.contenttypes.ui.newsitem.lead",
                                        CmsConstants.CMS_BUNDLE),
                  "description");

        // Show news item on homepage?
        sheet.add(new GlobalizedMessage(
            "cms.contenttypes.ui.newsitem.date",
            CmsConstants.CMS_BUNDLE),
                  "releaseDate",
                  new NewsItemDateAttributeFormatter());

        return sheet;
    }

    /**
     * Private class which implements an AttributeFormatter interface for
     * NewsItem's date values. Its format(...) class returns a string
     * representation for either a false or a true value.
     */
    private static class NewsItemDateAttributeFormatter
        implements DomainObjectPropertySheet.AttributeFormatter {

        /**
         * Constructor, does nothing.
         */
        public NewsItemDateAttributeFormatter() {
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

            if (obj != null && obj instanceof News) {

                final News newsItem = (News) obj;
                final BeanInfo beanInfo;
                try {
                    beanInfo = Introspector.getBeanInfo(obj.getClass());
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
                        .getDateInstance(
                            DateFormat.LONG,
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
