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

interface DispatcherConstants {

    /**
     * Attribute name for the Throwable object saved when a JSP
     * handles an error with the errorPage directive.
     */
    final static String JSP_EXCEPTION_ATTRIBUTE =
        "javax.servlet.jsp.jspException";

    /**
     * Attribute name for the URI that caused an error
     * when the servlet container forwards to an error page.
     */
    final static String ERROR_REQUEST_ATTRIBUTE =
        "javax.servlet.error.request_uri";

    /**
     * Attribute name to indicate that the dispatcher is within
     * a request--that is, request start listeners have run but
     * request end listeners haven't yet.
     */
    final static String REENTRANCE_ATTRIBUTE =
        "com.arsdigita.dispatcher.inside_request";

    /**
     * The attribute name for an included resource URI after
     * a servlet include (nesting).
     */
    final static String INCLUDE_URI =
        "javax.servlet.include.request_uri";

    /**
     * The attribute where we store the current RequestContext
     * object.
     */
    final static String REQUEST_CONTEXT_ATTR =
        "com.arsdigita.dispatcher.RequestContext";

    /**
     * The attribute where we store the original HttpServletRequest
     * object when we need to wrap the servlet request.
     */
    final static String ORIGINAL_REQUEST_ATTR =
        "com.arsdigita.dispatcher.OriginalRequest";

    /**
     * The attribute where we store the wrapped servlet request
     * object when we need to restore the original request object
     * for a forward/include.
     */
    final static String WRAPPED_REQUEST_ATTR =
        "com.arsdigita.dispatcher.WrappedRequest";

    /**
     * The session attribute where we store an identifier for the
     * previous request that made a redirect.  This prohibits us from
     * following a redirect until after the request that generated the
     * redirect commits its transaction.
     */
    final static String REDIRECT_SEMAPHORE =
        "com.arsdigita.dispatcher.redirect_semaphore";

    /**
     * The application attribute (in ServletContext) where we store the
     * list of welcome files from web.xml.
     */
    final static String WELCOME_FILES =
        "com.arsdigita.dispatcher.welcomefiles";


    final static String DISPATCHER_PREFIX_ATTR = 
        "com.arsdigita.dispatcher.DispatcherPrefix";
}
