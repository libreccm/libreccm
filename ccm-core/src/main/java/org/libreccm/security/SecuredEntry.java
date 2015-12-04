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

import java.util.Map;

/**
 * A decorator for {@link Map.Entry} which checks if the current subject is
 * permitted to access the value before returning it. If the current subject
 * is not permitted to access the value it is replaced with an virtual 
 * <em>Access Denied</em> which is an object of the same class as the value.
 * 
 * This class is not intended for direct use and is therefore only accessible 
 * from the {@code org.libreccm.security} package. The class is used by the
 * {@link SecuredMap}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <K> The type of the key of the entry.
 * @param <V> The type of the value of the entry.
 */
class SecuredEntry<K, V extends CcmObject> implements Map.Entry<K, V> {

    /**
     * The decorated entry.
     */
    private final Map.Entry<K, V> entry;
    /**
     * {@link SecuredHelper} for creating the virtual <em>Access denied</em> 
     * object. Provided by the {@link SecuredMap} which creates the 
     * {@code SecuredEntry}.
     */
    private final SecuredHelper<V> securedHelper;

    /**
     * Creates a new secured entry.
     * 
     * @param entry The entry to secure.
     * @param securedHelper The {@link SecuredHelper} for creating the 
     * virtual <em>Access denied</em> object.
     */
    public SecuredEntry(final Map.Entry<K, V> entry,
                        final SecuredHelper<V> securedHelper) {
        this.entry = entry;
        this.securedHelper = securedHelper;
    }

    @Override
    public K getKey() {
        return entry.getKey();
    }

    @Override
    public V getValue() {
        return securedHelper.canAccess(entry.getValue());
    }

    @Override
    public V setValue(final V value) {
        return securedHelper.canAccess(entry.setValue(value));
    }

}
