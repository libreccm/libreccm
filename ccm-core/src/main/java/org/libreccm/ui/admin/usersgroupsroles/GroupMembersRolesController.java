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
import org.libreccm.security.Group;
import org.libreccm.security.GroupManager;
import org.libreccm.security.GroupRepository;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.security.Role;
import org.libreccm.security.RoleManager;
import org.libreccm.security.RoleRepository;
import org.libreccm.security.User;
import org.libreccm.security.UserRepository;
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
@Path("/users-groups-roles/groups/")
@RequestScoped
public class GroupMembersRolesController {

    @Inject
    private AdminMessages adminMessages;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private GroupManager groupManager;

    @Inject
    private GroupRepository groupRepository;

    @Inject
    private Models models;

    @Inject
    private RoleManager roleManager;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private UserRepository userRepository;

    @POST
    @Path("{groupIdentifier}/members")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateGroupMemberships(
        @PathParam("groupIdentifier") final String groupIdentifierParam,
        @FormParam("groupMembers") final String[] groupMembersParam
    ) {
        final Identifier groupIdentifier = identifierParser.parseIdentifier(
            groupIdentifierParam
        );
        final Optional<Group> result;
        switch (groupIdentifier.getType()) {
            case ID:
                result = groupRepository.findById(
                    Long.parseLong(groupIdentifier.getIdentifier())
                );
                break;
            case UUID:
                result = groupRepository.findByUuid(
                    groupIdentifier.getIdentifier()
                );
                break;
            default:
                result = groupRepository.findByName(
                    groupIdentifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            final Group group = result.get();
            final List<String> memberNames = Arrays.asList(groupMembersParam);

            // Check for new members
            final List<String> newMemberNames = memberNames
                .stream()
                .filter(memberName -> !hasMember(group, memberName))
                .collect(Collectors.toList());

            // Check for removed members
            final List<String> removedMemberNames = group
                .getMemberships()
                .stream()
                .map(membership -> membership.getMember().getName())
                .filter(memberName -> !memberNames.contains(memberName))
                .collect(Collectors.toList());

            for (final String newMemberName : newMemberNames) {
                addNewMember(group, newMemberName);
            }

            for (final String removedMemberName : removedMemberNames) {
                removeMember(group, removedMemberName);
            }

            return String.format(
                "redirect:/users-groups-roles/groups/%s/details",
                groupIdentifierParam
            );
        } else {
            models.put(
                "errors", Arrays.asList(
                    adminMessages.getMessage(
                        "usersgroupsroles.groups.not_found.message",
                        Arrays.asList(groupIdentifierParam)
                    )
                )
            );
            return "org/libreccm/ui/admin/users-groups-roles/group-not-found.xhtml";
        }
    }

    @POST
    @Path("{groupIdentifier}/roles")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateRoleMemberships(
        @PathParam("groupIdentifier") final String groupIdentifierParam,
        @FormParam("groupRoles") final String[] groupRoles
    ) {
        final Identifier groupIdentifier = identifierParser.parseIdentifier(
            groupIdentifierParam
        );
        final Optional<Group> result;
        switch (groupIdentifier.getType()) {
            case ID:
                result = groupRepository.findById(
                    Long.parseLong(groupIdentifier.getIdentifier())
                );
                break;
            case UUID:
                result = groupRepository.findByUuid(
                    groupIdentifier.getIdentifier()
                );
                break;
            default:
                result = groupRepository.findByName(
                    groupIdentifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            final Group group = result.get();
            final List<String> roleNames = Arrays.asList(groupRoles);

            // Check for new roles
            final List<String> newRoleNames = roleNames
                .stream()
                .filter(roleName -> !hasRole(group, roleName))
                .collect(Collectors.toList());

            // Check for removed roles
            final List<String> removedRoleNames = group
                .getRoleMemberships()
                .stream()
                .map(membership -> membership.getRole().getName())
                .filter(roleName -> !roleNames.contains(roleName))
                .collect(Collectors.toList());

            for (final String newRoleName : newRoleNames) {
                addNewRole(group, newRoleName);
            }

            for (final String removedRoleName : removedRoleNames) {
                removeRole(group, removedRoleName);
            }

            return String.format(
                "redirect:/users-groups-roles/groups/%s/details",
                groupIdentifierParam
            );
        } else {
            models.put(
                "errors", Arrays.asList(
                    adminMessages.getMessage(
                        "usersgroupsroles.groups.not_found.message",
                        Arrays.asList(groupIdentifierParam)
                    )
                )
            );
            return "org/libreccm/ui/admin/users-groups-roles/group-not-found.xhtml";
        }
    }

    private boolean hasMember(final Group group, final String memberName) {
        return group
            .getMemberships()
            .stream()
            .map(membership -> membership.getMember().getName())
            .anyMatch(name -> name.equals(memberName));
    }

    private void addNewMember(final Group group, final String newMemberName) {
        final Optional<User> result = userRepository.findByName(newMemberName);
        if (result.isPresent()) {
            final User user = result.get();
            groupManager.addMemberToGroup(user, group);
        }
    }

    private void removeMember(
        final Group group, final String removedMemberName
    ) {
        final Optional<User> result = userRepository.findByName(
            removedMemberName
        );
        if (result.isPresent()) {
            final User user = result.get();
            groupManager.removeMemberFromGroup(user, group);
        }
    }

    private boolean hasRole(final Group group, final String roleName) {
        return group
            .getRoleMemberships()
            .stream()
            .map(membership -> membership.getMember().getName())
            .anyMatch(name -> name.equals(roleName));
    }

    private void addNewRole(final Group group, final String newRoleName) {
        final Optional<Role> result = roleRepository.findByName(newRoleName);
        if (result.isPresent()) {
            final Role role = result.get();
            roleManager.assignRoleToParty(role, group);
        }
    }

    private void removeRole(final Group group, final String removedRoleName) {
        final Optional<Role> result = roleRepository.findByName(
            removedRoleName
        );
        if (result.isPresent()) {
            final Role role = result.get();
            roleManager.removeRoleFromParty(role, group);
        }
    }

}
