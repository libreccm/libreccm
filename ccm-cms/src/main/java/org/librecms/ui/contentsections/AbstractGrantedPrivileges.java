/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import org.libreccm.security.Permission;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.PermissionManager;
import org.libreccm.security.Role;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * Abstract builder for {@link PrivilegesGrantedToRoleModel} instances.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
abstract class AbstractGrantedPrivileges {

    /**
     * {@link PermissionChecker} instance used.
     */
    @Inject
    private PermissionChecker permissionChecker;

    /**
     * {@link PermissionManager} instance used.
     */
    @Inject
    private PermissionManager permissionManager;

    /**
     * Build a permissions matrix for the provided folder.
     *
     * @param section The content section to which the folder belongs.
     * @param folder  The folder.
     *
     * @return A list of {@link PrivilegesGrantedToRoleModel} (the rows and
     *         columns of the matrix).
     */
    public List<PrivilegesGrantedToRoleModel> buildPermissionsMatrix(
        final ContentSection section, final Folder folder
    ) {
        return section
            .getRoles()
            .stream()
            .map(role -> buildPrivilegesGrantedToRoleModel(role, folder))
            .collect(Collectors.toList());
    }

    /**
     * Provides the privileges class to use.
     *
     * @return The class containing the constants for the privileges to use.
     */
    protected abstract Class<?> getPrivilegesClass();

    /**
     * Helper method for building a {@link PrivilegesGrantedToRoleModel}.
     *
     * @param role   The role for which the model is build.
     * @param folder The folder for which the model is build.
     *
     * @return A {@link PrivilegesGrantedToRoleModel} for the {@code role} and
     *         the {@code folder}.
     */
    private PrivilegesGrantedToRoleModel buildPrivilegesGrantedToRoleModel(
        final Role role, final Folder folder
    ) {
        final List<GrantedPrivilegeModel> grantedPrivilges = permissionManager
            .listDefiniedPrivileges(getPrivilegesClass())
            .stream()
            .map(
                privilege -> buildGrantedPrivilegeModel(
                    role,
                    folder,
                    privilege,
                    permissionManager.findPermissionsForRoleAndObject(
                        role, folder
                    )
                )
            )
            .collect(Collectors.toList());

        final PrivilegesGrantedToRoleModel model
            = new PrivilegesGrantedToRoleModel();
        model.setGrantedPrivileges(grantedPrivilges);
        model.setGrantee(role.getName());

        return model;
    }

    /**
     * Helper method for building a {@link GrantedPrivilegeModel}.
     *
     * @param role        The role for which the model is build.
     * @param folder      The folder for which the model is build.
     * @param privilege   The privilege for which the model is build.
     * @param permissions The permissions to use for building the model.
     *
     * @return A {@link GrantedPrivilegeModel} for the provided parameters.
     */
    private GrantedPrivilegeModel buildGrantedPrivilegeModel(
        final Role role,
        final Folder folder,
        final String privilege,
        final List<Permission> permissions
    ) {
        final GrantedPrivilegeModel model = new GrantedPrivilegeModel();
        model.setGranted(permissionChecker.isPermitted(privilege, folder, role));
        model.setInherited(
            model.isGranted()
                && permissions
                .stream()
                .anyMatch(
                    permission
                    -> permission.getGrantee().equals(role)
                           && permission.getGrantedPrivilege().equals(privilege)
                           && permission.getObject().equals(folder)
                           && permission.getInheritedFrom() != null
                )
        );
        model.setPrivilege(privilege);

        return model;
    }

}
