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

//import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.util.Assert;
import com.arsdigita.util.ParameterProvider;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.URLRewriter;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import javax.mail.MessagingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

/**
 * Class static helper methods for request dispatching.
 * Contains various generally useful procedural abstractions.
 *
 * @author Bill Schneider
 * @since 4.5 
 * @version $Id$
 */
public final class DispatcherHelper implements DispatcherConstants {

    /** Internal logger instance to faciliate debugging. Enable logging output
     *  by editing /WEB-INF/conf/log4j.properties int hte runtime environment
     *  and set com.arsdigita.dispatcher.DispatcherHelper=DEBUG 
     *  by uncommenting or adding the line.                                   */
    private static final Logger s_log = Logger.getLogger(DispatcherHelper.class);
    private static String s_webappCtx;
    private static String s_staticURL;
    private static boolean s_cachingActive;
    private static int s_defaultExpiry;
    public static SimpleDateFormat rfc1123_formatter;
    private static boolean initialized = false;

    static void init() {
        if (initialized) {
            return;
        }

        rfc1123_formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        rfc1123_formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        s_staticURL = DispatcherConfig.getConfig().getStaticUrlPrefix();
        s_defaultExpiry = DispatcherConfig.getConfig().getDefaultExpiry();
        s_cachingActive = DispatcherConfig.getConfig().isCachingActive();

        initialized = true;
    }

    /** The current HttpServletRequest.                                      */
    private static final ThreadLocal s_request = new ThreadLocal();

    /** null constructor, private so no one can instantiate! */
    private DispatcherHelper() {
    }

    /**
     * Return default cache expiry.
     * Default is specified in the configuration file (registry) if not 
     * otherweise set.
     * 
     * @return default cache expiry
     */
    public static int getDefaultCacheExpiry() {
        init();
        return s_defaultExpiry;
    }

    static void setDefaultCacheExpiry(int expiry) {
        init();
        s_defaultExpiry = expiry;
    }

    public static boolean isCachingActive() {
        init();
        return s_cachingActive;
    }

    static void setCachingActive(boolean status) {
        init();
        s_cachingActive = status;
    }

    /**
     * Returns the URL path (relative to the webapp root) for the
     * current (calling) resource.  This works around the quirk that,
     * if servlet A includes servlet B, calling getRequestURI() in B
     * returns "A" and not "B".
     *
     * @param req
     * @return the URL path (relative to the webapp root) for the currently
     *         executing resource.
     */
    public static String getCurrentResourcePath(HttpServletRequest req) {
        String attr = (String) req.getAttribute(INCLUDE_URI);
        String str;
        if (attr == null) {
            str = req.getRequestURI();
            if (str.indexOf("?") > -1) {
                str = str.substring(0, str.indexOf("?"));
            }
        } else {
            str = attr;
        }
        int startIndex = req.getContextPath().length();
        str = str.substring(startIndex);
        // fix-up URL -- sometimes broken (Tomcat serving error pages)
        if (str.startsWith("//")) {
            str = str.substring(1);
        }
        return str;
    }

    /**
     * Gets the application context from the request attributes.
     * @param req
     * @return the application context from the request attributes.
     */
    public static RequestContext getRequestContext(HttpServletRequest req) {
        return (RequestContext) req.getAttribute(REQUEST_CONTEXT_ATTR);
    }

    public static RequestContext getRequestContext() {
        return (RequestContext) getRequest().getAttribute(REQUEST_CONTEXT_ATTR);
    }

    public static String getDispatcherPrefix(HttpServletRequest req) {
        return (String) req.getAttribute(DISPATCHER_PREFIX_ATTR);
    }

    public static void setDispatcherPrefix(HttpServletRequest req,
                                           String val) {
        req.setAttribute(DISPATCHER_PREFIX_ATTR, val);
    }

    /**
     * Sets the current request context as a request attribute for
     * later retrieval.
     *
     * @param req the current request object
     * @param ac the current request context
     * @post DispatcherHelper.getRequestContext(req) == ac
     */
    public static void setRequestContext(HttpServletRequest req, RequestContext ac) {
        req.setAttribute(REQUEST_CONTEXT_ATTR, ac);
    }

