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

import com.arsdigita.dispatcher.DispatcherHelper;
//import com.arsdigita.kernel.security.Util;
import com.arsdigita.util.Assert;
import com.arsdigita.util.servlet.HttpHost;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.libreccm.web.CcmApplication;

/**
 * <p>
 * URL models a future request according to the servlet worldview. Its principal
 * uses are two:
 *
 * <ul>
 * <li>To expose all the parts of a URL. To a servlet's way of thinking, these
 * are the scheme, server name, server port, context path, servlet path, path
 * info, and parameters.</li>
 *
 * <li>To generate URLs in a consistent and complete way in one place.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Each URL has the following accessors, here set next to an example URL
 * instance,
 * <code>http://example.com:8080/ccmapp/forum/index.jsp?cat=2&cat=5</code>:</p>
 *
 * <p>
 * Atomic parts:
 *
 * <pre><blockquote>
 *               getScheme() -> "http"
 *           getServerName() -> "example.com"
 *           getServerPort() -> 8080
 * getWebContextPath() -> "/ccmapp"
 *          getServletPath() -> "/forum"
 *             getPathInfo() -> "/index.jsp"
 *       getParameter("cat") -> "2"
 * getParameterValues("cat") -> {"2", "5"}
 * </blockquote></pre>
 *
 * </p>
 *
 * <p>
 * Composite parts:
 *
 * <pre><blockquote>
 *                toString() -> "/ccmapp/forum/index.jsp?cat=2&cat=5"
 *                  getURL() -> "http://example.com:8080/ccmapp/forum/index.jsp?cat=2&cat=5
 *            getServerURI() -> "http://example.com:8080"   // No trailing "/"
 *           getRequestURI() -> "/ccmapp/forum/index.jsp"
 *          getQueryString() -> "cat=2&cat=5"               // No leading "?"
 *         getParameterMap() -> {cat={"2", "5"}}
 * </blockquote></pre>
 *
 * </p>
 *
 * <p>
 * The <code>toString()</code> method returns a URL suitable for use in
 * hyperlinks; since in the common case, the scheme, server name, and port are
 * best left off, <code>toString()</code> omits them. The <code>getURL()</code>
 * method returns a <code>String</code> URL which is fully qualified. Both
 * <code>getURL()</code> and <code>getServerURI()</code> omit the port from
 * their return values if the server port is the default, port 80.</p>
 *
 * <p>
 * Creating URLs will usually be done via one of the static create methods:</p>
 *
 * <p>
 * <code>URL.root()</code> creates a URL pointing at the server's root path,
 * "/".</p>
 *
 * <p>
 * <code>URL.request(req, params)</code> creates a URL reflecting the request
 * the client made but using the passed-in parameters instead.</p>
 *
 * <p>
 * <code>URL.there(req, path, params)</code> and its variants produce URLs that
 * go through the CCM main dispatcher. The variant
 * <code>URL.there(req, app, pathInfo, params)</code> dispatches to
 * <code>pathInfo</code> under the specified application. The variant
 * <code>URL.here(req, pathInfo, params)</code> dispatches to
 * <code>pathInfo</code> under the current application.</p>
 *
 * <p>
 * <code>URL.excursion(req, path, params)</code> produces URLs that go through
 * the dispatcher to a destination but also encode and store the origin. This is
 * used by <code>LoginSignal</code> and <code>ReturnSignal</code> to implement
 * UI excursions.</p>
 *
 * <p>
 * All static create methods taking an <code>HttpServletRequest</code> (1)
 * preserve the request's scheme, server name, and port and (2) run parameter
 * listeners if the URL's parameter map is not null.
 * </p>
 *
 * <p>
 * Those methods not taking an <code>HttpServletRequest</code> use the scheme,
 * server name, and port defined in <code>LegacyWebConfig</code>.</p>
 *
 * <p>
 * All static create methods taking a <code>ParameterMap</code> take null to
 * mean no query string at all. URLs defined this way will have no query string
 * and no "?".</p>
 *
 * <p>
 * Those methods not taking a <code>ParameterMap</code> argument implicitly
 * create an empty parameter map. Note that this is different from creating a
 * URL with a null parameter map, which produces a URL with no query string.</p>
 *
 * @see com.arsdigita.web.ParameterMap
 * @see com.arsdigita.web.DispatcherServlet
 * @see com.arsdigita.web.LoginSignal
 * @see com.arsdigita.web.ReturnSignal
 * @see com.arsdigita.web.LegacyWebConfig
 * @see com.arsdigita.web.Application
 * @author Justin Ross
 * &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @version $Id$
 */
