/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("SelectedRoleModel")
public class SelectedRoleModel {

    @Inject
    private PartyRepository partyRepository;

    private String name;

    private Map<String, String> description;

    private List<String> unusedDescriptionLocales;

    private List<RoleMembershipModel> members;

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
