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

/**
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

    @Transactional(Transactional.TxType.REQUIRED)
    public Page createPageForCategory(final Category category) {

        final Page page = new Page();
        pageRepo.save(page);
        categoryManager.addObjectToCategory(page, category);

        return page;
    }

}
