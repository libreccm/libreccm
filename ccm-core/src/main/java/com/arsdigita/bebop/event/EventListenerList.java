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
 * @version $Id$
 */
public class EventListenerList extends javax.swing.event.EventListenerList {

    /**
     * Append all the event listeners from <code>l</code>.
     *
     * @param l The list of listeners to copy from
     *
     * @pre l != null
     */
    public void addAll(EventListenerList l) {

        if ( l.listenerList.length == 0 )
            return;

        Object[] tmp = new Object[listenerList.length + l.listenerList.length];
        System.arraycopy(listenerList, 0, tmp, 0, listenerList.length);
        System.arraycopy(l.listenerList, 0,
                         tmp, listenerList.length, l.listenerList.length);
        listenerList = tmp;
    }

    /**
     * Return an iterator over all event listeners of class <code>t</code>.
     * This iterator replaces the for loop mentioned in the documentation for
     * {@link javax.swing.event.EventListenerList Swing's
     * <code>EventListenerList</code>}.
     *
     * @param t The class of the event listeners that should be returned
     *
     * @pre t != null
     * */
    public Iterator getListenerIterator(final Class t) {
        return new EventListenerIterator(t);
    }

    private class EventListenerIterator implements Iterator {

        /**
         * The listener we will return with the next call to next().
         * listener[_next] is always a class object of type t, unless all
         * matching listeners have been returned, in which case _next
         * is -1
         * */
        private int _count;
        private int _next;
        private Class _t;

        EventListenerIterator(Class t) {

            _count = getListenerList().length;
            _next = -2;
            _t = t;
            findNext();
        }

        public boolean hasNext() {
            return (_next < _count);
        }

        public Object next() throws NoSuchElementException {
            if ( ! hasNext() ) {
                throw new NoSuchElementException("Iterator exhausted");
            }
            int result = _next;
            findNext();
            return getListenerList()[result+1];
        }

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

            for (int i = _next+2; i<_count; i+=2) {

                if (getListenerList()[i] == _t) {
                    _next = i;
                    return;
                }
            }
            _next = _count;
        }
    }
}
