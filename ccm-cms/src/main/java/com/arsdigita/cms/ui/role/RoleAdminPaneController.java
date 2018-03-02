/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.role;

import com.arsdigita.cms.CMS;
import com.arsdigita.kernel.KernelConfig;

import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.security.Party;
import org.libreccm.security.PartyRepository;
import org.libreccm.security.Permission;
import org.libreccm.security.PermissionManager;
import org.libreccm.security.Role;
import org.libreccm.security.RoleManager;
import org.libreccm.security.RoleRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionManager;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.privileges.AdminPrivileges;
import org.librecms.contentsection.privileges.AssetPrivileges;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class RoleAdminPaneController {

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private PartyRepository partyRepo;

    @Inject
    private PermissionManager permissionManager;

    @Inject
    private RoleManager roleManager;
    
    @Inject
    private RoleRepository roleRepo;

    @Inject
    private ContentSectionManager sectionManager;

    @Inject
    private ContentSectionRepository sectionRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Role> findRolesForContentSection(final ContentSection section) {
        final ContentSection contentSection = sectionRepo
            .findById(section.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContentSection with id %d in the database. "
                + "Where did that ID come from?",
            section.getObjectId())));

        return new ArrayList<>(contentSection.getRoles());
    }

    public String[] getGrantedPrivileges(final Role role,
                                         final ContentSection section) {
        final List<Permission> sectionPermissions = permissionManager
            .findPermissionsForRoleAndObject(role, section);
        final List<Permission> itemPermissions = permissionManager
            .findPermissionsForRoleAndObject(role,
                                             section.getRootDocumentsFolder());
        final List<Permission> assetPermissions = permissionManager
            .findPermissionsForRoleAndObject(role,
                                             section.getRootAssetsFolder());
        final List<Permission> permissions = new ArrayList<>();
        permissions.addAll(sectionPermissions);
        permissions.addAll(itemPermissions);
        permissions.addAll(assetPermissions);
        final List<String> privileges = permissions.stream()
            .map(Permission::getGrantedPrivilege)
            .collect(Collectors.toList());

        return privileges.toArray(new String[]{});
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public String generateGrantedPermissionsString(final Role role,
                                                   final ContentSection section) {

        final List<Permission> sectionPermissions = permissionManager
            .findPermissionsForRoleAndObject(role, section);
        final List<Permission> itemPermissions = permissionManager
            .findPermissionsForRoleAndObject(role,
                                             section.getRootDocumentsFolder());
        final List<Permission> assetPermissions = permissionManager
            .findPermissionsForRoleAndObject(role,
                                             section.getRootAssetsFolder());
        final List<Permission> permissions = new ArrayList<>();
        permissions.addAll(sectionPermissions);
        permissions.addAll(itemPermissions);
        permissions.addAll(assetPermissions);

        return permissions.stream()
            .map(Permission::getGrantedPrivilege)
            .collect(Collectors.joining("; "));

    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Party> createRoleMemberList(final Role role) {

        final Role theRole = roleRepo
            .findById(role.getRoleId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No role with ID %d in the database. Where did that Id come from?",
            role.getRoleId())));

        return theRole.getMemberships()
            .stream()
            .map(membership -> membership.getMember())
            .sorted((member1, member2) -> {
                return member1.getName().compareTo(member2.getName());
            })
            .collect(Collectors.toList());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void deleteRole(final ContentSection section,
                           final String roleId) {

        final Role role = roleRepo.findById(Long.parseLong(roleId))
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No Role with ID %s in the database. Where did that ID come from?",
            roleId)));
        final ContentSection contentSection = sectionRepo
            .findById(section.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContentSection with ID %d in the database. "
                + "Where did that ID come from?",
            section.getObjectId())));

        sectionManager.removeRoleFromContentSection(contentSection, role);
        roleRepo.delete(role);
    }

    /**
     *
     * @param name
     * @param selectedRole
     *
     * @return {@code true} if name is unique, {@code false} otherwise.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public boolean validateRoleNameUniqueness(final String name,
                                              final Role selectedRole) {

        final ContentSection section = CMS.getContext().getContentSection();

        final ContentSection contentSection = sectionRepo
            .findById(section.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContentSection with ID %d in the database."
                + " Where did that ID come from?",
            section.getObjectId())));

        final Collection<Role> roles = contentSection.getRoles();
        boolean result = true;
        for (final Role role : roles) {
            if (role.getName().equalsIgnoreCase(name)
                    && (selectedRole == null
                        || selectedRole.getRoleId() != role.getRoleId())) {
                result = false;
                break;
            }
        }

        return result;
    }

    public void saveRole(final Role role,
                         final String roleName,
                         final String roleDescription,
                         final String[] selectedPermissions) {

        final Role roleToSave = roleRepo.findById(role.getRoleId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No Role with ID %d in the database. Where did that ID come from?",
            role.getRoleId())));

        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);
        final Locale defaultLocale = kernelConfig.getDefaultLocale();

        role.setName(roleName);
        role.getDescription().addValue(defaultLocale, roleDescription);

        roleRepo.save(role);

        final ContentSection contentSection = sectionRepo.findById(
            CMS.getContext().getContentSection().getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContentSection with ID %d in the database."
                + "Where did that ID come from?",
            CMS.getContext().getContentSection().getObjectId())));

        final List<String> adminPrivileges = permissionManager
            .listDefiniedPrivileges(AdminPrivileges.class);
        final List<String> itemPrivileges = permissionManager
            .listDefiniedPrivileges(ItemPrivileges.class);
        final List<String> assetPrivileges = permissionManager
            .listDefiniedPrivileges(AssetPrivileges.class);

        final Folder rootDocumentsFolder = contentSection
            .getRootDocumentsFolder();
        final Folder rootAssetsFolder = contentSection.getRootAssetsFolder();

        final List<Permission> currentPermissionsSection = permissionManager
            .findPermissionsForRoleAndObject(role, contentSection);
        final List<Permission> currentPermissionsDocuments = permissionManager
            .findPermissionsForRoleAndObject(role, rootDocumentsFolder);
        final List<Permission> currentPermissionsAssets = permissionManager
            .findPermissionsForRoleAndObject(role, rootAssetsFolder);

        //Revoke permissions not in selectedPermissions
        revokeNotSelectedPrivileges(selectedPermissions,
                                    role,
                                    currentPermissionsSection);
        revokeNotSelectedPrivileges(selectedPermissions,
                                    role,
                                    currentPermissionsDocuments);
        revokeNotSelectedPrivileges(selectedPermissions,
                                    role,
                                    currentPermissionsAssets);

        // Grant selected privileges
        for (final String privilege : adminPrivileges) {
            if (isPrivilegeSelected(selectedPermissions, privilege)) {
                permissionManager.grantPrivilege(privilege,
                                                 role,
                                                 contentSection);
            }
        }

        for (final String privilege : itemPrivileges) {
            if (isPrivilegeSelected(selectedPermissions, privilege)) {
                permissionManager.grantPrivilege(privilege,
                                                 role,
                                                 rootDocumentsFolder);
            }
        }

        for (final String privilege : assetPrivileges) {
            if (isPrivilegeSelected(selectedPermissions, privilege)) {
                permissionManager.grantPrivilege(privilege,
                                                 role,
                                                 rootAssetsFolder);
            }
        }
    }

    private void revokeNotSelectedPrivileges(final String[] selectedPrivileges,
                                             final Role role,
                                             final List<Permission> permissions) {
        for (final Permission permission : permissions) {
            if (!isPrivilegeSelected(selectedPrivileges,
                                     permission.getGrantedPrivilege())) {
                permissionManager.revokePrivilege(
                    permission.getGrantedPrivilege(),
                    role,
                    permission.getObject());
            }
        }
    }

    private boolean isPrivilegeSelected(
        final String[] selectedPrivileges, final String privilege) {

        return Arrays.stream(selectedPrivileges)
            .anyMatch(current -> current.equals(privilege));

    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Role addRole(final String name,
                        final String description,
                        final String[] selectedPrivileges) {

        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);
        final Locale defaultLocale = kernelConfig.getDefaultLocale();

        final Role role = new Role();
        role.setName(name);
        role.getDescription().addValue(defaultLocale, description);

        roleRepo.save(role);

        final List<String> adminPrivileges = permissionManager
            .listDefiniedPrivileges(AdminPrivileges.class);
        final List<String> itemPrivileges = permissionManager
            .listDefiniedPrivileges(ItemPrivileges.class);
        final List<String> assetPrivileges = permissionManager
            .listDefiniedPrivileges(AssetPrivileges.class);

        final ContentSection contentSection = sectionRepo.findById(
            CMS.getContext().getContentSection().getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContentSection with ID %d in the database."
                + "Where did that ID come from?",
            CMS.getContext().getContentSection().getObjectId())));
        sectionManager.addRoleToContentSection(role, contentSection);
        final Folder rootDocumentsFolder = contentSection
            .getRootDocumentsFolder();
        final Folder rootAssetsFolder = contentSection.getRootAssetsFolder();

        for (final String privilege : adminPrivileges) {
            if (isPrivilegeSelected(selectedPrivileges, privilege)) {
                permissionManager.grantPrivilege(privilege,
                                                 role,
                                                 contentSection);
            }
        }

        for (final String privilege : itemPrivileges) {
            if (isPrivilegeSelected(selectedPrivileges, privilege)) {
                permissionManager.grantPrivilege(privilege,
                                                 role,
                                                 rootDocumentsFolder);
            }
        }

        for (final String privilege : assetPrivileges) {
            if (isPrivilegeSelected(selectedPrivileges, privilege)) {
                permissionManager.grantPrivilege(privilege,
                                                 role,
                                                 rootAssetsFolder);
            }
        }

        return role;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void assignRoleToParty(final long roleId, final long partyId) {

        final Role role = roleRepo
            .findById(roleId)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No role with ID %d in the database.",
                    roleId)));
        final Party party = partyRepo
            .findById(partyId)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No party with ID %d in the database.",
                    partyId)));

        roleManager.assignRoleToParty(role, party);
    }

}
