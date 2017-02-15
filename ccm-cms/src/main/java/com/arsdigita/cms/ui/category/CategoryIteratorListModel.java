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
package com.arsdigita.cms.ui.category;

import com.arsdigita.bebop.list.ListModel;
import org.libreccm.categorization.Category;

import java.util.Iterator;

/**
 * A {@link ListModel} that iterates over categories via an iterator
 *
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 */
public class CategoryIteratorListModel implements ListModel {

    private Iterator m_iter;
    private Category m_cat;

    /**
     * Construct a new <code>CategoryIteratorListModel</code>
     *
     * @param iter an {@link Iterator} over all the categories
     *   which this model will supply
     */
    public CategoryIteratorListModel(Iterator iter) {
        m_iter = iter;
        m_cat = null;
    }

    public boolean next() {
        if(m_iter.hasNext()) {
            m_cat = (Category)m_iter.next();
            return true;
        } else {
            return false;
        }
    }

    public Object getElement() {
        return m_cat.getName();
    }

    public String getKey() {
        return m_cat.getUniqueId();
    }
}
