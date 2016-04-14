/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.bebop.parameters;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;

/**
 * A generic validation listener that wraps any parameter listener so
 * that the parameter listener is conditionally run based on the value
 * of a RequestLocal. The constructor takes in a parameter listener
 * and a RequestLocal that returns a Boolean. The request local is
 * lazily evaluated when the validation listener is run. A typical
 * code block for the request local:
 *
 * <pre>
 *     private RequestLocal m_isCancel = new RequestLocal() {
 *       public Object initialValue(PageState ps) {
 *           if ( m_submit.isSelected(ps) ) {
 *                return Boolean.FALSE;
 *           } else {
 *               return Boolean.TRUE;
 *           }
 *       }};
 * </pre>
 *
 * Which only returns false if the main submit button is selected.
 *
 *
 */
@Deprecated //Does not work. Do not use
public class CancellableValidationListener implements ParameterListener {

    private RequestLocal m_isCancel;
    private GlobalizedParameterListener m_listener;

    /**
     *
     * @parameter l The listener that should be fired if this is not a
     * cancel event.
     * @parameter isCancel a Boolean RequestLocal that is true if this
     * is a cancel event; otherwise false.
     *  */
    public CancellableValidationListener(GlobalizedParameterListener l,
                                         RequestLocal isCancel) {
        m_isCancel = isCancel;
        m_listener = l;
    }

    @Override
    public void validate(ParameterEvent evt) throws FormProcessException {
        PageState ps = evt.getPageState();
        Boolean b = (Boolean) m_isCancel.get(ps);

        if ( b == Boolean.TRUE ) {
            return;
        } else {
            m_listener.validate(evt);
        }

    }

}
