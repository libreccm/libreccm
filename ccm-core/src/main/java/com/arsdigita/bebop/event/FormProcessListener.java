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
 *    Defines the interface for a class that performs a processing step
 *    on valid data.
 *
 *    @author Karl Goldstein 
 *    @author Uday Mathur 
 *    @version $Id$
 */

public interface FormProcessListener extends EventListener {

    /**
     * Performs a processing step on the data in the
     * <code>FormData</code> object.
     *
     * <p>Implementations of this method are responsible for catching
     * specific exceptions that may occur during processing, and either
     * handling them internally or rethrowing them as instances of
     * <code>FormProcessException</code> to be handled by the calling
     * procedure.
     *
     * <p>Implementations of this method cannot assume success or
     * failure of other FormProcessListeners associated with a
     * particular FormModeel. Each implementation must act independently
     *
     * @param model The form model describing the structure and properties
     * of the form data included with this request.
     *
     * @param data The container for all data objects associated with
     * the request.  String values for all parameters specified in the
     * form model are converted to Java data objects and validated
     * before processing occurs.
     *
     * @param request The HTTP request information from which the form
     * data was extracted.  Note that the request object is supplied
     * only in case the processing step requires contextual information
     * (information extracted from cookies or the peer address, for
     * example) or needs to modify session properties.
     *
     * @param response The HTTP response that will be returned to the
     * user.  The processing step may require access to this object to
     * set cookies or handle errors. */

    void process(FormSectionEvent e) throws FormProcessException;

}
