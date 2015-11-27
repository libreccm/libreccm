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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <K>
 * @param <V>
 */
public class SecuredSortedMap<K, V extends CcmObject>
    extends SecuredMap<K, V>
    implements SortedMap<K, V> {

    private final SortedMap<K, V> sortedMap;
    private final Class<V> clazz;
    private final String requiredPrivilege;

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
