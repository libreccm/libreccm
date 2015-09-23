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

/**
 * An event originating from a component. <code>PrintEvent</code>s are
 * fired just before the <code>source</code> component is output either as
 * part of an XML document or as part of an HTML page.
 *
 * @see PrintListener
 *
 * @author Uday Mathur
 * @author David Lutterkort
 *
 * @version $Id$
 *
 */
public class PrintEvent extends PageEvent {

    private Object m_target;

    /**
     * Construct a <code>PrintEvent</code>
     *
     * @param source the object that originated the event
     * @param data the data for the parameter from the current request
     * @pre source != null
     * @pre target != null
     */
    public PrintEvent(Object source,  PageState state, Object target) {
        super(source, state);
        m_target = target;
    }

    /**
     * Get the target object, the one that can be freely modified by print
     * listeners. Initially, the target is an unlocked clone of the source of
     * the event.
     * @post return != null
     */
    public final Object getTarget() {
        return m_target;
    }

}
