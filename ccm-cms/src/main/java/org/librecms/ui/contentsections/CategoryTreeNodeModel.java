/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui.contentsections;

import org.libreccm.categorization.Category;
import org.libreccm.l10n.GlobalizationHelper;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

/**
 * Node of a category tree representing a category.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategoryTreeNodeModel {

    /**
     * The UUID of the category.
     */
    private String uuid;

    /**
     * Is the category the active category.
     */
    private boolean active;

    /**
     * The path of the category.
     */
    private String path;

    /**
     * The title of the category. This value is determined from
     * {@link Category#title} using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     */
    private String title;

    /**
     * The subcategories of the category.
     */
    private List<CategoryTreeNodeModel> subCategories;

    public CategoryTreeNodeModel() {
        subCategories = new ArrayList<>();
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public List<CategoryTreeNodeModel> getSubCategories() {
        return Collections.unmodifiableList(subCategories);
    }

    public void setSubCategories(
        final List<CategoryTreeNodeModel> subCategories
    ) {
        this.subCategories = new ArrayList<>(subCategories);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

}
