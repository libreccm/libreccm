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

import com.arsdigita.util.Assert;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * <p>A signal that returns the client to a return URL encoded in the
 * current URL, or if the return URL is not found, uses a fallback
 * URL.</p>
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public class ReturnSignal extends RedirectSignal {

    private static final Logger s_log = Logger.getLogger(ReturnSignal.class);
    private static final long serialVersionUID = -2923355745770322780L;

    public ReturnSignal(final HttpServletRequest sreq) {
        super(getReturnURL(sreq), true);
    }

    public ReturnSignal(final HttpServletRequest sreq, final String fallback) {
        super(getReturnURL(sreq, fallback), true);
    }

    public ReturnSignal(final HttpServletRequest sreq, final URL fallback) {
        this(sreq, fallback.toString());
    }

    private static String getReturnURL(final HttpServletRequest sreq) {
        s_log.debug("Fetching the return URL to redirect to");

        final String returnURL = sreq.getParameter("return_url");

        Assert.exists(returnURL, "String returnURL");

        if (s_log.isDebugEnabled()) {
            s_log.debug("Redirecting to URL '" + returnURL + "'");
        }

        return returnURL;
    }

    private static String getReturnURL(final HttpServletRequest sreq,
                                       final String fallback) {
        s_log.debug("Fetching the return URL to redirect to");

        Assert.exists(fallback, "String fallback");

        final String returnURL = sreq.getParameter("return_url");

        if (returnURL == null  || returnURL.equals("")) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Cannot find the return URL parameter; " +
                            "using the fallback URL '" + fallback + "'");
            }

            return fallback;
        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Redirecting to the value in the return URL " +
                            "parameter, '" + returnURL + "'");
            }

            return returnURL;
        }
    }
}
