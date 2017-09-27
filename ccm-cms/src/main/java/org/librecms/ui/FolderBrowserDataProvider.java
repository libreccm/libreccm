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
package org.librecms.ui;

import com.vaadin.cdi.ViewScoped;
import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.Query;
import org.libreccm.categorization.Categorization;
import org.libreccm.core.CcmObject;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;
import org.librecms.contenttypes.ContentTypeInfo;
import org.librecms.contenttypes.ContentTypesManager;

import java.util.stream.Stream;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
class FolderBrowserDataProvider
    extends AbstractBackEndDataProvider<FolderBrowserItem, String> {

    private static final long serialVersionUID = 7693820518000376630L;

    @Inject
    private ContentSectionViewState contentSectionViewState;
    
    @Inject
    private EntityManager entityManager;

    @Inject
    private ContentTypesManager typesManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    private Folder currentFolder;


    protected Folder getCurrentFolder() {
        return currentFolder;
    }

    protected void setCurrentFolder(final Folder currentFolder) {
        this.currentFolder = currentFolder;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    protected Stream<FolderBrowserItem> fetchFromBackEnd(
        final Query<FolderBrowserItem, String> query) {

        final Folder folder;
        if (currentFolder == null) {
            folder = contentSectionViewState
                .getSelectedContentSection()
                .getRootDocumentsFolder();
        } else {
            folder = currentFolder;
        }

        final CriteriaBuilder criteriaBuilder = entityManager
            .getCriteriaBuilder();

        final CriteriaQuery<CcmObject> criteriaQuery = criteriaBuilder
            .createQuery(CcmObject.class);

        final Subquery<Folder> folderQuery = criteriaQuery
            .subquery(Folder.class);
        final Root<Folder> fromFolder = folderQuery.from(Folder.class);
        folderQuery.select(fromFolder);
        if (query.getFilter().isPresent()) {
            final String filter = query.getFilter().get();
            folderQuery.where(criteriaBuilder
                .and(
                    criteriaBuilder.equal(fromFolder.get("parentCategory"),
                                          folder),
                    criteriaBuilder.or(
                        criteriaBuilder.like(fromFolder.get("name"),
                                             String.format("%s%%", filter)),
                        criteriaBuilder.like(fromFolder.get("displayName"),
                                             String.format("%s%%", filter)))));
        } else {
            folderQuery
                .where(criteriaBuilder.equal(fromFolder.get("parentCategory"),
                                             folder));
        }

        final Subquery<ContentItem> itemQuery = criteriaQuery
            .subquery(ContentItem.class);
        final Root<ContentItem> fromItem = itemQuery.from(ContentItem.class);
        final Join<ContentItem, Categorization> joinCat = fromItem
            .join("categories");
        itemQuery.select(fromItem);
        if (query.getFilter().isPresent()) {
            final String filter = query.getFilter().get();
            itemQuery
                .where(criteriaBuilder.and(
                    criteriaBuilder.equal(joinCat.get("category"), folder),
                    criteriaBuilder.or(
                        criteriaBuilder.like(fromItem.get("displayName"),
                                             String.format("%s%%", filter)),
                        criteriaBuilder.like(fromItem.get("name"),
                                             String.format("%s%%", filter))
                    )
                ));
        } else {
            itemQuery
                .where(criteriaBuilder.equal(joinCat.get("category"), folder));
        }

        final Root<CcmObject> from = criteriaQuery.from(CcmObject.class);
        criteriaQuery.select(from);

        criteriaQuery.where(
            criteriaBuilder.or(
                criteriaBuilder.in(from).value(folderQuery),
                criteriaBuilder.in(from).value(itemQuery)
            ));

        return entityManager
            .createQuery(criteriaQuery)
            .setMaxResults(query.getLimit())
            .setFirstResult(query.getOffset())
            .getResultList()
            .stream()
            .map(this::buildRow);
    }

    private FolderBrowserItem buildRow(final CcmObject object) {

        final FolderBrowserItem row = new FolderBrowserItem();
        if (object instanceof Folder) {

            final Folder folder = (Folder) object;
            row.setFolder(true);
            row.setItemId(folder.getObjectId());
            row.setName(folder.getName());
            row.setTitle(globalizationHelper
                .getValueFromLocalizedString(folder.getTitle()));
        } else if (object instanceof ContentItem) {

            final ContentItem item = (ContentItem) object;
            row.setCreationDate(((ContentItem) object).getCreationDate());
            row.setFolder(false);
            row.setItemId(item.getObjectId());
            row.setLastModified(item.getLastModified());
            row.setName(globalizationHelper
                .getValueFromLocalizedString(item.getName()));
            row.setTitle(globalizationHelper
                .getValueFromLocalizedString(item.getTitle()));

            final ContentTypeInfo typeInfo = typesManager
                .getContentTypeInfo(item.getClass());
            final LocalizedTextsUtil textsUtil = globalizationHelper
                .getLocalizedTextsUtil(typeInfo.getLabelBundle());
            row.setType(textsUtil.getText(typeInfo.getLabelKey()));
        } else {

            row.setFolder(false);
            row.setItemId(object.getObjectId());
            row.setName(object.getDisplayName());
        }

        return row;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    protected int sizeInBackEnd(
        final Query<FolderBrowserItem, String> query) {

        final Folder folder;
        if (currentFolder == null) {
            folder = contentSectionViewState
                .getSelectedContentSection()
                .getRootDocumentsFolder();
        } else {
            folder = currentFolder;
        }

        final CriteriaBuilder criteriaBuilder = entityManager
            .getCriteriaBuilder();

        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder
            .createQuery(Long.class);

        final Subquery<Folder> folderQuery = criteriaQuery
            .subquery(Folder.class);
        final Root<Folder> fromFolder = folderQuery.from(Folder.class);
        folderQuery.select(fromFolder);
        if (query.getFilter().isPresent()) {
            final String filter = query.getFilter().get();
            folderQuery.where(criteriaBuilder
                .and(
                    criteriaBuilder.equal(fromFolder.get("parentCategory"),
                                          folder),
                    criteriaBuilder.or(
                        criteriaBuilder.like(fromFolder.get("name"),
                                             String.format("%s%%", filter)),
                        criteriaBuilder.like(fromFolder.get("displayName"),
                                             String.format("%s%%", filter)))));
        } else {
            folderQuery
                .where(criteriaBuilder.equal(fromFolder.get("parentCategory"),
                                             folder));
        }

        final Subquery<ContentItem> itemQuery = criteriaQuery
            .subquery(ContentItem.class);
        final Root<ContentItem> fromItem = itemQuery.from(ContentItem.class);
        final Join<ContentItem, Categorization> joinCat = fromItem
            .join("categories");
        itemQuery.select(fromItem);
        if (query.getFilter().isPresent()) {
            final String filter = query.getFilter().get();
            itemQuery
                .where(criteriaBuilder.and(
                    criteriaBuilder.equal(joinCat.get("category"), folder),
                    criteriaBuilder.or(
                        criteriaBuilder.like(fromItem.get("displayName"),
                                             String.format("%s%%", filter)),
                        criteriaBuilder.like(fromItem.get("name"),
                                             String.format("%s%%", filter))
                    )
                ));
        } else {
            itemQuery
                .where(criteriaBuilder.equal(joinCat.get("category"), folder));
        }

        final Root<CcmObject> from = criteriaQuery.from(CcmObject.class);
        criteriaQuery.select(criteriaBuilder.count(from));

        criteriaQuery.where(
            criteriaBuilder.or(
                criteriaBuilder.in(from).value(folderQuery),
                criteriaBuilder.in(from).value(itemQuery)
            ));

        return entityManager
            .createQuery(criteriaQuery)
            .getSingleResult()
            .intValue();
    }

//    @Override
//    protected Stream<BrowseDocumentsItem> fetchFromBackEnd(
//        final Query<BrowseDocumentsItem, String> query) {
//
//        final int subFoldersCount = countSubFolder(currentFolder,
//                                                   query.getFilter());
//        final int itemsCount = countItems(currentFolder, query.getFilter());
//
//        final int limit = query.getLimit();
//        final int offset = query.getOffset();
//        
//        final int subFoldersLimit;
//        final int subFoldersOffset;
//        final int itemsLimit;
//        final int itemsOffset;
//        if (subFoldersCount > (limit + offset)) {
//            
//            subFoldersLimit = limit;
//            
//            
//        } else {
//            
//        }
//
//        final List<Folder> subFolders = fetchSubFolders(currentFolder,
//                                                        query.getFilter(),
//                                                        query.getLimit(),
//                                                        query.getOffset());
//        final List<ContentItem> items = fetchItems(currentFolder,
//                                                   query.getFilter());
//
//        final List<BrowseDocumentsItem> subFolderItems = subFolders
//            .stream()
//            .map(this::createBrowseDocumentsItem)
//            .collect(Collectors.toList());
//
//        final List<BrowseDocumentsItem> itemList = items
//            .stream()
//            .map(this::createBrowseDocumentsItem)
//            .collect(Collectors.toList());
//
//        final List<BrowseDocumentsItem> rows = new ArrayList<>();
//        rows.addAll(subFolderItems);
//        rows.addAll(itemList);
//
//        return rows.stream();
//    }
//
//    private List<Folder> fetchSubFolders(final Folder folder,
//                                         final Optional<String> filter,
//                                         final int limit,
//                                         final int offset) {
//
//        final CriteriaBuilder builder = entityManager
//            .getCriteriaBuilder();
//        final CriteriaQuery<Folder> query = builder.createQuery(Folder.class);
//        final Root<Folder> from = query.from(Folder.class);
//
//        if (filter.isPresent()) {
//            query.where(builder
//                .and(builder.equal(from.get("parentCategory"), folder),
//                     builder.or(
//                         builder.like(builder.lower(from.get("name")),
//                                      String.format("%s%%", filter.get())),
//                         builder.like(builder.lower(from.get("displayName")),
//                                      String.format("%s%%", filter.get())))));
//        } else {
//            query.where(builder.equal(from.get("parentCategory"), folder));
//        }
//
//        query.orderBy(builder.asc(from.get("name")),
//                      builder.asc(from.get("displayName")));
//
//        return entityManager
//            .createQuery(query)
//            .setMaxResults(limit)
//            .setFirstResult(offset)
//            .getResultList();
//    }
//
//    private List<ContentItem> fetchItems(final Folder folder,
//                                         final Optional<String> filter) {
//
//        final CriteriaBuilder builder = entityManager
//            .getCriteriaBuilder();
//        final CriteriaQuery<ContentItem> query = builder
//            .createQuery(ContentItem.class);
//        final Root<ContentItem> from = query.from(ContentItem.class);
//        final Join<ContentItem, Categorization> join = from.join("categories");
//
//        if (filter.isPresent()) {
//            query.where(builder
//                .and(builder.equal(join.get("category"), folder),
//                     builder.or(
//                         builder.like(builder.lower(from.get("name")),
//                                      String.format("%s%%", filter.get())),
//                         builder.like(builder.lower(from.get("displayName")),
//                                      String.format("%s%%", filter.get())))));
//
//        } else {
//            query.where(builder.equal(join.get("category"), folder));
//        }
//
//        return entityManager
//            .createQuery(query)
//            .getResultList();
//    }
//
//    private FolderBrowserItem createBrowseDocumentsItem(
//        final Folder fromFolder) {
//
//        final FolderBrowserItem item = new FolderBrowserItem();
//        item.setItemId(fromFolder.getObjectId());
//        item.setName(fromFolder.getName());
//        item.setTitle(fromFolder
//            .getTitle()
//            .getValue(controller.getGlobalizationHelper().getNegotiatedLocale()));
//        item.setFolder(true);
//
//        return item;
//    }
//
//    private FolderBrowserItem createBrowseDocumentsItem(
//        final ContentItem fromItem) {
//
//        final FolderBrowserItem item = new FolderBrowserItem();
//        item.setCreationDate(fromItem.getCreationDate());
//        item.setFolder(false);
//        item.setItemId(fromItem.getObjectId());
//        item.setLastChanged(fromItem.getLastModified());
//        item.setName(item.getName());
//        item.setTitle(fromItem
//            .getTitle()
//            .getValue(controller.getGlobalizationHelper().getNegotiatedLocale()));
//
//        final ContentTypesManager typesManager = controller
//            .getContentTypesManager();
//        final ContentTypeInfo typeInfo = typesManager
//            .getContentTypeInfo(fromItem.getContentType());
//        final LocalizedTextsUtil textsUtil = controller
//            .getGlobalizationHelper()
//            .getLocalizedTextsUtil(typeInfo.getLabelBundle());
//        item.setType(textsUtil.getText(typeInfo.getLabelKey()));
//
//        return item;
//    }
//
//    @Transactional(Transactional.TxType.REQUIRED)
//    @Override
//    protected int sizeInBackEnd(final Query<BrowseDocumentsItem, String> query) {
//
//        final Folder folder;
//        if (currentFolder == null) {
//            folder = controller.getCurrentSection().getRootDocumentsFolder();
//        } else {
//            folder = currentFolder;
//        }
//
//        final int subFolderCount = countSubFolder(folder, query.getFilter());
//        final int itemCount = countItems(folder, query.getFilter());
//
//        return subFolderCount + itemCount;
//    }
//
//    private int countSubFolder(final Folder folder,
//                               final Optional<String> filter) {
//
//        final CriteriaBuilder builder = entityManager
//            .getCriteriaBuilder();
//        final CriteriaQuery<Long> query = builder.createQuery(Long.class);
//        final Root<Folder> from = query.from(Folder.class);
//
//        query.select(builder.count(from));
//
//        if (filter.isPresent()) {
//            query.where(builder
//                .and(builder.equal(from.get("parentCategory"), folder),
//                     builder.or(
//                         builder.like(builder.lower(from.get("name")),
//                                      String.format("%s%%", filter.get())),
//                         builder.like(builder.lower(from.get("displayName")),
//                                      String.format("%s%%", filter.get())))));
//        } else {
//            query.where(builder.equal(from.get("parentCategory"), folder));
//        }
//
//        return entityManager
//            .createQuery(query)
//            .getSingleResult()
//            .intValue();
//    }
//
//    private int countItems(final Folder folder,
//                           final Optional<String> filter) {
//
//        final CriteriaBuilder builder = entityManager
//            .getCriteriaBuilder();
//        final CriteriaQuery<Long> query = builder.createQuery(Long.class);
//        final Root<ContentItem> from = query.from(ContentItem.class);
//        final Join<ContentItem, Categorization> join = from.join("categories");
//
//        query.select(builder.count(from));
//
//        if (filter.isPresent()) {
//            query.where(builder
//                .and(builder.equal(join.get("category"), folder),
//                     builder.or(
//                         builder.like(builder.lower(from.get("name")),
//                                      String.format("%s%%", filter.get())),
//                         builder.like(builder.lower(from.get("displayName")),
//                                      String.format("%s%%", filter.get())))));
//
//        } else {
//            query.where(builder.equal(join.get("category"), folder));
//        }
//
//        return entityManager
//            .createQuery(query)
//            .getSingleResult()
//            .intValue();
//    }
}
