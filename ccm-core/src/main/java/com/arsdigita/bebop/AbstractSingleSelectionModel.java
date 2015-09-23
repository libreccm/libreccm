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

import java.util.Iterator;

import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.EventListenerList;
import com.arsdigita.util.Assert;
import com.arsdigita.util.Lockable;

/**
 * A standard implementation of <code>SingleSelectionModel</code> and 
 * <code>Lockable</code>.  Those wishing to define a SingleSelectionModel 
 * will ordinarily want to extend this class.
 *
 * @version $Id: AbstractSingleSelectionModel.java 287 2005-02-22 00:29:02Z sskracic $
 */
public abstract class AbstractSingleSelectionModel
    implements SingleSelectionModel, Lockable {

    private EventListenerList m_listeners;
    private boolean m_locked;

    /** Creates a new AbstractSingleSelectionModel.
     */
    public AbstractSingleSelectionModel() {
        m_listeners = new EventListenerList();
    }

    /**
     * Returns <code>true</code> if there is a selected element.
     *
     * @param state the state of the current request
     * @return <code>true</code> if there is a selected component;
     * <code>false</code> otherwise.
     */
    public boolean isSelected(PageState state) {
        return getSelectedKey(state) != null;
    }

    public abstract Object getSelectedKey(PageState state);

    public abstract void setSelectedKey(PageState state, Object key);

    public void clearSelection(PageState state) {
        setSelectedKey(state, null);
    }

    // Selection change events

    public void addChangeListener(ChangeListener l) {
        Assert.isUnlocked(this);
        m_listeners.add(ChangeListener.class, l);
    }

    public void removeChangeListener(ChangeListener l) {
        Assert.isUnlocked(this);
        m_listeners.remove(ChangeListener.class, l);
    }

    protected void fireStateChanged(PageState state) {
        Iterator i = m_listeners.getListenerIterator(ChangeListener.class);
        ChangeEvent e = null;

        while (i.hasNext()) {
            if ( e == null ) {
                e = new ChangeEvent(this, state);
            }
            ((ChangeListener) i.next()).stateChanged(e);
        }
    }

    // implement Lockable
    public void lock() {
        m_locked = true;
    }

    public final boolean isLocked() {
        return m_locked;
    }
}
