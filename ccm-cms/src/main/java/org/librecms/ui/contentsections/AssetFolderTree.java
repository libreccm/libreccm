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

import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * A model bean for the tree of asset folder of a content section.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Dependent
public class AssetFolderTree
    extends AbstractFolderTree<AssetFolderTreeNode, AssetPermissionsModel> {

    /**
     * {@link AssetPermissionsModelProvider} used to create the
     * {@link AssetPermissionsModel} for the folder and the currentu user.
     */
    @Inject
    private AssetPermissionsModelProvider assetPermissions;

    
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
