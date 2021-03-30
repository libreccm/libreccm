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
 * A utility CDI bean for building the {@link AssetPermissionsModel} for a asset
 * {@link Folder} or an {@link Asset}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Dependent
public class AssetPermissionsModelProvider {

    /**
     * The {@link AssetPermissionsChecker} instance to use.
     */
    @Inject
    private AssetPermissionsChecker permissionChecker;

    /**
     * Builds an {@link AssetPermissionsModel} for the provided assets
     * {@link Folder}
     *
     * @param folder The {@link Folder} for which the
     *               {@link AssetPermissionsModel} is build.
     *
     * @return The {@link AssetFolderModel} for the {@link Folder}.
     */
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

    /**
     * Builds an {@link AssetPermissionsModel} for the provided assets
     * {@link Asset}
     *
     * @param folder The {@link Asset} for which the
     *               {@link AssetPermissionsModel} is build.
     *
     * @return The {@link AssetFolderModel} for the {@link Asset}.
     */
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
