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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.Categorization;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.CcmObject;
import org.libreccm.core.CcmObjectRepository;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedString;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemL10NManager;
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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class FolderBrowserController {

    private static final Logger LOGGER = LogManager.getLogger(
        FolderBrowserController.class);

    @Inject
    private EntityManager entityManager;

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

    private Locale defaultLocale;

    @PostConstruct
    private void init() {
        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);
        defaultLocale = kernelConfig.getDefaultLocale();
    }

    public List<CcmObject> findObjects(final Folder folder, final String orderBy) {
        return findObjects(folder, orderBy, -1, -1);
    }

    public List<CcmObject> findObjects(final Folder folder,
                                       final String orderBy,
                                       final int first,
                                       final int maxResults) {
        return findObjects(folder, "%", orderBy, first, maxResults);
    }

    public List<CcmObject> findObjects(final Folder folder,
                                       final String filterTerm,
                                       final String orderBy) {
        return findObjects(folder, filterTerm, orderBy, -1, -1);
    }

    public List<CcmObject> findObjects(final Folder folder,
                                       final String filterTerm,
                                       final String orderBy,
                                       final int first,
                                       final int maxResults) {

        Objects.requireNonNull(folder);
        LOGGER.debug("Trying to find objects in folder {}...",
                     Objects.toString(folder));

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<CcmObject> criteriaQuery = builder
            .createQuery(CcmObject.class);
        final Root<CcmObject> from = criteriaQuery.from(CcmObject.class);
 
        return entityManager.createQuery(
            criteriaQuery
                .select(from)
                .where(builder.or(
                    from.in(findSubFolders(folder, filterTerm)),
                    from.in(findItemsInFolder(folder, filterTerm))))
        .orderBy(builder.asc(from.get("displayName"))))
            .setFirstResult(first)
            .setMaxResults(maxResults)
            .getResultList();
    }

    public long countObjects(final Folder folder) {
        return countObjects(folder, "%");
    }

    public long countObjects(final Folder folder,
                             final String filterTerm) {

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = builder.createQuery(
            Long.class);
        final Root<CcmObject> from = criteriaQuery.from(CcmObject.class);

        return entityManager
            .createQuery(
                criteriaQuery
                    .select(builder.count(from))
                    .where(builder.or(
                        from.in(findSubFolders(folder, filterTerm)),
                        from.in(findItemsInFolder(folder, filterTerm)))))
            .getSingleResult();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    List<FolderBrowserTableRow> getObjectRows(final Folder folder,
                                              final String orderBy) {
        final List<CcmObject> objects = findObjects(folder, orderBy);

        return objects.stream()
            .map(object -> buildRow(object))
            .collect(Collectors.toList());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    List<FolderBrowserTableRow> getObjectRows(final Folder folder,
                                              final String filterTerm,
                                              final String orderBy) {
        final List<CcmObject> objects = findObjects(folder,
                                                    filterTerm,
                                                    orderBy);

        return objects.stream()
            .map(object -> buildRow(object))
            .collect(Collectors.toList());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    List<FolderBrowserTableRow> getObjectRows(final Folder folder,
                                              final String orderBy,
                                              final int first,
                                              final int maxResults) {
        final List<CcmObject> objects = findObjects(folder, 
                                                    orderBy, 
                                                    first, 
                                                    maxResults);

        return objects.stream()
            .map(object -> buildRow(object))
            .collect(Collectors.toList());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    List<FolderBrowserTableRow> getObjectRows(final Folder folder,
                                              final String filterTerm,
                                              final String orderBy,
                                              final int first,
                                              final int maxResults) {
        final List<CcmObject> objects = findObjects(folder,
                                                    filterTerm,
                                                    orderBy,
                                                    first,
                                                    maxResults);

        return objects.stream()
            .map(object -> buildRow(object))
            .collect(Collectors.toList());
    }

    private FolderBrowserTableRow buildRow(final CcmObject object) {
        final FolderBrowserTableRow row = new FolderBrowserTableRow();

        if (object instanceof Folder) {
            final Folder folder = (Folder) object;
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
        } else if (object instanceof ContentItem) {
            final ContentItem item = (ContentItem) object;
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
            row.setFolder(false);
        } else {
            row.setObjectId(object.getObjectId());
            row.setObjectUuid(object.getUuid());
            row.setName("???");
            row.setLanguages(Collections.emptyList());
            final LocalizedString title = new LocalizedString();
            title.addValue(globalizationHelper.getNegotiatedLocale(), "???");
            row.setFolder(false);
        }

        return row;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void deleteObject(final long objectId) {
        final Optional<CcmObject> object = objectRepo.findById(objectId);

        if (object.isPresent()) {
            objectRepo.delete(object.get());
        }
    }

    /**
     * Creates a Criteria Query
     *
     * @param folder
     * @param filterTerm
     *
     * @return
     */
    private List<Folder> findSubFolders(final Folder folder,
                                        final String filterTerm) {
        final CriteriaBuilder builder = entityManager
            .getCriteriaBuilder();

        final CriteriaQuery<Folder> query = builder.createQuery(
            Folder.class);
        final Root<Folder> from = query.from(Folder.class);

        return entityManager.createQuery(
            query.where(builder.and(
                builder.equal(from.get("parentCategory"), folder),
                builder.like(builder.lower(from.get("name")), filterTerm))))
            .getResultList();

    }

    private List<ContentItem> findItemsInFolder(
        final Folder folder,
        final String filterTerm) {

        final CriteriaBuilder builder = entityManager
            .getCriteriaBuilder();

        final CriteriaQuery<ContentItem> query = builder.createQuery(
            ContentItem.class);
        final Root<ContentItem> fromItem = query.from(ContentItem.class);
        final Join<ContentItem, Categorization> join = fromItem.join(
            "categories");

        return entityManager.createQuery(query
            .select(fromItem)
            .where(builder.and(
                builder.equal(join.get("category"), folder),
                builder.equal(join.get("type"),
                              CmsConstants.CATEGORIZATION_TYPE_FOLDER),
                builder.equal(fromItem.get("version"),
                              ContentItemVersion.DRAFT),
                builder.like(fromItem.get("displayName"),
                             filterTerm))))
            .getResultList();
    }

}