    private static void forwardHelper(javax.servlet.RequestDispatcher rd,
                                      HttpServletRequest req,
                                      HttpServletResponse resp)
            throws ServletException, IOException {

        Object attr = req.getAttribute(INCLUDE_URI);

        // Yes this does mean we're throwing away any POSTed
        // data from a MultipartHttpServletRequest, but we've
        // got to do what Servlet API specs say. Specifically
        // tomcat assumes  it gets back the original request :(
        // So we need to go through hoops to 'unrestore' the
        // original request when it comes back to us in just
        // a second...
        // Of course if the request disappears off to a 3rd
        // party servlet we're screwed
        req = restoreOriginalRequest(req);
        s_log.debug("Forwarding the request object " + req);
        if (attr != null) {
            rd.include(req, resp);
            req.setAttribute(INCLUDE_URI, attr);
        } else {
            try {
                rd.forward(req, resp);
            } catch (IllegalStateException e) {
                rd.include(req, resp);
            }
        }
    }

    /**
     * Forwards the request from this resource to another resource at
     * the servlet-container level.  This is a wrapper for
     * javax.servlet.RequestDispatcher
     * and is intended to hide the fact that you can't "forward" a request
     * once you've done an "include" in it.
     * @param path the URL of the resource, relative to the webapp
     * root. For example, if you request a JSP page with /context/foo/bar,
     * you would call this method with path == /foo/bar.
     * @param req the current request
     * @param resp the current response
     * @param sctx the current servlet context
     * @exception java.io.IOException may be propagated from target resource
     * @exception javax.servlet.ServletException may be
     * propagated from target resource
     */
    public static void forwardRequestByPath(String path,
                                            HttpServletRequest req,
                                            HttpServletResponse resp,
                                            ServletContext sctx)
            throws IOException, ServletException {
        RequestDispatcher rd = sctx.getRequestDispatcher(path);
        forwardHelper(rd, req, resp);
    }

    /**
     * Equivalent to <code>forwardRequestByPath(path, req, resp,
     * DispatcherHelper.getRequestContext(req).getServletContext())</code>.
     * @param path
     * @param req
     * @param resp
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    public static void forwardRequestByPath(String path,
                                            HttpServletRequest req,
                                            HttpServletResponse resp)
                       throws IOException, ServletException {
        ServletContext sctx =
                       DispatcherHelper.getRequestContext(req).getServletContext();
        forwardRequestByPath(path, req, resp, sctx);
    }

    /**
     * Forwards the request from this resource to another resource at
     * the JSP container level.  This is a wrapper for
     * <code>PageContext.forward</code> and <code>PageContext.include</code>
     * and is intended to transparently switch between "forward" and
     * "include" depending on whether or not an include has already been
     * done on the request.
     *
     * @param path the URL of the resource, relative to the webapp
     * root. For example, if you request a JSP page with /context/foo/bar,
     * you would call this method with path == /foo/bar.
     *
     * @param pageContext the JSP page context
     *
     * @exception java.io.IOException may be propagated from target resource
     * @exception javax.servlet.ServletException may be
     * propagated from target resource
     */
    public static void forwardRequestByPath(String path,
                                            PageContext pageContext)
            throws IOException, ServletException {

        ServletRequest req = pageContext.getRequest();
        Object attr = req.getAttribute(INCLUDE_URI);
        // restore original request if we're using the wrapped
        // multipart request here
        if (attr != null) {
            pageContext.include(path);
            req.setAttribute(INCLUDE_URI, attr);
        } else {
            try {
                pageContext.forward(path);
            } catch (IllegalStateException e) {
                pageContext.include(path);
            }
        }
    }

    /**
     * Forwards the request from this resource to a servlet resource
     * (named in server.xml) at the servlet-container level.  This is a
     * wrapper for javax.servlet.RequestDispatcher and is intended to hide
     * the fact that you can't "forward" a request  once you've done an
     * "include" in it.
     * @param name the named servlet to forward to
     * @param req the current request
     * @param resp the current response
     * @param sctx the current servlet context
     * @exception java.io.IOException may be propagated from target resource
     * @exception javax.servlet.ServletException may be
     * propagated from target resource
     */
    public static void forwardRequestByName(String name,
                                            HttpServletRequest req,
                                            HttpServletResponse resp,
                                            ServletContext sctx)
            throws IOException, ServletException {
        RequestDispatcher rd = sctx.getNamedDispatcher(name);
        forwardHelper(rd, req, resp);
    }

