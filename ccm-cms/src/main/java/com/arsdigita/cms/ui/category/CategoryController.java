/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.category;

import com.arsdigita.kernel.KernelConfig;

import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.LocalizedString;

import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Service methods for the Category Admin Tab in the Content Center.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class CategoryController {

    @Inject
    private CategoryManager categoryManager;

    @Inject
    private CategoryRepository categoryRepo;

    @Inject
    private ConfigurationManager confManager;

    /**
     * Creates a new category.
     * 
     * @param parentCategory The parent category of the new category.
     * @param name The name of the new category (URL fragment).
     * @param description A description of the new category-
     * @param isAbstract Is the new category abstract?
     * @return The new category.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected Category createCategory(final Category parentCategory,
                                   final String name,
                                   final String description,
                                   final boolean isAbstract) {

        Objects.requireNonNull(parentCategory);
        Objects.requireNonNull(name);
        
        if (name.isEmpty() || name.matches("\\s*")) {
            throw new IllegalArgumentException(
                "The name of a category can't be empty.");
        }
        
        final KernelConfig kernelConfig = confManager
            .findConfiguration(KernelConfig.class);

        final Category category = new Category();
        category.setName(name);
        final LocalizedString localizedDescription = new LocalizedString();
        localizedDescription.addValue(kernelConfig.getDefaultLocale(),
                                      description);
        category.setDescription(localizedDescription);
        category.setAbstractCategory(isAbstract);

        categoryRepo.save(category);
        categoryManager.addSubCategoryToCategory(category, parentCategory);

        return category;
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    protected Optional<Category> getParentCategory(final Category forCategory) {
        
        Objects.requireNonNull(forCategory);
        
        final Category category = categoryRepo
            .findById(forCategory.getObjectId())
        .orElseThrow(() -> new IllegalArgumentException(String
            .format("No category with ID %d in the database.",
                    forCategory.getObjectId())));
        
        if (category.getParentCategory() == null) {
            return Optional.empty();
        } else {
            return categoryRepo
                .findById(category.getParentCategory().getObjectId());
        }
    }

}
