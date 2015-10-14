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

import static com.arsdigita.bebop.Component.*;

import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.util.Assert;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.xml.Element;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;

/* FIXME: Add methods for using images in the tab strip */

/**
 * A tabbed pane that lets the user switch between components by
 * clicking on a given title in the tab strip.
 * <p>
 * Tabs (components) are added using the {@link #addTab addTab} method. Each
 * entry consists of a label (which is a string) and the {@link Component}
 * that is displayed if the user clicks on the label.
 * <p>
 * There is always exactly one component that is currently visible, the component
 * that is returned by {@link #getCurrentPane}. Without user interaction,
 * this is the default pane -- that was set by {@link #setDefaultPane} -- or, if
 * none has been set, the first component that was added to the <code>TabbedPane</code>.
 * <p>
 *
 * @author David Lutterkort 
 * @author Stanislav Freidin 
 * @author Uday Mathur 
 * @version $Id$
 */
public class TabbedPane extends SimpleContainer {

    private static final String CURRENT_PANE = "pane";
    /**
     * The name for the event to change the selected pane.
     * The value is the index of the pane
     */
    private static final String SELECT_EVENT = "select";

    private Pane m_defaultPane;
    private IntegerParameter m_currentPaneParam;
    private List m_actionListeners;

    private static final Logger s_log =
        Logger.getLogger(TabbedPane.class.getName());

    /**
     * Constructs an empty TabbedPane.
     */
    public TabbedPane() {
        m_currentPaneParam = new IntegerParameter(CURRENT_PANE);
    }

    /**
     * Registers with the specified root container. Adds a state
     * parameter to keep track of the visible component to the page.
     * @param p the root container to register with
     * @pre p != null
     */
    public void register(Page p) {
        Assert.isUnlocked(this);

        p.addComponentStateParam(this, m_currentPaneParam);
        // if there is no default pane, then set it to the first one
        // in the list
        Iterator i = children();
        if (!i.hasNext()) {
            s_log.warn("TabbedPane registered with no panes");
        } else if (m_defaultPane == null) {
            setDefaultPaneIndex(0);
        }
        while (i.hasNext()) {
            Pane pane = (Pane) i.next();
            p.setVisibleDefault(pane.getComponent(), pane == m_defaultPane);
        }
    }

    /**
     * Adds a new pane to the dialog. Assigns a rather unhelpful default label
     * (the pane number) to the component. Use {@link #addTab addTab}
     * instead.
     *
     * @pre pc != null
     */
    public void add(Component pc) {
        addTab(String.valueOf(size()), pc);
    }

    /**
     * Adds a new pane with layout constraints to the dialog.  Ignores
     * the constraints.  Assigns a rather unhelpful default label
     * (the pane number) to the component.  Use {@link #addTab
     * addTab} instead.
     *
     * @pre pc != null */
    public void add(Component pc, int constraints) {
        add(pc);
    }

    /**
     * Adds a tab and its associated component.
     * @param label the text to display in the tab strip
     * @param c the component to display when the user clicks on the
     *       <code>label</code> in the tab strip
     *
     * @pre label != null && c != null
     */
    public void addTab(Component label, Component c) {
        Assert.isUnlocked(this);
        super.add(new Pane(label, c));
    }

    /**
     * Adds a tab and its associated component.
     * @param label the text to display in the tab strip
     * @param c the component to display when the user clicks on the
     *       <code>label</code> in the tab strip
     *
     * @pre label != null && c != null
     */
    public void addTab(String label, Component c) {
        addTab(new Label(label), c);
    }

    /**
     * Adds an <code>ActionListener</code>, which is run whenever {@link
     * #respond respond} is called.
     * @param 1 the action listener
     *
     * @pre l != null
     * @pre ! isLocked()
     * @see #respond respond
     */
    public void addActionListener(ActionListener l) {
        Assert.isUnlocked(this);
        if ( m_actionListeners == null ) {
            m_actionListeners = new ArrayList();
        }
        m_actionListeners.add(l);
    }

    /**
     * Removes a previously added <code>ActionListener</code>.
     * @param 1 the action listener to remove
     * @see #addActionListener addActionListener
     */
    public void removeActionListener(ActionListener l) {
        Assert.isUnlocked(this);
        if ( m_actionListeners == null ) {
            return;
        }
        m_actionListeners.remove(l);
    }

