/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.libreccm.security;

import org.libreccm.core.CcmObject;

import java.util.Iterator;
import java.util.Map;

/**
 * A decorator for an iterator of {@link Map.Entry} objects which returns
 * {@link SecuredEntry} objects. Used by the {@link SecuredMap}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class SecuredEntryIterator<E extends Map.Entry<K, V>, K, V extends CcmObject>
    implements Iterator<E> {

    /**
     * The decorated iterator.
     */
    private final Iterator<E> iterator;
    /**
     * The {@link SecuredHelper} for creating the virtual <em>Access denied</em>
     * object. Provided by the {@link SecuredMap} creating the iterator.
     */
    private final SecuredHelper<V> securedHelper;

    /**
     * Creates a new secured iterator for entries.
     *
     * @param iterator      The iterator to secure.
     * @param securedHelper The {@link SecuredHelper} for creating the virtual
     *                      <em>Access denied</em> object.
     */
    public SecuredEntryIterator(final Iterator<E> iterator,
                                final SecuredHelper<V> securedHelper) {
        this.iterator = iterator;
        this.securedHelper = securedHelper;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    @SuppressWarnings("unchecked")
    public E next() {
        return (E) new SecuredEntry<>(iterator.next(), securedHelper);
    }

}
