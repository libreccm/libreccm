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

import org.libreccm.core.EmailAddress;
import org.libreccm.security.Group;
import org.libreccm.security.GroupMembership;
import org.libreccm.security.GroupRepository;
import org.libreccm.security.Role;
import org.libreccm.security.RoleMembership;
import org.libreccm.security.RoleRepository;
import org.libreccm.security.User;
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
 * Model used by the user details view and the user edit form.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("UserDetailsModel")
public class UserDetailsModel {
    
    @Inject
    private GroupRepository groupRepository;
    
    @Inject
    private RoleRepository roleRepository;

    private long userId;

    private String uuid;

    private String name;

    private String givenName;

    private String familyName;

    private EmailAddress primaryEmailAddress;

    private List<EmailAddress> emailAddresses;

    private boolean banned;

    private boolean passwordResetRequired = true;

    private List<UserGroupMembership> groupMemberships;

    private List<PartyRoleMembership> roles;
    
    private final List<Message> messages;
    
    public UserDetailsModel() {
        messages = new ArrayList<>();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void setUser(final User user) {
        Objects.requireNonNull(user);

        userId = user.getPartyId();
        uuid = user.getUuid();
        name = user.getName();
        givenName = user.getGivenName();
        familyName = user.getFamilyName();
        primaryEmailAddress = user.getPrimaryEmailAddress();
        // Ensure that we don't get a lazyily initalized list.
        emailAddresses = user
            .getEmailAddresses()
            .stream()
            .collect(Collectors.toList());
        banned = user.isBanned();
        passwordResetRequired = user.isPasswordResetRequired();
        groupMemberships = user
            .getGroupMemberships()
            .stream()
            .sorted()
            .map(GroupMembership::getGroup)
            .map(UserGroupMembership::new)
            .collect(Collectors.toList());
        roles = user
            .getRoleMemberships()
            .stream()
            .map(RoleMembership::getRole)
            .map(PartyRoleMembership::new)
            .sorted()
            .collect(Collectors.toList());
    }
    
    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }
    
    public void addMessage(final Message message) {
        messages.add(message);
    }

    public long getUserId() {
        return userId;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public EmailAddress getPrimaryEmailAddress() {
        return primaryEmailAddress;
    }

    public List<EmailAddress> getEmailAddresses() {
        return Collections.unmodifiableList(emailAddresses);
    }

    public boolean isBanned() {
        return banned;
    }

    public boolean isPasswordResetRequired() {
        return passwordResetRequired;
    }

    public List<UserGroupMembership> getGroupMemberships() {
        return Collections.unmodifiableList(groupMemberships);
    }
    
    public List<UserGroupsFormEntry> getUserGroupsFormEntries() {
        return groupRepository
            .findAll()
            .stream()
            .map(this::buildUserGroupsFormEntry)
            .collect(Collectors.toList());
    }

    public List<PartyRoleMembership> getRoles() {
        return Collections.unmodifiableList(roles);
    }
    
    public List<UserRolesFormEntry> getUserRolesFormEntries() {
        return roleRepository
            .findAll()
            .stream()
            .map(this::buildUserRolesFormEntry)
            .collect(Collectors.toList());
    }

    public boolean isNewUser() {
        return userId == 0;
    }
    
    private UserGroupsFormEntry buildUserGroupsFormEntry(final Group group) {
        final UserGroupsFormEntry entry = new UserGroupsFormEntry();
        entry.setGroupId(group.getPartyId());
        entry.setGroupName(group.getName());
        entry.setGroupUuid(group.getUuid());
        entry.setMember(
            groupMemberships
            .stream()
            .anyMatch(
                membership -> membership.getGroupUuid().equals(group.getUuid())
            )
        );
        return entry;
    }
    
    private UserRolesFormEntry buildUserRolesFormEntry(final Role role) {
        final UserRolesFormEntry entry = new UserRolesFormEntry();
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