public class URL {

    /**
     * Internal logger instance to faciliate debugging. Enable logging output by
     * editing /WEB-INF/conf/log4j.properties int hte runtime environment and
     * set com.arsdigita.web.URL=DEBUG by uncommenting or adding the line.
     */
    private static final Logger s_log = Logger.getLogger(URL.class);

    public static final String THEMES_DIR = "/themes";

    /**
     * Base direcotry for template files provided by packages. Each package has
     * to place files into a subdirectory with its name
     */
    public static final String TEMPLATE_DIR = "/templates";

    /**
     * The standard location for servlets.
     */
    public static final String SERVLET_DIR = "/templates/servlet";

    public static final String INTERNAL_THEME_DIR = THEMES_DIR + "/heirloom";

    private static final ThreadLocal s_empty = new EmptyParameterMap();
    private StringBuffer m_url;
    private ParameterMap m_params;
    private int m_schemeEnd = -1;
    private int m_serverNameEnd = -1;
    private int m_serverPortEnd = -1;
    private int m_contextPathEnd = -1;
    private int m_servletPathEnd = -1;
    private int m_dispatcherPrefixEnd = -1;

    private void init(final String scheme,
                      final String serverName,
                      final int serverPort,
                      final String contextPath,
                      final String servletPath,
                      final String dispatcherPrefix,
                      final String pathInfo,
                      final ParameterMap params) {
        m_url = new StringBuffer(96);
        m_params = params;

        if (Assert.isEnabled()) {
            Assert.exists(scheme, "String scheme");
            Assert.isTrue(!scheme.equals(""),
                          "The scheme cannot be an empty string");

            Assert.exists(serverName, "String serverName");
            Assert.isTrue(serverPort > 0,
                          "The serverPort must be greater than 0; " + "I got "
                              + serverPort);

            Assert.exists(contextPath, "String contextPath");

            if (contextPath.startsWith("/")) {
                Assert.isTrue(!contextPath.endsWith("/"),
                              "A contextPath starting with '/' must not end in '/'; "
                              + "I got '" + contextPath + "'");
            }

            Assert.exists(servletPath, "String servletPath");

            if (pathInfo != null) {
                Assert.isTrue(pathInfo.startsWith("/"),
                              "I expected a pathInfo starting with '/' "
                                  + "and got '" + pathInfo + "' instead");
            }
        }

        m_url.append(scheme);
        m_schemeEnd = m_url.length();

        m_url.append("://");

        m_url.append(serverName);
        m_serverNameEnd = m_url.length();

        if (serverPort != 80) {
            m_url.append(':');
            m_url.append(serverPort);
        }

        m_serverPortEnd = m_url.length();

        m_url.append(contextPath);
        m_contextPathEnd = m_url.length();

        if (dispatcherPrefix != null) {
            m_url.append(dispatcherPrefix);
        }

        m_dispatcherPrefixEnd = m_url.length();

        m_url.append(servletPath);
        m_servletPathEnd = m_url.length();

        if (pathInfo != null) {
            m_url.append(pathInfo);
        }

        if (Assert.isEnabled()) {
            Assert.isTrue(m_schemeEnd > -1);
            Assert.isTrue(m_serverNameEnd > -1);
            Assert.isTrue(m_serverPortEnd > -1);
            Assert.isTrue(m_contextPathEnd > -1);
            Assert.isTrue(m_servletPathEnd > -1);
        }
    }

