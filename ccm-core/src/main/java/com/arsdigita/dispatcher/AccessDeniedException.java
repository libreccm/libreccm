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

import javax.servlet.http.HttpServletRequest;


/**
 * <tt>AccessDeniedException</tt> is the runtime exception that is thrown
 * whenever the current user does not have access to the requested resources.
 *
 * @author Michael Pih 
 * @version $Id$
 */
public class AccessDeniedException extends RuntimeException {


    public final static String ACCESS_DENIED =
        "com.arsdigita.cms.dispatcher.AccessDeniedException";


    // The default error detail message.
    private final static String ERROR_MSG = "Access Denied";

    // The URL where the AccessDeniedException is thrown.
    private String m_url;


    /**
     * Constructs an AccessDeniedException with the default detail message.
     */
    public AccessDeniedException() {
        this(ERROR_MSG);
    }

    /**
     * Constructs an AccessDeniedException with the specified detail message.
     *
     * @param msg The error detail message
     */
    public AccessDeniedException(String msg) {
        super(msg);

        // Try and fetch the current request URL.
        HttpServletRequest request = DispatcherHelper.getRequest();
        if  ( request != null ) {
            m_url = DispatcherHelper.getRequest().getRequestURI();
            request.setAttribute(ACCESS_DENIED, m_url);
        } else {
            m_url = null;
        }
    }

    /**
     * Fetches the URL where the AccessDeniedException originated.
     *
     * @return The original URL
     */
    public String getOriginalURL() {
        return m_url;
    }

}
