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
public class CategoryTableRow implements Comparable<CategoryTableRow> {

    private long categoryId;

    private String uuid;

    private String uniqueId;

    private String name;

    private boolean enabled;

    private boolean visible;

    private boolean abstractCategory;

    private long categoryOrder;

    public CategoryTableRow() {
        super();
    }
    
    public CategoryTableRow(final Category category) {
        categoryId = category.getObjectId();
        uuid = category.getUuid();
        uniqueId = category.getUniqueId();
        name = category.getName();
        enabled = category.isEnabled();
        visible = category.isVisible();
        abstractCategory = category.isAbstractCategory();
        categoryOrder = category.getCategoryOrder();
    }
    
    public long getCategoryId() {
        return categoryId;
    }

    protected void setCategoryId(final long categoryId) {
        this.categoryId = categoryId;
    }

    public String getUuid() {
        return uuid;
    }

    protected void setUuid(final String uuid) {
        this.uuid = uuid;
    }
    
    public String getIdentifier() {
        return String.format("UUID-%s", uuid);
    }

    public String getUniqueId() {
        return uniqueId;
    }

    protected void setUniqueId(final String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getName() {
        return name;
    }

    protected void setName(final String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    protected void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isVisible() {
        return visible;
    }

    protected void setVisible(final boolean visible) {
        this.visible = visible;
    }

    public boolean isAbstractCategory() {
        return abstractCategory;
    }

    protected void setAbstractCategory(final boolean abstractCategory) {
        this.abstractCategory = abstractCategory;
    }

    public long getCategoryOrder() {
        return categoryOrder;
    }

    protected void setCategoryOrder(final long categoryOrder) {
        this.categoryOrder = categoryOrder;
    }

    @Override
    public int compareTo(final CategoryTableRow other) {
        return Long.compare(
            categoryOrder, Objects.requireNonNull(other).getCategoryOrder()
        );
    }

}
