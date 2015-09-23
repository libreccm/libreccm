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
package com.arsdigita.bebop.util;

/**
 * A map that keeps its entries in a fixed sequence. All iterators returned
 * by this class, for example by <code>entrySet().iterator()</code>, are
 * guaranteed to return the entries in the order in which they were put in
 * the map. This implementation allows <code>null</code> for both the key
 * or the associated value for a map entry.
 *
 * <p>
 * Almost all of the map operations, for example {@link #get get} or {@link
 * #containsKey containsKey} require time linear in the size of the map,
 * making this map only suitable for small map sizes.
 *
 * @author David Lutterkort
 * @version $Id: SequentialMap.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class SequentialMap extends com.arsdigita.util.SequentialMap {


    /**
     * Creates an empty <code>SequentialMap</code>.
     */
    public SequentialMap() {
        super();
    }

    /**
     * Find an entry with the given key. <code>key</code> may be null.
     *
     * <p>
     * Requires time linear in the size of the map
     *
     * @param key the key to find
     * @return the index with key <code>key</key> or -1 if no such entry
     * exists.
     */
    public int findKey(Object key) {
        return indexOf(key);
    }
}
