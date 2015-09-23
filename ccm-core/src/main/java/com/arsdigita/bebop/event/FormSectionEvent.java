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

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.PageState;

/**
 * An event originating from a form. <code>FormSectionEvent</code>s are
 * used to notify listeners that values in a form should be initilialized,
 * validated or processed.
 *
 * @author David Lutterkort 
 *
 * @version $Id$
 *
 * @see FormInitListener
 * @see FormValidationListener
 * @see FormProcessListener
 */
public class FormSectionEvent extends PageEvent {

    private final transient FormData _formData;

    /**
     * Get the form data for to the form that fired the event in the current
     * request.
     * 
     * @return form data
     */
    public final FormData getFormData() {
        return _formData;
    }

    /**
     * Construct a <code>FormSectionEvent</code>.
     *
     * @param source the form model that fired the event
     * @param state  the state of the enclosing page
     * @param formData the form data constructed so far
     */
    public FormSectionEvent(Object source, 
                            PageState state,
                            FormData formData) {
        super(source, state);
        _formData = formData;
    }

}
