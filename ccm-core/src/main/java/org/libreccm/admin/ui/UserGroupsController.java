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

import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.Group;
import org.libreccm.security.GroupManager;
import org.libreccm.security.GroupRepository;
import org.libreccm.security.User;
import org.libreccm.security.UserRepository;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import java.util.Set;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class UserGroupsController implements Serializable {

    private static final long serialVersionUID = -3226620773017042743L;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private GroupRepository groupRepo;

    @Inject
    private GroupManager groupManager;

    @Inject
    private UserRepository userRepo;

    protected GlobalizationHelper getGlobalizationHelper() {
        return globalizationHelper;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void addUserToGroups(final User user, final Set<Group> groups) {

        groups.forEach(group -> addUserToGroup(user, group));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void addUserToGroup(final User user, final Group group) {

        final Group theGroup = groupRepo
            .findById(group.getPartyId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Group with ID %d in the database.",
                    group.getPartyId())));

        final User theUser = userRepo
            .findById(user.getPartyId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No user with ID %d in the database. ",
                    user.getPartyId())));

        groupManager.addMemberToGroup(theUser, theGroup);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void removeUserFromGroup(final User user, final Group group) {

        final Group theGroup = groupRepo
            .findById(group.getPartyId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Group with ID %d in the database.",
                    group.getPartyId())));

        final User theUser = userRepo
            .findById(user.getPartyId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No user with ID %d in the database. ",
                    user.getPartyId())));

        groupManager.removeMemberFromGroup(theUser, theGroup);
    }

}
