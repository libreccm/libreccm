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
     * @param asset The {@link Asset} for which the
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
            permissionChecker.canEditAsset(asset)
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
