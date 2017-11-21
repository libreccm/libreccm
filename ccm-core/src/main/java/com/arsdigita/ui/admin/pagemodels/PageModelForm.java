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
package com.arsdigita.ui.admin.pagemodels;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.ui.admin.AdminUiConstants;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelManager;
import org.libreccm.pagemodel.PageModelRepository;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.CcmApplication;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TooManyListenersException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class PageModelForm extends Form {

    private static final String MODEL_APPLICATION = "application";
    private static final String MODEL_NAME = "model_name";
    private static final String MODEL_TITLE = "model_title";
    private static final String MODEL_DESC = "model_desc";

    private final PageModelTab pageModelTab;
    private final ParameterSingleSelectionModel<String> selectedModelId;

    private final TextField nameField;
    private final TextField titleField;
    private final TextArea descArea;
    private final SingleSelect applicationSelect;
    private final SaveCancelSection saveCancelSection;

    public PageModelForm(
        final PageModelTab pageModelTab,
        final ParameterSingleSelectionModel<String> selectedModelId) {

        super("pagemodelsform");

        this.pageModelTab = pageModelTab;
        this.selectedModelId = selectedModelId;

        final Label heading = new Label(event -> {

            final PageState state = event.getPageState();
            final Label target = (Label) event.getTarget();

            final String selectedModelIdStr = selectedModelId
                .getSelectedKey(state);
            if (selectedModelIdStr == null || selectedModelIdStr.isEmpty()) {
                target.setLabel(new GlobalizedMessage(
                    "ui.admin.pagemodels.create_new",
                    AdminUiConstants.ADMIN_BUNDLE));
            } else {
                target.setLabel(new GlobalizedMessage(
                    "ui.admin.pagemodels.edit",
                    AdminUiConstants.ADMIN_BUNDLE));
            }
        });
        heading.setClassAttr("heading");
        super.add(heading);

        nameField = new TextField(MODEL_NAME);
        nameField.setLabel(new GlobalizedMessage(
            "ui.admin.pagemodels.name",
            AdminUiConstants.ADMIN_BUNDLE));
        super.add(nameField);

        titleField = new TextField(MODEL_TITLE);
        titleField.setLabel(new GlobalizedMessage(
            "ui.admin.pagemodels.title",
            AdminUiConstants.ADMIN_BUNDLE));
        super.add(titleField);

        descArea = new TextArea(MODEL_DESC);
        descArea.setLabel(new GlobalizedMessage(
            "ui.admin.pagemodels.desc",
            AdminUiConstants.ADMIN_BUNDLE));
        super.add(descArea);

        applicationSelect = new SingleSelect(MODEL_APPLICATION);
        applicationSelect.setLabel(new GlobalizedMessage(
            "ui.admin.pagemodels.application",
            AdminUiConstants.ADMIN_BUNDLE));
        try {
            applicationSelect.addPrintListener(event -> {

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ApplicationRepository applicationRepo = cdiUtil
                    .findBean(ApplicationRepository.class);

                final SingleSelect target = (SingleSelect) event.getTarget();
                target.clearOptions();

                final List<CcmApplication> applications = applicationRepo
                    .findAll();
                applications.sort((app1, app2) -> {
                    return app1.getPrimaryUrl().compareTo(app2.getPrimaryUrl());
                });
                for (final CcmApplication app : applications) {
                    target.addOption(new Option(app.getPrimaryUrl(),
                                                new Text(app.getPrimaryUrl())));
                }
            });

        } catch (TooManyListenersException ex) {
            throw new UnexpectedErrorException(ex);
        }
        super.add(applicationSelect);

        saveCancelSection = new SaveCancelSection();
        super.add(saveCancelSection);

        super.addValidationListener(new ValidationListener());
        super.addInitListener(new InitListener());
        super.addProcessListener(new ProcessListener());
    }

    private class ValidationListener implements FormValidationListener {

        @Override
        public void validate(final FormSectionEvent event)
            throws FormProcessException {

            final PageState state = event.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {

                final FormData data = event.getFormData();
                final String nameValue = data.getString(MODEL_NAME);
                final String titleValue = data.getString(MODEL_TITLE);
                final String appValue = data.getString(MODEL_APPLICATION);

                final String selectedModelIdStr = selectedModelId
                    .getSelectedKey(state);
                final boolean modelEditedOrNew;

                if (selectedModelIdStr == null || selectedModelIdStr.isEmpty()) {
                    modelEditedOrNew = true;
                } else {
                    final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                    final ConfigurationManager confManager = cdiUtil
                        .findBean(ConfigurationManager.class);
                    final PageModelRepository pageModelRepo = cdiUtil
                        .findBean(PageModelRepository.class);
                    final PageModel pageModel = pageModelRepo
                        .findById(Long.parseLong(selectedModelIdStr))
                        .orElseThrow(() -> new IllegalArgumentException(String
                        .format("No PageModel with ID %s in the database.",
                                selectedModelIdStr)));

                    final KernelConfig kernelConfig = confManager
                        .findConfiguration(KernelConfig.class);

                    final boolean nameEdited = !pageModel
                        .getName()
                        .equals(nameValue);
                    final boolean titleEdited = !pageModel
                        .getTitle()
                        .getValue(kernelConfig.getDefaultLocale())
                        .equals(titleValue);
                    final boolean appEdited = !pageModel
                        .getApplication()
                        .getPrimaryUrl()
                        .equals(appValue);

                    modelEditedOrNew = nameEdited || titleEdited || appEdited;
                }

                if (modelEditedOrNew) {
                    if (nameValue == null
                            || nameValue.isEmpty()
                            || nameValue.matches("\\s*")) {

                        data.addError(MODEL_NAME,
                                      new GlobalizedMessage(
                                          "ui.admin.pagemodels.name.error.empty",
                                          AdminUiConstants.ADMIN_BUNDLE));
                    }

                    if (titleValue == null
                            || titleValue.isEmpty()
                            || titleValue.matches("\\s*")) {

                        data.addError(MODEL_TITLE,
                                      new GlobalizedMessage(
                                          "ui.admin.pagemodels.title.error.empty",
                                          AdminUiConstants.ADMIN_BUNDLE));
                    }

                    if (appValue == null
                            || appValue.isEmpty()
                            || appValue.matches("\\s*")) {

                        data.addError(MODEL_TITLE,
                                      new GlobalizedMessage(
                                          "ui.admin.pagemodels.application.error.empty",
                                          AdminUiConstants.ADMIN_BUNDLE));
                    } else {

                        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                        final ApplicationRepository appRepo = cdiUtil
                            .findBean(ApplicationRepository.class);

                        final Optional<CcmApplication> application = appRepo
                            .retrieveApplicationForPath(appValue);

                        if (!application.isPresent()) {
                            data.addError(MODEL_TITLE,
                                          new GlobalizedMessage(
                                              "ui.admin.pagemodels.application.error.invalid",
                                              AdminUiConstants.ADMIN_BUNDLE));
                        }

                    }
                }

            }
        }

    }

    private class InitListener implements FormInitListener {

        @Override
        public void init(final FormSectionEvent event)
            throws FormProcessException {

            final PageState state = event.getPageState();

            final String selectedModelIdStr = selectedModelId
                .getSelectedKey(state);

            if (selectedModelIdStr != null && !selectedModelIdStr.isEmpty()) {

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final PageModelRepository pageModelRepo = cdiUtil
                    .findBean(PageModelRepository.class);
                final ConfigurationManager confManager = cdiUtil
                    .findBean(ConfigurationManager.class);
                final KernelConfig kernelConfig = confManager
                    .findConfiguration(KernelConfig.class);
                final Locale defaultLocale = kernelConfig.getDefaultLocale();

                final PageModel pageModel = pageModelRepo
                    .findById(Long.parseLong(selectedModelIdStr))
                    .orElseThrow(() -> new IllegalArgumentException(String
                    .format("No PageModel with ID %s in the database.",
                            selectedModelIdStr)));

                nameField.setValue(state, pageModel.getName());
                titleField.setValue(state,
                                    pageModel.getTitle().getValue(defaultLocale));
                descArea
                    .setValue(state,
                              pageModel.getDescription().getValue(defaultLocale));
                applicationSelect
                    .setValue(state,
                              pageModel.getApplication().getPrimaryUrl());
            }
        }

    }

    private class ProcessListener implements FormProcessListener {

        @Override
        public void process(final FormSectionEvent event)
            throws FormProcessException {

            final PageState state = event.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {

                final FormData data = event.getFormData();

                final String nameValue = data.getString(MODEL_NAME);
                final String titleValue = data.getString(MODEL_TITLE);
                final String descValue = data.getString(MODEL_DESC);
                final String appValue = data.getString(MODEL_APPLICATION);

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final PageModelRepository pageModelRepo = cdiUtil
                    .findBean(PageModelRepository.class);
                final PageModelManager pageModelManager = cdiUtil
                .findBean(PageModelManager.class);
                final ConfigurationManager confManager = cdiUtil
                    .findBean(ConfigurationManager.class);
                final ApplicationRepository appRepo = cdiUtil
                    .findBean(ApplicationRepository.class);
                final KernelConfig kernelConfig = confManager
                    .findConfiguration(KernelConfig.class);
                final Locale defaultLocale = kernelConfig.getDefaultLocale();

                final String selectedModelIdStr = selectedModelId
                    .getSelectedKey(state);

                final CcmApplication application = appRepo
                    .retrieveApplicationForPath(appValue)
                    .orElseThrow(() -> new IllegalArgumentException(String
                    .format("No CcmApplication with primary URL \"%s\" in the "
                                + "database.",
                            appValue)));
                
                final PageModel pageModel;
                if (selectedModelIdStr == null || selectedModelIdStr.isEmpty()) {
                    pageModel = pageModelManager.createPageModel(nameValue, 
                                                                 application);
                } else {
                    pageModel = pageModelRepo
                        .findById(Long.parseLong(selectedModelIdStr))
                        .orElseThrow(() -> new IllegalArgumentException(String
                        .format("No PageModel with ID %s in the database.",
                                selectedModelIdStr)));
                }

                pageModel.setName(nameValue);

                pageModel.getTitle().addValue(defaultLocale, titleValue);
                pageModel.getDescription().addValue(defaultLocale, descValue);
                
                pageModel.setApplication(application);
                
                pageModelRepo.save(pageModel);
            }
            
            pageModelTab.showPageModelsTable(state);
        }

    }

}
