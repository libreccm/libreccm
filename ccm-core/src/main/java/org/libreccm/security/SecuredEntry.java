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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <K>
 * @param <V>
 */
class SecuredEntry<K, V extends CcmObject> implements Map.Entry<K, V> {

    private final Map.Entry<K, V> entry;
    private final SecuredHelper<V> securedHelper;

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
