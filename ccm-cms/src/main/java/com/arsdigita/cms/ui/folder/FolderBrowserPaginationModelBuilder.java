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
import com.arsdigita.bebop.PaginationModelBuilder;
import com.arsdigita.bebop.Paginator;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.Folder;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class FolderBrowserPaginationModelBuilder implements PaginationModelBuilder {

    private final FolderBrowser folderBrowser;

    public FolderBrowserPaginationModelBuilder(final FolderBrowser folderBrowser) {
        this.folderBrowser = folderBrowser;
    }

    @Override
    public int getTotalSize(final Paginator paginator,
                            final PageState state) {
        final FolderSelectionModel folderSelectionModel = folderBrowser
            .getFolderSelectionModel();
        final Folder folder = folderSelectionModel.getSelectedObject(state);
        if (folder == null) {
            return 0;
        } else {
            folderBrowser.getRowSelectionModel().clearSelection(state);
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

            if (filterTerm == null) {
                return (int) controller.countObjects(folder);
            } else {
                return (int) controller.countObjects(folder, 
                                                     filter);
            }
        }
    }

    @Override
    public boolean isVisible(final PageState state) {
        return folderBrowser != null && folderBrowser.isVisible(state);
    }

}
