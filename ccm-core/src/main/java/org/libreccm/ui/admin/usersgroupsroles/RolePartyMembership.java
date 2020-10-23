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

import org.libreccm.security.Party;

import java.util.Objects;

/**
 * Model friendly representation of a member of a role.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class RolePartyMembership implements Comparable<RolePartyMembership>{
    
    private long partyId;
    
    private String partyUuid;
    
    private String partyName;
    
    public RolePartyMembership() {
        // Nothing
    }
    
    public RolePartyMembership(final Party party) {
        partyId = party.getPartyId();
        partyUuid = party.getUuid();
        partyName = party.getName();
    }

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
    
    @Override
    public int compareTo(final RolePartyMembership other) {
        return partyName.compareTo(
            Objects.requireNonNull(other).getPartyName()
        );
    }
    
}
