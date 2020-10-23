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
 * Adds and removes a user from groups and roles depending on the selections
 * in the corresponding dialogs in the user details view.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Controller
@Path("/users-groups-roles/users/")
@RequestScoped
public class UserGroupsRolesController {

    @Inject
    private AdminMessages adminMessages;

    @Inject
    private GroupManager groupManager;

    @Inject
    private GroupRepository groupRepository;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private Models models;

    @Inject
    private RoleManager roleManager;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private UserRepository userRepository;

    @POST
    @Path("{userIdentifier}/groups")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateGroupMemberships(
        @PathParam("userIdentifier") final String userIdentifierParam,
        @FormParam("userGroups") final String[] userGroups
    ) {
        final Identifier userIdentifier = identifierParser.parseIdentifier(
            userIdentifierParam
        );
        final Optional<User> result;
        switch (userIdentifier.getType()) {
            case ID:
                result = userRepository.findById(
                    Long.parseLong(userIdentifier.getIdentifier())
                );
                break;
            case UUID:
                result = userRepository.findByUuid(
                    userIdentifier.getIdentifier()
                );
                break;
            default:
                result = userRepository.findByName(
                    userIdentifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            final User user = result.get();
            final List<String> groupNames = Arrays.asList(userGroups);

            // Check for new groups
            final List<String> newGroupNames = groupNames
                .stream()
                .filter(groupName -> !isMember(user, groupName))
                .collect(Collectors.toList());

            // Check for removed groups
            final List<String> removedGroupNames = user
                .getGroupMemberships()
                .stream()
                .map(membership -> membership.getGroup().getName())
                .filter(groupName -> !groupNames.contains(groupName))
                .collect(Collectors.toList());

            for (final String newGroupName : newGroupNames) {
                addNewGroup(user, newGroupName);
            }

            for (final String removedGroupName : removedGroupNames) {
                removeGroup(user, removedGroupName);
            }

            return String.format(
                "redirect:/users-groups-roles/users/%s/details",
                userIdentifierParam
            );
        } else {
            models.put(
                "errors", Arrays.asList(
                    adminMessages.getMessage(
                        "usersgroupsroles.users.not_found.message",
                        Arrays.asList(userIdentifierParam)
                    )
                )
            );
            return "org/libreccm/ui/admin/users-groups-roles/user-not-found.xhtml";
        }
    }

    @POST
    @Path("{userIdentifier}/roles")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateRoleMemberships(
        @PathParam("userIdentifier") final String userIdentifierParam,
        @FormParam("userRoles") final String[] userRoles
    ) {
        final Identifier userIdentifier = identifierParser.parseIdentifier(
            userIdentifierParam
        );
        final Optional<User> result;
        switch (userIdentifier.getType()) {
            case ID:
                result = userRepository.findById(
                    Long.parseLong(userIdentifier.getIdentifier())
                );
                break;
            case UUID:
                result = userRepository.findByUuid(
                    userIdentifier.getIdentifier()
                );
                break;
            default:
                result = userRepository.findByName(
                    userIdentifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            final User user = result.get();
            final List<String> roleNames = Arrays.asList(userRoles);

            // Check for new roles
            final List<String> newRoleNames = roleNames
                .stream()
                .filter(roleName -> !hasRole(user, roleName))
                .collect(Collectors.toList());

            // Check for removed roles
            final List<String> removedRoleNames = user
                .getRoleMemberships()
                .stream()
                .map(membership -> membership.getRole().getName())
                .filter(roleName -> !roleNames.contains(roleName))
                .collect(Collectors.toList());

            for (final String newRoleName : newRoleNames) {
                addNewRole(user, newRoleName);
            }

            for (final String removedRoleName : removedRoleNames) {
                removeRole(user, removedRoleName);
            }

            return String.format(
                "redirect:/users-groups-roles/users/%s/details",
                userIdentifierParam
            );
        } else {
            models.put(
                "errors", Arrays.asList(
                    adminMessages.getMessage(
                        "usersgroupsroles.users.not_found.message",
                        Arrays.asList(userIdentifierParam)
                    )
                )
            );
            return "org/libreccm/ui/admin/users-groups-roles/user-not-found.xhtml";
        }
    }

    private boolean isMember(final User user, final String groupName) {
        return user
            .getGroupMemberships()
            .stream()
            .map(membership -> membership.getGroup().getName())
            .anyMatch(name -> name.equals(groupName));
    }

    private void addNewGroup(final User user, final String newGroupName) {
        final Optional<Group> result = groupRepository.findByName(newGroupName);
        if (result.isPresent()) {
            final Group group = result.get();
            groupManager.addMemberToGroup(user, group);
        }
    }

    private void removeGroup(final User user, final String removedGroupName) {
        final Optional<Group> result = groupRepository.findByName(
            removedGroupName
        );
        if (result.isPresent()) {
            final Group group = result.get();
            groupManager.removeMemberFromGroup(user, group);
        }
    }

    private boolean hasRole(final User user, final String roleName) {
        return user
            .getRoleMemberships()
            .stream()
            .map(membership -> membership.getMember().getName())
            .anyMatch(name -> name.equals(roleName));
    }

    private void addNewRole(final User user, final String newRoleName) {
        final Optional<Role> result = roleRepository.findByName(newRoleName);
        if (result.isPresent()) {
            final Role role = result.get();
            roleManager.assignRoleToParty(role, user);
        }
    }

    private void removeRole(final User user, final String removedRoleName) {
        final Optional<Role> result = roleRepository.findByName(
            removedRoleName
        );
        if (result.isPresent()) {
            final Role role = result.get();
            roleManager.removeRoleFromParty(role, user);
        }
    }

}
