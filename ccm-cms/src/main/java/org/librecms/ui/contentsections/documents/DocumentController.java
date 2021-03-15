/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections.documents;

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.PermissionChecker;
import org.libreccm.workflow.AssignableTask;
import org.libreccm.workflow.AssignableTaskManager;
import org.libreccm.workflow.TaskManager;
import org.libreccm.workflow.Workflow;
import org.libreccm.workflow.WorkflowManager;
import org.libreccm.workflow.WorkflowRepository;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderRepository;
import org.librecms.contentsection.FolderType;
import org.librecms.contentsection.privileges.ItemPrivileges;
import org.librecms.lifecycle.LifecycleDefinition;
import org.librecms.lifecycle.LifecycleManager;
import org.librecms.lifecycle.Phase;
import org.librecms.ui.contentsections.ContentSectionsUi;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/{sectionIdentifier}/documents")
@Controller
@Named("CmsDocumentController")
public class DocumentController {

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private ContentSectionsUi sectionsUi;

    @Inject
    private FolderRepository folderRepo;

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private Instance<MvcAuthoringStep> authoringSteps;

    @Inject
    private Instance<MvcDocumentCreateStep<?>> createSteps;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private LifecycleManager lifecycleManager;

    @Inject
    private Models models;

    @Inject
    private PermissionChecker permissionChecker;

    @Inject
    private PublishStepModel publishStepModel;

    @Inject
    private SelectedDocumentModel selectedDocumentModel;

    @Inject
    private WorkflowManager workflowManager;

    @Inject
    private AssignableTaskManager assignableTaskManager;

    @Inject
    private TaskManager taskManager;

    @Inject
    private WorkflowRepository workflowRepository;

    @GET
    @Path("/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String redirectToDocumentFolders(
        @PathParam("sectionIdentifider") final String sectionIdentifier
    ) {
        return String.format(
            "redirect:/%s/documentfolders/",
            sectionIdentifier
        );
    }

    @Path("/@create/{documentType}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public MvcDocumentCreateStep<? extends ContentItem> createDocument(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
        @PathParam("documentType") final String documentType
    ) {
        return createDocument(sectionIdentifier, "", documentType);
    }

    @Path("/{folderPath:(.+)?}/@create/{documentType}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    @SuppressWarnings("unchecked")
    public MvcDocumentCreateStep<? extends ContentItem> createDocument(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
        @PathParam("folderPath") final String folderPath,
        @PathParam("documentType") final String documentType
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);
        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return new ContentSectionNotFound();
        }
        final ContentSection section = sectionResult.get();

        final Folder folder;
        if (folderPath.isEmpty()) {
            folder = section.getRootDocumentsFolder();
        } else {
            final Optional<Folder> folderResult = folderRepo
                .findByPath(
                    section, folderPath, FolderType.DOCUMENTS_FOLDER
                );
            if (!folderResult.isPresent()) {
                models.put("section", section.getLabel());
                models.put("folderPath", folderPath);
                return new FolderNotFound();
            }
            folder = folderResult.get();
        }

        final Class<? extends ContentItem> documentClass;
        try {
            documentClass = (Class<? extends ContentItem>) Class.forName(
                documentType
            );
        } catch (ClassNotFoundException ex) {
            models.put("documentType", documentType);
            return new DocumentTypeClassNotFound();
        }

        final boolean hasRequestedType = section
            .getContentTypes()
            .stream()
            .anyMatch(
                type -> type.getContentItemClass().equals(documentType)
            );
        if (!hasRequestedType) {
            models.put("documentType", documentType);
            models.put("section", section.getLabel());
            return new ContentTypeNotAvailable();
        }

        final Instance<MvcDocumentCreateStep<?>> instance = createSteps
            .select(new CreateDocumentOfTypeLiteral(documentClass));
        if (instance.isUnsatisfied() || instance.isAmbiguous()) {
            models.put("section", section.getLabel());
            models.put("folderPath", folderPath);
            models.put("documentType", documentType);
            return new CreateStepNotAvailable();
        }
        final MvcDocumentCreateStep<? extends ContentItem> createStep = instance
            .get();

        createStep.setContentSection(section);
        createStep.setFolder(folder);

