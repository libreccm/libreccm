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
package com.arsdigita.web;

import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * <p>
 * A <code>RedirectSignal</code> that sends the client to the login UI.
 *  <code>LoginSignal</code> encodes the origin URL so that
 * <code>ReturnSignal</code> can return the client to its starting point.</p>
 *
 * @see com.arsdigita.web.ReturnSignal
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public class LoginSignal extends RedirectSignal {

    private static final long serialVersionUID = 6546166999255204832L;

    /**
     * Constructs a signal to redirect the client to log in. This constructor
     * tells the base servlet to abandon the current transaction.
     * @param sreq
     */
    public LoginSignal(final HttpServletRequest sreq) {
        this(sreq, false);
    }

    /**
     * Constructs a signal to redirect the client to log in.
     *
     * @param sreq
     * @param isCommitRequested
     */
    public LoginSignal(final HttpServletRequest sreq,
                       final boolean isCommitRequested) {
        super(URL.login(sreq), isCommitRequested);
    }

}
