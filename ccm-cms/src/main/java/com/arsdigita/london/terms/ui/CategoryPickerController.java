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
package com.arsdigita.london.terms.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.Categorization;
import org.libreccm.categorization.CategorizationConstants;
import org.libreccm.categorization.CategorizationMarshaller;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainManager;
import org.libreccm.categorization.DomainRepository;
import org.libreccm.categorization.ObjectNotAssignedToCategoryException;
import org.libreccm.core.CcmObject;
import org.libreccm.core.CcmObjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class CategoryPickerController {

    private final static Logger LOGGER = LogManager
        .getLogger(CategoryPickerController.class);

    @Inject
    private CategoryManager categoryManager;

    @Inject
    private CategoryRepository categoryRepo;

    @Inject
    private CcmObjectRepository ccmObjectRepo;

    @Inject
    private DomainRepository domainRepo;

    @Inject
    private DomainManager domainManager;

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<Category> getCurrentCategories(final Domain domain,
                                                  final CcmObject object) {

        Objects.requireNonNull(domain);
        Objects.requireNonNull(object);

        final Domain catDomain = domainRepo
            .findById(domain.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Domain with ID %d in the database.",
                    domain.getObjectId())));

        final CcmObject ccmObject = ccmObjectRepo
            .findById(object.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No CcmObject with ID %d in the database.",
                    object.getObjectId())));

        final Category root = catDomain.getRoot();
        return collectAssignedCategories(ccmObject, root);
    }

    private List<Category> collectAssignedCategories(final CcmObject object,
                                                     final Category root) {

        final List<Category> categories = new ArrayList<>();
        if (categoryManager.isAssignedToCategory(root, object)) {
            categories.add(root);
        }

        if (!root.getSubCategories().isEmpty()) {
            for (final Category subCategory : root.getSubCategories()) {
                categories.addAll(collectAssignedCategories(object,
                                                            subCategory));
            }
        }

        return categories;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void clearCategories(final Domain domain, final CcmObject object) {

        Objects.requireNonNull(domain);
        Objects.requireNonNull(object);

        final Domain catDomain = domainRepo
            .findById(domain.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Domain with ID %d in the database.",
                    domain.getObjectId())));

        final CcmObject ccmObject = ccmObjectRepo
            .findById(object.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No CcmObject with ID %d in the database.",
                    object.getObjectId())));

        final Category root = catDomain.getRoot();
        final List<Category> assignedCategories = collectAssignedCategories(
            ccmObject, root);

        for (final Category category : assignedCategories) {
            try {
                categoryManager.removeObjectFromCategory(ccmObject, category);
            } catch (ObjectNotAssignedToCategoryException ex) {
                LOGGER.warn("Tried to remove category {} from object {} but "
                                + "the object was not assigned to that category.",
                            Objects.toString(category),
                            Objects.toString(ccmObject));
            }
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void assignCategories(final List<Category> categories,
                                    final CcmObject object) {

        Objects.requireNonNull(categories);
        Objects.requireNonNull(object);

        final CcmObject ccmObject = ccmObjectRepo
            .findById(object.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No CcmObject with ID %d in the database.",
                    object.getObjectId())));

        for (final Category category : categories) {

            final Category cat = categoryRepo
                .findById(category.getObjectId())
                .orElseThrow(() -> new IllegalArgumentException(String
                .format("No Category with ID %d in the database.",
                        category.getObjectId())));
            categoryManager.addObjectToCategory(ccmObject, category, "");
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected Category getDomainModelCategory(final Domain domain) {

        Objects.requireNonNull(domain);

        final Domain catDomain = domainRepo
            .findById(domain.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Domain with ID %d in the database.",
                    domain.getObjectId())));

        return catDomain.getRoot();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<Category> getRootCategories(final Domain domain) {

        return getDomainModelCategory(domain).getSubCategories();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<Category> getSubCategories(final Category category) {

        Objects.requireNonNull(category);

        final Category cat = categoryRepo
            .findById(category.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Category with ID %d in the database.",
                    category.getObjectId())));
        
        return cat.getSubCategories();
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    protected Category getParentCategory(final Category category) {
        
        Objects.requireNonNull(category);

        final Category cat = categoryRepo
            .findById(category.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Category with ID %d in the database.",
                    category.getObjectId())));
        
        return cat.getParentCategory();
    }

}
