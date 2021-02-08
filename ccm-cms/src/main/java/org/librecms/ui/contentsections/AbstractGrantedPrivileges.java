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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
abstract class AbstractGrantedPrivileges {
    
    @Inject
    private PermissionChecker permissionChecker;
    
    @Inject
    private PermissionManager permissionManager;
    
    public List<PrivilegesGrantedToRoleModel> buildPermissionsMatrix(
        final ContentSection section, final Folder folder
    ) {
        return section
            .getRoles()
            .stream()
            .map(role -> buildPrivilegesGrantedToRoleModel(role, folder))
            .collect(Collectors.toList());
    }
    
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

    protected abstract Class<?> getPrivilegesClass();
    
}
