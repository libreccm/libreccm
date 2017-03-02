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

import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.UnexpectedErrorException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * An enum describing if a folder can be deleted or not and why.
     *
     * @see #folderIsDeletable(org.librecms.contentsection.Folder)
     */
    public enum FolderIsDeletable {
        /**
         * Folder can be deleted.
         */
        YES,
        /**
         * Folder can't be deleted because is has sub categories.
         */
        HAS_SUBCATEGORIES,
        /**
         * Folder can't be deleted because the folder is not empty.
         */
        IS_NOT_EMPTY,
        /**
         * Folder can't be deleted because the folder is a root folder.
         */
        IS_ROOT_FOLDER
    }

    /**
     * Describes if a folder is movable to another folder or not and why.
     */
    public enum FolderIsMovable {
        /**
         * The folder can be moved to the specified target folder.
         */
        YES,
        /**
         * The folder is a root folder. Root folders can't be moved.
         */
        IS_ROOT_FOLDER,
        /**
         * The folder to move and the target folder are the same folder.
         */
        SAME_FOLDER,
        /**
         * The folder to move and the target folder belong the different content
         * sections.
         */
        DIFFERENT_SECTIONS,
        /**
         * The folder to move and the target folder have different types.
         */
        DIFFERENT_TYPES,
        /**
         * The folder to move contains live items.
         */
        HAS_LIVE_ITEMS
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<Folder> getParentFolder(final Folder folder) {
        Objects.requireNonNull(folder);
        final Optional<Folder> theFolder = folderRepo.findById(folder.
                getObjectId());
        if (!theFolder.isPresent()) {
            throw new UnexpectedErrorException(String.format(
                    "The folder %s should be in the database but is not.",
                    Objects.toString(folder)));
        }
        final Category parentCategory = theFolder.get().getParentCategory();
        if (parentCategory == null) {
            return Optional.empty();
        } else {
            return folderRepo.findById(parentCategory.getObjectId());
        }
    }

    /**
     * Creates new folder as sub folder of the provided parent folder. The type
     * and the content section to which the folder belongs are the same as for
     * the provided parent folder.
     *
     * @param name The name of the new folder.
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
    
    public FolderIsDeletable folderIsDeletable(final Folder folder) {
        if (folder == null) {
            throw new IllegalArgumentException(
                    "Can't check if null is deletable.");
        }
        
        if (!folder.getSubCategories().isEmpty()) {
            return FolderIsDeletable.HAS_SUBCATEGORIES;
        }
        
        if (!folder.getObjects().isEmpty()) {
            return FolderIsDeletable.IS_NOT_EMPTY;
        }
        
        if (!getParentFolder(folder).isPresent()) {
            return FolderIsDeletable.IS_ROOT_FOLDER;
        }
        
        return FolderIsDeletable.YES;
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
        
        final FolderIsDeletable status = folderIsDeletable(folder);
        switch (status) {
            case YES:
                folderRepo.delete(folder);
                break;
            case HAS_SUBCATEGORIES:
                throw new IllegalArgumentException(String.format(
                        "Can't delete folder \"%s\" because the folder is not empty",
                        getFolderPath(folder, true)));
            case IS_NOT_EMPTY:
                throw new IllegalArgumentException(String.format(
                        "Can't delete folder \"%s\" because the folder is not empty.",
                        getFolderPath(folder)));
            case IS_ROOT_FOLDER:
                throw new IllegalArgumentException(
                        "The folder to delete is a root folder can can't be deleted.");
            default:
                throw new IllegalArgumentException(String.format(
                        "Unexpected return value from #folderIsDeletable: "
                                + "\"%s\".",
                        status.toString()));
        }
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
        
        Objects.requireNonNull(folder, "Can't move folder null");
        Objects.requireNonNull(target, "Can't move a folder to folder null");
        
        final FolderIsMovable status = folderIsMovable(folder, target);
        switch (status) {
            case YES: {
                final Folder source = getParentFolder(folder).get();
                categoryManager.removeSubCategoryFromCategory(folder, source);
                final boolean sameName = target.getSubCategories()
                        .stream()
                        .anyMatch(subCategory -> folder.getName().equals(
                        subCategory.getName()));
                if (sameName) {
                    final String name = String.format("%s_1", folder.getName());
                    folder.setName(name);
                    folder.setDisplayName(name);
                    
                    final KernelConfig kernelConfig = confManager.
                            findConfiguration(
                                    KernelConfig.class);
                    folder.getTitle().addValue(kernelConfig.getDefaultLocale(),
                                               name);
                }
                categoryManager.addSubCategoryToCategory(folder, target);
                break;
            }
            case IS_ROOT_FOLDER:
                throw new IllegalArgumentException(String.format(
                        "The folder \"%s\" to move is a root folder can can't "
                                + "be moved.",
                        getFolderPath(folder)));
            case SAME_FOLDER:
                throw new IllegalArgumentException(
                        "The folder to move and the target folder are the same "
                                + "folder.");
            case DIFFERENT_SECTIONS:
                throw new IllegalArgumentException(String.format(
                        "Folders can't be moved between content section. The "
                                + "folder \"%s\" to move belongs to section "
                                + "\"%s\", the target folder \"%s\" belongs to "
                                + "section \"%s\".",
                        getFolderPath(folder),
                        folder.getSection().getDisplayName(),
                        getFolderPath(target),
                        target.getSection().getDisplayName()));
            case DIFFERENT_TYPES:
                throw new IllegalArgumentException(
                        "The folder to move is a \"%s\","
                                + "but the target folder is a \"%s\" folder.");
            case HAS_LIVE_ITEMS:
                throw new IllegalArgumentException(String.format(
                        "Can't move folder \"%s\" because some items in the "
                                + "folder or its sub folder are live.",
                        getFolderPath(folder, true)));
            default:
                throw new IllegalArgumentException(String.format(
                        "Unexpected return value from #folderIsMovable: %s",
                        status.toString()));
        }

//        if (folder.getParentFolder()
//                    == null) {
//            throw new IllegalArgumentException(String.format(
//                    "The folder \"%s\" to move is a root folder can can't be moved.",
//                    getFolderPath(folder)));
//        }
//
//        if (folder.equals(target)) {
//            throw new IllegalArgumentException(
//                    "The folder to move and the target folder are the same folder.");
//        }
//
//        if (!folder.getSection()
//                .equals(target.getSection())) {
//            throw new IllegalArgumentException(String.format(
//                    "Folders can't be moved between content section. The "
//                            + "folder \"%s\" to move belongs to section \"%s\", "
//                    + "the target folder \"%s\" belongs to section \"%s\".",
//                    getFolderPath(folder),
//                    folder.getSection().getDisplayName(),
//                    getFolderPath(target),
//                    target.getSection().getDisplayName()));
//        }
//
//        if (folder.getType()
//                    != target.getType()) {
//            throw new IllegalArgumentException("The folder to move is a \"%s\","
//                                                       + "but the target folder is a \"%s\" folder.");
//        }
//
//        if (liveItemsInFolder(folder)) {
//            throw new IllegalArgumentException(String.format(
//                    "Can't move folder \"%s\" because some items in the folder or"
//                    + "its sub folder are live.",
//                    getFolderPath(folder, true)));
//        }
//
//        final Folder source = folder.getParentFolder();
//
//        categoryManager.removeSubCategoryFromCategory(folder, source);
//        final boolean sameName = target.getSubCategories()
//                .stream()
//                .anyMatch(subCategory -> folder.getName().equals(subCategory
//                        .getName()));
//        if (sameName) {
//            final String name = String.format("%s_1", folder.getName());
//            folder.setName(name);
//            folder.setDisplayName(name);
//
//            final KernelConfig kernelConfig = confManager.findConfiguration(
//                    KernelConfig.class);
//            folder.getTitle().addValue(kernelConfig.getDefaultLocale(), name);
//        }
//
//        categoryManager.addSubCategoryToCategory(folder, target);
    }
    
    public FolderIsMovable folderIsMovable(final Folder folder,
                                           final Folder target) {
        if (folder == null) {
            throw new IllegalArgumentException("Can't check if null is movable.");
        }
        
        if (target == null) {
            throw new IllegalArgumentException(
                    "Can't check if a server can be moved to null.");
        }
        
        if (!getParentFolder(folder).isPresent()) {
            return FolderIsMovable.IS_ROOT_FOLDER;
        }
        
        if (folder.equals(target)) {
            return FolderIsMovable.SAME_FOLDER;
        }
        
        if (!folder.getSection().equals(target.getSection())) {
            return FolderIsMovable.DIFFERENT_SECTIONS;
        }
        
        if (folder.getType() != target.getType()) {
            return FolderIsMovable.DIFFERENT_TYPES;
        }
        
        if (liveItemsInFolder(folder)) {
            return FolderIsMovable.HAS_LIVE_ITEMS;
        }
        
        return FolderIsMovable.YES;
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    public void copyFolder(final Folder folder, final Folder target) {
        
        Objects.requireNonNull(folder, "Can't move null to a folder.");
        Objects.requireNonNull(target, "Can't move a folder to null.");
        
        final Folder copy = createFolder(folder.getName(), target);
        final List<ContentItem> items = folder.getObjects()
                .stream()
                .map(categorization -> categorization.getCategorizedObject())
                .filter(object -> object instanceof ContentItem)
                .map(object -> (ContentItem) object)
                .collect(Collectors.toList());
        for (final ContentItem item : items) {
            itemManager.copy(item, target);
        }
        
        for(final Folder subFolder : folder.getSubFolders()) {
            copyFolder(subFolder, copy);
        }
    }

    /**
     * Internal helper method for checking if there any live items in a given
     * folder or its sub folders.
     *
     * @param folder The folder to check for live items.
     *
     * @return {@code true} if there any live items in the folder or its sub
     * folders, {@code false} if not.
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
     * content section as prefix.
     */
    public String getFolderPath(final Folder folder) {
        return getFolderPath(folder, false);
    }

    /**
     * Returns the path of folder.
     *
     * @param folder The folder.
     * @param withContentSection Whether to include the content section in the
     * path.
     *
     * @return The path of the folder as a UNIX-like path, optionally with the
     * content section the folder belongs to as prefix..
     */
    public String getFolderPath(final Folder folder,
                                final boolean withContentSection) {
        if (folder == null) {
            throw new IllegalArgumentException("Can't generate a path for null.");
        }
        
        final List<String> tokens = new ArrayList<>();
        
        tokens.add(folder.getName());
        Folder current = folder;
        while (getParentFolder(current).isPresent()) {
            current = getParentFolder(current).get();
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

    /**
     * Creates list with a parent folders of the provided folder.
     *
     * @param folder The folder.
     *
     * @return
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Folder> getParentFolders(final Folder folder) {
        
        if (folder == null) {
            throw new IllegalArgumentException(
                    "Can't create a list of parent folder for folder null.");
        }
        
        final List<Folder> folders = new ArrayList<>();
        if (getParentFolder(folder).isPresent()) {
            Optional<Folder> currentFolder = getParentFolder(folder);
            while (currentFolder.isPresent()) {
                folders.add(currentFolder.get());
                currentFolder = getParentFolder(currentFolder.get());
            }
        }
        
        Collections.reverse(folders);
        return folders;
    }
    
}
