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

import java.util.List;

import com.arsdigita.bebop.list.ListModel;

import java.util.Iterator;

/**
 * A {@link ListModel} that iterates over categories via a cursor.
 *
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class CategoryListModel implements ListModel {

    private final Iterator<CategoryListItem> iterator;
    private CategoryListItem currentCategory;
    private Long excludedCategoryId;

    /**
     * Constructs a new <code>CategoryListModel</code>
     *
     * @param categories
     */
    public CategoryListModel(final List<CategoryListItem> categories) {
        this(categories, null);
    }

    /**
     * Constructs a new <code>CategoryListModel</code>
     */
    public CategoryListModel(final List<CategoryListItem> categories,
                             final Long excludedCategoryId) {

        iterator = categories.iterator();
        this.excludedCategoryId = excludedCategoryId;
    }

    @Override
    public boolean next() {
        if (iterator.hasNext()) {

            final CategoryListItem next = iterator.next();
            if (excludedCategoryId != null
                    && next.getCategoryId() == excludedCategoryId) {

                return next();
            } else {
                currentCategory = next;
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * Reads the label of the {@link CategoryListItem}.
     *
     *
     */
    @Override
    public Object getElement() {
        return currentCategory.getLabel();
    }

    @Override
    public String getKey() {
        return Long.toString(currentCategory.getCategoryId());
    }

}
