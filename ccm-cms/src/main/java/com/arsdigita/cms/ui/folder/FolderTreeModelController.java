/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.folder;

import com.arsdigita.bebop.tree.TreeNode;

import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderRepository;

import java.util.Iterator;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class FolderTreeModelController {

    @Inject
    private FolderRepository folderRepo;

    private Folder getCurrentFolder(final TreeNode node) {
        if (node == null) {
            throw new IllegalArgumentException(
                "Can't get current folder from null.");
        }

        final Folder current = (Folder) node.getElement();

        final Optional<Folder> folder = folderRepo.findById(current
            .getObjectId());
        if (folder.isPresent()) {
            return folder.get();
        } else {
            throw new IllegalArgumentException(String.format(
                "The provided TreeNode contains a folder (%s) which is not in "
                    + "the database.",
                folder.toString()));
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public boolean hasChildren(final TreeNode node) {
        return getCurrentFolder(node).getSubCategories().isEmpty();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Iterator<Folder> getChildren(final TreeNode node) {
        return getCurrentFolder(node).getSubFolders().iterator();
    }

}
