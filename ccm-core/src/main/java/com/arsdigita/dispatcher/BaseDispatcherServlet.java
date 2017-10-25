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

import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.RedirectSignal;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Contains functions common to all entry-point dispatcher servlets in the core.
 *
 * Any dispatcher that is the first in its chain to handle an HTTP request must
 * also be a servlet and should extend this class.
 *
 * <p>
 * You do <em>not</em> need to extend this class unless your dispatcher is also
 * a servlet and is mounted in web.xml. In any given ACS installation, you
 * generally only have one servlet that is mounted through web.xml, and that is
 * usually the <code>com.arsdigita.sitenode.SiteNodeDispatcher</code>, mapped to
 * URL "/".
 *
 * <p>
 * When a request comes in:
 *
 * <ul> <li>first we try to serve a concrete file that matches the URL (for
 * example, if the URL is /dir/image.gif, try to serve $docroot/dir/image.gif)
 *
 * <li>if the URL has no extension we assume it is a virtual directory. if there
 * is no trailing slash, redirect to URL + "/".
 *
 * <li>if the URL has no extension and a trailing slash, it is treated as a
 * directory. If the directory exists as a concrete directory on disk, and has a
 * welcome file (index.*) then serve as a directory by forwarding to the
 * "default" servlet.
 *
 * <li>if there is no concrete match for the URL on disk, we set up a
 * RequestContext object that acts as a request wrapper storing metadata about
 * the request; and call the <code>dispatch</code> method.
 *
 * </ul>
 *
 * @author Bill Schneider
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * $
 */
