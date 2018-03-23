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

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;

import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.DefaultListCellRenderer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.EventListenerList;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.util.Assert;
import com.arsdigita.bebop.util.BebopConstants;

import com.arsdigita.xml.Element;

/**
 * A <code>List</code>, similar to a <code>javax.swing.JList</code>, that keeps
 * track of a sequence of items and selections of one or more of these items. A
 * separate model, {@link ListModel}, is used to represent the items in the
 * list.
 *
 * @see ListModel
 * @see ListModelBuilder
 * @see com.arsdigita.bebop.list.ListCellRenderer
 * @author David Lutterkort
 * @version $Id: List.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class List extends SimpleComponent implements BebopConstants {

    /**
     * The name of the StringParameter that the list uses to keep track of which
     * item is selected.
     */
    public static final String SELECTED = "sel";

    /**
     * The name of the event the list sets when producing links that change
     * which item is selected.
     */
    public static final String SELECT_EVENT = "s";

    /**
     * The model builder for this list. Is used to produce a new model for each
     * request served by this <code>List</code>.
     *
     * @see #setListModelBuilder
     * @see ArrayListModelBuilder
     * @see MapListModelBuilder
     */
    private ListModelBuilder m_modelBuilder;

    private RequestLocal m_model;

    /**
     * The renderer used to format list items.
     *
     * @see DefaultListCellRenderer
     */
    private ListCellRenderer m_renderer;

    private EventListenerList m_listeners;

    private SingleSelectionModel m_selection;

    private ChangeListener m_changeListener;

    private Component m_emptyView;

    private boolean m_stateParamsAreRegistered;

    /**
     * <p>
     * Vertical List layout.</p>
     *
     */
    public static final int VERTICAL = 0;

    /**
     * <p>
     * Horizontal List layout.</p>
     *
     */
    public static final int HORIZONTAL = 1;

    private int m_layout = VERTICAL;

    /**
     * Creates a new <code>List</code> that uses the specified list model
     * builder to generate per-request {@link ListModel ListModels}.
     *
     * @param b the model builder used for this list
     *
     * @pre b != null
     */
    public List(ListModelBuilder b) {
        this();
        m_modelBuilder = b;
        m_emptyView = null;
    }

    /**
     * Creates an empty <code>List</code>.
     */
    public List() {
        // Force the use of the 'right' constructor
        this((SingleSelectionModel) null);
        m_selection = new ParameterSingleSelectionModel(new StringParameter(
            SELECTED));
    }

    /**
     * Create an empty <code>List</code>.
     */
    public List(SingleSelectionModel selection) {
        // This is the real constructor. All other constructors must call it
        // directly or indirectly
        super();
        m_renderer = new DefaultListCellRenderer();
        m_listeners = new EventListenerList();
        m_selection = selection;
        setListData(new Object[0]);
        initListModel();
        m_emptyView = null;
        m_stateParamsAreRegistered = true;
    }

    /**
     * Creates a new <code>List</code> from an array of objects. Uses an
     * internal {@link ListModelBuilder}. Each {@link ListModel} that is built
     * will iterate through the entries of the object, returning the objects
     * from calls to {@link ListModel#getElement} and the corresponding index,
     * which is converted to a <code>String</code> from calls to {@link
     * ListModel#getKey}.
     *
     * @param values an array of items
     *
     * @pre values != null
     * @see #setListData(Object[] v)
     */
    public List(Object[] values) {
        this();
        setListData(values);
    }

    /**
     * Creates a new <code>List</code> from a map. Uses an internal {@link
     * ListModelBuilder}. Each {@link ListModel} that is built will iterate
     * through the entries in <code>map</code> in the order in which they are
     * returned by <code>map.entrySet().iterator()</code>. Calls to {@link
     * ListModel#getElement} return one value in <code>map</code>. Calls to
     * {@link ListModel#getElement} return the corresponding key, which is
     * converted to a <code>String</code> by calling <code>toString()</code> on
     * the key.
     *
     * @param map a key-value mapping for the list items
     *
     * @pre map != null
     */
    public List(Map map) {
        this();
        setListData(map);
    }

    /**
     * Registers this <code>List</code> and its state parameter(s) with the
     * specified page.
     *
     * @param p the page this list is contained in
     *
     * @pre p != null
     * @pre ! isLocked()
     */
    public void register(Page p) {
        Assert.isUnlocked(this);
        if (m_selection.getStateParameter() != null) {
            p.addComponentStateParam(this, m_selection.getStateParameter());
        }
    }

    /**
     * Responds to a request in which this <code>List</code> was the targetted
     * component. Calls to this method should only be made through links
     * generated by this list.
     *
     * <p>
     * Determines the new selected element and fires a {@link
     * ChangeEvent} if it has changed. After that, fires an {@link
     * ActionEvent}.
     *
     * @param state the state of the current request
     *
     * @throws ServletException if the control event is unknown.
     * @pre state != null
     * @see #fireStateChanged fireStateChanged
     * @see #fireActionEvent fireActionEvent
     */
    public void respond(PageState state) throws ServletException {
        String event = state.getControlEventName();

        if (SELECT_EVENT.equals(event)) {
            setSelectedKey(state, state.getControlEventValue());
        } else {
            throw new ServletException("Unknown event '" + event + "'");
        }
        fireActionEvent(state);
    }

    /**
     * Allow subclasses to override how the layout is determined.
     *
     * @param list
     */
    protected void exportLayoutAttribute(final Element list) {
        if (m_layout == VERTICAL) {
            list.addAttribute("layout", "vertical");
        } else {
            list.addAttribute("layout", "horizontal");
        }
    }

    protected String getTagName() {
        return BEBOP_LIST;
    }
    
    protected String getTagXMLNS() {
        return BEBOP_XML_NS;
    }
    
    /**
     * Generates XML representing the items in the list. The items are formatted
     * using a {@link ListCellRenderer}. <code>generateXML</code> is called on
     * each component returned by the renderer.
     *
     * <p>
     * The XML that is generated has the following form:
     * <pre>
     *   &lt;bebop:list mode="single" %bebopAttr;>
     *     &lt;bebop:cell [selected="selected"] key="itemKey">
     *        ... XML generated for component returned by renderer ...
     *     &lt;/bebop:cell>
     *     ... more &lt;bebop:cell> elements, one for each list item ...
     *   &lt;/bebop:list></pre>
     *
     * @param state  the state of the current request
     * @param parent the element into which XML is generated
     *
     * @pre state != null
     * @pre parent != null
     * @see com.arsdigita.bebop.list.ListCellRenderer
     */
    public void generateXML(PageState state, Element parent) {

        if (!isVisible(state)) {
            return;
        }

        ListModel m = getModel(state);

        // Check if there are items in the list
        if (m.next()) {

            // The list has items
            Element list = parent.newChildElement(getTagName(), getTagXMLNS());
            exportAttributes(list);

//            if (m_layout == VERTICAL) {
//                list.addAttribute("layout", "vertical");
//            } else {
//                list.addAttribute("layout", "horizontal");
//            }
            exportLayoutAttribute(list);

            Component c;

            Object selKey;
            if (getStateParamsAreRegistered()) {
                selKey = getSelectedKey(state);
            } else {
                selKey = null;
            }

            int i = 0;
            do {
                Element item = list.newChildElement(BEBOP_CELL, BEBOP_XML_NS);

                String key = m.getKey();
                Assert.exists(key);

                // Converting both keys to String for comparison
                // since ListModel.getKey returns a String
                boolean selected = (selKey != null) && key.equals(selKey
                    .toString());

                item.addAttribute("key", key);
                if (selected) {
                    item.addAttribute("selected", "selected");
                }
                if (getStateParamsAreRegistered()) {
                    state.setControlEvent(this, SELECT_EVENT, key);
                }
                c = getCellRenderer().getComponent(this, state, m.getElement(),
                                                   key, i, selected);
                c.generateXML(state, item);
                i += 1;
            } while (m.next());

        } else {
            // The list has no items
            if (m_emptyView != null) {
                // Display the empty view
                m_emptyView.generateXML(state, parent);
            } else {
                // For compatibility reasons, generate an empty
                // list element. In the future, this should go away
                Element list = parent.newChildElement(BEBOP_LIST, BEBOP_XML_NS);
                exportAttributes(list);
            }
        }

        state.clearControlEvent();
    }

    /**
     * <p>
     * Retrieve the current List layout.</p>
     *
     * @return List.VERTICAL or List.HORIZONTAL
     *
     */
    public int getLayout() {
        return m_layout;
    }

    /**
     * <p>
     * Set the current List layout.</p>
     *
     * @param layout New layout value, must be List.VERTICAL or List.HORIZONTAL
     *
     */
    public void setLayout(int layout) {
        Assert.isUnlocked(this);
        Assert.isTrue((layout == VERTICAL) || (layout == HORIZONTAL),
                      "Invalid layout code passed to setLayout");
        m_layout = layout;
    }

    /**
     * This method is part of a mechanism to freakishly allow List's to be used
     * as parent classes for components that do not have their state params
     * registered with the page. An example of a situation like this is Form
     * ErrorDisplay being used in a Metaform
     */
    public void setStateParamsAreRegistered(boolean val) {
        m_stateParamsAreRegistered = val;
    }

    public boolean getStateParamsAreRegistered() {
        return m_stateParamsAreRegistered;
    }

    /**
     * Returns the renderer currently used for rendering list items.
     *
     * @return the current list cell renderer.
     *
     * @see #setCellRenderer setCellRenderer
     * @see com.arsdigita.bebop.list.ListCellRenderer
     */
    public final ListCellRenderer getCellRenderer() {
        return m_renderer;
    }

    /**
     * Sets the cell renderer to be used when generating output with or
     * {@link #generateXML generateXML}.
     *
     * @param r a <code>ListCellRenderer</code> value
     *
     * @pre r != null
     * @pre ! isLocked()
     * @see com.arsdigita.bebop.list.ListCellRenderer
     */
    public final void setCellRenderer(ListCellRenderer r) {
        Assert.isUnlocked(this);
        m_renderer = r;
    }

    /**
     * Returns the model builder currently used to build each request-specific
     * {@link ListModel}.
     *
     * @return a <code>ListModelBuilder</code> value.
     *
     * @see #setModelBuilder setModelBuilder
     * @see ListModelBuilder
     */
    public final ListModelBuilder getModelBuilder() {
        return m_modelBuilder;
    }

    /**
     * Sets the model builder used to build each request-specific
     * {@link ListModel}.
     *
     * @param b a <code>ListModelBuilder</code> value
     *
     * @pre ! isLocked()
     * @see ListModelBuilder
     */
    public final void setModelBuilder(ListModelBuilder b) {
        Assert.isUnlocked(this);
        m_modelBuilder = b;
    }

    /**
     * Sets the empty view component, which is shown if there are no items in
     * the list. This component must be stateless. For example, it could be an
     * Image or a Label.
     *
     * @param c the new empty view component
     */
    public final void setEmptyView(Component c) {
        Assert.isUnlocked(this);
        m_emptyView = c;
    }

    /**
     * Gets the empty view component. The empty view component is shown if there
     * are no items in the list.
     *
     * @return the empty view component.
     */
    public final Component getEmptyView() {
        return m_emptyView;
    }

    /**
     * Initialize the private <code>m_model</code> variable. The initial value
     * is what the model builder returns for the state.
     */
    private void initListModel() {
        m_model = new RequestLocal() {

            protected Object initialValue(PageState s) {
                return getModelBuilder().makeModel(List.this, s);
            }

        };
    }

    /**
     * Gets the list model used in processing the request represented by
     * <code>state</code>.
     *
     * @param state the state of the current request
     *
     * @return the list model used in processing the request represented by
     *         <code>state</code>.
     */
    public ListModel getModel(PageState state) {
        return (ListModel) m_model.get(state);
    }

    /**
     * Sets the list to use for the values in <code>values</code>. Each {@link
     * ListModel} that is built will iterate through the entries of the object,
     * returning the objects from calls to {@link ListModel#getElement} and the
     * corresponding index, which is converted to a <code>String</code> from
     * calls to {@link ListModel#getKey}.
     *
     * @param values an array of items
     *
     * @pre values != null
     * @pre ! isLocked()
     */
    public void setListData(Object[] values) {
        Assert.isUnlocked(this);
        m_modelBuilder = new ArrayListModelBuilder(values);
    }

    /**
     * Sets the list to use the entries in <code>map</code>. Each {@link
     * ListModel} that is built will iterate through the entries in
     * <code>map</code> in the order in which they are returned by
     * <code>map.entrySet().iterator()</code>. Calls to {@link
     * ListModel#getElement} return one value in <code>map</code>. Calls to
     * {@link ListModel#getElement} return the corresponding key, which is
     * converted to a <code>String</code> by calling <code>toString()</code> on
     * the key.
     *
     * @param map a key-value mapping for the list items
     *
     * @pre map != null
     * @pre ! isLocked()
     */
    public void setListData(Map map) {
        Assert.isUnlocked(this);
        m_modelBuilder = new MapListModelBuilder(map);
    }

    /**
     * Gets the selection model. The model keeps track of which list item is
     * currently selected, and can be used to manipulate the selection
     * programmatically.
     *
     * @return the model used by the list to keep track of the selected list
     *         item.
     */
    public final SingleSelectionModel getSelectionModel() {
        return m_selection;
    }

    /**
     * Sets the selection model that the list uses to keep track of the
     * currently selected list item.
     *
     * @param m the new selection model
     *
     * @pre m != null
     * @pre ! isLocked()
     */
    public final void setSelectionModel(SingleSelectionModel m) {
        Assert.isUnlocked(this);
        if (m_changeListener != null) {
            // Transfer the change listener
            m_selection.removeChangeListener(m_changeListener);
            m.addChangeListener(m_changeListener);
        }
        m_selection = m;
    }

    /**
     * Gets the key for the selected list item. This will only be a valid key if
     * {@link #isSelected isSelected} is <code>true</code>.
     *
     * @param state the state of the current request
     *
     * @return the key for the selected list item.
     *
     * @pre isSelected(state)
     */
    public Object getSelectedKey(PageState state) {
        return m_selection.getSelectedKey(state);
    }

    /**
     * Sets the selection to the one with the specified key. If <code>key</code>
     * was not already selected, fires the {@link
     * ChangeEvent}.
     *
     * @param state the state of the current request
     * @param key   the key for the selected list item
     *
     * @see #fireStateChanged fireStateChanged
     */
    public void setSelectedKey(PageState state, String key) {
        m_selection.setSelectedKey(state, key);
    }

    /**
     * Returns <code>true</code> if one of the list items is currently selected.
     *
     * @param state the state of the current request
     *
     * @return <code>true</code> if one of the list items is selected
     *         <code>false</code> otherwise.
     */
    public boolean isSelected(PageState state) {
        return m_selection.isSelected(state);
    }

    /**
     * Clears the selection in the request represented by <code>state</code>.
     *
     * @param state the state of the current request
     *
     * @post ! isSelected(state)
     */
    public void clearSelection(PageState state) {
        m_selection.clearSelection(state);
    }

    /**
     * Creates the change listener that is used for forwarding change events
     * fired by the selection model to change listeners registered with the
     * list. The returned change listener refires the event with the list,
     * rather than the selection model, as source.
     *
     * @return the change listener used internally by the list.
     */
    protected ChangeListener createChangeListener() {
        return new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                fireStateChanged(e.getPageState());
            }

        };
    }

    /**
     * Adds a change listener. A change event is fired whenever the selected
     * list item changes during the processing of a request. The change event
     * that listeners receive names the list as the source.
     *
     * @param l the change listener to run when the selected item changes in a
     *          request
     *
     * @pre ! isLocked()
     */
    public void addChangeListener(ChangeListener l) {
        Assert.isUnlocked(this);
        if (m_changeListener == null) {
            m_changeListener = createChangeListener();
            m_selection.addChangeListener(m_changeListener);
        }
        m_listeners.add(ChangeListener.class, l);
    }

    /**
     * Removes a change listener. The listener should have been previously added
     * with {@link #addChangeListener addChangeListener}, although no error is
     * signalled if the change listener is not found among the list's listeners.
     *
     * @param l the change listener to remove from the list
     */
    public void removeChangeListener(ChangeListener l) {
        Assert.isUnlocked(this);
        m_listeners.remove(ChangeListener.class, l);
    }

    /**
     * Fires a change event to signal that the selected list item has changed in
     * the request represented by <code>state</code>. The source of the event is
     * the list.
     *
     * @param state the state of the current request
     */
    protected void fireStateChanged(PageState state) {
        Iterator i = m_listeners.getListenerIterator(ChangeListener.class);
        ChangeEvent e = null;

        while (i.hasNext()) {
            if (e == null) {
                e = new ChangeEvent(this, state);
            }
            ((ChangeListener) i.next()).stateChanged(e);
        }
    }

    // Action events
    /**
     * Adds an action listener. This method is run whenever
     * {@link #respond respond} is called on the list. This gives clients a way
     * to track mouse clicks received by the list.
     *
     * @param 1 the action listener to add
     *
     * @pre l != null
     * @pre ! isLocked()
     * @see #respond respond
     */
    public void addActionListener(ActionListener l) {
        Assert.isUnlocked(this);
        m_listeners.add(ActionListener.class, l);
    }

    /**
     * Removes a previously added action listener.
     *
     * @param 1 the action listener to remove
     *
     * @see #addActionListener addActionListener
     */
    public void removeActionListener(ActionListener l) {
        Assert.isUnlocked(this);
        m_listeners.remove(ActionListener.class, l);
    }

    /**
     * Fires an action event signalling that the list received the request
     * submission. All registered action listeners are run. The source of the
     * event is the list.
     *
     * @param state the state of the current request
     *
     * @pre state != null
     * @see #respond respond
     */
    protected void fireActionEvent(PageState state) {
        Iterator i = m_listeners.getListenerIterator(ActionListener.class);
        ActionEvent e = null;

        while (i.hasNext()) {
            if (e == null) {
                e = new ActionEvent(this, state);
            }
            ((ActionListener) i.next()).actionPerformed(e);
        }
    }

    // ListModelBuilder for maps
    /**
     * Build list models from a map. The list models use the result of
     * <code>toString()</code> called on the key of the map entries as their
     * keys and return the associated value as the element for the list items
     * the list model iterates over.
     */
    private static class MapListModelBuilder implements ListModelBuilder {

        private Map m_map;
        private boolean m_locked;

        public MapListModelBuilder() {
            this(Collections.EMPTY_MAP);
        }

        public MapListModelBuilder(Map m) {
            m_map = m;
        }

        public ListModel makeModel(List l, PageState state) {
            return new ListModel() {

                private Iterator i = m_map.entrySet().iterator();
                private Map.Entry e = null;

                public boolean next() {
                    if (!i.hasNext()) {
                        e = null;
                        return false;
                    }
                    e = (Map.Entry) i.next();
                    return true;
                }

                public Object getElement() {
                    checkState();
                    return e.getValue();
                }

                public String getKey() {
                    checkState();
                    return e.getKey().toString();
                }

                private void checkState() {
                    if (e == null) {
                        throw new IllegalStateException(
                            "No valid current item. "
                                + "Model is either before first item or after last item");
                    }
                }

            };
        }

        public void lock() {
            m_locked = true;
        }

        public final boolean isLocked() {
            return m_locked;
        }

    }

    // ListModelBuilder for arrays
    /**
     * Build list models from an array of values. The list models use the index
     * of the array entries, converted to a <code>String</code>, as the key for
     * the list items and the array values as their elements.
     */
    private static class ArrayListModelBuilder implements ListModelBuilder {

        private Object[] m_values;
        private boolean m_locked;

        public ArrayListModelBuilder() {
            this(new Object[0]);
        }

        public ArrayListModelBuilder(Object[] values) {
            m_values = values;
        }

        public ListModel makeModel(List l, PageState state) {
            return new ListModel() {

                private int i = -1;

                public boolean next() {
                    i += 1;
                    return (i < m_values.length);
                }

                public Object getElement() {
                    checkState();
                    return m_values[i];
                }

                public String getKey() {
                    checkState();
                    return String.valueOf(i);
                }

                private void checkState() {
                    if (i < 0) {
                        throw new IllegalStateException(
                            "Before first item. Call next() first.");
                    }
                    if (i >= m_values.length) {
                        throw new IllegalStateException(
                            "After last item. Model exhausted.");
                    }
                }

            };
        }

        public void lock() {
            m_locked = true;
        }

        public final boolean isLocked() {
            return m_locked;
        }

    }

    /**
     * A {@link ListModel} that has no rows.
     */
    public static final ListModel EMPTY_MODEL = new ListModel() {

        public boolean next() {
            return false;
        }

        public String getKey() {
            throw new IllegalStateException("ListModel is empty");
        }

        public Object getElement() {
            throw new IllegalStateException("ListModel is empty");
        }

    };

}
