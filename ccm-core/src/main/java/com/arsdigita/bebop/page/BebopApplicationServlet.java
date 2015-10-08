/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
 */
package com.arsdigita.bebop.page;

import com.arsdigita.bebop.Page;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import com.arsdigita.util.Assert;
import com.arsdigita.web.BaseApplicationServlet;
import com.arsdigita.xml.Document;

import org.apache.log4j.Logger;
import org.libreccm.web.CcmApplication;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A common servlet to provide a generic URL-to-Bebop-Page map based dispatch
 * pattern. It provides methods to setup a url - page map. The doService method
 * uses the request's URL to determine the page to display and forwards to the
 * presentation manager for the Page handling.
 *
 * This class is a servlet based version of BebopMapDispatcher and associated
 * classes and is generally used in the same way by legacy free applications.
 *
 * Subclasses usually overwrite the doInit() method to create Page objects and
 * use this.put method to construct the mapping.
 *
 * Subclasses may overwrite the doService method to add additional
 * functionality, e.g. permission checking.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author chris gilbert - allow BebopApplicationServlet pages to disable
 * client/middleware
 */
public class BebopApplicationServlet extends BaseApplicationServlet {

    private static final long serialVersionUID = -6004503025521189639L;

    private static final Logger s_log = Logger.getLogger(
        BebopApplicationServlet.class);

    /**
     * URL (pathinfo) -> Page object mapping. Based on it (and the http request
     * url) the doService method to selects a page to display
     */
    private final Map m_pages = new HashMap();
    // Set of pathinfo
    private final Set m_clientCacheDisabledPages = new HashSet();

    /**
     * Initializer uses parent class's initializer to setup the servlet request
     * / response and application context. Usually a user of this class will NOT
     * overwrite this method but the user extension point doInit() to perform
     * local initialization tasks, in case of this servlet typically to setup
     * the page-url mapping using the provided mapping methods of this class.
     *
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        super.init();
    }

    /**
     * User extension point, overwrite this method to setup a URL - page mapping
     *
     * @throws ServletException
     */
    @Override
    public void doInit() throws ServletException {
        // nothing here
    }

    /**
     * Adds one Url-Page mapping to the internal mapping table.
     *
     * @param pathInfo url stub for a page to display
     * @param page     Page object to display
     */
    protected final void put(final String pathInfo,
                             final Page page) {
        Assert.exists(pathInfo, String.class);
        Assert.exists(page, Page.class);
        // Current Implementation requires pathInfo to start with a leading '/'
        // SUN Servlet API specifies: "PathInfo *may be empty* or will start
        // with a '/' character."
        Assert.isTrue(pathInfo.startsWith("/"), "path starts not with '/'");

        m_pages.put(pathInfo, page);
    }

    /**
     * Adds the provided page as specified by its pathInfo information to an
     * internal list of pages for which client/middleware caching will be
     * disabled when serving the page (added by Chris Gilbert).
     *
     * @param pathInfo - the same path used to add the page when put was called
     */
    protected final void disableClientCaching(String pathInfo) {
        Assert.exists(pathInfo, String.class);
        Assert.isTrue(m_pages.containsKey(pathInfo),
                      "Page " + pathInfo + " has not been put in servlet");
        m_clientCacheDisabledPages.add(pathInfo);
    }

    /**
     * Main processing unit searches in the page map for the request's url and
     * forwards the page to display to the appropriate presentation manager to
     * serve the page.
     *
     * @param sreq
     * @param sresp
     * @param app
     *
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected final void doService(final HttpServletRequest sreq,
                                   final HttpServletResponse sresp,
                                   final CcmApplication app)
        throws ServletException, IOException {

        final String pathInfo = sreq.getPathInfo();
        Assert.exists(pathInfo, "String pathInfo");

        final Page page = (Page) m_pages.get(pathInfo);

        if (page == null) {
            sresp.sendError(404, "Application not found");
            throw new IllegalStateException("No such page for path " + pathInfo);
        } else {
            if (m_clientCacheDisabledPages.contains(pathInfo)) {
                DispatcherHelper.cacheDisable(sresp);
            }
            preprocessRequest(sreq, sresp, app, pathInfo);
            final Document doc = page.buildDocument(sreq, sresp);

            PresentationManager pm = Templating.getPresentationManager();
            pm.servePage(doc, sreq, sresp);
        }
    }

    /**
     * Provides the opportunity for subclasses to do some preprocessing of a
     * given url, before it is handed off to main service process. One typical
     * action is to ensure permissions.
     *
     * @param sreq
     * @param sresp
     * @param app
     * @param url
     *
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    protected void preprocessRequest(HttpServletRequest sreq,
                                     HttpServletResponse sresp,
                                     CcmApplication app,
                                     String url)
        throws ServletException, IOException {
        // Nothing to do by default.
    }

}
