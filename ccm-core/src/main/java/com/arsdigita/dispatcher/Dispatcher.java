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
package com.arsdigita.dispatcher;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Defines a single dispatch
 * method that allows programmers to pass extra context information
 * around in a RequestContext object. Thus, when dispatcher A chains
 * to dispatcher B, dispatcher B can determine what portion of the
 * original request URL it needs to work with and what portion just
 * made dispatcher A chain to dispatcher B. This context information
 * allows a dispatcher to dynamically hand off a request to another
 * dispatcher.
 *
 * A dispatcher is an <em>entry point</em> for a package.  Each package
 * defines a dispatcher, which brokers out requests within the package.
 * The dispatcher for a package can dispatch requests to other packages.
 * Multiple packages can share the same dispatcher <em>type</em> but
 * not instance.<p>
 *
 * If a dispatcher object for a package also implements HttpServlet,
 * then it is also an entry point for the entire web application.
 *
 * @author Bill Schneider 
 * @version $Id$
 */

public interface Dispatcher {


    /**
     * Dispatches this request.
     * @param request the servlet request object
     * @param response the servlet response object
     * @param actx the request context
     * @exception java.io.IOException may be thrown by the dispatcher
     * to indicate an I/O error
     * @exception javax.servlet.ServletException may be thrown by the
     *  dispatcher to propagate a generic error to its caller
     */
    public void dispatch(HttpServletRequest request,
                         HttpServletResponse response,
                         RequestContext actx)
        throws IOException, ServletException;
}
