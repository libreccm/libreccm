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
package com.arsdigita.bebop.event;

import com.arsdigita.bebop.FormProcessException;
import java.util.EventListener;

/**
 *    Defines the interface for a class that performs cleanup after
 *    cancelling out of a form
 *
 *    @author Kevin Scaldeferri 
 *    @version $Id$
 */

public interface FormCancelListener extends EventListener {

    /**
     * Performs any necessary cleanup after a user cancels out of
     * a form
     *
     * <p>Implementations of this method are responsible for catching
     * specific exceptions that may occur during processing, and either
     * handling them internally or rethrowing them as instances of
     * <code>FormProcessException</code> to be handled by the calling
     * procedure.
     */

    void cancel(FormSectionEvent e) throws FormProcessException;

}
