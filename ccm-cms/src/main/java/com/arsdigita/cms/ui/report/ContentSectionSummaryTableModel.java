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
package com.arsdigita.cms.ui.report;

import com.arsdigita.bebop.table.RowData;
import com.arsdigita.bebop.table.TableModel;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class ContentSectionSummaryTableModel implements TableModel {

    private final Iterator<RowData<Long>> iterator;
    private RowData<Long> currentRow;

    protected ContentSectionSummaryTableModel(final List<RowData<Long>> rows) {
        iterator = rows.iterator();
    }

    @Override
    public int getColumnCount() {
        return 5;
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
        switch (columnIndex) {
            case ContentSectionSummaryTable.COL_FOLDER_NAME:
                return currentRow.getColData(
                        ContentSectionSummaryTable.COL_FOLDER_NAME);
            case ContentSectionSummaryTable.COL_SUBFOLDER_COUNT:
                return currentRow.getColData(
                        ContentSectionSummaryTable.COL_SUBFOLDER_COUNT);
            case ContentSectionSummaryTable.COL_CONTENT_TYPE:
                return currentRow.getColData(
                        ContentSectionSummaryTable.COL_CONTENT_TYPE);
            case ContentSectionSummaryTable.COL_DRAFT_COUNT:
                return currentRow.getColData(
                        ContentSectionSummaryTable.COL_DRAFT_COUNT);
            case ContentSectionSummaryTable.COL_LIVE_COUNT:
                return currentRow.getColData(
                        ContentSectionSummaryTable.COL_LIVE_COUNT);
            default:
                throw new IllegalArgumentException("Invalid column index");
        }
    }

    @Override
    public Object getKeyAt(final int columnIndex) {
        return currentRow.getRowKey();
    }

}
