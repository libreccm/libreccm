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
class GroupMembersController implements Serializable {

    private static final long serialVersionUID = 9206311321172059643L;

    @Inject
    private UserRepository userRepo;

    @Inject
    private GroupRepository groupRepo;

    @Inject
    private GroupManager groupManager;

    @Transactional(Transactional.TxType.REQUIRED)
    protected void addMembersToGroup(final Set<User> users, final Group group) {

        users.forEach(user -> addMemberToGroup(user, group));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void addMemberToGroup(final User user, final Group group) {

        final User theUser = userRepo
            .findById(user.getPartyId())
            .orElseThrow(() -> new IllegalArgumentException(
            String.format("No user with id %d in the database. "
                              + "Where did that ID come from?",
                          user.getPartyId())));

        final Group theGroup = groupRepo
            .findById(group.getPartyId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No group with id %d in the database. "
                        + "Where did that ID come from?",
                    group.getPartyId())));

        groupManager.addMemberToGroup(theUser, theGroup);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void removeMemberFromGroup(final User member, final Group group) {

        final User theMember = userRepo
            .findById(member.getPartyId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No user with id %d in the database. "
                        + "Where did that ID come from?",
                    member.getPartyId())));

        final Group theGroup = groupRepo
            .findById(group.getPartyId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No group with id %d in the database. "
                        + "Where did that ID come from?",
                    group.getPartyId())));

        groupManager.removeMemberFromGroup(theMember, theGroup);
    }

}
