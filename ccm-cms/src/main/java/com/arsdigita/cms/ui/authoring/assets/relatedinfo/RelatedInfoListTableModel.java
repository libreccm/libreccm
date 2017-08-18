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
package com.arsdigita.cms.ui.authoring.assets.relatedinfo;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.cms.ui.authoring.assets.AttachmentListSelectionModel;
import com.arsdigita.globalization.GlobalizedMessage;

import org.librecms.CmsConstants;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class RelatedInfoListTableModel implements TableModel {

    private final PageState state;
    private final AttachmentListSelectionModel moveListModel;

    private final Iterator<AttachmentListTableRow> iterator;
    private AttachmentListTableRow currentRow;

    RelatedInfoListTableModel(
        final List<AttachmentListTableRow> rows,
        final PageState state,
        final AttachmentListSelectionModel moveListModel) {

        this.iterator = rows.iterator();
        this.state = state;
        this.moveListModel = moveListModel;
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
            case RelatedInfoListTable.COL_NAME:
                return currentRow.getName();
            case RelatedInfoListTable.COL_TITLE:
                return currentRow.getTitle();
            case RelatedInfoListTable.COL_DESC:
                return currentRow.getDescription();
            case RelatedInfoListTable.COL_EDIT:
                return new Label(new GlobalizedMessage(
                    "cms.ui.authoring.assets.related_info_step.list.edit"));
            case RelatedInfoListTable.COL_MOVE:
                if (moveListModel.getSelectedAttachmentList(state) == null) {
                    return new Label(new GlobalizedMessage(
                        "cms.ui.authoring.assets.related_info_step.list.move",
                        CmsConstants.CMS_BUNDLE));
                } else {
                    return new Label(new GlobalizedMessage(
                        "cms.ui.authoring.assets.related_info_step.list.move_here",
                        CmsConstants.CMS_BUNDLE));
                }
            case RelatedInfoListTable.COL_DELETE:
                return new Label(new GlobalizedMessage(
                    "cms.ui.authoring.assets.related_info_step.list.delete"));
            default:
                throw new IllegalArgumentException(String.format(
                    "Illegal column index %d.", columnIndex));
        }
    }

    @Override
    public Object getKeyAt(final int columnIndex) {
        return currentRow.getListId();
    }

}
