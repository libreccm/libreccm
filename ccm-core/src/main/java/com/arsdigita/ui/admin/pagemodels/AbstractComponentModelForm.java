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
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.admin.AdminUiConstants;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.pagemodel.ComponentModel;
import org.libreccm.pagemodel.ComponentModelRepository;
import org.libreccm.pagemodel.PageModel;

/**
 * Base form for creating forms for editing/creating components of a
 * {@link PageModel}.
 *
 * Subclasses must provided a constructor with the following signature:
 * {@code SomeComponentModelForm(PageModelTab, ParameterSingleSelectionModel, ParameterSingleSelectionModel)}.
 *
 * This constructor has to call
 * {@link #AbstractComponentModelForm(java.lang.String, com.arsdigita.ui.admin.pagemodels.PageModelsTab, com.arsdigita.bebop.ParameterSingleSelectionModel, com.arsdigita.bebop.ParameterSingleSelectionModel)}
 *
 * with the provided parameters and a unique name for the form. Usually this be
 * the name of the component model which is associated with the form and the
 * suffix {@code Form}.
 *
 * The constructor is called is using reflection. The parameters passed to the
 * constructor are:
 * <ol>
 * <li>The {@link PageModelsTab} in which the form is displayed.</li>
 * <li>The {@link ParameterSingleSelectionModel} which holds the ID of the
 * currently selected {@link PageModel}.</li>
 * <li>The {@link ParameterSingleSelectionModel} which holds the ID of the
 * currently selected {@link ComponentModel}. The selected key of the selection
 * model might be null if a new component model is created.</li>
 * </ol>
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
     * The {@link PageModelsTab} in which the form is used
     */
    private final PageModelsTab pageModelTab;
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
        final PageModelsTab pageModelTab,
        final ParameterSingleSelectionModel<String> selectedModelId,
        final ParameterSingleSelectionModel<String> selectedComponentId) {

        super(name);

        this.pageModelTab = pageModelTab;
        this.selectedModelId = selectedModelId;
        this.selectedComponentId = selectedComponentId;

        createWidgets();

        super.addInitListener(this);
        super.addValidationListener(this);
        super.addProcessListener(this);
    }

    /**
     * Helper method called by the constructor to create the widgets of the
     * form. The method also calls the {@link #addWidgets()} after the basic
     * widgets have been created and adds the {@link SaveCancelSection} at the
     * end.
     */
    private void createWidgets() {
        keyField = new TextField(COMPONENT_KEY);
        keyField.setLabel(new GlobalizedMessage(
            "ui.admin.pagemodels.components.key.label",
            AdminUiConstants.ADMIN_BUNDLE));
        super.add(keyField);

        addWidgets();

        saveCancelSection = new SaveCancelSection();
        super.add(saveCancelSection);
    }

    /**
     * Provides access to the {@link PageModelsTab}.
     *
     * @return
     */
    protected final PageModelsTab getPageModelTab() {
        return pageModelTab;
    }

    /**
     * Provides access the {@link ParameterSingleSelectionModel} holding the ID
     * of the currently selected {@link ComponentModel}. The selected key of the
     * selection model is {@code null} if a new {@link ComponentModel} is
     * created.
     *
     * @return
     */
    protected final ParameterSingleSelectionModel<String> getSelectedComponentId() {
        return selectedComponentId;
    }

    /**
     * Provides access to the {@link ParameterSingleSelectionModel} holding the
     * ID of the currently selected {@link PageModel}.
     *
     * @return
     */
    protected final ParameterSingleSelectionModel<String> getSelectedModelId() {
        return selectedModelId;
    }

    /**
     * Provides access to the {@link SaveCancelSection} of the form allowing
     * subclasses to check if the <em>Save</em> button of the
     * {@link SaveCancelSection} has been pressed.
     *
     * @return
     */
    protected final SaveCancelSection getSaveCancelSection() {
        return saveCancelSection;
    }

    /**
     * Provides access to the currently selected {@link PageModel}. The
     * implementation for the init and validation listeners
     * ({@link #init(com.arsdigita.bebop.event.FormSectionEvent)} and
     * {@link #validate(com.arsdigita.bebop.event.FormSectionEvent)} initialise
     * this field.
     *
     * @return
     */
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

    /**
     * Init listener for the component form. Subclasses should override this
     * method to initialise their fields. If this method is overridden the
     * overriding method <strong>must</strong> call {@code super.init(event)}.
     * Otherwise the {@link #keyField} will not be initialised properly. Also
     * the method loads the selected current component model from the database
     * and stores it in the {@link #componentModel} field. Overriding methods
     * can access the field using the {@link #getComponentModel()} method. If
     * {@link super.init(event)} is not called the {@link #componentModel} field
     * will not be initialised.
     *
     * @param event The event which caused the listener to be invoked.
     *
     * @throws FormProcessException
     */
    @Override
    @SuppressWarnings("unchecked")
    public void init(final FormSectionEvent event) throws FormProcessException {

        final PageState state = event.getPageState();
        final String selectedComponentIdStr = selectedComponentId
            .getSelectedKey(state);

        if (selectedComponentIdStr != null
                && !selectedComponentIdStr.isEmpty()) {

            
            componentModel = loadSelectedComponent(
                Long.parseLong(selectedComponentIdStr));

            keyField.setValue(state, componentModel.getKey());
        }
    }
    
    @SuppressWarnings("unchecked")
    protected T loadSelectedComponent(final long componentId) {
        
         final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ComponentModelRepository componentModelRepo = cdiUtil
                .findBean(ComponentModelRepository.class);
        
        return (T) componentModelRepo
                .findById(componentId)
                .orElseThrow(() -> new IllegalArgumentException(String
                .format("No ComponentModel with ID %d in the database.",
                        componentId)));
    }

    /**
     * Validation listener for the component form. Subclasses should override
     * this method to validate their fields if necessary.. If this method is
     * overridden the overriding method <strong>must</strong> call
     * {@code super.validate(event)}. Otherwise the {@link #keyField} will not
     * be validated properly.
     *
     * @param event The event which caused the listener to be invoked.
     *
     * @throws FormProcessException
     */
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

    /**
     * Process listener for the component form. This method can't be overridden.
     * Instead subclasses have to implement
     * {@link #updateComponentModel(org.libreccm.pagemodel.ComponentModel, com.arsdigita.bebop.PageState, com.arsdigita.bebop.FormData)}
     * to set their specific values on the current component model. The
     * implementation of that method is called by the this method.
     *
     * @param event The event which caused the listener to be invoked.
     *
     * @throws FormProcessException
     */
    @Override
    public final void process(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

        if (saveCancelSection.getSaveButton().isSelected(state)) {

            final String selectedModelIdStr = selectedModelId
                .getSelectedKey(state);
            final String selectedComponentIdStr = selectedComponentId
                .getSelectedKey(state);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PageModelsController controller = cdiUtil
                .findBean(PageModelsController.class);

            final FormData data = event.getFormData();
            final String keyValue = data.getString(COMPONENT_KEY);

            if (selectedComponentIdStr == null
                    || selectedComponentIdStr.isEmpty()) {

                componentModel = createComponentModel();
                componentModel.setKey(keyValue);
                updateComponentModel(componentModel, state, data);

                controller.addComponentModel(Long.parseLong(selectedModelIdStr),
                                             componentModel);
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

    /**
     * Helper method for retrieving the component model from the database.
     *
     * @param componentModelId The ID of the component model to retrieve.
     *
     * @return The component model.
     */
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
