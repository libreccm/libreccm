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

import java.util.NavigableMap;
import java.util.NavigableSet;

/**
 * A decorator for {@link NavigableMap} which checks if the current subject is
 * permitted to access the values of the decorated map before returning them.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * 
 * @param <K> Type of the keys. 
 * @param <V> Type of the values.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class SecuredNavigableMap<K, V extends CcmObject>
    extends SecuredSortedMap<K, V>
    implements NavigableMap<K, V> {

    /**
     * The decorated navigable map.
     */
    private final NavigableMap<K, V> navigableMap;
    /**
     * Class of the values.
     */
    private final Class<V> clazz;
    /**
     * The privilege required to access the values of the decorated map.
     */
    private final String requiredPrivilege;
    /**
     * {@link SecuredHelper} used by the decorator.
     */
    private final SecuredHelper<V> securedHelper;

    /**
     * Creates a new secured navigable map.
     * 
     * @param navigableMap The navigable map to secure.
     * @param clazz The class of the values of the navigable map.
     * @param requiredPrivilege The privilege required to access the objects of
     * the navigable map.
     */
    public SecuredNavigableMap(final NavigableMap<K, V> navigableMap,
                               final Class<V> clazz,
                               final String requiredPrivilege) {
        super(navigableMap, clazz, requiredPrivilege);
        this.navigableMap = navigableMap;
        this.clazz = clazz;
        this.requiredPrivilege = requiredPrivilege;
        securedHelper = new SecuredHelper<>(clazz, requiredPrivilege);
    }

    @Override
    public Entry<K, V> lowerEntry(final K key) {
        return new SecuredEntry<>(navigableMap.lowerEntry(key), securedHelper);
    }

    @Override
    public K lowerKey(final K key) {
        return navigableMap.lowerKey(key);
    }

    @Override
    public Entry<K, V> floorEntry(final K key) {
        return new SecuredEntry<>(navigableMap.floorEntry(key), securedHelper);
    }

    @Override
    public K floorKey(final K key) {
        return navigableMap.floorKey(key);
    }

    @Override
    public Entry<K, V> ceilingEntry(final K key) {
        return new SecuredEntry<>(navigableMap.ceilingEntry(key), securedHelper);
    }

    @Override
    public K ceilingKey(final K key) {
        return navigableMap.ceilingKey(key);
    }

    @Override
    public Entry<K, V> higherEntry(final K key) {
        return new SecuredEntry<>(navigableMap.higherEntry(key), securedHelper);
    }

    @Override
    public K higherKey(final K key) {
        return navigableMap.higherKey(key);
    }

    @Override
    public Entry<K, V> firstEntry() {
        return new SecuredEntry<>(navigableMap.firstEntry(), securedHelper);
    }

    @Override
    public Entry<K, V> lastEntry() {
        return new SecuredEntry<>(navigableMap.lastEntry(), securedHelper);
    }

    @Override
    public Entry<K, V> pollFirstEntry() {
        return new SecuredEntry<>(navigableMap.pollFirstEntry(), securedHelper);
    }

    @Override
    public Entry<K, V> pollLastEntry() {
        return new SecuredEntry<>(navigableMap.pollLastEntry(), securedHelper);
    }

    @Override
    public NavigableMap<K, V> descendingMap() {
        return new SecuredNavigableMap<>(navigableMap.descendingMap(),
                                         clazz,
                                         requiredPrivilege);
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
        return navigableMap.navigableKeySet();
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
        return navigableMap.descendingKeySet();
    }

    @Override
    public NavigableMap<K, V> subMap(final K fromKey,
                                     final boolean fromInclusive,
                                     final K toKey,
                                     final boolean toInclusive) {
        return new SecuredNavigableMap<>(navigableMap.subMap(fromKey,
                                                             fromInclusive,
                                                             toKey,
                                                             toInclusive),
                                         clazz,
                                         requiredPrivilege);
    }

    @Override
    public NavigableMap<K, V> headMap(final K toKey,
                                      final boolean inclusive) {
        return new SecuredNavigableMap<>(navigableMap.headMap(toKey, inclusive),
                                         clazz,
                                         requiredPrivilege);
    }

    @Override
    public NavigableMap<K, V> tailMap(final K fromKey,
                                      final boolean inclusive) {
        return new SecuredNavigableMap<>(navigableMap.tailMap(fromKey,
                                                              inclusive),
                                         clazz,
                                         requiredPrivilege);
    }

}
