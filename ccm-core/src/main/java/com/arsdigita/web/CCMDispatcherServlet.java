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
import com.arsdigita.ui.UI;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.subject.Subject;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.web.ApplicationManager;
import org.libreccm.web.ApplicationType;

import java.util.Iterator;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.servlet.http.HttpServlet;

/**
 * <p>
 * The CCM main dispatcher. This servlet serves as the main servlet / main entry
 * point (mapped to "/someprefix/*") for requests to any CCM webapp.</p>
 *
 * <p>
 * Upon finding an {@link com.arsdigita.web.Application application} at the
 * requested URL, this class sets a request attribute storing the ID of the
 * application and forwards to the servlet associated with that application. If
 * instead no application is found, a 404 response is generated.</p>
 *
 * For LibreCCM there a few changes to this Servlet compared to earlier versions
 * of CCM:
 *
 * <ul>
 * <li>
 * No entries in the <code>web.xml</code> required anymore. We are now using the
 * annotations from the Servlet API 3.
 * </li>
 * <li>
 * The servlet in now mapped to <code>/ccm/*</code> and to
 * <code>/index.html</code>. The mapping to <code>/index.html</code> replaces
 * the <code>index.jsp</code>. The logic which was implemented in the
 * <code>index.jsp</code> is now part of the
 * {@link #doService(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
 * method of this Servlet.
 * </li>
 * </ul>
 *
 *
 * @author Justin Ross
 * &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @author Peter Boy &lt;<a href="mailto:pboy@barkhof.uni-bremen.de">Peter
 * Boy</a>&gt;
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@WebServlet(urlPatterns = {"/ccm/*", "/index.html"},
            loadOnStartup = 1)
public class CCMDispatcherServlet extends BaseServlet {

    private static final long serialVersionUID = 5292817856022435529L;

    private static final Logger LOGGER = LogManager.getLogger(
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
    private ApplicationRepository appRepository;

    @Inject
    private ApplicationManager appManager;

    @Inject
    private Subject subject;

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

        //This part replaces the index.jsp file
        if (request.getPathInfo() == null
                || request.getPathInfo().isEmpty()
                || "/".equals(request.getPathInfo())) {

            if (subject.isAuthenticated()) {
                // User is logged in, redirect to user redirect page
                throw new RedirectSignal(
                    URL.there(request,
                              UI.getUserRedirectURL(request)),
                    false);
            } else {
                // User is *not* logged in, display public front page
                throw new RedirectSignal(URL.there(request,
                                                   UI.getWorkspaceURL(request)),
                                         true);
            }
        }
        // index.jsp replacement end

        LOGGER.debug("Dispatching request {} [ {}, {}, {}, {} ]",
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
                LOGGER.debug("Using path '{}' to lookup application", path);
            }

            final ApplicationSpec spec = lookupApplicationSpec(path);

            if (spec == null) {
                LOGGER.debug("No application was found; doing nothing");
                // return false;
                // we have to create a 404 page here!
                String requestUri = request.getRequestURI(); // same as ctx.getRemainingURLPart()
                response.sendError(404, requestUri
                                            + " not found on this server.");
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Found application {}; "
                                     + "dispatching to its servlet",
                                 spec.getAppID());
                }

                request.setAttribute(
                    BaseApplicationServlet.APPLICATION_ID_ATTRIBUTE,
                    spec.getAppID());
                request.setAttribute(DISPATCHED_ATTRIBUTE, Boolean.TRUE);
                forward(spec.getTypeContextPath(),
                        spec.target(path),
                        request,
                        response);
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

        LOGGER.debug("Forwarding by path to target \"{}\"...", target);
        LOGGER.debug("The context path is: {}", contextPath);
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

        LOGGER.debug("forwarding from context \"{}\" to context \"{}\"...",
                     getServletContext(), context);

        forward(getServletContext().getRequestDispatcher(target),
                request,
                response);
    }

    private void forward(final RequestDispatcher dispatcher,
                         final HttpServletRequest request,
                         final HttpServletResponse response)
        throws ServletException, IOException {
        LOGGER.debug("Checking if this request need to be forwarded or "
                         + "included: {}", request);

        if (request.getAttribute("javax.servlet.include.request_uri") == null) {
            LOGGER.debug("The attribute javax.servlet.include.request_uri "
                             + "is not set; forwarding {}",
                         request);

            dispatcher.forward(request, response);
        } else {
            LOGGER.debug("The attribute javax.servlet.include.request_uri "
                             + "is set; including {}",
                         request);
            dispatcher.include(request, response);
        }
    }

    /**
     *
     * @param path
     *
     * @return
     */
    private ApplicationSpec lookupApplicationSpec(final String path) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("*** Starting application lookup for path '{}' ***",
                         path);
        }

        final CcmApplication application = appManager
            .findApplicationByPath(path);

        if (application == null) {
            LOGGER.warn("No application found for path \"{}\".", path);
            return null;
        } else {
            return new ApplicationSpec(application, appManager);
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
        private final ApplicationManager appManager;

        /**
         *
         * @param app
         */
        ApplicationSpec(final CcmApplication app, 
                        final ApplicationManager appManager) {
            this.appManager = appManager;
            
            if (app == null) {
                throw new NullPointerException("app");
            }

            m_id = app.getObjectId();
            m_instanceURI = app.getPrimaryUrl();
            if (app.getClass().isAnnotationPresent(ServletPath.class)) {
                m_typeURI = app
                    .getClass()
                    .getAnnotation(ServletPath.class)
                    .value();
            } else {
//                final ApplicationManager appManager = CDI.current().select(
//                    ApplicationManager.class).get();
//                final BeanManager beanManager = CDI.current().getBeanManager();
//                final Set<Bean<?>> beans = beanManager.getBeans(
//                    ApplicationManager.class);
//                final Iterator<Bean<?>> iterator = beans.iterator();
//                final ApplicationManager appManager;
//                if (iterator.hasNext()) {
//                    @SuppressWarnings("unchecked")
//                    final Bean<ApplicationManager> bean
//                                                   = (Bean<ApplicationManager>) iterator
//                        .next();
//                    final CreationalContext<ApplicationManager> ctx
//                                                                = beanManager
//                        .createCreationalContext(bean);
//
//                    appManager = (ApplicationManager) beanManager.getReference(
//                        bean, ApplicationManager.class, ctx);
//                } else {
//                    LOGGER.error("Failed to find {}.",
//                                 ApplicationManager.class.getName());
//                    throw new IllegalArgumentException(String.format(
//                        "Failed to find %s",
//                        ApplicationManager.class.getName()));
//                }

                final ApplicationType appType = appManager.getApplicationTypes()
                    .get(app.getApplicationType());
                final Class<? extends HttpServlet> appServletClass = appType
                    .servlet();
                final WebServlet servletAnnotation = appServletClass
                    .getAnnotation(WebServlet.class);
                if (servletAnnotation != null
                        && servletAnnotation.urlPatterns() != null
                        && servletAnnotation.urlPatterns().length > 0) {
                    if (servletAnnotation.urlPatterns()[0].endsWith("*")) {
                        m_typeURI = servletAnnotation
                            .urlPatterns()[0]
                            .substring(0,
                                       servletAnnotation
                                       .urlPatterns()[0]
                                       .length() - 1);
                    } else {
                        m_typeURI = servletAnnotation.urlPatterns()[0];
                    }
                } else {
                    m_typeURI = "";
                }
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
        long getAppID() {
            return m_id;
        }

        /**
         * Provides the context the application is executing. Usually all CCM
         * applications will now execute in the samme webapp context. The
         * app.getContextPath() return "" in this case where an application is
         * executing in no specific context but CCM's default.
         *
         * @return The context path of the application's url, "" in case of
         *         executing in the ROOT context.
         */
        String getTypeContextPath() {
            if (m_typeContextPath.equals("")) {
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
         *
         * @return
         */
        String target(final String path) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Building the target path from the request "
                                 + "path '{}' and the spec {}",
                             path,
                             this);
            }

            final StringBuffer target = new StringBuffer(128);

            target.append(m_typeURI);
            target.append(path.substring(m_instanceURI.length()));
            target.append("?");
            target.append(BaseApplicationServlet.APPLICATION_ID_PARAMETER);
            target.append("=");
            target.append(m_id);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Returning target value '{}'", target);
            }

            return target.toString();
        }

        /**
         *
         * @param obj
         *
         * @return
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }

            ApplicationSpec other = (ApplicationSpec) obj;
            return m_id == other.getAppID() && equal(m_instanceURI,
                                                     other.m_instanceURI)
                       && equal(m_typeURI, other.m_typeURI) && equal(
                m_typeContextPath, other.m_typeContextPath);

        }

        /**
         *
         * @param s1
         * @param s2
         *
         * @return
         */
        private boolean equal(String s1, String s2) {
            if (s1 == s2) {
                return true;
            }
            if (s1 == null) {
                return equal(s2, s1);
            }
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
