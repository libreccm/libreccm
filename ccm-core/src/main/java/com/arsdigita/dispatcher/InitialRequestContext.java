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

import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * Implements a request context for the site map application
 * or for any application dispatcher that creates the first application context
 * for an incoming request.
 *
 * @author Bill Schneider 
 * @version $Id$
 * @since 4.5 
 */
public class InitialRequestContext implements RequestContext {

    private static final Logger s_log = Logger.getLogger
        (InitialRequestContext.class);

    private String m_urlSoFar;
    private String m_urlRemainder;
    private String m_originalUrl;
    private ServletContext m_sctx;
    private String m_outputType;
    private Locale m_locale;
    private boolean m_debugging;  //These three vars are for /debug, /xml, /xsl
    private boolean m_debuggingXML;
    private boolean m_debuggingXSL;

    /**
     * Copy constructor.  Creates a new
     * <code>InitialRequestContext</code> with identical properties
     * as the parameter <code>that</code>.  This is useful for deferred
     * construction of subclass objects with the same properties.
     *
     * @param that a request context to copy basic properties from.
     * @post this.getProcessedURLPart() == that.getProcessedURLPart()
     * @post this.getRemainingURLPart() == that.getRemainingURLPart()
     * @post this.getOriginalURL() == that.getOriginalURL()
     * @post this.getServletContext() == that.getServletContext()
     * @post this.getLocale() == that.getLocale()
     **/
    protected InitialRequestContext(RequestContext that) {
        this.m_urlSoFar     = that.getProcessedURLPart();
        this.m_urlRemainder = that.getRemainingURLPart();
        this.m_originalUrl  = that.getOriginalURL();
        this.m_sctx         = that.getServletContext();
        this.m_outputType   = that.getOutputType();
        this.m_locale       = that.getLocale();
        this.m_debugging    = that.getDebugging();
        this.m_debuggingXML    = that.getDebuggingXML();
        this.m_debuggingXSL    = that.getDebuggingXSL();
    }

    /**
     * Constructs a new request context from the given servlet
     * request.  Some initial URL portion has already been handled by
     * the servlet container in dispatching to our web application.
     * @param request the servlet request
     * @param sctx the servlet context
     */
    public InitialRequestContext(HttpServletRequest request,
                                 ServletContext sctx) {
        m_sctx = sctx;
        initializeURLFromRequest(request, false);

        Object obj = request.getParameter("outputType");
        if (obj != null) {
            m_outputType = (String)obj;
        } else {
            m_outputType = "text/html";
        }

        m_locale = request.getLocale();
    }

    /**
     * Initializes the URL in this request context and decomposes
     * it into a part already processed (what part of the URL got
     * us here already?) and the part not yet processed (what
     * part will the next dispatcher in the chain use?).
     * In the initial step, the only part of the URL used so far
     * is the part that selects the servlet context (webapp).
     */
    void initializeURLFromRequest(HttpServletRequest request,
                                  boolean preserveOriginalURL) {
        s_log.debug("Initializing processed and remaining URL parts.");
        
        String requestUrl = DispatcherHelper.getCurrentResourcePath(request);
        m_urlSoFar = request.getContextPath();
        m_urlRemainder = requestUrl;

        if (s_log.isDebugEnabled()) {
            String contextPath = request.getContextPath();
            s_log.debug("contextPath: " + contextPath);
        }

        if (s_log.isDebugEnabled()) {
            String servletPath = request.getServletPath();
            s_log.debug("servletPath: " + servletPath);
        }

        if (s_log.isDebugEnabled()) {
            String pathInfo = request.getPathInfo();
            s_log.debug("pathInfo: " + pathInfo);
        }

        final String debugURL = "/debug";
        m_debugging = m_urlRemainder.startsWith(debugURL); // humor JTest
        if (m_debugging) {
            m_urlSoFar += debugURL;
            m_urlRemainder = m_urlRemainder.substring(debugURL.length());
        }

        final String debugURLXML = "/xml";
        m_debuggingXML = m_urlRemainder.startsWith(debugURLXML); // humor JTest
        if (m_debuggingXML) {
            m_urlSoFar += debugURLXML;
            m_urlRemainder = m_urlRemainder.substring(debugURLXML.length());
        }

        final String debugURLXSL = "/xsl";
        m_debuggingXSL = m_urlRemainder.startsWith(debugURLXSL); // humor JTest
        if (m_debuggingXSL) {
            m_urlSoFar += debugURLXSL;
            m_urlRemainder = m_urlRemainder.substring(debugURLXSL.length());
        }
        if (!preserveOriginalURL) {
            s_log.debug("Overwriting original URL, since the caller did not " +
                        "ask to preserve it");
            m_originalUrl = m_urlSoFar + m_urlRemainder;
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Set processed URL to '" + m_urlSoFar + "'");
            s_log.debug("Set remaining URL to '" + m_urlRemainder + "'");
        }
    }

    /**
     * Returns the portion of the requested URL that was used so by far
     * by all previous dispatchers in the chain.
     *
     * @return the portion of the requested URL that was used so by far
     * by all previous dispatchers in the chain.
     */
    public String getProcessedURLPart() {
        return m_urlSoFar;
    }

    /**
     * Returns the portion of the requested URL that has not already been
     * used by all previous dispatchers in the chain.
     *
     * @return the portion of the requested URL that has not already
     * been used by all previous dispatchers in the chain.
     */
    public String getRemainingURLPart() {
        return m_urlRemainder;
    }

    /**
     * Sets the portion of the requested URL that has not already
     * been processed by any previous dispatcher in the chain
     * @param s the remaining unprocessed URL portion
     */
    protected void setRemainingURLPart(String s) {
        m_urlRemainder = s;
    }

    /**
     * Sets the portion of the requested URL that has already
     * been processed by all the previous dispatchers in the chain.
     * This allows decorating subclasses like SiteNodeRequestContext
     * to mark an additional portion of the URL as processed.
     *
     * @param s the remaining unprocessed URL portion
     */
    protected void setProcessedURLPart(String s) {
        m_urlSoFar = s;
    }

    public String getOriginalURL() {
        return m_originalUrl;
    }

    public ServletContext getServletContext() {
        return m_sctx;
    }

    /**
     * At this point, we're not in any specific package, so just returns
     * '/'.
     */
    public String getPageBase() {
        return "/";
    }

    /**
     * @return the locale preferred by the user, as specified in the
     * Accept-Language header.
     */
    public Locale getLocale() {
        return m_locale;
    }

    public String getOutputType() {
        return m_outputType;
    }

    /**
     * XXX Only added so that the class compiles.
     */
    public ResourceBundle getResourceBundle() {
        return null;
    }

    public boolean getDebugging() {
        return m_debugging;
    }

    public boolean getDebuggingXML() {
        return m_debuggingXML;
    }

    public boolean getDebuggingXSL() {
        return m_debuggingXSL;
    }
}
