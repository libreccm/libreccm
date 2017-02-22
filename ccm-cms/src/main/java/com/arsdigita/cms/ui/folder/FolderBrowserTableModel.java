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

import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.globalization.GlobalizedMessage;

import org.librecms.CmsConstants;

import java.util.Iterator;
import java.util.List;


/**
 * Table model for the {@link FolderBrowser}.
 *
 * @see {FolderBrowserTableModelBuilder}
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class FolderBrowserTableModel implements TableModel {

    private static final int COL_NAME = 0;
    private static final int COL_LANGUAGES = 1;
    private static final int COL_TITLE = 2;
    private static final int COL_TYPE = 3;
    private static final int COL_CREATION_DATE = 4;
    private static final int COL_LAST_MODIFIED = 5;
    private static final int COL_DELETEABLE = 6;

    private final Iterator<FolderBrowserTableRow> iterator;
    private FolderBrowserTableRow currentRow;

    public FolderBrowserTableModel(final List<FolderBrowserTableRow> rows) {
        iterator = rows.iterator();
    }

    @Override
    public int getColumnCount() {
        return 6;
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
            case COL_NAME:
                return currentRow.getName();
            case COL_LANGUAGES:
                return currentRow.getLanguages();
            case COL_TITLE:
                return currentRow.getTitle();
            case COL_TYPE:
                final String typeLabelBundle = currentRow.getTypeLabelBundle();
                final String typeLabelKey = currentRow.getTypeLabelKey();
                if (typeLabelKey == null) {
                    return new GlobalizedMessage("empty_text",
                                                 CmsConstants.CMS_BUNDLE);
                } else {
                    return new GlobalizedMessage(typeLabelKey, typeLabelBundle);
                }
            case COL_CREATION_DATE:
                return currentRow.getCreated();
            case COL_LAST_MODIFIED:
                return currentRow.getLastModified();
            case COL_DELETEABLE:
                return currentRow.isDeletable();
            default:
                throw new IllegalArgumentException(String.format(
                    "Illegal column index %d.", columnIndex));
        }
    }

    @Override
    public Object getKeyAt(final int columnIndex) {
        if (currentRow.isFolder()) {
            return String.format("folder-%d", currentRow.getObjectId());
        } else {
            return String.format("item-%d", currentRow.getObjectId());
        }
        
        
//        return currentRow.getObjectId();
    }
    
    public boolean isFolder() {
        return currentRow.isFolder();
    }

}
