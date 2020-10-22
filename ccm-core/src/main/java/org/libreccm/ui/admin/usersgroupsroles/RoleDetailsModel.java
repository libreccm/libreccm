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
import org.libreccm.security.PartyRepository;
import org.libreccm.security.Role;
import org.libreccm.security.RoleMembership;
import org.libreccm.ui.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("RoleDetailsModel")
public class RoleDetailsModel {

    @Inject
    private PartyRepository partyRepository;

    private long roleId;

    private String uuid;

    private String roleName;

    private List<RolePartyMembership> members;

    private List<RolePermission> permissions;

    private final List<Message> messages;

    public RoleDetailsModel() {
        this.messages = new ArrayList<>();
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public void addMessage(final Message message) {
        messages.add(message);
    }

    public long getRoleId() {
        return roleId;
    }

    public String getUuid() {
        return uuid;
    }

    public String getRoleName() {
        return roleName;
    }

    public List<RolePartyMembership> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public List<RolePermission> getPermissions() {
        return Collections.unmodifiableList(permissions);
    }

    public List<RolePartyFormEntry> getRolePartyFormEnties() {
        return partyRepository
            .findAll()
            .stream()
            .map(this::buildRolePartyFormEntry)
            .collect(Collectors.toList());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void setRole(final Role role) {
        Objects.requireNonNull(role);
        
        roleId = role.getRoleId();
        uuid = role.getUuid();
        roleName = role.getName();
        members = role
            .getMemberships()
            .stream()
            .map(RoleMembership::getMember)
            .map(RolePartyMembership::new)
            .sorted()
            .collect(Collectors.toList());
    }

    public boolean isNewRole() {
        return roleId == 0;
    }

    private RolePartyFormEntry buildRolePartyFormEntry(final Party party) {
        final RolePartyFormEntry entry = new RolePartyFormEntry();
        entry.setPartyId(party.getPartyId());
        entry.setPartyUuid(party.getUuid());
        entry.setPartyName(party.getName());
        entry.setMember(
            members
                .stream()
                .anyMatch(
                    membership -> membership.getPartyUuid().equals(party
                        .getUuid())
                )
        );
        return entry;
    }

}