    /**
     * Equivalent to <code>forwardRequestByName(name, req, resp,
     * DispatcherHelper.getRequestContext(req).getServletContext())</code>.
     * 
     * @param name
     * @param req
     * @param resp
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    public static void forwardRequestByName(String name,
                                            HttpServletRequest req,
                                            HttpServletResponse resp)
            throws IOException, ServletException {
        ServletContext sc = getRequestContext(req).getServletContext();
        forwardRequestByName(name, req, resp, sc);
    }

    /**
     * Given the name of a resource in the file system that is missing an
     * extension, picks an extension that matches.  Serves a file
     * with a <code>.jsp</code> extension first, if available.
     * Otherwise picks any file that matches.  For directories, it tacks on
     * the "index" filename plus the extension.
     *
     * <b><font color="red">Unsupported</font></b>
     *
     * @param abstractFile the extensionless file
     * @param actx the current application context
     * @return a filename suffix (".jsp", "index.html", etc.) such
     * that (abstractFile.getAbsolutePath() + suffix) is a valid file
     * in the filesystem
     *
     * @exception com.arsdigita.dispatcher.RedirectException if the
     * requested file is a directory and the original request URL does
     * not end with a trailing slash.
     * @exception java.io.FileNotFoundException if no matching
     * file exists.
     * @throws com.arsdigita.dispatcher.DirectoryListingException
     * @deprecated abstract URLs are no longer supported.  Use
     * extensions when your file on disk has an extension.
     */
    public static String resolveAbstractFile(File abstractFile,
                                             RequestContext actx)
            throws RedirectException, DirectoryListingException,
                   java.io.FileNotFoundException {
        s_log.debug("Resolving abstract file");

        File dirToSearch = null;
        String fStr = abstractFile.getAbsolutePath();
        int lastSlash = fStr.lastIndexOf(File.separatorChar);
        String filenameStub = fStr.substring(lastSlash + 1);

        boolean indexPage = false;
        if (abstractFile.isDirectory()) {
            if (!actx.getOriginalURL().endsWith("/")) {
                // redirect to prevent confused browser
                throw new RedirectException(actx.getOriginalURL() + "/");
            }
            dirToSearch = abstractFile;
            filenameStub = "index";
            indexPage = true;
        } else if (abstractFile.exists()) {
            // file exists and is not a directory; don't resolve any
            // further
            return "";
        } else {
            dirToSearch = new File(abstractFile.getParent());
        }


        File filesInDir[] = dirToSearch.listFiles();

        final String extensionSearchList[] = {".jsp"};

        if (filesInDir != null) {
            for (String searchExtension : extensionSearchList) { //1.5 enhanced loop
                File possibleFile = new File(dirToSearch, 
                                             filenameStub + searchExtension);
                for (int i = 0; i < filesInDir.length; i++) {
                    if (filesInDir[i].equals(possibleFile)) {
                        return (indexPage ? File.separator + "index" : "") 
                               + searchExtension;
                    }
                }
            }

            // no preferential matches, so just match whatever
            // we can
            // note that if we have an index page, we really don't
            // want to match the abstractFile itself because it's
            // a directory; we want to match the "index.*" file
            // IN this directory.
            File abstractFileIndex = new File(abstractFile, "index");
            for (int i = 0; i < filesInDir.length; i++) {
                String fidStr = filesInDir[i].getPath();
                int lastDot = fidStr.lastIndexOf(".");
                if (lastDot == -1) {
                    // Match must have extension
                    // perfect match already tested
                    continue;
                }
                File possibleStub = new File(fidStr.substring(0, lastDot));
                if (indexPage && abstractFileIndex.equals(possibleStub)) {
                    return "index" + fidStr.substring(lastDot);
                } else if (abstractFile.equals(possibleStub)) {
                    return fidStr.substring(lastDot);
                }
            }
        }
        if (!abstractFile.isDirectory()) {
            // couldn't find anything and not a directory, so throw
            // An exception
            throw new FileNotFoundException(abstractFile.getAbsolutePath());
        } else {
            // we have a directory, no index file, maybe serve
            // as a directory listing?
            throw new DirectoryListingException(abstractFile.getAbsolutePath());
        }
    }