    /**
     * Fires an <code>ActionEvent</code>. All registered
     * <code>ActionListener</code>s are run. The source of the event is the
     * <code>TabbedPane</code>.
     * @param state the current page state
     * @pre state != null
     * @see #respond respond
     */
    protected void fireActionEvent(PageState state) {
        ActionEvent e = null;
        if (m_actionListeners == null) {
            return;
        }
        for (Iterator i=m_actionListeners.iterator(); i.hasNext(); ) {
            if ( e == null ) {
                e = new ActionEvent(this, state);
            }
            ((ActionListener) i.next()).actionPerformed(e);
        }
    }

    /**
     * Sets the index of the default pane, which is visible until the user
     * clicks on another label in the tab strip.
     * @param i the index of the default pane
     */
    protected void setDefaultPaneIndex(int i) {
        m_currentPaneParam.setDefaultValue(new Integer(i));
        m_defaultPane = (Pane)get(i);
    }

    /**
     * Sets the default pane, which is visible until the user
     * clicks on another label in the tab strip.
     * @param pane the component to display as the default pane
     *
     * @pre findPane(pane) != -1
     */
    public void setDefaultPane(Component pane)
        throws IllegalArgumentException {
        Assert.isUnlocked(this);

        setDefaultPaneIndex(findPaneSafe(pane));
    }

    /**
     * Show or hide a particular tab
     *
     * @param s the page state
     * @param i the index of the tab
     * @param v if true, shows the tab. Otherwise, hides the tab
     */
    public void setTabVisible(PageState s, int i, boolean v) {
        get(i).setVisible(s, v);
    }

    /**
     * Show or hide a particular tab
     *
     * @param s the page state
     * @param c the body of the tab
     * @param v if true, shows the tab. Otherwise, hides the tab
     */
    public void setTabVisible(PageState s, Component c, boolean v) {
        int i = findPaneSafe(c);
        setTabVisible(s, i, v);
    }

    /**
     * Determine if a particular tab is visible
     *
     * @param s the page state
     * @param i the index of the tab
     */
    public boolean isTabVisible(PageState s, int i) {
        return get(i).isVisible(s);
    }

    /**
     * Determine if a particular tab is visible
     *
     * @param s the page state
     * @param c the body of the tab
     */
    public boolean isTabVisible(PageState s, Component c) {
        int i = findPaneSafe(c);
        return isTabVisible(s, i);
    }

    /**
     * Find the pane whose body is the specified component
     * @param c the component
     * @return the pane index on success, -1 if no such pane exists
     */
    protected int findPane(Component c) {
        int index = 0;
        for(Iterator i = children(); i.hasNext(); index++) {
            Pane p = (Pane)i.next();
            if(p.getComponent() == c)
                return index;
        }

        return -1;
    }

    private int findPaneSafe(Component c) {
        int i = findPane(c);
        if ( i == -1 ) {
            throw new IllegalArgumentException
                ("Pane not part of this tabbed dialog");
        }

        return i;
    }


    /**
     * Gets the default pane. If no default pane has been set explicitly, the
     * first pane is returned.
     *
     * @return the default pane, or <code>null</code> if there are no
     * panes.
     */
    public Component getDefaultPane() {
        return m_defaultPane.getComponent();
    }

    /**
     * Gets the pane with the specified label.
     * @return the pane with the specified label, or <code>null</code>
     * if a pane with that label does not exist.
     */
    public Component getPane(Component label) {
        for (Iterator i = children(); i.hasNext();) {
            Pane p = (Pane) i.next();
            if ( p.getLabel().equals(label) ) {
                return p.getComponent();
            }
        }
        return null;
    }

    /**
     * Gets the pane with the specified key in its label.
     * Returns null if a pane with that label does not exist.
     * This function exists for backward compatibility.
     * @return the pane with the specified label, or <code>null</code>
     * if a pane with that label does not exist.
     */
    public Component getPane(String label) {

        for (Iterator i = children(); i.hasNext();) {
            Pane p = (Pane) i.next();
            Component pLabel = p.getLabel();
            if (pLabel instanceof Label
                && ((Label)pLabel).getLabel().equals(label) ) {
                return p.getComponent();
            }
        }
        return null;
    }

    /**
     * Gets the currently visible pane.
     *
     * @pre data != null
     */
    public Component getCurrentPane(PageState data) {
        return getCurrent(data).getComponent();
    }

    /**
     * Get the currently visible <code>Pane</code>, the tab label together
     * with its component.
     */
    private Pane getCurrent(PageState data) {
        Integer i = (Integer) data.getValue(m_currentPaneParam);
        if (i == null) {
            if (m_defaultPane!=null) {

                return m_defaultPane;
            } else {
                return (Pane)get(0);
            }
        }
        return (Pane)get(i.intValue());
    }

