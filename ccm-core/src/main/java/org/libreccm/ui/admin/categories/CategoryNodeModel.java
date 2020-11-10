/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.ui.admin.categories;

import org.libreccm.categorization.Category;

import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategoryNodeModel implements Comparable<CategoryNodeModel> {

    private final long categoryId;

    private final String uuid;

    private final String uniqueId;

    private final String name;

    private final long categoryOrder;

    public CategoryNodeModel(final Category category) {
        Objects.requireNonNull(category);
        categoryId = category.getObjectId();
        uuid = category.getUuid();
        uniqueId = category.getUniqueId();
        categoryOrder = category.getCategoryOrder();
        name = category.getName();
    }

    public long getCategoryId() {
        return categoryId;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }

    public long getCategoryOrder() {
        return categoryOrder;
    }

    @Override
    public int compareTo(final CategoryNodeModel other) {
        int result = Long.compare(
            categoryOrder,
            Objects.requireNonNull(other).getCategoryOrder()
        );
        
        if (result == 0) {
            result = Objects.compare(
                name, 
                Objects.requireNonNull(other).getName(), 
                String::compareTo
            );
        }
        
        return result;
    }

}
