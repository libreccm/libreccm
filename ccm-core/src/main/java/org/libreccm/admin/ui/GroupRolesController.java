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

import org.libreccm.security.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Set;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class GroupRolesController {

    @Inject
    private RoleRepository roleRepo;

    @Inject
    private RoleManager roleManager;

    @Inject
    private GroupRepository groupRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    protected void assignRolesToGroup(final Set<Role> roles, final Group group) {

        roles.forEach(role -> assignRoleToGroup(role, group));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void assignRoleToGroup(final Role role, final Group group) {

        final Role theRole = roleRepo
            .findById(role.getRoleId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Role with ID %d in the database.",
                    role.getRoleId())));

        final Group theGroup = groupRepo
            .findById(group.getPartyId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No group with id %d in the database. "
                        + "Where did that ID come from?",
                    group.getPartyId())));

        roleManager.assignRoleToParty(theRole, theGroup);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void removeRoleFromGroup(final Role role, final Group group) {

        final Role theRole = roleRepo
            .findById(role.getRoleId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Role with ID %d in the database.",
                    role.getRoleId())));

        final Group theGroup = groupRepo
            .findById(group.getPartyId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No group with id %d in the database. "
                        + "Where did that ID come from?",
                    group.getPartyId())));

        roleManager.removeRoleFromParty(theRole, theGroup);
    }

}
