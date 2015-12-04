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

import java.util.Comparator;
import java.util.SortedMap;

/**
 * A decorator for {@link SortedMap} which checks if the current subject is
 * permitted to access the values from the decorated sorted map before returning
 * them.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @param <K> Type of the keys.
 * @param <V> Type of the values.
 */
public class SecuredSortedMap<K, V extends CcmObject>
    extends SecuredMap<K, V>
    implements SortedMap<K, V> {

    /**
     * The decorated sorted map.
     */
    private final SortedMap<K, V> sortedMap;
    /**
     * Class of the values of the decorated sorted map.
     */
    private final Class<V> clazz;
    /**
     * Privilege required to access the values of the decorated sorted map.
     */
    private final String requiredPrivilege;

    /**
     * Creates new secured sorted map.
     *
     * @param sortedMap         The map to secure.
     * @param clazz             Class of the values.
     * @param requiredPrivilege Privilege required to access the values of the
     *                          secured sorted map.
     */
    public SecuredSortedMap(final SortedMap<K, V> sortedMap,
                            final Class<V> clazz,
                            final String requiredPrivilege) {
        super(sortedMap, clazz, requiredPrivilege);
        this.sortedMap = sortedMap;
        this.clazz = clazz;
        this.requiredPrivilege = requiredPrivilege;
    }

    @Override
    public Comparator<? super K> comparator() {
        return sortedMap.comparator();
    }

    @Override
    public SortedMap<K, V> subMap(final K fromKey, final K toKey) {
        return new SecuredSortedMap<>(sortedMap.subMap(fromKey, toKey),
                                      clazz,
                                      requiredPrivilege);
    }

    @Override
    public SortedMap<K, V> headMap(final K toKey) {
        return new SecuredSortedMap<>(sortedMap.headMap(toKey),
                                      clazz,
                                      requiredPrivilege);
    }

    @Override
    public SortedMap<K, V> tailMap(final K fromKey) {
        return new SecuredSortedMap<>(sortedMap.tailMap(fromKey),
                                      clazz,
                                      requiredPrivilege);
    }

    @Override
    public K firstKey() {
        return sortedMap.firstKey();
    }

    @Override
    public K lastKey() {
        return sortedMap.lastKey();
    }

}
