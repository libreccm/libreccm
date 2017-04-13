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
package com.arsdigita.cms.ui.assets.searchpage;

import com.arsdigita.bebop.table.TableModel;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class ResultsTableModel implements TableModel {

    private static final int COL_TITLE = 0;
    private static final int COL_PLACE = 1;
    private static final int COL_TYPE = 2;

    private final Iterator<ResultsTableRow> iterator;
    private ResultsTableRow currentRow;

    public ResultsTableModel(final List<ResultsTableRow> rows) {
        iterator = rows.iterator();
    }

    @Override
    public int getColumnCount() {
        return 3;
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
        switch(columnIndex) {
            case COL_TITLE:
                return currentRow.getTitle();
            case COL_PLACE:
                return currentRow.getPlace();
            case COL_TYPE:
                return currentRow.getType();
            default:
                throw new IllegalArgumentException("Illegal column index.");
        }
    }

    @Override
    public Object getKeyAt(final int columnIndex) {
        return currentRow.getAssetUuid();
    }

}
