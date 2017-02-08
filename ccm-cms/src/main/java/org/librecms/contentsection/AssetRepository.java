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


import org.libreccm.auditing.AbstractAuditedEntityRepository;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.ObjectNotAssignedToCategoryException;
import org.libreccm.core.CcmObjectRepository;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.librecms.contentsection.privileges.AssetPrivileges;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 * An repository for {@link Assets}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class AssetRepository
    extends AbstractAuditedEntityRepository<Long, Asset> {

    @Inject
    private EntityManager entityManager;

    @Inject
    private CcmObjectRepository ccmObjectRepo;

    @Inject
    private CategoryManager categoryManager;

    @Inject
    private AssetManager assetManager;

    @Override
    public Long getEntityId(final Asset asset) {
        return asset.getObjectId();
    }

    @Override
    public Class<Asset> getEntityClass() {
        return Asset.class;
    }

    @Override
    public boolean isNew(final Asset asset) {
        return asset.getObjectId() == 0;
    }

    /**
     * Set the UUID of a new asset.
     *
     * @param asset
     */
    @Override
    public void initNewEntity(final Asset asset) {
        super.initNewEntity(asset);
        if (asset.getUuid() == null) {
            final String uuid = UUID.randomUUID().toString();
            asset.setUuid(uuid);
        }
    }

    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void save(
        @RequiresPrivilege(AssetPrivileges.EDIT)
        final Asset asset) {

    }

    /**
     * Deletes an <strong>unused</strong> Asset. If the {@link Asset} is in use
     * (linked to at least one ContentItem) an {@link AssetInUseException} is
     * thrown. Use {@link AssetManager#isAssetInUse} to check if an
     * {@link Asset} is used.
     *
     * @param asset The {@link Asset} to delete.
     *
     * @throws AssetInUseException if the {@link Asset} to delete is in use.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void delete(
        @RequiresPrivilege(AssetPrivileges.DELETE)
        final Asset asset) {

        if (assetManager.isAssetInUse(asset)) {
            throw new AssetInUseException(String.format("Asset %s is in use.",
                                                        asset.getUuid()));
        } else {
            final List<Category> categories = asset.getCategories()
                .stream()
                .map(categorization -> categorization.getCategory())
                .collect(Collectors.toList());

            for (final Category category : categories) {
                try {
                    categoryManager.removeObjectFromCategory(asset, category);
                } catch (ObjectNotAssignedToCategoryException ex) {
                    throw new UnexpectedErrorException(ex);
                }
            }

            ccmObjectRepo.delete(asset);
        }
    }

    /**
     * Find an {@link Asset} by its UUID. This method does distinguish between
     * shared and non shared assets.
     *
     * @param uuid The UUID of the {@link Asset}.
     *
     * @return An {@link Optional} containing the {@link Asset} with the
     *         provided {@code uuid} if there is an asset with that
     *         {@code uuid}. Otherwise an empty {@link Optional} is returned.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<Asset> findByUuid(final String uuid) {
        final TypedQuery<Asset> query = entityManager.createNamedQuery(
            "Asset.findByUuid", Asset.class);
        query.setParameter("uuid", uuid);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    /**
     * Finds an {@link Asset} by its UUID <strong>and</strong> type. This method
     * does distinguish between shared and non shared assets.
     *
     * @param uuid The UUID of the asset to retrieve.
     * @param type The type of the asset to retrieve.
     *
     * @return An {@link Optional} containing the {@link Asset} with the
     *         provided {@code uuid} if there is an asset with that {@code uuid}
     *         and the provided {@code type}. Otherwise an empty
     *         {@link Optional} is returned.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<Asset> findByUuidAndType(
        final String uuid, final Class<? extends Asset> type) {

        final TypedQuery<Asset> query = entityManager.createNamedQuery(
            "Asset.findByUuidAndType", Asset.class);
        query.setParameter("uuid", uuid);
        query.setParameter("type", type);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    /**
     * Finds all shared {@link Asset}s of the specified type.
     *
     * @param type The type of the assets to find.
     *
     * @return A list containing all shared assets of the specified
     *         {@code type}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Asset> findByType(final Class<? extends Asset> type) {

        final TypedQuery<Asset> query = entityManager.createNamedQuery(
            "Asset.findByType", Asset.class);
        query.setParameter("type", type);

        return query.getResultList();
    }

    /**
     * Finds all shared {@link Asset}s in a specific {@link Folder}.
     *
     * @param folder The {@link Folder} which contains the assets.
     *
     * @return A list of all assets in the {@link Folder}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Asset> findByFolder(final Folder folder) {
        final TypedQuery<Asset> query = entityManager.createNamedQuery(
            "Asset.findByFolder", Asset.class);
        query.setParameter("folder", folder);

        return query.getResultList();
    }

    /**
     * Counts all shared {@link Asset}s in a specific {@link Folder}.
     *
     * @param folder The {@link Folder} which contains the assets.
     *
     * @return The number of {@link Asset}s in the {@link Folder}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public long countAssetsInFolder(final Folder folder) {
        final TypedQuery<Long> query = entityManager.createNamedQuery(
            "Asset.countInFolder", Long.class);
        query.setParameter("folder", folder);

        return query.getSingleResult();
    }

    /**
     * Finds all {@link Asset}s in a specific {@link Folder} which name starts
     * with the provided string.
     *
     * @param folder The {@link Folder} which {@link Asset}s are filtered using
     *               the provided {@code name}.
     * @param name   The string used to fiter the {@link Assets} in the provided
     *               {@code folder}.
     *
     * @return A list with all {@link Asset}s in the provided {@link Folder}
     *         which name starts with the provided string.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Asset> filterByFolderAndName(final Folder folder,
                                             final String name) {
        final TypedQuery<Asset> query = entityManager.createNamedQuery(
            "Asset.filterByFolderAndName", Asset.class);
        query.setParameter("folder", folder);
        query.setParameter("name", name);

        return query.getResultList();
    }

    /**
     * Counts all {@link Asset}s in a specific {@link Folder} which name starts
     * with the provided string.
     *
     * @param folder The {@link Folder} which {@link Asset}s are filtered using
     *               the provided {@code name}.
     * @param name   The string used to fiter the {@link Assets} in the provided
     *               {@code folder}.
     *
     * @return The number of {@link Asset}s in the provided {@link Folder} which
     *         name starts with the provided string.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public long countFilterByFolderAndName(final Folder folder,
                                           final String name) {
        final TypedQuery<Long> query = entityManager.createNamedQuery(
            "Asset.countFilterByFolderAndName", Long.class);
        query.setParameter("folder", folder);
        query.setParameter("name", name);

        return query.getSingleResult();
    }

    /**
     * Finds all {@link Asset}s of a specific type in a specific folder.
     *
     * @param folder The {@link Folder} which contains the {@link Asset}s.
     * @param type   The type of the {@link Asset}s.
     *
     * @return A list containing all {@link Asset}s of the provided {@code type}
     *         in the provided {@link Folder}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Asset> filterByFolderAndType(
        final Folder folder,
        final Class<? extends Asset> type) {

        final TypedQuery<Asset> query = entityManager.createNamedQuery(
            "Asset.filterByFolderAndType", Asset.class);
        query.setParameter("folder", folder);
        query.setParameter("type", type);

        return query.getResultList();
    }

    /**
     * Counts the {@link Asset}s of a specific type in a specific folder.
     *
     * @param folder The {@link Folder} which contains the {@link Asset}s.
     * @param type   The type of the {@link Asset}s.
     *
     * @return The number of {@link Asset}s of the provided {@code type} in the
     *         provided {@link Folder}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public long countFilterByFolderAndType(final Folder folder,
                                           final Class<? extends Asset> type) {
        final TypedQuery<Long> query = entityManager.createNamedQuery(
            "Asset.countFilterByFolderAndType", Long.class);
        query.setParameter("folder", folder);
        query.setParameter("type", type);

        return query.getSingleResult();
    }

    /**
     * Finds all assets of a specific type which name starts with a provided
     * string in a specific folder.
     *
     * @param folder The {@link Folder} which contains the assets.
     * @param type   The type of the {@link Asset}s.
     * @param name   The name to filter the {@link Asset}s for.
     *
     * @return A list of all {@link Asset}s of the provided type which name
     *         starts with the provided string in the provided folder.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Asset> filterByFolderAndTypeAndName(
        final Folder folder,
        final Class<? extends Asset> type,
        final String name) {

        final TypedQuery<Asset> query = entityManager.createNamedQuery(
            "Asset.filterByFolderAndNameAndType", Asset.class);
        query.setParameter("folder", folder);
        query.setParameter("type", type);
        query.setParameter("name", name);

        return query.getResultList();
    }

    /**
     * Counts the assets of a specific type which name starts with a provided
     * string in a specific folder.
     *
     * @param folder The {@link Folder} which contains the assets.
     * @param type   The type of the {@link Asset}s.
     * @param name   The name to filter the {@link Asset}s for.
     *
     * @return The number of {@link Asset}s of the provided type which name
     *         starts with the provided string in the provided folder.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public long countFilterByFolderAndTypeAndName(
        final Folder folder,
        final Class<? extends Asset> type,
        final String name) {

        final TypedQuery<Long> query = entityManager.createNamedQuery(
            "Asset.countFilterByFolderAndNameAndType", Long.class);
        query.setParameter("folder", folder);
        query.setParameter("type", type);
        query.setParameter("name", name);

        return query.getSingleResult();
    }

}
