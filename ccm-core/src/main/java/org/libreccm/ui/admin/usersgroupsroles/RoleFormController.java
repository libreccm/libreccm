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
import org.libreccm.ui.admin.AdminMessages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Processes the POST requests from the role edit form. Depending on the value
 * returned by {@link RoleDetailsModel#isNewRole()} a new role is created or
 * an existing role updated.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Controller
@Path("/users-groups-roles/roles/")
@RequestScoped
public class RoleFormController {

    @Inject
    private AdminMessages adminMessages;

    // MvcBinding does not work with Krazo 1.1.0-M1
//    @Inject
//    private BindingResult bindingResult;
    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private Models models;

    @Inject
    private RoleDetailsModel roleDetailsModel;

    @Inject
    private RoleRepository roleRepository;

    // MvcBinding does not work with Krazo 1.1.0-M1
//    @MvcBinding
    @FormParam("roleName")
//    @NotBlank
    private String roleName;

    @POST
    @Path("/new")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String createRole() {
        // MvcBinding does not work with Krazo 1.1.0-M1
//        if (bindingResult.isFailed()) {
//            models.put("errors", bindingResult.getAllMessages());
//            return "org/libreccm/ui/admin/users-groups-roles/role-form.xhtml";
//        }
        final List<String> errors = new ArrayList<>();
        if (roleName == null || roleName.matches("\\s*")) {
            errors.add(
                adminMessages.get(
                    "usersgroupsroles.roles.form.errors.name_not_empty"
                )
            );
        }
        if (!roleName.matches("[a-zA-Z0-9_-]")) {
            errors.add(
                adminMessages.get(
                    "usersgroupsroles.roles.form.errors.name_invalid"
                )
            );
        }
        if (!errors.isEmpty()) {
            models.put("errors", errors);
            roleDetailsModel.setRoleName(roleName);
            return "org/libreccm/ui/admin/users-groups-roles/role-form.xhtml";
        }

        final Role role = new Role();
        role.setName(roleName);
        roleRepository.save(role);

        return "redirect:users-groups-roles/roles";
    }

    @POST
    @Path("{roleIdentifier}/edit")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateRole(
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
                result = roleRepository.findByUuid(identifier.getIdentifier());
                break;
            default:
                result = roleRepository.findByName(identifier.getIdentifier());
                break;
        }

        if (result.isPresent()) {
            final Role role = result.get();

            // MvcBinding does not work with Krazo 1.1.0-M1
//        if (bindingResult.isFailed()) {
//            models.put("errors", bindingResult.getAllMessages());
//            return "org/libreccm/ui/admin/users-groups-roles/role-form.xhtml";
//        }
            final List<String> errors = new ArrayList<>();
            if (roleName == null || roleName.matches("\\s*")) {
                errors.add(
                    adminMessages.get(
                        "usersgroupsroles.roles.form.errors.name_not_empty"
                    )
                );
            }
            if (!roleName.matches("[a-zA-Z0-9_-]")) {
                errors.add(
                    adminMessages.get(
                        "usersgroupsroles.roles.form.errors.name_invalid"
                    )
                );
            }
            if (!errors.isEmpty()) {
                models.put("errors", errors);
                roleDetailsModel.setRole(role);
                return "org/libreccm/ui/admin/users-groups-roles/role-form.xhtml";
            }
            
            role.setName(roleName);
            return "redirect:users-groups-roles/roles";
        } else {
            models.put(
                "errors", Arrays.asList(
                    adminMessages.getMessage(
                        "usersgroupsroles.roles.not_found.message",
                        Arrays.asList(roleIdentifierParam)
                    )
                )
            );
            return "org/libreccm/ui/admin/users-groups-roles/role-not-found.xhtml";
        }
    }

}
