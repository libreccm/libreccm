/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package com.arsdigita.bebop.table;

import org.libreccm.cdi.utils.CdiUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used as an bridge between a CDI based Controller class and
 * a Bebop {@link TableModel}. The Controller provides a (transactional) method 
 * for retrieving the data to show in the table. The table model simply retrieves
 * the Controller bean using the {@link CdiUtil} and uses the returned list of
 * objects of this class for creating the table rows.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <K> Type of the row key.
 */
public class RowData<K> {
    
    private K rowKey;
    
    private final String[] cols;
    
    public RowData(final int numCols) {
        cols = new String[numCols];
    }
    
    public K getRowKey() {
        return rowKey;
    }
    
    public void setRowKey(final K rowKey) {
        this.rowKey = rowKey;
    }
    
    public String getColData(final int colIndex) {
        return cols[colIndex];
    }
    
    public void setColData(final int colIndex, final String data) {
        cols[colIndex] = data;
    }
    
}
