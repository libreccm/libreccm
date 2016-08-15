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
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.util.LockableImpl;

/**
 * Produce a list of the items starting from the selected item's root down
 * to the item itself.
 *
 * @author <a href="mailto:lutter@arsdigita.com">David Lutterkort</a>
 * @version $Id: ItemPath.java 1940 2009-05-29 07:15:05Z terry $
 */
public class ItemPath extends List {

    public ItemPath(ItemSelectionModel folderSel) {
        super(new ListModelBuilder(folderSel));
        setAttribute("type", "item-path");
        setSelectionModel(folderSel);
    }

    public static class ListModel
        implements com.arsdigita.bebop.list.ListModel {
        ItemCollection m_coll;

        public ListModel(ContentItem i) {
            m_coll = i.getPathInfo(true);
        }

        public boolean next() {
            return m_coll.next();
        }

        public Object getElement() {
            return m_coll.getName();
        }

        public String getKey() {
            return m_coll.getID().toString();
        }
    }

    public static class ListModelBuilder extends LockableImpl
        implements com.arsdigita.bebop.list.ListModelBuilder {

        ItemSelectionModel m_itemSel;

        public ListModelBuilder(ItemSelectionModel itemSel) {
            m_itemSel = itemSel;
        }

        public com.arsdigita.bebop.list.ListModel makeModel(List l, final PageState s) {
            return new ListModel((ContentItem) m_itemSel.getSelectedObject(s));
        }
    }
}