    /**
     * <p>
     * Assembles a fully qualified URL from its fundamental pieces. The contract
     * of URL dictates that once <code>params</code> is passed in to this
     * constructor, no parameters should be added or removed. This is to make
     * <code>URL</code> in practice a read-only object.</p>
     *
     * @param scheme      <code>"http"</code>, for example; see {@link
     * javax.servlet.ServletRequest#getScheme()}
     *
     * @param serverName  a valid domain name, for example
     *                    <code>"ccm.redhat.com"</code>; see {@link
     * javax.servlet.ServletRequest#getServerName()}
     *
     * @param serverPort  <code>8080</code>, for instance; see {@link
     * javax.servlet.ServletRequest#getServerPort()}
     *
     * @param contextPath the path to your web app; empty string indicates the
     *                    default context; any other values for contextPath must
     *                    start with <code>"/"</code> but not end in
     *                    <code>"/"</code>; contextPath cannot be null; see {@link
     * javax.servlet.http.HttpServletRequest#getContextPath()}
     *
     * @param servletPath the path to your servlet; empty string and values
     *                    starting with <code>"/"</code> are valid, but null is
     *                    not; see {@link
     * javax.servlet.http.HttpServletRequest#getServletPath()}
     *
     * @param pathInfo    the path data remaining after the servlet path but
     *                    before the query string; pathInfo may be null; see {@link
     * javax.servlet.http.HttpServletRequest#getPathInfo()}
     *
     * @param params      a <code>ParameterMap</code> representing a set of
     *                    query parameters
     *
     * @return a fully specified URL
     */
    public URL(final String scheme,
               final String serverName,
               final int serverPort,
               final String contextPath,
               final String servletPath,
               final String pathInfo,
               final ParameterMap params) {
        HttpServletRequest req = Web.getRequest();
        String dispatcherPrefix = req == null ? null : DispatcherHelper.
            getDispatcherPrefix(req);

        init(scheme,
             serverName,
             serverPort,
             contextPath,
             servletPath,
             dispatcherPrefix,
             pathInfo,
             params);
    }

    /**
     * (private) Constructor.
     *
     * @param sreq
     * @param params
     */
    private URL(final HttpServletRequest sreq,
                final ParameterMap params) {
        final String dispatcherPrefix = DispatcherHelper.getDispatcherPrefix(
            sreq);
        final HttpHost host = new HttpHost(sreq);

        init(sreq.getScheme(),
             host.getName(),
             host.getPort(),
             sreq.getContextPath(),
             sreq.getServletPath(),
             dispatcherPrefix,
             sreq.getPathInfo(),
             params);
    }

    /**
     * <p>
     * Constructor, produce a URL representation of the given request.</p>
     *
     * @param sreq an <code>HttpServletRequest</code> from which to copy
     *
     * @return a URL whose contents correspond to the request used to create it
     */
    public URL(final HttpServletRequest sreq) {
        this(sreq, new ParameterMap(sreq));
    }

    /**
     * <p>
     * Produces a short description of a URL suitable for debugging.</p>
     *
     * @return a debugging representation of this URL
     */
    public final String toDebugString() {
        return super.toString() + " " + "[" + getScheme() + ","
                   + getServerName() + "," + getServerPort() + ","
                   + getContextPath() + "," + getServletPath() + ","
                   + getDispatcherPrefix() + "," + getPathInfo() + ","
                   + getQueryString() + "]";
    }

    /**
     * Returns a <code>String</code> representation of the URL, fully qualified.
     * The port is omitted if it is the standard HTTP port, 80.
     *
     * @return a <code>String</code> URL, with all of its parts
     */
    public final String getURL() {
        if (m_params == null) {
            return m_url.toString();
        } else {
            return m_url.toString() + m_params;
        }
    }

    /**
     * <p>
     * Returns the scheme (sometimes called the protocol) of the URL. Examples
     * are <code>"http"</code> and <code>"https"</code>.</p>
     *
     * @see javax.servlet.ServletRequest#getScheme()
     * @return a <code>String</code> representing the URL's scheme
     */
    public final String getScheme() {
        return m_url.substring(0, m_schemeEnd);
    }

    /**
     * <p>
     * Returns the domain name part of the URL. For instance,
     * <code>"ccm.redhat.com"</code>.</p>
     *
     * @see javax.servlet.ServletRequest#getServerName()
     * @return a <code>String</code> representing the URL's server name
     */
    public final String getServerName() {
        return m_url.substring(m_schemeEnd + 3, m_serverNameEnd);
    }

    /**
     * <p>
     * Returns the port number of the URL.  <code>8080</code>, for example.</p>
     *
     * @see javax.servlet.ServletRequest#getServerPort()
     * @return an <code>int</code> for the URL's port number
     */
    public final int getServerPort() {
        final String port = m_url.substring(m_serverNameEnd, m_serverPortEnd);

        if (port.equals("")) {
            return 80;
        } else {
            return Integer.parseInt(port.substring(1));
        }
    }

