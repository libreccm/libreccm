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

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.Party;
import org.libreccm.security.PartyRepository;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.PermissionManager;
import org.libreccm.security.Role;
import org.libreccm.security.RoleManager;
import org.libreccm.security.RoleMembership;
import org.libreccm.security.RoleRepository;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionManager;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.privileges.AdminPrivileges;
import org.librecms.contentsection.privileges.AssetPrivileges;
import org.librecms.contentsection.privileges.ItemPrivileges;
import org.librecms.ui.CmsAdminMessages;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;

/**
 * Controller for managing the roles of a content section.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/{sectionIdentifier}/configuration/roles")
public class ConfigurationRolesController {

    /**
     * Used to check admin permissions for content sections.
     */
    @Inject
    private AdminPermissionsChecker adminPermissionsChecker;

    /**
     * Wrapper around the messages bundle for CMS admin messages.
     */
    @Inject
    private CmsAdminMessages messages;

    /**
     * Used to perform actions involing {@link ContentSection}s.
     */
    @Inject
    private ContentSectionManager sectionManager;

    /**
     * Model for the current content section.
     */
    @Inject
    private ContentSectionModel sectionModel;

    /**
     * Provides common functions for all controllers working with
     * {@link ContentSection}s.
     */
    @Inject
    private ContentSectionsUi sectionsUi;

    /**
     * Provides several functions for working with {@link LocalizedString}
     */
    @Inject
    private GlobalizationHelper globalizationHelper;

    /**
     * Used to parse identifiers.
     */
    @Inject
    private IdentifierParser identifierParser;

    /**
     * Used to provide data for views with a named bean.
     */
    @Inject
    private Models models;

    /**
     * Used to retrieve and save parties.
     */
    @Inject
    private PartyRepository partyRepository;

    /**
     * Used to check permissions.
     */
    @Inject
    private PermissionChecker permissionChecker;

    /**
     * Used for complex operations on permissions.
     */
    @Inject
    private PermissionManager permissionManager;

    /**
     * Used for operations on roles.
     */
    @Inject
    private RoleManager roleManager;

    /**
     * Used to retrieve and save {@link Role}s.
     */
    @Inject
    private RoleRepository roleRepo;

    /**
     * Model for the selected role.
     */
    @Inject
    private SelectedRoleModel selectedRoleModel;

    /**
     * List all roles of the current content section.
     *
     * @param sectionIdentifierParam Identifier of the current content section.
     *
     * @return The template for the roles list.
     */
    @GET
    @Path("/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String listRoles(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);

        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!adminPermissionsChecker.canAdministerRoles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final List<Role> sectionRoles = section.getRoles();
        final Set<Role> otherRoles = roleRepo
            .findAll()
            .stream()
            .filter(role -> !sectionRoles.contains(role))
            .collect(Collectors.toSet());

        models.put(
            "roles",
            sectionRoles
                .stream()
                .map(this::buildRoleListModel)
                .collect(Collectors.toList())
        );
        models.put(
            "otherRoles",
            otherRoles
                .stream()
                .map(this::buildRoleListModel)
                .sorted(
                    (role1, role2) -> role1.getName().compareTo(role2.getName())
                ).collect(Collectors.toList())
        );

        return "org/librecms/ui/contentsection/configuration/roles.xhtml";
    }

    /**
     * Show the details view for a role.
     *
     * @param sectionIdentifierParam Identifier of the current content section.
     * @param roleName               The name of the role.
     *
     * @return The template for the details view of the role.
     */
    @GET
    @Path("/{roleName}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showRole(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("roleName") final String roleName
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!adminPermissionsChecker.canAdministerRoles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Role> result = section
            .getRoles()
            .stream()
            .filter(role -> roleName.equals(role.getName()))
            .findAny();

        if (!result.isPresent()) {
            return showRoleNotFound(section, roleName);
        }

        final Role role = result.get();
        selectedRoleModel.setDescription(
            role
                .getDescription()
                .getValues()
                .entrySet()
                .stream()
                .collect(
                    Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> entry.getValue()
                    )
                )
        );
        selectedRoleModel.setMembers(
            role
                .getMemberships()
                .stream()
                .map(this::buildRoleMembershipModel)
                .collect(Collectors.toList())
        );
        selectedRoleModel.setName(role.getName());
        selectedRoleModel.setPermissions(buildRolePermissions(role, section));
        final Set<Locale> descriptionLocales = role
            .getDescription()
            .getAvailableLocales();
        final List<Locale> availableLocales = globalizationHelper
            .getAvailableLocales();
        selectedRoleModel.setUnusedDescriptionLocales(
            availableLocales
                .stream()
                .filter(locale -> !descriptionLocales.contains(locale))
                .map(Locale::toString)
                .collect(Collectors.toList())
        );

        return "org/librecms/ui/contentsection/configuration/role.xhtml";
    }

    /**
     * Renames a role
     *
     * @param sectionIdentifierParam Identifier of the current content section.
     * @param roleName               The name of the role to rename.
     * @param newRoleName            The new name of the role.
     *
     * @return A redirect to the details view of the role.
     */
    @POST
    @Path("/{roleName}/@rename")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String renameRole(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("roleName") final String roleName,
        @FormParam("roleName") final String newRoleName
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!adminPermissionsChecker.canAdministerRoles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Role> result = section
            .getRoles()
            .stream()
            .filter(role -> roleName.equals(role.getName()))
            .findAny();

        if (!result.isPresent()) {
            return showRoleNotFound(section, roleName);
        }

        final Role role = result.get();
        role.setName(newRoleName);
        roleRepo.save(role);

        return String.format(
            "redirect:%s/configuration/roles/%s",
            sectionIdentifierParam,
            newRoleName
        );
    }

    /**
     * Update the permissions granted to the role for the current content
     * section.
     *
     * @param sectionIdentifierParam Identifier of the current content section.
     * @param roleName               The name of the role.
     * @param grantedPermissions     The permissions granted to the role for the
     *                               current content section. Permissions not
     *                               included here, but are granted are removed.
     *
     * @return A redirect to the details view of the role.
     */
    @POST
    @Path("/{roleName}/@permissions")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateRolePermissions(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("roleName") final String roleName,
        @FormParam("grantedPermissions") final List<String> grantedPermissions
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!adminPermissionsChecker.canAdministerRoles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Role> result = section
            .getRoles()
            .stream()
            .filter(role -> roleName.equals(role.getName()))
            .findAny();

        if (!result.isPresent()) {
            return showRoleNotFound(section, roleName);
        }

        final Role role = result.get();

        for (final String privilege : permissionManager.listDefiniedPrivileges(
            AdminPrivileges.class
        )) {
            if (grantedPermissions.contains(privilege)) {
                permissionManager.grantPrivilege(privilege, role, section);
            } else {
                permissionManager.revokePrivilege(privilege, role, section);
            }
        }

        final Folder documentsFolder = section.getRootDocumentsFolder();
        for (final String privilege : permissionManager.listDefiniedPrivileges(
            ItemPrivileges.class
        )) {
            if (grantedPermissions.contains(privilege)) {
                permissionManager.grantPrivilege(
                    privilege, role, documentsFolder
                );
            } else {
                permissionManager.revokePrivilege(
                    privilege, role, documentsFolder
                );
            }
        }

        final Folder assetsFolder = section.getRootAssetsFolder();
        for (final String privilege : permissionManager.listDefiniedPrivileges(
            AssetPrivileges.class
        )) {
            if (grantedPermissions.contains(privilege)) {
                permissionManager.grantPrivilege(
                    privilege, role, assetsFolder
                );
            } else {
                permissionManager.revokePrivilege(
                    privilege, role, assetsFolder
                );
            }
        }

        return String.format(
            "redirect:%s/configuration/roles/%s",
            sectionIdentifierParam,
            roleName
        );
    }

    /**
     * Updates the members of a role.
     *
     * @param sectionIdentifierParam Identifier of the current content section.
     * @param roleName               The name of the role.
     * @param roleMembersParam       The members of the role. Parties that are a
     *                               member of the role but not included this
     *                               list are removed from the members of the
     *                               role.
     *
     * @return A redirect to the details view of the role.
     */
    @POST
    @Path("/{roleName}/@members")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateRoleMembers(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("roleName") final String roleName,
        @FormParam("roleMembers") final List<String> roleMembersParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!adminPermissionsChecker.canAdministerRoles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Role> result = section
            .getRoles()
            .stream()
            .filter(role -> roleName.equals(role.getName()))
            .findAny();

        if (!result.isPresent()) {
            return showRoleNotFound(section, roleName);
        }

        final Role role = result.get();

        // Check for new members
        final List<String> newMemberNames = roleMembersParam
            .stream()
            .filter(memberName -> !hasMember(role, memberName))
            .collect(Collectors.toList());

        // Check for removed members
        final List<String> removedMemberNames = role
            .getMemberships()
            .stream()
            .map(membership -> membership.getMember().getName())
            .filter(memberName -> !roleMembersParam.contains(memberName))
            .collect(Collectors.toList());

        for (final String newMemberName : newMemberNames) {
            addNewMember(role, newMemberName);
        }

        for (final String removedMemberName : removedMemberNames) {
            removeMember(role, removedMemberName);
        }

        return String.format(
            "redirect:%s/configuration/roles/%s",
            sectionIdentifierParam,
            roleName
        );
    }

    /**
     * Adds a localized description to a role.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param roleName               The name of the role.
     * @param localeParam            The locale of the value to add.
     * @param value                  The value to add.
     *
     * @return A redirect to the details view of the role.
     */
    @POST
    @Path("/{roleName}/description/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("roleName") final String roleName,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!adminPermissionsChecker.canAdministerRoles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Role> result = section
            .getRoles()
            .stream()
            .filter(role -> roleName.equals(role.getName()))
            .findAny();

        if (!result.isPresent()) {
            return showRoleNotFound(section, roleName);
        }

        final Role role = result.get();
        final Locale locale = new Locale(localeParam);
        role.getDescription().addValue(locale, value);
        roleRepo.save(role);

        return String.format(
            "redirect:%s/configuration/roles/%s",
            sectionIdentifierParam,
            roleName
        );
    }

    /**
     * Updates a localized description of a role.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param roleName               The name of the role.
     * @param localeParam            The locale of the value to update.
     * @param value                  The updated value.
     *
     * @return A redirect to the details view of the role.
     */
    @POST
    @Path("/{roleName}/description/@edit/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("roleName") final String roleName,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!adminPermissionsChecker.canAdministerRoles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Role> result = section
            .getRoles()
            .stream()
            .filter(role -> roleName.equals(role.getName()))
            .findAny();

        if (!result.isPresent()) {
            return showRoleNotFound(section, roleName);
        }

        final Role role = result.get();
        final Locale locale = new Locale(localeParam);
        role.getDescription().addValue(locale, value);
        roleRepo.save(role);

        return String.format(
            "redirect:%s/configuration/roles/%s",
            sectionIdentifierParam,
            roleName
        );
    }

    /**
     * Removes a localized description from a role.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param roleName               The name of the role.
     * @param localeParam            The locale of the value to remove.
     *
     * @return A redirect to the details view of the role.
     */
    @POST
    @Path("/{roleName}/description/@remove/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("roleName") final String roleName,
        @PathParam("locale") final String localeParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!adminPermissionsChecker.canAdministerRoles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Role> result = section
            .getRoles()
            .stream()
            .filter(role -> roleName.equals(role.getName()))
            .findAny();

        if (!result.isPresent()) {
            return showRoleNotFound(section, roleName);
        }

        final Role role = result.get();
        final Locale locale = new Locale(localeParam);
        role.getDescription().removeValue(locale);

        return String.format(
            "redirect:%s/configuration/roles/%s",
            sectionIdentifierParam,
            roleName
        );
    }

    /**
     * Creates a new role for the current content section.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param roleName               The name of the new role.
     *
     * @return A redirect to the list of roles.
     */
    @POST
    @Path("/@new")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String createRole(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @FormParam("roleName") final String roleName
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!adminPermissionsChecker.canAdministerRoles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final List<String> errors = new ArrayList<>();
        if (roleName == null || roleName.matches("\\s*")) {
            errors.add(
                messages.get(
                    "contentsection.configuration.roles.errors.name_not_empty"
                )
            );
        }
        if (roleName != null && !roleName.matches("[a-zA-Z0-9_-]*")) {
            errors.add(
                messages.get(
                    "contentsection.configuration.roles.errors.name_invalid"
                )
            );
        }
        if (!errors.isEmpty()) {
            models.put("errors", errors);
            models.put("roleName", roleName);
            return listRoles(sectionIdentifierParam);
        }

        sectionManager.addRoleToContentSection(section, roleName);

        return String.format(
            "redirect:/%s/configuration/roles", sectionIdentifierParam
        );
    }

    /**
     * Adds some existing roles the content section.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param rolesToAdd             The existing roles to add to the current
     *                               content section.
     *
     * @return A redirect to the list of roles.
     */
    @POST
    @Path("/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addRole(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @FormParam("rolesToAdd") final List<String> rolesToAdd
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!adminPermissionsChecker.canAdministerRoles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        for (final String roleUuid : rolesToAdd) {
            roleRepo
                .findByUuid(roleUuid)
                .ifPresent(
                    role -> sectionManager.addRoleToContentSection(
                        role, section
                    )
                );
        }

        return String.format(
            "redirect:/%s/configuration/roles", sectionIdentifierParam
        );
    }

    /**
     * Removes a role from the current content section. The role is
     * <strong>not</strong> deleted.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param roleIdentifierParam    The identifier of the role to remove.
     *
     * @return A redirect to the list of roles.
     */
    @POST
    @Path("/{roleIdentifier}/@remove")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeRole(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("roleIdentifier") final String roleIdentifierParam
    ) {

        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!adminPermissionsChecker.canAdministerRoles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Role> roleResult = findRole(roleIdentifierParam);
        if (!roleResult.isPresent()) {
            return showRoleNotFound(section, roleIdentifierParam);
        }
        final Role role = roleResult.get();
        sectionManager.removeRoleFromContentSection(section, role);

        return String.format(
            "redirect:/%s/configuration/roles", sectionIdentifierParam
        );
    }

    /**
     * Removes a role from the current content section and deletes the role.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param roleIdentifierParam    The identifier of the role to delete.
     *
     * @return A redirect to the list of roles.
     */
    @POST
    @Path("/{roleIdentifier}/@delete")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String deleteRole(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("roleIdentifier") final String roleIdentifierParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!adminPermissionsChecker.canAdministerRoles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Role> roleResult = findRole(roleIdentifierParam);
        if (!roleResult.isPresent()) {
            return showRoleNotFound(section, roleIdentifierParam);
        }
        final Role role = roleResult.get();
        sectionManager.removeRoleFromContentSection(section, role);
        roleRepo.delete(role);

        return String.format(
            "redirect:/%s/configuration/roles", sectionIdentifierParam
        );
    }

    /**
     * Helper method for finding a role.
     *
     * @param roleIdentifierParam The identifier of the role.
     *
     * @return An {@link Optional} with the role, or an empty {@link Optional}
     *         if there is not role with the provided identifier.
     */
    private Optional<Role> findRole(final String roleIdentifierParam) {
        final Identifier roleIdentifier = identifierParser.parseIdentifier(
            roleIdentifierParam
        );
        switch (roleIdentifier.getType()) {
            case ID:
                return roleRepo.findById(
                    Long.parseLong(roleIdentifier.getIdentifier())
                );
            case UUID:
                return roleRepo.findByUuid(
                    roleIdentifier.getIdentifier()
                );
            default:
                return roleRepo.findByName(
                    roleIdentifier.getIdentifier()
                );
        }
    }

    /**
     * Shows the "role not found" error page.
     *
     * @param section  The current content section.
     * @param roleName The name of the role.
     *
     * @return The template of the "role not found" error page.
     */
    private String showRoleNotFound(
        final ContentSection section, final String roleName
    ) {
        models.put("sectionIdentifier", section.getLabel());
        models.put("roleName", roleName);
        return "org/librecms/ui/contentsection/configuration/role-not-found.xhtml";
    }

    /**
     * Helper method for building a {@link RoleListItemModel} for a role.
     *
     * @param role The role.
     *
     * @return A {@link RoleListItemModel} for the {@code role}.
     */
    private RoleListItemModel buildRoleListModel(final Role role) {
        final RoleListItemModel model = new RoleListItemModel();
        model.setRoleId(role.getRoleId());
        model.setUuid(role.getUuid());
        model.setName(role.getName());
        model.setDescription(
            globalizationHelper.getValueFromLocalizedString(
                role.getDescription()
            )
        );
        return model;
    }

    /**
     * Build a {@link RoleMembershipModel} for showing the members of a role.
     *
     * @param membership The membership from which the model is build.
     *
     * @return {@link RoleMembershipModel} for the {@code membership}.
     */
    private RoleMembershipModel buildRoleMembershipModel(
        final RoleMembership membership
    ) {
        final RoleMembershipModel model = new RoleMembershipModel();
        model.setMemberName(membership.getMember().getName());
        model.setMemberUuid(membership.getMember().getUuid());

        return model;
    }

    /**
     * Builds the {@link RoleSectionPermissionModel}s for a role and content
     * section.
     *
     * @param role    The role.
     * @param section The content section
     *
     * @return A list of {@link RoleSectionPermissionModel}s for the role and
     *         the content section.
     */
    private List<RoleSectionPermissionModel> buildRolePermissions(
        final Role role, final ContentSection section
    ) {

        final List<RoleSectionPermissionModel> adminPermissions
            = permissionManager
                .listDefiniedPrivileges(AdminPrivileges.class)
                .stream()
                .map(
                    privilege -> buildRoleSectionPermissionModel(
                        role, privilege, section
                    )
                ).collect(Collectors.toList());
        final List<RoleSectionPermissionModel> itemPermissions
            = permissionManager
                .listDefiniedPrivileges(ItemPrivileges.class)
                .stream()
                .map(
                    privilege -> buildRoleSectionPermissionModel(
                        role, privilege, section.getRootDocumentsFolder()
                    )
                ).collect(Collectors.toList());
        final List<RoleSectionPermissionModel> assetPermissions
            = permissionManager
                .listDefiniedPrivileges(AssetPrivileges.class)
                .stream()
                .map(
                    privilege -> buildRoleSectionPermissionModel(
                        role, privilege, section.getRootAssetsFolder()
                    )
                ).collect(Collectors.toList());
        final List<RoleSectionPermissionModel> permissions = new ArrayList<>();
        permissions.addAll(adminPermissions);
        permissions.addAll(itemPermissions);
        permissions.addAll(assetPermissions);
        return permissions;
    }

    /**
     * Builds a {@link RoleSectionPermissionModel} for a role, a privilege and a
     * content section.
     *
     * @param role      The role.
     * @param privilege The privilege.
     * @param section   The content section.
     *
     * @return A {@link RoleSectionPermissionModel} for the provided parameters.
     */
    private RoleSectionPermissionModel buildRoleSectionPermissionModel(
        final Role role, final String privilege, final ContentSection section
    ) {
        final RoleSectionPermissionModel model
            = new RoleSectionPermissionModel();
        model.setPrivilege(privilege);
        model.setGranted(
            permissionChecker.isPermitted(privilege, section, role)
        );
        return model;
    }

    /**
     * Builds a {@link RoleSectionPermissionModel} for a role, a privilege and a
     * folder.
     *
     * @param role      The role.
     * @param privilege The privilege.
     * @param folder    The folder
     *
     * @return A {@link RoleSectionPermissionModel} for the provided parameters.
     */
    private RoleSectionPermissionModel buildRoleSectionPermissionModel(
        final Role role, final String privilege, final Folder folder
    ) {
        final RoleSectionPermissionModel model
            = new RoleSectionPermissionModel();
        model.setPrivilege(privilege);
        model.setGranted(
            permissionChecker.isPermitted(privilege, folder, role)
        );
        return model;
    }

    /**
     * Checks if a role has a member.
     *
     * @param role       The role.
     * @param memberName The name of the member.
     *
     * @return {@code true} if the role has member with the provided name,
     *         {@code false} otherwise.
     */
    private boolean hasMember(final Role role, final String memberName) {
        return role
            .getMemberships()
            .stream()
            .map(membership -> membership.getMember().getName())
            .anyMatch(name -> name.equals(memberName));
    }

    /**
     * Adds a new member to a role.
     *
     * @param role          The role.
     * @param newMemberName The name of the new member.
     */
    private void addNewMember(final Role role, final String newMemberName) {
        final Optional<Party> result = partyRepository.findByName(
            newMemberName
        );
        if (result.isPresent()) {
            final Party party = result.get();
            roleManager.assignRoleToParty(role, party);
        }
    }

    /**
     * Removes a new member from a role.
     *
     * @param role              The role.
     * @param removedMemberName The name of the member to remove from the role.
     */
    private void removeMember(final Role role, final String removedMemberName) {
        final Optional<Party> result = partyRepository.findByName(
            removedMemberName
        );
        if (result.isPresent()) {
            final Party party = result.get();
            roleManager.removeRoleFromParty(role, party);
        }
    }

}
