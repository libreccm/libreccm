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

import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategoryNodeModel implements Comparable<CategoryNodeModel> {

    private long categoryId;

    private String uuid;

    private String uniqueId;

    private String name;

    private String path;

    private boolean enabled;

    private boolean visible;

    private boolean abstractCategory;

    private long categoryOrder;

    public long getCategoryId() {
        return categoryId;
    }

    protected void setCategoryId(final long categoryId) {
        this.categoryId = categoryId;
    }

    public String getIdentifier() {
        return String.format("ID-%d", categoryId);
    }

    public String getUuid() {
        return uuid;
    }

    protected void setUuid(final String uuid) {
        this.uuid = uuid;
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

    public String getPath() {
        return path;
    }

    protected void setPath(final String path) {
        this.path = path;
    }

    public long getCategoryOrder() {
        return categoryOrder;
    }

    protected void setCategoryOrder(final long categoryOrder) {
        this.categoryOrder = categoryOrder;
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
