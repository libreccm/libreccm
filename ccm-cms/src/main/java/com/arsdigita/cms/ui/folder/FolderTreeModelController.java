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

import org.libreccm.categorization.Category;
import org.libreccm.core.UnexpectedErrorException;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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

//        final Folder current = (Folder) node.getElement();
        final Optional<Folder> folder = folderRepo
            .findById((long) node.getKey());
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
    public boolean hasTitleValue(final Folder ofFolder, 
                                 final Locale forLocale) {

        final Folder folder = folderRepo
        .findById(ofFolder.getObjectId())
        .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No folder with Id %d found.",
            ofFolder.getObjectId())));
        
        return folder.getTitle().hasValue(forLocale);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public String getTitleValue(final Folder ofFolder, 
                                final Locale forLocale) {
        
        final Folder folder = folderRepo
        .findById(ofFolder.getObjectId())
        .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No folder with Id %d found.",
            ofFolder.getObjectId())));
        
        return folder.getTitle().getValue(forLocale);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public boolean hasChildren(final TreeNode node) {
        return !getCurrentFolder(node).getSubCategories().isEmpty();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Folder> getChildren(final TreeNode node) {
        return getCurrentFolder(node).getSubFolders();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Long> findAncestorIds(final Folder folder) {
        Objects.requireNonNull(folder,
                               "Can't find ids of the ancestors of folder null.");

        final Optional<Folder> theFolder = folderRepo.findById(folder
            .getObjectId());
        if (!theFolder.isPresent()) {
            throw new UnexpectedErrorException(String.format(
                "The folder %s was not found in the database, but it should be there.",
                Objects.toString(folder)));
        }
        final List<Long> ancestorIds = new ArrayList<>();
        Category current = theFolder.get();
        while (current != null) {
            ancestorIds.add(current.getObjectId());
            current = current.getParentCategory();
        }

        return ancestorIds;
    }

}
