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
import com.arsdigita.util.LockableImpl;

/**
 * A convenience for implementing <code>TableModelBuilder</code>s. This
 * class provides a default implementation of the methods demanded by
 * <code>Lockable</code>, so that implementors of
 * <code>TableModelBuilder</code> only need to override the
 * <code>makeModel</code> method.
 *
 * @author David Lutterkort
 * @see TableModelBuilder
 * @see com.arsdigita.util.Lockable
 *
 * @version $Id$
 */
public abstract class AbstractTableModelBuilder extends LockableImpl
    implements TableModelBuilder {

    /**
     * Return a table model for the request represented by
     * <code>s</code>. The table model contains all the data that is to be
     * displayed in a table. The returned table model is used only during
     * the duration of that request.
     *
     * @param t the table which will use this table model
     * @param s represents the current request
     * @return the data to be displayed in the table
     */
    public abstract TableModel makeModel(Table t, PageState s);

}
