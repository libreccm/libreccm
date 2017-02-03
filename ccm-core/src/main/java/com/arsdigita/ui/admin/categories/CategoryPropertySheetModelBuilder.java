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
package com.arsdigita.ui.admin.categories;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.PropertySheetModel;
import com.arsdigita.bebop.PropertySheetModelBuilder;
import com.arsdigita.util.LockableImpl;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.cdi.utils.CdiUtil;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategoryPropertySheetModelBuilder
        extends LockableImpl
        implements PropertySheetModelBuilder {

    private final ParameterSingleSelectionModel<String> selectedCategoryId;

    public CategoryPropertySheetModelBuilder(
            final ParameterSingleSelectionModel<String> selectedCategoryId) {

        this.selectedCategoryId = selectedCategoryId;

    }

    @Override
    public PropertySheetModel makeModel(final PropertySheet sheet,
                                        final PageState state) {
        final String categoryIdStr = selectedCategoryId.getSelectedKey(state);
        final Category selectedCategory;

        if (categoryIdStr == null || categoryIdStr.isEmpty()) {
            selectedCategory = null;
        } else {
            final CategoryRepository categoryRepository = CdiUtil.
                    createCdiUtil().findBean(CategoryRepository.class);
            selectedCategory = categoryRepository.findById(Long.parseLong(
                    categoryIdStr)).get();
        }
        
        return new CategoryPropertySheetModel(selectedCategory);
    }

}
