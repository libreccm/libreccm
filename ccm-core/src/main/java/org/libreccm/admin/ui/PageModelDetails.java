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
package org.libreccm.admin.ui;

import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.ui.admin.AdminUiConstants;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.libreccm.pagemodel.ComponentModel;
import org.libreccm.pagemodel.ComponentModels;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelComponentModel;
import org.libreccm.ui.ConfirmDialog;
import org.libreccm.web.CcmApplication;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.Optional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class PageModelDetails extends Window {

    private static final long serialVersionUID = -3617001410191320596L;

    private static final String COL_KEY = "key";
    private static final String COL_TYPE = "type";
    private static final String COL_EDIT = "edit";
    private static final String COL_DEL = "del";

    private final AdminViewController controller;
    private final CcmApplication application;
    private final PageModel pageModel;

//    private final NativeSelect<PageModelComponentModel> componentModelTypeSelect;
    private final ComboBox<PageModelComponentModel> componentModelTypeSelect;

    PageModelDetails(final PageModel pageModel,
                     final CcmApplication application,
                     final AdminViewController controller) {

        super();

        this.controller = controller;
        this.application = application;
        this.pageModel = pageModel;

        final GlobalizationHelper globalizationHelper = controller
            .getGlobalizationHelper();
        final LocalizedTextsUtil textsUtil = globalizationHelper
            .getLocalizedTextsUtil(AdminUiConstants.ADMIN_BUNDLE);
        final ConfigurationManager configurationManager = controller
            .getConfigurationManager();
        final KernelConfig kernelConfig = configurationManager
            .findConfiguration(KernelConfig.class);
        final Locale defaultLocale = kernelConfig.getDefaultLocale();

        super.setCaption(textsUtil
            .getText("ui.admin.pagemodels.details.heading",
                     new String[]{pageModel.getName()}));

        final Label nameLabel = new Label(pageModel.getName());
        nameLabel.setCaption(textsUtil
            .getText("ui.admin.pagemodels.details.model_name"));

        final Label titleLabel = new Label(pageModel
            .getTitle().getValue(defaultLocale));
        titleLabel.setCaption(textsUtil
            .getText("ui.admin.pagemodels.details.model_title"));

        final Label applicationLabel = new Label(pageModel
            .getApplication().getPrimaryUrl());
        applicationLabel.setCaption(textsUtil
            .getText("ui.admin.pagemodels.details.model_application"));

        final Label descLabel = new Label(pageModel
            .getDescription().getValue(defaultLocale));
        descLabel.setCaption(textsUtil
            .getText("ui.admin.pagemodels.details.model_desc"));

        final FormLayout propertiesSheetLayout = new FormLayout(
            nameLabel, titleLabel, applicationLabel, descLabel);

        final Button editPropertiesButton = new Button(textsUtil
            .getText("ui.admin.pagemodels.edit_basic_properties"));
        editPropertiesButton.setIcon(VaadinIcons.EDIT);
        editPropertiesButton
            .addClickListener(this::editBasicPropertiesButtonClicked);
        final HorizontalLayout buttonsLayout = new HorizontalLayout(
            editPropertiesButton);

        final PageModelsController pageModelsController = controller
            .getPageModelsController();

        final Grid<ComponentModel> componentsModelGrid = new Grid<>();
        final PageModelComponentModelsTableDataProvider dataProvider
                                                            = pageModelsController
                .getComponentModelsTableDataProvider();
        dataProvider.setPageModel(pageModel);
        componentsModelGrid.setDataProvider(dataProvider);
        componentsModelGrid
            .addColumn(ComponentModel::getKey)
            .setCaption(textsUtil
                .getText("ui.admin.pagemodels.componentmodels.cols.key.heading"))
            .setId(COL_KEY);
        componentsModelGrid
            .addColumn(this::getComponentModelType)
            .setCaption(textsUtil
                .getText("ui.admin.pagemodels.componentmodels.cols.type.heading"))
            .setId(COL_TYPE);
        componentsModelGrid
            .addComponentColumn(this::buildEditButton)
            .setCaption(textsUtil
                .getText("ui.admin.pagemodels.componentmodels.cols.edit.heading"))
            .setId(COL_EDIT);
        componentsModelGrid
            .addComponentColumn(this::buildDeleteButton)
            .setCaption(textsUtil
                .getText(
                    "ui.admin.pagemodels.componentmodels.cols.delete.heading"))
            .setId(COL_DEL);
        componentsModelGrid.setWidth("100%");

//        componentModelTypeSelect = new NativeSelect<>(
//            textsUtil.getText("ui.admin.pagemodels.add_new_component.type"),
//            pageModelsController.getComponentModelTypesDataProvider());
        componentModelTypeSelect = new ComboBox<>();
        componentModelTypeSelect.setTextInputAllowed(false);
        componentModelTypeSelect.setEmptySelectionAllowed(false);
        componentModelTypeSelect.setDescription(textsUtil
            .getText("ui.admin.pagemodels.add_new_component.type"));
        componentModelTypeSelect.setDataProvider(pageModelsController
            .getComponentModelTypesDataProvider());
        componentModelTypeSelect
            .setItemCaptionGenerator(this::generateComponentModelTypeCaption);
        componentModelTypeSelect.addStyleName(ValoTheme.COMBOBOX_TINY);
        final Button addComponentModelButton = new Button(textsUtil
            .getText("ui.admin.pagemodels.add_new_component.submit"));
        addComponentModelButton.addStyleName(ValoTheme.BUTTON_TINY);
        addComponentModelButton.setIcon(VaadinIcons.PLUS_CIRCLE_O);
        addComponentModelButton
            .addClickListener(this::addComponentButtonClicked);
//        final Panel componentsPanel = new Panel(
//            "Components",
//            new VerticalLayout(new HorizontalLayout(
//                new FormLayout(componentModelTypeSelect),
//                addComponentModelButton),
//                               componentsModelGrid));

        final HeaderRow headerRow = componentsModelGrid.prependHeaderRow();
        final HeaderCell headerCell = headerRow.join(COL_KEY,
                                                     COL_TYPE,
                                                     COL_EDIT,
                                                     COL_DEL);
        headerCell.setComponent(new HorizontalLayout(componentModelTypeSelect,
                                                     addComponentModelButton));
        super.setContent(new VerticalLayout(propertiesSheetLayout,
                                            buttonsLayout,
                                            componentsModelGrid));
//        super.setContent(new VerticalLayout(propertiesSheetLayout,
//                                            buttonsLayout,
//                                            componentsPanel));
    }

    @SuppressWarnings("unchecked")
    private void addComponentButtonClicked(final Button.ClickEvent event) {

        final PageModelComponentModel componentModelInfo
                                          = componentModelTypeSelect.getValue();

        final String bebopFormClassName = componentModelInfo
            .editor()
            .getName();

        final PageModelsController pageModelsController = controller
            .getPageModelsController();

        final String editorName = bebopFormClassName
            .replace("com.arsdigita.cms", "org.librecms")
            .replace("Form", "Editor");

        final Class<? extends AbstractPageModelComponentEditor<?>> editorClass;
        try {
            editorClass
                = (Class<? extends AbstractPageModelComponentEditor<?>>) Class
                    .forName(editorName);
        } catch (ClassNotFoundException ex) {
            throw new UnexpectedErrorException(ex);
        }

        final Constructor<? extends AbstractPageModelComponentEditor<?>> constructor;
        try {
            constructor = editorClass
                .getDeclaredConstructor(PageModel.class,
                                        PageModelComponentModel.class,
                                        PageModelComponentEditorController.class);
        } catch (NoSuchMethodException ex) {
            throw new UnexpectedErrorException(ex);
        }

        final AbstractPageModelComponentEditor<?> editor;
        try {
            editor = constructor.newInstance(
                pageModel,
                componentModelInfo,
                pageModelsController.getComponentEditorController());
        } catch (InstantiationException
                 | IllegalAccessException
                 | InvocationTargetException ex) {
            throw new UnexpectedErrorException(ex);
        }
        editor.initWidgets();

        editor.setModal(true);
        editor.setWidth("50%");
        editor.setHeight("40%");

        UI.getCurrent().addWindow(editor);
    }

    private void editBasicPropertiesButtonClicked(final Button.ClickEvent event) {

        final PageModelForm pageModelForm = new PageModelForm(pageModel,
                                                              application,
                                                              controller);
        pageModelForm.setModal(true);
        pageModelForm.setWidth("40%");
        pageModelForm.setHeight("30%");

        UI.getCurrent().addWindow(pageModelForm);
    }

    private String getComponentModelType(final ComponentModel model) {

        return controller
            .getPageModelsController()
            .getComponentModelTitle(model.getClass());

    }

    private String generateComponentModelTypeCaption(
        final PageModelComponentModel item) {

        final GlobalizationHelper globalizationHelper = controller
            .getGlobalizationHelper();
        final LocalizedTextsUtil textsUtil = globalizationHelper
            .getLocalizedTextsUtil(item.descBundle());

        return textsUtil.getText(item.titleKey());
    }

    private Component buildEditButton(final ComponentModel componentModel) {

        final LocalizedTextsUtil textsUtil = controller
            .getGlobalizationHelper()
            .getLocalizedTextsUtil(AdminUiConstants.ADMIN_BUNDLE);

        final Button editButton = new Button(textsUtil
            .getText("ui.admin.pagemodels.components.edit"));
        editButton.setIcon(VaadinIcons.EDIT);
        editButton.addStyleName(ValoTheme.BUTTON_TINY);
        editButton.addClickListener(event -> editComponentModel(componentModel));

        return editButton;
    }

    @SuppressWarnings(
        "unchecked")
    private void editComponentModel(final ComponentModel componentModel) {

        final LocalizedTextsUtil textsUtil = controller
            .getGlobalizationHelper()
            .getLocalizedTextsUtil(AdminUiConstants.ADMIN_BUNDLE);

        final PageModelsController pageModelsController = controller
            .getPageModelsController();
        final ComponentModels componentModels = pageModelsController
            .getComponentModels();

        final Optional<PageModelComponentModel> componentModelInfo
                                                    = componentModels
                .getComponentModelInfo(componentModel.getClass());
        if (componentModelInfo.isPresent()) {

            final String bebopFormClassName = componentModelInfo
                .get()
                .editor()
                .getName();
            final String editorName = bebopFormClassName
                .replace("com.arsdigita.cms", "org.librecms")
                .replace("Form", "Editor");

            final Class<? extends AbstractPageModelComponentEditor<?>> editorClass;
            try {
                editorClass
                    = (Class<? extends AbstractPageModelComponentEditor<?>>) Class
                        .forName(editorName);
            } catch (ClassNotFoundException ex) {
                throw new UnexpectedErrorException(ex);
            }

            final Constructor<? extends AbstractPageModelComponentEditor<?>> constructor;

            try {
                constructor = editorClass
                    .getDeclaredConstructor(PageModel.class,
                                            componentModelInfo.get().modelClass(),
                                            PageModelComponentEditorController.class
                    );
            } catch (NoSuchMethodException ex) {
                throw new UnexpectedErrorException(ex);
            }

            final AbstractPageModelComponentEditor<?> editor;
            try {
                editor = constructor.newInstance(
                    pageModel,
                    componentModel,
                    pageModelsController.getComponentEditorController());
            } catch (InstantiationException
                     | IllegalAccessException
                     | InvocationTargetException ex) {
                throw new UnexpectedErrorException(ex);
            }
            editor.initWidgets();

            editor.setModal(true);
            editor.setWidth("50%");
            editor.setHeight("40%");

            UI.getCurrent().addWindow(editor);
        } else {
            Notification.show(textsUtil
                .getText("ui.admin.pageModels.no_info_for_component",
                         new String[]{componentModel.getClass().getName()}),
                              Notification.Type.ERROR_MESSAGE);
        }
    }

    private Component buildDeleteButton(final ComponentModel componentModel) {

        final PageModelsController pageModelsController = controller
            .getPageModelsController();
        final LocalizedTextsUtil textsUtil = controller
            .getGlobalizationHelper()
            .getLocalizedTextsUtil(AdminUiConstants.ADMIN_BUNDLE);

        final Button deleteButton = new Button(textsUtil
            .getText("ui.admin.pagemodels.components.delete"));
        deleteButton.setIcon(VaadinIcons.MINUS_CIRCLE_O);
        deleteButton.addStyleNames(ValoTheme.BUTTON_TINY,
                                   ValoTheme.BUTTON_DANGER);
        deleteButton.addClickListener(event -> {

            final ConfirmDialog confirmDialog = new ConfirmDialog(() -> {
                pageModelsController.removeComponentModel(pageModel,
                                                          componentModel);
                return null;
            });
            confirmDialog.setMessage(textsUtil.getText(
                "ui.admin.pagemodels.componentmodels.cols.delete.confirmation"));
            confirmDialog.setModal(true);
            UI.getCurrent().addWindow(confirmDialog);
        });

        return deleteButton;
    }

}
