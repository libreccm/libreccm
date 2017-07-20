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
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.security.Role;
import org.libreccm.security.Shiro;
import org.libreccm.security.User;
import org.librecms.contentsection.privileges.AssetPrivileges;

import java.util.Collections;
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
    private Shiro shiro;

    @Inject
    private PermissionChecker permissionChecker;

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

    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<Asset> findById(final long assetId) {

        final TypedQuery<Asset> query = getEntityManager()
            .createNamedQuery("Asset.findById", Asset.class)
            .setParameter("assetId", assetId);
        setAuthorizationParameters(query);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @SuppressWarnings("unchecked")
    public <T extends Asset> Optional<T> findById(final long assetId,
                                                  final Class<T> type) {

        final TypedQuery<Asset> query = getEntityManager()
            .createNamedQuery("assetId", Asset.class)
            .setParameter("assetId", assetId)
            .setParameter("type", type);
        setAuthorizationParameters(query);

        try {
            final Asset result = query.getSingleResult();
            if (result.getClass().isAssignableFrom(type)) {
                return Optional.of((T) query.getSingleResult());
            } else {
                return Optional.empty();
            }
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void save(
        @RequiresPrivilege(AssetPrivileges.EDIT)
        final Asset asset) {

        super.save(asset);
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
     * Find an {@link Asset} by its UUID. This method does not distinguish
     * between shared and non shared assets.
     *
     * @param uuid The UUID of the {@link Asset}.
     *
     * @return An {@link Optional} containing the {@link Asset} with the
     *         provided {@code uuid} if there is an asset with that
     *         {@code uuid}. Otherwise an empty {@link Optional} is returned.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<Asset> findByUuid(final String uuid) {

        final TypedQuery<Asset> query = entityManager
            .createNamedQuery("Asset.findByUuid", Asset.class);
        query.setParameter("uuid", uuid);
        setAuthorizationParameters(query);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    /**
     * Finds an {@link Asset} by its UUID <strong>and</strong> type. This method
     * does not distinguish between shared and non shared assets.
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
        setAuthorizationParameters(query);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Asset> findByContentSection(final ContentSection section) {

        final TypedQuery<Asset> query = entityManager
            .createNamedQuery("Asset.findByContentSection", Asset.class);
        query.setParameter("section", section);
        setAuthorizationParameters(query);

        return query.getResultList();
    }

    /**
     * Finds all sharable {@link Asset}s where the title is like the provided
     * one. This method does a {@code LIKE} query. Therefore it will find all
     * assets where the title contains the string provided using the
     * {@code title} parameter.
     *
     * @param title The title to search for.
     *
     * @return A list of all sharable assets (from all content sections) where
     *         the title contains the string provided by the {@code title}
     *         parameter.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Asset> findByTitle(final String title) {

        final TypedQuery<Asset> query = entityManager
            .createNamedQuery("Asset.findByTitle", Asset.class);
        query.setParameter("title", title);
        setAuthorizationParameters(query);

        return query.getResultList();
    }

    public List<Asset> findByTitleAndContentSection(
        final String title, final ContentSection section) {

        final TypedQuery<Asset> query = entityManager.createNamedQuery(
            "Asset.findByTitleAndContentSection", Asset.class);
        query.setParameter("title", title);
        query.setParameter("section", section);
        setAuthorizationParameters(query);

        return query.getResultList();
    }

    /**
     * Finds all sharable {@link Asset}s of the specified type from all content
     * sections..
     *
     * @param type The type of the assets to find.
     *
     * @return A list containing all sharable assets of the specified
     *         {@code type} from all content sections.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Asset> findByType(final Class<? extends Asset> type) {

        final TypedQuery<Asset> query = entityManager.createNamedQuery(
            "Asset.findByType", Asset.class);
        query.setParameter("type", type);
        setAuthorizationParameters(query);

        return query.getResultList();
    }

    /**
     * Finds all sharable {@link Asset}s of the specified type in the specified
     * content section.
     *
     * @param type    The type of the assets to find.
     * @param section The section.
     *
     * @return A list containing all sharable assets of the specified
     *         {@code type} in the specified content section.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Asset> findByTypeAndContentSection(
        final Class<? extends Asset> type,
        final ContentSection section) {

        final TypedQuery<Asset> query = entityManager.createNamedQuery(
            "Asset.findByTypeAndContentSection", Asset.class);
        query.setParameter("type", type);
        query.setParameter("section", section);
        setAuthorizationParameters(query);

        return query.getResultList();
    }

    /**
     * Finds all assets of the provided type which contain the provided string
     * in their title.
     *
     * @param title The title fragment used to filter the assets.
     * @param type  The type of the assets.
     *
     * @return A list of all assets from all content sections which are the
     *         specified type and which title matches the provided title.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Asset> findByTitleAndType(final String title,
                                          final Class<? extends Asset> type) {

        final TypedQuery<Asset> query = entityManager
            .createNamedQuery("Asset.findByTitle", Asset.class);
        query.setParameter("title", title);
        query.setParameter("type", type);
        setAuthorizationParameters(query);

        return query.getResultList();
    }

    public List<Asset> findByTitleAndTypeAndContentSection(
        final String title,
        final Class<? extends Asset> type,
        final ContentSection section) {

        final TypedQuery<Asset> query = entityManager
            .createNamedQuery("Asset.findByTitleAndTypeAndContentSection",
                              Asset.class);
        query.setParameter("title", title);
        query.setParameter("type", type);
        query.setParameter("section", section);
        setAuthorizationParameters(query);

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
        setAuthorizationParameters(query);

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
        setAuthorizationParameters(query);

        return query.getSingleResult();
    }

    /**
     * Finds all {@link Asset}s in a specific {@link Folder} which name starts
     * with the provided string.
     *
     * @param folder The {@link Folder} which {@link Asset}s are filtered using
     *               the provided {@code name}.
     * @param title  The string used to fiter the {@link Assets} in the provided
     *               {@code folder}.
     *
     * @return A list with all {@link Asset}s in the provided {@link Folder}
     *         which name starts with the provided string.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Asset> filterByFolderAndTitle(final Folder folder,
                                              final String title) {
        final TypedQuery<Asset> query = entityManager.createNamedQuery(
            "Asset.filterByFolderAndTitle", Asset.class);
        query.setParameter("folder", folder);
        query.setParameter("title", title);
        setAuthorizationParameters(query);

        return query.getResultList();
    }

    /**
     * Counts all {@link Asset}s in a specific {@link Folder} which name starts
     * with the provided string.
     *
     * @param folder The {@link Folder} which {@link Asset}s are filtered using
     *               the provided {@code name}.
     * @param title  The string used to fiter the {@link Assets} in the provided
     *               {@code folder}.
     *
     * @return The number of {@link Asset}s in the provided {@link Folder} which
     *         name starts with the provided string.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public long countFilterByFolderAndTitle(final Folder folder,
                                            final String title) {
        final TypedQuery<Long> query = entityManager.createNamedQuery(
            "Asset.countFilterByFolderAndTitle", Long.class);
        query.setParameter("folder", folder);
        query.setParameter("title", title);
        setAuthorizationParameters(query);

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
        setAuthorizationParameters(query);

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
        setAuthorizationParameters(query);

        return query.getSingleResult();
    }

    /**
     * Finds all assets of a specific type which name starts with a provided
     * string in a specific folder.
     *
     * @param folder The {@link Folder} which contains the assets.
     * @param type   The type of the {@link Asset}s.
     * @param title  The name to filter the {@link Asset}s for.
     *
     * @return A list of all {@link Asset}s of the provided type which name
     *         starts with the provided string in the provided folder.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Asset> filterByFolderAndTypeAndTitle(
        final Folder folder,
        final Class<? extends Asset> type,
        final String title) {

        final TypedQuery<Asset> query = entityManager.createNamedQuery(
            "Asset.filterByFolderAndTitleAndType", Asset.class);
        query.setParameter("folder", folder);
        query.setParameter("type", type);
        query.setParameter("title", title);
        setAuthorizationParameters(query);

        return query.getResultList();
    }

    /**
     * Counts the assets of a specific type which name starts with a provided
     * string in a specific folder.
     *
     * @param folder The {@link Folder} which contains the assets.
     * @param type   The type of the {@link Asset}s.
     * @param title  The name to filter the {@link Asset}s for.
     *
     * @return The number of {@link Asset}s of the provided type which name
     *         starts with the provided string in the provided folder.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public long countFilterByFolderAndTypeAndTitle(
        final Folder folder,
        final Class<? extends Asset> type,
        final String title) {

        final TypedQuery<Long> query = entityManager.createNamedQuery(
            "Asset.countFilterByFolderAndTitleAndType", Long.class);
        query.setParameter("folder", folder);
        query.setParameter("type", type);
        query.setParameter("title", title);
        setAuthorizationParameters(query);

        return query.getSingleResult();
    }

    private void setAuthorizationParameters(final TypedQuery<?> query) {

        final Optional<User> user = shiro.getUser();
        final List<Role> roles;
        if (user.isPresent()) {
            roles = user
                .get()
                .getRoleMemberships()
                .stream()
                .map(membership -> membership.getRole())
                .collect(Collectors.toList());
        } else {
            roles = Collections.emptyList();
        }

        final boolean isSystemUser = shiro.isSystemUser();
        final boolean isAdmin = permissionChecker.isPermitted("*");

        query.setParameter("roles", roles);
        query.setParameter("isSystemUser", isSystemUser);
        query.setParameter("isAdmin", isAdmin);
    }

}
