/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.web;

import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.util.Assert;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.web.CcmApplication;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.ServletPath;

import java.io.IOException;
import java.math.BigDecimal;

import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@WebServlet(urlPatterns = {"/ccm/*"},
            loadOnStartup = 1)
public class CCMDispatcherServlet extends BaseServlet {

    private static final long serialVersionUID = 5292817856022435529L;

    private static final Logger LOGGER = LogManager.getFormatterLogger(
        CCMDispatcherServlet.class);

    private static final String DISPATCHED_ATTRIBUTE
                                    = CCMDispatcherServlet.class
        .getName() + ".dispatched";

    /**
     * String containing the web context path portion of the WEB application
     * where this CCMDispatcherServlet is executed. (I.e. where the WEB-INF
     * directory containing the web.xml configuring this CCMDispatcherServlet is
     * located in the servlet container webapps directory.
     *
     */
    private static String s_contextPath;

    @Inject
    private transient ApplicationRepository appRepository;

    /**
     * Servlet initializer uses the extension point of parent class.
     *
     * @throws ServletException
     */
    @Override
    public void doInit() throws ServletException {

        ServletContext servletContext = getServletContext();
        s_contextPath = servletContext.getContextPath();
        // For backwords compatibility reasons register the web application
        // context of the Core (root) application als "/"
        // Web.registerServletContext("/",
        //                            servletContext);

    }

    @Override
    protected void doService(final HttpServletRequest request,
                             final HttpServletResponse response)
        throws ServletException, IOException {

        LOGGER.debug("Dispatching request %s [ %s, %s, %s, %s ]",
                     request.getRequestURI(),
                     request.getContextPath(),
                     request.getPathInfo(),
                     request.getQueryString());

        final String path = request.getPathInfo();

        if (requiresTrailingSlash(path)) {
            LOGGER.debug("The request URI needs a trailing slash. Redirecting");

            final String prefix = DispatcherHelper.getDispatcherPrefix(request);
            String uri = request.getRequestURI();
            if (prefix != null && prefix.trim().length() > 0) {
                uri = prefix + uri;
            }
            final String query = request.getQueryString();

            if (query == null) {
                response.sendRedirect(response.encodeRedirectURL(uri + "/"));
            } else {
                response.sendRedirect(response
                    .encodeRedirectURL(uri + "/?" + query));
            }
        } else {
            LOGGER.debug("Storing the path elements of the current request as "
                             + "the original path elements");

            request.setAttribute(BaseServlet.REQUEST_URL_ATTRIBUTE,
                                 new URL(request));

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Using path '" + path + "' to lookup application");
            }

            final ApplicationSpec spec = lookupApplicationSpec(path);
            
            if (spec == null) {
                LOGGER.debug("No application was found; doing nothing");
                // return false;
                // we have to create a 404 page here!
                String requestUri = request.getRequestURI(); // same as ctx.getRemainingURLPart()
                response.sendError(404, requestUri + " not found on this server.");
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Found application " + spec.getAppID() + "; " +
                                "dispatching to its servlet");
                }

