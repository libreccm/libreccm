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
package org.libreccm.api;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;

/**
 * A filter which intercepts {@code OPTION} requests to the API and responds
 * with the required headers.
 *
 * A {@code OPTIONS} request is send by browsers to send to endpoints to prevent
 * cross site scripting.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class PreflightRequestFilter implements ContainerRequestFilter {

    @Override
    public void filter(final ContainerRequestContext requestContext) {
        if (requestContext.getMethod().equals("OPTIONS")) {
            requestContext.abortWith(
                Response
                    .status(Response.Status.NO_CONTENT)
                    .header("Connection", "keep-alive")
                    .header("Access-Control-Allow-Origin", "*")
                    .header(
                        "Access-Control-Allow-Headers",
                        "Authorization, Content-Type"
                    )
                    .header(
                        "Access-Control-Allow-Methods",
                        "DELETE, HEAD, GET, OPTIONS, POST, PUT"
                    )
                    .header("Access-Control-Max-Age", "86400")
                    .build()
            );
        }
    }

}
