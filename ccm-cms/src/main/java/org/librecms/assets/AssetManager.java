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
package org.librecms.assets;

import com.arsdigita.util.UncheckedWrapperException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.Categorization;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.core.CoreConstants;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.librecms.CmsConstants;
import org.librecms.attachments.AttachmentList;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderManager;
import org.librecms.contentsection.FolderRepository;
import org.librecms.contentsection.privileges.AssetPrivileges;

import java.util.Objects;
import org.libreccm.categorization.ObjectNotAssignedToCategoryException;

import static org.librecms.CmsConstants.*;

/**
 * Provides methods for managing {@link Asset}s, especially sharable
 * {@link Asset}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class AssetManager {

    private static final Logger LOGGER = LogManager.
        getLogger(AssetManager.class);

    @Inject
    private EntityManager entityManager;

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private CategoryManager categoryManager;

    @Inject
    private FolderRepository folderRepo;

    @Inject
    private FolderManager folderManager;

    /**
     * Makes an {@link Asset} a shared {@code Asset} by adding it to an asset
     * folder. This action can't be undone.
     *
     * The folder must be a subfolder {@link ContentSection#rootAssetsFolder} of
     * a content section. Otherwise an {@link IllegalArgumentException} is
     * thrown.
     *
     *
     * @param asset  The {@link Asset} to share.
     * @param folder The {@link Folder} in which the {@link Asset} is created.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void shareAsset(
        final Asset asset,
        @RequiresPrivilege(AssetPrivileges.CREATE_NEW)
        final Folder folder) {

        if (asset == null) {
            throw new IllegalArgumentException("Can't share asset null.");
        }

        if (folder == null) {
            throw new IllegalArgumentException("No folder provided");
        }

        if (isShared(asset)) {
            throw new IllegalArgumentException(String.format(
                "The asset %s is already shared.",
                Objects.toString(asset)));
        }

        categoryManager.addObjectToCategory(
            asset,
            folder,
            CmsConstants.CATEGORIZATION_TYPE_FOLDER);
    }

    /**
     * Checks of an {@link Asset} is shared (associated with an {@link Folder}.
     * The folder in which the asset is stored can be retrieved using
     * {@link #getAssetFolder(org.librecms.assets.Asset)}.
     *
     * @param asset The asset the check.
     *
     * @return {@code true} is the {@link Asset} is shared,
     *         {@code false if not}.
     */
    public boolean isShared(final Asset asset) {
        return getAssetFolder(asset).isPresent();
    }

    /**
     * Finds all orphaned {@link Asset}s and deletes them. This method requires
     * {@code admin} privileges.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void cleanOrphanedAssets() {
        final List<Asset> assets = assetRepo.findAll();

        final List<Asset> orphaned = assets.stream()
            .filter(asset -> asset.getCategories().isEmpty()
                                 && asset.getItemAttachments().isEmpty())
            .collect(Collectors.toList());

        orphaned.forEach(orphan -> assetRepo.delete(orphan));
    }

    /**
     * Moves an {@link Asset} to an folder.
     *
     * @param asset        The {@link Asset} to move.
     * @param targetFolder The folder to which the {@link Asset} is moved. Must
     *                     be an asset folder.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void move(
        @RequiresPrivilege(AssetPrivileges.EDIT)
        final Asset asset,
        @RequiresPrivilege(AssetPrivileges.CREATE_NEW)
        final Folder targetFolder) {
        
        if (asset == null) {
            throw new IllegalArgumentException("No asset to move provided.");
        }
        
        if (targetFolder == null) {
            throw new IllegalArgumentException("No target folder specified.");
        }
        
        final Optional<Folder> currentFolder = getAssetFolder(asset);
        
        if (currentFolder.isPresent()) {
            try {
                categoryManager.removeObjectFromCategory(asset, 
                                                         currentFolder.get());
            } catch(ObjectNotAssignedToCategoryException ex) {
                throw new UncheckedWrapperException(ex);
            }
        }
        
        categoryManager.addObjectToCategory(asset, 
                                            targetFolder,
                                            CATEGORIZATION_TYPE_FOLDER);
    }

    /**
     * Copies an {@link Asset}.
     *
     * @param asset        The {@link Asset} to copy.
     * @param targetFolder The folder to which the {@link Asset} is copied.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    public void copy(final Asset asset,
                     @RequiresPrivilege(AssetPrivileges.CREATE_NEW)
                     final Folder targetFolder) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Checks if an {@link Asset} is in use. An {@link Asset} is in use if it is
     * member of at least one {@link AttachmentList}.
     *
     * @param asset The {@link Asset} to check for usage.
     *
     * @return {@code true} if the {@link Asset} is in use, {@link false} if
     *         not.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public boolean isAssetInUse(final Asset asset) {
        return !asset.getItemAttachments().isEmpty();
    }

    /**
     * Returns the path of an shared {@link Asset}. The path of an asset is the
     * path of the folder category in which the asset is placed concatenated
     * with the name of the asset. The path is relative to the content section.
     *
     * @param asset The {@link Assset} for which the path is generated.
     *
     * @return The path of the {@link Asset}. If the {@link Asset} is a non
     *         shared asset the path is empty.
     *
     * @see #getAssetPath(org.librecms.assets.Asset, boolean)
     */
    public String getAssetPath(final Asset asset) {
        return getAssetPath(asset, false);
    }

    /**
     * Returns the path of an item as String.
     *
     * @param asset              The {@link Asset} for which the path is
     *                           generated.
     * @param withContentSection Whether to include the content section into the
     *                           path or not.
     *
     * @return The path of the asset. For non shared assets this is an empty
     *         string.
     *
     * @see #getAssetPath(org.librecms.assets.Asset)
     */
    public String getAssetPath(final Asset asset,
                               final boolean withContentSection) {
        final List<Categorization> result = asset.getCategories().stream()
            .filter(categorization -> {
                return CATEGORIZATION_TYPE_FOLDER.equals(
                    categorization.getType());
            })
            .collect(Collectors.toList());

        if (result.isEmpty()) {
            return "";
        } else {
            final List<String> tokens = new ArrayList<>();
            tokens.add(asset.getDisplayName());

            Category current = result.get(0).getCategory();
            while (current.getParentCategory() != null) {
                tokens.add(current.getName());
                current = current.getParentCategory();
            }

            Collections.reverse(tokens);
            final String path = String.join("/", tokens);

            if (withContentSection) {
                final String sectionName
                                 = ((Folder) result.get(0).getCategory()).
                        getSection().getDisplayName();
                return String.format("%s:/%s", sectionName, path);
            } else {
                return String.format("/%s", path);
            }
        }
    }

    /**
     * Creates a list of the folder in which an asset is placed.
     *
     * @param asset
     *
     * @return A list of the folders which form the path of the asset. For non
     *         shared assets an empty list is returned.
     */
    public List<Folder> getAssetFolders(final Asset asset) {
        final List<Categorization> result = asset.getCategories().stream()
            .filter(categorization -> {
                return CATEGORIZATION_TYPE_FOLDER.equals(categorization
                    .getType());
            })
            .collect(Collectors.toList());

        final List<Folder> folders = new ArrayList<>();
        if (!result.isEmpty()) {
            Category current = result.get(0).getCategory();
            if (current instanceof Folder) {
                folders.add((Folder) current);
            } else {
                throw new IllegalArgumentException(String.format(
                    "The asset %s is assigned to the category %s with the"
                        + "categorization type \"%s\", but the Category is not"
                        + "a folder. This is no supported.",
                    asset.getUuid(),
                    current.getUuid(),
                    CATEGORIZATION_TYPE_FOLDER));
            }

            while (current.getParentCategory() != null) {
                current = current.getParentCategory();
                if (current instanceof Folder) {
                    folders.add((Folder) current);
                } else {
                    throw new IllegalArgumentException(String.format(
                        "The asset %s is assigned to the category %s with the"
                            + "categorization type \"%s\", but the Category is not"
                        + "a folder. This is no supported.",
                        asset.getUuid(),
                        current.getUuid(),
                        CATEGORIZATION_TYPE_FOLDER));
                }
            }
        }

        Collections.reverse(folders);
        return folders;
    }

    /**
     * Gets the folder in which an asset is placed.
     *
     * @param asset The asset.
     *
     * @return The folder in which the asset is placed. If the asset is a non
     *         shared asset an empty {@link Optional} is returned.
     */
    public Optional<Folder> getAssetFolder(final Asset asset) {
        return asset.getCategories().stream()
            .filter(categorization -> {
                return CATEGORIZATION_TYPE_FOLDER.equals(
                    categorization.getType());
            })
            .map(categorization -> {
                return (Folder) categorization.getCategory();
            })
            .findFirst();
    }

}
