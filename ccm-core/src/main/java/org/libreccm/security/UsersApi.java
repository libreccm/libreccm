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
package org.libreccm.security;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.libreccm.core.CoreConstants;
import org.libreccm.core.api.ApiConstants;
import org.libreccm.core.api.ExtractedIdentifier;
import org.libreccm.core.api.IdentifierExtractor;
import org.libreccm.core.api.JsonArrayCollector;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.transaction.Transactional;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Unstable! First try for a RESTful API for user management. 
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/users")
public class UsersApi {

    @Inject
    private IdentifierExtractor identifierExtractor;

    @Inject
    private UserRepository userRepository;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public JsonArray getUsers(
        @QueryParam("limit") @DefaultValue("20") final int limit,
        @QueryParam("offset") @DefaultValue("0") final int offset
    ) {
        return userRepository
            .findAll(limit, offset)
            .stream()
            .map(User::buildJson)
            .map(JsonObjectBuilder::build)
            .collect(new JsonArrayCollector());
    }

    @GET
    @Path("/{userIdentifier}")
    @Produces(MediaType.APPLICATION_JSON)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public JsonObject getUser(
        final @PathParam("userIdentifier") String identifierParam
    ) {
        final ExtractedIdentifier identifier = identifierExtractor
            .extractIdentifier(identifierParam);

        switch (identifier.getType()) {
            case ID:
                return userRepository
                    .findById(Long.parseLong(identifier.getIdentifier()))
                    .orElseThrow(
                        () -> new WebApplicationException(
                            String.format(
                                "No user with ID %s found.",
                                identifier.getIdentifier()
                            ),
                            Response.Status.NOT_FOUND)
                    )
                    .buildJson()
                    .build();
            case UUID:
                return userRepository
                    .findByUuid(identifier.getIdentifier())
                    .orElseThrow(
                        () -> new WebApplicationException(
                            String.format(
                                "No user with ID %s found.",
                                identifier.getIdentifier()
                            ),
                            Response.Status.NOT_FOUND)
                    )
                    .buildJson()
                    .build();
            default:
                return userRepository
                    .findByName(identifier.getIdentifier())
                    .orElseThrow(
                        () -> new WebApplicationException(
                            String.format(
                                "No user with ID %s found.",
                                identifier.getIdentifier()
                            ),
                            Response.Status.NOT_FOUND)
                    )
                    .buildJson()
                    .build();
        }
    }

}
