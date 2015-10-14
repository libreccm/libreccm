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

/**
 * The <code>TableModel</code> is the abstraction a {@link
 * com.arsdigita.bebop.Table Table} uses to access the data it
 * displays. The table will ask its {@link TableModelBuilder} to
 * instantiate a new table model once for each request it processes.
 *
 * <p> The table will request each element in the model at most once per
 * request, moving through the rows with successive calls to {@link
 * #nextRow}. For each row, the table retrieves the values and keys in each
 * column with calls to {@link #getElementAt} and {@link #getKeyAt}.
 *
 * <p> The table data is accessed by the table by moving through the rows
 * of the table model with calls to {@link #nextRow}. The data for each
 * column in a row is represented by two objects: the data element which
 * usually contains display information for that column and can be as
 * simple as a string, and the key, which is used to identify the
 * column. The key is usually a suitable representation of the primary key
 * of the underlying object in the database. The key needs to be unique
 * amongst all <em>rows</em> in the table model, but doesn't need to
 * uniquely identify the row <em>and</em> column for that data item -
 * all calls to {@link #getKeyAt} can return the same value for one row in
 * the table model.
 *
 * @see com.arsdigita.bebop.Table Table
 * @see TableModelBuilder
 *
 * @author David Lutterkort
 * @version $Id$ */
public interface TableModel {


    /**
     * Return the number of columns this table model has.
     *
     * @return the number of columns in the table model
     * @post return >= 0
     */
    int getColumnCount();

    /**
     * Move to the next row and return true if the model is now positioned on
     * a valid row. Initially, the table model is positioned before the first
     * row. The table will call this method before it retrieves the data for
     * the row with calls to {@link #getElementAt getElementAt} and {@link
     * #getKeyAt getKeyAt}.
     *
     * <p> If this method returns <code>true</code>, subsequent calls to
     * {@link #getElementAt getElementAt} and {@link #getKeyAt getKeyAt} have
     * to succeed and return non-null objects. If this method returns
     * <code>false</code>, the table assumes that it has traversed all the
     * data contained in this model.
     *
     * @return <code>true</code> if the model is positioned on a valid row
     */
    boolean nextRow();

    /**
     * Return the data element for the given column and the current row. The
     * returned object will be passed to the table cell renderer as the
     * <code>value</code> argument without modifications.
     *
     * @param columnIndex the number of the column for which to get data
     * @return the object to pass to the table cell renderer for display
     * @pre columnIndex >= 0 && columnIndex < getColumnCount()
     * @post return != null
     * @see TableCellRenderer
     */
    Object getElementAt(int columnIndex);

    /**
     * Return the key for the given column and the current row. The key has
     * to be unique for each <em>row</em> in the table model, but does not
     * need to be unique for each row <em>and</em> column, though it may.
     * The key is passed to the table cell renderer as the <code>key</code>
     * argument.
     *
     * @param columnIndex the number of the column for which to get data
     * @return the key for the given column and the current row.
     * @pre columnIndex >= 0 && columnIndex < getColumnCount()
     * @post return != null
     * @see TableCellRenderer
     */
    Object getKeyAt(int columnIndex);
}
