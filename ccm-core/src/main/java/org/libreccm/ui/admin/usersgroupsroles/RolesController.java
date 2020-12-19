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

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.core.CoreConstants;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;
import org.libreccm.ui.Message;
import org.libreccm.ui.MessageType;
import org.libreccm.ui.admin.AdminMessages;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Primary controller for managing roles. Retrieves the data for the views for
 * manageing roles. POST requests from the forms a processed by other
 * controllers.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/users-groups-roles/roles")
public class RolesController {

    @Inject
    private AdminMessages adminMessages;

    @Inject
    private RoleDetailsModel rolesDetailsModel;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private Models models;

    @GET
    @Path("/")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String getRoles() {
        final List<Role> roles = roleRepository.findAll();
        models.put("roles", roles);

        return "org/libreccm/ui/admin/users-groups-roles/roles.xhtml";
    }

    @GET
    @Path("/{roleIdentifier}/details")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String getRoleDetails(
        @PathParam("roleIdentifier") final String roleIdentifierParam
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            roleIdentifierParam
        );
        final Optional<Role> result;
        switch (identifier.getType()) {
            case ID:
                result = roleRepository.findById(
                    Long.parseLong(identifier.getIdentifier())
                );
                break;
            case UUID:
                result = roleRepository.findByUuid(
                    identifier.getIdentifier()
                );
                break;
            default:
                result = roleRepository.findByName(
                    identifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            rolesDetailsModel.setRole(result.get());
            return "org/libreccm/ui/admin/users-groups-roles/role-details.xhtml";
        } else {
            rolesDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "usersgroupsroles.roles.not_found_message",
                        Arrays.asList(roleIdentifierParam)
                    ),
                    MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/users-groups-roles/role-not-found.xhtml";
        }
    }

    @GET
    @Path("/new")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String newRole() {
        return "org/libreccm/ui/admin/users-groups-roles/role-form.xhtml";
    }

    @GET
    @Path("/{roleIdentifier}/edit")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String editRole(
        @PathParam("roleIdentifier") final String roleIdentifierParam
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            roleIdentifierParam
        );
        final Optional<Role> result;
        switch (identifier.getType()) {
            case ID:
                result = roleRepository.findById(
                    Long.parseLong(identifier.getIdentifier())
                );
                break;
            case UUID:
                result = roleRepository.findByUuid(
                    identifier.getIdentifier()
                );
                break;
            default:
                result = roleRepository.findByName(
                    identifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            rolesDetailsModel.setRole(result.get());
            return "org/libreccm/ui/admin/users-groups-roles/role-form.xhtml";
        } else {
            rolesDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "usersgroupsroles.roles.not_found_message",
                        Arrays.asList(roleIdentifierParam)
                    ),
                    MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/users-groups-roles/role-not-found.xhtml";
        }
    }

    @POST
    @Path("/{roleIdentifier}/delete")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String deleteRole(
        @PathParam("roleIdentifier") final String roleIdentifierParam,
        @FormParam("confirmed") final String confirmed
    ) {
        if ("true".equals(confirmed)) {
            final Identifier identifier = identifierParser.parseIdentifier(
                roleIdentifierParam
            );
            final Optional<Role> result;
            switch (identifier.getType()) {
                case ID:
                    result = roleRepository.findById(
                        Long.parseLong(identifier.getIdentifier())
                    );
                    break;
                case UUID:
                    result = roleRepository.findByUuid(
                        identifier.getIdentifier()
                    );
                    break;
                default:
                    result = roleRepository.findByName(
                        identifier.getIdentifier()
                    );
                    break;
            }

            if (result.isPresent()) {
                roleRepository.delete(result.get());
            } else {
                rolesDetailsModel.addMessage(
                    new Message(
                        adminMessages.getMessage(
                            "usersgroupsroles.roles.not_found_message",
                            Arrays.asList(roleIdentifierParam)
                        ),
                        MessageType.WARNING
                    )
                );
                return "org/libreccm/ui/admin/users-groups-roles/role-not-found.xhtml";
            }
        }

        return "redirect:users-groups-roles/roles";
    }

}
