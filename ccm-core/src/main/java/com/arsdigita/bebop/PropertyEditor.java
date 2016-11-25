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
package com.arsdigita.bebop;

import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.list.DefaultListCellRenderer;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.util.Assert;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.util.SequentialMap;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.globalization.GlobalizedMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Maintains a set of forms that are used when editing the
 * properties of some object. The component maintains a single
 * display pane and a list of forms that are selectable by links.
 * <p>
 * By default, the component looks something like this:
 * <blockquote><pre><code>
 * +----------------+
 * |                |
 * |  Display Pane  |
 * |                |
 * +----------------+
 * [link to form1]
 * [link to form2]
 * [link to form3]
 * </code></pre></blockquote>
 *
 * When the user clicks on a link, the display pane is hidden and the
 * corresponding form is shown.
 *
 * <blockquote><pre><code>
 * Enter foo: [           ]
 * Enter bar: [           ]
 *
 *          [Save] [Cancel]
 * </code></pre></blockquote>
 *
 * When the user clicks the Save or Cancel button on the form, the form
 * is hidden and the display pane (with its list of links)
 * is shown once again.
 * <p>
 * The simple usage pattern for this class is as follows:
 *
 * <blockquote><pre><code>
 * PropertyEditor editor = new PropertyEditor();
 * editor.setDisplayComponent(new FooComponent());
 * NameEditForm n = new NameEditForm();
 * editor.add("name", "Edit Name", n, n.getCancelButton());
 * AddressEditForm a = new AddressEditForm();
 * editor.add("address", "Edit Address", a, a.getCancelButton());
 * </code></pre></blockquote>
 *
 * The <code>PropertyEditor</code> will automatically add the right
 * listeners to the forms.
 * <p>
 * This class is used extensively in the default authoring kit steps,
 * especially <code>PageEdit</code> and <code>TextPageBody</code> in CMS.
 * <p>
 * <b>Advanced Usage</b>:<br>
 * The <code>PropertyEditor</code> may be used to maintain
 * visibility of any components, not just forms. The
 * {@link #addComponent(String, String, Component)} method
 * can be used to add an arbitrary component to the editor. The
 * component will be shown in the list of links, along with other components
 * and/or forms. The component will be shown as usual when the user clicks
 * on a link. However, you must be sure to include a call to
 * {@link #showDisplayPane(PageState)} when the component needs to be hidden.
 * <p>
 * In addition, it is possible to manually generate {@link ActionLink}s
 * that will display the right components in the editor. The
 * {@link #addComponent(String, Component)} method can be used to add
 * a component to the <code>PropertyEditor</code> without automatically
 * generating the link for it. The {@link #addVisibilityListener(ActionLink, String)} 
 * method can then be used to add an appropriate {@link ActionListener} to any
 * {@link ActionLink}. For example:
 *
 * <blockquote><pre><code>// Add a form
 * Form fooForm = new FooForm();
 * editor.addComponent(FOO_FORM, fooForm);
 * editor.addListeners(fooForm, fooForm.getCancelButton());
 * // Create a link that shows the form
 * fooLink = new ActionLink("Edit the Foo property");
 * editor.addVisibilityListener(fooLink, FOO_FORM);</code></pre></blockquote>
 *
 * Note that the visibility of the form will be handled automatically. There
 * is no need to show or hide it manually. This approach allows
 * greater flexibility in placing links on a page. The links may be
 * part of the editor's display pane, but they do not have to be.
 * <p>
 * <b>More-advanced Usage</b>:<br>
 * The <code>PropertyEditor</code> is backed by a
 * {@link PropertyEditorModel} through a {@link PropertyEditorModelBuilder}.
 * Therefore, the <code>PropertyEditor</code> is a model-backed component,
 * as described in the Bebop tutorials. This means that the list
 * of properties for the editor could be generated dynamically during
 * each request. The {@link #setModelBuilder(PropertyEditorModelBuilder)} method 
 * can be used to set a specialized {@link PropertyEditorModelBuilder} for the 
 * editor. In order to write the model builder, you may choose to extend the 
 * protected inner classes {@link PropertyEditor.DefaultModelBuilder} and
 * {@link PropertyEditor.DefaultModel}. It is also possible to write the model
 * builder and the corresponding model from scratch. However, most people won't 
 * need to do this.
 * <p>
 * For example, <code>SecurityPropertyEditor</code> uses a custom
 * {@link PropertyEditorModelBuilder} in order to hide the links for properties
 * which the web user is not allowed to edit.
 * <p>
 *
 * @author Stanislav Freidin
 * @version $Id: PropertyEditor.java 1638 2007-09-17 11:48:34Z chrisg23 $
 */
public class PropertyEditor extends SimpleContainer {

    private SequentialMap m_forms;
    private SequentialMap m_labels;
    private Component m_display;
    private Container m_displayPane;
    private List m_list;
    private PropertyEditorModelBuilder m_builder;
    private RequestLocal m_model;
    private java.util.List m_additionalDisplayComponents = new ArrayList();

    /**
     * Constructs a new, empty <code>PropertyEditor</code>.
     * The {@link #setDisplayComponent(Component)} method must be called before
     * this component is locked.
     */
    public PropertyEditor() {
        this(null);
    }

    /**
     * Constructs a new <code>PropertyEditor</code> with the given
     * display component.  The pane defaults to a {@link
     * com.arsdigita.bebop.SimpleContainer}.
     *
     * @param display the display component
     */
    public PropertyEditor(Component display) {
        this(display, new SimpleContainer());
    }

    /**
     * Constructs a new <code>PropertyEditor</code> with the given
     * display component and display pane.
     *
     * @param display the display component
     * @param pane the display pane.  The caller should pass in a
     * freshly allocated Container.
     */
    public PropertyEditor(Component display, Container pane) {
        super();
        setClassAttr("propertyEditor");
        m_forms = new SequentialMap();
        m_labels = new SequentialMap();
        m_display = null;

        m_displayPane = pane;
        super.add(m_displayPane);

        m_list = new List();
        m_list.setCellRenderer(new IdentityCellRenderer());

        // Change listener: reset visibility
        // Should a ComponentSelectionModel be used here instead ? It's tempting,
        // but there doesn't seem to be a real need for it
        m_list.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    PageState state = e.getPageState();

                    // Get the visible component
                    Component c = null;
                    if ( !m_list.isSelected(state) ) {
                        // Select the display pane
                        c = m_displayPane;
                    } else {
                        c = getComponent(getSelectedComponentKey(state));
                    }

                    // Iterate over the forms
                    for(Iterator i = m_forms.values().iterator(); i.hasNext(); ) {
                        Component f = (Component)i.next();
                        f.setVisible(state, (f == c));
                    }

                    m_displayPane.setVisible(state, (m_displayPane == c));
                }
            });

        // Don't add the list yet; add it when we add the display
        // component

        if(display != null)
            setDisplayComponent(display);

        // Prepare the model builder
        setModelBuilder(new DefaultModelBuilder());

        m_model = new RequestLocal() {
                @Override
                protected Object initialValue(PageState s) {
                    return getModelBuilder().makeModel(PropertyEditor.this, s);
                }
            };
    }

    /** Set the display component visible by default, and the
     *  form(s) invisible by default.
     */
    @Override
    public void register(Page p) {
        Assert.exists(m_display, "display component");

        p.setVisibleDefault(m_displayPane, true);

        for(Iterator i = m_forms.values().iterator(); i.hasNext(); ) {
            p.setVisibleDefault((Component)i.next(), false);
        }
    }

    /**
     * Hides the form(s) and shows the display pane.
     *
     * @param state the page state
     */
    public void showDisplayPane(PageState state) {
        m_list.clearSelection(state);
    }

    /**
     * Shows the component that is identified by the specified key.
     *
     * @param state the page state
     * @param key
     */
    public void showComponent(PageState state, String key) {
        m_list.setSelectedKey(state, key);
    }

    /**
     * Returns the key of the currently visible component, or null if
     * the display pane is currently visible.
     * @param state the page state
     *
     * @return the key of the currently visible component, or null if the
     *         display pane is visible.
     */
    public String getSelectedComponentKey(PageState state) {
        return (String)m_list.getSelectedKey(state);
    }

    /**
     * add an additional component below the list of links
     * @param c
     */
    public void addDisplayComponent(Component c) {
    	m_additionalDisplayComponents.add(c);
    }
    /**
     * Adds the display component if it has not been added already.
     *
     * @param c the display component to add
     */
    public void setDisplayComponent(Component c) {
        if(m_display != null) {
            throw new IllegalStateException("Display component has already been set");
        }

        m_displayPane.add(c);
        m_displayPane.add(m_list);
        Iterator it = m_additionalDisplayComponents.iterator();
        while (it.hasNext()) {
        	m_displayPane.add((Component)it.next());
        }

        m_display = c;
    }

    /**
     * Adds a component to the property editor. The component will be
     * completely invisible. It is up to the user to call
     * {@link #showComponent(PageState, String)} to display the component and to
     * call {@link #showDisplayPane(PageState)} when the component needs to be hidden.
     *
     * @param key   the symbolic key for the component (must be unique
     *    for this <code>PropertyEditor</code>)
     * @param c     the component
     */
    public void addComponent(String key, Component c) {
        m_forms.put(key, c);
        super.add(c);
    }

    /**
     * Adds a component to the list of links. It is up to the component to
     * correctly call {@link #showDisplayPane(PageState)} when it is done.
     *
     * @param key   the symbolic key for the component (must be unique
     *              for this <code>PropertyEditor</code>)
     * @param label the label for the link
     * @param c     the component
     * @deprecated use addComponent(String,GlobalizedMessage,Component) instead
     */
    public void addComponent(String key, String label, Component c) {
        addComponent(key, c);
        m_labels.put(key, label);
    }

    /**
     * Adds a component to the list of links. It is up to the component to
     * correctly call {@link #showDisplayPane(PageState)} when it is done.
     *
     * @param key   the symbolic key for the component (must be unique
     *              for this <code>PropertyEditor</code>)
     * @param label the label for the link
     * @param c     the component
     */
    public void addComponent(String key, GlobalizedMessage label, Component c) {
        addComponent(key, c);
        m_labels.put(key, label);
    }

    /**
     * Adds a form to the set of forms that can be used to edit the
     * properties.
     *
     * @param key   the symbolic key for the form (must be unique
     *              for this <code>PropertyEditor</code>)
     * @param label the label for the link
     * @param form  the form component
     * @deprecated use add(String,GlobalizedMessage,Form) instead.
     */
    public void add(String key, String label, Form form) {
        addComponent(key, label, form);
        addProcessListener(form);
    }

    /**
     * Adds a form to the set of forms that can be used to edit the
     * properties.
     *
     * @param key   the symbolic key for the form (must be unique
     *              for this <code>PropertyEditor</code>)
     * @param label the label for the link
     * @param form  the form component
     */
    public void add(String key, GlobalizedMessage label, Form form) {
        addComponent(key, label, form);
        addProcessListener(form);
    }

    /**
     * Adds a form to the set of forms that can be used to edit the
     * properties.
     *
     * @param key   the symbolic key for the form (must be unique
     *    for this <code>PropertyEditor</code>)
     * @param label the label for the link
     * @param form the form component
     * @param cancelButton the Cancel button on the form
     * @deprecated use add(String,GlobalizedMessage,Form,Submit) instead.
     */
    public void add(String key, String label, Form form, Submit cancelButton) {
        add(key, label, form);
        addListeners(form, cancelButton);
    }

    /**
     * Adds a form to the set of forms that can be used to edit the
     * properties.
     *
     * @param key   the symbolic key for the form (must be unique
     *    for this <code>PropertyEditor</code>)
     * @param label the label for the link
     * @param form the form component
     * @param cancelButton the Cancel button on the form
     */
    public void add(String key, GlobalizedMessage label, Form form, Submit cancelButton) {
        add(key, label, form);
        addListeners(form, cancelButton);
    }

    /**
     * Adds a form to the set of forms that can be used to edit the
     * properties.
     *
     * @param key   the symbolic key for the form (must be unique
     *              for this <code>PropertyEditor</code>)
     * @param label the label for the link
     * @param formSection  the form component
     * 
     * @pre !(formSection instanceof Form)
     * @deprecated use add(String,GlobalizedMessage,FormSection) instead.
     */
    public void add(String key, String label, FormSection formSection) {
        if (formSection instanceof Form) {
            throw new IllegalArgumentException("formSection is an instance of Form");
        }
        Form form = new Form("property" + key);
        form.add(new FormErrorDisplay(form), ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
        form.add(formSection);
        form.setMethod(Form.POST);
        form.setEncType("multipart/form-data");
        add(key, label , form);
    }

    /**
     * Adds a form to the set of forms that can be used to edit the
     * properties.
     *
     * @param key   the symbolic key for the form (must be unique
     *              for this <code>PropertyEditor</code>)
     * @param label the label for the link
     * @param formSection  the form component
     *
     * @pre !(formSection instanceof Form)
     */
    public void add(String key, GlobalizedMessage label, FormSection formSection) {
        if (formSection instanceof Form) {
            throw new IllegalArgumentException("formSection is an instance of Form");
        }
        Form form = new Form("property" + key);
        form.add(new FormErrorDisplay(form), ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
        form.add(formSection);
        form.setMethod(Form.POST);
        form.setEncType("multipart/form-data");
        add(key, label , form);
    }

    /**
     * Adds a form to the set of forms that can be used to edit the properties.
     *
     * @param key   the symbolic key for the form (must be unique
     *              for this <code>PropertyEditor</code>)
     * @param label the label for the link
     * @param formSection the form component
     * @param cancelButton the Cancel button on the form
     * @deprecated use add(String,GlobalizedMessage,FormSection,Submit) instead.
     */
    public void add(String key,
                    String label,
                    FormSection formSection,
                    Submit cancelButton) {
        Form form = new Form("property" + key);
        form.add(new FormErrorDisplay(form), ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
        form.add(formSection);
        form.setMethod(Form.POST);
        form.setEncType("multipart/form-data");
        add(key, label , form, cancelButton);
    }

    /**
     * Adds a form to the set of forms that can be used to edit the
     * properties.
     *
     * @param key   the symbolic key for the form (must be unique
     *              for this <code>PropertyEditor</code>)
     * @param label the label for the link as a GlobalizedMessage
     * @param formSection the form component
     * @param cancelButton the Cancel button on the form
     */
    public void add(String key,
                    GlobalizedMessage label,
                    FormSection formSection,
                    Submit cancelButton) {
        Form form = new Form("property" + key);
        form.add(new FormErrorDisplay(form), ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
        form.add(formSection);
        form.setMethod(Form.POST);
        form.setEncType("multipart/form-data");
        add(key, label , form, cancelButton);
    }

    /**
     * Retrieves the component at the specified key.
     *
     * @param key the key of the component to retrieve
     * @return    a component that has been added to this
     *            <code>PropertyEditor</code> at the specified key, or null
     *            if no such component exists.
     */
    public Component getComponent(String key) {
        return (Component)m_forms.get(key);
    }

    /**
     * Adds a submission listener to the form that will hide all components
     * and show the display pane. This method should be used to add
     * submission listeners to forms that are buried deep inside some
     * component and are not members of this <code>PropertyEditor</code>.
     *
     * @param form the form
     * @param cancelButton the Cancel button on the form
     */
    public void addCancelListener(FormSection form, Submit cancelButton) {
        // Add a different submission listener for each form since the
        // cancel button may be different
        final Submit theButton = cancelButton;

        form.addSubmissionListener(new FormSubmissionListener() {
                @Override
                public void submitted(FormSectionEvent e) throws FormProcessException {
                    PageState state = e.getPageState();
                    if(theButton.isSelected(state)) {
                        showDisplayPane(state);
                        throw new FormProcessException(
                                "Submission Cancelled",
                                GlobalizationUtil.globalize("bebop.cancel.msg"));
                    }
                }
            });
    }

    /**
     * Adds a process listener to the form that will hide all components
     * and show the display pane. This method should be used to add
     * process listeners to forms that are buried deep inside some
     * component and are not members of this <code>PropertyEditor</code>.
     *
     * @param form the form
     */
    public void addProcessListener(FormSection form) {
        form.addProcessListener(new FormProcessListener() {
                @Override
                public void process(FormSectionEvent e) throws FormProcessException {
                    PageState state = e.getPageState();
                    showDisplayPane(state);
                }
            });
    }

    /**
     * Adds all required listeners to the form to ensure that
     * if the form is either submitted successfully or cancelled,
     * the display pane will be shown. This method should be used
     * to add listeners to forms that are buried deep inside some
     * component, and are not members of this <code>PropertyEditor</code>.
     *
     * @param form the form
     * @param cancelButton the Cancel button on the form
     */
    public void addListeners(FormSection form, Submit cancelButton) {
        addCancelListener(form, cancelButton);
        addProcessListener(form);
    }

    /**
     * This method can be used to add an {@link ActionListener} to any
     * {@link ActionLink}, causing the action link to show the specified
     * component when it is clicked. For example, this method may be useful
     * if the {@link ActionLink} that is supposed to show the component is
     * buried somewhere deep within the UI.
     *
     * @param l   the {@link ActionLink}
     * @param key the key of the component that will be shown when the link
     *             is clicked, as specified in the 
     *             {@link #addComponent(String, Component)}  method
     * @see       #addComponent(String, Component)
     */
    public void addVisibilityListener(ActionLink l, String key) {
        final String t_key = key;
        l.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showComponent(e.getPageState(), t_key);
                }
            });
    }

    /**
     * Returns the {@link List} that contains all the links.
     * @return the {@link List} that contains all the links.
     */
    public List getList() {
        return m_list;
    }

    /**
     * Returns the {@link PropertyEditorModelBuilder} that supplies this
     * property editor with its {@link PropertyEditorModel} during each
     * request.
     *
     * @return the {@link PropertyEditorModelBuilder} for this component.
     */
    protected final PropertyEditorModelBuilder getModelBuilder() {
        return m_builder;
    }

    /**
     * Sets the {@link PropertyEditorModelBuilder} that will supply this
     * property editor with its {@link PropertyEditorModel} during each
     * request.
     * @param b the property editor model builder
     */
    protected final void setModelBuilder(PropertyEditorModelBuilder b) {
        Assert.isUnlocked(this);
        m_builder = b;
        m_list.setModelBuilder(new BuilderAdapter(this));
    }

    /**
     * Returns the {@link PropertyEditorModel} in use during the current
     * request.
     *
     * @param s represents the current request
     * @return the {@link PropertyEditorModel} that supplies the properties
     *   for the current request.
     */
    protected final PropertyEditorModel getModel(PageState s) {
        return (PropertyEditorModel)m_model.get(s);
    }

    /**
     * Returns the display component.
     * @return the display component.
     */
    public Component getDisplayComponent() {
        return m_display;
    }

    /**
     * Returns the display pane (component + list).
     * @return the display pane (component + list).
     */
    public Container getDisplayPane() {
        return m_displayPane;
    }

    /**
     * Returns the map of labels.
     * @return the map of labels.
     */
    protected SequentialMap getLabelsMap() {
        return m_labels;
    }

    /**
     * Locks this component.
     */
    @Override
    public void lock() {
        getModelBuilder().lock();
        super.lock();
    }

    /**
     *  Renders the components generated by the model directly
     */
    protected static class IdentityCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getComponent(List list, PageState state, Object value,
                                      String key, int index, boolean isSelected) {
            return (Component)value;
        }
    }

    /**
     * Default implementation of the {@link PropertyEditorModelBuilder}.
     * Takes in a SequentialMap of key->label, and constructs a ControlLink for each
     * label.
     */
    protected static class DefaultModelBuilder
        extends LockableImpl implements PropertyEditorModelBuilder {

        public DefaultModelBuilder() {
            super();
        }

        /**
         * Return an iterator of all properties of the specified property
         * editor. These properties should be passed into the constructor
         * of the {@link PropertyEditor.DefaultModel}
         * @param p
         * @return 
         */
        protected Iterator getProperties(PropertyEditor p) {
            return p.getLabelsMap().entrySet().iterator();
        }

        /**
         * Construct a {@link PropertyEditorModel} for the current
         * request.
         */
        @Override
        public PropertyEditorModel makeModel(PropertyEditor p, PageState s) {
            return new DefaultModel(getProperties(p));
        }
    }

    /**
     * Internal class with default implementation of the {@link PropertyEditorModel}.
     * Takes in an iterator of key->label pairs, and constructs a ControlLink 
     * for each label.
     */
    protected static class DefaultModel implements PropertyEditorModel {

        protected Iterator m_iter;
        protected Map.Entry m_entry;

        public DefaultModel(Iterator iter) {
            m_iter = iter;
            m_entry = null;
        }

        @Override
        public boolean next() {
            if(!m_iter.hasNext()) {
                m_entry = null;
                return false;
            }
            m_entry = (Map.Entry)m_iter.next();
            return true;
        }

        /**
         * Actually retrieve action link and label. Will be executed at each
         * request to ensure proper localization.
         * @return 
         */
        @Override
        public Component getComponent() {
            Assert.exists(m_entry);
            if ( m_entry.getValue() instanceof GlobalizedMessage ) {
                ControlLink l = new ControlLink(new 
                                    Label((GlobalizedMessage)m_entry.getValue()));
                l.setClassAttr("actionLink");
                return l;
            } else {
                ControlLink l = new ControlLink(new Label((String)m_entry.getValue()));                
                
                l.setClassAttr("actionLink");
                return l;
            }
        }

        @Override
        public Object getKey() {
            Assert.exists(m_entry);
            return m_entry.getKey();
        }
    }

    /**
     * Adapts a {@link PropertyEditorModelBuilder} to a {@link ListModelBuilder}
     */
    private static final class BuilderAdapter extends LockableImpl
        implements ListModelBuilder {

        private final PropertyEditor m_parent;

        public BuilderAdapter(PropertyEditor parent) {
            super();
            m_parent = parent;
        }

        @Override
        public ListModel makeModel(List l, PageState state) {
            return new ModelAdapter(m_parent.getModel(state));
        }
    }

    /**
     * Adapts a {@link PropertyEditorModel} to a {@link ListModel}
     */
    private static final class ModelAdapter implements ListModel {

        private final PropertyEditorModel m_model;

        public ModelAdapter(PropertyEditorModel model) {
            m_model = model;
        }

        @Override
        public boolean next() { return m_model.next(); }
        @Override
        public Object getElement() { return m_model.getComponent(); }
        @Override
        public String getKey() { return m_model.getKey().toString(); }
    }

}
