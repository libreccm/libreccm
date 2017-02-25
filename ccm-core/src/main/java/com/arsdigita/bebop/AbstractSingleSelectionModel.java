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
 * <code>Lockable</code>. Those wishing to define a SingleSelectionModel will
 * ordinarily want to extend this class.
 *
 * jensp: Added generics and Java 8 streams instead of using an iterator.
 *
 * @param <T> The type managed by the parameter model.
 *
 * @author Unknown
 * @author Jens Pelzetter (jensp)
 */
public abstract class AbstractSingleSelectionModel<T>
    implements SingleSelectionModel<T>, Lockable {

    private final EventListenerList m_listeners;
    private boolean m_locked;

    /**
     * Creates a new AbstractSingleSelectionModel.
     */
    public AbstractSingleSelectionModel() {
        m_listeners = new EventListenerList();
    }

    /**
     * Returns <code>true</code> if there is a selected element.
     *
     * @param state the state of the current request
     *
     * @return <code>true</code> if there is a selected component;
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean isSelected(final PageState state) {
        return getSelectedKey(state) != null;
    }

    @Override
    public abstract T getSelectedKey(final PageState state);

    @Override
    public abstract void setSelectedKey(final PageState state, final T key);

    @Override
    public void clearSelection(final PageState state) {
        setSelectedKey(state, null);
    }

    // Selection change events
    @Override
    public void addChangeListener(final ChangeListener changeListener) {
        Assert.isUnlocked(this);
        m_listeners.add(ChangeListener.class, changeListener);
    }

    @Override
    public void removeChangeListener(final ChangeListener changeListener) {
        Assert.isUnlocked(this);
        m_listeners.remove(ChangeListener.class, changeListener);
    }

    protected void fireStateChanged(final PageState state) {
        final ChangeEvent event = new ChangeEvent(this, state);
        final Iterator<ChangeListener> iterator = m_listeners
            .getListenerIterator(ChangeListener.class);
        while(iterator.hasNext()) {
            iterator.next().stateChanged(event);
        }
    }

    // implement Lockable
    @Override
    public void lock() {
        m_locked = true;
    }

    @Override
    public final boolean isLocked() {
        return m_locked;
    }

}
