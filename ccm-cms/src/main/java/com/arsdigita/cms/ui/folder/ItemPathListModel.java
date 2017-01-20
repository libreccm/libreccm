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

import com.arsdigita.bebop.list.ListModel;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.Folder;

import java.util.Iterator;

/**
 * Model for {@link ItemPath}. This was originally a inner class.
 *
 * @author <a href="mailto:lutter@arsdigita.com">David Lutterkort</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class ItemPathListModel implements ListModel {

    private final Iterator<Folder> pathFolders;
    private Folder currentFolder;

    public ItemPathListModel(final ContentItem item) {
        pathFolders = CdiUtil.createCdiUtil().findBean(
            ContentItemManager.class).getItemFolders(item).iterator();
    }

    @Override
    public boolean next() {
        if (pathFolders.hasNext()) {
            currentFolder = pathFolders.next();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object getElement() {
        return currentFolder.getName();
    }

    @Override
    public String getKey() {
        return Long.toString(currentFolder.getObjectId());
    }

}