                request.setAttribute
                    (BaseApplicationServlet.APPLICATION_ID_ATTRIBUTE,
                     spec.getAppID());
                request.setAttribute(DISPATCHED_ATTRIBUTE, Boolean.TRUE);
                forward(spec.getTypeContextPath(), spec.target(path), request, response);
                // return true;
            }

        }

    }

    private boolean requiresTrailingSlash(final String path) {
        LOGGER.debug("Checking if the required needs a trailing slash...");

        if (path == null) {
            LOGGER.debug("The path is null; the request needs a trailing slash");
            return true;
        }

        if (path.endsWith("/")) {
            LOGGER.debug("The path already ends in '/'");
            return false;
        }

        if (path.lastIndexOf(".") < path.lastIndexOf("/")) {
            LOGGER.debug("The last fragment of the path has no '.', so we "
                             + "assume a directory was requested; a trailing "
                             + "slash is required");
            return true;
        } else {
            LOGGER.debug("The last fragment of the path appears to be a file "
                             + "name; no trailing slash is needed");
            return false;
        }
    }

    private void forward(final String contextPath,
                         final String target,
                         final HttpServletRequest request,
                         final HttpServletResponse response)
        throws ServletException, IOException {

        LOGGER.debug("Forwarding by path to target \"%s\"...", target);
        LOGGER.debug("The context path is: %s", contextPath);
        final String forwardContextPath;
        if (contextPath == null || contextPath.isEmpty()) {
            //Not compliant with Servlet specification. 
            //Empty context has be be "/"
            forwardContextPath = "/";
        } else if (!contextPath.endsWith("/")) {
            //No trailing slash, add one
            forwardContextPath = String.format("%s/", contextPath);
        } else {
            forwardContextPath = contextPath;
        }

        final ServletContext context = getServletContext().getContext(
            forwardContextPath);

        LOGGER.debug("forwarding from context \"%s\" to context \"%s\"...",
                     getServletContext(), context);

        forward(context.getRequestDispatcher(target),
                request,
                response);
    }

    private void forward(final RequestDispatcher dispatcher,
                         final HttpServletRequest request,
                         final HttpServletResponse response)
        throws ServletException, IOException {
        LOGGER.debug("Checking if this request need to be forwarded or "
                         + "included: %s", request);

        if (request.getAttribute("javax.servlet.include.request_uri") == null) {
            LOGGER.debug("The attribute javax.servlet.include.request_uri "
                             + "is not set; forwarding %s",
                         request);

            dispatcher.forward(request, response);
        } else {
            LOGGER.debug("The attribute javax.servlet.include.request_uri "
                             + "is set; including %s",
                         request);
            dispatcher.include(request, response);
        }
    }

        /**
     * 
     * @param path
     * @return 
     */
    private ApplicationSpec lookupApplicationSpec(final String path) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("*** Starting application lookup for path '" +
                        path + "' ***");
        }

        final CcmApplication application = appRepository
            .retrieveApplicationForPath(path);
        
        if (application == null) {
            LOGGER.warn("No application found for path \"%s\".");
            return null;
        } else {
            return new ApplicationSpec(application);
        }
    }
    
    public static String getContextPath() {
        return s_contextPath;
    }

    /**
     * 
     */
    /*  Nothing specifically to destroy here
    @Override
    protected void doDestroy() {
    }
    */


    
    /**
     * Private class.
     */
    private static class ApplicationSpec {
        private final long m_id;
        private final String m_instanceURI;
        private final String m_typeURI;
        private final String m_typeContextPath;

        /**
         * 
         * @param app 
         */
        ApplicationSpec(CcmApplication app) {
            if ( app == null ) { throw new NullPointerException("app"); }

            m_id              = app.getObjectId();
            m_instanceURI     = app.getPrimaryUrl().toString();
            if (app.getClass().isAnnotationPresent(ServletPath.class)) {
                m_typeURI = app.getClass().getAnnotation(ServletPath.class).value();
            } else {
                m_typeURI = URL.SERVLET_DIR + "/legacy-adapter";
            }
            m_typeContextPath = "";

            if (Assert.isEnabled()) {
                Assert.exists(m_id, BigDecimal.class);
                Assert.exists(m_instanceURI, String.class);
                Assert.exists(m_typeURI, String.class);
                Assert.exists(m_typeContextPath, String.class);
            }
        }

        /**
         * 
         * @return 
         */
        long getAppID() { return m_id; }
        
        /**
         * Provides the context the application is executing. Usually all CCM
         * applications will now execute in the samme webapp context. The 
         * app.getContextPath() return "" in this case where an application is
         * executing in no specific context but CCM's default.
         * @return The context path of the application's url, "" in case of
         *         executing in the ROOT context.
         */
        String getTypeContextPath() { 
            if (m_typeContextPath.equals("") ) {
                // app is running in CCM's default context, determine the
                // actual one
                return Web.getWebappContextPath();
            } else {
                return m_typeContextPath; 
            }
        }

        /**
         * 
         * @param path
         * @return 
         */
        String target(final String path) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Building the target path from the request path '" +
                            path + "' and the spec " + this);
            }

            final StringBuffer target = new StringBuffer(128);

            target.append(m_typeURI);
            target.append(path.substring(m_instanceURI.length()));
            target.append("?");
            target.append(BaseApplicationServlet.APPLICATION_ID_PARAMETER);
            target.append("=");
            target.append(m_id);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Returning target value '" + target + "'");
            }

            return target.toString();
        }

        /**
         * 
         * @param obj
         * @return 
         */
        @Override
        public boolean equals(Object obj) {
            if ( obj==null ) { return false; }

            ApplicationSpec other = (ApplicationSpec) obj;
            return m_id == other.getAppID() &&
                equal(m_instanceURI, other.m_instanceURI) &&
                equal(m_typeURI, other.m_typeURI) &&
                equal(m_typeContextPath, other.m_typeContextPath);

        }

        /**
         * 
         * @param s1
         * @param s2
         * @return 
         */
        private boolean equal(String s1, String s2) {
            if (s1==s2) { return true; }
            if (s1==null) { return equal(s2, s1); }
            return s1.equals(s2);
        }

        /**
         * 
         * @return 
         */
        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        /**
         * 
         * @return 
         */
        @Override
        public String toString() {
            final String sep = ", ";
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            sb.append("appID=").append(m_id).append(sep);
            sb.append("instanceURI=").append(m_instanceURI).append(sep);
            sb.append("typeURI=").append(m_typeURI).append(sep);
            sb.append("typeContextPath=").append(m_typeContextPath);
            return sb.append("]").toString();
        }
    }

}
