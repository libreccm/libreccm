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

import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.util.Assert;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * ModalContainer is a container that manages visibility for a set of
 * components. It allows only one of its children to be visible. One of its
 * children can be selected as the default visible component. If none is
 * selected the child with index equal to zero is used. The modal container
 * sets the appropriate default and PageState-based visibility for its
 * children.
 *
 * @author Archit Shah
 * @version $Id$
 **/
public class ModalContainer extends SimpleContainer implements Resettable {

    private static final Logger s_log = Logger.getLogger(ModalContainer.class);

    private int m_default = 0;

    private ArrayList m_changeListeners = new ArrayList();

    public ModalContainer() { 
        super();
    }

    public ModalContainer(String tagName,
                          String xmlns) {
        super(tagName, xmlns);
    }

    /**
     * Registers state parameters for the page with its model.
     *
     * Used here to set the visibility of the component.
     *
     * The super class' method is empty, so the rule "Always call 
     * <code>super.register</code> when you override <code>register</code> 
     * doessn't apply here.
     *
     * @pre p != null 
     * @param p 
     */
    @Override
    public void register(Page p) {
        Assert.isUnlocked(this);

        Iterator it = children();
        while (it.hasNext()) {
            // guaranteed to have at least one child
            Component child = (Component) it.next();
            if (m_default != indexOf(child)) {
                p.setVisibleDefault(child, false);
            }
        }
    }

    public void setDefaultComponent(Component c) {
        Assert.isUnlocked(this);

        if (!contains(c)) {
            add(c);
        }

        m_default = indexOf(c);
    }

    public Component getDefaultComponent() {
        return get(m_default);
    }

    public void setVisibleComponent(PageState state, Component c) {
        s_log.debug("changing visibility");

        Component old = getVisibleComponent(state);
        try {
            old.setVisible(state, false);
        } catch (NullPointerException e) {
            // rare(?) situation where something messes with the
            // visibility of the components.  don't have to do anything
        }
        c.setVisible(state, true);
        if (old!=c) {
            Iterator listeners = m_changeListeners.iterator();
            while (listeners.hasNext()) {
                ChangeListener l = (ChangeListener) listeners.next();
                l.stateChanged(new ChangeEvent(this, state));
            }
        }
    }

    public Component getVisibleComponent(PageState state) {
        Iterator it = children();
        while (it.hasNext()) {
            Component c = (Component) it.next();
            if (c.isVisible(state)) { return c; }
        }

        return null;
    }

    /**
     * Makes the next component in a wizard sequence visible while hiding all
     * other components.
     **/
    public void next(PageState state) {
        setVisibleComponent(state,
                            get(indexOf(getVisibleComponent(state)) + 1));
    }

    /**
     * Makes the previous component in a wizard sequence visible while hiding
     * all other components.
     **/
    public void previous(PageState state) {
        setVisibleComponent(state,
                            get(indexOf(getVisibleComponent(state)) - 1));
    }

    /**
     * Makes the component specified by index visible
     *
     * @param state
     * @param index 0 based index of component
     */
    public void jumpTo (PageState state, int index) {
	setVisibleComponent(state, get(index));
    }

    /**
     * Makes the specified component visible
     *
     */
    public void jumpTo (PageState state, Component comp) {
    	setVisibleComponent(state, get(indexOf(comp)));
    }


    /**
     * Resets the container to display the default component.
     **/
    public void reset(PageState state) {
        setVisibleComponent(state, getDefaultComponent());
    }

    /**
     * Adds a listener that is called whenever this container's mode
     * (i.e., visible component) is changed using setVisibleComponent().
     **/
    public void addModeChangeListener(ChangeListener cl) {
        Assert.isUnlocked(this);

        m_changeListeners.add(cl);
    }
}
