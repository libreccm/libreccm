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

import java.util.Iterator;

import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.util.Lockable;

/**
 * Describe interface <code>TableColumnModel</code> here.
 *
 * @author David Lutterkort
 * @version $Id$
 */
public interface TableColumnModel extends Lockable {


    void add(TableColumn column);

    TableColumn get(int columnIndex);

    /**
     * Insert a column at the given index. The columns from
     * <code>columnIndex</code> on are shifted one up.
     *
     * @param columnIndex the index for the new column.
     * @param column the table column to add to the model.
     * @pre 0 <= columnIndex && columnIndex <= size()
     */
    void add(int columnIndex, TableColumn column);

    void set(int columnIndex, TableColumn v);

    int size();

    int getIndex(Object columnIdentifier);

    Iterator columns();

    void remove(TableColumn column);

    SingleSelectionModel getSelectionModel();

    void setSelectionModel(SingleSelectionModel model);
}
