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
package org.libreccm.ui.admin.usersgroupsroles;

import org.libreccm.core.CoreConstants;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/users-groups-roles/users")
public class UsersController {

    @Inject
    private UsersTableModel usersTableModel;

    @GET
    @Path("/")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String getUsers(
        @QueryParam("filterterm") @DefaultValue("") final String filterTerm
    ) {
        usersTableModel.setFilterTerm(filterTerm);
        return "org/libreccm/ui/admin/users-groups-roles/users.xhtml";
    }

    @GET
    @Path("/{userIdentifier}/details")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String getUser(
        @PathParam("userIdentifier") final String userIdentifier
    ) {

        throw new UnsupportedOperationException();
    }

    @POST
    @Path("/{userIdentifier}/create")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String createUser(
        @PathParam("userIdentifier") final String userIdentifier
    ) {

        throw new UnsupportedOperationException();
    }

    @GET
    @Path("/users/{userIdentifier}/edit")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String editUser(
        @PathParam("userIdentifier") final String userIdentifier
    ) {

        throw new UnsupportedOperationException();
    }

    @POST
    @Path("/users/{userIdentifier}/edit")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String updateUser(
        @PathParam("userIdentifier") final String userIdentifier
    ) {

        throw new UnsupportedOperationException();
    }

    @POST
    @Path("/users/{userIdentifier}/disable")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String disableUser(
        @PathParam("userIdentifier") final String userIdentifier
    ) {

        throw new UnsupportedOperationException();
    }

    @GET
    @Path("/{userIdentifier}/emailaddresses")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String getEmailAddressesOfUser(
        @PathParam("userIdentifier") final String userIdentifier
    ) {

        throw new UnsupportedOperationException();
    }

    @POST
    @Path("/{userIdentifier}/emailaddresses/{address}/add")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String addEmailAddresses(
        @PathParam("userIdentifier") final String userIdentifier,
        @PathParam("address") final String addressToAdd
    ) {
        throw new UnsupportedOperationException();
    }

    @POST
    @Path("/{userIdentifier}/emailaddresses/{address}/remove")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String removeEmailAddresses(
        @PathParam("userIdentifier") final String userIdentifier,
        @PathParam("address") final String addressToRemove
    ) {
        throw new UnsupportedOperationException();
    }

}
