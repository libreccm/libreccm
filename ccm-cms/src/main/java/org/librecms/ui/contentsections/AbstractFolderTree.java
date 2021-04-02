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
import org.librecms.contentsection.FolderManager;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Abstract base class for building folder trees.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
abstract class AbstractFolderTree<T extends FolderTreeNode<T, P>, P extends PermissionsModel> {

    /**
     * {@link FolderManager} instance to work the the folders.
     */
    @Inject
    private FolderManager folderManager;

    /**
     * Builds the subfolder tree for a folder and the content section.
     *
     * @param section       The content section to which the folder belongs.
     * @param currentFolder The current folder.
     *
     * @return A list of {@link FolderTreeNode}s representing the folder tree.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<T> buildFolderTree(
        final ContentSection section, final Folder currentFolder
    ) {
        final Folder root = getRootFolder(section);
        final String currentFolderPath = folderManager
            .getFolderPath(currentFolder)
            .substring(
                folderManager
                    .getFolderPath(section.getRootDocumentsFolder())
                    .length() - 1
            );

        return root
            .getSubFolders()
            .stream()
            .sorted(this::compareFolders)
            .map(
                folder -> buildFolderTreeNode(
                    section, currentFolderPath, folder
                )
            ).collect(Collectors.toList());
    }

    /**
     * Creates a new {@link FolderTreeNode}.
     *
     * @return A new {@link FolderTreeNode}.
     */
    protected abstract T newFolderTreeNode();

    /**
     * Retrieves the root folder of the current content section.
     *
     * @param section The content section.
     *
     * @return The root folder of the current content section.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected abstract Folder getRootFolder(final ContentSection section);

    /**
     * Builds the permissions model for a folder.
     *
     * @param folder The folder.
     *
     * @return The permissions model.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected abstract P buildPermissionsModel(final Folder folder);

    /**
     * Helper method for building a folder tree.
     *
     * @param section           The content section to which the folder belongs.
     * @param currentFolderPath The path of the current folder.
     * @param folder            The folder for which the node is build.
     *
     * @return A {@link FolderTreeNode} for the provided {@code folder}.
     */
    private T buildFolderTreeNode(
        final ContentSection section,
        final String currentFolderPath,
        final Folder folder
    ) {
        final String folderPath = folderManager
            .getFolderPath(folder)
            .substring(
                folderManager
                    .getFolderPath(section.getRootDocumentsFolder())
                    .length() - 1
            );
        final T node = newFolderTreeNode();
        node.setFolderId(folder.getObjectId());
        node.setUuid(folder.getUuid());
        node.setName(folder.getName());
        node.setPath(folderPath);
        node.setOpen(currentFolderPath.startsWith(folderPath));
        node.setSelected(currentFolderPath.equals(folderPath));
        node.setPermissions(buildPermissionsModel(folder));

        node.setSubFolders(
            folder
                .getSubFolders()
                .stream()
                .sorted(this::compareFolders)
                .map(
                    subFolder -> buildFolderTreeNode(
                        section, currentFolderPath, subFolder
                    )
                )
                .collect(Collectors.toList())
        );

        return node;
    }

    /**
     * Compare function for ordering folder by their name.
     * 
     * @param folder1 First folder to compare.
     * @param folder2 Second folder to compare.
     * @return The result of comparing the names of the two folders.
     */
    private int compareFolders(final Folder folder1, final Folder folder2) {
        return folder1.getName().compareTo(folder2.getName());
    }

}
