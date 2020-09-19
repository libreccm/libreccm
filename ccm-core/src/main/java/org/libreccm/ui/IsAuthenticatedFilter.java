/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.ui;

import org.libreccm.security.Shiro;

import java.io.IOException;
import java.net.URI;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@PreMatching
public class IsAuthenticatedFilter implements ContainerRequestFilter {

    @Inject
    private ServletContext servletContext;

    @Inject
    private Shiro shiro;

    @Override
    public void filter(final ContainerRequestContext requestContext)
        throws IOException {
        if (!shiro.getSubject().isAuthenticated()) {
            final String contextPath = servletContext.getContextPath();
            final String returnUrl = requestContext
                .getUriInfo()
                .getRequestUri()
                .getPath();
            requestContext.abortWith(
                Response.temporaryRedirect(
                    URI.create(
                        String.format(
                            "/%s/ccm/register?return_url=%s",
                            contextPath,
                            returnUrl
                        )
                    )
                ).build()
            );
        }
    }

}
