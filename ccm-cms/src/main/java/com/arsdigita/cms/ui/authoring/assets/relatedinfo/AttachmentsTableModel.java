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
import com.arsdigita.cms.ui.authoring.assets.AttachmentSelectionModel;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.assets.AssetTypeInfo;
import org.librecms.assets.AssetTypesManager;

import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class AttachmentsTableModel implements TableModel {

    private final PageState state;
    private final AttachmentSelectionModel moveAttachmentModel;

    private final Iterator<AttachmentTableRow> iterator;
    private AttachmentTableRow currentRow;

    AttachmentsTableModel(
        final List<AttachmentTableRow> rows,
        final PageState state,
        final AttachmentSelectionModel moveAttachmentModel) {

        this.state = state;
        this.moveAttachmentModel = moveAttachmentModel;
        this.iterator = rows.iterator();
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

        switch (columnIndex) {
            case AttachmentsTable.COL_TITLE:
                return currentRow.getTitle();
            case AttachmentsTable.COL_TYPE: {
                final AssetTypesManager typesManager = CdiUtil
                    .createCdiUtil()
                    .findBean(AssetTypesManager.class);
                final AssetTypeInfo typeInfo = typesManager
                    .getAssetTypeInfo(currentRow.getType());
                final ResourceBundle bundle = ResourceBundle
                    .getBundle(typeInfo.getLabelBundle());
                return bundle.getString(typeInfo.getLabelKey());
            }
            case AttachmentsTable.COL_MOVE:
                if (moveAttachmentModel.getSelectedAttachment(state) == null) {
                    return new Label(new GlobalizedMessage(
                    "cms.ui.authoring.assets.related_info_step.attachment.move",
                    CmsConstants.CMS_BUNDLE));
                } else {
                    return new Label(new GlobalizedMessage(
                    "cms.ui.authoring.assets.related_info_step.attachment.move_here",
                    CmsConstants.CMS_BUNDLE));
                }
            case AttachmentsTable.COL_REMOVE:
                return new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.related_info_step.attachment.remove",
                CmsConstants.CMS_BUNDLE));
            default:
                throw new IllegalArgumentException(String.format(
                    "Illegal column index %d.", columnIndex));
        }
    }

    @Override
    public Object getKeyAt(final int columnIndex) {
        return currentRow.getAttachmentId();
    }

}
