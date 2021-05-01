/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui.contentsections.documents;

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.PermissionChecker;
import org.libreccm.workflow.AssignableTask;
import org.libreccm.workflow.AssignableTaskManager;
import org.libreccm.workflow.Workflow;
import org.libreccm.workflow.WorkflowManager;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.privileges.ItemPrivileges;
import org.librecms.ui.contentsections.ContentSectionsUi;
import org.librecms.ui.contentsections.ItemPermissionChecker;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Controller for managing the {@link Workflow} assigned to a
 * {@link ContentItem}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/{sectionIdentifier}/documents/{documentPath:(.+)?}/@workflow/")
@Controller
public class DocumentWorkflowController {

    /**
     * Used to manage the tasks of the workflow.
     */
    @Inject
    private AssignableTaskManager assignableTaskManager;

    /**
     * Used to retrieve the current content item.
     */
    @Inject
    private ContentItemRepository itemRepo;

    /**
     * Common functions for controllers working with {@link ContentSection}s.
     */
    @Inject
    private ContentSectionsUi sectionsUi;

    @Inject
    private DefaultStepsMessageBundle defaultStepsMessageBundle;
    
    /**
     * Common functions for controllers working with {@link ContentItem}s.
     */
    @Inject
    private DocumentUi documentUi;

    /**
     * {@link IdentifierParser} instance for parsing identifiers.
     */
    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private ItemPermissionChecker itemPermissionChecker;
    
    /**
     * Used to check permissions for the current content item.
     */
    @Inject
    private PermissionChecker permissionChecker;

    /**
     * Provides access to the properties of the select content item in the view.
     */
    @Inject
    private SelectedDocumentModel selectedDocumentModel;

    /**
     * Used to provided data for the view without a named bean.
     */
    @Inject
    private Models models;

    /**
     * Used to manage the workflow of a {@link ContentItem}.
     */
    @Inject
    private WorkflowManager workflowManager;

