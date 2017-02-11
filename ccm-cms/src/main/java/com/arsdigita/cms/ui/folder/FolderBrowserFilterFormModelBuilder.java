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

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.Folder;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class FolderBrowserFilterFormModelBuilder 
    implements FolderManipulator.FilterFormModelBuilder{

    private final FolderBrowser folderBrowser;
    
    public FolderBrowserFilterFormModelBuilder(final FolderBrowser folderBrowser) {
        this.folderBrowser = folderBrowser;
    }
    
    @Override
    public long getFolderSize(final PageState state) {
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

            final String filterTerm;
            if (filter != null && !filter.trim().isEmpty()) {
                filterTerm = filter.trim();
            } else if (atozFilter != null && !atozFilter.trim().isEmpty()) {
                filterTerm = atozFilter.trim();
            } else {
                filterTerm = null;
            }

            if (filterTerm == null) {
                return controller.countObjects(folder);
            } else {
                return controller.countObjects(folder);
            }
        }
    }
    
    
    
}
