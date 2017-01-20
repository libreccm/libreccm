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

import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.util.LockableImpl;

import org.librecms.contentsection.Folder;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class FolderPathListModelBuilder extends LockableImpl
    implements ListModelBuilder {

    private final FolderSelectionModel folderSelectionModel;

    public FolderPathListModelBuilder(
        final FolderSelectionModel folderSelectionModel) {

        this.folderSelectionModel = folderSelectionModel;
    }

    @Override
    public ListModel makeModel(final List list, final PageState state) {

        return new FolderPathListModel(folderSelectionModel
            .getSelectedObject(state));
    }

}