        return createStep;
    }

    @Path("/{documentPath:(.+)?}/@authoringsteps/{authoringStep}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public MvcAuthoringStep editDocument(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
        @PathParam("documentPath") final String documentPath,
        @PathParam("authoringStep") final String authoringStepIdentifier
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);
        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return new ContentSectionNotFound();
        }
        final ContentSection section = sectionResult.get();

        final Optional<ContentItem> itemResult = itemRepo
            .findByPath(section, documentPath);
        if (!itemResult.isPresent()) {
            models.put("section", section.getLabel());
            models.put("documentPath", documentPath);
            return new DocumentNotFound();
        }
        final ContentItem item = itemResult.get();

        final Instance<MvcAuthoringStep> instance = authoringSteps
            .select(
                new AuthoringStepPathFragmentLiteral(
                    authoringStepIdentifier
                )
            );
        if (instance.isUnsatisfied() || instance.isAmbiguous()) {
            models.put("section", section.getLabel());
            models.put("documentPath", documentPath);
            models.put("authoringStep", authoringStepIdentifier);
            return new AuthoringStepNotAvailable();
        }
        final MvcAuthoringStep authoringStep = instance.get();

        if (!authoringStep.supportedDocumenType().isAssignableFrom(item
            .getClass())) {
            models.put("section", section.getLabel());
            models.put("documentPath", documentPath);
            models.put("documentType", item.getClass().getName());
            models.put("authoringStep", authoringStepIdentifier);
            return new UnsupportedDocumentType();
        }

        selectedDocumentModel.setContentItem(item);

        authoringStep.setContentSection(section);
        authoringStep.setContentItem(item);

        return authoringStep;
    }

    @POST
    @Path("/{documentPath:(.+)?}/@history")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showHistory(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
        @PathParam("documentPath") final String documentPath
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
            return showDocumentNotFound(section, documentPath);
        }
        final ContentItem item = itemResult.get();
        if (!permissionChecker.isPermitted(ItemPrivileges.EDIT, item)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifier,
                "documentPath", documentPath
            );
        }
        selectedDocumentModel.setContentItem(item);

        models.put(
            "revisions",
            itemRepo.retrieveRevisions(item, item.getObjectId())
        );

        return "org/librecms/ui/contentsection/documents/history.xhtml";
    }

    @GET
    @Path("/{documentPath:(.+)?}/@publish")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showPublishStep(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
        @PathParam("documentPath") final String documentPath
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
            return showDocumentNotFound(section, documentPath);
        }
        final ContentItem item = itemResult.get();
        if (!permissionChecker.isPermitted(ItemPrivileges.PUBLISH, item)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifier,
                "documentPath", documentPath
            );
        }

        publishStepModel.setAvailableLifecycles(
            section
                .getLifecycleDefinitions()
                .stream()
                .map(this::buildLifecycleListEntry)
                .collect(Collectors.toList())
        );
        if (item.getLifecycle() != null) {
            publishStepModel.setPhases(
                item
                    .getLifecycle()
                    .getPhases()
                    .stream()
                    .map(this::buildPhaseListEntry)
                    .collect(Collectors.toList())
            );
        }

        return "org/librecms/ui/contentsection/documents/publish.xhtml";
    }

    @POST
    @Path("/{documentPath:(.+)?}/@lifecycle/phases/{phaseId}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String updatePhaseDates(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
        @PathParam("documentPath") final String documentPath,
        @PathParam("phaseId") final long phaseId,
        @FormParam("startDate") final String startDateParam,
        @FormParam("endDate") final String endDateParam
    ) {
        throw new UnsupportedOperationException("ToDo");
    }

    @POST
    @Path("/{documentPath:(.+)?}/@publish")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String publish(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
        @PathParam("documentPath") final String documentPath,
        @FormParam("selectedLifecycleUuid") @DefaultValue("")
        final String selectedLifecycleUuid
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
            return showDocumentNotFound(section, documentPath);
        }
        final ContentItem item = itemResult.get();
        if (!permissionChecker.isPermitted(ItemPrivileges.PUBLISH, item)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifier,
                "documentPath", documentPath
            );
        }

        if (selectedLifecycleUuid.isEmpty()) {
            itemManager.publish(item);
        } else {
            final Optional<LifecycleDefinition> result = section
                .getLifecycleDefinitions()
                .stream()
                .filter(
                    definition -> definition.getUuid().equals(
                        selectedLifecycleUuid
                    )
                ).findAny();
            if (!result.isPresent()) {
                models.put("section", section.getLabel());
                models.put("lifecycleDefUuid", selectedLifecycleUuid);
                return "org/librecms/ui/contentsection/lifecycle-def-not-found.xhtml";
            }
        }

        return String.format(
            "redirect:/%s/documents/%s/@publish",
            sectionIdentifier,
            documentPath
        );
    }

    @POST
    @Path("/{documentPath:(.+)?}/@republish")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String republish(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
        @PathParam("documentPath") final String documentPath
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
            return showDocumentNotFound(section, documentPath);
        }
        final ContentItem item = itemResult.get();
        if (!permissionChecker.isPermitted(ItemPrivileges.PUBLISH, item)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifier,
                "documentPath", documentPath
            );
        }

        itemManager.publish(item);

        return String.format(
            "redirect:/%s/documents/%s/@publish",
            sectionIdentifier,
            documentPath
        );
    }

    public String unpublish() {
        throw new UnsupportedOperationException("ToDo");
    }

    @POST
    @Path("/{documentPath:(.+)?}/@workflow/tasks/${taskIdentifier}/@lock")
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
            return showDocumentNotFound(section, documentPath);
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
    @Path("/{documentPath:(.+)?}/@workflow/tasks/${taskIdentifier}/@unlock")
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
            return showDocumentNotFound(section, documentPath);
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
    @Path("/{documentPath:(.+)?}/@workflow/tasks/${taskIdentifier}/@finish")
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
            return showDocumentNotFound(section, documentPath);
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
    @Path(
        "/{documentPath:(.+)?}/@workflow/@applyAlternative/{workflowIdentifier}")
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
            return showDocumentNotFound(section, documentPath);
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
    @Path("/{documentPath:(.+)?}/@workflow/@restart")
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
            return showDocumentNotFound(section, documentPath);
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

    private LifecycleListEntry buildLifecycleListEntry(
        final LifecycleDefinition definition
    ) {
        final LifecycleListEntry entry = new LifecycleListEntry();
        entry.setDescription(
            globalizationHelper.getValueFromLocalizedString(
                definition.getDescription()
            )
        );
        entry.setLabel(
            globalizationHelper.getValueFromLocalizedString(
                definition.getLabel()
            )
        );
        entry.setUuid(definition.getUuid());
        return entry;
    }

    private PhaseListEntry buildPhaseListEntry(final Phase phase) {
        final DateTimeFormatter dateTimeFormatter
            = DateTimeFormatter.ISO_DATE_TIME
                .withZone(ZoneId.systemDefault());
        final PhaseListEntry entry = new PhaseListEntry();
        entry.setDescription(
            globalizationHelper.getValueFromLocalizedString(
                phase.getDefinition().getDescription()
            )
        );
        entry.setEndDateTime(
            dateTimeFormatter.format(phase.getEndDateTime().toInstant())
        );
        entry.setFinished(phase.isFinished());
        entry.setLabel(
            globalizationHelper.getValueFromLocalizedString(
                phase.getDefinition().getLabel()
            )
        );
        entry.setPhaseId(phase.getPhaseId());
        entry.setStartDateTime(
            dateTimeFormatter.format(phase.getStartDateTime().toInstant())
        );
        entry.setStarted(phase.isStarted());
        return entry;
    }

    private String showDocumentNotFound(
        final ContentSection section, final String documentPath
    ) {
        models.put("section", section.getLabel());
        models.put("documentPath", documentPath);
        return "org/librecms/ui/contentsection/document-not-found.xhtml";
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

    private static class CreateDocumentOfTypeLiteral
        extends AnnotationLiteral<CreatesDocumentOfType>
        implements CreatesDocumentOfType {

        private static final long serialVersionUID = 1L;

        private final Class<? extends ContentItem> value;

        public CreateDocumentOfTypeLiteral(
            final Class<? extends ContentItem> value
        ) {
            this.value = value;
        }

        @Override
        public Class<? extends ContentItem> value() {
            return value;
        }

    }

    private static class AuthoringStepPathFragmentLiteral
        extends AnnotationLiteral<AuthoringStepPathFragment>
        implements AuthoringStepPathFragment {

        private static final long serialVersionUID = 1L;

        private final String value;

        public AuthoringStepPathFragmentLiteral(final String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }

    }

}