    /**
     * If the given servlet request is wrapped in one of our own classes, returns
     * the original (unwrapped) request object and stores a reference
     * to the request wrapper in the request attributes of the returned request.
     * Otherwise just returns the request object.
     *
     * @param req the servlet request
     * @return the original servlet request object, as created by
     * the servlet container. This can be used as a parameter for forward().
     */
    public static HttpServletRequest restoreOriginalRequest(HttpServletRequest req) {
        if (req instanceof MultipartHttpServletRequest) {
            HttpServletRequest oldReq = (HttpServletRequest) req.getAttribute(ORIGINAL_REQUEST_ATTR);
            oldReq.setAttribute(WRAPPED_REQUEST_ATTR, req);
            req = oldReq;
        }
        return req;
    }

    /**
     * If we've stored a reference to a request wrapper as a request
     * attribute to the current servlet request, returns the wrapper object.
     * Otherwise, returns the request object.
     *
     * @param req the current servlet request
     * @return the previously created wrapper around the current servlet
     *         request, if any;
     *         otherwise returns the request object.
     */
    public static HttpServletRequest restoreRequestWrapper(HttpServletRequest req) {
        // switch back wrapped request if we're forwarded
        // from somewhere else.
        Object maybeWrappedReq = req.getAttribute(WRAPPED_REQUEST_ATTR);
        if (maybeWrappedReq != null
            && !(req instanceof MultipartHttpServletRequest)) {
            req = (HttpServletRequest) maybeWrappedReq;
        }
        return req;
    }

    /**
     * This method will optionally wrap the request if it  is a multipart POST,
     * or restore the original wrapper if it was already wrapped.
     * 
     * @param sreq
     * @return 
     * @throws java.io.IOException 
     * @throws javax.servlet.ServletException 
     */
    public static HttpServletRequest maybeWrapRequest(HttpServletRequest sreq)
            throws IOException, ServletException {
        final String type = sreq.getContentType();

        if (sreq.getMethod().toUpperCase().equals("POST")
            && type != null
            && type.toLowerCase().startsWith("multipart")) {
            final HttpServletRequest orig = sreq;

            final HttpServletRequest previous =
                                     DispatcherHelper.restoreRequestWrapper(orig);

            if (previous instanceof MultipartHttpServletRequest) {
                s_log.debug("Build new multipart request from previous "
                            + previous + " and current " + orig);

                MultipartHttpServletRequest previousmp =
                                            (MultipartHttpServletRequest) previous;

                sreq = new MultipartHttpServletRequest(previousmp,
                                                       orig);

                DispatcherHelper.saveOriginalRequest(sreq,
                                                     orig);

                s_log.debug("The main request is now " + sreq);
            } else {
                s_log.debug("The request is a new multipart; wrapping the request "
                            + "object");
                try {
                    sreq = new MultipartHttpServletRequest(sreq);
                } catch (MessagingException me) {
                    throw new ServletException(me);
                }

                DispatcherHelper.saveOriginalRequest(sreq, orig);
            }
        } else {
            s_log.debug("The request is not multipart; proceeding "
                        + "without wrapping the request");
        }
        return sreq;
    }

    /**
     * Stores req as request attribute of oldReq.
     * @param req the current servlet request (wrapper)
     * @param oldReq the original servlet request
     */
    public static void saveOriginalRequest(HttpServletRequest req,
                                           HttpServletRequest oldReq) {
        req.setAttribute(ORIGINAL_REQUEST_ATTR, oldReq);
    }

    /**
     * Redirects the client to the given URL without rewriting it. Delegates
     * to the sendExternalRedirect method.
     *
     * @throws java.io.IOException
     * @deprecated This method does not rewrite URLs.  Use
     * sendRedirect(HttpServletRequest, HttpServletResponse, String) for
     * redirects within this ACS or
     * sendExternalRedirect(HttpServletResponse, String) for redirects to
     * sites outside this ACS.
     *
     * @param resp the current response
     * @param url the destination URL for redirect
     **/
    public static void sendRedirect(HttpServletResponse resp,
                                    String url)
                       throws IOException {
        sendExternalRedirect(resp, url);
    }

