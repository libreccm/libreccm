/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.item;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.table.RowData;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.globalization.GlobalizedMessage;

import org.librecms.CmsConstants;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class ItemLanguagesTableModel implements TableModel {

    private final Iterator<RowData<String>> iterator;
    private RowData<String> curentRow;

    protected ItemLanguagesTableModel(final List<RowData<String>> rows) {
        iterator = rows.iterator();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public boolean nextRow() {
        if (iterator.hasNext()) {
            curentRow = iterator.next();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object getElementAt(final int columnIndex) {
        switch (columnIndex) {
            case ItemLanguagesTable.COL_LANGUAGE:
                return curentRow.getColData(0);
            case ItemLanguagesTable.COL_TITLE:
                return curentRow.getColData(1);
            case ItemLanguagesTable.COL_DELETE:
                return new Label(new GlobalizedMessage("cms.ui.delete",
                                                       CmsConstants.CMS_BUNDLE));
            default:
                throw new IllegalArgumentException("Invalid column index.");
        }
    }

    @Override
    public Object getKeyAt(final int columnIndex) {
        return curentRow.getRowKey();
    }

}
