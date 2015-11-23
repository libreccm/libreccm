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
package org.libreccm.core;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * Provides methods for managing the members of a {@link Group}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class GroupManager {

    @Inject
    private transient EntityManager entityManager;
    
    @Inject
    private transient GroupRepository groupRepository;

    @Inject
    private transient UserRepository userRepository;

    /**
     * Add a user to a group. If the user is already a member of the group, this
     * method will do nothing.
     *
     * @param user  The user to add to the group. If the value is {@code null}
     *              an {@link IllegalArgumentException} will be thrown.
     * @param group The group to add the user to. If the value is {@code null}
     *              an {@link IllegalArgumentException} will be thrown.
     */
    public void addUserToGroup(final User user, final Group group) {
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
        membership.setUser(user);

        group.addMember(membership);
        user.addGroupMembership(membership);

        entityManager.persist(membership);
        groupRepository.save(group);
        userRepository.save(user);
    }

    /**
     * Remove a user from a group. If the user is not member of the group this
     * method will do nothing.
     *
     * @param user  The user to remove from the group. If the value is
     *              {@code null} an {@link IllegalArgumentException} will be
     *              thrown.
     * @param group The group to remove the user from. If the value is
     *              {@code null} an {@link IllegalArgumentException} will be
     *              thrown.
     */
    public void removeUserFromGroup(final User user, final Group group) {
        if (user == null) {
            throw new IllegalArgumentException(
                "Can't remove user null from a group.");
        }

        if (group == null) {
            throw new IllegalArgumentException(
                "Can't remove a user from group null");
        }
        
        GroupMembership delete = null;
        for(final GroupMembership membership : group.getMembers()) {
            if (membership.getUser().equals(user)) {
                delete = membership;
                break;
            }
        }
        
        if (delete != null) {
            group.removeMember(delete);
            user.removeGroupMembership(delete);
            
            entityManager.remove(delete);
        }
    }

    /**
     * Determines if a user is member of a group.
     *
     * @param user  The user to check. If the value is {@code null} an
     *              {@link IllegalArgumentException} will be thrown.
     * @param group The group to check. If the value is {@code null} an
     *              {@link IllegalArgumentException} will be thrown.
     *
     * @return {@code true} if the provided user is a member of the provided
     *         group, {@code false} otherwise.
     */
    public boolean isMemberOfGroup(final User user, final Group group) {
        if (user == null) {
            throw new IllegalArgumentException("Can't check null user");
        }

        if (group == null) {
            throw new IllegalArgumentException("Can't check null group");
        }

        boolean result = false;

        for (final GroupMembership membership : user.getGroupMemberships()) {
            if (group.equals(membership.getGroup())) {
                result = true;
                break;
            }
        }

        return result;
    }

}
