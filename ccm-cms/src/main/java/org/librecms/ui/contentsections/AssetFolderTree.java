/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Dependent
public class AssetFolderTree
    extends AbstractFolderTree<AssetFolderTreeNode, AssetPermissionsModel> {

    @Inject
    private AssetPermissions assetPermissions;

    @Override
    public AssetFolderTreeNode newFolderTreeNode() {
        return new AssetFolderTreeNode();
    }

    @Override
    public Folder getRootFolder(final ContentSection section) {
        return section.getRootAssetsFolder();
    }

    @Override
    public AssetPermissionsModel buildPermissionsModel(final Folder folder) {
        return assetPermissions.buildAssetPermissionsModel(folder);
    }

}
