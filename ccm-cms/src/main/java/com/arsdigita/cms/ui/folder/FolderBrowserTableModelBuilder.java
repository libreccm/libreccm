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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.util.LockableImpl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.Folder;

import java.util.List;

/**
 * Creates the {@link TableModel} for the {@link FolderBrowser}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class FolderBrowserTableModelBuilder
    extends LockableImpl
    implements TableModelBuilder {

    private final static Logger LOGGER = LogManager.getLogger(
        FolderBrowserTableModelBuilder.class);

    @Override
    public TableModel makeModel(final Table table,
                                final PageState state) {
        if (!(table instanceof FolderBrowser)) {
            throw new IllegalArgumentException(
                "The FolderBrowserTableModelBuilder can only be used for the "
                    + "FolderBrowser.");
        }
        final FolderBrowser folderBrowser = (FolderBrowser) table;
        final FolderSelectionModel folderSelectionModel = folderBrowser
            .getFolderSelectionModel();
        final Folder folder = folderSelectionModel.getSelectedObject(state);
        if (folder == null) {
            return Table.EMPTY_MODEL;
        } else {
            folderBrowser.getRowSelectionModel().clearSelection(state);
//            final Paginator paginator = folderBrowser.getPaginator();
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final FolderBrowserController controller = cdiUtil
                .findBean(FolderBrowserController.class);
            final String filter = folderBrowser.getFilter(state);
            final String orderBy;
            if (folderBrowser.getSortType(state) == null) {
                orderBy = FolderBrowser.SORT_KEY_NAME;
            } else {
                orderBy = folderBrowser.getSortType(state);
            }
            final String orderDirection;
            if (folderBrowser.getSortDirection(state) == null) {
                orderDirection = FolderBrowser.SORT_ACTION_UP;
            } else {
                orderDirection = folderBrowser.getSortDirection(state);
            }
            final String atozFilter = folderBrowser.getAtoZfilter(state);
//            final int first = paginator.getFirst(state);
//            final int pageSize = paginator.getPageSize(state);

            final String filterTerm;
            if (filter != null && !filter.trim().isEmpty()) {
                filterTerm = String.format("%s%%", filter.trim());
            } else if (atozFilter != null && !atozFilter.trim().isEmpty()) {
                filterTerm = String.format("%s%%", atozFilter.trim());
            } else {
                filterTerm = null;
            }

            final long start = System.currentTimeMillis();
            LOGGER.debug("Retrieving table rows...");
            final List<FolderBrowserTableRow> rows;
            if (filterTerm == null) {
                rows = controller.getObjectRows(folder,
                                                orderBy,
                                                orderDirection);
//                rows = controller.getObjectRows(folder,
//                                                orderBy,
//                                                orderDirection,
//                                                first - 1,
//                                                pageSize);
            } else {
                rows = controller.getObjectRows(folder,
                                                filterTerm,
                                                orderBy,
                                                orderDirection);
//                rows = controller.getObjectRows(folder,
//                                                filterTerm,
//                                                orderBy,
//                                                orderDirection,
//                                                first - 1,
//                                                pageSize);
            }

            LOGGER.debug("Retrieve table rows in {} ms.",
                         System.currentTimeMillis() - start);
            return new FolderBrowserTableModel(rows);
        }
    }

}
