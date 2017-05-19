/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.toolbox.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.PropertySheetModel;
import com.arsdigita.bebop.PropertySheetModelBuilder;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.toolbox.ToolboxConstants;
import com.arsdigita.ui.CcmObjectSelectionModel;
import com.arsdigita.util.LockableImpl;

import org.libreccm.core.CcmObject;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.l10n.LocalizedString;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.StringTokenizer;

/**
 * Displays a list of label-value pairs, which represent the attributes of a
 * domain object.
 *
 * Typical usage is
 * <blockquote><pre><code>
 * DomainObjectPropertySheet mySheet =
 *         new DomainObjectPropertySheet(myDomainObjectSelectionModel);
 * mySheet.add("Name:", ContentPage.NAME);
 * mySheet.add("Title:", ContentPage.TITLE);
 * </code></pre></blockquote>
 *
 * The first argument is the visible label for the property, and the second
 * argument is the name of the property as it appears in the PDL file.
 *
 * Instead of specifying the property directly, you may specify the "path" to
 * the property. For example,
 * <blockquote><pre><code>
 * mySheet.add("Address Line 1:", "user.address.street");
 * </code></pre></blockquote>
 *
 * The code above tells the <code>DomainObjectPropertySheet</code> to look for
 * the child of the current object named "user"; then look for the child of the
 * user named "address", and finally to return the property of the address named
 * "street".
 *
 * Note that, by default, <code>DomainObjectPropertySheet</code> retrieves the
 * values for its properties directly from the underlying {@link DataObject} of
 * the {@link DomainObject}. This means that the Java <code>getXXX</code>
 * methods of the <code>DomainObject</code> will never be called. Of course, it
 * is always possible to create a custom {@link AttributeFormatter} that will
 * call the appropriate methods.
 *
 * @author Stanislav Freidin
 * @author Peter Boy (localization)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class DomainObjectPropertySheet extends PropertySheet {

    private List<Property> properties;
    private CcmObjectSelectionModel<?> objectSelectionModel;
    private AttributeFormatter toStringFormatter;
    private AttributeFormatter recursiveFormatter;

    private StringParameter selectedLanguageParam;

    /**
     * Construct a new DomainObjectPropertySheet
     *
     * @param objectSelectionModel The selection model which feeds domain
     *                             objects to this property sheet.
     */
    public DomainObjectPropertySheet(
        final CcmObjectSelectionModel<?> objectSelectionModel) {

        this(objectSelectionModel, false);
    }

    /**
     * Construct a new DomainObjectPropertySheet
     *
     * @param objectSelectionModel The selection model which feeds domain
     *                             objects to this property sheet
     * @param valueOutputEscape    The value of the label-value pair(i.e.,
     *                             column[1])'s output-escaping
     */
    public DomainObjectPropertySheet(
        final CcmObjectSelectionModel<?> objectSelectionModel,
        final boolean valueOutputEscape) {

        this(objectSelectionModel, valueOutputEscape, null);
    }

    public DomainObjectPropertySheet(
        final CcmObjectSelectionModel<?> objectSelectionModel,
        final boolean valueOutputEscape,
        final StringParameter selectedLanguageParam) {

        super(new DomainObjectModelBuilder(), valueOutputEscape);

        this.objectSelectionModel = objectSelectionModel;
        properties = new LinkedList<>();
        this.selectedLanguageParam = selectedLanguageParam;

        toStringFormatter = new SimpleAttributeFormatter();
        recursiveFormatter = new RecursiveAttributeFormatter();

        getColumn(0).setVAlign("top");
        getColumn(0).setAlign("left");
        getColumn(1).setVAlign("top");
        getColumn(1).setAlign("left");
    }

    /**
     * Add a new property to the sheet. The sheet will automatically retrieve an
     * attribute of the object and call toString() on it
     *
     * @param label     The label for the attribute
     * @param attribute The name for the attribute. Could be a simple name or a
     *                  compound path, such as "foo.bar.baz" (usually a PDL
     *                  property)
     *
     * @deprecated use add(GlobalizedMessage label, String attribute) instead
     */
    public void add(final String label, String attribute) {
        add(new GlobalizedMessage(label,
                                  ToolboxConstants.TOOLBOX_BUNDLE),
            attribute);
    }

    /**
     * Add a new property to the sheet. The sheet will automatically retrieve an
     * attribute of the object and call toString() on it
     *
     * @param label     The label for the attribute
     * @param attribute The name for the attribute. Could be a simple name or a
     *                  compound path, such as "foo.bar.baz" (usually a PDL
     *                  property)
     */
    public void add(final GlobalizedMessage label,
                    final String attribute) {
        // Determine if we are dealing with a simple string or a complex
        // path
        if (attribute.indexOf('.') == -1) {
            add(label, attribute, toStringFormatter);
        } else {
            add(label, attribute, recursiveFormatter);
        }
    }

    /**
     * Add a new property to the sheet. The sheet will use an AttributeFormatter
     * to convert the value of the attribute to a String.
     *
     * @param label     The label for the attribute
     * @param attribute The name for the attribute. Could be a simple name or a
     *                  compound path, such as "foo.bar.baz" (usually a PDL
     *                  property)
     * @param formatter An instance of AttributeFormatter
     *
     * @deprecated Use add(GlobalizedMessage label, String attribute,
     * AttributeFormatter f) instead
     */
    public void add(final String label,
                    final String attribute,
                    final AttributeFormatter formatter) {
        add(new GlobalizedMessage(label, ToolboxConstants.TOOLBOX_BUNDLE),
            attribute,
            formatter);
    }

    /**
     * Add a new property to the sheet. The sheet will use an AttributeFormatter
     * to convert the value of the attribute to a String.
     *
     * @param label     The label for the attribute
     * @param attribute The name for the attribute. Could be a simple name or a
     *                  compound path, such as "foo.bar.baz" (usually a PDL
     *                  property)
     * @param formatter An instance of AttributeFormatter
     */
    public void add(final GlobalizedMessage label,
                    final String attribute,
                    final AttributeFormatter formatter) {
        properties.add(new Property(label, attribute, formatter));
    }

    /**
     * @return The object selection model
     */
    public CcmObjectSelectionModel<?> getObjectSelectionModel() {
        return objectSelectionModel;
    }

    /**
     * @return The iterator over all properties
     */
    protected Iterator<Property> properties() {
        return properties.iterator();
    }

    /**
     * An interface which can transform the value of a (domain) property to a
     * string.
     *
     * Most of the time, classes which implement this interface will just return
     * <code>object.get(attribute).toString()</code>
     * <p>
     * In case of associations, however, more complicated processing will be
     * required.
     */
    public interface AttributeFormatter {

        /**
         * Formatter for the value of an attribute. It has to retrieve the value
         * for the specified attribute of the object and format it as an string
         * if it is one already.
         *
         * Note: the format method has to be executed at each page request. Take
         * care to properly adjust globalization and localization inside thes
         * method and not earlier in one of the classes using it!
         *
         * @param obj       Object containing the attribute to format.
         * @param attribute Name of the attribute to retrieve and format
         * @param state     PageState of the request
         *
         * @return A String representation of the retrieved attribute of the
         *         domain object.
         */
        String format(Object obj, String attribute, PageState state);

    }

    /**
     * Associates a label with the attribute and the formatter.
     */
    protected static class Property {

        private GlobalizedMessage label;
        private String attribute;
        private AttributeFormatter formatter;

        /**
         * Constructor, takes the set of parameter to create a new Property.
         *
         * @param label     the labal for the attribute
         * @param attribute the attribute (as String, i.e name of the property)
         * @param formatter the formatter to convert the attribute a into a
         *                  String
         */
        public Property(final GlobalizedMessage label,
                        final String attribute,
                        final AttributeFormatter formatter) {
            this.label = label;
            this.attribute = attribute;
            this.formatter = formatter;
        }

        /**
         * @deprecated use getGlobalizedLabel instead
         */
        public String getLabel() {
            return label.getKey();
        }

        /**
         * Fetch the (globalises) label of the property.
         *
         * @return
         */
        public GlobalizedMessage getGlobalizedLabel() {
            return label;
        }

        /**
         * Fetch the attribute.
         *
         * @return name of the attribute (a String)
         */
        public String getAttribute() {
            return attribute;
        }

        /**
         * Fetch the formatter for the attribute
         *
         * @return
         */
        public AttributeFormatter getFormatter() {
            return formatter;
        }

    }

    /**
     * Build up the object properties model from the iterator over all
     * properties.
     */
    private static class DomainObjectPropertiesModel
        implements PropertySheetModel {

        private CcmObject object;
        private PageState pageState;
        private Iterator<Property> properties;
        private Property currentProperty;
        private static String ERROR = "No current property. "
                                          + "Make sure that nextRow() was "
                                          + "called at least once.";

        /**
         *
         * @param object
         * @param properties
         * @param pageState
         */
        public DomainObjectPropertiesModel(final CcmObject object,
                                           Iterator<Property> properties,
                                           final PageState pageState) {
            this.object = object;
            this.properties = properties;
            this.pageState = pageState;
            this.currentProperty = null;
        }

        @Override
        public boolean nextRow() {
            if (!properties.hasNext()) {
                return false;
            }

            currentProperty = properties.next();
            return true;
        }

        /**
         * @deprecated use getGlobalizedLabel() instead
         */
        @Override
        public String getLabel() {
            return getGlobalizedLabel().getKey();
        }

        /**
         *
         * @return
         */
        @Override
        public GlobalizedMessage getGlobalizedLabel() {
            if (currentProperty == null) {
                throw new IllegalStateException(ERROR);
            }
            return currentProperty.getGlobalizedLabel();
        }

        @Override
        public String getValue() {
            if (currentProperty == null) {
                throw new IllegalStateException(ERROR);
            }
            return currentProperty
                .getFormatter()
                .format(object, currentProperty.getAttribute(), pageState);
        }

    }

    /**
     * Builds an DomainObjectPropertiesModel.
     */
    private static class DomainObjectModelBuilder
        extends LockableImpl
        implements PropertySheetModelBuilder {

        @Override
        public PropertySheetModel makeModel(
            final PropertySheet sheet,
            final PageState state) {

            final DomainObjectPropertySheet objSheet
                                                = (DomainObjectPropertySheet) sheet;
            return new DomainObjectPropertiesModel(
                objSheet.getObjectSelectionModel().getSelectedObject(state),
                objSheet.properties(),
                state);
        }

    }

    /**
     * Abstract AttributeFormatter class which maintains a "default" value for
     * the attribute. The default value is a GlobalizedMessage, which will be
     * formatted to a String by the default format method.
     */
    private static abstract class DefaultAttributeFormatter
        implements AttributeFormatter {

        private GlobalizedMessage m_default;

        /**
         * Default Constructor which creates a default GlobalizedMessage to be
         * used as default value for an attribute.
         */
        public DefaultAttributeFormatter() {
            m_default = new GlobalizedMessage(
                "toolbox.ui.na", ToolboxConstants.TOOLBOX_BUNDLE);
        }

        /**
         * Constructor which takes a custom GlobalizedMessage to be used as a
         * default value.
         *
         * @param def GlobalizedMessage used as default value
         */
        public DefaultAttributeFormatter(final GlobalizedMessage def) {
            m_default = def;
        }

        public GlobalizedMessage getDefaultValue() {
            return m_default;
        }

    }

    /**
     * A simple attribute formatter that calls get on the object with the
     * specified attribute.
     */
    private class SimpleAttributeFormatter
        extends DefaultAttributeFormatter {

        /**
         * Constructor, simply calls the super class. Uses a default value for
         * empty attributes.
         */
        public SimpleAttributeFormatter() {
            super();
        }

        /**
         * Constructor which takes a custom GlobalizedMessage to be used as a
         * default value.
         *
         * @param def GlobalizedMessage used as default value
         */
        public SimpleAttributeFormatter(GlobalizedMessage def) {
            super(def);
        }

        /**
         * Formatter method, invoked at every page request!
         *
         * @param obj
         * @param attribute
         * @param state
         *
         * @return
         */
        @Override
        public String format(final Object obj,
                             final String attribute,
                             final PageState state) {

            /* Determine the default value                                    */
            GlobalizedMessage defaultMsg = getDefaultValue();

            if (obj == null) {
                return (String) defaultMsg.localize();
            }

            final Optional<Object> value = getPropertyValue(obj, 
                                                            attribute, 
                                                            state);

            if (value.isPresent()) {
                return value.get().toString();
            } else {
                return (String) defaultMsg.localize();
            }
        }

    }

    /**
     * A more advanced attribute formatter. Follows the path to the value by
     * following the names in the attribute string. For example, if the string
     * says "foo.bar.baz", the formatter will attempt to call
     * obj.get("foo").get("bar").get("baz");
     */
    private class RecursiveAttributeFormatter extends DefaultAttributeFormatter {

        /**
         * Constructor, simply calls the super class. Uses a default value for
         * empty attributes.
         */
        public RecursiveAttributeFormatter() {
            super();
        }

        /**
         * Constructor which takes a custom GlobalizedMessage to be used as a
         * default value.
         *
         * @param def GlobalizedMessage used as default value
         */
        public RecursiveAttributeFormatter(GlobalizedMessage def) {
            super(def);
        }

        /**
         * Formatter method, invoked at every page request!
         *
         * @param obj
         * @param attribute
         * @param state
         *
         * @return
         */
        @Override
        public String format(final Object obj,
                             final String attribute,
                             final PageState state) {

            if (obj == null) {
                return (String) getDefaultValue().localize();
            }

            final StringTokenizer tokenizer
                                      = new StringTokenizer(attribute, ".");
            String token = null;
            Object currentObject = obj;

            while (tokenizer.hasMoreTokens()) {
                token = tokenizer.nextToken();
                // Null check
                currentObject = getPropertyValue(currentObject, token, state);
                if (currentObject == null) {
                    return (String) getDefaultValue().localize();
                }
            }

            // Extract leaf value
            if (token == null) {
                return (String) getDefaultValue().localize();
            }

            return currentObject.toString();
        }

    }

    private Optional<Object> getPropertyValue(final Object obj,
                                              final String property,
                                              final PageState state) {

        final BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(obj.getClass());
        } catch (IntrospectionException ex) {
            throw new UnexpectedErrorException(ex);
        }

        final Optional<PropertyDescriptor> propertyDescriptor = Arrays
            .stream(beanInfo.getPropertyDescriptors())
            .filter(current -> property.equals(current.getName()))
            .findAny();

        if (propertyDescriptor.isPresent()) {

            final Method readMethod = propertyDescriptor
                .get()
                .getReadMethod();

            final Object value;
            try {
                final Object tmp = readMethod.invoke(obj);
                if (tmp instanceof LocalizedString) {
                    final LocalizedString localizedString
                                          = (LocalizedString) tmp;
                    final Locale selectedLocale = new Locale(
                        (String) state.getValue(selectedLanguageParam));
                    final Locale defaultLocale = KernelConfig
                        .getConfig()
                        .getDefaultLocale();
                    if (localizedString.hasValue(selectedLocale)) {
                        value = localizedString.getValue(selectedLocale);
                    } else {
                        value = "";
                    }
                } else {
                    value = tmp;
                }
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new UnexpectedErrorException(ex);
            }
            return Optional.of(value);

        } else {
            return Optional.empty();
        }
    }

}
