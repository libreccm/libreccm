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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * Model for showing the {@link Domain}s (category systems) and categories
 * assigned to a content section.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CategorySystemModel")
public class CategorySystemModel {

    /**
     * List of the category systems/{@link Domain} assigned to the current
     * content section.
     */
    private List<DomainListEntryModel> categorySystems;

    /**
     * The currently selected category system.
     */
    private DomainListEntryModel selectedCategorySystem;

    /**
     * The category tree of the currently selected category system.
     */
    private CategoryTreeNodeModel categoryTree;

    /**
     * The currently selected category.
     */
    private CategoryModel selectedCategory;

    public CategorySystemModel() {
        categorySystems = new ArrayList<>();
    }

    public List<DomainListEntryModel> getCategorySystems() {
        return Collections.unmodifiableList(categorySystems);
    }

    public void setCategorySystems(
        final List<DomainListEntryModel> categorySystems
    ) {
        this.categorySystems = new ArrayList<>(categorySystems);
    }

    public DomainListEntryModel getSelectedCategorySystem() {
        return selectedCategorySystem;
    }

    public void setSelectedCategorySystem(
        final DomainListEntryModel selectedCategorySystem
    ) {
        this.selectedCategorySystem = selectedCategorySystem;
    }

    public CategoryTreeNodeModel getCategoryTree() {
        return categoryTree;
    }

    public void setCategoryTree(final CategoryTreeNodeModel categoryTree) {
        this.categoryTree = categoryTree;
    }

    public CategoryModel getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(final CategoryModel selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

}