    /**
     * Rewrites the given URL and redirects the client to the rewritten URL.
     * This method should be used for redirects within this ACS.
     *
     * @param req the current request; used as a source for parameters
     * for URL rewriting
     * @param resp the current response
     * @param url the destination URL for redirect
     * @throws java.io.IOException
     **/
    public static void sendRedirect(HttpServletRequest req,
                                    HttpServletResponse resp,
                                    String url)
                       throws IOException {
        sendExternalRedirect(resp, url);
    }

    /**
     * Redirects the client to the given URL without rewriting it.  This
     * method should be used for redirects to sites outside this ACS.
     *
     * @param resp the current response
     * @param url the destination URL for redirect
     * @throws java.io.IOException
     **/
    public static void sendExternalRedirect(HttpServletResponse resp,
                                            String url)
                       throws IOException {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Redirecting to URL '" + url + "'", new Throwable());
        }

        if (StringUtils.emptyString(url)) {
            // This is a fix so that redirecting to empty string
            // (i.e. index file in current directory)
            // works properly when running in Apache.
            // DEE 3/13/01 the original apache redirect-fix string of "?"
            // has been replaced with ".", because
            // IE will reload the current page if redirected to "?".
            url = ".";
        }

        HttpServletRequest req = getRequest();
        Object attr;
        if (req != null
            && (attr = req.getAttribute(REENTRANCE_ATTRIBUTE)) != null) {
            req.getSession(true).setAttribute(REDIRECT_SEMAPHORE, attr);
        }

        if (url.startsWith("http")) {
            final int start = url.indexOf("/", url.indexOf("//") + 2);
            final String path = start >= 0 ? url.substring(start) : "/";

            if (!path.startsWith(URL.getDispatcherPath())) {
                url = path;
            }
        }

