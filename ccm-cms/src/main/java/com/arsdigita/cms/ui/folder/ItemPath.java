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

import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.util.LockableImpl;
import org.libreccm.categorization.Category;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;

/**
 * Produce a list of the items starting from the selected item's root down to
 * the item itself.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @author <a href="mailto:lutter@arsdigita.com">David Lutterkort</a>
 */
public class ItemPath extends List {

    public ItemPath(ItemSelectionModel folderSel) {
        super(new ItemPathListModelBuilder(folderSel));
        setAttribute("type", "item-path");
        setSelectionModel(folderSel);
    }

    public static class ItemPathListModel implements ListModel {

        private final java.util.List<Category> pathFolders;
        private int index = -1;

        public ItemPathListModel(final ContentItem item) {
            pathFolders = CdiUtil.createCdiUtil().findBean(ContentItemManager.class).getItemFolders(item);
        }

        @Override
        public boolean next() {
            index++;
            return index < pathFolders.size();
        }

        public Object getElement() {
            return pathFolders.get(index).getName();
        }

        public String getKey() {
            return Long.toString(pathFolders.get(index).getObjectId());
        }
    }

    public static class ItemPathListModelBuilder extends LockableImpl
            implements ListModelBuilder {

        ItemSelectionModel m_itemSel;

        public ItemPathListModelBuilder(ItemSelectionModel itemSel) {
            m_itemSel = itemSel;
        }

        public com.arsdigita.bebop.list.ListModel makeModel(List l,
                                                            final PageState s) {
            return new ItemPathListModel((ContentItem) m_itemSel.
                    getSelectedObject(s));
        }
    }
}