public abstract class BaseDispatcherServlet extends HttpServlet
    implements Dispatcher, DispatcherConstants {

    private static final Logger LOGGER = LogManager.getLogger(
        BaseDispatcherServlet.class);
    private final static int NOT_FOUND = 0;
    private final static int STATIC_FILE = 1;
    private final static int JSP_FILE = 2;
    private final static String WEB_XML_22_PUBLIC_ID
                                    = "-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN";
    private final static String WEB_XML_23_PUBLIC_ID
                                    = "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN";
    /**
     * We use a Vector here instead of another collection because Vector is
     * synchronised.
     */
    private static Vector s_listenerList = new Vector();
    /**
     * list of active requests
     */
    private static Vector s_activeList = new Vector();
    private static final long serialVersionUID = 7349556332411247334L;

    static {
        LOGGER.debug("Static initalizer starting...");
        // Add the basic request listeners.

        BaseDispatcherServlet.addRequestListener(new RequestListener() {

            public void requestStarted(RequestEvent re) {
                DispatcherHelper.setRequest(re.getRequest());
            }

            public void requestFinished(RequestEvent re) {
                // We could do this:
                // DispatcherHelper.setRequest(null);
                // but some later RequestListener might want to access
                // the request or session.  So we'll just let the
                // DispatcherHelper hang on to one stale
                // HttpServletRequest (per thread).  The reference will
                // be overwritten on the next request, so we keep only
                // a small amount of garbage.
            }

        });

        BaseDispatcherServlet.addRequestListener(new RequestListener() {

            public void requestStarted(RequestEvent re) {
                
            }

            public void requestFinished(RequestEvent re) {
                
            }

        });

      
        LOGGER.debug("Static initalizer finished.");
    }

    private List m_welcomeFiles = new ArrayList();

    /**
     * Reads web.xml to get the configured list of welcome files. We have to
     * read web.xml ourselves because there is no public API to get this
     * information from the ServletContext.
     */
    public synchronized void init() throws ServletException {
        super.init();
        try {
            File file = new File(getServletContext().getRealPath(
                "/WEB-INF/web.xml"));
            // all we care about is the welcome-file-list element
            SAXParserFactory spf = SAXParserFactory
                .newInstance();
            spf.setValidating(false);
            SAXParser parser = spf.newSAXParser();
            parser.parse(file, new WebXMLReader());
        } catch (SAXException se) {
            LOGGER.error("error in init", se);
        } catch (ParserConfigurationException pce) {
            LOGGER.error("error in init", pce);
        } catch (IOException ioe) {
            LOGGER.error("error in init", ioe);
        }
        // default to index.jsp, index.html
        if (m_welcomeFiles.isEmpty()) {
            m_welcomeFiles.add("index.jsp");
            m_welcomeFiles.add("index.html");
        }
        getServletContext().setAttribute(WELCOME_FILES, m_welcomeFiles);
    }

    /**
     * Adds a request listener to <code>this</code>.
     *
     * @param rl the <code>RequestListener</code> to add to the listener list
     */
    public static void addRequestListener(RequestListener rl) {
        s_listenerList.add(rl);
    }

    /**
     * A placeholder method for performing user authentication during request
     * processing. Subclasses should override this method.
     *
     * @param req the current servlet request object
     * @param req the current servlet response object
     * @param req the current request context
     *
     * @return the updated request context (which may be the same as the context
     *         context parameter).
     *
     * @throws com.arsdigita.dispatcher.RedirectException if the dispatcher
     *                                                    should redirect the
     *                                                    client to the page
     *                                                    contained in the
     *                                                    exception
     *
     */
    protected abstract RequestContext authenticateUser(HttpServletRequest req,
                                                       HttpServletResponse resp,
                                                       RequestContext ctx)
        throws RedirectException;

    /**
     * Called directly by the servlet container when this servlet is invoked
     * from a URL request. First tries to dispatch the URL to a concrete file on
     * disk, if there is a matching file. Otherwise, sets up an initial
     * RequestContext, tries to identify the user/session, parses form
     * variables, and wraps the request object to handle multipart forms if
     * necessary. Calls the <code>dispatch</code> method as declared in
     * implementing subclasses.
     *
     * @param req  the servlet request
     * @param resp the servlet response
     *
     * @throws javax.servlet.ServletException re-thrown when
     *                                        <code>dispatch</code> throws an
     *                                        exception
     * @throws java.io.IOException            re-thrown when
     *                                        <code>dispatch</code> throws an
     *                                        IOException
     */
    public void service(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\n*** *** *** *** *** ***\n"
                            + "Servicing request for URL '" + req
                .getRequestURI()
                            + "'\n" + "*** *** *** *** *** ***");
        }

        boolean reentrant = true;
        RequestContext reqCtx = DispatcherHelper.getRequestContext(req);
        boolean finishedNormal = false;

        // there are two types of re-entrancy we need to consider:
        // * forwarded requests specified by the application,
        //  where the forwarded request is picked up by a subclass
        // of BDS.  (e.g., SND forwards /foo/bar to
        // /packages/foo/www/bar.jsp)
        // * a secondary request, forwarded by the CONTAINER in response
        // to an exception thrown from service(), after the first request
        //  completes
        //
        // in the FIRST case, we need to guard against running
        // the start/end listeners again.  in the SECOND case,
        // we need to treat this like a new request so that
        // we open a transaction, etc. for serving the error page.
        // wrap entire rest of method in try-catch block.  that way if
        // some method call throws ServletException or IOException,
        // implicitly exiting the service method, we'll still be able
        // to free up the database connection in a finally.
        // STEP #1: if no extension, treat as directory;
        // make sure we have a trailing slash. and redirect
        //otherwise.
        DispatcherHelper.setRequest(req);

        if (trailingSlashRedirect(req, resp)) {
            // note, this is OUTSIDE of try/catch/finally.  No
            // listeners of any kind are run!
            return;
        }

        // STEP #2: try to serve concrete static file, if one exists.
        // (defer serving concrete JSPs until after listeners run)
        int concreteFileType = concreteFileType(req);
        if (concreteFileType == STATIC_FILE) {
            LOGGER.debug("Setting world cache headers on static file");
            DispatcherHelper.cacheForWorld(resp);
            DispatcherHelper.forwardRequestByName("default", req, resp,
                                                  getServletContext());
            return;
        }

        try {
            if (req.getAttribute(REENTRANCE_ATTRIBUTE) == null) {
                reentrant = false;

                waitForPreviousRequestToFinish(req);

                // need an identifier for this particular request
                String requestId = Thread.currentThread().getName() + "|"
                                       + System.
                    currentTimeMillis();
                req.setAttribute(REENTRANCE_ATTRIBUTE, requestId);
                s_activeList.add(requestId);

                try {
                    // first time through:
                    // do all actions that must be done initially on hit
                    StartRequestRecord srr = startRequest(req, resp);
                    reqCtx = srr.m_reqCtx;
                    req = srr.m_req;
                    LOGGER.debug("After startRequest the request is now " + req);
                } catch (RedirectException re) {
                    final String url = re.getRedirectURL();

                    resp.sendRedirect(resp.encodeRedirectURL(url));

                    return;
                }
            } else {
                req = DispatcherHelper.maybeWrapRequest(req);

                // if we're handling a secondary request for an
                // error, but we haven't run the finally ... block
                // on the primary request yet (this happens when
                // sendError is called explicitly, as opposed to when
                // the container calls sendError(500...) in response
                // to an exception rethrown here) we DON'T run the
                // request listeners.  BUT we need to clear
                // the request context.
                if (req.getAttribute(ERROR_REQUEST_ATTRIBUTE) != null
                        || req.getAttribute(JSP_EXCEPTION_ATTRIBUTE) != null) {
                    // reset URL boookeeping but don't wipe out
                    // whole object since it might actually be a
                    // KernelRequestContext with user / session info
                    if (reqCtx instanceof InitialRequestContext) {
                        ((InitialRequestContext) reqCtx).
                            initializeURLFromRequest(req, true);
                    }
                }
            }
            // finally, call dispatch
            finishedNormal = false;

            if (concreteFileType == JSP_FILE) {
                // STEP #3: dispatch to a concrete JSP if we have a matching
                // one
                DispatcherHelper.forwardRequestByName("jsp", req, resp);
            } else {
                // STEP #4: if no concrete file exists, dispatch to
                // implementing class
                dispatch(req, resp, reqCtx);
            }

            // if JSP already dispatched to error page, no exception
            // will be thrown.  have to check for attribute manually.
            if (req.getAttribute(JSP_EXCEPTION_ATTRIBUTE) == null) {
                finishedNormal = true;
            }
        } catch (AbortRequestSignal ars) {
            // treat this as a normal end of request and
            // try to commit
            finishedNormal = true;
        } catch (IOException ioe) {
            LOGGER.error("error in BaseDispatcherServlet", ioe);
            throw ioe;
        } catch (ServletException se) {
            // SDM #140226, improved handling of
            // ServletException.getRootCause()
            Throwable t = se;
            Throwable rootError;
            do {
                rootError = t;
                t = ((ServletException) t).getRootCause();
            } while (t instanceof ServletException);
            if (t != null) {
                rootError = t;
            }
            // handle this in case AbortRequestSignal got wrapped
            // accidentally--e.g., inside a JSP.
            if (rootError != null
                    && (rootError instanceof AbortRequestSignal)) {
                finishedNormal = true;
            } else if (rootError != null
                           && (rootError instanceof RedirectSignal)) {
                LOGGER.debug("rethrowing RedirectSignal", rootError);
                throw (RedirectSignal) rootError;
            } else {
                LOGGER.error("error in BaseDispatcherServlet", rootError);
                throw new ServletException(rootError);
            }
        } catch (RuntimeException re) {
            LOGGER.error("error in BaseDispatcherServlet", re);
            throw re;
        } catch (Error error) {
            LOGGER.error("error in BaseDispatcherServlet", error);
            throw error;
        } finally {
            if (!reentrant) {
                // run the request listener events
                fireFinishedListener(
                    new RequestEvent(req, resp, reqCtx, false,
                                     finishedNormal));
                // at this point, clear the attribute so
                // a secondary request will work
                // and remove the request from the list of currently-active
                // requests
                Object requestId = req.getAttribute(REENTRANCE_ATTRIBUTE);
                synchronized (s_activeList) {
                    s_activeList.remove(requestId);
                    s_activeList.notifyAll();
                }
                req.removeAttribute(REENTRANCE_ATTRIBUTE);
            }
        }
    }

    /**
     * Processes a request when it is first handled by the servlet. This method
     * runs exactly once for each request, even if the request is reentrant.
     *
     * @return a tuple containing the updated request context and the request
     *
     * @throws com.arsdigita.dispatcher.RedirectException if the dispatcher
     *                                                    should redirect the
     *                                                    client to the page
     *                                                    contained in the
     *                                                    exception
     *
     */
    private StartRequestRecord startRequest(HttpServletRequest req,
                                            HttpServletResponse resp)
        throws RedirectException, IOException, ServletException {

        // turn multipart request into wrapped request
        // to make up for servlet 2.2 brokenness
        req = DispatcherHelper.maybeWrapRequest(req);

        RequestContext reqCtx = new InitialRequestContext(req,
                                                          getServletContext());

        // run the request listener events
        fireStartListener(new RequestEvent(req, resp, reqCtx, true));

        // Authenticate user AFTER request listeners because authentication
        // may need to use the database connection (opened by a listener).
        // Allow subclass to update request context with user info.
        reqCtx = authenticateUser(req, resp, reqCtx);

        // save the request context in the request
        DispatcherHelper.setRequestContext(req, reqCtx);

        return new StartRequestRecord(reqCtx, req);
    }

    /**
     * Fires all finished listeners. Collects and logs errors to ensure that all
     * finished listeners run.
     *
     * @param evt the current RequestEvent to broadcast to all event listeners
     */
    protected void fireFinishedListener(RequestEvent evt) {
        for (int i = 0; i < s_listenerList.size(); i++) {
            try {
                ((RequestListener) s_listenerList.get(i)).requestFinished(evt);
            } catch (Exception e) {
                LOGGER.error("Error running request finished listener "
                                + s_listenerList.
                    get(i) + " (#" + i + ")", e);
            }
        }
    }

    /**
     * Fires all start listeners. Does <b>not</b> collect and log errors.
     * Instead, a runtime failure in a start listener will inhibit further
     * servicing of the request.
     *
     * @param evt the current RequestEvent to broadcast to all event listeners
     */
    protected void fireStartListener(RequestEvent evt) {
        for (int i = 0; i < s_listenerList.size(); i++) {
            ((RequestListener) s_listenerList.get(i)).requestStarted(evt);
        }
    }

    /**
     * Kludge for returning a typed 2-tuple.
     */
    private class StartRequestRecord {

        RequestContext m_reqCtx;
        HttpServletRequest m_req;

        public StartRequestRecord(RequestContext rc, HttpServletRequest req) {
            m_reqCtx = rc;
            m_req = req;
        }

    }

    private void waitForPreviousRequestToFinish(HttpServletRequest req) {
        // handle concurrence -- serialize requests from the same
        // user agent, so that you can't follow a link/redirect from
        // a request until the request's transaction has committed

        // get identifier from previous request, if there is any
        HttpSession sess = req.getSession(false);
        if (sess != null) {
            Object sema = sess.getAttribute(REDIRECT_SEMAPHORE);
            if (sema != null) {
                while (s_activeList.indexOf(sema) != -1) {
                    try {
                        synchronized (s_activeList) {
                            s_activeList.wait();
                        }
                    } catch (InterruptedException ie) {
                    }
                }
                sess.removeAttribute(REDIRECT_SEMAPHORE);
            }
        }
    }

    /**
     * helper method: if the current request URL points to a concrete file under
     * the webapp root, returns STATIC_FILE or JSP_FILE indicating the type of
     * file. returns NOT_FOUND if no corresponding concrete file exists.
     *
     * <p>
     * If the concrete file is a directory, then we require that the directory
     * have a welcome file like index.*; this prevents us from serving directory
     * listings. For directories we return STATIC_FILE if there is a welcome
     * file, otherwise return NOT_FOUND.
     *
     * @return STATIC_FILE if the current request points to a concrete static
     *         file (non-JSP) or a directory that has a welcome file. returns
     *         JSP_FILE if it corresponds to a dynamic JSP file. returns
     *         NOT_FOUND otherwise.
     */
    private int concreteFileType(HttpServletRequest req)
        throws ServletException, IOException {

        String path = DispatcherHelper.getCurrentResourcePath(req);

        ServletContext sctx = this.getServletContext();
        File realFile = new File(sctx.getRealPath(path));
        if (realFile.exists() && (!realFile.isDirectory() || hasWelcomeFile(
                                  realFile))) {
            // yup.  Go there, bypass the site map.
            // we have a concrete file so no forwarding to
            // rewrite the request URL is necessary.
            if (realFile.getName().endsWith(".jsp")) {
                return JSP_FILE;
            } else {
                return STATIC_FILE;
            }
        } else {
            return NOT_FOUND;
        }
    }

    /**
     * returns true if dir is a directory and has a welcome file like index.*.
     *
     * @pre dir.isDirectory()
     */
    private boolean hasWelcomeFile(File dir) {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("dir must be a directory");
        }
        String[] files = dir.list();
        for (int i = 0; i < files.length; i++) {
            if (m_welcomeFiles.indexOf(files[i]) >= 0) {
                return true;
            }
        }
        return false;
    }

    private boolean trailingSlashRedirect(HttpServletRequest req,
                                          HttpServletResponse resp)
        throws IOException {
        String path = DispatcherHelper.getCurrentResourcePath(req);
        // first, see if we have an extension
        if (path.lastIndexOf(".") <= path.lastIndexOf("/")) {
            // maybe no extension. check if there's a trailing
            // slash already.
            if (!path.endsWith("/")) {
                // no trailing slash
                String targetURL = req.getContextPath() + path + "/";
                String query = req.getQueryString();
                if (query != null && query.length() > 0) {
                    targetURL += "?" + query;
                }
                resp.sendRedirect(resp.encodeRedirectURL(targetURL));
                return true;
            }
        }
        return false;
    }

    /**
     * SAX content handler class to pick welcome-file-list out of web.xml
     */
    private class WebXMLReader extends DefaultHandler {

        StringBuffer m_buffer = new StringBuffer();

        @Override
        public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException {
            // we don't want to read the web.xml dtd
            if (WEB_XML_22_PUBLIC_ID.equals(publicId)
                    || WEB_XML_23_PUBLIC_ID.equals(publicId)) {
                StringReader reader = new StringReader(" ");
                return new InputSource(reader);
            } else {
                try {
                    return super.resolveEntity(publicId, systemId);
                } catch (Exception e) {
                    if (e instanceof SAXException) {
                        throw (SAXException) e;
                    } else {
                        throw new UncheckedWrapperException("Resolve Error", e);
                    }
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int len) {
            for (int i = 0; i < len; i++) {
                m_buffer.append(ch[start + i]);
            }
        }

        @Override
        public void endElement(String uri,
                               String localName,
                               String qname) {
            if (qname.equals("welcome-file-list")) {
                String[] welcomeFiles = StringUtils.split(m_buffer.toString(),
                                                          ',');
                for (int i = 0; i < welcomeFiles.length; i++) {
                    m_welcomeFiles.add(welcomeFiles[i].trim());
                }
            }
            m_buffer = new StringBuffer();
        }

    }

}
