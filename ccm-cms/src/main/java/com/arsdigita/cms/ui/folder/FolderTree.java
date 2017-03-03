/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.ui.folder;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Tree;

import org.librecms.CmsConstants;

import static org.librecms.CmsConstants.*;

public class FolderTree extends Tree {

    public FolderTree(final FolderSelectionModel folderSel) {
        super(new FolderTreeModelBuilder());
        setSelectionModel(folderSel);
    }

    @Override
    public void setSelectedKey(final PageState state, final Object key) {
        if (key instanceof String) {
            final Long keyAsLong;
            if (((String) key).startsWith(
                FOLDER_BROWSER_KEY_PREFIX_FOLDER)) {
                keyAsLong = Long.parseLong(((String) key).substring(
                    FOLDER_BROWSER_KEY_PREFIX_FOLDER.length()));
            } else {
                keyAsLong = Long.parseLong((String) key);
            }
            super.setSelectedKey(state, keyAsLong);
        } else if (key instanceof Long) {
            super.setSelectedKey(state, key);
        } else {
            //We now that a FolderSelectionModel only takes keys of type Long.
            //Therefore we try to cast here
            final Long keyAsLong = (Long) key;
            super.setSelectedKey(state, keyAsLong);
        }
    }

}
