/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.category;

import com.arsdigita.bebop.list.ListModel;
import org.librecms.contentsection.ContentItem;

import java.util.Iterator;
import java.util.List;

/**
 * A {@link ListModel} that iterates over content items via a cursor.
 *
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 */
public final class ContentItemListModel implements ListModel {

    private ContentItem m_contentItem;
    private long m_excludedID;
    private Iterator<ContentItem> iterator;


    /**
     * Constructs a new <code>ContentItemListModel</code>
     */
    public ContentItemListModel(List<ContentItem> coll) {
        this(coll, -1); //Hopefully a decent replacement for null in BigDecimal. Negative ids would be weird...
    }

    /**
     * Constructs a new <code>ContentItemListModel</code>
     */
    public ContentItemListModel(List<ContentItem> coll,
                                long excludedID) {

        m_excludedID = excludedID;
        m_contentItem = null;
        iterator = coll.iterator();
    }

    public boolean next() {
        if (iterator.hasNext()) {
            final ContentItem contentItem = iterator.next();
            if (Long.parseLong(contentItem.getItemUuid()) == m_excludedID) {
                return next();
            } else {
                m_contentItem = contentItem;
                return true;
            }
        } else {
            return false;
        }
    }

    private ContentItem getContentItem() {
        if ( m_contentItem == null ) {
            throw new IllegalStateException("call next() first");
        }
        return m_contentItem;
    }

    /**
     * Reads the name of the content item.
     *
     */
    public Object getElement() {
        return getContentItem().getName();
    }

    public String getKey() {
        return getContentItem().getItemUuid();
    }
}
