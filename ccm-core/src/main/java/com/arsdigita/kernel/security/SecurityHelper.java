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
package com.arsdigita.kernel.security;

import javax.servlet.http.HttpServletRequest;

/**
 * Provides methods for determining security properties for a request.
 *
 * @author Sameer Ajmani
 * @version $Id: SecurityHelper.java 287 2005-02-22 00:29:02Z sskracic $
 */
public interface SecurityHelper {

    /**
     * Determines whether the given request is secure.  Implementation may
     * simply return <code>req.isSecure()</code>, but certain deployments
     * may use other information (such as the requested port number)
     * instead.
     *
     * @param req the request to check
     *
     * @return <code>true</code> if the given request uses a secure
     * protocol, <code>false</code> otherwise.
     **/
    public boolean isSecure(HttpServletRequest req);

    /**
     * Determines whether the given request requires the user to be logged
     * in.  If this method returns <code>true</code>, the system will call
     * <code>getLoginURL</code> to determine where to redirect the client to
     * log in.
     *
     * @param req the request to check
     *
     * @return <code>true</code> if the given request requires the user to
     * be logged in, <code>false</code> otherwise.
     **/
    public boolean requiresLogin(HttpServletRequest req);

    /**
     * Determines where to redirect the client to log in.  The system calls
     * this method if the user fails to log in and
     * {@code requiresLogin(req)} is true.
     *
     * @param req The request for which the login URL is determined.
     * 
     * @return the URL to which the client should be redirected to log in,
     * never null.
     **/
    public String getLoginURL(HttpServletRequest req);
}
