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

import org.libreccm.security.User;

/**
 * Model friendly representation of a member of a group.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class GroupUserMembership implements Comparable<GroupUserMembership> {

    private long userId;

    private String userUuid;

    private String userName;

    private String primaryEmailAddress;

    private String givenName;

    private String familyName;

    public GroupUserMembership() {
        // Nothing
    }
    
    public GroupUserMembership(final User user) {
        userId = user.getPartyId();
        userUuid = user.getUuid();
        userName = user.getName();
        primaryEmailAddress = user.getPrimaryEmailAddress().getAddress();
        givenName = user.getGivenName();
        familyName = user.getFamilyName();
    }
    
    public long getUserId() {
        return userId;
    }

    public void setUserId(final long userId) {
        this.userId = userId;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(final String userUuid) {
        this.userUuid = userUuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public String getPrimaryEmailAddress() {
        return primaryEmailAddress;
    }

    public void setPrimaryEmailAddress(final String primaryEmailAddress) {
        this.primaryEmailAddress = primaryEmailAddress;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(final String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(final String familyName) {
        this.familyName = familyName;
    }

    @Override
    public int compareTo(final GroupUserMembership other) {
        int result = userName.compareTo(other.getUserName());
        if (result == 0) {
            return primaryEmailAddress.compareTo(other.getPrimaryEmailAddress());
        } else {
            return result;
        }
    }

}
