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

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.Folder;

import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class FolderBrowserTableModelBuilder extends LockableImpl
    implements TableModelBuilder {

    @Override
    public TableModel makeModel(final Table table,
                                final PageState state) {
        if (!(table instanceof FolderBrowser)) {
            throw new IllegalArgumentException(
                "The FolderBrowserTableModelBuilder can be used for the FolderBrowser.");
        }
        final FolderBrowser folderBrowser = (FolderBrowser) table;
        final FolderSelectionModel folderSelectionModel = folderBrowser
            .getFolderSelectionModel();
        final Folder folder = folderSelectionModel.getSelectedObject(state);
        if (folder == null) {
            return Table.EMPTY_MODEL;
        } else {
            folderBrowser.getRowSelectionModel().clearSelection(state);
            final Paginator paginator = folderBrowser.getPaginator();
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final FolderBrowserController controller = cdiUtil.findBean(
                FolderBrowserController.class);
            final String filter = folderBrowser.getFilter(state);
            final String atozFilter = folderBrowser.getAtoZfilter(state);
            final int first = paginator.getFirst(state);
            final int pageSize = paginator.getPageSize(state);

            final String filterTerm;
            if (filter != null && !filter.trim().isEmpty()) {
                filterTerm = filter.trim();
            } else if (atozFilter != null && !atozFilter.trim().isEmpty()) {
                filterTerm = atozFilter.trim();
            } else {
                filterTerm = null;
            }

            final List<FolderBrowserTableRow> rows;
            if (filterTerm == null) {
                rows = controller.getObjectRows(folder, first, pageSize);
            } else {
                rows = controller.getObjectRows(folder, filter, first, pageSize);
            }
            
            
            return new FolderBrowserTableModel(rows);
        }
    }
    
    

}
