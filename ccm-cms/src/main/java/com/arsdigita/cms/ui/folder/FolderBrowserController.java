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

import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.CcmObject;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedString;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemL10NManager;
import org.librecms.contentsection.Folder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
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

    @Inject
    private EntityManager entityManager;

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private ContentItemL10NManager itemL10NManager;

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
        final TypedQuery<CcmObject> query = entityManager.createNamedQuery(
            "Folder.findObjects", CcmObject.class);
        query.setParameter("folder", folder);
        query.setParameter(filterTerm, filterTerm);

        if (first > 0 && maxResults > 0) {
            query.setFirstResult(first);
            query.setMaxResults(maxResults);
        }

        return query.getResultList();
    }

    public int countObjects(final Folder folder) {
        return countObjects(folder, -1, -1);
    }

    public int countObjects(final Folder folder,
                            final int frist,
                            final int maxResults) {
        return countObjects(folder, "%", frist, maxResults);
    }

    public int countObjects(final Folder folder,
                            final String filterTerm) {
        return countObjects(folder, filterTerm, -1, -1);
    }

    public int countObjects(final Folder folder,
                            final String filterTerm,
                            final int first,
                            final int maxResults) {
        final TypedQuery<Integer> query = entityManager.createNamedQuery(
            "Folder.countObjects", Integer.class);
        query.setParameter("folder", folder);
        query.setParameter(filterTerm, filterTerm);

        if (first > 0 && maxResults > 0) {
            query.setFirstResult(first);
            query.setMaxResults(maxResults);
        }

        return query.getSingleResult();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<FolderBrowserTableRow> getObjectRows(final Folder folder) {
        final List<CcmObject> objects = findObjects(folder);

        return objects.stream()
            .map(object -> buildRow(object))
            .collect(Collectors.toList());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<FolderBrowserTableRow> getObjectRows(final Folder folder,
                                                        final String filterTerm) {
        final List<CcmObject> objects = findObjects(folder,
                                                    filterTerm);

        return objects.stream()
            .map(object -> buildRow(object))
            .collect(Collectors.toList());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<FolderBrowserTableRow> getObjectRows(final Folder folder,
                                                        final int first,
                                                        final int maxResults) {
        final List<CcmObject> objects = findObjects(folder, first, maxResults);

        return objects.stream()
            .map(object -> buildRow(object))
            .collect(Collectors.toList());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<FolderBrowserTableRow> getObjectRows(final Folder folder,
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

}
