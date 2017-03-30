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
package com.arsdigita.cms.ui.assets;

import com.arsdigita.cms.ui.folder.FolderBrowser;
import com.arsdigita.kernel.KernelConfig;

import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.CmsConstants;
import org.librecms.assets.AssetTypeInfo;
import org.librecms.assets.AssetTypesManager;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.ContentType;

import org.librecms.contentsection.Folder;
import org.librecms.contenttypes.ContentTypeInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import org.libreccm.core.CcmObject;
import org.librecms.contentsection.AssetRepository;
import org.librecms.contentsection.FolderRepository;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class AssetFolderBrowserController {

    @Inject
    private EntityManager entityManager;

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private CategoryManager categoryManager;

    @Inject
    private AssetTypesManager typesManager;

    @Inject
    private FolderRepository folderRepo;

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private GlobalizationHelper globalizationHelper;

    private Locale defaultLocale;

    /**
     * Initialisation method called by the CDI-Container after an instance of
     * this class has be created by the container. Sets the
     * {@link #defaultLocale} property using the the value from the
     * {@link KernelConfig}.
     */
    @PostConstruct
    private void init() {
        final KernelConfig kernelConfig = confManager.findConfiguration(
                KernelConfig.class);
        defaultLocale = kernelConfig.getDefaultLocale();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    List<AssetFolderBrowserTableRow> getAssetRows(final Folder folder,
                                                  final String orderBy,
                                                  final String orderDirection,
                                                  final int firstResult,
                                                  final int maxResults) {
        final List<Folder> subFolders = findSubFolders(folder,
                                                       orderBy,
                                                       orderDirection,
                                                       firstResult,
                                                       maxResults);
        final List<AssetFolderBrowserTableRow> subFolderRows = subFolders
                .stream()
                .map(subFolder -> buildRow(subFolder))
                .collect(Collectors.toList());

        if (subFolders.size() > maxResults) {
            return subFolderRows;
        } else {
            final int maxAssets = maxResults - subFolders.size();
            final int firstAsset = firstResult - subFolders.size();

            final List<Asset> assets = findAssetsInFolder(folder,
                                                          orderBy,
                                                          orderDirection,
                                                          firstAsset,
                                                          maxAssets);
            final List<AssetFolderBrowserTableRow> assetRows = assets
                    .stream()
                    .map(asset -> buildRow(asset))
                    .collect(Collectors.toList());

            final List<AssetFolderBrowserTableRow> rows = new ArrayList<>();
            rows.addAll(subFolderRows);
            rows.addAll(assetRows);

            return rows;
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected long countObjects(final Folder folder) {

        Objects.requireNonNull(folder);

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        final Root<CcmObject> from = criteriaQuery.from(CcmObject.class);

        criteriaQuery = criteriaQuery.select(builder.count(from));

        final List<Folder> subFolders = findSubFolders(
                folder,
                AssetFolderBrowser.SORT_KEY_NAME,
                AssetFolderBrowser.SORT_ACTION_UP,
                -1,
                -1);
        final List<Asset> assets = findAssetsInFolder(
                folder,
                AssetFolderBrowser.SORT_KEY_NAME,
                AssetFolderBrowser.SORT_ACTION_UP,
                -1,
                -1);
        
        if (subFolders.isEmpty() && assets.isEmpty()) {
            return 0;
        } else if(subFolders.isEmpty() && !assets.isEmpty()) {
            criteriaQuery = criteriaQuery.where(from.in(assets));
        } else if (!subFolders.isEmpty() && assets.isEmpty()) {
            criteriaQuery = criteriaQuery.where(from.in(subFolders));
        } else {
            criteriaQuery = criteriaQuery.where(builder.or(
                from.in(subFolders),
                from.in(assets)));
        }

        return entityManager.createQuery(criteriaQuery).getSingleResult();

    }

    /**
     * Called by the {@link AssetFolderBrowser} to delete an object.
     *
     * @param objectId
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected void deleteObject(final String objectId) {

        Objects.requireNonNull(objectId);

        if (objectId.startsWith("folder-")) {
            final long folderId = Long.parseLong(
                    objectId.substring("folder-".length()));

            folderRepo
                    .findById(folderId)
                    .ifPresent(folderRepo::delete);
        } else if (objectId.startsWith("asset-")) {
            final long assetId = Long.parseLong(
                    objectId.substring("asset-".length()));

            assetRepo
                    .findById(assetId)
                    .ifPresent(assetRepo::delete);
        } else {
            throw new IllegalArgumentException(
                    "The objectId is expected to start with 'folder-' or 'item.'.");
        }
    }

    private AssetFolderBrowserTableRow buildRow(final Folder folder) {

        final AssetFolderBrowserTableRow row = new AssetFolderBrowserTableRow();

        row.setObjectId(folder.getObjectId());
        row.setObjectUuid(folder.getUuid());
        row.setName(folder.getName());
        if (folder.getTitle().hasValue(globalizationHelper
                .getNegotiatedLocale())) {
            row.setTitle(folder.getTitle().getValue(globalizationHelper
                    .getNegotiatedLocale()));
        } else {
            row.setTitle(folder.getTitle().getValue(defaultLocale));
        }
        row.setFolder(true);
        row.setDeletable(!categoryManager.hasSubCategories(folder)
                                 && !categoryManager.hasObjects(folder));

        return row;
    }

    private AssetFolderBrowserTableRow buildRow(final Asset asset) {

        final AssetFolderBrowserTableRow row = new AssetFolderBrowserTableRow();

        row.setObjectId(asset.getObjectId());
        row.setObjectUuid(asset.getUuid());
        row.setName(asset.getDisplayName());
        if (asset.getTitle().hasValue(globalizationHelper
                .getNegotiatedLocale())) {
            row.setTitle(asset.getTitle().getValue(globalizationHelper
                    .getNegotiatedLocale()));
        } else {
            row.setTitle(asset.getTitle().getValue(defaultLocale));
        }
        final AssetTypeInfo typeInfo = typesManager
                .getAssetTypeInfo(asset.getClass());
        row.setTypeLabelBundle(typeInfo.getLabelBundle());
        row.setTypeLabelKey(typeInfo.getLabelKey());

        row.setFolder(false);

        return row;
    }

    private List<Folder> findSubFolders(final Folder folder,
                                        final String orderBy,
                                        final String orderDirection,
                                        final int firstResult,
                                        final int maxResults) {

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        final CriteriaQuery<Folder> criteria = builder
                .createQuery(Folder.class);
        final Root<Folder> from = criteria.from(Folder.class);

        final Order order;
        if (AssetFolderBrowser.SORT_KEY_NAME.equals(orderBy)
                    && AssetFolderBrowser.SORT_ACTION_DOWN.
                        equals(orderDirection)) {
            order = builder.desc(from.get("name"));
        } else {
            order = builder.asc(from.get("name"));
        }

        final TypedQuery<Folder> query = entityManager
                .createQuery(
                        criteria.where(
                                builder.
                                        equal(from.get("parentCategory"), folder)
                        )
                                .orderBy(order)
                );

        if (firstResult >= 0) {
            query.setFirstResult(firstResult);
        }

        if (maxResults >= 0) {
            query.setMaxResults(maxResults);
        }

        return query.getResultList();
    }

    private List<Asset> findAssetsInFolder(final Folder folder,
                                           final String orderBy,
                                           final String orderDirection,
                                           final int firstResult,
                                           final int maxResults) {

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        final CriteriaQuery<Asset> criteria = builder.createQuery(Asset.class);
        final Root<Asset> fromAsset = criteria.from(Asset.class);
        final Join<Asset, Category> join = fromAsset.join("categories");

        final Path<?> orderPath;
        switch (orderBy) {
            case AssetFolderBrowser.SORT_KEY_NAME:
                orderPath = fromAsset.get("displayName");
                break;
            case AssetFolderBrowser.SORT_KEY_CREATION_DATE:
                orderPath = fromAsset.get("creationDate");
                break;
            case AssetFolderBrowser.SORT_KEY_LAST_MODIFIED_DATE:
                orderPath = fromAsset.get("lastModifed");
                break;
            default:
                orderPath = fromAsset.get("displayName");
                break;
        }

        final Order order;
        if (AssetFolderBrowser.SORT_ACTION_DOWN.equals(orderDirection)) {
            order = builder.desc(orderPath);
        } else {
            order = builder.asc(orderPath);
        }

        final TypedQuery<Asset> query = entityManager
                .createQuery(
                        criteria.select(fromAsset)
                                .where(
                                        builder.and(
                                                builder.equal(join.get(
                                                        "category"), folder),
                                                builder.equal(join.get("type"),
                                                              CmsConstants.CATEGORIZATION_TYPE_FOLDER)
                                        )
                                )
                                .orderBy(order)
                );

        if (firstResult >= 0) {
            query.setFirstResult(firstResult);
        }

        if (maxResults >= 0) {
            query.setMaxResults(maxResults);
        }

        return query.getResultList();
    }

}
