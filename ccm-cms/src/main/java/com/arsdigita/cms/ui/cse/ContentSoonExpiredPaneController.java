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
package com.arsdigita.cms.ui.cse;

import com.arsdigita.bebop.table.RowData;

import org.arsdigita.cms.CMSConfig;
import org.libreccm.auditing.CcmRevision;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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
public class ContentSoonExpiredPaneController {

    @Inject
    private EntityManager entityManager;

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private PermissionChecker permissionChecker;

    @Transactional(Transactional.TxType.REQUIRED)
    public List<RowData<Long>> getSoonExpiredItems(
        final ContentSection section) {
        final TypedQuery<ContentItem> query = entityManager.createQuery(
            "SELECT i FROM ContentItem i "
                + "WHERE i.contentType.contentSection = :section "
                + "AND :endDateTime <= i.lifecycle.endDateTime",
            ContentItem.class);

        final CMSConfig cmsConfig = confManager.findConfiguration(
            CMSConfig.class);
        final int months = cmsConfig.getSoonExpiredTimespanMonths();
        final int days = cmsConfig.getSoonExpiredTimespanDays();

        final Calendar date = Calendar.getInstance(Locale.ROOT);
        date.add(Calendar.DAY_OF_YEAR, days);
        date.add(Calendar.MONTH, months);
        query.setParameter("endDateTime", date.getTime());

        query.setParameter("section", section);

        final List<ContentItem> result = query.getResultList();

        return result.stream()
            .map(item -> createRow(item))
            .collect(Collectors.toList());

    }

    private RowData<Long> createRow(final ContentItem item) {
        final RowData<Long> row = new RowData<>(5);
        row.setRowKey(item.getObjectId());

        final CcmRevision current = itemRepo.retrieveCurrentRevision(
            item, item.getObjectId());
        row.setColData(ContentSoonExpiredTable.COL_AUTHOR_NAME,
                       current.getUserName());

        row.setColData(ContentSoonExpiredTable.COL_ITEM_NAME,
                       item.getDisplayName());

        row.setColData(ContentSoonExpiredTable.COL_VIEW,
                       item.getUuid());

        if (permissionChecker.isPermitted(ItemPrivileges.EDIT, item)) {
            row.setColData(ContentSoonExpiredTable.COL_EDIT,
                           item.getUuid());
        } else {
            row.setColData(ContentSoonExpiredTable.COL_EDIT,
                           "--");
        }

        final DateFormat dateFormat = DateFormat.getDateTimeInstance(
            DateFormat.LONG,
            DateFormat.LONG,
            globalizationHelper.getNegotiatedLocale());
        row.setColData(ContentSoonExpiredTable.COL_END_DATE_TIME,
                       dateFormat.format(item.getLifecycle().getEndDateTime()));

        return row;
    }

}
