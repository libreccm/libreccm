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
package com.arsdigita.ui.admin.categories;

import org.libreccm.categorization.Categorization;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class CategoriesController {

    @Inject
    private CategoryRepository categoryRepo;

    @Inject
    private CategoryManager categoryManager;

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<Category> getSubCategories(final Category ofCategory) {

        final Category category = categoryRepo
            .findById(ofCategory.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Category with ID %d in the database.",
                    ofCategory.getObjectId())));

        return new ArrayList<>(category.getSubCategories());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected boolean hasChildren(final Category category) {
        
        final Category categoryToCheck = categoryRepo
            .findById(category.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Category with ID %d in the database.",
                    category.getObjectId())));
        
        return categoryManager.hasSubCategories(categoryToCheck);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected boolean isDeletable(final Category category) {

        final Category categoryToCheck = categoryRepo
            .findById(category.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Category with ID %d in the database.",
                    category.getObjectId())));

        final List<Category> subCats = categoryToCheck.getSubCategories();
        final List<Categorization> objects = categoryToCheck.getObjects();

        return (subCats == null || subCats.isEmpty())
                   && (objects == null || objects.isEmpty());
    }

}
