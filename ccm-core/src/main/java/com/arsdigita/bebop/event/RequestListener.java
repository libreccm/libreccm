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
 * The listener interface for receiving request events. The class that is
 * interested in processing a request event implements this interface, and
 * the object created with that class is registered with a Bebop page, using
 * the Page.addRequestListener method. When the page has finished processing
 * the page state, and is about to pass control to the currently selected
 * component, the pageRequested method will be called.
 *
 * @author David Lutterkort 
 *
 * @version $Id$
 *
 * @see ActionEvent
 * @see java.awt.event.ActionListener
 */
public interface RequestListener extends EventListener {

    /**
     * Invoked when an action has been performed.
     *
     * @pre e != null
     */
    void pageRequested(RequestEvent e);
}
