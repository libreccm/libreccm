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
import org.libreccm.security.GroupMembership;
import org.libreccm.security.Role;
import org.libreccm.security.RoleMembership;
import org.libreccm.security.RoleRepository;
import org.libreccm.security.User;
import org.libreccm.security.UserRepository;
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
 * Model used by the group details form and the group edit form.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("GroupDetailsModel")
public class GroupDetailsModel {
    
    @Inject
    private RoleRepository roleRepository;
    
    @Inject
    private UserRepository userRepository;

    private long groupId;

    private String uuid;

    private String groupName;

    private List<GroupUserMembership> members;

    private List<PartyRoleMembership> roles;

    private final List<Message> messages;

    public GroupDetailsModel() {
        messages = new ArrayList<>();
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public void addMessage(final Message message) {
        messages.add(message);
    }

    public long getGroupId() {
        return groupId;
    }

    public String getUuid() {
        return uuid;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<GroupUserMembership> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public List<PartyRoleMembership> getRoles() {
        return Collections.unmodifiableList(roles);
    }
    
    public List<GroupUserFormEntry> getGroupMemberFormEntries() {
        return userRepository
            .findAll()
            .stream()
            .map(this::buildGroupUserFormEntry)
            .collect(Collectors.toList());
    }
    
    public List<PartyRolesFormEntry> getGroupRolesFormEntries() {
        return roleRepository
            .findAll()
            .stream()
            .map(this::buildGroupRolesFormEntry)
            .collect(Collectors.toList());
    }
        
    @Transactional(Transactional.TxType.REQUIRED)
    protected void setGroup(final Group group) {
        Objects.requireNonNull(group);

        groupId = group.getPartyId();
        uuid = group.getUuid();
        groupName = group.getName();
        members = group
            .getMemberships()
            .stream()
            .map(GroupMembership::getMember)
            .map(GroupUserMembership::new)
            .sorted()
            .collect(Collectors.toList());
        roles = group
            .getRoleMemberships()
            .stream()
            .map(RoleMembership::getRole)
            .map(PartyRoleMembership::new)
            .sorted()
            .collect(Collectors.toList());
    }

    public boolean isNewGroup() {
        return groupId == 0;
    }
    
    private GroupUserFormEntry buildGroupUserFormEntry(final User user) {
        final GroupUserFormEntry entry = new GroupUserFormEntry();
        entry.setUserId(user.getPartyId());
        entry.setUserName(user.getName());
        entry.setUserUuid(user.getUuid());
        entry.setMember(
            members
            .stream()
            .anyMatch(
                membership -> membership.getUserUuid().equals(user.getUuid())
            )
        );
        return entry;
    }
    
    private PartyRolesFormEntry buildGroupRolesFormEntry(final Role role) {
        final PartyRolesFormEntry entry = new PartyRolesFormEntry();
        entry.setRoleId(role.getRoleId());
        entry.setRoleName(role.getName());
        entry.setRoleUuid(role.getUuid());
        entry.setMember(
            roles
            .stream()
            .anyMatch(
                membership -> membership.getRoleUuid().equals(role.getUuid())
            )
        );
        return entry;
    }

}
