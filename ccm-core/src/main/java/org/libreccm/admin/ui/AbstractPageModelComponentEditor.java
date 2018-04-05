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

import com.arsdigita.ui.admin.AdminUiConstants;

import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.libreccm.pagemodel.ComponentModel;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelComponentModel;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
public abstract class AbstractPageModelComponentEditor<T extends ComponentModel>
    extends Window {

    private static final long serialVersionUID = 7347805088308841378L;

    private final PageModelComponentEditorController controller;
    private final PageModel pageModel;
    private PageModelComponentModel componentModelInfo;
    private T componentModel;

    private final FormLayout formLayout;

    private TextField keyField;

    public AbstractPageModelComponentEditor(
        final PageModel pageModel,
        final PageModelComponentModel componentModelInfo,
        final PageModelComponentEditorController controller) {

        super();

        this.controller = controller;
        this.pageModel = pageModel;
        this.componentModelInfo = componentModelInfo;

        formLayout = new FormLayout();

        createWidgets();
    }

    public AbstractPageModelComponentEditor(
        final PageModel pageModel,
        final T componentModel,
        final PageModelComponentEditorController controller) {

        super();

        this.pageModel = pageModel;
        this.componentModel = componentModel;
        this.controller = controller;

        formLayout = new FormLayout();

        createWidgets();

        keyField.setValue(componentModel.getKey());
    }

    private void createWidgets() {

        final LocalizedTextsUtil textsUtil = controller
            .getGlobalizationHelper()
            .getLocalizedTextsUtil(AdminUiConstants.ADMIN_BUNDLE);

        keyField = new TextField(textsUtil.getText(
            "ui.admin.pagemodels.components.key.label"));
        addComponent(keyField);

        final Button saveButton = new Button(textsUtil
            .getText("ui.admin.pagemodels.components.save"));
        saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.addClickListener(this::saveButtonClicked);

        final Button cancelButton = new Button(textsUtil
            .getText("ui.admin.pagemodels.components.cancel"));
        cancelButton.addStyleName(ValoTheme.BUTTON_DANGER);
        cancelButton.addClickListener(event -> close());

        final HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton,
                                                                    cancelButton);

        setContent(new VerticalLayout(formLayout, buttonsLayout));
    }

    private void saveButtonClicked(final Button.ClickEvent event) {

        final LocalizedTextsUtil textsUtil = controller
            .getGlobalizationHelper()
            .getLocalizedTextsUtil(AdminUiConstants.ADMIN_BUNDLE);

        final String key = keyField.getValue();
        if (key == null
                || key.isEmpty()
                || key.matches("\\s*")) {

            keyField.setComponentError(new UserError(textsUtil
                .getText("ui.admin.pagemodels.components.key.error.not_empty")));
            return;
        }

        if (!validate()) {
            return;
        }

        if (componentModel == null) {
            componentModel = createComponentModel();
            componentModel.setKey(key);
            updateComponentModel();
// ToDo
//            controller
//                .getPageModelsController()
//                .addComponentModel(pageModel, componentModel);
        } else {
            componentModel.setKey(key);
            updateComponentModel();
            controller.getComponentModelRepository().save(componentModel);
        }
        controller.refreshComponentModels();
        close();
    }

    protected PageModelComponentEditorController getController() {
        return controller;
    }

    protected PageModelComponentModel getComponentModelInfo() {
        return componentModelInfo;
    }

    protected T getComponentModel() {
        return componentModel;
    }

    protected final void addComponent(final Component component) {

        formLayout.addComponent(component);
    }

    protected abstract void initWidgets();

    /**
     *
     * @return {@code true} if form is validate, {@code false} if not.
     */
    protected abstract boolean validate();

    protected abstract T createComponentModel();

    protected abstract void updateComponentModel();

}
