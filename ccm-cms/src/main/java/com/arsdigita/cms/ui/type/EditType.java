/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.ui.type;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.cms.CMS;

import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentType;
import org.librecms.lifecycle.LifecycleDefinition;

import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.util.UncheckedWrapperException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.workflow.Workflow;
import org.libreccm.workflow.WorkflowRepository;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentTypeManager;
import org.librecms.contentsection.ContentTypeRepository;
import org.librecms.lifecycle.LifecycleDefinitionRepository;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TooManyListenersException;

/**
 * This class contains a form component to edit a content type
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @author Michael Pih (pihman@arsdigita.com)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class EditType extends CMSForm
    implements FormInitListener, FormProcessListener {

    private static final Logger LOGGER = LogManager.getLogger(EditType.class);

    private final SingleSelectionModel<String> selectedTypeModel;

    // Form widgets
    private final Hidden idField;
//    private TextField m_label;
//    private TextArea m_description;
    private final SingleSelect lifecycleSelect;
    private final SingleSelect workflowSelect;
    private final Submit submitButton;
    private final Submit cancelButton;

    /**
     * @param selectedTypeModel The content type selection model. This tells the
     *                          form which content type is selected.
     */
    public EditType(final SingleSelectionModel<String> selectedTypeModel) {
        super("EditContentType");

        this.selectedTypeModel = selectedTypeModel;

        idField = new Hidden(new LongParameter("id"));
        idField.addValidationListener(new NotNullValidationListener());
        super.add(idField);

//        add(new Label(new GlobalizedMessage("cms.ui.type.label",
//                                            CmsConstants.CMS_BUNDLE)));
//        m_label = new TextField(new StringParameter("label"));
//        m_label.addValidationListener(new NotNullValidationListener());
//        m_label.setSize(40);
//        m_label.setMaxLength(1000);
//        add(m_label);
//
//        add(new Label(new GlobalizedMessage("cms.ui.description",
//                                            CmsConstants.CMS_BUNDLE)));
//        m_description = new TextArea(new StringParameter("description"));
//        m_description.addValidationListener(
//            new StringLengthValidationListener(4000));
//        m_description.setCols(40);
//        m_description.setRows(5);
//        m_description.setWrap(TextArea.SOFT);
//        add(m_description);
        super.add(new Label(new GlobalizedMessage("cms.ui.type.lifecycle",
                                                  CmsConstants.CMS_BUNDLE)));
        lifecycleSelect = new SingleSelect(new LongParameter("lifecycle"));
        try {
            lifecycleSelect.addPrintListener(new SelectLifecyclePrintListener());
        } catch (TooManyListenersException e) {
            throw new UncheckedWrapperException("TooManyListeners: " + e
                .getMessage(), e);
        }
        super.add(lifecycleSelect);

        super.add(new Label(new GlobalizedMessage("cms.ui.type.workflow",
                                                  CmsConstants.CMS_BUNDLE)));
        workflowSelect = new SingleSelect(new LongParameter("workflow"));
        try {
            workflowSelect.addPrintListener(new SelectWorkflowPrintListener());
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException("TooManyListeners: " + ex
                .getMessage(), ex);
        }
        super.add(workflowSelect);

        final SimpleContainer buttonContainer = new SimpleContainer();
        submitButton = new Submit(new GlobalizedMessage("cms.ui.save",
                                                        CmsConstants.CMS_BUNDLE));
        buttonContainer.add(submitButton);
        cancelButton = new Submit(new GlobalizedMessage("cms.ui.cancel",
                                                        CmsConstants.CMS_BUNDLE));
        buttonContainer.add(cancelButton);
        super.add(buttonContainer, ColumnPanel.FULL_WIDTH | ColumnPanel.CENTER);

        super.addInitListener(this);
        super.addSubmissionListener(new TypeSecurityListener());
        super.addProcessListener(this);
    }

    /**
     * Returns true if the "cancel" button was submitted.
     *
     * @param state The page state
     *
     * @return True if the form was cancelled, false otherwise
     */
    @Override
    public boolean isCancelled(final PageState state) {
        return cancelButton.isSelected(state);
    }

    /**
     * Returns the "cancel" button on the form
     *
     * @return the cancel button on the form
     */
    public Submit getCancelButton() {
        return cancelButton;
    }

    /**
     * Populates the form with the content type properties.
     *
     * @param event
     */
    @Override
    public void init(final FormSectionEvent event) {

        final FormData data = event.getFormData();
        final PageState state = event.getPageState();

//        final ContentSection section = CMS.getContext().getContentSection();
//
//        final KernelConfig kernelConfig = KernelConfig.getConfig();
        final ContentType type = getContentType(state);
        final long typeId = type.getObjectId();
//        final String label = type.getLabel().getValue(kernelConfig
//            .getDefaultLocale());
//        final String description = type.getDescription().getValue(kernelConfig
//            .getDefaultLocale());

        data.put(idField.getName(), typeId);
//        data.put(m_label.getName(), label);
//        data.put(m_description.getName(), description);

        final LifecycleDefinition cycle = type.getDefaultLifecycle();
        if (cycle != null) {
            data.put(lifecycleSelect.getName(), cycle.getDefinitionId());
        }

        final Workflow template = type.getDefaultWorkflow();
        if (template != null) {
            data.put(workflowSelect.getName(), template.getWorkflowId());
        }
    }

    /**
     * Fetches the currently selected content type from the single selection
     * model.
     */
    private ContentType getContentType(final PageState state) {

        final String key = selectedTypeModel.getSelectedKey(state);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentTypeRepository typeRepo = cdiUtil.findBean(
            ContentTypeRepository.class);

        final Optional<ContentType> result;
        try {
            result = typeRepo.findByIdAndFetchAttributes(Long.parseLong(key),
                                                         "defaultLifecycle",
                                                         "defaultWorkflow");
        } catch (NumberFormatException ex) {
            throw new UncheckedWrapperException(String.format(
                "The provided key \"%s\" is not a long.", key),
                                                ex);
        }

        if (result.isPresent()) {
            return result.get();
        } else {
            throw new UncheckedWrapperException(String.format(
                "ContentType with ID %s not found.", key));
        }
    }

    /**
     * Edits the content type.
     *
     * @param event
     *
     * @throws FormProcessException
     */
    @Override
    public void process(final FormSectionEvent event) throws
        FormProcessException {
        final FormData data = event.getFormData();

        // Get the current content section.
        final ContentSection section = CMS.getContext().getContentSection();

        // Read form variables.
        final Long key = (Long) data.get(idField.getName());
//        final String label = (String) data.get(m_label.getName());
//        final String description = (String) data.get(m_description.getName());
        final Long lifecycleId = (Long) data.get(lifecycleSelect.getName());
        final Long workflowId = (Long) data.get(workflowSelect.getName());

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentTypeRepository typeRepo = cdiUtil.findBean(
            ContentTypeRepository.class);
        final LifecycleDefinitionRepository lifecycleDefRepo = cdiUtil.findBean(
            LifecycleDefinitionRepository.class);
        final WorkflowRepository workflowRepo = cdiUtil
            .findBean(WorkflowRepository.class);
        final ContentTypeManager typeManager = cdiUtil.findBean(
            ContentTypeManager.class);

        final Optional<ContentType> type = typeRepo.findById(key);
        if (!type.isPresent()) {
            LOGGER.error("Can't find ContentType with key {}", key);
            throw new FormProcessException(new GlobalizedMessage(
                "cms.ui.type.content_editing_failed",
                CmsConstants.CMS_BUNDLE,
                new Object[]{key}));
        }

//        final KernelConfig kernelConfig = KernelConfig.getConfig();
//        type.get().getLabel().addValue(kernelConfig.getDefaultLocale(), label);
//        type.get().getDescription().addValue(kernelConfig.getDefaultLocale(),
//                                       description);
        typeRepo.save(type.get());

        // Handle default lifecycle and workflow.
        final LifecycleDefinition defaultLifecycle;
        if (lifecycleId == 0) {
            defaultLifecycle = null;
        } else {
            defaultLifecycle = lifecycleDefRepo.findById(lifecycleId).get();
        }
        final Workflow defaultWorkflow;
        if (workflowId == 0) {
            defaultWorkflow = null;
        } else {
            defaultWorkflow = workflowRepo.findById(workflowId).get();
        }

        typeManager.setDefaultLifecycle(type.get(), defaultLifecycle);
        typeManager.setDefaultWorkflow(type.get(), defaultWorkflow);

    }

    /**
     * Print listener to generate the select widget for the list of lifecyle
     * definitions.
     */
    private class SelectLifecyclePrintListener implements PrintListener {

        @Override
        public void prepare(final PrintEvent event) {

            final SingleSelect lifecycleSelect = (SingleSelect) event
                .getTarget();
            lifecycleSelect.clearOptions();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ContentTypeAdminPaneController controller = cdiUtil
                .findBean(ContentTypeAdminPaneController.class);

            // Get the current content section
            ContentSection section = CMS.getContext().getContentSection();

            lifecycleSelect.addOption(new Option(
                "0",
                new Label(new GlobalizedMessage("cms.ui.type.lifecycle.select",
                                                CmsConstants.CMS_BUNDLE))));

            final List<LifecycleDefinition> cycles = controller
                .getLifecycleDefinitions(section);
            final Locale defaultLocale = KernelConfig.getConfig()
                .getDefaultLocale();
            cycles.forEach(cycle -> {
                lifecycleSelect.addOption(
                    new Option(Long.toString(cycle.getDefinitionId()),
                               new Text(cycle.getLabel().getValue(defaultLocale))));
            });
        }

    }

    /**
     * Print listener to generate the select widget for the list of workflow
     * templates.
     */
    private class SelectWorkflowPrintListener implements PrintListener {

        @Override
        public void prepare(final PrintEvent event) {

            final SingleSelect workflowSelect = (SingleSelect) event.getTarget();
            workflowSelect.clearOptions();

            // Get the current content section
            ContentSection section = CMS.getContext().getContentSection();

            workflowSelect.addOption(new Option(
                "0",
                new Label(new GlobalizedMessage("cms.ui.type.workflow.select",
                                                CmsConstants.CMS_BUNDLE))));

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ContentTypeAdminPaneController controller = cdiUtil
                .findBean(ContentTypeAdminPaneController.class);

            final List<Workflow> templates = controller
                .getWorkflowTemplates(section);
            final Locale defaultLocale = KernelConfig.getConfig()
                .getDefaultLocale();
            templates.forEach(template -> {
                workflowSelect.addOption(
                    new Option(
                        Long.toString(template.getWorkflowId()),
                        new Text(template.getName().getValue(defaultLocale))));
            });

        }

    }

}
