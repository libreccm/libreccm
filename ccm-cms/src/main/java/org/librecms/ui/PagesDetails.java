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
package org.librecms.ui;

import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import org.libreccm.categorization.Category;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.libreccm.pagemodel.PageModel;
import org.librecms.CmsConstants;
import org.librecms.pages.Page;
import org.librecms.pages.PageManager;
import org.librecms.pages.Pages;

import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class PagesDetails extends Window {

    private static final long serialVersionUID = 6306677625483502088L;

    private final CmsViewController controller;
    private final Pages pages;

    private final GlobalizationHelper globalizationHelper;
    private final LocalizedTextsUtil textsUtil;

    private Category selectedCategory;

    private final Panel panel;
    private final ComboBox<PageModel> indexPageModelSelect;
    private final ComboBox<PageModel> itemPageModelSelect;

    private final Button saveButton;
    private final Button cancelButton;

    public PagesDetails(final Pages pages, final CmsViewController controller) {

        super();

        globalizationHelper = controller.getGlobalizationHelper();
        textsUtil = globalizationHelper
            .getLocalizedTextsUtil(CmsConstants.CMS_BUNDLE);

        this.pages = pages;
        this.controller = controller;

        final PagesCategoryTreeDataProvider treeDataProvider = controller
            .getPagesController()
            .getPagesCategoryTreeDataProvider();
        treeDataProvider.setDomain(pages.getCategoryDomain());
        final Tree<Category> categoryTree = new Tree<>();
        categoryTree.setDataProvider(treeDataProvider);
        categoryTree.addItemClickListener(this::categoryTreeClicked);
        categoryTree.setItemCaptionGenerator(this::createCategoryTreeCaption);

        indexPageModelSelect = new ComboBox<>(textsUtil
            .getText("cms.ui.pages.pagemodels.index_page"));
        indexPageModelSelect.setDataProvider(controller
            .getPagesController()
            .getPageModelSelectDataProvider());
        indexPageModelSelect
            .setItemCaptionGenerator(this::createPageModelCaption);
        indexPageModelSelect.setTextInputAllowed(false);
        indexPageModelSelect.setEmptySelectionAllowed(true);
        indexPageModelSelect.setEmptySelectionCaption(textsUtil
            .getText("cms.ui.pages.assigned_page_model.inherit"));
        indexPageModelSelect
            .setItemCaptionGenerator(this::createPageModelCaption);

        itemPageModelSelect = new ComboBox<>(textsUtil
            .getText("cms.ui.pages.pagemodels.item_page"));
        itemPageModelSelect.setDataProvider(controller
            .getPagesController()
            .getPageModelSelectDataProvider());
        itemPageModelSelect
            .setItemCaptionGenerator(this::createPageModelCaption);
        itemPageModelSelect.setTextInputAllowed(false);
        itemPageModelSelect.setEmptySelectionAllowed(true);
        itemPageModelSelect.setEmptySelectionCaption(textsUtil
            .getText("cms.ui.pages.assigned_page_model.inherit"));

        indexPageModelSelect.addSelectionListener(this::selectionChange);
        itemPageModelSelect.addSelectionListener(this::selectionChange);

        saveButton = new Button(textsUtil
            .getText("cms.ui.pages.pagemodels.save"));
        saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.addClickListener(this::saveButtonClicked);

        cancelButton = new Button(textsUtil
            .getText("cms.ui.pages.pagemodels.cancel"));
        cancelButton.addStyleName(ValoTheme.BUTTON_DANGER);
        cancelButton.addClickListener(this::cancelButtonClicked);

        saveButton.setEnabled(false);
        cancelButton.setEnabled(false);
        
        super.setCaption(textsUtil.getText("cms.ui.pages.title",
                                           new String[]{pages.getPrimaryUrl()}));

        panel = new Panel(new VerticalLayout(
            new FormLayout(indexPageModelSelect,
                           itemPageModelSelect),
            new HorizontalLayout(saveButton, cancelButton)));
        final HorizontalSplitPanel splitPanel = new HorizontalSplitPanel(
            categoryTree, panel);
        splitPanel.setSplitPosition(25.0f);
        super.setContent(splitPanel);
        init();
    }

    private void init() {

        selectedCategory = pages.getCategoryDomain().getRoot();
        updateWidgets(selectedCategory);
    }

    private String createCategoryTreeCaption(final Category category) {

        if (category.getTitle().getValues().isEmpty()) {
            return category.getName();
        } else {

            return globalizationHelper
                .getValueFromLocalizedString(category.getTitle());
        }
    }

    private String createPageModelCaption(final PageModel pageModel) {

        if (pageModel.getTitle().getValues().isEmpty()) {
            return pageModel.getName();
        } else {
            return globalizationHelper
                .getValueFromLocalizedString(pageModel.getTitle());
        }
    }

    private void categoryTreeClicked(
        final Tree.ItemClick<Category> event) {

        final Category category = event.getItem();
        selectedCategory = category;
        updateWidgets(category);
    }

    private void selectionChange(final SingleSelectionEvent<PageModel> event) {

        saveButton.setEnabled(true);
        cancelButton.setEnabled(true);
    }

    private void updateWidgets(final Category category) {

        Objects.requireNonNull(category);

        final Optional<Page> page = controller
            .getPagesController()
            .findPage(category);

        if (page.isPresent()) {
            panel.setCaption(textsUtil
                .getText("cms.ui.pages.page_config_for_category",
                         new String[]{globalizationHelper
                                 .getValueFromLocalizedString(category
                                     .getTitle())}));
            indexPageModelSelect.setSelectedItem(page.get().getIndexPageModel());
            itemPageModelSelect.setSelectedItem(page.get().getItemPageModel());
//            indexPageModelSelect.setValue(page.get().getIndexPageModel());
//            itemPageModelSelect.setValue(page.get().getItemPageModel());
        } else {
            indexPageModelSelect.setSelectedItem(null);
            itemPageModelSelect.setSelectedItem(null);
        }
        
        saveButton.setEnabled(false);
        cancelButton.setEnabled(false);
    }

    private void saveButtonClicked(final Button.ClickEvent event) {

        final Page page = findOrCreatePage();

        final Optional<PageModel> selectedIndexPageModel
                                      = indexPageModelSelect
                .getSelectedItem();
        if (selectedIndexPageModel.isPresent()) {
            page.setIndexPageModel(selectedIndexPageModel.get());
        } else {
            page.setIndexPageModel(null);
        }

        final Optional<PageModel> selectedItemPageModel
                                      = itemPageModelSelect
                .getSelectedItem();
        if (selectedItemPageModel.isPresent()) {
            page.setItemPageModel(selectedItemPageModel.get());
        } else {
            page.setItemPageModel(null);
        }

        controller.getPagesController().getPageRepo().save(page);
        updateWidgets(selectedCategory);
    }

    private Page findOrCreatePage() {

        final Optional<Page> page = controller
            .getPagesController()
            .findPage(selectedCategory);

        if (page.isPresent()) {
            return page.get();
        } else {

            final PageManager pageManager = controller
                .getPagesController()
                .getPageManager();
            return pageManager.createPageForCategory(selectedCategory);
        }

    }

    private void cancelButtonClicked(final Button.ClickEvent event) {

        updateWidgets(selectedCategory);
    }

}
