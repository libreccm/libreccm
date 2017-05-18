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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PropertyEditor;
import com.arsdigita.bebop.PropertyEditorModel;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.ComponentAccess;
import com.arsdigita.util.Assert;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.librecms.CmsConstants;

/**
 * Extends {@link com.arsdigita.bebop.PropertyEditor} to provide access control
 * features. Each link may be associated with a {@link
 * com.arsdigita.toolbox.ui.ComponentAccess} object; if the current does not
 * have sufficient privileges, the link will be hidden.
 * <p>
 * The simple use pattern for this component is as follows:
 *
 * <blockquote><pre><code>
 * SecurityPropertyEditor editor = new SecurityPropertyEditor();
 * editor.setDisplayComponent(new FooComponent());
 * NameEditForm n = new NameEditForm();
 * ComponentAccess ca1 = new ComponentAccess(n);
 * ca1.addAccessCheck(WORKFLOW_ADMIN);
 * ca1.addAccessCheck(CATEGORY_ADMIN);
 * editor.add("name", "Edit Name", ca, n.getCancelButton());
 * AddressEditForm a = new AddressEditForm();
 * ComponentAccess ca2 = new ComponentAccess(a);
 * editor.add("address", "Edit Address", ca2, a.getCancelButton());
 * </code></pre></blockquote>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SecurityPropertyEditor extends PropertyEditor {

    private final Map<String, ComponentAccess> accessChecks;

    /**
     * Construct a new, empty <code>PropertyEditor</code>. The {@link
     * #setDisplayComponent(Component)} method must be called before this
     * component is locked.
     */
    public SecurityPropertyEditor() {
        this(null);
    }

    /**
     * Construct a new, <code>PropertyEditor</code> with the given display
     * component
     *
     * @param display The display component
     */
    public SecurityPropertyEditor(final Component display) {
        super(display);
        accessChecks = new HashMap<>();
        setModelBuilder(new AccessListModelBuilder());
    }

    /**
     * Add a component to the property editor. The component will be completely
     * invisible; it is up to the user to call {@link #showComponent(PageState,
     * String)} to display the component, and to call {@link
     * #showDisplayPane(PageState)} when the component needs to be hidden.
     *
     * @param key The symbolic key for the component; must be unique for this
     * <code>PropertyEditor</code>
     * @param componentAccess The {@link ComponentAccess} object which contains
     * the child component, along with security restrictions
     */
    public void addComponent(final String key,
                             final ComponentAccess componentAccess) {
        super.addComponent(key, componentAccess.getComponent());
        accessChecks.put(key, componentAccess);
    }

    /**
     * Add a component to the list of links. It is up to the component to
     * correctly call showDisplayPane when it's done.
     *
     * @param key The symbolic key for the component; must be unique for this
     * <code>PropertyEditor</code>
     * @param label The label for the link
     * @param componentAccess The component access
     * @deprecated use addComponent(String,GlobalizedMessage,ComponentAccess)
     * instead.
     */
    public void addComponent(final String key,
                             final String label,
                             final ComponentAccess componentAccess) {
        addComponent(key, componentAccess);
        getLabelsMap().put(key, label);
    }

    /**
     * Add a component to the list of links. It is up to the component to
     * correctly call showDisplayPane when it's done.
     *
     * @param key The symbolic key for the component; must be unique for this
     * <code>PropertyEditor</code>
     * @param label The label for the link
     * @param componentAccess The component access
     */
    public void addComponent(final String key,
                             final GlobalizedMessage label,
                             final ComponentAccess componentAccess) {
        addComponent(key, componentAccess);
        getLabelsMap().put(key, label);
    }

    /**
     * Specify a new {@link ComponentAccess} for a component which has already
     * been added to the <code>SecurityPropertyEditor</code>.
     *
     * @param key the key under which the component was added
     * @param componentAccess the <code>ComponentAccess</code> instance that
     * will determine when the link for the specified component should be
     * visible
     * @pre access.getComponent() == m_forms.get(key)
     */
    public void setComponentAccess(final String key,
                                   final ComponentAccess componentAccess) {
        Assert.isUnlocked(this);
        final Component component = getComponent(key);
        Assert.exists(component, "the specified component");
        Assert.isTrue(componentAccess.getComponent().equals(component),
                      "The specified component does not match the component that"
                      + " id already in the PropertyEditor");
        accessChecks.put(key, componentAccess);
    }

    /**
     * Add a form to the set of forms which could be used to edit the
     * properties.
     *
     * @param key The symbolic key for the form; must be unique for this
     * <code>PropertyEditor</code>
     * @param label The label for the link to access the form
     * @param componentAccess The form ComponentAccess
     *
     * @deprecated use add(String,GlobalizedMessage,ComponentAccess)
     */
    public void add(final String key,
                    final String label,
                    final ComponentAccess componentAccess) {
        final Component component = componentAccess.getComponent();
        if (component instanceof Form) {
            final Form form = (Form) component;
            accessChecks.put(key, componentAccess);
            add(key, label, form);
            addSecurityListener(form);
        } else if (component instanceof FormSection) {
            final FormSection section = (FormSection) componentAccess.
                    getComponent();
            accessChecks.put(key, componentAccess);
            add(key, label, section);
            addSecurityListener(section);
        } else {
            throw new IllegalArgumentException(
                    "The ComponentAccess object does "
                            + "not contain a form section.");
        }
    }

    /**
     * Add a form to the set of forms which could be used to edit the
     * properties.
     *
     * @param key The symbolic key for the form; must be unique for this
     * <code>PropertyEditor</code>
     * @param label The label for the link to access the form
     * @param componentAccess The form ComponentAccess
     */
    public void add(final String key,
                    final GlobalizedMessage label,
                    final ComponentAccess componentAccess) {
        final Component component = componentAccess.getComponent();
        if (component instanceof Form) {
            final Form form = (Form) component;
            accessChecks.put(key, componentAccess);
            add(key, label, form);
            addSecurityListener(form);
        } else if (component instanceof FormSection) {
            final FormSection section = (FormSection) componentAccess.
                    getComponent();
            accessChecks.put(key, componentAccess);
            add(key, label, section);
            addSecurityListener(section);
        } else {
            throw new IllegalArgumentException(
                    "The ComponentAccess object does "
                            + "not contain a form section.");
        }
    }

    /**
     * Add a form to the set of forms which could be used to edit the properties
     *
     * @param key The symbolic key for the form; must be unique for this
     * <code>PropertyEditor</code>
     * @param label The label for the link to access the form.
     * @param componentAccess The form ComponentAccess
     * @param cancelButton The Cancel button on the form.
     *
     * @deprecated use add(String,GlobalizedMessage,ComponentAccess,Submit)
     * instead
     */
    public void add(final String key,
                    final String label,
                    final ComponentAccess componentAccess,
                    final Submit cancelButton) {
        add(key, label, componentAccess);
        addCancelListener((FormSection) componentAccess.getComponent(),
                          cancelButton);
    }

    /**
     * Add a form to the set of forms which could be used to edit the properties
     *
     * @param key The symbolic key for the form; must be unique for this
     * <code>PropertyEditor</code>
     * @param label The label for the link to access the form.
     * @param componentAccess The form ComponentAccess
     * @param cancelButton The Cancel button on the form.
     */
    public void add(final String key,
                    final GlobalizedMessage label,
                    final ComponentAccess componentAccess,
                    final Submit cancelButton) {
        add(key, label, componentAccess);
        addCancelListener((FormSection) componentAccess.getComponent(),
                          cancelButton);
    }

    /**
     * Add a submission listener to the form that will hide all components and
     * show the display pane. This method should be used to add submission
     * listeners to forms which are buried deep inside some component, and are
     * not members of this <code>PropertyEditor</code>.
     *
     * @param form The form
     */
    public void addSecurityListener(final FormSection form) {
        form.addSubmissionListener(new FormSubmissionListener() {

            @Override
            public void submitted(final FormSectionEvent event) throws
                    FormProcessException {

                final PageState state = event.getPageState();

                // Cancel the form if the user does not pass the access checks.
                final String key = (String) getList().getSelectedKey(state);
                final ComponentAccess componentAccess
                                      = (ComponentAccess) accessChecks.get(key);

                if (key == null || componentAccess == null) {
                    // no components currently selected and therefore
                    // no access checks to run for visibility
                    // or
                    // there are no access restrictions on the form
                    return;
                }

                if (!componentAccess.canAccess()) {
                    showDisplayPane(state);
                    throw new FormProcessException(new GlobalizedMessage(
                            "cms.ui.insufficient_privileges",
                            CmsConstants.CMS_BUNDLE));
                }
            }
        });
    }

    /**
     * Add all required listeners to the form to ensure that if the form is
     * submitted successfully or cancelled, the display pane will be shown. This
     * method should be used to add listeners to forms which are buried deep
     * inside some component, and are not members of this
     * <code>PropertyEditor</code>.
     *
     * @param form The form
     * @param cancelButton the "Cancel" button on the form
     */
    @Override
    public void addListeners(final FormSection form,
                             final Submit cancelButton) {
        addSecurityListener(form);
        super.addListeners(form, cancelButton);
    }

    /**
     * Return the map of keys to access checks
     *
     * @return Map of keys to access check
     */
    protected final Map<String, ComponentAccess> getAccessMap() {
        return accessChecks;
    }

    /**
     * Returns an {@link SecurityPropertyEditor.AccessListModel} during each
     * request
     */
    protected static class AccessListModelBuilder extends DefaultModelBuilder {

        public AccessListModelBuilder() {
            super();
        }

        @Override
        public PropertyEditorModel makeModel(
                final PropertyEditor propertyEditor, final PageState state) {
            
            return new AccessListModel(
                    getProperties(propertyEditor),
                    ((SecurityPropertyEditor) propertyEditor).getAccessMap(),
                    state);
        }
    }

    /**
     * Performs access checks for each property; skips the properties that the
     * user is not allowed to access
     */
    protected static class AccessListModel extends DefaultModel {

        private final Map<String, ComponentAccess> accessMap;
        private final PageState state;

        public AccessListModel(final Iterator iter, 
                               final Map<String, ComponentAccess> accessMap, 
                               final PageState state) {
            super(iter);
            this.accessMap = accessMap;
            this.state = state;
        }

        @Override
        public boolean next() {
            
            while (super.next()) {
                final Object key = getKey();
                final ComponentAccess ca = accessMap.get(key.toString());

                if (ca == null) {
                    return true;
                }

                if (ca.canAccess()) {
                    return true;
                }

                // Otherwise, skip the property
            }

            return false;
        }
    }

}
