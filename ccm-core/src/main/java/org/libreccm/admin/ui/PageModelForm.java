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
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.libreccm.l10n.ui.LocalizedStringEditor;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelManager;
import org.libreccm.pagemodel.PageModelRepository;
import org.libreccm.web.CcmApplication;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class PageModelForm extends Window {

    private static final long serialVersionUID = -8618363472800298648L;

    private final AdminViewController controller;
    private final CcmApplication application;
    private PageModel pageModel;

    private TextField nameField;
    private LocalizedStringEditor titleEditor;
    private LocalizedStringEditor descriptionEditor;

    public PageModelForm(final AdminViewController controller,
                         final CcmApplication application) {

        super();

        this.controller = controller;
        this.application = application;

        addWidgets();
    }

    public PageModelForm(final PageModel pageModel,
                         final CcmApplication application,
                         final AdminViewController controller) {

        super();

        this.controller = controller;
        this.application = application;
        this.pageModel = pageModel;

        addWidgets();

        nameField.setValue(pageModel.getName());
    }

    private void addWidgets() {

        final GlobalizationHelper globalizationHelper = controller
            .getGlobalizationHelper();
        final LocalizedTextsUtil textsUtil = globalizationHelper
            .getLocalizedTextsUtil(AdminUiConstants.ADMIN_BUNDLE);

        nameField = new TextField(textsUtil.getText("ui.admin.pagemodels.name"));
        nameField.setRequiredIndicatorVisible(true);

        titleEditor = new LocalizedStringEditor(pageModel.getTitle(),
                                                globalizationHelper);
        titleEditor.setCaption(textsUtil.getText("ui.admin.pagemodels.title"));

        descriptionEditor = new LocalizedStringEditor(
            pageModel.getDescription(), globalizationHelper);
        descriptionEditor
            .setCaption(textsUtil.getText("ui.admin.pagemodels.desc"));

        final FormLayout formLayout = new FormLayout(nameField,
                                                     titleEditor,
                                                     descriptionEditor);

        final Button saveButton = new Button(textsUtil
            .getText("ui.admin.pagemodels.buttons.save"));
        saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.addClickListener(this::saveButtonClicked);
        final Button cancelButton = new Button(textsUtil
            .getText("ui.admin.pagemodels.buttons.cancel"));
        cancelButton.addStyleName(ValoTheme.BUTTON_DANGER);
        cancelButton.addClickListener(event -> close());
        final HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton,
                                                                    cancelButton);

        final VerticalLayout layout = new VerticalLayout(formLayout,
                                                         buttonsLayout);
        setContent(layout);
    }

    private void saveButtonClicked(final Button.ClickEvent event) {

        final PageModelsController pageModelsController = controller
            .getPageModelsController();
        final PageModelManager pageModelManager = pageModelsController
            .getPageModelManager();
        final LocalizedTextsUtil textsUtil = controller
            .getGlobalizationHelper()
            .getLocalizedTextsUtil(AdminUiConstants.ADMIN_BUNDLE);

        final String name = nameField.getValue();

        if (name == null
                || name.isEmpty()
                || name.matches("\\s*")) {

            nameField
                .setComponentError(new UserError(
                    textsUtil.getText("ui.admin.pagemodels.name.error.empty")));
            return;
        }

        if (pageModel == null) {
            
            pageModel = pageModelManager.createPageModel(name, application);
        }
        pageModel.setName(name);

        final PageModelRepository pageModelRepo = pageModelsController
            .getPageModelRepo();
        pageModelRepo.save(pageModel);

        close();
    }

}
