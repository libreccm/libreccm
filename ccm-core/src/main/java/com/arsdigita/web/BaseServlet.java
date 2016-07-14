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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class BaseServlet extends HttpServlet {

    private static final long serialVersionUID = -3402624854177649796L;

    private static final Logger LOGGER = LogManager.getFormatterLogger(
        BaseServlet.class);

    public static final String REQUEST_URL_ATTRIBUTE = BaseServlet.class
        .getName() + ".request_url";

    /**
     * Initializer uses parent class's initializer to setup the servlet request,
     * response and application context. Usually a user of this class will not
     * overwrite this method but the user extension point doInit to perform
     * local initialization tasks!
     *
     * @param config
     *
     * @throws javax.servlet.ServletException
     */
    @Override
    public void init(final ServletConfig config) throws ServletException {
        LOGGER.info("Initialising servlet %s (class: %s)...",
                    config.getServletName(),
                    getClass().getName());

        super.init(config);

        //Check if we not the ResourceManager for CCM NG. Also check if we
        //can replace static instance with an application scoped CDI Bean
        //ResourceManager.getInstance().setServletContext(getServletContext);
        doInit();
    }

    protected void doInit() throws ServletException {
        //Empty
    }

    @Override
    public final void destroy() {
        LOGGER.info("Destroying servlet %s...",
                    getServletConfig().getServletName());

        doDestroy();
    }

    protected void doDestroy() {
        /// Empty
    }

    private void internalService(final HttpServletRequest request,
                                 final HttpServletResponse response)
        throws ServletException, IOException {

        Web.init(request, getServletContext());
        Web.getWebContext().setRequestURL(getRequestURL(request));

        try {
            doService(request, response);
        } catch (RedirectSignal signal) {
            redirect(response, signal);
        } catch (ServletException ex) {
            final RedirectSignal signal = findRedirectSignal(ex);
            
            if (signal == null) {
                throw ex;
            } else {
                redirect(response, signal);
            }
        }
    }

    /**
     * <p>
     * The method that {@link
     * #doGet(HttpServletRequest,HttpServletResponse)} and {@link
     * #doPost(HttpServletRequest,HttpServletResponse)} call. This is the
     * extension point for users of this class.</p>
     *
     * @param request
     * @param response
     *
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    protected abstract void doService(final HttpServletRequest request,
                                      final HttpServletResponse response)
        throws ServletException, IOException;

    /**
     * <p>
     * Processes HTTP GET requests.</p>
     *
     * @param request
     * @param response
     *
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     * @see
     * javax.servlet.http.HttpServlet#doGet(HttpServletRequest,HttpServletResponse)
     */
    @Override
    protected final void doGet(final HttpServletRequest request,
                               final HttpServletResponse response)
        throws ServletException, IOException {
        LOGGER.info("Serving GET request path %s with servlet %s (class: %s)",
                    request.getPathInfo(),
                    getServletConfig().getServletName(),
                    getClass().getName());

        internalService(request, response);
    }

    /**
     * <p>
     * Processes HTTP POST requests.</p>
     *
     * @param request
     * @param response
     *
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     *
     * @see
     * javax.servlet.http.HttpServlet#doPost(HttpServletRequest,HttpServletResponse)
     */
    @Override
    protected final void doPost(final HttpServletRequest request,
                                final HttpServletResponse response)
        throws ServletException, IOException {
        LOGGER.info("Serving POST request path %s with servlet %s (class: %s)",
                    request.getPathInfo(),
                    getServletConfig().getServletName(),
                    getClass().getName());

        internalService(request, response);
    }

    private URL getRequestURL(final HttpServletRequest request) {
        URL url = (URL) request.getAttribute(REQUEST_URL_ATTRIBUTE);

        if (url == null) {
            url = new URL(request);
        }

        return url;
    }

    private RedirectSignal findRedirectSignal(final ServletException ex) {
        Throwable root = ex.getRootCause();

        while (root instanceof ServletException) {
            root = ((ServletException) root).getRootCause();
        }

        if (root instanceof RedirectSignal) {
            return (RedirectSignal) root;
        } else {
            return null;
        }
    }

    private void redirect(final HttpServletResponse response,
                          final RedirectSignal redirectSignal)
        throws IOException {
        final String url = response.encodeRedirectURL(redirectSignal
            .getDestinationURL());

        response.sendRedirect(url);
    }

}
