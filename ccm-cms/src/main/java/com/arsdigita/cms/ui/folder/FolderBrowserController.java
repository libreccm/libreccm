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

import com.arsdigita.kernel.KernelConfig;

import org.libreccm.categorization.Categorization;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.CcmObject;
import org.libreccm.core.CcmObjectRepository;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemL10NManager;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemVersion;
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.Folder;
import org.librecms.contenttypes.ContentTypeInfo;
import org.librecms.contenttypes.ContentTypesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;

/**
 * The {@code FolderBrowserController} wraps all database operations (queries)
 * required by the {@link FolderBrowser}, the
 * {@link FolderBrowserTableModelBuilder} and the
 * {@link FolderBrowserTableModel}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class FolderBrowserController {

    @Inject
    private EntityManager entityManager;

    @Inject
    private CategoryManager categoryManager;
    
    @Inject
    private CcmObjectRepository objectRepo;

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private ContentItemL10NManager itemL10NManager;

    @Inject
    private ContentTypesManager typesManager;
    
    @Inject 
    private ContentItemManager itemManager;

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

    /**
     * Count the objects (subfolders and content items) in the provided folder.
     *
     * @param folder The folder.
     *
     * @return The number of objects (subfolders and content items) in the
     *         provided {@code folder}.
     */
    public long countObjects(final Folder folder) {
        return countObjects(folder, "%");
    }

    /**
     * Count all objects (subfolders and content items) in the provided folder
     * which match the provided filter term.
     *
     * @param folder     The folder.
     * @param filterTerm The filter term.
     *
     * @return The number of objects (subfolders and content items) in the
     *         provided {@code folder} which match the provided
     *         {@code filterTerm}.
     */
    public long countObjects(final Folder folder,
                             final String filterTerm) {

        Objects.requireNonNull(folder, "Can't count objects in Folder null.");
        Objects.requireNonNull(filterTerm, "Can't filter for 'null'.");

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        final Root<CcmObject> from = criteriaQuery.from(CcmObject.class);

        criteriaQuery = criteriaQuery.select(builder.count(from));

        final List<Folder> subFolders = findSubFolders(folder,
                                                       filterTerm,
                                                       FolderBrowser.SORT_KEY_NAME,
                                                       FolderBrowser.SORT_ACTION_UP,
                                                       -1,
                                                       -1);
        final List<ContentItem> items = findItemsInFolder(folder,
                                                          filterTerm,
                                                          FolderBrowser.SORT_KEY_NAME,
                                                          FolderBrowser.SORT_ACTION_UP,
                                                          -1,
                                                          -1);
        if (subFolders.isEmpty() && items.isEmpty()) {
            return 0;
        } else if (subFolders.isEmpty() && !items.isEmpty()) {
            criteriaQuery = criteriaQuery.where(from.in(items));
        } else if (!subFolders.isEmpty() && items.isEmpty()) {
            criteriaQuery = criteriaQuery.where(from.in(subFolders));
        } else {
            criteriaQuery = criteriaQuery.where(builder.or(
                from.in(subFolders),
                from.in(items)));
        }

        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * Create {@link FolderBrowserTableRow} objects for all objects in the
     * provided folder.
     *
     * @param folder         The folder which contains the objects.
     * @param orderBy        The field used to order the objects.
     * @param orderDirection The direction for ordering the objects.
     *
     * @return A list with {@link FolderBrowserTableRow} objects for each object
     *         in the provided {@code folder} ordered by the provided field and
     *         in the provided direction.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    List<FolderBrowserTableRow> getObjectRows(final Folder folder,
                                              final String orderBy,
                                              final String orderDirection) {
        return getObjectRows(folder, "%", orderBy, orderDirection, -1, -1);
    }

    /**
     * Create {@link FolderBrowserTableRow} objects for all objects in the
     * provided folder which match provided filter term.
     *
     * @param folder         The folder which contains the objects.
     * @param filterTerm     The filter term.
     * @param orderBy        The field used to order the objects.
     * @param orderDirection The direction for ordering the objects.
     *
     * @return A list with {@link FolderBrowserTableRow} objects for each object
     *         in the provided {@code folder} which matches the provided
     *         {@code filterTerm}, ordered by the provided field and in the
     *         provided direction.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    List<FolderBrowserTableRow> getObjectRows(final Folder folder,
                                              final String filterTerm,
                                              final String orderBy,
                                              final String orderDirection) {
        return getObjectRows(folder,
                             filterTerm,
                             orderBy,
                             orderDirection,
                             -1,
                             -1);
    }

    /**
     * Create {@link FolderBrowserTableRow} objects for the objects in the
     * provided folder which are in the range provided by {@code firstResult}
     * and {@code maxResult}
     *
     * @param folder         The folder which contains the objects.
     * @param orderBy        The field used to order the objects.
     * @param orderDirection The direction for ordering the objects.
     * @param firstResult    The index of the first object to use.
     * @param maxResults     The maximum number of objects to retrieve.
     *
     * @return A list with {@link FolderBrowserTableRow} objects for each object
     *         in the provided {@code folder} ordered by the provided field and
     *         in the provided direction. The list will start with the object
     *         with index provided as {@code firstResult} and contain at most
     *         {@code maxResults} items.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    List<FolderBrowserTableRow> getObjectRows(final Folder folder,
                                              final String orderBy,
                                              final String orderDirection,
                                              final int firstResult,
                                              final int maxResults) {
        return getObjectRows(folder,
                             "%",
                             orderBy,
                             orderDirection,
                             firstResult,
                             maxResults);
    }

    /**
     * Create {@link FolderBrowserTableRow} objects for the objects in the
     * provided folder which match the provided filter term and which are in the
     * range provided by {@code firstResult} and {@code maxResult}
     *
     * @param folder         The folder which contains the objects.
     * @param filterTerm     The filter term.
     * @param orderBy        The field used to order the objects.
     * @param orderDirection The direction for ordering the objects.
     * @param firstResult    The index of the first object to use.
     * @param maxResults     The maximum number of objects to retrieve.
     *
     * @return A list with {@link FolderBrowserTableRow} objects for each object
     *         in the provided {@code folder} which matches the provided
     *         {@code filterTerm}, ordered by the provided field and in the
     *         provided direction. The list will start with the object with
     *         index provided as {@code firstResult} and contain at most
     *         {@code maxResults} items.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    List<FolderBrowserTableRow> getObjectRows(final Folder folder,
                                              final String filterTerm,
                                              final String orderBy,
                                              final String orderDirection,
                                              final int firstResult,
                                              final int maxResults) {
        final List<Folder> subFolders = findSubFolders(folder,
                                                       filterTerm,
                                                       orderBy,
                                                       orderDirection,
                                                       firstResult,
                                                       maxResults);
        final List<FolderBrowserTableRow> subFolderRows = subFolders.stream()
            .map(subFolder -> buildRow(subFolder))
            .collect(Collectors.toList());

        if (subFolders.size() > maxResults) {
            return subFolderRows;
        } else {
            final int maxItems = maxResults - subFolders.size();
            final int firstItem = firstResult - subFolders.size();

            final List<ContentItem> items = findItemsInFolder(folder,
                                                              filterTerm,
                                                              orderBy,
                                                              orderDirection,
                                                              firstItem,
                                                              maxItems);
            final List<FolderBrowserTableRow> itemRows = items.stream()
                .map(item -> buildRow(item))
                .collect(Collectors.toList());

            final ArrayList<FolderBrowserTableRow> rows = new ArrayList<>();
            rows.addAll(subFolderRows);
            rows.addAll(itemRows);

            return rows;
        }
    }

    /**
     * Helper method for building a {@link FolderBrowserTableRow} from a
     * {@link Folder}.
     *
     * @param folder The {@link Folder} to use for building the
     *               {@link FolderBrowserTableRow}.
     *
     * @return A {@link FolderBrowserTableRow} containing the data needed by the
     *         {@link FolderBrowser} to display the provided {@code folder}.
     */
    private FolderBrowserTableRow buildRow(final Folder folder) {

        final FolderBrowserTableRow row = new FolderBrowserTableRow();

        row.setObjectId(folder.getObjectId());
        row.setObjectUuid(folder.getUuid());
        row.setName(folder.getName());
        row.setLanguages(Collections.emptyList());
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

    /**
     * Helper method for building a {@link FolderBrowserTableRow} from a
     * {@link ContentItem}.
     *
     * @param item The {@link ContentItem} to use for building the
     *             {@link FolderBrowserTableRow}.
     *
     * @return A {@link FolderBrowserTableRow} containing the data needed by the
     *         {@link FolderBrowser} to display the provided {@code item}.
     */
    private FolderBrowserTableRow buildRow(final ContentItem item) {

        final FolderBrowserTableRow row = new FolderBrowserTableRow();

        row.setObjectId(item.getObjectId());
        row.setObjectUuid(item.getItemUuid());
        row.setName(item.getName().getValue(defaultLocale));
        final List<Locale> languages = new ArrayList<>(itemL10NManager
            .availableLanguages(item));
        languages.sort((lang1, lang2) -> lang1.toString().compareTo(
            lang2.toString()));
        row.setLanguages(languages);
        if (item.getTitle().hasValue(globalizationHelper
            .getNegotiatedLocale())) {
            row.setTitle(item.getTitle().getValue(globalizationHelper
                .getNegotiatedLocale()));
        } else {
            row.setTitle(item.getTitle().getValue(defaultLocale));
        }
        final ContentType type = item.getContentType();
        final ContentTypeInfo typeInfo = typesManager.getContentTypeInfo(
            type);
        row.setTypeLabelBundle(typeInfo.getLabelBundle());
        row.setTypeLabelKey(typeInfo.getLabelKey());
        
        row.setDeletable(!itemManager.isLive(item));

        row.setCreated(item.getCreationDate());
        row.setLastModified(item.getLastModified());

        row.setFolder(false);

        return row;
    }

    /**
     * Called by the {@link FolderBrowser} to delete an object.
     *
     * @param objectId
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected void deleteObject(final long objectId) {
        final Optional<CcmObject> object = objectRepo.findById(objectId);

        if (object.isPresent()) {
            objectRepo.delete(object.get());
        }
    }

    /**
     * Retrieves all subfolders of a folder matching the provided filter term.
     * Because {@link Folder} does not have any of the fields despite the name
     * which can be used to order the objects ordering is done as follows:
     *
     * If {@code orderBy} has any value other than
     * {@link FolderBrowser#SORT_KEY_NAME} the subfolders are ordered in
     * ascending order by their name. If {@code orderBy} is
     * {@link FolderBrowser#SORT_KEY_NAME} the subfolders are ordered by their
     * name in ascending and descending order depending on the value of
     * {@code orderDirection}.
     *
     * @param folder         The folder which contains the subfolders.
     * @param filterTerm     The filter term.
     * @param orderBy        Field to use for ordering. If the value is negative
     *                       the parameter is ignored.
     * @param orderDirection Direction for ordering. If the value is negative
     *                       the parameter is ignored.
     * @param firstResult    Index of the first result to retrieve.
     * @param maxResults     Maxium number of results to retrieve.
     *
     *
     * @return A list of the subfolders of the provided {@code folder} which
     *         match the provided {@code filterTerm}. The list is ordered as
     *         described above. The list will contain at most {@code maxResults}
     *         starting with the result with the index provided as
     *         {@code firstResult}.
     */
    private List<Folder> findSubFolders(final Folder folder,
                                        final String filterTerm,
                                        final String orderBy,
                                        final String orderDirection,
                                        final int firstResult,
                                        final int maxResults) {
        final CriteriaBuilder builder = entityManager
            .getCriteriaBuilder();

        final CriteriaQuery<Folder> criteria = builder.createQuery(
            Folder.class);
        final Root<Folder> from = criteria.from(Folder.class);

        final Order order;
        if (FolderBrowser.SORT_KEY_NAME.equals(orderBy)
                && FolderBrowser.SORT_ACTION_DOWN.equals(orderDirection)) {
            order = builder.desc(from.get("name"));
        } else {
            order = builder.asc(from.get("name"));
        }

        final TypedQuery<Folder> query = entityManager.createQuery(
            criteria.where(builder.and(
                builder.equal(from.get("parentCategory"), folder),
                builder
                    .like(builder.lower(from.get("name")), filterTerm)))
                .orderBy(order));

        if (firstResult >= 0) {
            query.setFirstResult(firstResult);
        }
        if (maxResults >= 0) {
            query.setMaxResults(maxResults);
        }

        return query.getResultList();
    }

    /**
     * Retrieves all items of a folder matching the provided filter term.
     *
     * @param folder         The folder which contains the subfolders.
     * @param filterTerm     The filter term.
     * @param orderBy        Field to use for ordering. If the value is negative
     *                       the parameter is ignored.
     * @param orderDirection Direction for ordering. If the value is negative
     *                       the parameter is ignored.
     * @param firstResult    Index of the first result to retrieve.
     * @param maxResults     Maxium number of results to retrieve.
     *
     *
     * @return A list of the subfolders of the provided {@code folder} which
     *         match the provided {@code filterTerm}. The list is ordered the
     *         field provided as {@code orderBy} in the direction provided by
     *         {@code orderDirection}. The list will contain at most
     *         {@code maxResults} starting with the result with the index
     *         provided as {@code firstResult}.
     */
    private List<ContentItem> findItemsInFolder(final Folder folder,
                                                final String filterTerm,
                                                final String orderBy,
                                                final String orderDirection,
                                                final int firstResult,
                                                final int maxResults) {

        final CriteriaBuilder builder = entityManager
            .getCriteriaBuilder();

        final CriteriaQuery<ContentItem> criteria = builder.createQuery(
            ContentItem.class);
        final Root<ContentItem> fromItem = criteria.from(ContentItem.class);
        final Join<ContentItem, Categorization> join = fromItem.join(
            "categories");

        final Path<?> orderPath;
        switch (orderBy) {
            case FolderBrowser.SORT_KEY_NAME:
                orderPath = fromItem.get("displayName");
                break;
            case FolderBrowser.SORT_KEY_CREATION_DATE:
                orderPath = fromItem.get("creationDate");
                break;
            case FolderBrowser.SORT_KEY_LAST_MODIFIED_DATE:
                orderPath = fromItem.get("lastModified");
                break;
            default:
                orderPath = fromItem.get("displayName");
                break;
        }

        final Order order;
        if (FolderBrowser.SORT_ACTION_DOWN.equals(orderDirection)) {
            order = builder.desc(orderPath);
        } else {
            order = builder.asc(orderPath);
        }

        final TypedQuery<ContentItem> query = entityManager.createQuery(criteria
            .select(fromItem)
            .where(builder.and(
                builder.equal(join.get("category"), folder),
                builder.equal(join.get("type"),
                              CmsConstants.CATEGORIZATION_TYPE_FOLDER),
                builder.equal(fromItem.get("version"),
                              ContentItemVersion.DRAFT),
                builder.like(fromItem.get("displayName"),
                             filterTerm)))
            .orderBy(order));

        if (firstResult >= 0) {
            query.setFirstResult(firstResult);
        }
        if (maxResults >= 0) {
            query.setMaxResults(maxResults);
        }

        return query.getResultList();
    }

}
