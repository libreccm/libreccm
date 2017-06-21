/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package com.arsdigita.ui.admin.usersgroupsroles.users;

import org.libreccm.security.Group;
import org.libreccm.security.GroupMembership;
import org.libreccm.security.Role;
import org.libreccm.security.RoleMembership;
import org.libreccm.security.User;
import org.libreccm.security.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class UsersGroupsRolesController {

    @Inject
    private UserRepository userRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    protected String getNamesOfAssignedGroups(final User user) {

        final User theUser = userRepo
            .findById(user.getPartyId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No User with ID %d in the database.",
                    user.getPartyId())));

        return theUser
            .getGroupMemberships()
            .stream()
            .map(GroupMembership::getGroup)
            .map(Group::getName)
            .sorted((name1, name2) -> name1.compareTo(name2))
            .collect(Collectors.joining(", "));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public String getNamesOfAssignedRoles(final User user) {

        final User theUser = userRepo
            .findById(user.getPartyId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No User with ID %d in the database.",
                    user.getPartyId())));

        return theUser
            .getRoleMemberships()
            .stream()
            .map(RoleMembership::getRole)
            .map(Role::getName)
            .sorted((name1, name2) -> name1.compareTo(name2))
            .collect(Collectors.joining(", "));

    }

    @Transactional(Transactional.TxType.REQUIRED)
    public String getNamesOfAllAssignedRoles(final User user) {

        final User theUser = userRepo
            .findById(user.getPartyId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No User with ID %d in the database.",
                    user.getPartyId())));

        final Set<Role> rolesFromGroups = new HashSet<>();
        theUser
            .getGroupMemberships()
            .stream()
            .map(GroupMembership::getGroup)
            .forEach(group -> {
                group
                    .getRoleMemberships()
                    .stream()
                    .map(RoleMembership::getRole)
                    .forEach(role -> rolesFromGroups.add(role));
            });

        return Stream.concat(
            theUser
                .getRoleMemberships()
                .stream()
                .map(RoleMembership::getRole)
                .map(Role::getName),
            rolesFromGroups
                .stream()
                .map(Role::getName)
                .sorted((name1, name2) -> name1.compareTo(name2)))
            .collect(Collectors.joining(", "));
    }

}
