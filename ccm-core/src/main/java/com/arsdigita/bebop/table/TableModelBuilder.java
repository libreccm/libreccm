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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.util.Lockable;

/**
 * Builds the request-specific table models. A table retrieves the data it
 * displays by asking the table model builder for a table model. This is
 * done for each request; the table does not cahce table models across
 * requests. If such caching is desired, it has to be performed by the
 * table model builder.
 *
 * <p> Typically, the table model builder will run a database query based
 * on the information contained in the page state and return the result of
 * the database query by wrapping it in a table model. The table will then
 * traverse the table model during rendering.
 *
 * <p> The table model builder is automatically locked by the table to
 * which it was added either through one of the {@link
 * com.arsdigita.bebop.Table Table} constructors or with a call to {@link
 * com.arsdigita.bebop.Table#setModelBuilder}.
 *
 * @see com.arsdigita.bebop.Table Table
 * @see TableModel
 *
 * @author David Lutterkort
 * @version $Id$
 */
public interface TableModelBuilder extends Lockable {


    /**
     * Return a table model for the request represented by
     * <code>s</code>. The table model contains all the data that is to be
     * displayed in a table. The returned table model is used only during
     * the duration of that request.
     *
     * @param t the table which will use this table model
     * @param s represents the current request
     * @return the data to be displayed in the table
     * @pre t != null
     * @pre s != null
     * @post return != null
     */
    TableModel makeModel(Table t, PageState s);
}
