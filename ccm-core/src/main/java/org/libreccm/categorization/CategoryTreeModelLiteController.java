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
package org.libreccm.categorization;

import org.hibernate.LazyInitializationException;
import org.libreccm.l10n.GlobalizationHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * CDI bean used as interface between Bebop ({@link CategoryTreeModelLite}) and
 * CDI. The CDI beans primarly takes are care of transactions and avoids 
 * {@link LazyInitializationException}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class CategoryTreeModelLiteController {

    @Inject
    private CategoryManager categoryManager;

    @Inject
    private CategoryRepository categoryRepo;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Transactional(Transactional.TxType.REQUIRED)
    protected boolean hasSubCategories(final long categoryId) {

        final Category category = categoryRepo
            .findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Category with ID %d in the database.",
                    categoryId)));

        return categoryManager.hasSubCategories(category);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<Category> findSubCategories(final long categoryId) {

        final Category category = categoryRepo
            .findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Category with ID %d in the database.",
                    categoryId)));

        return new ArrayList<>(category.getSubCategories());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected String getTitle(final Category ofCategory) {
        Objects.requireNonNull(ofCategory);

        final Category category = categoryRepo
            .findById(ofCategory.getObjectId())
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "No Category with ID %d available.",
                        ofCategory.getObjectId()
                    )
                )
            );
        return globalizationHelper.getValueFromLocalizedString(
            category.getTitle()
        );
    }

}
