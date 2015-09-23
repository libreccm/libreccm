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
package com.arsdigita.bebop.event;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;

/**
 * An event for the {@link com.arsdigita.bebop.Table} component.
 * Table will fire this event when one of its active cells receives a
 * click.
 *
 * @see TableActionListener
 * @see TableActionAdapter
 * @author David Lutterkort
 * @version $Id$
 */
public class TableActionEvent extends ActionEvent {

    private Object m_rowKey;
    private Integer m_column;

    /**
     * Construct a TableActionEvent for a click on a particular row
     * and a particular column.
     *
     * @param source the Component generating the event.
     * @param s the state for the current request.
     * @param rowKey the key for the row where the click was registered.
     * @param column the index of the column where the click was registered.
     */
    public TableActionEvent(Component source, PageState s,
                            Object rowKey, Integer column) {
        super(source, s);
        m_rowKey = rowKey;
        m_column = column;
    }

    /**
     * Construct a TableActionEvent for a click on a particular row.
     *
     * @param source the Component generating the event.
     * @param s the state for the current request.
     * @param rowKey the key for the row where the click was registered.
     */
    public TableActionEvent(Component source, PageState s, Object rowKey) {
        this(source, s, rowKey, new Integer(-1));
    }

    /**
     * Get the key for the row that received the click.
     *
     * @return the key for the row that received the click.
     */
    public final Object getRowKey() {
        return m_rowKey;
    }

    /**
     * Get the index of the column that received the click.
     *
     * @return the index of the column that received the click.
     */
    public final Integer getColumn() {
        return m_column;
    }
}
