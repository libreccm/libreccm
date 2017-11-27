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
package org.librecms.pages;

import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import static org.librecms.pages.PagesConstants.*;

/**
 * Manager for {@link Page} entities.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class PageManager {

    @Inject
    private CategoryManager categoryManager;

    @Inject
    private CategoryRepository categoryRepo;

    @Inject
    private PageRepository pageRepo;

    /**
     * Finds the {@link Page} associated with an {@link Category}. If there is
     * no {@link Page} associated with the provided {@link Category} this method
     * will return the {@link Page} associated with the parent category.
     *
     * @param category The {@link Category} which is associated with the
     *                 {@link Page}.
     *
     * @return The {@link Page} associated with the provided {@code category}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Page findPageForCategory(final Category category) {

        final Optional<Page> page = pageRepo.findPageForCategory(category);

        if (page.isPresent()) {
            return page.get();
        } else {
            final Category forCategory = categoryRepo
                .findById(category.getObjectId())
                .orElseThrow(() -> new IllegalArgumentException(String
                .format("No Category with ID %d in the database.",
                        category.getObjectId())));

            if (forCategory.getParentCategory() == null) {
                return new Page();
            } else {
                return findPageForCategory(forCategory.getParentCategory());
            }
        }
    }

    /**
     * Creates a new {@link Page} entity for a {@link Category} if there is no
     * page for that category yet.
     *
     * @param category The category for which the new {@link Page} is created.
     *
     * @return A new {@link Page} if there is no {@link Page} for the provided 
     * {@code category}. If there is already a {@link Page} for that category
     * the existing {@link Page} is returned.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Page createPageForCategory(final Category category) {

        final Optional<Page> pageForCategory = pageRepo
            .findPageForCategory(category);

        if (pageForCategory.isPresent()) {
            return pageForCategory.get();
        } else {
            final Page page = new Page();
            pageRepo.save(page);
            categoryManager.addObjectToCategory(page,
                                                category,
                                                CATEGORIZATION_TYPE_PAGE_CONF);

            return page;
        }
    }

}
