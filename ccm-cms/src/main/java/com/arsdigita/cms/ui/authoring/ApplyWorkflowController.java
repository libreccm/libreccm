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
package com.arsdigita.cms.ui.authoring;

import org.libreccm.security.PermissionChecker;
import org.libreccm.workflow.AssignableTask;
import org.libreccm.workflow.AssignableTaskManager;
import org.libreccm.workflow.Workflow;
import org.libreccm.workflow.WorkflowManager;
import org.libreccm.workflow.WorkflowTemplate;
import org.libreccm.workflow.WorkflowTemplateRepository;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.ContentTypeRepository;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.nio.charset.IllegalCharsetNameException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class ApplyWorkflowController {

    @Inject
    private ContentTypeRepository typeRepo;

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private WorkflowTemplateRepository templateRepo;

    @Inject
    private WorkflowManager workflowManager;

    @Inject
    private AssignableTaskManager assignableTaskManager;

    @Inject
    private PermissionChecker permissionChecker;

    @Transactional(Transactional.TxType.REQUIRED)
    protected WorkflowTemplate getDefaultWorkflow(final ContentType contentType) {

        Objects.requireNonNull(contentType);

        final ContentType type = typeRepo
            .findById(contentType.getObjectId())
            .orElseThrow(() -> new IllegalCharsetNameException(String.format(
            "No ContentType with ID %d in the database. Where did that ID come from?",
            contentType.getObjectId())));

        return type.getDefaultWorkflow();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    Long getDefaultWorkflowId(final ContentType contentType) {
        final WorkflowTemplate workflowTemplate
                                   = getDefaultWorkflow(contentType);
        if (workflowTemplate == null) {
            return null;
        } else {
            return workflowTemplate.getWorkflowId();
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<WorkflowTemplate> getWorkflowTemplates(
        final ContentSection section) {

        final ContentSection contentSection = sectionRepo
            .findById(section.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContentSection with ID %d in the database. "
                + "Where did that ID come from?",
            section.getObjectId())));

        return new ArrayList<>(contentSection.getWorkflowTemplates());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void applyWorkflow(final ContentItem item,
                                 final Folder folder,
                                 final Long workflowTemplateId) {

        final WorkflowTemplate workflowTemplate;
        if (workflowTemplateId == null
                && permissionChecker
                .isPermitted(ItemPrivileges.APPLY_ALTERNATE_WORKFLOW, folder)) {

            workflowTemplate = templateRepo
                .findById(workflowTemplateId)
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                "No WorkflowTemplate with ID %d in database. "
                    + "Where did that ID come from?",
                workflowTemplateId)));
        } else {
            workflowTemplate = item.getContentType().getDefaultWorkflow();
        }

        if (workflowTemplate != null) {

            final Workflow workflow = workflowManager
                .createWorkflow(workflowTemplate, item);
            workflowManager.start(workflow);

            if (!workflow.getTasks().isEmpty()) {

                if (workflow.getTasks().get(0) instanceof AssignableTask) {

                    final AssignableTask task = (AssignableTask) workflow
                        .getTasks()
                        .get(0);
                    assignableTaskManager.lockTask(task);
                }
            }

        }

    }

}
