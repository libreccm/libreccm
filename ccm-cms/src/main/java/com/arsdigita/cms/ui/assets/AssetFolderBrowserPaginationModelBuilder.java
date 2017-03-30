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
import com.arsdigita.bebop.PaginationModelBuilder;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.cms.ui.folder.FolderSelectionModel;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.Folder;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class AssetFolderBrowserPaginationModelBuilder implements PaginationModelBuilder {

    private final AssetFolderBrowser folderBrowser;
    
    public AssetFolderBrowserPaginationModelBuilder(
            final AssetFolderBrowser folderBrowser) {
        
        this.folderBrowser = folderBrowser;
    }
    
    @Override
    public int getTotalSize(final Paginator paginator, final PageState state) {
        
        final FolderSelectionModel folderSelectionModel = folderBrowser
                .getFolderSelectionModel();
        final Folder folder = folderSelectionModel.getSelectedObject(state);
        if (folder == null) {
            return 0;
        } else {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final AssetFolderBrowserController controller = cdiUtil.findBean(
                    AssetFolderBrowserController.class);
            return (int) controller.countObjects(folder);
        }
    }

    @Override
    public boolean isVisible(final PageState state) {
        return folderBrowser != null && folderBrowser.isVisible(state);
    }
    
    
    
}
