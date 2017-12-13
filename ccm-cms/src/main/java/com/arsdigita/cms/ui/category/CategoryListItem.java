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
package com.arsdigita.cms.ui.category;

import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class CategoryListItem implements Comparable<CategoryListItem> {

    private long categoryId;
    private String label;

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(final long categoryId) {
        this.categoryId = categoryId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    @Override
    public int compareTo(final CategoryListItem other) {

        final int result = label.compareTo(other.getLabel());
        if (result == 0) {
            return Long.compare(categoryId, other.getCategoryId());
        } else {
            return result;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (int) (categoryId ^ (categoryId >>> 32));
        hash = 67 * hash + Objects.hashCode(label);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CategoryListItem)) {
            return false;
        }
        final CategoryListItem other = (CategoryListItem) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (categoryId != other.getCategoryId()) {
            return false;
        }
        return Objects.equals(label, other.getLabel());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof CategoryListItem;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "categoryId = %d,"
                                 + "label = \"%s\"%s"
                                 + " }",
                             super.toString(),
                             categoryId,
                             label,
                             data);
    }

}
