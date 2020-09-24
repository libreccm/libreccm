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

import org.libreccm.security.Group;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class UserGroupMembership implements Comparable<UserGroupMembership> {

    private long groupId;

    private String groupUuid;

    private String groupName;

    public UserGroupMembership() {
        // Nothing
    }

    public UserGroupMembership(final Group group) {
        this.groupId = group.getPartyId();
        this.groupUuid = group.getUuid();
        this.groupName = group.getName();
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(final long groupId) {
        this.groupId = groupId;
    }

    public String getGroupUuid() {
        return groupUuid;
    }

    public void setGroupUuid(final String groupUuid) {
        this.groupUuid = groupUuid;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }

    @Override
    public int compareTo(final UserGroupMembership other) {
        return groupName.compareTo(other.getGroupName());
    }

}
