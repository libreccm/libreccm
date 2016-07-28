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
import javax.servlet.http.HttpServletResponse;

/**
 * Request event class.  Passed as a
 * parameter to the methods of RequestListener.
 *
 * @see RequestListener
 * @author Bill Schneider
 * @version $Id$
 * @since 4.5 */
public class RequestEvent  {


    private HttpServletRequest m_req;
    private HttpServletResponse m_resp;
    private RequestContext m_ctx;
    private boolean m_start;
    private boolean m_finished_normal;

    /**
     * Creates a new <code>RequestEvent</code> with a success/failure status
     * code.
     * @param req the current request
     * @param resp the current response
     * @param ctx the current request context
     * @param start if true, indicates that this is a start-request event;
     * if false, this is an end-request event.
     * @param finishedNormal if true, indicates the request finished
     * without error.
     */
    public RequestEvent(HttpServletRequest req,
                        HttpServletResponse resp,
                        RequestContext ctx,
                        boolean start,
                        boolean finishedNormal) {
        m_req = req;
        m_resp = resp;
        m_ctx = ctx;
        m_start = start;
        m_finished_normal = finishedNormal;
    }


    /**
     * Creates a new <code>RequestEvent</code> with no status code.
     * @param req the current request
     * @param resp the current response
     * @param ctx the current request context
     * @param start if true, indicates that this is a start-request event;
     * if false, this is an end-request event.
     */
    public RequestEvent(HttpServletRequest req,
                        HttpServletResponse resp,
                        RequestContext ctx,
                        boolean start) {
        this(req, resp, ctx, start, false);
    }


    /**
     * Returns the current request for the request event.
     * @return the current request
     */
    public HttpServletRequest getRequest() {
        return m_req;
    }

    /**
     * Returns the current response for the request event.
     * @return the current response
     */
    public HttpServletResponse getResponse() {
        return m_resp;
    }

    /**
     * Returns the current request context for the request event.
     * @return the current request context
     */
    public RequestContext getRequestContext() {
        return m_ctx;
    }

    /**
     * Returns true if the event is a start-request event;
     * false for an end-request event.
     *
     * @return  true if we're starting a request, false at end.
     */
    public boolean isStart() {
        return m_start;
    }

    /**
     * Returns a status code to indicate whether the request
     * finished without error.
     *
     * @return true if the request finished without exception.
     * false if the request finished with an error, or if the request
     * event is a start-request.
     */
    public boolean finishedNormal() {
        return m_finished_normal;
    }
}
