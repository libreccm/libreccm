/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui.contentsections;

import org.libreccm.security.Party;
import org.libreccm.security.PartyRepository;
import org.libreccm.ui.admin.usersgroupsroles.RolePartyFormEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Model for details view of a role providing the data of the selected role.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("SelectedRoleModel")
public class SelectedRoleModel {

    /**
     * Party repository for retrieving parties.
     */
    @Inject
    private PartyRepository partyRepository;

    /**
     * The name of the role.
     */
    private String name;

    /**
     * The localized descriptions of the role.
     */
    private Map<String, String> description;

    /**
     * Locales for which not localized description has been defined yet.
     */
    private List<String> unusedDescriptionLocales;

    /**
     * The members of the role.
     */
    private List<RoleMembershipModel> members;

    /**
     * Builds {@link RolePartyFormEntry} objects for all parties. They are used
     * to create the form for adding members and removing members from the role.
     *
     * @return A list of {@link RolePartyFormEntry} objects for all parties.
     */
    public List<RolePartyFormEntry> getRolePartyFormEnties() {
        return partyRepository
            .findAll()
            .stream()
            .map(this::buildRolePartyFormEntry)
            .collect(Collectors.toList());
    }

    /**
     * Permissions of the role for the content section.
     */
    private List<RoleSectionPermissionModel> permissions;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Map<String, String> getDescription() {
        return Collections.unmodifiableMap(description);
    }

    public void setDescription(final Map<String, String> description) {
        this.description = new HashMap<>(description);
    }

    public List<RoleMembershipModel> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public void setMembers(final List<RoleMembershipModel> members) {
        this.members = new ArrayList<>(members);
    }

    public List<RoleSectionPermissionModel> getPermissions() {
        return Collections.unmodifiableList(permissions);
    }

    public void setPermissions(
        final List<RoleSectionPermissionModel> permissions) {
        this.permissions = new ArrayList<>(permissions);
    }

    public List<String> getUnusedDescriptionLocales() {
        return Collections.unmodifiableList(unusedDescriptionLocales);
    }

    public void setUnusedDescriptionLocales(
        final List<String> unusedDescriptionLocales
    ) {
        this.unusedDescriptionLocales
            = new ArrayList<>(unusedDescriptionLocales);
    }

    public boolean getHasUnusedDescriptionLocales() {
        return !unusedDescriptionLocales.isEmpty();
    }

    /**
     * Helper method for building a {@link RolePartyFormEntry} for the a
     * {@link Party}.
     *
     * @param party The party.
     *
     * @return A {@link RolePartyFormEntry} for the {@code party}.
     */
    private RolePartyFormEntry buildRolePartyFormEntry(final Party party) {
        final RolePartyFormEntry entry = new RolePartyFormEntry();
        entry.setPartyId(party.getPartyId());
        entry.setPartyUuid(party.getUuid());
        entry.setPartyName(party.getName());
        entry.setMember(
            members
                .stream()
                .anyMatch(
                    membership -> membership.getMemberUuid().equals(party
                        .getUuid())
                )
        );
        return entry;
    }

}
