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
import com.arsdigita.bebop.parameters.ParameterData;

import java.util.EventObject;

/**
 * An event connected to a request parameter.
 *
 * @author David Lutterkort 
 *
 * @version $Id$
 *
 * @see ParameterListener
 * @see com.arsdigita.bebop.parameters.ParameterModel
 * @see com.arsdigita.bebop.parameters.ParameterData
 */

public class ParameterEvent extends EventObject {

    /* The request specific data about the event */
    private ParameterData m_data;
    private PageState m_state;

    /**
     * Construct a <code>ParameterEvent</code>
     *
     * @param source the object that originated the event
     * @param data the data for the parameter from the current request
     **/

    public ParameterEvent(Object source,  ParameterData data) {
        super(source);
        m_data = data;
        m_state = PageState.getPageState();
    }


    /**
     * Get the request specific data about the parameter.
     **/

    public final ParameterData getParameterData() {
        return m_data;
    }


    /**
     *
     **/

    public PageState getPageState() {
        return m_state;
    }

}
