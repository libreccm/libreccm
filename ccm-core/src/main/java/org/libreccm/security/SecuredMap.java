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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Decorator for {@link Map} which checks if the current subject is permitted to
 * access the values of the decorated map before returning them. The keys used
 * by the map are not checked.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @param <K> Type of the keys
 * @param <V> Type of the values.
 */
public class SecuredMap<K, V extends CcmObject> implements Map<K, V> {

    /**
     * The decorated map.
     */
    private final Map<K, V> map;
    /**
     * Class of the values in map.
     */
    private final Class<V> clazz;
    /**
     * The privilege required to access the values of the map.
     */
    private final String requiredPrivilege;
    /**
     * {@link SecuredHelper} used by the map.
     */
    private final SecuredHelper<V> securedHelper;

    /**
     * Creates a new secured map.
     *
     * @param map               The map to secure.
     * @param clazz             Class of the values in the map.
     * @param requiredPrivilege The privilege required to access the values of
     *                          the map.
     */
    public SecuredMap(final Map<K, V> map,
                      final Class<V> clazz,
                      final String requiredPrivilege) {
        this.map = map;
        this.clazz = clazz;
        this.requiredPrivilege = requiredPrivilege;
        this.securedHelper = new SecuredHelper<>(clazz, requiredPrivilege);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(final Object key) {
        return securedHelper.canAccess(map.get(key));
    }

    @Override
    public V put(final K key, final V value) {
        return map.put(key, value);
    }

    @Override
    public V remove(final Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        this.map.putAll(map);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return new SecuredCollection<>(map.values(), clazz, requiredPrivilege);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new SecuredEntrySet<>(map.entrySet(),
                                     requiredPrivilege,
                                     securedHelper);

    }

}
