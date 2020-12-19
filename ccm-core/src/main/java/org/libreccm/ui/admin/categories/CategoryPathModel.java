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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model for displaying the path of category in the UI.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategoryPathModel {

    private DomainNodeModel domain;

    private List<CategoryNodeModel> categories;
    
    public CategoryPathModel() {
        categories = new ArrayList<>();
    }

    public DomainNodeModel getDomain() {
        return domain;
    }

    protected void setDomain(final DomainNodeModel domain) {
        this.domain = domain;
    }

    public List<CategoryNodeModel> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    protected void addCategory(final CategoryNodeModel category) {
        categories.add(category);
    }
    
    protected void addCategoryAtBegin(final CategoryNodeModel category) {
        categories.add(0, category);
    }
    
    protected void setCategories(final List<CategoryNodeModel> categories) {
        this.categories = new ArrayList<>(categories);
    }

}
