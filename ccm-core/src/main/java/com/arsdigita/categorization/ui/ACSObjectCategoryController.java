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
package com.arsdigita.categorization.ui;

import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.categorization.ObjectNotAssignedToCategoryException;
import org.libreccm.core.CcmObject;
import org.libreccm.core.CcmObjectRepository;

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
class ACSObjectCategoryController {

    @Inject
    private CategoryRepository categoryRepo;
    
    @Inject
    private CategoryManager categoryManager;
    
    @Inject
    private CcmObjectRepository ccmObjectRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<Category> getCategoriesForObject(final CcmObject object) {

        Objects.requireNonNull(object);

        final CcmObject ccmObject = ccmObjectRepo
            .findById(object.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No CcmObject with ID %d in the database",
                    object.getObjectId())));

        return ccmObject
            .getCategories()
            .stream()
            .map(categorization -> categorization.getCategory())
            .collect(Collectors.toList());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void addObjectToCategory(final CcmObject object,
                                       final Category category) {

        Objects.requireNonNull(object);
        Objects.requireNonNull(category);

        final CcmObject ccmObject = ccmObjectRepo
            .findById(object.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No CcmObject with ID %d in the database",
                    object.getObjectId())));

        final Category cat = categoryRepo
        .findById(category.getObjectId())
        .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Category with ID %d in the database.",
                    category.getObjectId())));
        
        categoryManager.addObjectToCategory(ccmObject, cat);
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    protected void removeObjectFromCategory(final CcmObject object,
                                            final Category category) 
        throws ObjectNotAssignedToCategoryException {
        
        Objects.requireNonNull(object);
        Objects.requireNonNull(category);

        final CcmObject ccmObject = ccmObjectRepo
            .findById(object.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No CcmObject with ID %d in the database",
                    object.getObjectId())));

        final Category cat = categoryRepo
        .findById(category.getObjectId())
        .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Category with ID %d in the database.",
                    category.getObjectId())));
        
        categoryManager.removeObjectFromCategory(ccmObject, cat);
    }

}
