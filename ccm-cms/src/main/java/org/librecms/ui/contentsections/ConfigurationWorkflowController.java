/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.workflow.CircularTaskDependencyException;
import org.libreccm.workflow.Task;
import org.libreccm.workflow.TaskManager;
import org.libreccm.workflow.TaskRepository;
import org.libreccm.workflow.Workflow;
import org.libreccm.workflow.WorkflowManager;
import org.libreccm.workflow.WorkflowRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionManager;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
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
@Controller
@Path("/{sectionIdentifier}/configuration/workflows")
public class ConfigurationWorkflowController {

    @Inject
    private AdminPermissionsChecker adminPermissionsChecker;

    @Inject
    private ContentSectionManager sectionManager;

    @Inject
    private ContentSectionModel sectionModel;

    @Inject
    private ContentSectionsUi sectionsUi;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private Models models;

    @Inject
    private WorkflowManager workflowManager;

    @Inject
    private WorkflowRepository workflowRepo;

    @Inject
    private SelectedWorkflowTemplateModel selectedWorkflowTemplateModel;

    @Inject
    private SelectedWorkflowTaskTemplateModel selectedWorkflowTaskTemplateModel;

    @Inject
    private TaskManager taskManager;

    @Inject
    private TaskRepository taskRepo;

    @GET
    @Path("/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String listWorkflowTemplates(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerWorkflows(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        models.put(
            "workflowTemplates",
            section
                .getWorkflowTemplates()
                .stream()
                .map(this::buildWorkflowTemplateListModel)
                .collect(Collectors.toList())
        );
        return "org/librecms/ui/contentsection/configuration/workflows.xhtml";
    }

    @GET
    @Path("/{workflowIdentifier}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showWorkflowTemplate(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerWorkflows(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Workflow> workflowResult = findWorkflowTemplate(
            section, workflowIdentiferParam
        );
        if (!workflowResult.isPresent()) {
            return showWorkflowTemplateNotFound(section, workflowIdentiferParam);
        }
        final Workflow workflow = workflowResult.get();
        selectedWorkflowTemplateModel.setDisplayName(
            globalizationHelper.getValueFromLocalizedString(
                workflow.getName()
            )
        );

        final List<Locale> availableLocales = globalizationHelper
            .getAvailableLocales();
        selectedWorkflowTemplateModel.setDescription(
            workflow
                .getDescription()
                .getValues()
                .entrySet()
                .stream()
                .collect(
                    Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> entry.getValue()
                    )
                )
        );
        final Set<Locale> descriptionLocales = workflow
            .getDescription()
            .getAvailableLocales();
        selectedWorkflowTemplateModel
            .setUnusedDescriptionLocales(
                availableLocales
                    .stream()
                    .filter(locale -> !descriptionLocales.contains(locale))
                    .map(Locale::toString)
                    .collect(Collectors.toList())
            );

        selectedWorkflowTemplateModel.setName(
            workflow
                .getName()
                .getValues()
                .entrySet()
                .stream()
                .collect(
                    Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> entry.getValue()
                    )
                )
        );
        final Set<Locale> nameLocales = workflow
            .getName()
            .getAvailableLocales();
        selectedWorkflowTemplateModel
            .setUnusedNameLocales(
                availableLocales
                    .stream()
                    .filter(locale -> !nameLocales.contains(locale))
                    .map(Locale::toString)
                    .collect(Collectors.toList())
            );

        selectedWorkflowTemplateModel.setTasks(
            workflow
                .getTasks()
                .stream()
                .map(this::buildWorkflowTaskTemplateListModel)
                .collect(Collectors.toList())
        );
        selectedWorkflowTemplateModel.setUuid(workflow.getUuid());
        selectedWorkflowTemplateModel.setWorkflowId(workflow.getWorkflowId());

        return "org/librecms/ui/contentsection/configuration/workflow.xhtml";
    }

    @POST
    @Path("/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addWorkflowTemplate(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @FormParam("label") final String label
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Workflow template = new Workflow();
        template.setAbstractWorkflow(true);
        template.getName().addValue(
            globalizationHelper.getNegotiatedLocale(), label
        );
        workflowRepo.save(template);
        sectionManager.addWorkflowTemplateToContentSection(template, section);

        return String.format(
            "redirect:/%s/configuration/workflows", sectionIdentifierParam
        );
    }

    @POST
    @Path("/{workflowIdentifier}/@delete")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String deleteWorkflowTemplate(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Workflow> workflowResult = findWorkflowTemplate(
            section, workflowIdentiferParam
        );
        if (!workflowResult.isPresent()) {
            return showWorkflowTemplateNotFound(section, workflowIdentiferParam);
        }
        final Workflow workflow = workflowResult.get();
        sectionManager.removeWorkflowTemplateFromContentSection(
            workflow, section
        );
        workflowRepo.delete(workflow);

        return String.format(
            "redirect:/%s/configuration/workflows", sectionIdentifierParam
        );
    }

    @POST
    @Path("/{workflowIdentifier}/label/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addWorkflowTemplateName(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Workflow> workflowResult = findWorkflowTemplate(
            section, workflowIdentiferParam
        );
        if (!workflowResult.isPresent()) {
            return showWorkflowTemplateNotFound(section, workflowIdentiferParam);
        }
        final Workflow workflow = workflowResult.get();
        workflow.getName().addValue(new Locale(localeParam), value);
        workflowRepo.save(workflow);

        return String.format(
            "redirect:/%s/configuration/workflows/%s",
            sectionIdentifierParam,
            workflowIdentiferParam
        );
    }

    @POST
    @Path("/{workflowIdentifier}/label/@edit/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editWorkflowTemplateName(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Workflow> workflowResult = findWorkflowTemplate(
            section, workflowIdentiferParam
        );
        if (!workflowResult.isPresent()) {
            return showWorkflowTemplateNotFound(section, workflowIdentiferParam);
        }
        final Workflow workflow = workflowResult.get();
        workflow.getName().addValue(new Locale(localeParam), value);
        workflowRepo.save(workflow);

        return String.format(
            "redirect:/%s/configuration/workflows/%s",
            sectionIdentifierParam,
            workflowIdentiferParam
        );
    }

    @POST
    @Path("/{workflowIdentifier}/label/@remove/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeWorkflowTemplateName(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {

        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Workflow> workflowResult = findWorkflowTemplate(
            section, workflowIdentiferParam
        );
        if (!workflowResult.isPresent()) {
            return showWorkflowTemplateNotFound(section, workflowIdentiferParam);
        }
        final Workflow workflow = workflowResult.get();
        workflow.getName().removeValue(new Locale(localeParam));
        workflowRepo.save(workflow);

        return String.format(
            "redirect:/%s/configuration/workflows/%s",
            sectionIdentifierParam,
            workflowIdentiferParam
        );
    }

    @POST
    @Path("/{workflowIdentifier}/description/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addWorkflowTemplateDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Workflow> workflowResult = findWorkflowTemplate(
            section, workflowIdentiferParam
        );
        if (!workflowResult.isPresent()) {
            return showWorkflowTemplateNotFound(section, workflowIdentiferParam);
        }
        final Workflow workflow = workflowResult.get();
        workflow.getDescription().addValue(new Locale(localeParam), value);
        workflowRepo.save(workflow);

        return String.format(
            "redirect:/%s/configuration/workflows/%s",
            sectionIdentifierParam,
            workflowIdentiferParam
        );
    }

    @POST
    @Path("/{workflowIdentifier}/description/@edit/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editWorkflowTemplateDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Workflow> workflowResult = findWorkflowTemplate(
            section, workflowIdentiferParam
        );
        if (!workflowResult.isPresent()) {
            return showWorkflowTemplateNotFound(section, workflowIdentiferParam);
        }
        final Workflow workflow = workflowResult.get();
        workflow.getDescription().addValue(new Locale(localeParam), value);
        workflowRepo.save(workflow);

        return String.format(
            "redirect:/%s/configuration/workflows/%s",
            sectionIdentifierParam,
            workflowIdentiferParam
        );
    }

    @POST
    @Path("/{workflowIdentifier}/description/@remove/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeWorkflowTemplateDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Workflow> workflowResult = findWorkflowTemplate(
            section, workflowIdentiferParam
        );
        if (!workflowResult.isPresent()) {
            return showWorkflowTemplateNotFound(section, workflowIdentiferParam);
        }
        final Workflow workflow = workflowResult.get();
        workflow.getDescription().removeValue(new Locale(localeParam));
        workflowRepo.save(workflow);

        return String.format(
            "redirect:/%s/configuration/workflows/%s",
            sectionIdentifierParam,
            workflowIdentiferParam
        );
    }

    @GET
    @Path("/{workflowIdentifier}/tasks/{taskIdentifier}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showTask(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("taskIdentifier") final String taskIdentifierParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Workflow> workflowResult = findWorkflowTemplate(
            section, workflowIdentiferParam
        );
        if (!workflowResult.isPresent()) {
            return showWorkflowTemplateNotFound(section, workflowIdentiferParam);
        }
        final Workflow workflow = workflowResult.get();
        final Optional<Task> taskResult = findTaskTemplate(
            workflow, taskIdentifierParam
        );
        if (!taskResult.isPresent()) {
            return showWorkflowTaskTemplateNotFound(
                section, workflowIdentiferParam, taskIdentifierParam
            );
        }
        final Task task = taskResult.get();
        selectedWorkflowTaskTemplateModel.setTaskId(task.getTaskId());
        selectedWorkflowTaskTemplateModel.setUuid(task.getUuid());
        selectedWorkflowTaskTemplateModel.setDisplayLabel(
            globalizationHelper.getValueFromLocalizedString(
                task.getLabel()
            )
        );

        selectedWorkflowTemplateModel.setUuid(workflow.getUuid());
        selectedWorkflowTemplateModel.setDisplayName(
            globalizationHelper.getValueFromLocalizedString(
                workflow.getName()
            )
        );

        final List<Locale> availableLocales = globalizationHelper
            .getAvailableLocales();

        selectedWorkflowTaskTemplateModel.setLabel(
            task
                .getLabel()
                .getValues()
                .entrySet()
                .stream()
                .collect(
                    Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> entry.getValue()
                    )
                )
        );
        final Set<Locale> labelLocales = task.getLabel().getAvailableLocales();
        selectedWorkflowTaskTemplateModel
            .setUnusedLabelLocales(
                availableLocales
                    .stream()
                    .filter(locale -> !labelLocales.contains(locale))
                    .map(Locale::toString)
                    .collect(Collectors.toList())
            );

        selectedWorkflowTaskTemplateModel.setDescription(
            task
                .getDescription()
                .getValues()
                .entrySet()
                .stream()
                .collect(
                    Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> entry.getValue()
                    )
                )
        );
        final Set<Locale> descriptionLocales = task
            .getDescription()
            .getAvailableLocales();
        selectedWorkflowTaskTemplateModel.setUnusedDescriptionLocales(
            availableLocales
                .stream()
                .filter(locale -> !descriptionLocales.contains(locale))
                .map(Locale::toString)
                .collect(Collectors.toList())
        );

        selectedWorkflowTaskTemplateModel.setBlockedTasks(
            task
                .getBlockedTasks()
                .stream()
                .map(dependency -> dependency.getBlockedTask())
                .map(this::buildWorkflowTaskTemplateListModel)
                .collect(Collectors.toList())
        );
        final List<Task> blockingTasks = task
            .getBlockingTasks()
            .stream()
            .map(dependency -> dependency.getBlockingTask())
            .collect(Collectors.toList());
        selectedWorkflowTaskTemplateModel.setBlockingTasks(
            blockingTasks
                .stream()
                .map(this::buildWorkflowTaskTemplateListModel)
                .collect(Collectors.toList())
        );

        selectedWorkflowTaskTemplateModel.setNoneBlockingTasks(
            workflow
                .getTasks()
                .stream()
                .filter(workflowTask -> !workflowTask.equals(task))
                .filter(
                    workflowTask -> !blockingTasks.contains(workflowTask)
                )
                .collect(
                    Collectors.toMap(
                        workflowTask -> String.format(
                            "UUID-%s", workflowTask.getUuid()
                        ),
                        workflowTask -> globalizationHelper
                            .getValueFromLocalizedString(
                                workflowTask.getLabel()
                            )
                    )
                )
        );

        return "org/librecms/ui/contentsection/configuration/workflow-task.xhtml";
    }

    @POST
    @Path("/{workflowIdentifier}/tasks/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addTask(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @FormParam("label") final String label
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Workflow> workflowResult = findWorkflowTemplate(
            section, workflowIdentiferParam
        );
        if (!workflowResult.isPresent()) {
            return showWorkflowTemplateNotFound(section, workflowIdentiferParam);
        }
        final Workflow workflow = workflowResult.get();
        final Task task = new Task();
        task.getLabel().addValue(
            globalizationHelper.getNegotiatedLocale(), label
        );

        taskRepo.save(task);
        taskManager.addTask(workflow, task);

        return String.format(
            "redirect:/%s/configuration/workflows/%s",
            sectionIdentifierParam,
            workflowIdentiferParam
        );
    }

    @POST
    @Path("/{workflowIdentifier}/tasks/{taskIdentifier}/@remove")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeTask(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("taskIdentifier") final String taskIdentifierParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Workflow> workflowResult = findWorkflowTemplate(
            section, workflowIdentiferParam
        );
        if (!workflowResult.isPresent()) {
            return showWorkflowTemplateNotFound(section, workflowIdentiferParam);
        }
        final Workflow workflow = workflowResult.get();
        final Optional<Task> taskResult = findTaskTemplate(
            workflow, taskIdentifierParam
        );
        if (!taskResult.isPresent()) {
            return showWorkflowTaskTemplateNotFound(
                section, workflowIdentiferParam, taskIdentifierParam
            );
        }
        final Task task = taskResult.get();
        taskManager.removeTask(workflow, task);
        taskRepo.delete(task);

        return String.format(
            "redirect:/%s/configuration/workflows/%s",
            sectionIdentifierParam,
            workflowIdentiferParam
        );
    }

    @POST
    @Path("/{workflowIdentifier}/tasks/{taskIdentifier}/label/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addTaskLabel(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("taskIdentifier") final String taskIdentifierParam,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Workflow> workflowResult = findWorkflowTemplate(
            section, workflowIdentiferParam
        );
        if (!workflowResult.isPresent()) {
            return showWorkflowTemplateNotFound(section, workflowIdentiferParam);
        }
        final Workflow workflow = workflowResult.get();
        final Optional<Task> taskResult = findTaskTemplate(
            workflow, taskIdentifierParam
        );
        if (!taskResult.isPresent()) {
            return showWorkflowTaskTemplateNotFound(
                section, workflowIdentiferParam, taskIdentifierParam
            );
        }
        final Task task = taskResult.get();
        task.getLabel().addValue(new Locale(localeParam), value);
        taskRepo.save(task);

        return String.format(
            "redirect:/%s/configuration/workflows/%s/tasks/%s",
            sectionIdentifierParam,
            workflowIdentiferParam,
            taskIdentifierParam
        );
    }

    @POST
    @Path("/{workflowIdentifier}/tasks/{taskIdentifier}/label/@edit/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editTaskLabel(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("taskIdentifier") final String taskIdentifierParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Workflow> workflowResult = findWorkflowTemplate(
            section, workflowIdentiferParam
        );
        if (!workflowResult.isPresent()) {
            return showWorkflowTemplateNotFound(section, workflowIdentiferParam);
        }
        final Workflow workflow = workflowResult.get();
        final Optional<Task> taskResult = findTaskTemplate(
            workflow, taskIdentifierParam
        );
        if (!taskResult.isPresent()) {
            return showWorkflowTaskTemplateNotFound(
                section, workflowIdentiferParam, taskIdentifierParam
            );
        }
        final Task task = taskResult.get();
        task.getLabel().addValue(new Locale(localeParam), value);
        taskRepo.save(task);

        return String.format(
            "redirect:/%s/configuration/workflows/%s/tasks/%s",
            sectionIdentifierParam,
            workflowIdentiferParam,
            taskIdentifierParam
        );
    }

    @POST
    @Path(
        "/{workflowIdentifier}/tasks/{taskIdentifier}/label/@remove/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeTaskLabel(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("taskIdentifier") final String taskIdentifierParam,
        @PathParam("locale") final String localeParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Workflow> workflowResult = findWorkflowTemplate(
            section, workflowIdentiferParam
        );
        if (!workflowResult.isPresent()) {
            return showWorkflowTemplateNotFound(section, workflowIdentiferParam);
        }
        final Workflow workflow = workflowResult.get();
        final Optional<Task> taskResult = findTaskTemplate(
            workflow, taskIdentifierParam
        );
        if (!taskResult.isPresent()) {
            return showWorkflowTaskTemplateNotFound(
                section, workflowIdentiferParam, taskIdentifierParam
            );
        }
        final Task task = taskResult.get();
        task.getLabel().removeValue(new Locale(localeParam));
        taskRepo.save(task);

        return String.format(
            "redirect:/%s/configuration/workflows/%s/tasks/%s",
            sectionIdentifierParam,
            workflowIdentiferParam,
            taskIdentifierParam
        );
    }

    @POST
    @Path("/{workflowIdentifier}/tasks/{taskIdentifier}/description/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addTaskDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("taskIdentifier") final String taskIdentifierParam,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Workflow> workflowResult = findWorkflowTemplate(
            section, workflowIdentiferParam
        );
        if (!workflowResult.isPresent()) {
            return showWorkflowTemplateNotFound(section, workflowIdentiferParam);
        }
        final Workflow workflow = workflowResult.get();
        final Optional<Task> taskResult = findTaskTemplate(
            workflow, taskIdentifierParam
        );
        if (!taskResult.isPresent()) {
            return showWorkflowTaskTemplateNotFound(
                section, workflowIdentiferParam, taskIdentifierParam
            );
        }
        final Task task = taskResult.get();
        task.getDescription().addValue(new Locale(localeParam), value);
        taskRepo.save(task);

        return String.format(
            "redirect:/%s/configuration/workflows/%s/tasks/%s",
            sectionIdentifierParam,
            workflowIdentiferParam,
            taskIdentifierParam
        );
    }

    @POST
    @Path(
        "/{workflowIdentifier}/tasks/{taskIdentifier}/description/@edit/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editTaskDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("taskIdentifier") final String taskIdentifierParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Workflow> workflowResult = findWorkflowTemplate(
            section, workflowIdentiferParam
        );
        if (!workflowResult.isPresent()) {
            return showWorkflowTemplateNotFound(section, workflowIdentiferParam);
        }
        final Workflow workflow = workflowResult.get();
        final Optional<Task> taskResult = findTaskTemplate(
            workflow, taskIdentifierParam
        );
        if (!taskResult.isPresent()) {
            return showWorkflowTaskTemplateNotFound(
                section, workflowIdentiferParam, taskIdentifierParam
            );
        }
        final Task task = taskResult.get();
        task.getDescription().addValue(new Locale(localeParam), value);
        taskRepo.save(task);

        return String.format(
            "redirect:/%s/configuration/workflows/%s/tasks/%s",
            sectionIdentifierParam,
            workflowIdentiferParam,
            taskIdentifierParam
        );
    }

    @POST
    @Path(
        "/{workflowIdentifier}/tasks/{taskIdentifier}/description/@remove/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeTaskDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("taskIdentifier") final String taskIdentifierParam,
        @PathParam("locale") final String localeParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Workflow> workflowResult = findWorkflowTemplate(
            section, workflowIdentiferParam
        );
        if (!workflowResult.isPresent()) {
            return showWorkflowTemplateNotFound(section, workflowIdentiferParam);
        }
        final Workflow workflow = workflowResult.get();
        final Optional<Task> taskResult = findTaskTemplate(
            workflow, taskIdentifierParam
        );
        if (!taskResult.isPresent()) {
            return showWorkflowTaskTemplateNotFound(
                section, workflowIdentiferParam, taskIdentifierParam
            );
        }
        final Task task = taskResult.get();
        task.getDescription().removeValue(new Locale(localeParam));
        taskRepo.save(task);

        return String.format(
            "redirect:/%s/configuration/workflows/%s/tasks/%s",
            sectionIdentifierParam,
            workflowIdentiferParam,
            taskIdentifierParam
        );
    }

    @POST
    @Path(
        "/{workflowIdentifier}/tasks/{taskIdentifier}/blocking-tasks/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addBlockingTask(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("taskIdentifier") final String taskIdentifierParam,
        @FormParam("blockingTask") final String blockingTaskParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Workflow> workflowResult = findWorkflowTemplate(
            section, workflowIdentiferParam
        );
        if (!workflowResult.isPresent()) {
            return showWorkflowTemplateNotFound(section, workflowIdentiferParam);
        }
        final Workflow workflow = workflowResult.get();
        final Optional<Task> taskResult = findTaskTemplate(
            workflow, taskIdentifierParam
        );
        if (!taskResult.isPresent()) {
            return showWorkflowTaskTemplateNotFound(
                section, workflowIdentiferParam, taskIdentifierParam
            );
        }
        final Optional<Task> blockingTaskResult = findTaskTemplate(
            workflow, blockingTaskParam
        );
        if (!blockingTaskResult.isPresent()) {
            return showWorkflowTaskTemplateNotFound(
                section, workflowIdentiferParam, blockingTaskParam
            );
        }

        final Task task = taskResult.get();
        final Task blockingTask = blockingTaskResult.get();
        try {
            taskManager.addDependentTask(blockingTask, task);
        } catch (CircularTaskDependencyException ex) {
            models.put("sectionIdentifier", section.getLabel());
            models.put("workflowTemplateIdentifier", workflowIdentiferParam);
            models.put("blockedTaskIdentifier", taskIdentifierParam);
            models.put("blockingTaskIdentifier", blockingTaskParam);

            return "org/librecms/ui/contentsection/configuration/workflow-task-circular-dependency.xhtml";
        }

        return String.format(
            "redirect:/%s/configuration/workflows/%s/tasks/%s",
            sectionIdentifierParam,
            workflowIdentiferParam,
            taskIdentifierParam
        );
    }

    @POST
    @Path(
        "/{workflowIdentifier}/tasks/{taskIdentifier}/blockingTasks/{blockingTaskIdentifier}/@remove")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeBlockingTask(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("taskIdentifier") final String taskIdentifierParam,
        @PathParam("blockingTaskIdentifier") final String blockingTaskParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<Workflow> workflowResult = findWorkflowTemplate(
            section, workflowIdentiferParam
        );
        if (!workflowResult.isPresent()) {
            return showWorkflowTemplateNotFound(section, workflowIdentiferParam);
        }
        final Workflow workflow = workflowResult.get();
        final Optional<Task> taskResult = findTaskTemplate(
            workflow, taskIdentifierParam
        );
        if (!taskResult.isPresent()) {
            return showWorkflowTaskTemplateNotFound(
                section, workflowIdentiferParam, taskIdentifierParam
            );
        }
        final Optional<Task> blockingTaskResult = findTaskTemplate(
            workflow, blockingTaskParam
        );
        if (!blockingTaskResult.isPresent()) {
            return showWorkflowTaskTemplateNotFound(
                section, workflowIdentiferParam, blockingTaskParam
            );
        }

        final Task task = taskResult.get();
        final Task blockingTask = blockingTaskResult.get();
        taskManager.removeDependentTask(blockingTask, task);

        return String.format(
            "redirect:/%s/configuration/workflows/%s/tasks/%s",
            sectionIdentifierParam,
            workflowIdentiferParam,
            taskIdentifierParam
        );
    }

    private Optional<Workflow> findWorkflowTemplate(
        final ContentSection section, final String templateIdentifierParam
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            templateIdentifierParam
        );
        switch (identifier.getType()) {
            case ID:
                return section
                    .getWorkflowTemplates()
                    .stream()
                    .filter(
                        template -> template.isAbstractWorkflow()
                    )
                    .filter(
                        template -> template.getWorkflowId() == Long.parseLong(
                        identifier.getIdentifier())
                    ).findAny();
            default:
                return section
                    .getWorkflowTemplates()
                    .stream()
                    .filter(
                        template -> template.isAbstractWorkflow()
                    )
                    .filter(
                        template -> template.getUuid().equals(identifier
                            .getIdentifier())
                    ).findAny();
        }
    }

    private String showWorkflowTemplateNotFound(
        final ContentSection section,
        final String templateIdentifier
    ) {
        models.put("sectionIdentifier", section.getLabel());
        models.put("workflowTemplateIdentifier", templateIdentifier);
        return "org/librecms/ui/contentsection/configuration/workflow-not-found.xhtml";
    }

    private Optional<Task> findTaskTemplate(
        final Workflow workflow,
        final String taskTemplateIdentifierParam
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            taskTemplateIdentifierParam
        );
        switch (identifier.getType()) {
            case ID:
                return workflow
                    .getTasks()
                    .stream()
                    .filter(
                        template -> template.getTaskId() == Long.parseLong(
                        identifier.getIdentifier()
                    )
                    ).findAny();
            default:
                return workflow
                    .getTasks()
                    .stream()
                    .filter(
                        template -> template.getUuid().equals(identifier
                            .getIdentifier())
                    ).findAny();
        }
    }

    private String showWorkflowTaskTemplateNotFound(
        final ContentSection section,
        final String workflowTemplateIdentifier,
        final String taskTemplateIdentifier
    ) {
        models.put("sectionIdentifier", section.getLabel());
        models.put("workflowTemplateIdentifier", workflowTemplateIdentifier);
        models.put("workflowTaskTemplateIdentifier", taskTemplateIdentifier);
        return "org/librecms/ui/contentsection/configuration/workflow-task-not-found.xhtml";
    }

    private WorkflowTemplateListModel buildWorkflowTemplateListModel(
        final Workflow workflow
    ) {
        final WorkflowTemplateListModel model = new WorkflowTemplateListModel();
        model.setDescription(
            globalizationHelper.getValueFromLocalizedString(
                workflow.getDescription()
            )
        );
        model.setName(
            globalizationHelper.getValueFromLocalizedString(workflow.getName())
        );
        model.setUuid(workflow.getUuid());
        model.setWorkflowId(workflow.getWorkflowId());
        return model;
    }

    private WorkflowTaskTemplateListModel buildWorkflowTaskTemplateListModel(
        final Task task
    ) {
        final WorkflowTaskTemplateListModel model
            = new WorkflowTaskTemplateListModel();
        model.setDescription(
            globalizationHelper.getValueFromLocalizedString(
                task.getDescription()
            )
        );
        model.setLabel(
            globalizationHelper.getValueFromLocalizedString(
                task.getLabel()
            )
        );
        model.setTaskId(task.getTaskId());
        model.setUuid(task.getUuid());
        return model;
    }

}