    /**
     * Lock a task of a workflow.
     *
     * @param sectionIdentifier The identifier of the curent content section.
     * @param documentPath      The path of the current document.
     * @param taskIdentifier    The identifier of the task to lock.
     * @param returnUrl         The URL to return to.
     *
     * @return A redirect to the {@code returnUrl}.
     */
    @POST
    @Path("/tasks/${taskIdentifier}/@lock")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String lockTask(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("documentPath") final String documentPath,
        @PathParam("taskIdentifier") final String taskIdentifier,
        @FormParam("returnUrl") final String returnUrl
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifier);
        }
        final ContentSection section = sectionResult.get();

        final Optional<ContentItem> itemResult = itemRepo
            .findByPath(section, documentPath);
        if (!itemResult.isPresent()) {
            return documentUi.showDocumentNotFound(section, documentPath);
        }
        final ContentItem item = itemResult.get();
        selectedDocumentModel.setContentItem(item);
        if (!itemPermissionChecker.canEditItem(item)) {
            return documentUi.showAccessDenied(
                section, 
                documentPath, 
                defaultStepsMessageBundle.getMessage(
                    "workflows.task.lock.access_denied"
                )
            );
        }

        final Optional<AssignableTask> taskResult = findTask(
            item, taskIdentifier
        );
        if (!taskResult.isPresent()) {
            return showTaskNotFound(section, documentPath, taskIdentifier);
        }

        final AssignableTask task = taskResult.get();
        assignableTaskManager.lockTask(task);

        return String.format("redirect:%s", returnUrl);
    }

    /**
     * Unlocks/releases a task.
     *
     * @param sectionIdentifier The identifier of the current content section.
     * @param documentPath      The identifier of the current document.
     * @param taskIdentifier    The identifier of the task to unlock.
     * @param returnUrl         The URL to return to.
     *
     * @return A redirect to the {@code returnUrl}.
     */
    @POST
    @Path("/tasks/${taskIdentifier}/@unlock")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String unlockTask(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("documentPath") final String documentPath,
        @PathParam("taskIdentifier") final String taskIdentifier,
        @FormParam("returnUrl") final String returnUrl
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifier);
        }
        final ContentSection section = sectionResult.get();

        final Optional<ContentItem> itemResult = itemRepo
            .findByPath(section, documentPath);
        if (!itemResult.isPresent()) {
            return documentUi.showDocumentNotFound(section, documentPath);
        }
        final ContentItem item = itemResult.get();
        selectedDocumentModel.setContentItem(item);
         if (!itemPermissionChecker.canEditItem(item)) {
            return documentUi.showAccessDenied(
                section, 
                documentPath, 
                defaultStepsMessageBundle.getMessage(
                    "workflows.task.unlock.access_denied"
                )
            );
        }

        final Optional<AssignableTask> taskResult = findTask(
            item, taskIdentifier
        );
        if (!taskResult.isPresent()) {
            return showTaskNotFound(section, documentPath, taskIdentifier);
        }

        final AssignableTask task = taskResult.get();
        assignableTaskManager.unlockTask(task);

        return String.format("redirect:%s", returnUrl);
    }

    /**
     * Finishes a task.
     *
     * @param sectionIdentifier The identifier of the current content section.
     * @param documentPath      The path of the current document.
     * @param taskIdentifier    The identifier of task to finish.
     * @param comment           A comment to add to the task.
     * @param returnUrl         The URL to return to.
     *
     * @return A redirect to the {@code returnUrl}.
     */
    @POST
    @Path("/tasks/${taskIdentifier}/@finish")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String finishTask(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("documentPath") final String documentPath,
        @PathParam("taskIdentifier") final String taskIdentifier,
        @FormParam("comment") @DefaultValue("") final String comment,
        @FormParam("returnUrl") final String returnUrl
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifier);
        }
        final ContentSection section = sectionResult.get();

        final Optional<ContentItem> itemResult = itemRepo
            .findByPath(section, documentPath);
        if (!itemResult.isPresent()) {
            return documentUi.showDocumentNotFound(section, documentPath);
        }
        final ContentItem item = itemResult.get();
        selectedDocumentModel.setContentItem(item);
         if (!itemPermissionChecker.canEditItem(item)) {
            return documentUi.showAccessDenied(
                section, 
                documentPath, 
                defaultStepsMessageBundle.getMessage(
                    "workflows.task.finish.access_denied"
                )
            );
        }

        final Optional<AssignableTask> taskResult = findTask(
            item, taskIdentifier
        );
        if (!taskResult.isPresent()) {
            return showTaskNotFound(section, documentPath, taskIdentifier);
        }

        final AssignableTask task = taskResult.get();
        if (comment.isEmpty()) {
            assignableTaskManager.finish(task);
        } else {
            assignableTaskManager.finish(task, comment);
        }

        return String.format("redirect:%s", returnUrl);
    }

    /**
     * Apply another workflow to a content item.
     *
     * @param sectionIdentifier The identifier of the current content section.
     * @param documentPath      The path of the currentd document.
     * @param newWorkflowUuid   The UUID of the the workflow definition form
     *                          which the new workflow is created.
     * @param returnUrl         The URL to return to.
     *
     * @return A redirect to the {@code returnUrl}.
     */
    @POST
    @Path("@workflow/@applyAlternative/{workflowIdentifier}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String applyAlternateWorkflow(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("documentPath") final String documentPath,
        @FormParam("newWorkflowUuid") final String newWorkflowUuid,
        @FormParam("returnUrl") final String returnUrl
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifier);
        }
        final ContentSection section = sectionResult.get();

        final Optional<ContentItem> itemResult = itemRepo
            .findByPath(section, documentPath);
        if (!itemResult.isPresent()) {
            return documentUi.showDocumentNotFound(section, documentPath);
        }
        final ContentItem item = itemResult.get();
        selectedDocumentModel.setContentItem(item);
         if (!itemPermissionChecker.canApplyAlternateWorkflow(item)) {
            return documentUi.showAccessDenied(
                section, 
                documentPath, 
                defaultStepsMessageBundle.getMessage(
                    "workflows.task.apply_alternate.access_denied"
                )
            );
        }

        if (!permissionChecker.isPermitted(
            ItemPrivileges.APPLY_ALTERNATE_WORKFLOW, item)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifier,
                "documentPath", documentPath
            );
        }

        final Optional<Workflow> workflowResult = section
            .getWorkflowTemplates()
            .stream()
            .filter(template -> template.getUuid().equals(newWorkflowUuid))
            .findAny();
        if (!workflowResult.isPresent()) {
            models.put("section", section.getLabel());
            models.put("workflowUuid", newWorkflowUuid);
            return "org/librecms/ui/contentsection/documents/workflow-not-found.xhtml";
        }

        workflowManager.createWorkflow(workflowResult.get(), item);
        return String.format("redirect:%s", returnUrl);
    }

    /**
     * Starts the workflow assigned to an content item.
     *
     * @param sectionIdentifier The identifier of the current content section.
     * @param documentPath      The path of the current document.
     * @param returnUrl         The URL to return to.
     *
     * @return A redirect to the {@code returnUrl}.
     */
    @POST
    @Path("/@start")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String startWorkflow(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("documentPath") final String documentPath,
        @FormParam("returnUrl") final String returnUrl
    ) {
        return restartWorkflow(sectionIdentifier, documentPath, returnUrl);
    }
    
    /**
     * Restarts the workflow assigned to an content item.
     *
     * @param sectionIdentifier The identifier of the current content section.
     * @param documentPath      The path of the current document.
     * @param returnUrl         The URL to return to.
     *
     * @return A redirect to the {@code returnUrl}.
     */
    @POST
    @Path("/@restart")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String restartWorkflow(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("documentPath") final String documentPath,
        @FormParam("returnUrl") final String returnUrl
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifier);
        }
        final ContentSection section = sectionResult.get();

        final Optional<ContentItem> itemResult = itemRepo
            .findByPath(section, documentPath);
        if (!itemResult.isPresent()) {
            return documentUi.showDocumentNotFound(section, documentPath);
        }
        final ContentItem item = itemResult.get();
        selectedDocumentModel.setContentItem(item);
         if (!itemPermissionChecker.canEditItem(item)) {
            return documentUi.showAccessDenied(
                section, 
                documentPath, 
                defaultStepsMessageBundle.getMessage(
                    "workflows.task.restart.access_denied"
                )
            );
        }

        if (!permissionChecker.isPermitted(
            ItemPrivileges.APPLY_ALTERNATE_WORKFLOW, item)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifier,
                "documentPath", documentPath
            );
        }

        if (item.getWorkflow() != null) {
            workflowManager.start(item.getWorkflow());
        }

        return String.format("redirect:%s", returnUrl);
    }

    /**
     * Helper method to find a specific task of the current workflow of a
     * content item.
     *
     * @param item           The content item.
     * @param taskIdentifier The identifier of the task.
     *
     * @return An {@link Optional} with the task identified by the provided
     *         {@code taskIdentifier} or an empty {@link Optional} if no such
     *         task could be found.
     */
    private Optional<AssignableTask> findTask(
        final ContentItem item,
        final String taskIdentifier
    ) {
        final Workflow workflow = item.getWorkflow();
        if (workflow == null) {
            return Optional.empty();
        }

        final Identifier identifier = identifierParser.parseIdentifier(
            taskIdentifier
        );
        switch (identifier.getType()) {
            case ID:
                return workflow
                    .getTasks()
                    .stream()
                    .filter(task -> task instanceof AssignableTask)
                    .map(task -> (AssignableTask) task)
                    .filter(
                        task -> task.getTaskId() == Long
                        .parseLong(identifier.getIdentifier())
                    ).findAny();
            default:
                return workflow
                    .getTasks()
                    .stream()
                    .filter(task -> task instanceof AssignableTask)
                    .map(task -> (AssignableTask) task)
                    .filter(
                        task -> task.getUuid().equals(
                            identifier.getIdentifier()
                        )
                    ).findAny();
        }
    }

    /**
     * Helper method for showing the "task not found" error message.
     *
     * @param section
     * @param documentPath
     * @param taskIdentifier
     *
     * @return
     */
    private String showTaskNotFound(
        final ContentSection section,
        final String documentPath,
        final String taskIdentifier
    ) {
        models.put("section", section.getLabel());
        models.put("documentPath", documentPath);
        models.put("taskIdentifier", taskIdentifier);
        return "org/librecms/ui/contentsection/documents/task-not-found.xhtml";
    }

}
