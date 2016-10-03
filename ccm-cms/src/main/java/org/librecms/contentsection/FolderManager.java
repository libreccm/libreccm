/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.librecms.contentsection;

import com.arsdigita.kernel.KernelConfig;

import org.libreccm.categorization.CategoryManager;
import org.libreccm.configuration.ConfigurationManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Provides several methods for managing {@link Folder}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class FolderManager {

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private FolderRepository folderRepo;

    @Inject
    private CategoryManager categoryManager;

    @Inject
    private ContentItemManager itemManager;

    /**
     * Creates new folder as sub folder of the provided parent folder. The type
     * and the content section to which the folder belongs are the same as for
     * the provided parent folder.
     *
     * @param name   The name of the new folder.
     * @param parent The folder in which the new folder is generated.
     *
     * @return The new folder.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Folder createFolder(final String name, final Folder parent) {
        if (parent == null) {
            throw new IllegalArgumentException(
                "Can't create a folder without a parent folder.");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Can't create a folder with an empty name");
        }

        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);

        final Folder folder = new Folder();
        folder.setName(name);
        folder.setDisplayName(name);
        folder.getTitle().addValue(kernelConfig.getDefaultLocale(), name);
        folder.setSection(parent.getSection());
        folder.setType(parent.getType());
        folderRepo.save(folder);

        categoryManager.addSubCategoryToCategory(folder, parent);

        return folder;
    }

    /**
     * Deletes a empty, none-root folder.
     *
     * @param folder The folder to delete.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void deleteFolder(final Folder folder) {
        if (folder == null) {
            throw new IllegalArgumentException("Can't delete folder null");
        }

        if (!folder.getSubCategories().isEmpty()) {
            throw new IllegalArgumentException(String.format(
                "Can't delete folder \"%s\" because the folder is not empty",
                getFolderPath(folder, true)));
        }

        if (!folder.getObjects().isEmpty()) {
            throw new IllegalArgumentException(String.format(
                "Can't delete folder \"%s\" because the folder is not empty.",
                getFolderPath(folder)));
        }

        if (folder.getParentFolder() == null) {
            throw new IllegalArgumentException(
                "The folder to delete is a root folder can can't be deleted.");
        }

        folderRepo.delete(folder);
    }

    /**
     * Moves a none-root folder to another folder. If there any live
     * {@link ContentItem}s in the folder and its sub folder the folder can't be
     * moved. Also the target folder must belong to the same
     * {@link ContentSection} than the folder to move.
     *
     * @param folder The folder to move.
     * @param target The target folder.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void moveFolder(final Folder folder, final Folder target) {
        if (folder == null) {
            throw new IllegalArgumentException("Can't move folder null");
        }

        if (target == null) {
            throw new IllegalArgumentException(
                "Can't move a folder to folder null");
        }

        if (folder.getParentFolder() == null) {
            throw new IllegalArgumentException(String.format(
                "The folder \"%s\" to move is a root folder can can't be moved.",
                getFolderPath(folder)));
        }

        if (folder.equals(target)) {
            throw new IllegalArgumentException(
                "The folder to move and the target folder are the same folder.");
        }

        if (!folder.getSection().equals(target.getSection())) {
            throw new IllegalArgumentException(String.format(
                "Folders can't be moved between content section. The "
                    + "folder \"%s\" to move belongs to section \"%s\", "
                    + "the target folder \"%s\" belongs to section \"%s\".",
                getFolderPath(folder),
                folder.getSection().getDisplayName(),
                getFolderPath(target),
                target.getSection().getDisplayName()));
        }

        if (folder.getType() != target.getType()) {
            throw new IllegalArgumentException("The folder to move is a \"%s\","
                                                   + "but the target folder is a \"%s\" folder.");
        }

        if (liveItemsInFolder(folder)) {
            throw new IllegalArgumentException(String.format(
                "Can't move folder \"%s\" because some items in the folder or"
                    + "its sub folder are live.",
                getFolderPath(folder, true)));
        }

        final Folder source = folder.getParentFolder();
        categoryManager.removeSubCategoryFromCategory(folder, source);
        final boolean sameName = target.getSubCategories()
            .stream()
            .anyMatch(subCategory -> folder.getName().equals(subCategory
                .getName()));
        if (sameName) {
            final String name = String.format("%s_1", folder.getName());
            folder.setName(name);
            folder.setDisplayName(name);

            final KernelConfig kernelConfig = confManager.findConfiguration(
                KernelConfig.class);
            folder.getTitle().addValue(kernelConfig.getDefaultLocale(), name);
        }
        categoryManager.addSubCategoryToCategory(folder, target);
    }

    /**
     * Internal helper method for checking if there any live items in a given
     * folder or its sub folders.
     *
     * @param folder The folder to check for live items.
     *
     * @return {@code true} if there any live items in the folder or its sub
     *         folders, {@code false} if not.
     */
    private boolean liveItemsInFolder(final Folder folder) {
        final boolean liveItemsInFolder = folder.getObjects()
            .stream()
            .map(categorization -> categorization.getCategorizedObject())
            .filter(object -> object instanceof ContentItem)
            .map(object -> (ContentItem) object)
            .anyMatch(item -> itemManager.isLive(item));

        final boolean liveItemsInSubFolders = folder.getSubFolders()
            .stream()
            .anyMatch(subFolder -> liveItemsInFolder(subFolder));

        return liveItemsInFolder || liveItemsInSubFolders;
    }

    /**
     * Returns the path of folder.
     *
     * @param folder The folder.
     *
     * @return The path of the folder as a UNIX-like path, but without the
     *         content section as prefix.
     */
    public String getFolderPath(final Folder folder) {
        return getFolderPath(folder, false);
    }

    /**
     * Returns the path of folder.
     *
     * @param folder             The folder.
     * @param withContentSection Whether to include the content section in the
     *                           path.
     *
     * @return The path of the folder as a UNIX-like path, optionally with the
     *         content section the folder belongs to as prefix..
     */
    public String getFolderPath(final Folder folder,
                                final boolean withContentSection) {
        if (folder == null) {
            throw new IllegalArgumentException("Can't generate a path for null.");
        }
        
        final List<String> tokens = new ArrayList<>();

        tokens.add(folder.getName());
        Folder current = folder;
        while (current.getParentFolder() != null) {
            current = current.getParentFolder();
            tokens.add(current.getName());
        }

        Collections.reverse(tokens);
        final String path = String.join("/", tokens);

        if (withContentSection) {
            final String sectionName = folder.getSection().getDisplayName();
            return String.format("%s:/%s/", sectionName, path);
        } else {
            return String.format("/%s/", path);
        }
    }

}
