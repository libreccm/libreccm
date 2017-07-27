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

import com.arsdigita.cms.CMS;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.web.CCMDispatcherServlet;

import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.CmsConstants;
import org.librecms.assets.AssetTypeInfo;
import org.librecms.assets.AssetTypesManager;
import org.librecms.contentsection.Asset;

import org.librecms.contentsection.Folder;

import java.util.ArrayList;
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
import org.librecms.contentsection.AssetManager;
import org.librecms.contentsection.AssetRepository;
import org.librecms.contentsection.FolderManager;
import org.librecms.contentsection.FolderRepository;

import java.util.Collections;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.librecms.assets.Image;

import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class AssetFolderBrowserController {

    private static final Logger LOGGER = LogManager
        .getLogger(AssetFolderBrowserController.class);

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
    private FolderManager folderManager;

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private AssetManager assetManager;

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
                                                       "%",
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
                                                          "%",
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

        return countObjects(folder, "%");

    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected long countObjects(final Folder folder, final String filterTerm) {

        Objects.requireNonNull(folder);
        Objects.requireNonNull(filterTerm);

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        final Root<CcmObject> from = criteriaQuery.from(CcmObject.class);

        criteriaQuery = criteriaQuery.select(builder.count(from));

        final List<Folder> subFolders = findSubFolders(
            folder,
            filterTerm,
            AssetFolderBrowser.SORT_KEY_NAME,
            AssetFolderBrowser.SORT_ACTION_UP,
            -1,
            -1);
        final List<Asset> assets = findAssetsInFolder(
            folder,
            filterTerm,
            AssetFolderBrowser.SORT_KEY_NAME,
            AssetFolderBrowser.SORT_ACTION_UP,
            -1,
            -1);

        if (subFolders.isEmpty() && assets.isEmpty()) {
            return 0;
        } else if (subFolders.isEmpty() && !assets.isEmpty()) {
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

    @Transactional(Transactional.TxType.REQUIRED)
    protected void copyObjects(final Folder targetFolder,
                               final String[] objectIds) {

        Objects.requireNonNull(targetFolder);
        Objects.requireNonNull(objectIds);

        for (final String objectId : objectIds) {
            if (objectId.startsWith(FOLDER_BROWSER_KEY_PREFIX_FOLDER)) {
                copyFolder(targetFolder,
                           Long.parseLong(objectId.substring(
                               FOLDER_BROWSER_KEY_PREFIX_FOLDER.length())));
            } else if (objectId.startsWith(FOLDER_BROWSER_KEY_PREFIX_ASSET)) {
                copyAsset(targetFolder,
                          Long.parseLong(objectId.substring(
                              FOLDER_BROWSER_KEY_PREFIX_ASSET.length())));
            } else {
                throw new IllegalArgumentException(String.format(
                    "ID '%s' does not start with '%s' or '%s'.",
                    objectId,
                    FOLDER_BROWSER_KEY_PREFIX_FOLDER,
                    FOLDER_BROWSER_KEY_PREFIX_ASSET));
            }
        }

    }

    private void copyFolder(final Folder targetFolder,
                            final long folderId) {

        Objects.requireNonNull(targetFolder);

        final Folder folder = folderRepo.findById(folderId)
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No folder with ID %d in the database. "
                + "Where did that ID come from?",
            folderId)));

        folderManager.copyFolder(folder, targetFolder);

    }

    private void copyAsset(final Folder targetFolder,
                           final long assetId) {

        Objects.requireNonNull(targetFolder);

        final Asset asset = assetRepo
            .findById(assetId)
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No asset ith ID %d in the database. Where did that ID come from?",
            assetId)));

        assetManager.copy(asset, targetFolder);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void moveObjects(final Folder targetFolder,
                            final String[] objectIds) {

        Objects.requireNonNull(targetFolder);
        Objects.requireNonNull(objectIds);

        for (final String objectId : objectIds) {
            if (objectId.startsWith(FOLDER_BROWSER_KEY_PREFIX_FOLDER)) {
                moveFolder(targetFolder,
                           Long.parseLong(objectId.substring(
                               FOLDER_BROWSER_KEY_PREFIX_FOLDER.length())));
            } else if (objectId.startsWith(FOLDER_BROWSER_KEY_PREFIX_ASSET)) {
                moveAsset(targetFolder,
                          Long.parseLong(objectId.substring(
                              FOLDER_BROWSER_KEY_PREFIX_ASSET.length())));
            } else {
                throw new IllegalArgumentException(String.format(
                    "ID '%s' does not start with '%s' or '%s'.",
                    objectId,
                    FOLDER_BROWSER_KEY_PREFIX_FOLDER,
                    FOLDER_BROWSER_KEY_PREFIX_ASSET));
            }
        }
    }

    private void moveFolder(final Folder targetFolder, final long folderId) {

        Objects.requireNonNull(targetFolder);

        final Folder folder = folderRepo.findById(folderId)
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No folder with ID %d in the database. "
                + "Where did that ID come from?",
            folderId)));

        folderManager.moveFolder(folder, targetFolder);
    }

    private void moveAsset(final Folder targetFolder, final long assetId) {

        Objects.requireNonNull(targetFolder);

        final Asset asset = assetRepo
            .findById(assetId)
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No asset with ID %d in the database. Where did that ID come from?",
            assetId)));

        assetManager.move(asset, targetFolder);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<String> createInvalidTargetsList(final List<String> sources) {

        Objects.requireNonNull(sources);

        final List<String> sourceFolderIds = sources
            .stream()
            .filter(source -> source.startsWith(
            FOLDER_BROWSER_KEY_PREFIX_FOLDER))
            .collect(Collectors.toList());
        final List<String> parentFolderIds = sourceFolderIds
            .stream()
            .map(sourceFolderId -> findParentFolderId(sourceFolderId))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
        final List<List<String>> subFolderIds = sourceFolderIds
            .stream()
            .map(sourceFolderId -> findSubFolderIds(sourceFolderId))
            .collect(Collectors.toList());

        final List<String> invalidTargetIds = new ArrayList<>();
        invalidTargetIds.addAll(sourceFolderIds);
        invalidTargetIds.addAll(parentFolderIds);
        for (final List<String> subFolderIdList : subFolderIds) {
            invalidTargetIds.addAll(subFolderIdList);
        }

        return invalidTargetIds;

    }

    private Optional<String> findParentFolderId(final String folderId) {

        Objects.requireNonNull(folderId);

        if (!folderId.startsWith(FOLDER_BROWSER_KEY_PREFIX_FOLDER)) {
            throw new IllegalArgumentException(String.format(
                "Provided string '%s' is not an ID of a folder.",
                folderId));
        }

        final long objectId = Long.parseLong(folderId.substring(
            FOLDER_BROWSER_KEY_PREFIX_FOLDER.length()));
        final Folder folder = folderRepo.findById(objectId)
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No folder with ID %d found in database. "
                + "Where did that ID come form?",
            objectId)));
        final Optional<Folder> parentFolder = folderManager.getParentFolder(
            folder);
        if (parentFolder.isPresent()) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(String.format(
                "%s%d",
                FOLDER_BROWSER_KEY_PREFIX_FOLDER,
                parentFolder.get().getObjectId()));
        }
    }

    private List<String> findSubFolderIds(final String folderId) {

        Objects.requireNonNull(folderId);

        if (!folderId.startsWith(FOLDER_BROWSER_KEY_PREFIX_FOLDER)) {
            throw new IllegalArgumentException(String.format(
                "Provided string '%s' is not the ID of a folder.",
                folderId));
        }

        final long objectId = Long.parseLong(folderId.substring(
            FOLDER_BROWSER_KEY_PREFIX_FOLDER.length()));
        final Folder folder = folderRepo.findById(objectId)
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No folder with ID %d found in database. "
                + "Where did that ID come form?",
            objectId)));
        return findSubFolders(folder)
            .stream()
            .map(subFolder -> String.format("%s%d",
                                            FOLDER_BROWSER_KEY_PREFIX_FOLDER,
                                            subFolder.getObjectId()))
            .collect(Collectors.toList());
    }

    private List<Folder> findSubFolders(final Folder folder) {

        Objects.requireNonNull(folder);

        if (folder.getSubFolders() == null
                || folder.getSubFolders().isEmpty()) {
            return Collections.emptyList();
        }

        final List<Folder> subFolders = new ArrayList<>();
        for (final Folder subFolder : folder.getSubFolders()) {
            subFolders.add(subFolder);
            subFolders.addAll(findSubFolders(subFolder));
        }

        return subFolders;
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
                "The objectId is expected to start with 'folder-' or 'asset-'.");
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
        if (asset instanceof Image) {
            row.setThumbnailUrl(String
                .format("%s/content-sections/%s/images/"
                            + "uuid-%s?width=150&height=100",
                        CCMDispatcherServlet.getContextPath(),
                        CMS.getContext().getContentSection().getLabel(),
                        asset.getUuid()));
        }
        final AssetTypeInfo typeInfo = typesManager
            .getAssetTypeInfo(asset.getClass());
        row.setTypeLabelBundle(typeInfo.getLabelBundle());
        row.setTypeLabelKey(typeInfo.getLabelKey());

        row.setFolder(false);

        row.setDeletable(!assetManager.isAssetInUse(asset));

        return row;
    }

    private List<Folder> findSubFolders(final Folder folder,
                                        final String filterTerm,
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
                    builder.and(
                        builder.
                            equal(from.get("parentCategory"),
                                  folder),
                        builder.like(builder.lower(from.get(
                            "name")),
                                     filterTerm)
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

    private List<Asset> findAssetsInFolder(final Folder folder,
                                           final String filterTerm,
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

        LOGGER.debug("The database contains {} assets.",
                     entityManager.createQuery(criteria.select(fromAsset)
                         .where(
                             builder.and(
                                 builder.equal(join.get("category"),
                                               folder),
                                 builder.equal(join.get("type"),
                                               CmsConstants.CATEGORIZATION_TYPE_FOLDER),
                                 builder.like(builder.lower(
                                     fromAsset.get(
                                         "displayName")),
                                              filterTerm)
                             ))).getResultList().size());

        final TypedQuery<Asset> query = entityManager
            .createQuery(
                criteria.select(fromAsset)
                    .where(
                        builder.and(
                            builder.equal(join.get(
                                "category"), folder),
                            builder.equal(join.get("type"),
                                          CmsConstants.CATEGORIZATION_TYPE_FOLDER),
                            builder.like(builder.lower(
                                fromAsset.get(
                                    "displayName")),
                                         filterTerm)
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
