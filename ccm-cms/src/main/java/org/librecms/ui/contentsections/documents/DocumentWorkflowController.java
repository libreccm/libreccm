/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/{sectionIdentifier}/documents/{documentPath:(.+)?}/@workflow/")
@Controller
public class DocumentWorkflowController {

    @Inject
    private AssignableTaskManager assignableTaskManager;

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private ContentSectionsUi sectionsUi;

    @Inject
    private DocumentUi documentUi;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private PermissionChecker permissionChecker;

    @Inject
    private SelectedDocumentModel selectedDocumentModel;

    @Inject
    private Models models;

    @Inject
    private WorkflowManager workflowManager;

    @POST
    @Path("/tasks/${taskIdentifier}/@lock")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String lockTask(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
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

    @POST
    @Path("/tasks/${taskIdentifier}/@unlock")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String unlockTask(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
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

    @POST
    @Path("/tasks/${taskIdentifier}/@finish")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String finishTask(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
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

    @POST
    @Path("@workflow/@applyAlternative/{workflowIdentifier}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String applyAlternateWorkflow(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
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
            return "org/librecms/ui/contentsection/workflow-not-found.xhtml";
        }

        workflowManager.createWorkflow(workflowResult.get(), item);
        return String.format("redirect:%s", returnUrl);
    }

    @POST
    @Path("/@restart")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String restartWorkflow(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
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

    private String showTaskNotFound(
        final ContentSection section,
        final String documentPath,
        final String taskIdentifier
    ) {
        models.put("section", section.getLabel());
        models.put("documentPath", documentPath);
        models.put("taskIdentifier", taskIdentifier);
        return "org/librecms/ui/contentsection/task-not-found.xhtml";
    }

}
