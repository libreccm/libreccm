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
package org.libreccm.shortcuts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Optional;

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
 * A Servlet filter which intercepts all requests to CCM and checks of there is
 * a shortcut for the requested URL. If there is matching shortcut the request 
 * is redirected to the URL specified in the matching shortcut. If no matching 
 * shortcut is found the next filter in the filter chain is called.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@WebFilter(urlPatterns = {"/*"})
public class ShortcutsFilter implements Filter {

    private final static Logger LOGGER = LogManager.getLogger(
        ShortcutsFilter.class);

    @Inject
    private ShortcutRepository shortcutRepository;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        //Nothing
    }

    @Override
    public void doFilter(final ServletRequest request,
                         final ServletResponse response,
                         final FilterChain chain)
        throws IOException, ServletException {

        if (request instanceof HttpServletRequest
                && response instanceof HttpServletResponse) {

            final HttpServletRequest httpRequest = (HttpServletRequest) request;

            final String path = httpRequest.getRequestURI();
            LOGGER.debug(
                "Request path into: '{}', path translated: '{}', URI: '{}'",
                httpRequest.getPathInfo(),
                httpRequest.getPathTranslated(),
                httpRequest.getRequestURI());

            if (path == null || path.isEmpty()) {
                LOGGER.debug("No path, passing off to next filter.");

                chain.doFilter(request, response);
                return;
            }

            final Optional<Shortcut> shortcut = shortcutRepository.findByUrlKey(
                cleanUrlKey(httpRequest, path));

            if (shortcut.isPresent()) {
                LOGGER.debug("Found Shortcut for path {}: {}",
                             path,
                             shortcut.toString());
                final StringBuffer targetBuffer = new StringBuffer(shortcut
                    .get().getRedirect());

                final String queryString = httpRequest.getQueryString();
                if (queryString != null && !queryString.isEmpty()) {
                    LOGGER.debug("Request URL has query parameters. Appending "
                                     + "then to target URL.");

                    targetBuffer.append('?');
                    targetBuffer.append(queryString);
                }

                final String target = targetBuffer.toString();
                LOGGER.debug("Redirecting to {}...", target);

                ((HttpServletResponse) response).sendRedirect(target);
            } else {
                LOGGER.debug(
                    "No Shortcut for {} found. Passing off to next filter.",
                    path);
                chain.doFilter(request, response);
            }

        } else {
            LOGGER.debug("Request is not a HttpServletRequest. Passing off "
                             + "to next filter.");
            chain.doFilter(request, response);
        }
    }

    private String cleanUrlKey(final HttpServletRequest request,
                               final String requestUri) {
        String urlKey;
        if (request.getContextPath() == null
                || request.getContextPath().isEmpty()) {
            urlKey = requestUri;
        } else if (requestUri.startsWith(request.getContextPath())) {
            urlKey = requestUri.substring(request.getContextPath().length());
        } else {
            urlKey = requestUri;
        }

        if (!urlKey.startsWith("/")) {
            urlKey = String.join("", "/", urlKey);
        }

        if (!urlKey.endsWith("/")) {
            urlKey = String.join("", urlKey, "/");
        }

        return urlKey;
    }

    @Override
    public void destroy() {
        //Nothing
    }

}