    /**
     * <p>
     * Returns the server half of the URL, as opposed to the "file" half. For
     * example, "http://ccm.redhat.com:8080". Note that there is no trailing
     * slash; any characters following the server port are considered part of
     * the {@link #getRequestURI() request
     * URI}.</p>
     *
     * <p>
     * This method has no equivalent in the Servlet API, but it is similar in
     * spirit to {@link
     * javax.servlet.http.HttpServletRequest#getRequestURI()}.</p>
     *
     * <p>
     * It is defined to return
     *
     * <blockquote><code>getScheme() + "://" + getServerName() + ":"
     *   + getServerPort()</code></blockquote>
     *
     * or, if the server port is 80,
     *
     * <blockquote><code>getScheme() + "://" +
     *   getServerName()</code></blockquote>
     *
     * </p>
     *
     * @see #getRequestURI()
     * @return a <code>String</code> comprised of the scheme, server name, and
     *         server port plus connecting bits
     */
    public final String getServerURI() {
        return m_url.substring(0, m_serverPortEnd);
    }

    /**
     * <p>
     * Returns the context path of the URL. The value cannot be null, and values
     * starting with <code>"/"</code> do not end in <code>"/"</code>; empty
     * string is a valid return value that stands for the default web app.
     * Example values are <code>""</code> and <code>"/ccm-app"</code>.</p>
     *
     * @see javax.servlet.http.HttpServletRequest#getContextPath()
     * @return a <code>String</code> path to a web app context
     */
    public final String getContextPath() {
        return m_url.substring(m_serverPortEnd, m_contextPathEnd);
    }

    /**
     * <p>
     * Experimental</p>
     * <p>
     * Returns the dispatcher prefix of this request as set by the
     * InternalPrefixerServlet
     */
    public final String getDispatcherPrefix() {
        if (m_dispatcherPrefixEnd < m_servletPathEnd) {
            //there is no dispatcher prefix
            return "";
        } else {
            return m_url.substring(m_servletPathEnd, m_dispatcherPrefixEnd);
        }
    }

    /**
     * <p>
     * Returns the servlet path of the URL. The value cannot be null.</p>
     *
     * @see javax.servlet.http.HttpServletRequest#getServletPath()
     * @return a <code>String</code> path to a servlet
     */
    public final String getServletPath() {
        return m_url.substring(m_dispatcherPrefixEnd, m_servletPathEnd);
    }

    /**
     * <p>
     * Returns the servlet-local path data of the URL. The value may be null. If
     * it is not null, the value begins with a "/". Examples are
     * <code>null</code>, <code>"/"</code>, and <code>"/remove.jsp"</code>.</p>
     *
     * @see javax.servlet.http.HttpServletRequest#getPathInfo()
     * @return a <code>String</code> of path data addressed to a servlet
     */
    public final String getPathInfo() {
        final String pathInfo = m_url.substring(m_servletPathEnd);

        if (pathInfo.equals("")) {
            return null;
        } else {
            return pathInfo;
        }
    }

    /**
     * <p>
     * Returns the "file" part of the URL, in contrast to the
     * {@link #getServerURI() server part}. The value cannot be null and always
     * starts with a "/". For example, <code>"/ccm/forum/thread.jsp"</code>.</p>
     *
     * <p>
     * This method is defined to return the equivalent of      * <code>getWebContextPath() + getServletPath() +
 getPathInfo()</code>.</p>
     *
     * @see javax.servlet.http.HttpServletRequest#getRequestURI()
     * @return a <code>String</code> comprised of the context path, servlet
     *         path, and path info
     */
    public final String getRequestURI() {
        return m_url.substring(m_serverPortEnd);
    }

    /**
     * <p>
     * Returns the query string of the URL. If the URL was constructed with a
     * null <code>ParameterMap</code>, this method returns null. If the URL was
     * constructed with an empty <code>ParameterMap</code>, this method returns
     * the empty string. Example values are <code>null</code>, <code>""</code>,
     * and <code>"ticket-id=56&amp;user-id=24"</code>.</p>
     *
     * @see javax.servlet.http.HttpServletRequest#getQueryString()
     * @return a <code>String</code> representing the query parameters of the
     *         URL
     */
    public final String getQueryString() {
        if (m_params == null) {
            return null;
        } else {
            return m_params.getQueryString();
        }
    }

