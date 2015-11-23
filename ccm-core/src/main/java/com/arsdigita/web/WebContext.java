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

// import com.arsdigita.web.CcmApplication;
import com.arsdigita.util.Assert;
import com.arsdigita.util.Record;

import org.apache.log4j.Logger;
import org.libreccm.web.CcmApplication;

/**
 * <p>
 * A session object that provides an environment in which code can execute. The
 * WebContext contains all session-specific variables. One session object is
 * maintained per thread.</p>
 *
 * <p>
 * Accessors of this class may return null. Developers should take care to trap
 * null return values in their code.</p>
 *
 * @author Rafael Schloming
 * @author Justin Ross
 * @version $Id$
 */
public final class WebContext extends Record {

    /**
     * Internal logger instance to faciliate debugging. Enable logging output by
     * editing /WEB-INF/conf/log4j.properties int the runtime environment and
     * set com.arsdigita.web.WebContext=DEBUG by uncommenting or adding the
     * line.
     */
    private static final Logger s_log = Logger.getLogger(WebContext.class);

    private CcmApplication m_application = null;
    private URL m_requestURL = null;

    /**
     * List of properties making up a Web Context
     */
    private static String[] s_fields = new String[]{
        "User",
        "Application",
        "RequestURL"
    };

    /**
     * Constructor
     */
    WebContext() {
        super(WebContext.class, s_log, s_fields);
    }

    /**
     * Creates a copy of this WebContext
     *
     * @return a new WebContext as a copy of this one
     */
    final WebContext copy() {
        WebContext result = new WebContext();

        result.m_application = m_application;
        result.m_requestURL = m_requestURL;

        return result;
    }

    /**
     * Initializes this WebContext object and setting its properties.
     *
     * @param app
     * @param requestURL
     */
    final void init(final CcmApplication app, final URL requestURL) {
        setApplication(app);
        setRequestURL(requestURL);
    }

    final void clear() {
        m_application = null;
        m_requestURL = null;
    }

    /**
     *
     * @return
     */
    public final CcmApplication getApplication() {
        return m_application;
    }

    /**
     *
     * @param app
     */
    final void setApplication(final CcmApplication app) {
        m_application = app;

        mutated("Application");
    }

    /**
     *
     * @return
     */
    public final URL getRequestURL() {
        return m_requestURL;
    }

    /**
     *
     * @param url
     */
    final void setRequestURL(final URL url) {
        Assert.exists(url, "URL url");

        m_requestURL = url;

        mutated("RequestURL");
    }

}