    public void setSelectedIndex(PageState state, int index) {
        if ( index != getSelectedIndex(state) ) {
            getCurrentPane(state).setVisible(state, false);
            state.setValue(m_currentPaneParam, new Integer(index));
            getCurrentPane(state).setVisible(state, true);
        }
    }

    public int getSelectedIndex(PageState state) {
        Integer current = (Integer) state.getValue(m_currentPaneParam);
        if ( current == null ) {
            return -1;
        }
        return current.intValue();
    }


    /**
     * Builds a DOM representing the header for the tab strip. Marks the current pane.
     */
    protected void generateTabs(PageState data, Element parent) {
        Element strip = parent.newChildElement("bebop:tabStrip", BEBOP_XML_NS);
        exportAttributes(strip);

        Pane current = getCurrent(data);
        strip.addAttribute("selected",current.getComponent().getClass().getName());
        Iterator tabs;
        int i;
        for (tabs = children(), i = 0; tabs.hasNext(); i++) {
            Pane pane = (Pane)tabs.next();
            // Skip hidden tabs
            if(!pane.isVisible(data)) continue;

            data.setControlEvent(this, SELECT_EVENT, String.valueOf(i));

            Element tab = strip.newChildElement("bebop:tab", BEBOP_XML_NS);
            if (pane == current) {
                tab.addAttribute("current", "t");
            } else {
                try {
                    tab.addAttribute("href", data.stateAsURL());
                } catch (java.io.IOException ioe) {
                    // stateAsURL failed => this node gets neither href nor current
                    //TODO cat.error("cannot get stateAsURL from "+data);
                }
            }
            String key = ((Label) pane.getLabel()).getGlobalizedMessage().getKey();
            tab.addAttribute("key", key.substring(key.lastIndexOf(".") + 1));
            pane.getLabel().generateXML(data, tab);
        }
        data.clearControlEvent();
    }

    /**
     * Services the request by building a DOM tree with the tabs
     * themselves and then the included page.
     * <p>Generates a DOM fragment:
     * <p><code><pre>
     * &lt;bebop:tabbedPane>
     *  &lt;bebop:tabStrip>
     *   &lt;bebop:tab [href="..."] [current="t|f"]> .. label .. &lt;/bebop:tab>
     *   &lt;bebop:tab [href="..."] [current="t|f"]> .. label .. &lt;/bebop:tab>
     *   &lt;bebop:tab [href="..."] [current="t|f"]> .. label .. &lt;/bebop:tab>
     *  &lt;/bebop:tabStrip>
     *  &lt;bebop:currentPane>
     *    ... contentes ..
     *  &lt;/bebop:currentPane>
     * &lt;/bebop:tabbedPane>
     * </pre></code>
     */
    public void generateXML(PageState state, Element parent) {
        if ( isVisible(state) && !isEmpty()) {
            Element tabbed = parent.newChildElement("bebop:tabbedPane", BEBOP_XML_NS);
            generateTabs(state, tabbed);
            exportAttributes(tabbed);

            Element pane = tabbed.newChildElement("bebop:currentPane", BEBOP_XML_NS);
            exportAttributes(pane);
            getCurrentPane(state).generateXML(state, pane);
        }
    }

    /**
     * Notifies the <code>TabbedPane</code> that one of the tabs has been
     * selected. Changes the currently visible pane and runs all the {@link
     * ActionListener ActionListeners}.
     * <p>
     * The <code>respond</code> method on the now-visible component is
     * <em>not</em> called.
     *
     * @pre state != null
     */
    public void respond(PageState state)
        throws ServletException
    {
        String event = state.getControlEventName();

        if ( SELECT_EVENT.equals(event)) {
            String value = state.getControlEventValue();
            setSelectedIndex(state, Integer.parseInt(value));
        } else {
            throw new ServletException("Received unknown control event " + event);
        }
        fireActionEvent(state);
    }

    /**
     * Associates a label with the component
     */
    private class Pane extends SimpleContainer {
        private Component m_label;
        private Component m_component;

        public Pane(Component label, Component c) {
            m_label = label;
            super.add(label);
            m_component = c;
            super.add(c);
        }

        public final Component getLabel() {
            return m_label;
        }

        public final Component getComponent() {
            return m_component;
        }
    }
}
