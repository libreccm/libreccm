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

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.util.UncheckedWrapperException;
import java.util.HashSet;
import java.util.Set;
import java.util.TooManyListenersException;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.cdi.utils.CdiUtil;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class CategoryDetails extends SegmentedPanel {

    private final CategoriesTab categoriesTab;
    private final ParameterSingleSelectionModel<String> selectedDomainId;
    private final ParameterSingleSelectionModel<String> selectedCategoryId;
    private final ParameterSingleSelectionModel<String> selectedLanguage;
    private final CategoryTitleAddForm categoryTitleAddForm;
    private final CategoryDescriptionAddForm categoryDescriptionAddForm;
    private final SubCategoriesTable subCategoriesTable;

    public CategoryDetails(
        final CategoriesTab categoriesTab,
        final ParameterSingleSelectionModel<String> selectedDomainId,
        final ParameterSingleSelectionModel<String> selectedCategoryId,
        final ParameterSingleSelectionModel<String> selectedLanguage) {

        this.categoriesTab = categoriesTab;
        this.selectedDomainId = selectedDomainId;
        this.selectedCategoryId = selectedCategoryId;
        this.selectedLanguage = selectedLanguage;

        final ActionLink backLink = new ActionLink(new GlobalizedMessage(
            "ui.admin.categories.category_details.back", ADMIN_BUNDLE));
        backLink.addActionListener(e -> {
            final PageState state = e.getPageState();
            categoriesTab.hideCategoryDetails(state);
        });
        addSegment("category-details-back", backLink);

        final Label heading = new Label(e -> {
            final PageState state = e.getPageState();
            final Label target = (Label) e.getTarget();

            final CategoryRepository categoryRepo = CdiUtil.createCdiUtil()
                .findBean(CategoryRepository.class);
            final Category category = categoryRepo.findById(Long.parseLong(
                selectedCategoryId.getSelectedKey(state))).get();

            target.setLabel(new GlobalizedMessage(
                "ui.admin.categories.category_details.heading",
                ADMIN_BUNDLE,
                new String[]{category.getName()}));

        });
        heading.setClassAttr("heading");
        add(heading);

        final BoxPanel propertiesPanel = new BoxPanel(BoxPanel.VERTICAL);
        propertiesPanel.add(new PropertySheet(
            new CategoryPropertySheetModelBuilder(selectedCategoryId)));
        final ActionLink editBasicPropertiesLink = new ActionLink(
            new GlobalizedMessage(
                "ui.admin.categories.category_details.basic_properties.edit",
                ADMIN_BUNDLE));
        editBasicPropertiesLink.addActionListener(e -> {
            final PageState state = e.getPageState();
            categoriesTab.showCategoryEditForm(state);
        });
        propertiesPanel.add(editBasicPropertiesLink);
        final ActionLink moveLink = new ActionLink(new GlobalizedMessage(
            "ui.admin.categories.category_details.move_category", ADMIN_BUNDLE)) {

            @Override
            public boolean isVisible(final PageState state) {
                if (super.isVisible(state)) {
                    final CategoryRepository categoryRepo = CdiUtil
                        .createCdiUtil().findBean(CategoryRepository.class);
                    final Category category = categoryRepo.findById(Long
                        .parseLong(selectedCategoryId.getSelectedKey(state)))
                        .get();

                    //If the category has no parent category it is the root
                    //category of a domain and can't be moved
                    return category.getParentCategory() != null;
                } else {
                    return false;
                }
            }

        };
        moveLink.addActionListener(e -> {
            final PageState state = e.getPageState();
            categoriesTab.showCategoryMover(state);
        });
        propertiesPanel.add(moveLink);
        addSegment(
            new Label(new GlobalizedMessage(
                "ui.admin.categories.category_details.basic_properties",
                ADMIN_BUNDLE)),
            propertiesPanel
        );

        final BoxPanel titlesPanel = new BoxPanel(BoxPanel.VERTICAL);
        titlesPanel.add(new CategoryTitleTable(categoriesTab,
                                               selectedCategoryId,
                                               selectedLanguage));
        categoryTitleAddForm = new CategoryTitleAddForm();
        titlesPanel.add(categoryTitleAddForm);
        addSegment(
            new Label(new GlobalizedMessage(
                "ui.admin.categories.category_details.category_title",
                ADMIN_BUNDLE)),
            titlesPanel);

        final BoxPanel descPanel = new BoxPanel(BoxPanel.VERTICAL);
        descPanel.add(new CategoryDescriptionTable(categoriesTab,
                                                   selectedCategoryId,
                                                   selectedLanguage));
        categoryDescriptionAddForm = new CategoryDescriptionAddForm();
        descPanel.add(categoryDescriptionAddForm);
        addSegment(
            new Label(new GlobalizedMessage(
                "ui.admin.categories.category_details.description",
                ADMIN_BUNDLE)),
            descPanel);

        final BoxPanel subCategoriesPanel = new BoxPanel(BoxPanel.VERTICAL);
        subCategoriesTable = new SubCategoriesTable(categoriesTab,
                                                    selectedCategoryId);
        subCategoriesPanel.add(subCategoriesTable);
        final ActionLink addSubCategory = new ActionLink(new GlobalizedMessage(
            "ui.admin.categories.category_details.subcategories.add",
            ADMIN_BUNDLE));
        addSubCategory.addActionListener(e -> {
            final PageState state = e.getPageState();

            categoriesTab.showCategoryCreateForm(state);
        });
        subCategoriesPanel.add(addSubCategory);
        addSegment(
            new Label(new GlobalizedMessage(
                "ui.admin.categories.category_details.subcategories.header",
                ADMIN_BUNDLE)),
            subCategoriesPanel);
    }

    private class CategoryTitleAddForm extends Form {

        private static final String TITLE_SELECT_LANG = "titleSelectLang";

        public CategoryTitleAddForm() {
            super("categoryTitleAddForm", new BoxPanel(BoxPanel.HORIZONTAL));

            final SingleSelect titleSelectLang = new SingleSelect(
                TITLE_SELECT_LANG);
            titleSelectLang.setLabel(new GlobalizedMessage(
                "ui.admin.categories.category_details.category_title.add.label",
                ADMIN_BUNDLE));
            try {
                titleSelectLang.addPrintListener(e -> {
                    final PageState state = e.getPageState();

                    final CategoryRepository categoryRepository = CdiUtil.
                        createCdiUtil().findBean(CategoryRepository.class);
                    final Category category = categoryRepository.findById(
                        Long.parseLong(selectedCategoryId.getSelectedKey(
                            state))).get();
                    final KernelConfig kernelConfig = KernelConfig.getConfig();
                    final Set<String> supportedLanguages = kernelConfig.
                        getSupportedLanguages();
                    final Set<String> assignedLanguages = new HashSet<>();
                    category.getTitle().getAvailableLocales().forEach(l -> {
                        assignedLanguages.add(l.toString());
                    });

                    final SingleSelect target = (SingleSelect) e.getTarget();

                    target.clearOptions();

                    supportedLanguages.forEach(l -> {
                        if (!assignedLanguages.contains(l)) {
                            target.addOption(new Option(l, new Text(l)));
                        }
                    });
                });
            } catch (TooManyListenersException ex) {
                throw new UncheckedWrapperException(ex);
            }

            add(titleSelectLang);
            add(new Submit(new GlobalizedMessage(
                "ui.admin.categories.category_details.category_title.add.submit",
                ADMIN_BUNDLE)));

            addProcessListener(e -> {
                final PageState state = e.getPageState();
                final FormData data = e.getFormData();

                final String language = data.getString(TITLE_SELECT_LANG);
                selectedLanguage.setSelectedKey(state, language);

                categoriesTab.showCategoryTitleForm(state);
            });
        }

        @Override
        public boolean isVisible(final PageState state) {
            if (super.isVisible(state)) {
                final CategoryRepository categoryRepository = CdiUtil.
                    createCdiUtil().findBean(CategoryRepository.class);
                final Category category = categoryRepository.findById(
                    Long.parseLong(selectedCategoryId.getSelectedKey(
                        state))).get();
                final KernelConfig kernelConfig = KernelConfig.getConfig();
                final Set<String> supportedLanguages = kernelConfig.
                    getSupportedLanguages();
                final Set<String> assignedLanguages = new HashSet<>();
                category.getTitle().getAvailableLocales().forEach(l -> {
                    assignedLanguages.add(l.toString());
                });

                //If all supported languages are assigned the form is not 
                //visible
                return !assignedLanguages.equals(supportedLanguages);
            } else {
                return false;
            }
        }

    }

    private class CategoryDescriptionAddForm extends Form {

        private static final String DESC_SELECT_LANG = "descSelectLang";

        public CategoryDescriptionAddForm() {
            super("categoryAddDescLang", new BoxPanel(BoxPanel.HORIZONTAL));

            final SingleSelect descSelectLang = new SingleSelect(
                DESC_SELECT_LANG);
            descSelectLang.setLabel(new GlobalizedMessage(
                "ui.admin.categories.category_details.category_desc.add.label",
                ADMIN_BUNDLE));
            try {
                descSelectLang.addPrintListener(e -> {
                    final PageState state = e.getPageState();

                    final CategoryRepository categoryRepository = CdiUtil.
                        createCdiUtil().findBean(CategoryRepository.class);
                    final Category category = categoryRepository.findById(
                        Long.parseLong(selectedCategoryId.getSelectedKey(
                            state))).get();
                    final KernelConfig kernelConfig = KernelConfig.getConfig();
                    final Set<String> supportedLanguages = kernelConfig.
                        getSupportedLanguages();
                    final Set<String> assignedLanguages = new HashSet<>();
                    category.getDescription().getAvailableLocales().forEach(
                        l -> {
                            assignedLanguages.add(l.toString());
                        });

                    final SingleSelect target = (SingleSelect) e.getTarget();

                    target.clearOptions();

                    supportedLanguages.forEach(l -> {
                        if (!assignedLanguages.contains(l)) {
                            target.addOption(new Option(l, new Text(l)));
                        }
                    });
                });

            } catch (TooManyListenersException ex) {
                throw new UncheckedWrapperException(ex);
            }

            add(descSelectLang);
            add(new Submit(new GlobalizedMessage(
                "ui.admin.categories.category_details.category_desc.add.submit",
                ADMIN_BUNDLE)));

            addProcessListener(e -> {
                final PageState state = e.getPageState();
                final FormData data = e.getFormData();

                final String language = data.getString(DESC_SELECT_LANG);
                selectedLanguage.setSelectedKey(state, language);

                categoriesTab.showCategoryDescriptionForm(state);
            });
        }

        @Override
        public boolean isVisible(final PageState state) {
            if (super.isVisible(state)) {
                final CategoryRepository categoryRepository = CdiUtil.
                    createCdiUtil().findBean(CategoryRepository.class);
                final Category category = categoryRepository.findById(
                    Long.parseLong(selectedCategoryId.getSelectedKey(
                        state))).get();
                final KernelConfig kernelConfig = KernelConfig.getConfig();
                final Set<String> supportedLanguages = kernelConfig.
                    getSupportedLanguages();
                final Set<String> assignedLanguages = new HashSet<>();
                category.getDescription().getAvailableLocales().forEach(
                    l -> {
                        assignedLanguages.add(l.toString());
                    });

                //If all supported languages are assigned the form is not 
                //visible
                return !assignedLanguages.equals(supportedLanguages);
            } else {
                return false;
            }
        }

    }

}
