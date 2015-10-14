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
package com.arsdigita.bebop.table;

import java.util.ArrayList;
import java.util.Iterator;

import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.util.Assert;

/**
 * Describe interface <code>TableColumnModel</code> here.
 *
 * @author David Lutterkort
 * @version $Id$
 */
public class DefaultTableColumnModel implements TableColumnModel {

    private static final String SELECTED_COLUMN="col";

    private boolean m_locked;

    private ArrayList m_columns;

    private SingleSelectionModel m_selection;

    public DefaultTableColumnModel() {
        this(new Object[0]);
    }

    public DefaultTableColumnModel(SingleSelectionModel sel) {
        this(new Object[0], sel);
    }

    public DefaultTableColumnModel(Object[] headers) {
        this(headers,
             new ParameterSingleSelectionModel(new IntegerParameter(SELECTED_COLUMN)) );
    }

    public DefaultTableColumnModel(Object[] headers, SingleSelectionModel sel) {
        m_columns = new ArrayList();
        m_selection = sel;

        for (int i=0; i < headers.length; i++) {
            add(new TableColumn(i, headers[i], new Integer(i)));
        }
    }

    public void add(TableColumn column) {
        Assert.isUnlocked(this);
        m_columns.add(column);
    }

    public void add(int columnIndex, TableColumn column) {
        Assert.isUnlocked(this);
        m_columns.add(columnIndex, column);
    }

    public TableColumn get(int columnIndex) {
        return (TableColumn) m_columns.get(columnIndex);
    }

    public void set(int columnIndex, TableColumn v) {
        m_columns.set(columnIndex, v);
    }

    public int size() {
        return m_columns.size();
    }

    public int getIndex(Object key) {
        if ( key == null ) {
            return -1;
        }
        for (int i=0; i<size(); i++) {
            TableColumn t  = get(i);
            if ( key.equals(t.getHeaderKey()) ) {
                return i;
            }
        }
        return -1;
    }

    public Iterator columns() {
        return m_columns.iterator();
    }

    public void remove(TableColumn column) {
        Assert.isUnlocked(this);
        m_columns.remove(column);
    }

    public final SingleSelectionModel getSelectionModel() {
        return m_selection;
    }

    public void setSelectionModel(SingleSelectionModel model) {
        Assert.isUnlocked(this);
        m_selection = model;
    }

    public final void lock() {
        m_locked = true;
    }

    public final boolean isLocked() {
        return m_locked;
    }
}
