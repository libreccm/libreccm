/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
abstract class AbstractFolderTree<T extends FolderTreeNode<T, P>, P extends PermissionsModel> {

    @Inject
    private FolderManager folderManager;

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

    protected abstract T newFolderTreeNode();

    @Transactional(Transactional.TxType.REQUIRED)
    protected abstract Folder getRootFolder(final ContentSection section);

    @Transactional(Transactional.TxType.REQUIRED)
    protected abstract P buildPermissionsModel(final Folder folder);

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

    private int compareFolders(final Folder folder1, final Folder folder2) {
        return folder1.getName().compareTo(folder2.getName());
    }

}
