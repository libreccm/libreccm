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

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Basic dispatcher class for dispatching URLs to JSP or
 * other file-based assets (images, CSS, HTML, etc.) in the file
 * system in the context of a single package's URL space.
 * <p>
 * Given an application context that contains a SiteNode, and a
 * remaining URL, tries to resolve the remaining URL into a concrete file:<br>
 * $root/packages/$key/$remaining-url<br>
 *
 * where<br>
 *  $root is the ACS webapp root<br>
 *  $key is siteNode.getPackageKey()<br>
 *  $remaining-url is given by appContext.getRemainingURLPart()
 *
 * <p> To make the URL mapping in JSPApplicationDispatcher work, you
 * must map the "jsp" servlet in web.xml to whatever your entry-point
 * dispatcher is (whatever servlet is mapped to the URL pattern "/").
 * Otherwise, requests for *.jsp will get picked up by the JSP
 * container before any ACS dispatchers have a shot at URL mapping.
 *
 * <p>
 * Example: if the application sample-app is mounted on the site nodes
 * /sample1 and /sample2 then URLs will be dispatched as follows:
 * <table>
 * <tr><th>Request URL <th>File served</tr>
 * <tr><td> /sample1/page.jsp</td> <td> /packages/sample-app/www/page.jsp</td>
 * </tr>
 * <tr><td> /sample2/image.gif</td> <td> /packages/sample-app/www/image.gif</td>
 * </tr>
 * <tr><td> /sample2/script.jsp</td> <td> /packages/sample-app/www/script.jsp</td>
 * </tr>
 * </table>
 * @author Bill Schneider 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class JSPApplicationDispatcher extends BaseDispatcherServlet
                                      implements Dispatcher  {

    private static final Logger LOGGER = LogManager.getLogger
        (JSPApplicationDispatcher.class);

    private static JSPApplicationDispatcher s_instance = newInstance();
    private static final long serialVersionUID = 1662461509796743896L;

    /**
     * Returns a new instance of a JSPApplicationDispatcher.
     * @return a new JSPApplicationDispatcher.
     */
    public static JSPApplicationDispatcher newInstance() {
        return new JSPApplicationDispatcher();
    }

    /**
     * Returns a new instance of JSPApplicationDispatcher.
     * @return a JSPApplicationDispatcher object.
     * @deprecated No longer returns a singleton instance.  Remains
     * in place for API stability.
     */
    public static JSPApplicationDispatcher getInstance() {
        return s_instance;
    }

    // No Authentication is performed here.
    protected RequestContext authenticateUser(HttpServletRequest req,
                                              HttpServletResponse resp,
                                              RequestContext ctx)
        throws RedirectException {
        return ctx;
    }

    public void dispatch(HttpServletRequest req,
                         HttpServletResponse resp,
                         RequestContext actx)
        throws IOException, ServletException {

        // Set the request context as a request attribute because a
        // JSP page might need it, which doesn't get passed the
        // request context as a parameter.
        DispatcherHelper.setRequestContext(req, actx);

        ServletContext sctx = actx.getServletContext();
        String remainingURL = actx.getRemainingURLPart();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("I think the remaining URL is '" + remainingURL + "'");
        }

        // This is where we forward a request from /foo1/bar.ext or
        // /foo2/bar.ext to /packages/foo/www/bar.ext the concrete
        // file should then get picked up by BaseDispatcherServlet.

        String concreteURL =
            actx.getPageBase() +
            actx.getRemainingURLPart();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Looking for a concrete resource under the web app " +
                        "context at '" + concreteURL + "'");
        }

        File concreteFile = new File(sctx.getRealPath(concreteURL));

        if (concreteFile.exists()) {
            LOGGER.debug("Resource was found; forwarding");
            DispatcherHelper.setRequestContext(req, actx);
            DispatcherHelper.forwardRequestByPath(concreteURL, req, resp);
        } else {
            LOGGER.debug("Resource not found");
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
