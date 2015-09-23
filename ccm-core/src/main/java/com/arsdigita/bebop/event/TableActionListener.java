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

import java.util.EventListener;

/**
 * Specifies the interface for handling events on {@link
 * com.arsdigita.bebop.Table}.  Programmers wishing to override just
 * one of these methods, not both, may prefer to use {@link
 * TableActionAdapter}.
 *
 * @see TableActionEvent
 * @see TableActionAdapter
 * @author David Lutterkort
 * @version $Id$
 */
public interface TableActionListener extends EventListener {

    /**
     * An event handler for actions on a particular cell or a set of
     * cells.
     *
     * @param e the event fired for the table.
     */
    void cellSelected(TableActionEvent e);

    /**
     * An event handler for actions on a particular column heading or
     * set of column headings.
     *
     * @param e the event fired for the table.
     */
    void headSelected(TableActionEvent e);
}
