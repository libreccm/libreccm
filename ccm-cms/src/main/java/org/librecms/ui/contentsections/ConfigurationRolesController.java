/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.core.CcmObject;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.Party;
import org.libreccm.security.PartyRepository;
import org.libreccm.security.Permission;
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
import org.librecms.contentsection.ContentSectionRepository;
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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/{sectionIdentifier}/configuration/roles")
public class ConfigurationRolesController {

    @Inject
    private CmsAdminMessages messages;

    @Inject
    private ContentSectionManager sectionManager;

    @Inject
    private ContentSectionModel sectionModel;

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private Models models;

    @Inject
    private PartyRepository partyRepository;

    @Inject
    private PermissionChecker permissionChecker;

    @Inject
    private PermissionManager permissionManager;

    @Inject
    private RoleManager roleManager;

    @Inject
    private RoleRepository roleRepo;

    @Inject
    private SelectedRoleModel selectedRoleModel;

    @GET
    @Path("/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String listRoles(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam
    ) {
        final Identifier sectionIdentifier = identifierParser.parseIdentifier(
            sectionIdentifierParam
        );

        final Optional<ContentSection> sectionResult;
        switch (sectionIdentifier.getType()) {
            case ID:
                sectionResult = sectionRepo.findById(
                    Long.parseLong(
                        sectionIdentifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                sectionResult = sectionRepo.findByUuid(
                    sectionIdentifier.getIdentifier()
                );
                break;
            default:
                sectionResult = sectionRepo.findByLabel(
                    sectionIdentifier.getIdentifier()
                );
                break;
        }

        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_ROLES, section
        )) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
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

    @GET
    @Path("/{roleName}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showRole(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("roleName") final String roleName
    ) {
        final Identifier sectionIdentifier = identifierParser.parseIdentifier(
            sectionIdentifierParam
        );

        final Optional<ContentSection> sectionResult;
        switch (sectionIdentifier.getType()) {
            case ID:
                sectionResult = sectionRepo.findById(
                    Long.parseLong(
                        sectionIdentifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                sectionResult = sectionRepo.findByUuid(
                    sectionIdentifier.getIdentifier()
                );
                break;
            default:
                sectionResult = sectionRepo.findByLabel(
                    sectionIdentifier.getIdentifier()
                );
                break;
        }

        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_ROLES, section
        )) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
        }

        final Optional<Role> result = section
            .getRoles()
            .stream()
            .filter(role -> roleName.equals(role.getName()))
            .findAny();

        if (!result.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            models.put("roleName", roleName);
            return "org/librecms/ui/contentsection/configuration/role-not-found.xhtml";
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

    @POST
    @Path("/{roleName}/@rename")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String renameRole(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("roleName") final String roleName,
        @FormParam("roleName") final String newRoleName
    ) {
        final Identifier sectionIdentifier = identifierParser.parseIdentifier(
            sectionIdentifierParam
        );

        final Optional<ContentSection> sectionResult;
        switch (sectionIdentifier.getType()) {
            case ID:
                sectionResult = sectionRepo.findById(
                    Long.parseLong(
                        sectionIdentifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                sectionResult = sectionRepo.findByUuid(
                    sectionIdentifier.getIdentifier()
                );
                break;
            default:
                sectionResult = sectionRepo.findByLabel(
                    sectionIdentifier.getIdentifier()
                );
                break;
        }

        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_ROLES, section
        )) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
        }

        final Optional<Role> result = section
            .getRoles()
            .stream()
            .filter(role -> roleName.equals(role.getName()))
            .findAny();

        if (!result.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            models.put("roleName", roleName);
            return "org/librecms/ui/contentsection/configuration/role-not-found.xhtml";
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

    @POST
    @Path("/{roleName}/@permissions")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateRolePermissions(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("roleName") final String roleName,
        @FormParam("grantedPermissions") final List<String> grantedPermissions
    ) {
        final Identifier sectionIdentifier = identifierParser.parseIdentifier(
            sectionIdentifierParam
        );

        final Optional<ContentSection> sectionResult;
        switch (sectionIdentifier.getType()) {
            case ID:
                sectionResult = sectionRepo.findById(
                    Long.parseLong(
                        sectionIdentifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                sectionResult = sectionRepo.findByUuid(
                    sectionIdentifier.getIdentifier()
                );
                break;
            default:
                sectionResult = sectionRepo.findByLabel(
                    sectionIdentifier.getIdentifier()
                );
                break;
        }

        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_ROLES, section
        )) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
        }

        final Optional<Role> result = section
            .getRoles()
            .stream()
            .filter(role -> roleName.equals(role.getName()))
            .findAny();

        if (!result.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            models.put("roleName", roleName);
            return "org/librecms/ui/contentsection/configuration/role-not-found.xhtml";
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

    @POST
    @Path("/{roleName}/@members")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateRoleMembers(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("roleName") final String roleName,
        @FormParam("roleMembers") final List<String> roleMembersParam
    ) {
        final Identifier sectionIdentifier = identifierParser.parseIdentifier(
            sectionIdentifierParam
        );

        final Optional<ContentSection> sectionResult;
        switch (sectionIdentifier.getType()) {
            case ID:
                sectionResult = sectionRepo.findById(
                    Long.parseLong(
                        sectionIdentifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                sectionResult = sectionRepo.findByUuid(
                    sectionIdentifier.getIdentifier()
                );
                break;
            default:
                sectionResult = sectionRepo.findByLabel(
                    sectionIdentifier.getIdentifier()
                );
                break;
        }

        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_ROLES, section
        )) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
        }

        final Optional<Role> result = section
            .getRoles()
            .stream()
            .filter(role -> roleName.equals(role.getName()))
            .findAny();

        if (!result.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            models.put("roleName", roleName);
            return "org/librecms/ui/contentsection/configuration/role-not-found.xhtml";
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
        final Identifier sectionIdentifier = identifierParser.parseIdentifier(
            sectionIdentifierParam
        );

        final Optional<ContentSection> sectionResult;
        switch (sectionIdentifier.getType()) {
            case ID:
                sectionResult = sectionRepo.findById(
                    Long.parseLong(
                        sectionIdentifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                sectionResult = sectionRepo.findByUuid(
                    sectionIdentifier.getIdentifier()
                );
                break;
            default:
                sectionResult = sectionRepo.findByLabel(
                    sectionIdentifier.getIdentifier()
                );
                break;
        }

        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_ROLES, section
        )) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
        }

        final Optional<Role> result = section
            .getRoles()
            .stream()
            .filter(role -> roleName.equals(role.getName()))
            .findAny();

        if (!result.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            models.put("roleName", roleName);
            return "org/librecms/ui/contentsection/configuration/role-not-found.xhtml";
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
        final Identifier sectionIdentifier = identifierParser.parseIdentifier(
            sectionIdentifierParam
        );

        final Optional<ContentSection> sectionResult;
        switch (sectionIdentifier.getType()) {
            case ID:
                sectionResult = sectionRepo.findById(
                    Long.parseLong(
                        sectionIdentifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                sectionResult = sectionRepo.findByUuid(
                    sectionIdentifier.getIdentifier()
                );
                break;
            default:
                sectionResult = sectionRepo.findByLabel(
                    sectionIdentifier.getIdentifier()
                );
                break;
        }

        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_ROLES, section
        )) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
        }

        final Optional<Role> result = section
            .getRoles()
            .stream()
            .filter(role -> roleName.equals(role.getName()))
            .findAny();

        if (!result.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            models.put("roleName", roleName);
            return "org/librecms/ui/contentsection/configuration/role-not-found.xhtml";
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

    @POST
    @Path("/{roleName}/description/@remove/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("roleName") final String roleName,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Identifier sectionIdentifier = identifierParser.parseIdentifier(
            sectionIdentifierParam
        );

        final Optional<ContentSection> sectionResult;
        switch (sectionIdentifier.getType()) {
            case ID:
                sectionResult = sectionRepo.findById(
                    Long.parseLong(
                        sectionIdentifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                sectionResult = sectionRepo.findByUuid(
                    sectionIdentifier.getIdentifier()
                );
                break;
            default:
                sectionResult = sectionRepo.findByLabel(
                    sectionIdentifier.getIdentifier()
                );
                break;
        }

        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_ROLES, section
        )) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
        }

        final Optional<Role> result = section
            .getRoles()
            .stream()
            .filter(role -> roleName.equals(role.getName()))
            .findAny();

        if (!result.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            models.put("roleName", roleName);
            return "org/librecms/ui/contentsection/configuration/role-not-found.xhtml";
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

    @POST
    @Path("/@new")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String createRole(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @FormParam("roleName") final String roleName
    ) {
        final Identifier sectionIdentifier = identifierParser.parseIdentifier(
            sectionIdentifierParam
        );

        final Optional<ContentSection> sectionResult;
        switch (sectionIdentifier.getType()) {
            case ID:
                sectionResult = sectionRepo.findById(
                    Long.parseLong(
                        sectionIdentifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                sectionResult = sectionRepo.findByUuid(
                    sectionIdentifier.getIdentifier()
                );
                break;
            default:
                sectionResult = sectionRepo.findByLabel(
                    sectionIdentifier.getIdentifier()
                );
                break;
        }

        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_ROLES, section
        )) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
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

    @POST
    @Path("/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addRole(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @FormParam("rolesToAdd") final List<String> rolesToAdd
    ) {
        final Identifier sectionIdentifier = identifierParser.parseIdentifier(
            sectionIdentifierParam
        );

        final Optional<ContentSection> sectionResult;
        switch (sectionIdentifier.getType()) {
            case ID:
                sectionResult = sectionRepo.findById(
                    Long.parseLong(
                        sectionIdentifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                sectionResult = sectionRepo.findByUuid(
                    sectionIdentifier.getIdentifier()
                );
                break;
            default:
                sectionResult = sectionRepo.findByLabel(
                    sectionIdentifier.getIdentifier()
                );
                break;
        }

        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_ROLES, section
        )) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
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

    @POST
    @Path("/{roleIdentifier}/@remove")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeRole(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("roleIdentifier") final String roleIdentifierParam
    ) {
        final Identifier sectionIdentifier = identifierParser.parseIdentifier(
            sectionIdentifierParam
        );

        final Optional<ContentSection> sectionResult;
        switch (sectionIdentifier.getType()) {
            case ID:
                sectionResult = sectionRepo.findById(
                    Long.parseLong(
                        sectionIdentifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                sectionResult = sectionRepo.findByUuid(
                    sectionIdentifier.getIdentifier()
                );
                break;
            default:
                sectionResult = sectionRepo.findByLabel(
                    sectionIdentifier.getIdentifier()
                );
                break;
        }

        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_ROLES, section
        )) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
        }

        final Identifier roleIdentifier = identifierParser.parseIdentifier(
            roleIdentifierParam
        );

        final Optional<Role> roleResult;
        switch (roleIdentifier.getType()) {
            case ID:
                roleResult = roleRepo.findById(
                    Long.parseLong(roleIdentifier.getIdentifier())
                );
                break;
            case UUID:
                roleResult = roleRepo.findByUuid(
                    roleIdentifier.getIdentifier()
                );
                break;
            default:
                roleResult = roleRepo.findByName(
                    roleIdentifier.getIdentifier()
                );
                break;
        }

        if (!roleResult.isPresent()) {
            models.put("roleIdentifier", roleIdentifierParam);
            return "org/librecms/ui/contentsection/configuration/role-not-found.xhtml";
        }
        final Role role = roleResult.get();
        sectionManager.removeRoleFromContentSection(section, role);

        return String.format(
            "redirect:/%s/configuration/roles", sectionIdentifierParam
        );
    }

    @POST
    @Path("/{roleIdentifier}/@delete")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String deleteRole(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("roleIdentifier") final String roleIdentifierParam
    ) {
        final Identifier sectionIdentifier = identifierParser.parseIdentifier(
            sectionIdentifierParam
        );

        final Optional<ContentSection> sectionResult;
        switch (sectionIdentifier.getType()) {
            case ID:
                sectionResult = sectionRepo.findById(
                    Long.parseLong(
                        sectionIdentifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                sectionResult = sectionRepo.findByUuid(
                    sectionIdentifier.getIdentifier()
                );
                break;
            default:
                sectionResult = sectionRepo.findByLabel(
                    sectionIdentifier.getIdentifier()
                );
                break;
        }

        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_ROLES, section
        )) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
        }

        final Identifier roleIdentifier = identifierParser.parseIdentifier(
            roleIdentifierParam
        );

        final Optional<Role> roleResult;
        switch (roleIdentifier.getType()) {
            case ID:
                roleResult = roleRepo.findById(
                    Long.parseLong(roleIdentifier.getIdentifier())
                );
                break;
            case UUID:
                roleResult = roleRepo.findByUuid(
                    roleIdentifier.getIdentifier()
                );
                break;
            default:
                roleResult = roleRepo.findByName(
                    roleIdentifier.getIdentifier()
                );
                break;
        }

        if (!roleResult.isPresent()) {
            models.put("roleIdentifier", roleIdentifierParam);
            return "org/librecms/ui/contentsection/configuration/role-not-found.xhtml";
        }
        final Role role = roleResult.get();
        sectionManager.removeRoleFromContentSection(section, role);
        roleRepo.delete(role);

        return String.format(
            "redirect:/%s/configuration/roles", sectionIdentifierParam
        );
    }

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

    private RoleMembershipModel buildRoleMembershipModel(
        final RoleMembership membership
    ) {
        final RoleMembershipModel model = new RoleMembershipModel();
        model.setMemberName(membership.getMember().getName());
        model.setMemberUuid(membership.getMember().getUuid());

        return model;
    }

    private boolean onlyContentSectionPermissions(
        final Permission permission, final ContentSection section
    ) {
        final Folder rootDocumentsFolder = section.getRootDocumentsFolder();
        final Folder rootAssetsFolder = section.getRootAssetsFolder();

        final CcmObject object = permission.getObject();
        return section.equals(object)
                   || rootDocumentsFolder.equals(object)
                   || rootAssetsFolder.equals(object);
    }

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

    private boolean hasMember(final Role role, final String memberName) {
        return role
            .getMemberships()
            .stream()
            .map(membership -> membership.getMember().getName())
            .anyMatch(name -> name.equals(memberName));
    }

    private void addNewMember(final Role role, final String newMemberName) {
        final Optional<Party> result = partyRepository.findByName(
            newMemberName
        );
        if (result.isPresent()) {
            final Party party = result.get();
            roleManager.assignRoleToParty(role, party);
        }
    }

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
