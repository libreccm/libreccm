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
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.admin.AdminUiConstants;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.pagemodel.ComponentModel;
import org.libreccm.pagemodel.ComponentModelRepository;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelManager;
import org.libreccm.pagemodel.PageModelRepository;

/**
 * Base form for creating forms for editing/creating components of a
 * {@link PageModel}.
 *
 * @param <T>
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class AbstractComponentModelForm<T extends ComponentModel>
    extends Form
    implements FormInitListener,
               FormValidationListener,
               FormProcessListener {

    /**
     * Constant for identifying the key text field.
     */
    private static final String COMPONENT_KEY = "componentKey";

    /**
     * The {@link PageModelTab} in which the form is used
     */
    private final PageModelTab pageModelTab;
    /**
     * ID of the selected {@link PageModel}.
     */
    private final ParameterSingleSelectionModel<String> selectedModelId;
    /**
     * ID of the selected {@link ComponentModel}. {@code null} of empty if a new
     * component is added.
     */
    private final ParameterSingleSelectionModel<String> selectedComponentId;

    /**
     * Text field for the component's key in the page model.
     */
    private TextField keyField;

    private SaveCancelSection saveCancelSection;

    /**
     * The selected component model.
     */
    private T componentModel;

    public AbstractComponentModelForm(
        final String name,
        final PageModelTab pageModelTab,
        final ParameterSingleSelectionModel<String> selectedModelId,
        final ParameterSingleSelectionModel<String> selectedComponentId) {

        super(name);

        this.pageModelTab = pageModelTab;
        this.selectedModelId = selectedModelId;
        this.selectedComponentId = selectedComponentId;

        keyField = new TextField(COMPONENT_KEY);
        keyField.setLabel(new GlobalizedMessage(
            "ui.admin.pagemodels.components.key.label",
            AdminUiConstants.ADMIN_BUNDLE));
        super.add(keyField);

        addBasicWidgets();
    }

    private void addBasicWidgets() {
        keyField = new TextField("componentModelKey");
        keyField.setLabel(new GlobalizedMessage(
            "ui.admin.pagemodels.components.key.label",
            AdminUiConstants.ADMIN_BUNDLE));
        super.add(keyField);

        addWidgets();

        saveCancelSection = new SaveCancelSection();
        super.add(saveCancelSection);
    }

    protected final PageModelTab getPageModelTab() {
        return pageModelTab;
    }

    protected final ParameterSingleSelectionModel<String> getSelectedComponentId() {
        return selectedComponentId;
    }

    protected final ParameterSingleSelectionModel<String> getSelectedModelId() {
        return selectedModelId;
    }

    protected final SaveCancelSection getSaveCancelSection() {
        return saveCancelSection;
    }
    
    protected final T getComponentModel() {
        return componentModel;
    }

    /**
     * Subclasses have to override this method to add the widgets specific for a
     * component model.
     */
    protected abstract void addWidgets();

    /**
     * Creates a new {@link ComponentModel} of a specific type. This method is
     * only a wrapper around the constructor. An implementation should not add
     * the component to a {@link PageModel} or save the {@link ComponentModel}
     * in the database. This class takes care of that.
     *
     * @return A new {@link ComponentModel}.
     */
    protected abstract T createComponentModel();

    /**
     * Updates the current component model with data from the form.
     *
     * @param componentModel
     * @param state
     * @param data
     */
    protected abstract void updateComponentModel(T componentModel,
                                                 PageState state,
                                                 FormData data);

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {

        final PageState state = event.getPageState();
        final String selectedComponentIdStr = selectedComponentId
            .getSelectedKey(state);

        if (selectedComponentIdStr != null
                && !selectedComponentIdStr.isEmpty()) {

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ComponentModelRepository componentModelRepo = cdiUtil
                .findBean(ComponentModelRepository.class);

            final ComponentModel model = componentModelRepo
                .findById(Long.parseLong(selectedComponentIdStr))
                .orElseThrow(() -> new IllegalArgumentException(String
                .format("No ComponentModel with ID %s in the database.",
                        selectedComponentIdStr)));

            keyField.setValue(state, model.getKey());
        }
    }

    @Override
    public void validate(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

        if (saveCancelSection.getSaveButton().isSelected(state)) {

            final FormData data = event.getFormData();
            final String keyValue = data.getString(COMPONENT_KEY);

            if (keyValue == null
                    || keyValue.isEmpty()
                    || keyValue.matches("\\s*")) {

                data.addError(COMPONENT_KEY,
                              new GlobalizedMessage(
                                  "ui.admin.pagemodels.components.key.error.not_empty",
                                  AdminUiConstants.ADMIN_BUNDLE));
            }
        }
    }

    @Override
    public void process(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

        if (saveCancelSection.getSaveButton().isSelected(state)) {

            final String selectedModelIdStr = selectedModelId
                .getSelectedKey(state);
            final String selectedComponentIdStr = selectedComponentId
                .getSelectedKey(state);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PageModelRepository pageModelRepo = cdiUtil
                .findBean(PageModelRepository.class);
            final PageModelManager pageModelManager = cdiUtil
                .findBean(PageModelManager.class);

            final FormData data = event.getFormData();
            final String keyValue = data.getString(COMPONENT_KEY);

            if (selectedComponentIdStr == null
                    || selectedComponentIdStr.isEmpty()) {

                componentModel = createComponentModel();
                componentModel.setKey(keyValue);
                updateComponentModel(componentModel, state, data);

                final PageModel pageModel = pageModelRepo
                    .findById(Long.parseLong(selectedModelIdStr))
                    .orElseThrow(() -> new IllegalArgumentException(String
                    .format("No PageModel with ID %s in the database.",
                            selectedModelIdStr)));

                pageModelManager.addComponentModel(pageModel, componentModel);
            } else {

                componentModel = retrieveComponentModel(selectedComponentIdStr);
                componentModel.setKey(keyValue);

                updateComponentModel(componentModel, state, data);

                final ComponentModelRepository componentModelRepo = cdiUtil
                    .findBean(ComponentModelRepository.class);
                componentModelRepo.save(componentModel);
            }

        }

        selectedComponentId.clearSelection(state);
        pageModelTab.showPageModelDetails(state);

    }

    @SuppressWarnings("unchecked")
    private T retrieveComponentModel(final String componentModelId) {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();

        final ComponentModelRepository componentModelRepo = cdiUtil
            .findBean(ComponentModelRepository.class);

        return (T) componentModelRepo
            .findById(Long.parseLong(componentModelId))
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ComponentModel with ID %s in the database",
                    componentModelId)));

    }

}
