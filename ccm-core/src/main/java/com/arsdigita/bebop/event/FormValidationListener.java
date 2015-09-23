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
 *    Defines the interface for a class that implements a validation check
 *    on a set of form data.
 *
 *    @author Karl Goldstein 
 *    @author Uday Mathur 
 *    @version $Id$
 */
public interface FormValidationListener extends EventListener {

    /**
     * Performs a validation check on the specified <tt>FormData</tt>
     * object, involving any number of parameters.
     *
     * <p>The check is always performed after all HTTP request
     * parameters have been converted to data objects and stored in the
     * FormData object.
     *
     * <p>If a validation error is encountered, the <tt>setError</tt>
     * method of the <tt>FormData</tt> object may be used to set an
     * error message for reporting back to the user.
     *
     * <p>This method is responsible for catching any exceptions that
     * may occur during the validation.  These exceptions may either
     * be handled internally, or if they are unrecoverable may be
     * rethrown as instances of <code>FormProcessException</code>.
     *
     * @param e   FormSectionEvent containing the FormData as well as the
     *            PageState.
     *            Clients may access the PageState by executing something like
     *            PageState state = fse.getPageState();
     *            Method getFormData() allows access to the Form's data.
     *
     * @exception FormProcessException ff the data does not pass the check. 
     */

    void validate(FormSectionEvent e) throws FormProcessException;

}
