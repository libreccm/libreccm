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

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.librecms.contentsection.privileges.ItemPrivileges;

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
     * Creates a new, non shared {@link Asset} and adds it to the provided
     * {@link AttachmentList}.
     *
     * @param <T> Type variable for the type of the new {@link Asset}.
     * @param name The name of the new {@link Asset}.
     * @param attachments The {@link AttachmentList} to which the new
     * {@link Asset} is added.
     * @param type The type of the new {@link Asset}. Must be a subclass of the
     * {@link Asset} class.
     * @return The new {@link Asset}.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public <T extends Asset> T createAsset(
            final String name,
            @RequiresPrivilege(ItemPrivileges.EDIT)
            final AttachmentList attachments,
            final Class<T> type) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Creates a new shared {@link Asset} in the provided {@link Folder}.
     *
     * The folder must be a subfolder {@link ContentSection#rootAssetsFolder} of
     * a content section. Otherwise an {@link IllegalArgumentException} is
     * thrown.
     *
     * @param <T> Type variable for the type of the {@link Asset} to create.
     * @param name The name of the new {@link Asset}.
     * @param folder The {@link Folder} in which the {@link Asset} is created.
     * @param type The type of the new {@link Asset}. Must be a subclass of the
     * {@link Asset} class.
     * @return The new {@link Asset}.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public <T extends Asset> T createAsset(
            final String name,
            @RequiresPrivilege(AssetPrivileges.CREATE_NEW)
            final Folder folder,
            final Class<T> type) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Creates a new {@link Asset}. If a folder is provided a sharable
     * {@link Asset} is created. Otherwise a non shared asset is created. This
     * method implements the common logic for
     * {@link #createAsset(java.lang.String, org.librecms.attachments.AttachmentList, java.lang.Class)}
     * and
     * {@link #createAsset(java.lang.String, org.librecms.contentsection.Folder, java.lang.Class)}.
     * Users of this class usually should use these methods. This method has
     * been made public for special cases. Please note that this class does
     * <strong>not</strong>
     * perform any authorization checks. This is up to the caller.
     *
     * @param <T> Type variable for the type of the {@link Asset}.
     * @param name The name of the new {@link Asset}.
     * @param folder Optional folder in which the new {@link Asset} is placed.
     * @param type The type of the new {@link Asset}. Must be a subclass of the
     * {@link Asset} class.
     * @return The new {@link Asset}. Note: If no {@link Folder} is provided and
     * the and the returned {@link Asset} is not added to an
     * {@link AttachmentList} the new {@code Asset} will become orphaned and
     * can't be accessed by any method.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public <T extends Asset> T createAsset(final String name,
                                           final Optional<Folder> folder,
                                           final Class<T> type) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Finds all orphaned {@link Asset}s and deletes them. This method requires
     * {@code admin} privileges.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void deleteOrphanedAssets() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Moves an {@link Asset} to an folder.
     *
     * @param asset The {@link Asset} to move.
     * @param targetFolder The folder to which the {@link Asset} is moved. Must
     * be an asset folder.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void move(
            @RequiresPrivilege(AssetPrivileges.EDIT)
            final Asset asset,
            @RequiresPrivilege(AssetPrivileges.CREATE_NEW)
            final Folder targetFolder) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Copies an {@link Asset}.
     *
     * @param asset The {@link Asset} to copy.
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
     * @return {@code true} if the {@link Asset} is in use, {@link false} if
     * not.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public boolean isAssetInUse(final Asset asset) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Returns the path of an shared {@link Asset}. The path of an asset is the
     * path of the folder category in which the asset is placed concatenated
     * with the name of the asset. The path is relative to the content section.
     *
     * @param asset The {@link Assset} for which the path is generated.
     * @return The path of the {@link Asset}. If the {@link Asset} is a non
     * shared asset the path is empty.
     *
     * @see #getAssetPath(org.librecms.assets.Asset, boolean)
     */
    public String getAssetPath(final Asset asset) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Returns the path of an item as String.
     *
     * @param asset The {@link Asset} for which the path is generated.
     * @param withContentSection Whether to include the content section into the
     * path or not.
     * @return The path of the asset.
     *
     * @see #getAssetPath(org.librecms.assets.Asset)
     */
    public String getAssetPath(final Asset asset,
                               final boolean withContentSection) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Creates a list of the folder in which an asset is placed.
     *
     * @param asset
     * @return
     */
    public List<Folder> getAssetFolders(final Asset asset) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Gets the folder in which an asset is placed.
     *
     * @param asset The asset.
     * @return The folder in which the asset is placed. If the asset is a non
     * shared asset an empty {@link Optional} is returned.
     */
    public Optional<Folder> getAssetFolder(final Asset asset) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
