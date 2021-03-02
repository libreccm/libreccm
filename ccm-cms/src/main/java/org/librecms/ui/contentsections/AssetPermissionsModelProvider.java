/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import org.librecms.contentsection.Asset;
import org.librecms.contentsection.Folder;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Dependent
class AssetPermissionsModelProvider {

    @Inject
    private AssetPermissionsChecker permissionChecker;

    public AssetPermissionsModel buildAssetPermissionsModel(
        final Folder folder
    ) {
        final AssetPermissionsModel model = new AssetPermissionsModel();
        model.setGrantedCreateNew(
            permissionChecker.canCreateAssets(folder)
        );
        model.setGrantedDelete(
            permissionChecker.canDeleteAssets(folder)
        );
        model.setGrantedEdit(
            permissionChecker.canEditAssets(folder)
        );
        model.setGrantedUse(
            permissionChecker.canUseAssets(folder)
        );
        model.setGrantedView(
            permissionChecker.canViewAssets(folder)
        );

        return model;
    }

    public AssetPermissionsModel buildAssetPermissionsModel(
        final Asset asset
    ) {
        final AssetPermissionsModel model = new AssetPermissionsModel();
        model.setGrantedCreateNew(
            permissionChecker.canCreateAssets(asset)
        );
        model.setGrantedDelete(
            permissionChecker.canDeleteAssets(asset)
        );
        model.setGrantedEdit(
            permissionChecker.canEditAssets(asset)
        );
        model.setGrantedUse(
            permissionChecker.canUseAssets(asset)
        );
        model.setGrantedView(
            permissionChecker.canViewAssets(asset)
        );

        return model;
    }

}
