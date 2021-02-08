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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
abstract class AbstractCurrentUserPermissions {

    @Inject
    private PermissionChecker permissionChecker;

    @Inject
    private PermissionManager permissionManager;

    public List<GrantedPrivilegeModel> buildCurrentUserPermissions(
        final Folder folder
    ) {
        return permissionManager
            .listDefiniedPrivileges(getPrivilegesClass())
            .stream()
            .map(privilege -> buildCurrentUserPermission(folder, privilege))
            .collect(Collectors.toList());
    }

    protected abstract Class<?> getPrivilegesClass();

    private GrantedPrivilegeModel buildCurrentUserPermission(
        final Folder folder, final String privilege
    ) {
        final GrantedPrivilegeModel model = new GrantedPrivilegeModel();
        model.setPrivilege(privilege);
        model.setGranted(permissionChecker.isPermitted(privilege, folder));
        return model;
    }

}