    /**
     * <p>
     * Returns the value of one query parameter. If the URL was constructed with
     * a null <code>ParameterMap</code>, this method returns null. If the
     * parameter requested has multiple values, this method will only return the
     * first; use {@link
     * #getParameterValues(String)} to get all of the values.</p>
     *
     * @see javax.servlet.http.HttpServletRequest#getParameter(String)
     * @param name the name of the parameter to fetch
     *
     * @return the <code>String</code> value of the parameter
     */
    public final String getParameter(final String name) {
        if (m_params == null) {
            return null;
        } else {
            return m_params.getParameter(name);
        }
    }

    /**
     * <p>
     * Returns the values for a parameter. If the URL was constructed with a
     * null <code>ParameterMap</code>, this method returns null.</p>
     *
     * @see javax.servlet.http.HttpServletRequest#getParameterValues(String)
     * @param name the name of the parameter to get
     *
     * @return a <code>String[]</code> of values for the parameter
     */
    public final String[] getParameterValues(final String name) {
        if (m_params == null) {
            return null;
        } else {
            return m_params.getParameterValues(name);
        }
    }

    /**
     * <p>
     * Returns an immutable map of the query parameters. The map's keys are
     * <code>String</code>s and the map's values are <code>String[]</code>s. If
     * the URL was constructed with a null <code>ParameterMap</code>, this
     * method returns null.</p>
     *
     * @see javax.servlet.http.HttpServletRequest#getParameterMap()
     * @return a <code>Map</code> of the URL's query parameters
     */
    public final Map getParameterMap() {
        if (m_params == null) {
            return null;
        } else {
            return m_params.getParameterMap();
        }
    }

    /**
     * <p>
     * Creates a URL to the site's root path. For example,
     * <code>http://somewhere.net/</code>.</p>
     *
     * @return a <code>URL</code> to your server's root path
     */
    public static final URL root() {
        final WebConfig config = Web.getConfig();

        URL url = new URL(config.getDefaultScheme(),
                          config.getServerName(),
                          config.getServerPort(),
                          "",
                          "/",
                          null,
                          null);

        return url;
    }

    /**
     * <p>
     * Creates a URL using the elements of the user's original request but with
     * the given set of parameters instead of the original ones.</p>
     *
     * @param sreq   the servlet request
     * @param params a <code>ParameterMap</code> of params to replace those of
     *               the request
     *
     * @return a <code>URL</code> representing the original request except for
     *         its parameters
     */
    public static final URL request(final HttpServletRequest sreq,
                                    final ParameterMap params) {
        if (params != null) {
            params.runListeners(sreq);
        }

        final URL url = Web.getWebContext().getRequestURL();

        if (url == null) {
            // If the URL is being generated outside of a WebContext,
            // use the request to fill out the URL.

            return new URL(sreq, params);
        } else {
            return new URL(url.getScheme(),
                           url.getServerName(),
                           url.getServerPort(),
                           url.getContextPath(),
                           url.getServletPath(),
                           url.getPathInfo(),
                           params);
        }
    }

    /**
     * <p>
     * Creates a URL to <code>path</code> under the CCM main dispatcher and with
     * the given parameters. A null <code>ParameterMap</code> indicates that the
     * URL has no query string at all. If the parameter map is not null, its
     * parameter listeners are run and may further edit the parameter map.</p>
     *
     * @see com.arsdigita.web.DispatcherServlet
     * @param sreq   the servlet request
     * @param path   a <code>String</code> path to which to dispatch
     * @param params a <code>ParameterMap</code> of parameters to use; this
     *               value may be null
     *
     * @return a <code>URL</code> with a path to dispatch to
     */
    public static final URL there(final HttpServletRequest sreq,
                                  final String path,
                                  final ParameterMap params) {
        final WebConfig config = Web.getConfig();

        Assert.exists(sreq, "HttpServletRequest sreq");
        Assert.exists(config, "WebConfig config");

        if (params != null) {
            params.runListeners(sreq);
        }

        final HttpHost host = new HttpHost(sreq);

        return new URL(sreq.getScheme(),
                       host.getName(),
                       host.getPort(),
                       CCMDispatcherServlet.getContextPath(),
                       config.getDispatcherServletPath(),
                       path,
                       params);
    }

