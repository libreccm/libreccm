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
package org.libreccm.admin.ui;

import org.libreccm.security.Role;
import org.libreccm.security.RoleManager;
import org.libreccm.security.RoleRepository;
import org.libreccm.security.User;
import org.libreccm.security.UserRepository;

import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class UserRolesController {

    @Inject
    private RoleRepository roleRepo;

    @Inject
    private RoleManager roleManager;

    @Inject
    private UserRepository userRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    protected void assignRolesToUser(final Set<Role> roles, final User user) {

        roles.forEach(role -> assignRoleToUser(role, user));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void assignRoleToUser(final Role role, final User user) {

        final Role theRole = roleRepo
            .findById(role.getRoleId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Role with ID %d in the database.",
                    role.getRoleId())));

        final User theUser = userRepo
            .findById(user.getPartyId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No user with ID %d in the database. ",
                    user.getPartyId())));

        roleManager.assignRoleToParty(theRole, theUser);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void removeRoleFromUser(final Role role, final User user) {

        final Role theRole = roleRepo
            .findById(role.getRoleId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Role with ID %d in the database.",
                    role.getRoleId())));

        final User theUser = userRepo
            .findById(user.getPartyId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No group with id %d in the database. "
                        + "Where did that ID come from?",
                    user.getPartyId())));

        roleManager.removeRoleFromParty(theRole, theUser);
    }

}
