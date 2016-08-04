/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.libreccm.ui.admin;

import com.arsdigita.web.BaseServlet;
import com.arsdigita.web.CCMDispatcherServlet;
import com.arsdigita.web.WebConfig;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.shiro.subject.Subject;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.security.PermissionChecker;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named
public class AuthorizationListener {

    private static final Logger LOGGER = LogManager.getLogger(
        AuthorizationListener.class);

    @Inject
    private Subject subject;

    @Inject
    private HttpServletRequest request;

//    @Inject
//    private HttpServletResponse response;
    @Inject
    private PermissionChecker permissionChecker;

    @Inject
    private ConfigurationManager confManager;

    public void isPermitted(final ComponentSystemEvent event) {
        if (!subject.isAuthenticated()) {
            redirectToLogin();
            return;
        }

        final String requiredPrivilege = (String) event.getComponent().
            getAttributes().get("requiredPrivilege");

        if (!permissionChecker.isPermitted(requiredPrivilege)) {
            try {
                final FacesContext facesContext = FacesContext.
                    getCurrentInstance();
                final ExternalContext externalContext = facesContext.
                    getExternalContext();
                final HttpServletResponse response
                                              = (HttpServletResponse) externalContext
                    .getResponse();
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            } catch (IOException ex) {
                LOGGER.error("Failed to send FORBIDDEN error to client.", ex);
                throw new RuntimeException(
                    "Failed to send FORBIDDEN error to client", ex);
            }
        }

    }

    private void redirectToLogin() {
        try {
            final FacesContext facesContext = FacesContext.getCurrentInstance();
            final ExternalContext externalContext = facesContext.
                getExternalContext();
            final HttpServletResponse response
                                          = (HttpServletResponse) externalContext
                .getResponse();
            final WebConfig webConfig = confManager.findConfiguration(
                WebConfig.class);
            final URLCodec urlCodec = new URLCodec("utf-8");
            response.sendRedirect(new URI(String.format(
                "%s://%s:%d%s%s/register/?return_url=%s",
                request.getScheme(),
                request.getServerName(),
                request.getLocalPort(),
                CCMDispatcherServlet.getContextPath(),
                webConfig.getDispatcherServletPath(),
                urlCodec.encode(request.getAttribute(
                    BaseServlet.REQUEST_URL_ATTRIBUTE).toString())))
                .toString());
        } catch (IOException |
                 URISyntaxException |
                 EncoderException ex) {
            LOGGER.error("Failed to redirect to login.", ex);
            throw new RuntimeException("Failed to redirect to login.", ex);
        }
    }

}
