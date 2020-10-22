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

import java.util.Arrays;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.mvc.binding.BindingResult;
import javax.mvc.binding.MvcBinding;
import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Controller
@Path("/users-groups-roles/roles/")
@RequestScoped
public class RoleFormController {

    @Inject
    private AdminMessages adminMessages;

    @Inject
    private BindingResult bindingResult;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private Models models;

    @Inject
    private RoleRepository roleRepository;

    @MvcBinding
    @FormParam("roleName")
    @NotBlank
    private String roleName;

    @POST
    @Path("/new")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String createRole() {
        if (bindingResult.isFailed()) {
            models.put("errors", bindingResult.getAllMessages());
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
        if (bindingResult.isFailed()) {
            models.put("errors", bindingResult.getAllMessages());
            return "org/libreccm/ui/admin/users-groups-roles/role-form.xhtml";
        }

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
