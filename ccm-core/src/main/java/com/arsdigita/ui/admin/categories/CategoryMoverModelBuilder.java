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
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.bebop.tree.TreeModelBuilder;
import com.arsdigita.util.LockableImpl;

import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainRepository;
import org.libreccm.cdi.utils.CdiUtil;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategoryMoverModelBuilder
    extends LockableImpl
    implements TreeModelBuilder {

    private final ParameterSingleSelectionModel<String> selectedDomainId;
    private final ParameterSingleSelectionModel<String> selectedCategoryId;

    public CategoryMoverModelBuilder(
        final ParameterSingleSelectionModel<String> selectedDomainId,
        final ParameterSingleSelectionModel<String> selectedCategoryId) {

        this.selectedDomainId = selectedDomainId;
        this.selectedCategoryId = selectedCategoryId;

    }

    @Override
    public TreeModel makeModel(final Tree tree, final PageState state) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();

        final DomainRepository domainRepository = cdiUtil.findBean(
            DomainRepository.class);
        final Domain domain = domainRepository.findById(Long.parseLong(
            selectedDomainId.getSelectedKey(state))).get();

        final CategoryRepository categoryRepository = cdiUtil.findBean(
            CategoryRepository.class);
        final Category category = categoryRepository.findById(Long.parseLong(
            selectedCategoryId.getSelectedKey(state))).get();
        
        return new CategoryMoverModel(domain, category);
    }

}
