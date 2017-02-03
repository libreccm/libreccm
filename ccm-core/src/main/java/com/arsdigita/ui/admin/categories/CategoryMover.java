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

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.Tree;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.cdi.utils.CdiUtil;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategoryMover extends Form {

    private final CategoriesTab categoriesTab;
    private final ParameterSingleSelectionModel<String> selectedDomainId;
    private final ParameterSingleSelectionModel<String> selectedCategoryId;

    private final Tree categoryTree;
    private final SaveCancelSection saveCancelSection;

    public CategoryMover(
        final CategoriesTab categoriesTab,
        final ParameterSingleSelectionModel<String> selectedDomainId,
        final ParameterSingleSelectionModel<String> selectedCategoryId) {

        super("categoryMover", new BoxPanel(BoxPanel.VERTICAL));

        this.categoriesTab = categoriesTab;
        this.selectedDomainId = selectedDomainId;
        this.selectedCategoryId = selectedCategoryId;

        final Label heading = new Label(e -> {
            final PageState state = e.getPageState();
            final Label target = (Label) e.getTarget();

            final CategoryRepository categoryRepo = CdiUtil
                .createCdiUtil().findBean(CategoryRepository.class);
            final Category category = categoryRepo.findById(Long.parseLong(
                selectedCategoryId.getSelectedKey(state))).get();

            target.setLabel(new GlobalizedMessage(
                "ui.admin.categories.category.move.heading",
                ADMIN_BUNDLE,
                new String[]{category.getName()}));
        });
        heading.setClassAttr("heading");
        add(heading);

        categoryTree = new Tree(new CategoryMoverModelBuilder(
            selectedDomainId, selectedCategoryId));
        add(categoryTree);

        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);

        addProcessListener(e -> {
            final PageState state = e.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final CategoryRepository categoryRepo = cdiUtil.findBean(
                    CategoryRepository.class);
                final CategoryManager categoryManager = cdiUtil.findBean(
                    CategoryManager.class);

                final Category category = categoryRepo.findById(Long.parseLong(
                    selectedCategoryId.getSelectedKey(state))).get();
                final Category parent = category.getParentCategory();
                final Category target = categoryRepo.findById(Long.parseLong(
                    (String) categoryTree.getSelectedKey(state))).get();
                
                categoryManager.removeSubCategoryFromCategory(category, parent);
                categoryManager.addSubCategoryToCategory(category, target);
            }

            categoriesTab.hideCategoryMover(state);
        });
    }

}
