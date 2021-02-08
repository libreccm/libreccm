/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.privileges.AssetPrivileges;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Dependent
class AssetPermissions {

    @Inject
    private PermissionChecker permissionChecker;

    public AssetPermissionsModel buildAssetPermissionsModel(
        final Folder folder
    ) {
        final AssetPermissionsModel model = new AssetPermissionsModel();
        model.setGrantedCreateNew(
            permissionChecker.isPermitted(AssetPrivileges.CREATE_NEW, folder)
        );
        model.setGrantedDelete(
            permissionChecker.isPermitted(AssetPrivileges.DELETE, folder)
        );
        model.setGrantedEdit(
            permissionChecker.isPermitted(AssetPrivileges.EDIT, folder)
        );
        model.setGrantedUse(
            permissionChecker.isPermitted(AssetPrivileges.USE, folder)
        );
        model.setGrantedView(
            permissionChecker.isPermitted(AssetPrivileges.VIEW, folder)
        );

        return model;
    }

    public AssetPermissionsModel buildAssetPermissionsModel(
        final Asset asset
    ) {
        final AssetPermissionsModel model = new AssetPermissionsModel();
        model.setGrantedCreateNew(
            permissionChecker.isPermitted(AssetPrivileges.CREATE_NEW, asset)
        );
        model.setGrantedDelete(
            permissionChecker.isPermitted(AssetPrivileges.DELETE, asset)
        );
        model.setGrantedEdit(
            permissionChecker.isPermitted(AssetPrivileges.EDIT, asset)
        );
        model.setGrantedUse(
            permissionChecker.isPermitted(AssetPrivileges.USE, asset)
        );
        model.setGrantedView(
            permissionChecker.isPermitted(AssetPrivileges.VIEW, asset)
        );

        return model;
    }

}