    /**
     * Method similar to there(), but which checks the
     * waf.web.dynamic_host_provider parameter to generate the site name and
     * port dynamically.
     *
     * @see com.arsdigita.web.DispatcherServlet
     * @param sreq   the servlet request
     * @param path   a <code>String</code> path to which to dispatch
     * @param params a <code>ParameterMap</code> of parameters to use; this
     *               value may be null
     *
     * @return a <code>URL</code> with a path to dispatch to
     */
    public static final URL dynamicHostThere(final HttpServletRequest sreq,
                                             final String path,
                                             final ParameterMap params) {
        final WebConfig config = Web.getConfig();
        DynamicHostProvider provider = Web.getConfig().getDynamicHostProvider();
        if (provider == null) {
            return there(sreq, path, params);
        }

        Assert.exists(sreq, "HttpServletRequest sreq");
        Assert.exists(config, "WebConfig config");

        if (params != null) {
            params.runListeners(sreq);
        }

        final HttpHost host = new HttpHost(sreq);

        return new URL(sreq.getScheme(),
                       provider.getName(),
                       provider.getPort(),
                       CCMDispatcherServlet.getContextPath(),
                       config.getDispatcherServletPath(),
                       path,
                       params);
    }

    /**
     * <p>
     * Creates a URL with no local parameters to <code>path</code> under the CCM
     * main dispatcher. This method implicitly creates an empty parameter map
     * (not a null one); this empty map may be altered by parameter listeners,
     * for instance to include global parameters.</p>
     *
     * @param sreq the servlet request
     * @param path a <code>String</code> path to dispatch to
     *
     * @return a <code>URL</code> to a path under the dispatcher and with an
     *         empty parameter map
     */
    public static final URL there(final HttpServletRequest sreq,
                                  final String path) {
        final WebConfig config = Web.getConfig();

        Assert.exists(sreq, "HttpServletRequest sreq");
        Assert.exists(config, "WebConfig config");

        final HttpHost host = new HttpHost(sreq);

        return new URL(sreq.getScheme(),
                       host.getName(),
                       host.getPort(),
                       CCMDispatcherServlet.getContextPath(),
                       config.getDispatcherServletPath(),
                       path,
                       (ParameterMap) s_empty.get());
    }

    /**
     * <p>
     * Creates a URL to <code>pathInfo</code> under the specified application
     * and using the given parameters. The parmeter map argument may be null,
     * indicating that the URL has no query string.</p>
     *
     * @param sreq     the servlet request
     * @param app      the <code>CcmApplication</code> to dispatch to
     * @param pathInfo a <code>String</code> of extra path info for the
     *                 application
     * @param params   a <code>ParameterMap</code> of parameters to use
     *
     * @return a <code>URL</code> to an application with a particular
     *         <code>pathInfo</code>
     */
    public static final URL there(final HttpServletRequest sreq,
                                  final CcmApplication app,
                                  final String pathInfo,
                                  final ParameterMap params) {
        if (Assert.isEnabled() && pathInfo != null) {
            Assert.isTrue(pathInfo.startsWith("/"),
                          "pathInfo, if not null, must " + "start with a slash");
        }

        if (pathInfo == null) {
            return URL.there(sreq, app.getPrimaryUrl().toString(), params);
        } else {
            return URL.there(sreq, app.getPrimaryUrl().toString() + pathInfo, 
                                   params);
        }
    }

    /**
     * <p>
     * Creates a URL with no local parameters to <code>pathInfo</code> under the
     * specified application.
     *
     * @param sreq     the servlet request
     * @param app      the <code>CcmApplication</code> to dispatch to
     * @param pathInfo a <code>String</code> of extra path info for the
     *                 application
     *
     * @return a <code>URL</code> to an application with a particular
     *         <code>pathInfo</code>
     */
    public static final URL there(final HttpServletRequest sreq,
                                  final CcmApplication app,
                                  final String pathInfo) {
        if (Assert.isEnabled() && pathInfo != null) {
            Assert.isTrue(pathInfo.startsWith("/"),
                          "pathInfo, if not null, must " + "start with a slash");
        }

        if (pathInfo == null) {
            return URL.there(sreq, app.getPrimaryUrl().toString());
        } else {
            return URL.there(sreq, app.getPrimaryUrl().toString() + pathInfo);
        }
    }

