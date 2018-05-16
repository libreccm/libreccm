/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package org.libreccm.webdav;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import java.util.StringTokenizer;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@WebFilter(filterName = "WebDAVAuthFilter", urlPatterns = {"/DAV/*"})
public class WebDAVAuthFilter implements Filter {

    private static final Logger LOGGER = LogManager
        .getLogger(WebDAVAuthFilter.class);

    @Inject
    private Subject subject;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        // Nothing
    }

    @Override
    public void doFilter(final ServletRequest request,
                         final ServletResponse response,
                         final FilterChain chain) throws IOException,
                                                         ServletException {

        LOGGER.warn("Filtering");

        if (subject.isAuthenticated()) {
            chain.doFilter(request, response);
        } else {

            final HttpServletRequest httpRequest = (HttpServletRequest) request;
            final HttpServletResponse httpResponse
                                          = (HttpServletResponse) response;

            final String authHeader = httpRequest.getHeader("Authorization");
            if (authHeader == null) {

                sendUnauthorizedResponse(httpResponse, "Authorization required");

            } else {

                final StringTokenizer tokenizer
                                          = new StringTokenizer(authHeader);
                if (tokenizer.hasMoreTokens()) {

                    final String authMethod = tokenizer.nextToken();
                    if ("basic".equalsIgnoreCase(authMethod)) {

                        authenticate(httpRequest,
                                     httpResponse,
                                     chain,
                                     tokenizer.nextToken());
                    } else {
                        sendUnauthorizedResponse(httpResponse,
                                                 "Unsupported authentication method");
                    }

                } else {
                    sendUnauthorizedResponse(httpResponse,
                                             "Failed to read authentication header");
                }
            }
        }
    }

    private void authenticate(final HttpServletRequest request,
                              final HttpServletResponse response,
                              final FilterChain chain,
                              final String credentials)
        throws IOException, ServletException {

        final Optional<UsernamePasswordToken> usernamePasswordToken
                                                  = readCredentials(
                credentials);
        if (usernamePasswordToken.isPresent()) {
            try {
                subject.login(usernamePasswordToken.get());
                chain.doFilter(request, response);
            } catch (AuthenticationException ex) {
                LOGGER.warn("Authentication failed for "
                                + "subject \"{}\"",
                            usernamePasswordToken
                                .get()
                                .getUsername());
                sendUnauthorizedResponse(response,
                                         "Authentication failed");
            }
        } else {
            sendUnauthorizedResponse(response,
                                     "Invalid authentication token");
        }
    }

    private Optional<UsernamePasswordToken> readCredentials(
        final String credentialsBase64) {

        final String credentials = new String(
            Base64
                .getDecoder()
                .decode(credentialsBase64));
        final int pos = credentials.indexOf(":");
        if (pos == -1) {
            return Optional.empty();
        } else {
            final String username = credentials
                .substring(0, pos)
                .trim();
            final String password = credentials
                .substring(pos + 1);

            return Optional.of(new UsernamePasswordToken(username, password));
        }
    }

    private void sendUnauthorizedResponse(final HttpServletResponse response,
                                          final String message)
        throws IOException {

        response.setHeader("WWW-Authenticate",
                           "Basic realm=\"LibreCCM_WebDAV\"");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                           message);
    }

    @Override
    public void destroy() {
        // Nothing
    }

}
