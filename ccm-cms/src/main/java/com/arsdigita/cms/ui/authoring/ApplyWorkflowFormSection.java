/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;

import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.Folder;

import com.arsdigita.cms.ui.workflow.WorkflowsOptionPrintListener;
import com.arsdigita.globalization.GlobalizedMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.libreccm.workflow.AssignableTask;
import org.libreccm.workflow.AssignableTaskManager;
import org.libreccm.workflow.Workflow;
import org.libreccm.workflow.WorkflowManager;
import org.libreccm.workflow.WorkflowTemplate;
import org.libreccm.workflow.WorkflowTemplateRepository;
import org.librecms.CmsConstants;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.util.List;
import java.util.TooManyListenersException;

/**
 * A FormSection which will allow users with
 * SecrityConstants.APPLY_ALTERNATE_WORFLOWS permission to choose a different
 * workflow to apply to a new item.
 *
 * @author Stanislav Freidin (stas@arsdigita.com)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ApplyWorkflowFormSection
    extends FormSection
    implements FormInitListener {

    private static final Logger LOGGER = LogManager
        .getLogger(ApplyWorkflowFormSection.class);

    private RadioGroup radioGroup;
    private CreationSelector creationSelector;
    private ContentType contentType;
    private ApplyWorkflowPrintListener printListener;

    /**
     * Construct a new ApplyWorkflowFormSection
     */
    public ApplyWorkflowFormSection() {
        this(null);
    }

    /**
     * Construct a new ApplyWorkflowFormSection
     *
     * @param type
     */
    public ApplyWorkflowFormSection(final ContentType type) {
        this(type, new ColumnPanel(2, true));
    }

    /**
     * Construct a new ApplyWorkflowFormSection
     *
     * @param type
     * @param panel Container to use for this FormSection
     */
    public ApplyWorkflowFormSection(final ContentType type,
                                    final Container panel) {
        super(panel);

        radioGroup = new RadioGroup(new LongParameter("workflowSelect"));
        radioGroup.setClassAttr("vertical");
        contentType = type;
        printListener = new ApplyWorkflowPrintListener();

        try {
            // should we filter on WorkflowDefinitions where this user
            // is assigned to at least one initial task, or should we
            // assume that users with "alternate workflow" permission
            // are advanced enough to know what they're doing? 
            radioGroup.addPrintListener(printListener);
        } catch (TooManyListenersException ex) {
            LOGGER.error("Too many listeners", ex);
        }

        add(new Label(new GlobalizedMessage("cms.ui.authoring.workflow",
                                            CmsConstants.CMS_BUNDLE)));
        radioGroup.addValidationListener(new NotNullValidationListener() {

            @Override
            public void validate(final ParameterEvent event) {
                final PageState state = event.getPageState();
                if (!ApplyWorkflowFormSection.this.isVisible(state)) {
                    return;
                }
                super.validate(event);
            }

        });
        add(radioGroup);
        addInitListener(this);
    }

    /**
     * Initialises the workflow selection widget to the default workflow for the
     * content type.
     *
     * @param event
     *
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {

        final PageState state = event.getPageState();
        final ContentSection section = creationSelector.getContentSection(
            state);
        final WorkflowTemplate template = contentType.getDefaultWorkflow();
        if (template != null) {
            radioGroup.setValue(state, template.getWorkflowId());
        }
    }

    /**
     * Sets the CreationSelector which should be the same as that of the
     * creation component. This cannot be set in the constructor since for most
     * creation components, addWidgets() is called via the superclass
     * constructor, so this member will not yet be set.
     *
     * @param creationSelector CreationSelector to use for this FormSection
     */
    public void setCreationSelector(final CreationSelector creationSelector) {
        this.creationSelector = creationSelector;
    }

    /**
     * Sets the ContentType for the creation component.
     *
     * @param contentType ContentType to use for this FormSection
     */
    public void setContentType(final ContentType contentType) {
        this.contentType = contentType;
    }

    /**
     * Whether or not this component is visible. The additional visibility
     * requirement is that the user must have the
     * SecurityConstants.APPLY_ALTERNATE_WORKFLOWS privilege on the parent
     * folder.
     *
     * @param state The PageState
     *
     * @return
     */
    @Override
    public boolean isVisible(final PageState state) {
        boolean result = false;

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PermissionChecker permissionChecker = cdiUtil
            .findBean(PermissionChecker.class);
        ;

        if (super.isVisible(state)
                && permissionChecker
                .isPermitted(ItemPrivileges.APPLY_ALTERNATE_WORKFLOW,
                             creationSelector.getFolder(state))) {
            
            return !printListener.getCollection(state).isEmpty();
        }
        return result;
    }

    /**
     * Apply the proper initial workflow to the item. If the user has
     * SecurityConstants.APPLY_ALTERNATE_WORKFLOWS permission on the parent
     * folder <em>and</em> a workflow has been chosen, use this workflow.
     * Otherwise use the default workflow for the content type.
     *
     * @param state The PageState
     * @param item  The new ContentItem
     */
    public void applyWorkflow(final PageState state, final ContentItem item) {

        final Long flowId = (Long) radioGroup.getValue(state);
        final ContentSection section = creationSelector.getContentSection(
            state);
        final Folder folder = creationSelector.getFolder(state);
        final WorkflowTemplate template;

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PermissionChecker permissionChecker = cdiUtil
            .findBean(PermissionChecker.class);
        final WorkflowTemplateRepository templateRepo = cdiUtil
            .findBean(WorkflowTemplateRepository.class);

        if (flowId != null
                && permissionChecker.isPermitted(
                ItemPrivileges.APPLY_ALTERNATE_WORKFLOW, folder)) {
            template = templateRepo
                .findById(flowId)
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                "No WorkflowTemplate with ID %d in database. "
                    + "Where did that ID come from?")));
        } else {
            template = item.getContentType().getDefaultWorkflow();
        }

        if (template != null) {

            final WorkflowManager workflowManager = cdiUtil
                .findBean(WorkflowManager.class);

            final Workflow workflow = workflowManager.createWorkflow(template,
                                                                     item);
            workflowManager.start(workflow);

            if (!workflow.getTasks().isEmpty()) {

                if (workflow.getTasks().get(0) instanceof AssignableTask) {

                    final AssignableTaskManager taskManager = cdiUtil
                        .findBean(AssignableTaskManager.class);
                    final AssignableTask task = (AssignableTask) workflow
                        .getTasks()
                        .get(0);
                    taskManager.lockTask(task);
                }
            }
        }

    }

    private class ApplyWorkflowPrintListener
        extends WorkflowsOptionPrintListener {

        @Override
        protected ContentSection getContentSection(final PageState state) {
            return creationSelector.getContentSection(state);
        }

        @Override
        protected List<WorkflowTemplate> getCollection(final PageState state) {
            return super.getCollection(state);
        }

    }

}
