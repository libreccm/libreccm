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
import com.arsdigita.dispatcher.InitialRequestContext;
import com.arsdigita.dispatcher.RequestContext;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.CcmApplication;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * The base servlet for CCM applications. It manages database transactions,
 * prepares an execution context for the request, and traps and handles requests
 * to redirect.</p>
 *
 * <p>
 * Most CCM applications will extend this class by implementing
 * {@link #doService(HttpServletRequest,HttpServletResponse,CcmApplication)} to
 * perform application-private dispatch to UI code.</p>
 *
 * <p>
 * The application will be available at the path
 * <code>www.example.org/ccm/applicationname</code>, where
 * <code>applicationname</code> is the name defined for the application and
 * <code>www.example.org</code> the URL of the server.
 * </p>
 *
 * @see com.arsdigita.web.BaseServlet
 * @see com.arsdigita.web.DispatcherServlet
 * @see com.arsdigita.web.RedirectSignal
 *
 * @author Justin Ross
 * &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @author
 * <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class BaseApplicationServlet extends BaseServlet {

    private static final long serialVersionUID = 3204787384428680311L;

    private static final Logger LOGGER = LogManager.getLogger(
        BaseApplicationServlet.class);

    /**
     * <p>
     * The ID of the application whose service is requested. This request
     * attribute must be set by a previous servlet or filter before this servlet
     * can proceed. In CCM, the default servlet, {@link DispatcherServlet}, sets
     * this attribute using the {@link BaseDispatcher}.
     * <strong>Important:</strong> This does only work if the application is
     * called using an URL like
     * <code>http://www.example.org/ccm/application</code>!</p>
     */
    public static final String APPLICATION_ID_ATTRIBUTE
                                   = BaseApplicationServlet.class.getName()
                                         + ".application_id";

    /**
     * <p>
     * The same as {@link #APPLICATION_ID_ATTRIBUTE}, but as a request
     * parameter. This is present so applications not using the dispatcher
     * servlet may accept requests directly to their servlets, provided the
     * application ID is given in the URL.</p>
     */
    public static final String APPLICATION_ID_PARAMETER = "app-id";

    /**
     * {@link ApplicationRepository} provided by CDI.
     */
    @Inject
    private ApplicationRepository appRepository;

    /**
     * <p>
     * Augments the context of the request and delegates to {@link
     * #doService(HttpServletRequest,HttpServletResponse,CcmApplication)}.</p>
     *
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     * @see
     * com.arsdigita.web.BaseServlet#doService(HttpServletRequest,HttpServletResponse)
     */
    @Override
    protected void doService(final HttpServletRequest request,
                             final HttpServletResponse response)
        throws ServletException, IOException {

        final CcmApplication app = getApplication(request);

        if (app == null) {
            response.sendError(404, "Application not found");
            throw new IllegalStateException("Application not found");
        }

        Web.getWebContext().setApplication(app);

//        final RequestContext rc = makeLegacyContext(
//            request, app, Web.getUserContext());
//
        final RequestContext context = new InitialRequestContext(request,
                                                            getServletContext());
        DispatcherHelper.setRequestContext(request, context);
//
//        final ServletException[] servletException = {null};
//        final IOException[] ioException = {null};
        doService(request, response, app);
    }

    /**
     * The method that
     * {@link #doService(HttpServletRequest,HttpServletResponse)} calls. Servlet
     * authors should implement this method to perform application-specific
     * request handling
     *
     * @see
     * javax.servlet.http.HttpServlet#service(HttpServletRequest,HttpServletResponse)
     *
     * @param sreq
     * @param sresp
     * @param app
     *
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    protected abstract void doService(final HttpServletRequest sreq,
                                      final HttpServletResponse sresp,
                                      final CcmApplication app)
        throws ServletException, IOException;

    /**
     *
     * @param sreq
     *
     * @return
     */
    private CcmApplication getApplication(final HttpServletRequest request) {
        LOGGER.debug("Resolving the application that will handle this request");

        Long appId = (Long) request.getAttribute(APPLICATION_ID_ATTRIBUTE);

        if (appId == null) {
            LOGGER.debug("I didn't receive an application ID with the "
                            + "servlet request; trying to get it from the "
                            + "query string");

            final String value = request.getParameter(APPLICATION_ID_PARAMETER);

            if (value != null) {
                try {
                    appId = Long.getLong(value);
                } catch (NumberFormatException ex) {
                    throw new IllegalStateException("Could not parse '" + value
                                                        + "' into a long");
                }
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Retrieving application " + appId + " from the "
                            + "database");
        }

        return appRepository.findById(appId).get();
    }

    /**
     *
     * @param sreq
     * @param app
     * @param uc
     *
     * @return
     */
//    private RequestContext makeLegacyContext(HttpServletRequest sreq,
//                                             final CcmApplication app,
//                                             final UserContext uc) {
//        s_log.debug("Setting up a legacy context object");
//
//        sreq = DispatcherHelper.restoreOriginalRequest(sreq);
//
//        final InitialRequestContext irc = new InitialRequestContext
//                                              (sreq, getServletContext());
//        final SessionContext sc = uc.getSessionContext();
//
//        final KernelRequestContext krc = new KernelRequestContext
//                                             (irc, sc, uc);
//
//        return krc;
//    }
}