        if (url.startsWith("/")) {
            final int sep = url.indexOf('?');
            URL destination = null;

            if (sep == -1) {
                destination = URL.there(req, url);
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Setting destination to " + destination);
                }
            } else {
                final ParameterMap params = ParameterMap.fromString(url.substring(sep + 1));

                destination = URL.there(req, url.substring(0, sep), params);
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Setting destination with map to "
                                + destination);
                }
            }
            throw new RedirectSignal(destination, true);
        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Redirecting to URL without using URL.there. "
                            + "URL is " + url);
            }
            throw new RedirectSignal(url, true);
        }
    }

    /**
     * Adds a <code>ParameterProvider</code> to the URLRewriter engine.
     * <code>ParameterProvider</code>s are used when
     * <code>encodeRedirectURL</code> and <code>encodeURL</code> are
     * called. They add global state parameters like the session ID (for
     * cookieless login) to URLs for links and redirects.
     *
     * @param provider the parameter provider to add
     * @see com.arsdigita.util.URLRewriter#addParameterProvider
     * @deprecated use URLRewriter.addParameterProvider
     */
    public static void addParameterProvider(ParameterProvider provider) {
        URLRewriter.addParameterProvider(provider);
    }

    /**
     * Clears all parameter providers.
     * @deprecated use URLRewriter#clearParameterProviders() instead
     * @see com.arsdigita.util.URLRewriter#clearParameterProviders()
     **/
    public static void clearParameterProviders() {
        URLRewriter.clearParameterProviders();
    }

    /**
     * Returns the set of global parameter models, or the empty set if no
     * provider is set.
     *
     * @return a set of Bebop parameter models.
     * @deprecated use URLRewriter.getGlobalModels instead
     * @see com.arsdigita.util.URLRewriter#getGlobalModels()
     **/
    public static Set getGlobalModels() {
        return URLRewriter.getGlobalModels();
    }

    /**
     * Returns the set of global URL parameters for the given request, or
     * the empty set if no provider is set.
     *
     * @param req the current request
     * @return a Set of Bebop parameter data.
     * @deprecated use URLRewriter.getGlobalParams instead
     **/
    public static Set getGlobalParams(HttpServletRequest req) {
        return URLRewriter.getGlobalParams(req);
    }

    /**
     * Prepares the given URL for a client link.  If no providers are
     * set, has no effect.
     *
     * @param url the target URL to prepare
     * @return the prepared URL with global parameters added from
     * providers
     *
     * @deprecated This method does not encode the servlet session ID.  Use
     * encodeURL(req, res, url) instead.
     **/
    public static String prepareURL(String url, HttpServletRequest req) {
        return URLRewriter.prepareURL(url, req);
    }

    /**
     * Encodes the given URL for the client.  Adds ACS global parameters and
     * servlet session parameters to the URL.  If the URL will be used for
     * redirection, use sendRedirect(req, resp, url) instead.
     *
     * @param req the current request
     * @param resp the current response
     * @param url the target URL (for a link) to encode
     * @return the new URL, with extra URL variables added
     * from parameter providers
     * @deprecated use URLRewriter.encodeURL instead
     * @see com.arsdigita.util.URLRewriter
     **/
    public static String encodeURL(HttpServletRequest req,
                                   HttpServletResponse resp,
                                   String url) {
        return URLRewriter.encodeURL(req, resp, url);
    }

    /**
     * Returns a global URL prefix for referencing static assets (images, CSS,
     * etc.) on disk in href attributes. This can be on the same server
     * ("/STATIC/") or a different server/port ("http://server:port/dir/").
     * The return value is guaranteed to end with a trailing slash.
     *
     * Usage example:
     * <pre>
     * String pathToImage = DispatcherHelper.getStaticURL() + "images/pic.gif";
     * Image img = new Image(pathToImage);
     * </pre>
     *
     * @return a URL prefix ending with a trailing slash.
     */
    public static String getStaticURL() {
        init();
        return s_staticURL;
    }

    /**
     * sets the  global URL prefix for referencing static assets (images, CSS,
     * etc.) from user-agents in href attributes.
     * Package visibility is intentional.
     *
     * @param s the static asset URL
     * @pre s != null
     */
    static void setStaticURL(String s) {
        init();
        if (s == null) {
            return;
        }
        if (!s.endsWith("/")) {
            s += "/";
        }
        s_staticURL = s;
    }

    /**
     * sets the webapp Context using the webappContext parameter in enterprise.init.
     * This is an optional parameter.  If it is not specified, null will be used and
     * the value of s_webappContext will be set when there is a request.
     * Package visibility is intentional.
     *
     * @param webappCtx the webappContext specified in enterprise.init. 
     *        Normally this wouldbe "/".
     */
    static void setWebappContext(String webappCtx) {
        init();
        if (webappCtx == null) {
            return;
        }
        if (!webappCtx.startsWith("/")) {
            webappCtx = "/" + webappCtx;
        }
        s_webappCtx = webappCtx;
        s_log.warn("webappContext set to '" + webappCtx + "'");
    }

    /**
     * Gets the webapp Context using the following procedure:
     *
     * 1. If there is a request, get the value from the request.
     * 2. If there is no request, get the value saved from a previous
     *    request.
     * 3. If there is no request or previous request, use the value
     *    specified by the enterprise.init webappContext parameter.
     * 4. Lastly, return null.
     * 
     * @return 
     */
    public static String getWebappContext() {
        init();
        String webappCtx = null;
        HttpServletRequest request = DispatcherHelper.getRequest();
        if (request != null) {
            webappCtx = request.getContextPath();

            // Safety check to make sure the webappCtx from the request
            // matches the webappCtx from enterprise.init.
            if (s_webappCtx != null) {
                if (s_webappCtx.equals("/")) {
                    s_webappCtx = "";
                }
                if (!s_webappCtx.equals(webappCtx)) {
                    s_log.warn(
                            "webappContext changed. Expected='" + s_webappCtx
                            + "' found='" + webappCtx + "'.\nPerhaps the enterprise.init "
                            + "com.arsdigita.dispatcher.Initializer webappContext "
                            + "parameter is wrong.");
                    // Save the webappCtx from the request for future use.
                    s_webappCtx = webappCtx;
                }
            }
        } else {
            if (s_webappCtx != null && s_webappCtx.equals("/")) {
                s_webappCtx = "";
            }
            webappCtx = s_webappCtx;
        }
        return webappCtx;
    }

    /**
     * Aborts all processing of the current request and treat it
     * as successfully completed.  We abort the request by percolating
     * an unchecked Error up through the call stack.  Then the
     * BaseDispatcherServlet.service method traps this and commits
     * whatever DML has already happened on the transaction.
     *
     * @exception com.arsdigita.dispatcher.AbortRequestSignal Error thrown
     * to abort current request
     */
    public static void abortRequest() {
        throw new AbortRequestSignal();
    }

    /**
     * Stores the HttpServletRequest in a ThreadLocal so that it can be
     * accessed globally.
     * 
     * @param r
     */
    public static void setRequest(HttpServletRequest r) {
        init();
        s_request.set(r);
    }

    /**
     * Gets the current HttpServletRequest for this thread.
     * @return the current HttpServletRequest for this thread.
     */
    public static HttpServletRequest getRequest() {
        init();
        return (HttpServletRequest) s_request.get();
    }

    /***************************************************/
    /*        !!! Danger Will Robinson!!!!             */
    /*                                                 */
    /* Don't go making changes to the cache headers    */
    /* without reading *and* fully understanding the   */
    /* sections on caching in RFC 2616 (HTTP 1.1)      */
    /*                                                 */
    /*    -- Daniel Berrange <berrange@redhat.com>     */
    /***************************************************/
    /**
     * If no existing cache policy is set, then call
     * cacheDisable to disable all caching of the response.
     * @param response
     */
    public static void maybeCacheDisable(HttpServletResponse response) {
        if (!response.containsHeader("Cache-Control")) {
            cacheDisable(response);
        }
    }

    /**
     * Aggressively disable all caching of the response.
     * 
     * @param response
     */
    public static void cacheDisable(HttpServletResponse response) {
        init();
        if (!s_cachingActive) {
            return;
        }

        // Assert.isTrue(!response.containsHeader("Cache-Control"),
        //                   "Caching headers have already been set");
        // XXX Probably need to assert here if isCommitted() returns true.
        // But first need to figure out what is setting Cache-Control.
        if (response.containsHeader("Cache-Control")) {
            s_log.warn("Cache-Control has already been set. Overwriting.");
        }

        forceCacheDisable(response);
    }

    /**
     * 
     * @param response 
     */
    public static void forceCacheDisable(HttpServletResponse response) {
        init();
        if (!s_cachingActive) {
            return;
        }

        s_log.info("Setting cache control to disable");
        // Aggressively defeat caching - works even for HTTP 0.9 proxies/clients!
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "must-revalidate, no-cache");
        response.setHeader("Expires", rfc1123_formatter.format(new Date(0)));
    }

    /**
     * If no existing cache policy is set, then
     * call cacheForUser to enable caching for a user.
     * 
     * @param response
     */
    public static void maybeCacheForUser(HttpServletResponse response) {
        if (!response.containsHeader("Cache-Control")) {
            cacheForUser(response);
        }
    }

    /**
     * Allow caching of the response for this user only, as identified
     * by the Cookie header. The response will expire according
     * to the default age setting.
     * 
     * @param response
     */
    public static void cacheForUser(HttpServletResponse response) {
        cacheForUser(response, s_defaultExpiry);
    }

    /**
     * If no existing cache policy is set, then
     * call cacheForUser to enable caching for a user
     *
     * @param response
     * @param maxage the max time in second until this expires
     */
    public static void maybeCacheForUser(HttpServletResponse response,
                                         int maxage) {
        if (!response.containsHeader("Cache-Control")) {
            cacheForUser(response, maxage);
        }
    }

    /**
     * If no existing cache policy is set, then
     * call cacheForUser to enable caching for a user
     *
     * @param response
     * @param expiry the time at which to expire
     */
    public static void maybeCacheForUser(HttpServletResponse response,
                                         Date expiry) {
        if (!response.containsHeader("Cache-Control")) {
            cacheForUser(response, expiry);
        }
    }

    /**
     * Allow caching of the response for this user only,
     * as identified by the Cookie header. The response
     * will expire in 'age' seconds time.
     * @param response
     * @param maxage the max life of the response in seconds
     */
    public static void cacheForUser(HttpServletResponse response,
                                    int maxage) {
        init();
        if (!s_cachingActive) {
            return;
        }

        Assert.isTrue(!response.containsHeader("Cache-Control"),
                      "Caching headers have already been set");

        s_log.info("Setting cache control to user");

        // For HTTP/1.1 user agents, we tell them only cache
        // for the original person  making the request
        response.setHeader("Last-Modified", rfc1123_formatter.format(new Date()));
        response.setHeader("Cache-Control", "private, max-age=" + maxage);

        // NB. THis line is delibrately *NOT* using the actual expiry date
        // supplied. HTTP/1.0 caches don't understand Cache-Control
        // so we use a expiry time in the past to prevent accidental
        // caching. HTTP/1.1 compliant caches still work, since they will
        // look at the above max-age header in preference to Expires...
        response.setHeader("Expires", rfc1123_formatter.format(new Date(0)));
    }

    /**
     * Allowing caching of the response for this user only.
     * The response will expire at time given in the expiry parameter
     * @param response
     * @param expiry time at which to expire
     */
    public static void cacheForUser(HttpServletResponse response,
                                    Date expiry) {
        cacheForUser(response, (int) ((expiry.getTime() - (new Date()).getTime()) / 1000l));
    }

    /**
     * If no existing cache policy is set, then call cacheForUser to enable
     * caching for the world. The response expiry will take the default
     * age setting.
     * 
     * @param response
     */
    public static void maybeCacheForWorld(HttpServletResponse response) {
        if (!response.containsHeader("Cache-Control")) {
            cacheForWorld(response);
        }
    }

    /**
     * Allow caching of this response for anyone in the world.
     * The response take the default expiry time.
     * 
     * @param response
     */
    public static void cacheForWorld(HttpServletResponse response) {
        cacheForWorld(response, s_defaultExpiry);
    }

    /**
     * If no existing cache policy is set, then call cacheForUser to enable
     * caching for the world.
     * 
     * @param response
     * @param maxage the time in seconds until expiry
     */
    public static void maybeCacheForWorld(HttpServletResponse response,
                                          int maxage) {
        if (!response.containsHeader("Cache-Control")) {
            cacheForWorld(response, maxage);
        }
    }

    /**
     * If no existing cache policy is set, then call cacheForUser to
     * enable caching for the world.
     * 
     * @param response
     * @param expiry the time at which it will expire
     */
    public static void maybeCacheForWorld(HttpServletResponse response,
                                          Date expiry) {
        if (!response.containsHeader("Cache-Control")) {
            cacheForWorld(response, expiry);
        }
    }

    /**
     * Allow caching of this response for anyone in the
     * world. The response will expire at the current time
     * plus maxage seconds.
     * @param response
     * @param maxage time in seconds until this expires
     */
    public static void cacheForWorld(HttpServletResponse response,
                                     int maxage) {
        init();
        if (!s_cachingActive) {
            return;
        }

        Assert.isTrue(!response.containsHeader("Cache-Control"),
                      "Caching headers have already been set");

        Calendar expires = Calendar.getInstance();
        expires.add(Calendar.SECOND, maxage);

        s_log.info("Setting cache control to world");
        response.setHeader("Cache-Control", "public, max-age=" + maxage);
        response.setHeader("Expires",
                           rfc1123_formatter.format(expires.getTime()));
        response.setHeader("Last-Modified",
                           rfc1123_formatter.format(new Date()));
    }

    /**
     * Allow caching of this response for anyone in the world.
     * THe response will  expire at the time given.
     * 
     * @param response
     * @param expiry
     */
    public static void cacheForWorld(HttpServletResponse response,
                                     Date expiry) {
        cacheForWorld(response, (int) ((expiry.getTime() - (new Date()).getTime()) / 1000l));
    }

    /**
     * This method returns the best matching locale for the request. In contrast 
     * to the other methods available this one will also respect the 
     * supported_languages config entry.
     *
     * @return The negotiated locale
     */
    public static Locale getNegotiatedLocale() {
        final KernelConfig kernelConfig = KernelConfig.getConfig();

        // Set the preferedLocale to the default locale (first entry in the 
        // config parameter list)
        Locale preferedLocale = new Locale(kernelConfig.getDefaultLanguage(), "", "");

        // The ACCEPTED_LANGUAGES from the client
        Enumeration locales = null;

        // Try to get the RequestContext
        try {
            locales = ((ServletRequest) DispatcherHelper.getRequest()).getLocales();

            // For everey element in the enumerator
            while (locales.hasMoreElements()) {

                // Test if the current locale is listed in the supported locales list
                Locale curLocale = (Locale) locales.nextElement();
                if (kernelConfig.hasLanguage(curLocale.getLanguage())) {
                    preferedLocale = curLocale;
                    break;
                }

            }

        } catch (NullPointerException ex) {
            // Don't have to do anything because I want to fall back to default 
            // language anyway. This case should only appear during setup
        } finally {

            return preferedLocale;

        }
    }

}
