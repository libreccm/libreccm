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
package com.arsdigita.cms.ui.assets;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ui.folder.FolderSelectionModel;
import com.arsdigita.util.LockableImpl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.Folder;

import java.util.List;

/**
 * Creates the {@link TableModel} for the {@link AssetFolderBrowser}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class AssetFolderBrowserTableModelBuilder
        extends LockableImpl
        implements TableModelBuilder {

    private static final Logger LOGGER = LogManager
            .getLogger(AssetFolderBrowserTableModelBuilder.class);

    @Override
    public TableModel makeModel(final Table table,
                                final PageState state) {

        if (!(table instanceof AssetFolderBrowser)) {
            throw new IllegalArgumentException(
                    "The AssetFolderBrowserTableModelBuilder can only be used "
                            + "for the AssetFolderBrowser.");
        }

        final AssetFolderBrowser assetFolderBrowser = (AssetFolderBrowser) table;
        final FolderSelectionModel folderSelectionModel = assetFolderBrowser
                .getFolderSelectionModel();
        final Folder folder = folderSelectionModel.getSelectedObject(state);
        if (folder == null) {
            return Table.EMPTY_MODEL;
        } else {
            assetFolderBrowser.getRowSelectionModel().clearSelection(state);
            final Paginator paginator = assetFolderBrowser.getPaginator();
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final AssetFolderBrowserController controller = cdiUtil
                    .findBean(AssetFolderBrowserController.class);
            final String orderBy;
            if (assetFolderBrowser.getSortType(state) == null) {
                orderBy = AssetFolderBrowser.SORT_KEY_NAME;
            } else {
                orderBy = assetFolderBrowser.getSortType(state);
            }
            final String orderDirection;
            if (assetFolderBrowser.getSortDirection(state) == null) {
                orderDirection = AssetFolderBrowser.SORT_ACTION_UP;
            } else {
                orderDirection = assetFolderBrowser.getSortDirection(state);
            }
            final int first = paginator.getFirst(state);
            final int pageSize = paginator.getPageSize(state);

            final long start = System.nanoTime();
            LOGGER.debug("Retrieving table rows...");
            final List<AssetFolderBrowserTableRow> rows = controller
                    .getAssetRows(folder,
                                  orderBy,
                                  orderDirection,
                                  first - 1,
                                  pageSize);

            LOGGER.debug("Retrieve table rows in {} ms.",
                         (System.nanoTime() - start) / 1000);
            return new AssetFolderBrowserTableModel(rows);

        }
    }

}
