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
import org.libreccm.security.Permission;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.Role;
import org.libreccm.security.RoleMembership;

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
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.privileges.AdminPrivileges;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/{sectionIdentifier}/configuration/roles")
public class ConfigurationRolesController {

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
    private PermissionChecker permissionChecker;

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

        final List<RoleListItemModel> roles = section
            .getRoles()
            .stream()
            .map(this::buildRoleListModel)
            .collect(Collectors.toList());
        models.put("roles", roles);

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
        selectedRoleModel.setPermissions(
            role
                .getPermissions()
                .stream()
                .filter(
                    permission -> onlyContentSectionPermissions(
                        permission, section
                    )
                )
                .map(permission -> permission.getGrantedPrivilege())
                .collect(Collectors.toList())
        );
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

}
