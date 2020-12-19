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

/**
 * Model for an entry in the role members form.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class RolePartyFormEntry {

    private long partyId;

    private String partyUuid;

    private String partyName;

    private boolean member;

    public long getPartyId() {
        return partyId;
    }

    public void setPartyId(final long partyId) {
        this.partyId = partyId;
    }

    public String getPartyUuid() {
        return partyUuid;
    }

    public void setPartyUuid(final String partyUuid) {
        this.partyUuid = partyUuid;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(final String partyName) {
        this.partyName = partyName;
    }

    public boolean isMember() {
        return member;
    }

    public void setMember(final boolean member) {
        this.member = member;
    }

}
