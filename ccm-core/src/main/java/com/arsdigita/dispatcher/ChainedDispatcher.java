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
 *  This interface defines
 * a dispatcher that is intended to be chained together with other dispatchers.
 * The functional difference between ChainedDispatcher and Dispatcher is
 * that a ChainedDispatcher's chainedDispatch() method can return a status
 * code to indicate that it was successful or unsuccessful in finding
 * a resource to dispatch to.
 *
 * <p>This interface is mainly used in conjunction with
 * DispatcherChain, a general-purpose dispatcher that joins many
 * different ChainedDispatchers together in a chain of different
 * URL-to-resource mappings; if one cannot find a resource, the next
 * one is tried.  This is useful when an application has several
 * different methods to find a resource for a URL and each method can
 * be separated into a re-usable module.
 *
 * <p>For example, suppose an application resolves a URL to a resource
 * like this:
 *
 * <ul>
 * <li>try to serve a file /templates/$site-node/$page.jsp
 * <li>if not found, try to serve a Bebop Page mapped to $page
 * <li>if not found, try to serve a file /packages/$key/www/$page.jsp
 * <li>if not found, serve "not found" page
 * </ul>
 *
 * If we implement each stage as a separate ChainedDispatcher, then we
 * can mix and match these dispatch stages in any number of
 * applications.
 *
 * @author Bill Schneider
 * @version $Id$
 */

public interface ChainedDispatcher {

    public final static int DISPATCH_BREAK = 0;
    public final static int DISPATCH_CONTINUE = 1;

    /**
     * Dispatch this request and return a status code if
     * successful.
     *
     * @param request The servlet request object
     * @param response the servlet response object
     * @param actx The request context
     * @return DISPATCH_BREAK if dispatch successful, DISPATCH_CONTINUE
     * if no resource found (try next dispatcher in chain)
     */
    public int chainedDispatch(HttpServletRequest request,
                               HttpServletResponse response,
                               RequestContext actx)
        throws IOException, ServletException;
}
