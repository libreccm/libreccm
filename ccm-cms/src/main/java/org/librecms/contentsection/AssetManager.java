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
import org.librecms.contentsection.privileges.AssetPrivileges;

import java.util.Objects;

import org.libreccm.categorization.ObjectNotAssignedToCategoryException;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.l10n.LocalizedString;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

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
        if (asset == null) {
            throw new IllegalArgumentException(
                "Can't determine if null is a shared asset.");
        }

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
                                 && asset.getItemAttachments().isEmpty()).
            collect(Collectors.toList());

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

        if (targetFolder.getType() != FolderType.ASSETS_FOLDER) {
            throw new IllegalArgumentException(String.format(
                "The provided target folder %s is not an asset folder.",
                Objects.toString(targetFolder)));
        }

        final Optional<Folder> currentFolder = getAssetFolder(asset);

        if (currentFolder.isPresent()) {
            try {
                categoryManager.removeObjectFromCategory(asset,
                                                         currentFolder.get());
            } catch (ObjectNotAssignedToCategoryException ex) {
                throw new UnexpectedErrorException(ex);
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
     *
     * @return The copy of the {@code asset}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @SuppressWarnings("unchecked")
    public Asset copy(final Asset asset,
                      @RequiresPrivilege(AssetPrivileges.CREATE_NEW)
                      final Folder targetFolder) {

        if (asset == null) {
            throw new IllegalArgumentException("No asset to copy.");
        }

        if (targetFolder == null) {
            throw new IllegalArgumentException("No target folder provided.");
        }

        if (targetFolder.getType() != FolderType.ASSETS_FOLDER) {
            throw new IllegalArgumentException(String.format(
                "The provided target folder %s is not an asset folder.",
                Objects.toString(targetFolder)));
        }

        final Asset copy;
        try {
            copy = asset.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new UnexpectedErrorException(ex);
        }

        final BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(asset.getClass());
        } catch (IntrospectionException ex) {
            throw new UnexpectedErrorException(ex);
        }

        for (final PropertyDescriptor propertyDescriptor : beanInfo
            .getPropertyDescriptors()) {
            if (propertyIsExcluded(propertyDescriptor.getName())) {
                continue;
            }

            final Class<?> propType = propertyDescriptor.getPropertyType();
            final Method readMethod = propertyDescriptor.getReadMethod();
            final Method writeMethod = propertyDescriptor.getWriteMethod();

            if (writeMethod == null) {
                continue;
            }

            if (LocalizedString.class.equals(propType)) {
                final LocalizedString source;
                final LocalizedString target;
                try {
                    source = (LocalizedString) readMethod.invoke(asset);
                    target = (LocalizedString) readMethod.invoke(copy);
                } catch (IllegalAccessException
                         | IllegalArgumentException
                         | InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }

                source.getAvailableLocales().forEach(
                    locale -> target.addValue(locale,
                                              source.getValue(locale)));
            } else if (propType != null
                           && propType.isAssignableFrom(Asset.class)) {

                final Asset linkedAsset;
                try {
                    linkedAsset = (Asset) readMethod.invoke(asset);
                } catch (IllegalAccessException
                         | IllegalArgumentException
                         | InvocationTargetException ex) {
                    throw new UnexpectedErrorException(ex);
                }

                try {
                    writeMethod.invoke(copy, linkedAsset);
                } catch (IllegalAccessException
                         | IllegalArgumentException
                         | InvocationTargetException ex) {
                    throw new UnexpectedErrorException(ex);
                }
            } else if (propType != null
                           && propType.isAssignableFrom(List.class)) {
                final List<Object> source;
                final List<Object> target;
                try {
                    source = (List<Object>) readMethod.invoke(asset);
                    target = (List<Object>) readMethod.invoke(copy);
                } catch (IllegalAccessException
                         | IllegalArgumentException
                         | InvocationTargetException ex) {
                    throw new UnexpectedErrorException(ex);
                }

                target.addAll(source);
            } else if (propType != null
                           && propType.isAssignableFrom(Map.class)) {
                final Map<Object, Object> source;
                final Map<Object, Object> target;

                try {
                    source = (Map<Object, Object>) readMethod.invoke(asset);
                    target = (Map<Object, Object>) readMethod.invoke(copy);
                } catch (IllegalAccessException
                         | IllegalArgumentException
                         | InvocationTargetException ex) {
                    throw new UnexpectedErrorException(ex);
                }

                source.forEach((key, value) -> target.put(key, value));
            } else if (propType != null
                           && propType.isAssignableFrom(Set.class)) {
                final Set<Object> source;
                final Set<Object> target;

                try {
                    source = (Set<Object>) readMethod.invoke(asset);
                    target = (Set<Object>) readMethod.invoke(copy);
                } catch (IllegalAccessException
                         | IllegalArgumentException
                         | InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }

                target.addAll(source);
            } else {
                final Object value;
                try {
                    value = readMethod.invoke(asset);
                    writeMethod.invoke(copy, value);
                } catch (IllegalAccessException
                         | IllegalArgumentException
                         | InvocationTargetException ex) {
                    throw new UnexpectedErrorException(ex);
                }
            }
        }

        if (targetFolder.equals(getAssetFolder(asset).orElse(null))) {
            final long number = assetRepo.countFilterByFolderAndTitle(
                targetFolder, String.format("%s_copy",
                                            asset.getDisplayName()));
            final long index = number + 1;
            copy.setDisplayName(String.format("%s_copy%d",
                                              copy.getDisplayName(),
                                              index));
        }

        assetRepo.save(copy);

        categoryManager.addObjectToCategory(copy,
                                            targetFolder,
                                            CATEGORIZATION_TYPE_FOLDER);

        return asset;
    }

    private boolean propertyIsExcluded(final String name) {
        final String[] excluded = new String[]{"objectId",
                                               "uuid",
                                               "categories",
                                               "itemAttachments"};

        boolean result = false;
        for (final String current : excluded) {
            if (current.equals(name)) {
                result = true;
            }
        }

        return result;
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
        if (asset == null) {
            throw new IllegalArgumentException("Can't verify if null is in use.");
        }

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
                final Category category = result.get(0).getCategory();
                final Optional<Folder> folder = folderRepo.findById(
                    category.getObjectId());
                final String sectionName;
                if (folder.isPresent()) {
                    sectionName = folder.get().getSection().getDisplayName();
                } else {
                    sectionName = "?";
                }
                
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
        if (asset == null) {
            throw new IllegalArgumentException(
                "Can't retrieve the folder for asset null.");
        }

        if (asset.getCategories() == null) {
            return Optional.empty();
        }

        final Optional<Category> category = asset.getCategories().stream()
            .filter(categorization -> CATEGORIZATION_TYPE_FOLDER.equals(
            categorization.getType()))
            .map(categorization -> categorization.getCategory())
            .findFirst();

        if (category.isPresent()) {
            return folderRepo.findById(category.get().getObjectId());
        } else {
            return Optional.empty();
        }
    }

}
