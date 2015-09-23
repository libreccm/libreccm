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

import com.arsdigita.bebop.FormProcessException;
import java.util.EventListener;

/**
 * Defines the interface for initializing a form with default values.
 * Typical implementations of this interface query the database to
 * set up an "edit" form, or obtain an id from a sequence to initialize
 * a "create" form.
 *
 * @author Karl Goldstein 
 * @author Uday Mathur 
 * @version $Id$
 */
public interface FormInitListener extends EventListener {

    /**
     * Initializes a FormData object already populated with values from
     * the request.
     *
     * @param date The form data containing data included with this
     * request.  The initializer may require knowledge of form or
     * parameter properties.
     *
     * @param request The HTTP request associated with the
     * initialization event.  This supplied so that the initializer may
     * rely on contextual information, such information extracted from
     * headers or cookies or an associated <code>HttpSession</code>
     * object.
     * */
    void init(FormSectionEvent e) throws FormProcessException;

}