    /**
     * <p>
     * Creates a URL with local parameters.</p>
     *
     * <p>
     * This function <b>should not be used</b> unless you really don't have an
     * <code>HttpServletRequest</code> object as it will ignore any Host header
     * given by the client.</p>
     *
     * @param path
     * @param params
     *
     * @return
     */
    public static final URL there(final String path,
                                  final ParameterMap params) {
        final WebConfig config = Web.getConfig();

        return new URL(config.getDefaultScheme(),
                       config.getServerName(),
                       config.getServerPort(),
                       "",
                       config.getDispatcherServletPath(),
                       path,
                       params);
    }

    /**
     * <p>
     * Create a URL with local parameters to <code>pathInfo</code> under the
     * specified application.</p>
     *
     * <p>
     * This function <b>should not be used</b> unless you really don't have an
     * <code>HttpServletRequest</code> object as it will ignore any Host header
     * given by the client.</p>
     */
    public static final URL there(final CcmApplication app,
                                  final String pathInfo,
                                  final ParameterMap params) {
        return URL.there(app.getPrimaryUrl() + pathInfo, params);
    }

    public static final URL here(final HttpServletRequest sreq,
                                 final String pathInfo,
                                 final ParameterMap params) {
        final CcmApplication app = Web.getWebContext().getApplication();

        Assert.exists(app, "Application app");

        return URL.there(sreq, app, pathInfo, params);
    }

    public static final URL here(final HttpServletRequest sreq,
                                 final String pathInfo) {
        final CcmApplication app = Web.getWebContext().getApplication();

        Assert.exists(app, "Application app");

        return URL.there(sreq, app, pathInfo);
    }

    public static URL excursion(final HttpServletRequest sreq,
                                final String path,
                                final ParameterMap params) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Creating excursion URL to " + path);
        }

        final URL url = URL.there(sreq, path, params);

        params.setParameter("return_url", Web.getWebContext().getRequestURL());

        return url;
    }

    public static URL excursion(final HttpServletRequest sreq,
                                final String path) {
        return URL.excursion(sreq, path, new ParameterMap());
    }

    static URL login(final HttpServletRequest sreq) {
        //Replace register eventuelly...
        return URL.excursion(sreq, 
                             "/register/",
                             (ParameterMap) s_empty.get());
    }

    final String getReturnURL() {
        return getParameter("return_url");
    }

    /**
     * Returns a <code>String</code> representation of the URL suitable for use
     * as a hyperlink. The scheme, server name, and port are omitted.
     *
     * @return a <code>String</code> URL
     */
    @Override
    public final String toString() {
        if (m_params == null) {
            return m_url.substring(m_serverPortEnd);
        } else {
            String str = m_url.substring(m_serverPortEnd);
            if (str.contains("?")) {
                return String.format("%s%s", m_url.substring(m_serverPortEnd),
                                     m_params.toString().replace('?', '&'));
            } else {
                return m_url.substring(m_serverPortEnd) + m_params;
            }
        }
    }

    /**
     *
     * @return
     */
    public static String getDispatcherPath() {
        final WebConfig config = Web.getConfig();
        final HttpServletRequest req = Web.getRequest();

        final String context = CCMDispatcherServlet.getContextPath();
        final String servlet = config.getDispatcherServletPath();

        if (req == null) {
            return context + servlet;
        } else {
            final String prefix = DispatcherHelper.getDispatcherPrefix(req);

            if (prefix == null) {
                return context + servlet;
            } else {
                return context + prefix + servlet;
            }
        }
    }

    private static class EmptyParameterMap extends InternalRequestLocal {

        @Override
        protected final Object initialValue() {
            return new ParameterMap();
        }

        @Override
        protected final void prepareValue(final HttpServletRequest sreq) {
            ((ParameterMap) get()).runListeners(sreq);
        }

        @Override
        protected final void clearValue() {
            ((ParameterMap) get()).clear();
        }

    }

}
