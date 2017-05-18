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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.parameters.StringParameter;

import org.librecms.contentsection.ContentItem;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.ui.SecurityPropertyEditor;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.ComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.List;

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
import java.util.Arrays;
import java.util.Optional;

/**
 * A simple implementation of an Authoring Kit editing step. Extends
 * {@link SecurityPropertyEditor} and provides authoring kit integration. See
 * the authoring kit documentation for more info.
 *
 * Child classes should a). call setDisplayComponent() b). call add() zero or
 * more times
 *
 * @author Stanislav Freidin
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SimpleEditStep extends SecurityPropertyEditor
    implements AuthoringStepComponent, RequestListener {

    private AuthoringKitWizard authoringKitWizard;
    private ItemSelectionModel itemSelectionModel;
    private String defaultEditKey = null;

    private StringParameter streamlinedCreationParameter;
    private static final String STREAMLINED = "_streamlined";
    private static final String STREAMLINED_DONE = "1";

    private static List<AdditionalDisplayComponent> additionalDisplayComponents
                                                        = new ArrayList<>();

    /**
     * allow additional display components to be added to all implementations of
     * SimpleEditStep. This allows shared optional packages such as notes to
     * display information on the initial authoring page of all content types
     * without causing dependencies from ccm-cms.
     *
     * Any additional components must be added before the edit step is created.
     * An initialiser is a suitable location
     *
     * @param additionalDisplayComponent
     */
    public static void addAdditionalDisplayComponent(
        AdditionalDisplayComponent additionalDisplayComponent) {

        additionalDisplayComponents.add(additionalDisplayComponent);
    }

    /**
     * Construct a new SimpleEditStep component
     *
     * @param itemModel The {@link ItemSelectionModel} which will be responsible
     *                  for loading the current item
     *
     * @param parent    The parent wizard which contains the form. The component
     *                  may use the wizard's methods, such as stepForward and
     *                  stepBack, in its process listener.
     */
    public SimpleEditStep(final ItemSelectionModel itemModel,
                          final AuthoringKitWizard parent) {
        this(itemModel, parent, "");
    }

    /**
     * Construct a new SimpleEditStep component
     *
     * @param itemSelectionModel The {@link ItemSelectionModel} which will be
     *                           responsible for loading the current item
     *
     * @param authoringKitWizard The parent wizard which contains the form. The
     *                           component may use the wizard's methods, such as
     *                           stepForward and stepBack, in its process
     *                           listener.
     *
     * @param parameterSuffix    Additional global parameter name suffix if
     *                           there are multiple SimpleEditStep instances in
     *                           an authoring kit.
     */
    public SimpleEditStep(final ItemSelectionModel itemSelectionModel,
                          final AuthoringKitWizard authoringKitWizard,
                          final String parameterSuffix) {

        super();
        this.authoringKitWizard = authoringKitWizard;
        this.itemSelectionModel = itemSelectionModel;

        streamlinedCreationParameter = new StringParameter(
            authoringKitWizard.getContentType().getContentItemClass().getName()
                + "_properties_done" + parameterSuffix);

        authoringKitWizard
            .getList()
            .addActionListener(event -> showDisplayPane(event.getPageState()));

        additionalDisplayComponents
            .stream()
            .forEach(component -> {
                component.setItemSelectionModel(itemSelectionModel);
                addDisplayComponent(component);
            });
    }

    /**
     * Registers global state parameter for cancelling streamlined creation
     *
     * @param page
     */
    @Override
    public void register(final Page page) {

        super.register(page);
        page.addGlobalStateParam(streamlinedCreationParameter);
        page.addRequestListener(this);
    }

    /**
     * @return the parent wizard
     */
    public AuthoringKitWizard getParentWizard() {
        return authoringKitWizard;
    }

    /**
     * @return The item selection model
     */
    public ItemSelectionModel getItemSelectionModel() {
        return itemSelectionModel;
    }

    /**
     * Forward to the next step if the streamlined creation parameter is turned
     * on _and_ the streamlined_creation global state parameter is set to
     * 'active'
     *
     * @param state the PageState
     */
    public void maybeForwardToNextStep(final PageState state) {
        if (ContentItemPage.isStreamlinedCreationActive(state)
                && !STREAMLINED_DONE.equals(state.getValue(
                streamlinedCreationParameter))) {
            state.setValue(streamlinedCreationParameter, STREAMLINED_DONE);
            fireCompletionEvent(state);
        }
    }

    /**
     * Cancel streamlined creation for this step if the streamlined creation
     * parameter is turned on _and_ the streamlined_creation global state param
     * is set to 'active'
     *
     * @param state the PageState
     */
    public void cancelStreamlinedCreation(final PageState state) {

        if (ContentItemPage.isStreamlinedCreationActive(state)) {
            state.setValue(streamlinedCreationParameter, STREAMLINED_DONE);
        }
    }

    public void setDefaultEditKey(final String key) {
        defaultEditKey = key;
    }

    /**
     * Open the edit component if the streamlined creation parameter is turned
     * on _and_ the streamlined_creation global state param is set to 'active'
     *
     * @param e
     */
    @Override
    public void pageRequested(final RequestEvent e) {
        PageState state = e.getPageState();

        // XXX: This method is called on every page request for every authoring
        // step in every authoring kit. This has in the past revealed a caching
        // side-effect bug, but should in the main be harmless. Except of course
        // for performance.
        // Ideally this method would only be called for a single authoring step
        // on each page load. However, at the stage that this is called,
        // visibility has not been set, and getting the selected authoring kit
        // or component is not straightforward, and would almost certainly
        // involve duplicating code.
        // This need to be rethought.
        //if( !state.isVisibleOnPage( this ) ) return;
        if (defaultEditKey != null && itemSelectionModel.getSelectedItem(state)
                                          != null) {

            final ComponentAccess componentAccess = getAccessMap()
                .get(defaultEditKey);

            if (ContentItemPage.isStreamlinedCreationActive(state)
                    && !STREAMLINED_DONE
                    .equals(state.getValue(streamlinedCreationParameter))
                    && componentAccess != null
                    && componentAccess.canAccess(state)) {
                showComponent(state, defaultEditKey);
            }
        }

    }

    /**
     * Public class which implements an AttributeFormatter interface for boolean
     * values. Its format(...) class returns a string representation for either
     * a false or a true value.
     */
    protected static class LaunchDateAttributeFormatter
        implements DomainObjectPropertySheet.AttributeFormatter {

        /**
         * Constructor, does nothing.
         */
        public LaunchDateAttributeFormatter() {
        }

        /**
         * Formatter for the value of a (LaunchDate) attribute.
         *
         * It currently relays on the prerequisite that the passed in property
         * attribute is in fact a date property. No type checking yet!
         *
         * Note: the format method has to be executed at each page request. Take
         * care to properly adjust globalisation and localisation here!
         *
         * @param object    Object containing the attribute to format.
         * @param attribute Name of the attribute to retrieve and format
         * @param state     PageState of the request
         *
         * @return A String representation of the retrieved boolean attribute of
         *         the domain object.
         */
        @Override
        public String format(final Object object,
                             final String attribute,
                             final PageState state) {

            if (object != null && object instanceof ContentItem) {

                final ContentItem page = (ContentItem) object;
                final BeanInfo beanInfo;
                try {
                    beanInfo = Introspector
                        .getBeanInfo(page.getClass());
                } catch (IntrospectionException ex) {
                    throw new UnexpectedErrorException(ex);
                }
                final Optional<PropertyDescriptor> propertyDescriptor = Arrays
                    .stream(beanInfo.getPropertyDescriptors())
                    .filter(propDesc -> attribute.equals(propDesc.getName()))
                    .findAny();
                if (propertyDescriptor.isPresent()) {
                    final Method readMethod = propertyDescriptor
                        .get()
                        .getReadMethod();
                    final Object value;
                    try {
                        value = readMethod.invoke(object);
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                    if (value == null) {
                        return (String) new GlobalizedMessage("cms.ui.unknown",
                                                              CmsConstants.CMS_BUNDLE)
                            .localize();
                    } else {
                        final GlobalizationHelper globalizationHelper = CdiUtil
                            .createCdiUtil()
                            .findBean(GlobalizationHelper.class);

                        // Note: No type safety here! We relay that it is
                        // attached to a date property!
                        return DateFormat.getDateInstance(
                            DateFormat.LONG,
                            globalizationHelper.getNegotiatedLocale())
                            .format(value);
                    }
                } else {
                    return (String) new GlobalizedMessage("cms.ui.unknown",
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
