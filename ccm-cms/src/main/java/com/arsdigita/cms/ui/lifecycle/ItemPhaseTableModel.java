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
package com.arsdigita.cms.ui.lifecycle;

import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.globalization.GlobalizedMessage;

import org.librecms.CMSConfig;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.CmsConstants;

import java.text.DateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class ItemPhaseTableModel implements TableModel {

    private final Iterator<ItemPhaseTableRow> iterator;
    private ItemPhaseTableRow currentRow;

    public ItemPhaseTableModel(final List<ItemPhaseTableRow> rows) {
        iterator = rows.iterator();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public boolean nextRow() {

        if (iterator.hasNext()) {
            currentRow = iterator.next();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object getElementAt(final int columnIndex) {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final GlobalizationHelper globalizationHelper = cdiUtil
            .findBean(GlobalizationHelper.class);
        final Locale locale = globalizationHelper.getNegotiatedLocale();
        final DateFormat format;
        if (CMSConfig.getConfig().isHideTimezone()) {
            format = DateFormat.getDateTimeInstance(
                DateFormat.FULL, DateFormat.SHORT, locale);
        } else {
            format = DateFormat.getDateTimeInstance(
                DateFormat.FULL, DateFormat.FULL, locale);
        }

        switch (columnIndex) {
            case 0:
                return currentRow.getName();
            case 1:
                return currentRow.getDescription();
            case 2:
                if (currentRow.getStartDate() == null) {
                    return "";
                } else {
                    return format.format(currentRow.getStartDate());
                }
            case 3:
                if (currentRow.getEndDate() == null) {
                    return new GlobalizedMessage("cms.ui.lifecycle.forever",
                                                 CmsConstants.CMS_BUNDLE)
                        .localize();
                } else {
                    return currentRow.getEndDate();
                }
            default:
                throw new IllegalArgumentException("Illegal Column Index");
        }

    }

    @Override
    public Object getKeyAt(final int columnIndex) {

        return currentRow.getPhaseId();
    }

}
