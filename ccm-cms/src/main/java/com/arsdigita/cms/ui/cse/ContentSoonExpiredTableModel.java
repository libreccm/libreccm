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
import com.arsdigita.bebop.table.TableModel;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class ContentSoonExpiredTableModel implements TableModel {

    private final Iterator<RowData<Long>> iterator;
    private RowData<Long> currentRow;

    protected ContentSoonExpiredTableModel(final List<RowData<Long>> rows) {
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
            case ContentSoonExpiredTable.COL_AUTHOR_NAME:
                return currentRow.getColData(
                    ContentSoonExpiredTable.COL_AUTHOR_NAME);
            case ContentSoonExpiredTable.COL_ITEM_NAME:
                return currentRow.getColData(
                    ContentSoonExpiredTable.COL_ITEM_NAME);
            case ContentSoonExpiredTable.COL_VIEW:
                return currentRow.getColData(ContentSoonExpiredTable.COL_VIEW);
            case ContentSoonExpiredTable.COL_EDIT:
                return currentRow.getColData(ContentSoonExpiredTable.COL_EDIT);
            case ContentSoonExpiredTable.COL_END_DATE_TIME:
                return currentRow.getColData(
                    ContentSoonExpiredTable.COL_END_DATE_TIME);
            default:
                throw new IllegalArgumentException("Invalid column index.");
        }
    }

    @Override
    public Object getKeyAt(final int columnIndex) {
        return currentRow.getRowKey();
    }

}
