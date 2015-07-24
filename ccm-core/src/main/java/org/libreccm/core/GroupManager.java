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

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class GroupManager {

    /**
     * Add a user to a group. If the user is already a member of the group, this
     * method will do nothing.
     *
     * @param user  The user to add to the group
     * @param group The group to add the user to.
     */
    public void addUserToGroup(final User user, final Group group) {

    }

    /**
     * Remove a user from a group. If the user is not member of the group this
     * method will do nothing.
     *
     * @param user  The user to remove from the group.
     * @param group The group to remove the user from.
     */
    public void removeUserFromGroup(final User user, final Group group) {

    }

    /**
     * Determines if a user is member of a group.
     *
     * @param user  The user to check
     * @param group The group to check
     *
     * @return {@code true} if the provided user is a member of the provided
     *         group, {@code false} otherwise.
     */
    public boolean isMemberOfGroup(final User user, final Group group) {
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
