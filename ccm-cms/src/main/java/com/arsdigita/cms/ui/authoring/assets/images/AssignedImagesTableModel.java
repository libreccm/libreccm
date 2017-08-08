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
package com.arsdigita.cms.ui.authoring.assets.images;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ui.authoring.assets.ItemAttachmentSelectionModel;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.web.CCMDispatcherServlet;

import org.librecms.CmsConstants;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class AssignedImagesTableModel implements TableModel {

    private final PageState state;
    private final ItemAttachmentSelectionModel moveAttachmentModel;

    private final Iterator<AssignedImageTableRow> iterator;
    private AssignedImageTableRow currentRow;

    public AssignedImagesTableModel(
        final List<AssignedImageTableRow> rows,
        final PageState state,
        final ItemAttachmentSelectionModel moveAttachmentModel) {

        this.iterator = rows.iterator();
        this.state = state;
        this.moveAttachmentModel = moveAttachmentModel;
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
            case AssignedImagesTable.COL_PREVIEW:
                return String
                    .format("%s/content-.sections/%s/images/"
                                + "uuid-%s?width=150&height=100",
                            CCMDispatcherServlet.getContextPath(),
                            CMS.getContext().getContentSection().getLabel(),
                            currentRow.getImageUuid());
            case AssignedImagesTable.COL_IMAGE_DATA: {
                final ImageProperties imageProperties = new ImageProperties();
                imageProperties.setFilename(currentRow.getFilename());
                imageProperties.setWidth(currentRow.getWidth());
                imageProperties.setHeight(currentRow.getHeight());
                imageProperties.setType(currentRow.getType());

                return imageProperties;
            }
            case AssignedImagesTable.COL_TITLE:
                return currentRow.getTitle();
            case AssignedImagesTable.COL_CAPTION:
                return currentRow.getCaption();
            case AssignedImagesTable.COL_MOVE:
                if (moveAttachmentModel.getSelectedKey(state) == null) {
                    return new Label(new GlobalizedMessage(
                        "cms.ui.authoring.assets.imagestep.assigned_image.link_move",
                        CmsConstants.CMS_BUNDLE));
                } else {
                    return new Label(new GlobalizedMessage(
                        "cms.ui.authoring.assets.imagestep.assigned_image.link_move_below_here",
                        CmsConstants.CMS_BUNDLE));
                }
            case AssignedImagesTable.COL_REMOVE:
                return new Label(new GlobalizedMessage(
                    "cms.ui.authoring.assets.imagestep.assigned_images.remove",
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
