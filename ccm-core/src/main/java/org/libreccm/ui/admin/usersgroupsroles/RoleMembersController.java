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
import org.libreccm.security.Party;
import org.libreccm.security.PartyRepository;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.security.Role;
import org.libreccm.security.RoleManager;
import org.libreccm.security.RoleRepository;
import org.libreccm.ui.admin.AdminMessages;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Controller
@Path("/users-groups-roles/roles/")
@RequestScoped

public class RoleMembersController {

    @Inject
    private AdminMessages adminMessages;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private Models models;

    @Inject
    private PartyRepository partyRepository;

    @Inject
    private RoleManager roleManager;

    @Inject
    private RoleRepository roleRepository;

    @POST
    @Path("{roleIdentifier}/members")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateRoleMemberships(
        @PathParam("roleIdentifier") final String roleIdentifierParam,
        @FormParam("roleMembers") final String[] roleMembersParam
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
            final Role role = result.get();
            final List<String> memberNames = Arrays.asList(roleMembersParam);

            // Check for new members
            final List<String> newMemberNames = memberNames
                .stream()
                .filter(memberName -> !hasMember(role, memberName))
                .collect(Collectors.toList());

            // Check for removed members
            final List<String> removedMemberNames = role
                .getMemberships()
                .stream()
                .map(membership -> membership.getMember().getName())
                .filter(memberName -> !memberNames.contains(memberName))
                .collect(Collectors.toList());

            for (final String newMemberName : newMemberNames) {
                addNewMember(role, newMemberName);
            }

            for (final String removedMemberName : removedMemberNames) {
                removeMember(role, removedMemberName);
            }

            return String.format(
                "redirect:/users-groups-roles/roles/%s/details",
                roleIdentifierParam
            );
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

    private boolean hasMember(final Role role, final String memberName) {
        return role
            .getMemberships()
            .stream()
            .map(membership -> membership.getMember().getName())
            .anyMatch(name -> name.equals(memberName));
    }
    
    private void addNewMember(final Role role, final String newMemberName) {
        final Optional<Party> result = partyRepository.findByName(
            newMemberName
        );
        if (result.isPresent()) {
            final Party party = result.get();
            roleManager.assignRoleToParty(role, party);
        }
    }
    
    private void removeMember(final Role role, final String removedMemberName) {
        final Optional<Party> result = partyRepository.findByName(
            removedMemberName
        );
        if (result.isPresent()) {
            final Party party = result.get();
            roleManager.removeRoleFromParty(role, party);
        }
    }
}
