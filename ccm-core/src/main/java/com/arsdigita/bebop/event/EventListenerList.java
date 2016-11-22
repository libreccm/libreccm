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
package com.arsdigita.bebop.event;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Convenience extensions to {@link javax.swing.event.EventListenerList
 * Swing's <code>EventListenerList</code>}.
 *
 */
public class EventListenerList extends javax.swing.event.EventListenerList {

    private static final long serialVersionUID = -1930203818146602205L;

    /**
     * Append all the event listeners from <code>l</code>.
     *
     * @param list The list of listeners to copy from
     *
     * @pre l != null
     */
    public void addAll(final EventListenerList list) {

        if ( list.listenerList.length == 0 ) {
            return;
        }

        Object[] tmp = new Object[listenerList.length + list.listenerList.length];
        System.arraycopy(listenerList, 0, tmp, 0, listenerList.length);
        System.arraycopy(list.listenerList, 0,
                         tmp, listenerList.length, list.listenerList.length);
        listenerList = tmp;
    }

    /**
     * Return an iterator over all event listeners of class <code>t</code>.
     * This iterator replaces the for loop mentioned in the documentation for
     * {@link javax.swing.event.EventListenerList Swing's
     * <code>EventListenerList</code>}.
     *
     * @param <T>
     * @param type The class of the event listeners that should be returned
     * @return 
     *
     * @pre t != null
     * */
    public <T> Iterator<T> getListenerIterator(final Class<T> type) {
        return new EventListenerIterator<>(type);
    }

    private class EventListenerIterator<T> implements Iterator<T> {

        /**
         * The listener we will return with the next call to next().
         * listener[_next] is always a class object of type t, unless all
         * matching listeners have been returned, in which case _next
         * is -1
         * */
        private final int count;
        private int next;
        private final Class<T> type;

        EventListenerIterator(Class<T> type) {

            count = getListenerList().length;
            next = -2;
            this.type = type;
            findNext();
        }

        @Override
        public boolean hasNext() {
            return (next < count);
        }

        @Override
        @SuppressWarnings("unchecked")
        public T next() throws NoSuchElementException {
            if ( ! hasNext() ) {
                throw new NoSuchElementException("Iterator exhausted");
            }
            int result = next;
            findNext();
            return (T) getListenerList()[result+1];
        }

        @Override
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Removal not supported");
        }

        /**
         * Advance <code>_next</code> so that either <code>_next == -1</code>
         * if all listeners of class <code>_t</code> have been returned in the
         * enclosing <code>EventListenerList</code>, or that
         * <code>getListenersList()[_next] == _t</code> and
         * <code>getListenersList()[_next+1]</code> (the corresponding listener
         * object) has not been returned yet by <code>next()</code>.
         * */
        private void findNext() {

            for (int i = next+2; i<count; i+=2) {

                if (getListenerList()[i] == type) {
                    next = i;
                    return;
                }
            }
            next = count;
        }
    }
}
