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

/**
 * An implentation of the TableActionListener interface meant to save the
 * developer from having to override both the {@link
 * #cellSelected(TableActionEvent)} and {@link #headSelected(TableActionEvent)}
 * methods when they only need to change the behavior of one.
 *
 * @see TableActionEvent
 * @author David Lutterkort
 * @version $Id$
 */
public class TableActionAdapter implements TableActionListener {

    /**
     * A no-op implementation of {@link
     * TableActionListener#cellSelected(TableActionEvent)}.
     *
     * @param e the event fired for the table.
     */
    public void cellSelected(TableActionEvent e) {
        return;
    }

    /**
     * A no-op implementation of {@link
     * TableActionListener#headSelected(TableActionEvent)}.
     *
     * @param e the event fired for the table.
     */
    public void headSelected(TableActionEvent e) {
        return;
    }
}
