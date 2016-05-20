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

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.globalization.GlobalizedMessage;

import org.apache.logging.log4j.util.Strings;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.cdi.utils.CdiUtil;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategoryCreateForm extends Form {

    private static final String CATEGORY_NAME = "categoryName";

    private final ParameterSingleSelectionModel<String> selectedCategoryId;

    private final TextField categoryName;
    private final SaveCancelSection saveCancelSection;

    public CategoryCreateForm(
        final CategoriesTab categoriesTab,
        final ParameterSingleSelectionModel<String> selectedCategoryId) {

        super("categoryCreateForm");

        this.selectedCategoryId = selectedCategoryId;

        final Label heading = new Label(e -> {
            final PageState state = e.getPageState();
            final Label target = (Label) e.getTarget();

            final CategoryRepository categoryRepository = CdiUtil
                .createCdiUtil().findBean(CategoryRepository.class);
            final Category category = categoryRepository.findById(Long
                .parseLong(selectedCategoryId.getSelectedKey(state)));
            target.setLabel(new GlobalizedMessage(
                "ui.admin.categories.category.create_new_subcategory",
                ADMIN_BUNDLE,
                new String[]{category.getName()}));
        });
        heading.setClassAttr("heading");
        add(heading);

        categoryName = new TextField(CATEGORY_NAME);
        categoryName.setLabel(new GlobalizedMessage(
            "ui.admin.categories.category.name.label", ADMIN_BUNDLE));
        add(categoryName);

        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);

        addValidationListener(e -> {
            final PageState state = e.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final FormData data = e.getFormData();
                final String categoryNameData = data.getString(CATEGORY_NAME);

                if (Strings.isBlank(categoryNameData)) {
                    data.addError(
                        CATEGORY_NAME,
                        new GlobalizedMessage(
                            "ui.admin.categories.category.name.errors.not_blank",
                            ADMIN_BUNDLE));
                }
            }
        });

        addProcessListener(e -> {
            final PageState state = e.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final CategoryRepository categoryRepository = cdiUtil.findBean(
                    CategoryRepository.class);
                final CategoryManager categoryManager = cdiUtil.findBean(
                    CategoryManager.class);

                final Category selectedCategory = categoryRepository.findById(
                    Long.parseLong(selectedCategoryId.getSelectedKey(state)));

                final FormData data = e.getFormData();
                final String categoryNameData = data.getString(CATEGORY_NAME);

                final Category category = new Category();
                category.setName(categoryNameData);

                categoryRepository.save(category);
                categoryManager.addSubCategoryToCategory(category,
                                                         selectedCategory);

                selectedCategoryId.setSelectedKey(
                    state, Long.toString(category.getObjectId()));
            }

            categoriesTab.hideCategoryCreateForm(state);
        });
    }

}
