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
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.CcmObject;
import org.libreccm.core.CcmObjectRepository;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedString;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemL10NManager;
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

    public long countSubFolders(final Folder folder) {
        return countSubFolders(folder, "%");
    }

    public long countSubFolders(final Folder folder,
                                final String filterTerm) {
        final TypedQuery<Long> query = entityManager.createNamedQuery(
            "Folder.countSubFolders", Long.class);
        query.setParameter("folder", folder);
        query.setParameter("term", filterTerm);

        return query.getSingleResult();
    }

    public long countItems(final Folder folder) {
        return countItems(folder, "%");
    }

    public long countItems(final Folder folder,
                           final String filterTerm) {
        final TypedQuery<Long> query = entityManager.createNamedQuery(
            "Folder.countItems", Long.class);
        query.setParameter("folder", folder);
        query.setParameter("term", filterTerm);

        return query.getSingleResult();
    }

    public List<Folder> findSubFolders(final Folder folder) {
        return findSubFolders(folder, "%");
    }

    public List<Folder> findSubFolders(final Folder folder,
                                       final String filterTerm) {
        final TypedQuery<Folder> query = entityManager.createNamedQuery(
            "Folder.findSubFolders", Folder.class);
        query.setParameter("folder", folder);
        query.setParameter("term", filterTerm);

        return query.getResultList();
    }

    public List<ContentItem> findItems(final Folder folder) {
        return findItems(folder, "%");
    }

    public List<ContentItem> findItems(final Folder folder,
                                       final int first,
                                       final int maxResults) {
        return findItems(folder, "%", first, maxResults);
    }

    public List<ContentItem> findItems(final Folder folder,
                                       final String filterTerm) {
        return findItems(folder, filterTerm, -1, -1);
    }

    public List<ContentItem> findItems(final Folder folder,
                                       final String filterTerm,
                                       final int first,
                                       final int maxResults) {
        final TypedQuery<ContentItem> query = entityManager.createNamedQuery(
            "Folder.findItems", ContentItem.class);
        query.setParameter("folder", folder);
        query.setParameter("term", filterTerm);
        if (first > 0 && maxResults > 0) {
            query.setFirstResult(first);
            query.setMaxResults(maxResults);
        }

        return query.getResultList();
    }

    public List<CcmObject> findObjects(final Folder folder) {
        return findObjects(folder, -1, -1);
    }

    public List<CcmObject> findObjects(final Folder folder,
                                       final int first,
                                       final int maxResults) {
        return findObjects(folder, "%", first, maxResults);
    }

    public List<CcmObject> findObjects(final Folder folder,
                                       final String filterTerm) {
        return findObjects(folder, filterTerm, -1, -1);
    }

    public List<CcmObject> findObjects(final Folder folder,
                                       final String filterTerm,
                                       final int first,
                                       final int maxResults) {

        Objects.requireNonNull(folder);
        LOGGER.debug("Trying to find objects in folder {}...",
                     Objects.toString(folder));

//        final TypedQuery<CcmObject> testQuery1 = entityManager.createQuery(
//            "SELECT f FROM Folder f "
//                + "WHERE f.parentCategory = :folder "
//                + "AND LOWER(f.name) LIKE :term",
//            CcmObject.class);
//        testQuery1.setParameter("folder", folder);
//        testQuery1.setParameter("term", filterTerm);
//        final List<CcmObject> testResult1 = testQuery1.getResultList();
//        LOGGER.debug("TestResult1: {}",
//                     Objects.toString(testResult1));
//
//        final TypedQuery<CcmObject> testQuery2 = entityManager.createQuery(
//            "SELECT i FROM ContentItem i JOIN i.categories c "
//                + "WHERE c.category = :folder "
//                + "AND c.type = '" + CmsConstants.CATEGORIZATION_TYPE_FOLDER
//            + "' "
//                + "AND i.version = "
//                + "org.librecms.contentsection.ContentItemVersion.DRAFT "
//                + "AND (LOWER(i.displayName) LIKE LOWER(:term))",
//            CcmObject.class);
//        testQuery2.setParameter("folder", folder);
//        testQuery2.setParameter("term", filterTerm);
//        final List<CcmObject> testResult2 = testQuery2.getResultList();
//        LOGGER.debug("TestResult2: {}",
//                     Objects.toString(testResult2));

        final TypedQuery<CcmObject> query = entityManager.createNamedQuery(
            "Folder.findObjects", CcmObject.class);
        query.setParameter("folder", folder);
        query.setParameter("term", filterTerm);

        if (first > 0 && maxResults > 0) {
            query.setFirstResult(first);
            query.setMaxResults(maxResults);
        }

        return query.getResultList();
    }

    public long countObjects(final Folder folder) {
        return countObjects(folder, "%");
    }

    public long countObjects(final Folder folder,
                             final String filterTerm) {
        final TypedQuery<Long> query = entityManager.createNamedQuery(
            "Folder.countObjects", Long.class);
        query.setParameter("folder", folder);
        query.setParameter("term", filterTerm);

        return query.getSingleResult();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    List<FolderBrowserTableRow> getObjectRows(final Folder folder) {
        final List<CcmObject> objects = findObjects(folder);

        return objects.stream()
            .map(object -> buildRow(object))
            .collect(Collectors.toList());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    List<FolderBrowserTableRow> getObjectRows(final Folder folder,
                                              final String filterTerm) {
        final List<CcmObject> objects = findObjects(folder,
                                                    filterTerm);

        return objects.stream()
            .map(object -> buildRow(object))
            .collect(Collectors.toList());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    List<FolderBrowserTableRow> getObjectRows(final Folder folder,
                                              final int first,
                                              final int maxResults) {
        final List<CcmObject> objects = findObjects(folder, first, maxResults);

        return objects.stream()
            .map(object -> buildRow(object))
            .collect(Collectors.toList());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    List<FolderBrowserTableRow> getObjectRows(final Folder folder,
                                              final String filterTerm,
                                              final int first,
                                              final int maxResults) {
        final List<CcmObject> objects = findObjects(folder,
                                                    filterTerm,
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
            final ContentTypeInfo typeInfo = typesManager.getContentTypeInfo(type);
            row.setTypeLabelBundle(typeInfo.getLabelBundle());
            row.setTypeLabelKey(typeInfo.getLabelKey());
        } else {
            row.setObjectId(object.getObjectId());
            row.setObjectUuid(object.getUuid());
            row.setName("???");
            row.setLanguages(Collections.emptyList());
            final LocalizedString title = new LocalizedString();
            title.addValue(globalizationHelper.getNegotiatedLocale(), "???");
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

}
