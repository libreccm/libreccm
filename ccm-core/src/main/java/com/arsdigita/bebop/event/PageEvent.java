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

import com.arsdigita.bebop.PageState;

import java.util.EventObject;

/**
 * The base class for all page related events. All page related events
 * should be derived from this class, since it defines a standard way to
 * get at the source of the event and at the state of the page under the
 * request that is currently being processed.
 *
 * @author David Lutterkort 
 *
 * @version $Id$
 */
public class PageEvent extends EventObject {

    private transient PageState _state;

    /**
     * Construct a new <code>PageEvent</code>.
     * @param source the object firing the event, usually a {@link
     * com.arsdigita.bebop.Component <code>Component</code>}.
     * @param state the state of the page under the current request
     */
    public PageEvent(Object source, PageState state) {
        super(source);
        _state = state;
    }

    /**
     * Get the state of the page under the request in which the event was fired
     */
    public final PageState getPageState() {
        return _state;
    }

}
