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
package com.arsdigita.util;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * A map that keeps its entries in a fixed sequence. All iterators returned
 * by this class, for example by <code>entrySet().iterator()</code>, are
 * guaranteed to return the entries in the order in which they were put in
 * the map. This implementation allows <code>null</code> for both the key
 * or the associated value for a map entry.
 * </p>
 *
 * <p>
 * Almost all of the map operations, for example {@link #get get} or {@link
 * #containsKey containsKey} require time linear in the size of the map,
 * making this map only suitable for small map sizes.
 * </p>
 *
 * @author David Lutterkort
 * @version $Id: SequentialMap.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class SequentialMap extends AbstractMap implements Map {

    private ArrayList m_entries = null;
    private Set m_entrySet = null;

    /**
     * <p>
     * Creates an empty <code>SequentialMap</code>.
     * </p>
     */
    public SequentialMap() {
        m_entries = new ArrayList();
    }

    /**
     * </p>
     * Return the number of entries in the map.
     * </p>
     *
     * @return int the number of entries.
     */
    public int size() {
        return m_entries.size();
    }

    /**
     * <p>
     * Index of the entry with the given key. <code>key</code> may be null.
     * </p>
     *
     * <p>
     * Requires time linear in the size of the map.
     * </p>
     *
     * @param key the key to find.
     *
     * @return int the index with key <code>key</key> or -1 if no such entry
     *         exists.
     */
    public int indexOf(Object key) {
        int index = -1;

        for (int i = 0; i < size(); i++) {
            Map.Entry p = (Map.Entry) m_entries.get(i);

            if (key == null) {
                if (p.getKey() == null) {
                    index = i;
                }
            } else {
                if (key.equals(p.getKey())) {
                    index = i;
                }
            }
        }

        return index;
    }

    /**
     * <p>
     * Return true if the map maps one or more keys to the specified
     * value. More formally, returns true if and only if this map contains at
     * least one mapping to a value <code>v</code> such that
     * <code>(value==null ? v==null : value.equals(v))</code>.
     * </p>
     *
     * <p>
     * Requires time linear in the size of the map
     * </p>
     *
     * @param value value whose presence in this map is to be tested.
     *
     * @return <code>true</code> if this map maps one or more keys to the
     *         specified value.
     */
    public boolean containsValue(Object value) {
        boolean contains = false;

        for (int i = 0; i < size(); i++) {
            Map.Entry p = (Map.Entry) m_entries.get(i);

            if (value == null) {
                if (p.getValue() == null) {
                    contains = true;
                }
            } else {
                if (value.equals(p.getValue())) {
                    contains = true;
                }
            }
        }

        return contains;
    }

    /**
     * <p>
     * Returns <code>true</code> if this map contains a mapping for the
     * specified key.
     * </p>
     *
     * <p>
     * Requires time linear in the size of the map.
     * </p>
     *
     * @param key key whose presence in this map is to be tested.
     *
     * @return boolean <code>true</code> if this map contains a mapping for the
     *         specified key.
     */
    public boolean containsKey(Object key) {
        return (indexOf(key) != -1);
    }

    /**
     * <p>
     * Returns the value to which this map maps the specified key. Returns
     * <code>null</code> if the map contains no mapping for this key. A
     * return value of <code>null</code> does not necessarily indicate that
     * the map contains no mapping for the key; it's also possible that the
     * map explicitly maps the key to <code>null</code>. The
     * <code>containsKey</code> operation may be used to distinguish these
     * two cases.
     * </p>
     *
     * <p>
     * Requires time linear in the size of the map.
     * </p>
     *
     * @param key key whose associated value is to be returned.
     *
     * @return Object the value to which this map maps the specified key, or
     *         <code>null</code> if the map contains no mapping for this key.
     */
    public Object get(Object key) {
        int i = indexOf(key);

        return (i == -1) ? null : ((Map.Entry) m_entries.get(i)).getValue();
    }

    /**
     * <p>
     * Returns the value which is stored at the specified sequential
     * position in the map. May throw an IndexOutOfBoundsException
     * if (index < 0 || index >= size()).
     * </p>
     *
     * <p>
     * Requires constant time.
     * </p>
     *
     * @param index The index of the element to return.
     *
     * @return Object The element at the specified index.
     */
    public Object get(int index) {
        return ((Map.Entry) m_entries.get(index)).getValue();
    }

    /**
     * <p>
     * Returns the key which is stored at the specified sequential
     * position in the map. May throw an IndexOutOfBoundsException
     * if (index < 0 || index >= size()).
     * </p>
     *
     * <p>
     * Requires constant time.
     * </p>
     *
     * @param index The index of the element to return.
     *
     * @return Object The key of the element at the specified index.
     */
    public Object getKey(int index) {
        return ((Map.Entry) m_entries.get(index)).getKey();
    }

    /**
     * Associates the specified value with the specified key in this map. The
     * new entry is appended at the end of the map. If an entry with the same
     * <code>key</code> already exists, it is removed first. To change the
     * value of an existing key-value pair without changing the position of
     * the <code>key</code>, use {@link #update update}.
     *
     * <p>
     * Requires time linear in the size of the map
     *
     * @param key key with which the specified value is to be associated.
     *
     * @param value value to be associated with the specified key.
     *
     * @return Object The previous value associated with specified key, or
     *         null if there was no mapping for key. A null return can also
     *         indicate that the map previously associated null with the
     *         specified key.
     *
     * @see #update update
     */
    public Object put(Object key, Object value) {
        Object result = null;
        int i = indexOf(key);

        if (i != -1) {
            result = ((Pair) m_entries.get(i)).getValue();
            m_entries.remove(i);
        }
        m_entries.add(new Pair(key, value));

        return result;
    }

    /**
     * <p>
     * Update an existing key-value pair. If an entry with key
     * <code>key</code> already exists, it is replaced with the new
     * association without changing the place in which the key appears in the
     * sequence of keys. If no such entry exists, it is appended at the end.
     * </p>
     *
     * <p>
     * Requires time linear in the size of the map.
     * </p>
     *
     * @param key key with which the specified value is to be associated.
     *
     * @param value value to be associated with the specified key.
     *
     * @return Object The previous value associated with specified key, or
     *         null if there was no mapping for key. A null return can also
     *         indicate that the map previously associated null with the
     *         specified key.
     *
     * @see #put put
     */
    public Object update(Object key, Object value) {
        Object result = null;
        int i = indexOf(key);

        if (i != -1) {
            result = ((Pair) m_entries.get(i)).getValue();
            m_entries.set(i, new Pair(key, value));
        } else {
            m_entries.add(new Pair(key, value));
        }

        return result;
    }

    /**
     * <p>
     * Removes the mapping for this key from this map if present.
     * </p>
     *
     * @param key key whose mapping is to be removed from the map.
     *
     * @return Object The previous value associated with specified key, or null
     *         if there was no mapping for key. A null return can also indicate
     *         that the map previously associated null with the specified key.
     */
    public Object remove(Object key) {
        Object result = null;
        int i = indexOf(key);

        if (i != -1) {
            result = ((Pair) m_entries.get(i)).getValue();
            m_entries.remove(i);
        }

        return result;
    }

    /**
     * <p>
     * Removes all mappings from this map.
     * </p>
     */
    public void clear() {
        m_entries.clear();
    }

    /**
     * <p>
     * Returns a set view of the mappings contained in this map. Each element
     * in the returned set is a <code>Map.Entry</code>. The set is backed by
     * the map, so changes to the map are reflected in the set, and
     * vice-versa. If the map is modified while an iteration over the set is
     * in progress, the results of the iteration are undefined. The set
     * supports element removal, which removes the corresponding mapping from
     * the map, via the <code>Iterator.remove</code>,
     * <code>Set.remove</code>, <code>removeAll</code>,
     * <code>retainAll</code> and <code>clear</code> operations. It does not
     * support the <code>add</code> or <code>addAll</code> operations.
     * </p>
     *
     * @return Set A set view of the mappings contained in this map.
     *
     * @post return != null
     */
    public Set entrySet() {
        if (m_entrySet == null) {
            m_entrySet = new AbstractSet() {
                    public Iterator iterator() {
                        return m_entries.iterator();
                    }

                    public boolean contains(Object o) {
                        return m_entries.contains(o);
                    }

                    public boolean remove(Object o) {
                        return m_entries.remove(o);
                    }

                    public int size() {
                        return m_entries.size();
                    }

                    public void clear() {
                        m_entries.clear();
                    }
                };
        }

        return m_entrySet;
    }
}
