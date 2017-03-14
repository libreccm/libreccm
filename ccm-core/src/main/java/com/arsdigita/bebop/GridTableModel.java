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
package com.arsdigita.bebop;

import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.list.ListModel;

import java.util.ArrayList;

/**
 * Converts a linear ListModel to a grid of items.
 * For example, <code>A B C D E F G</code> becomes:
 *
 * <code><pre>
 * A D G
 * B E .
 * C F .
 * </pre></code>
 *
 * The extraneous cells in the table are filled
 * with <code>GridTableModel.PLACEHOLDER</code>.
 *
 * @version $Id: GridTableModel.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class GridTableModel implements TableModel {

    private ListModel m_items;
    private int m_colHeight, m_cols, m_size, m_index;
    private Object[] m_elements;
    private Object[] m_keys;

    /**
     * Constructs a new <code>GridTableModel</code>.
     * @param items a {@link ListModel} that represents the
     *   items
     * @param cols the number of columns in the grid
     */
    public GridTableModel(ListModel items, int cols) {
        m_items = items;
        m_cols = cols;

        // Load the items into memory
        ArrayList elements = new ArrayList(), keys = new ArrayList();
        for(m_size=0; m_items.next(); m_size++) {
            elements.add(m_items.getElement());
            keys.add(m_items.getKey());
        }

        m_elements = elements.toArray();
        m_keys = keys.toArray();

        // Round up
        m_colHeight = m_size / m_cols;
        if(m_colHeight * m_cols < m_size) ++m_colHeight;

        m_index = -1;
    }

    public int getColumnCount() {
        return m_cols;
    }

    public boolean nextRow() {
        if(m_index >= m_colHeight - 1)
            return false;

        ++m_index;
        return true;
    }

    private Object safeGet(Object[] a, int columnIndex) {
        int i = m_index + m_colHeight*columnIndex;

        if(i >= a.length)
            return null;
        else
            return a[i];
    }

    public Object getElementAt(int columnIndex) {
        return safeGet(m_elements, columnIndex);
    }

    public Object getKeyAt(int columnIndex) {
        return safeGet(m_keys, columnIndex);
    }
}
