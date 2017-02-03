/*
 * Copyright (C) 2016 LibreCCM Foundation.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class TestCategoryController {

    @Inject
    private CategoryRepository categoryRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    public Map<String, List<String>> getData(final long categoryId) {

        final Category category = categoryRepo.findById(categoryId).get();
        if (category == null) {
            throw new IllegalArgumentException(String.format(
                "No category for id %d.", categoryId));
        }

        final Map<String, List<String>> result = new HashMap<>();
        final List<String> subCategories = new ArrayList<>();
        final List<String> objects = new ArrayList<>();
        result.put("subCategories", subCategories);
        result.put("objects", objects);

        category.getSubCategories().forEach(
            subCategory -> subCategories.add(subCategory.getDisplayName()));

        category.getObjects().forEach(
            object -> objects
            .add(object.getCategorizedObject().getDisplayName()));

        return result;
    }

}
