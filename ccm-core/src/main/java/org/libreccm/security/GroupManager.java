/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.security;

import org.libreccm.core.CoreConstants;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Manager class providing methods for adding and removing members to and from
 * a group.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class GroupManager {

    @Inject
    private GroupRepository groupRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private EntityManager entityManager;

    /**
     * Adds a member to group and saves the changed group and user entities.
     * 
     * If the user is already a member of the group the method will do nothing.
     * 
     * @param user The user to add to a group.
     * @param group  The group to which the user is added.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void addMemberToGroup(final User user, final Group group) {
        if (user == null) {
            throw new IllegalArgumentException(
                "Can't add null as user to a group.");
        }

        if (group == null) {
            throw new IllegalArgumentException("Can't add a user to group null");
        }

        if (isMemberOfGroup(user, group)) {
            return;
        }

        final GroupMembership membership = new GroupMembership();
        membership.setGroup(group);
        membership.setMember(user);

        group.addMembership(membership);
        user.addGroupMembership(membership);

        entityManager.persist(membership);
        groupRepository.save(group);
        userRepository.save(user);
    }

    /**
     * Removes a member from a group and saves the changed group an user 
     * entities. 
     * 
     * If the provided {@code User} is not a member of the provided 
     * {@code Group} the method does nothing.
     * 
     * @param member The user to remove from the group.
     * @param group The group from which the user is removed.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void removeMemberFromGroup(final User member, final Group group) {
        if (member == null) {
            throw new IllegalArgumentException(
                "Can't add null as user to a group.");
        }

        if (group == null) {
            throw new IllegalArgumentException("Can't add a user to group null");
        }
        
        final TypedQuery<GroupMembership> query = entityManager
            .createNamedQuery("GroupMembership.findByGroupAndUser",
                              GroupMembership.class);
        query.setParameter("member", member);
        query.setParameter("group", group);

        final GroupMembership delete;
        try {
            delete = query.getSingleResult();
        } catch (NoResultException ex) {
            return;
        }

        group.removeMembership(delete);
        member.removeGroupMembership(delete);
        entityManager.remove(delete);
        groupRepository.save(group);
        userRepository.save(member);
    }

    /**
     * Determins if the provided {@link User} is a member of the provided
     * {@code Group}.
     * 
     * @param member 
     * @param group
     * @return {@code true} if the provided {@code User} is member of the 
     * provided {@code Group}, {@code false} if not.
     */
    public boolean isMemberOfGroup(final User member, final Group group) {

        final TypedQuery<GroupMembership> query = entityManager
            .createNamedQuery("GroupMembership.findByGroupAndUser",
                              GroupMembership.class);
        query.setParameter("member", member);
        query.setParameter("group", group);

        final List<GroupMembership> result = query.getResultList();
        return !result.isEmpty();
    }

}
