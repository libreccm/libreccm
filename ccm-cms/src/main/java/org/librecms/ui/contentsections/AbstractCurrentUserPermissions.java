/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import org.libreccm.security.PermissionChecker;
import org.libreccm.security.PermissionManager;
import org.librecms.contentsection.Folder;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * A helper class to build {@link GrantedPrivilegeModel} for a specific category
 * of privileges granted to the current user.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
abstract class AbstractCurrentUserPermissions {

    /**
     * CDI managed {@link PermissionChecker} instance used to check the
     * permissions of the current user.
     */
    @Inject
    private PermissionChecker permissionChecker;

    /**
     * CDI managed {@link PermissionManager} instance used to enumerate the
     * permission constants in the class provided by
     * {@link #getPrivilegesClass()}.
     */
    @Inject
    private PermissionManager permissionManager;

    /**
     * Builds a list of {@link GrantedPrivilegeModel}s for the current user and
     * the privileges returned by {@link #getPrivilegesClass() }.
     *
     * @param folder The folder for which the permissions are checked.
     *
     * @return A list of {@link GrantedPrivilegeModel} instances.
     */
    public List<GrantedPrivilegeModel> buildCurrentUserPermissions(
        final Folder folder
    ) {
        return permissionManager
            .listDefiniedPrivileges(getPrivilegesClass())
            .stream()
            .map(privilege -> buildCurrentUserPermission(folder, privilege))
            .collect(Collectors.toList());
    }

    /**
     * Provides the class with privilege constants to use.
     *
     * @return The class with privilege constants to use.
     */
    protected abstract Class<?> getPrivilegesClass();

    /**
     * Build a {@link GrantedPrivilegeModel} for a folder and a privilege.
     *
     * @param folder    The folder to use.
     * @param privilege The privilege.
     *
     * @return A {@link GrantedPrivilegeModel} for the current user, the
     *         provided folder and the provided privilege.
     */
    private GrantedPrivilegeModel buildCurrentUserPermission(
        final Folder folder, final String privilege
    ) {
        final GrantedPrivilegeModel model = new GrantedPrivilegeModel();
        model.setPrivilege(privilege);
        model.setGranted(permissionChecker.isPermitted(privilege, folder));
        return model;
    }

}
