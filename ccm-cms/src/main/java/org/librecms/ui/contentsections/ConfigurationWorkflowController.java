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
 * Controller for managing the {@link Workflow} templates of the
 * {@link ContentSection}
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/{sectionIdentifier}/configuration/workflows")
public class ConfigurationWorkflowController {

    /**
     * Used to check the admin permissions of a content section.
     */
    @Inject
    private AdminPermissionsChecker adminPermissionsChecker;

    /**
     * Used for actions involving content sections.
     */
    @Inject
    private ContentSectionManager sectionManager;

    /**
     * Model for the current content section.
     */
    @Inject
    private ContentSectionModel sectionModel;

    /**
     * Common functions for views working with {@link ContentSection}s.
     */
    @Inject
    private ContentSectionsUi sectionsUi;

    /**
     * Used for globaliazation stuff.
     */
    @Inject
    private GlobalizationHelper globalizationHelper;

    /**
     * Used to parse identifiers.
     */
    @Inject
    private IdentifierParser identifierParser;

    /**
     * Used to provide data for the views without a named bean.
     */
    @Inject
    private Models models;

    /**
     * Used to manage workflows and workflow templates.
     */
    @Inject
    private WorkflowManager workflowManager;

    /**
     * Used to retrieve and save workflows and workflow templates.
     */
    @Inject
    private WorkflowRepository workflowRepo;

    /**
     * Model for the selected {@link Workflow} template.
     */
    @Inject
    private SelectedWorkflowTemplateModel selectedWorkflowTemplateModel;

    /**
     * Model for the selected task template of a workflow template.
     */
    @Inject
    private SelectedWorkflowTaskTemplateModel selectedWorkflowTaskTemplateModel;

    /**
     * Used to manage the task of a workflow template.
     */
    @Inject
    private TaskManager taskManager;

    /**
     * Used to retrieve and save tasks.
     */
    @Inject
    private TaskRepository taskRepo;

    /**
     * List all {@link Workflow} templates in the current content section.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     *
     * @return The template for the list of workflow templates.
     */
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

    /**
     * Show the details view for a workflow template.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param workflowIdentiferParam The identifier of the workflow to show.
     *
     * @return The template of the details view for workflow templates.
     */
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

    /**
     * Add a new workflow template to the current content section.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param label                  The label of the new workflow template.
     *
     * @return A redirect to the list of workflow templates.
     */
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

    /**
     * Deletes a workflow template.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param workflowIdentiferParam The identifier of the workflow template to
     *                               remove.
     *
     * @return A redirect to the list of workflow templates.
     */
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

    /**
     * Add a localized name to a workflow template.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param workflowIdentiferParam The identifier of the workflow template.
     * @param localeParam            The locale of the value to add.
     * @param value                  The value to add.
     *
     * @return A redirect to the details view of the workflow template.
     */
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

    /**
     * Updates tzhe localized name of a workflow template.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param workflowIdentiferParam The identifier of the workflow template.
     * @param localeParam            The locale of the value to update.
     * @param value                  The updated.
     *
     * @return A redirect to the details view of the workflow template.
     */
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

    /**
     * Removes a localized name from a workflow template.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param workflowIdentiferParam The identifier of the workflow template.
     * @param localeParam            The locale of the value to remove.
     *
     * @return A redirect to the details view of the workflow template.
     */
    @POST
    @Path("/{workflowIdentifier}/label/@remove/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeWorkflowTemplateName(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
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
        workflow.getName().removeValue(new Locale(localeParam));
        workflowRepo.save(workflow);

        return String.format(
            "redirect:/%s/configuration/workflows/%s",
            sectionIdentifierParam,
            workflowIdentiferParam
        );
    }

    /**
     * Adds a localized description to a workflow template.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param workflowIdentiferParam The identifier of the workflow template.
     * @param localeParam            The locale of the value to add.
     * @param value                  The value to add.
     *
     * @return A redirect to the details view of the workflow template.
     */
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

    /**
     * Updates a localized description of a workflow template.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param workflowIdentiferParam The identifier of the workflow template.
     * @param localeParam            The locale of the value to update.
     * @param value                  The updated value.
     *
     * @return A redirect to the details view of the workflow template.
     */
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

    /**
     * Removes a localized description from a workflow template.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param workflowIdentiferParam The identifier of the workflow template.
     * @param localeParam            The locale of the value to remove.
     *
     * @return A redirect to the details view of the workflow template.
     */
    @POST
    @Path("/{workflowIdentifier}/description/@remove/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeWorkflowTemplateDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
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
        workflow.getDescription().removeValue(new Locale(localeParam));
        workflowRepo.save(workflow);

        return String.format(
            "redirect:/%s/configuration/workflows/%s",
            sectionIdentifierParam,
            workflowIdentiferParam
        );
    }

    /**
     * Shows the details view for a task of a workflow template.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param workflowIdentiferParam The identifier of the current workflow
     *                               template.
     * @param taskIdentifierParam    The identifier of the task to show.
     *
     * @return The template for the details view of the task.
     */
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

    /**
     * Adds a task to a workflow template.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param workflowIdentiferParam The identifier of the current workflow
     *                               template.
     * @param label                  The label of the new task.
     *
     * @return A redirect to the details view of the workflow.
     */
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

    /**
     * Removes a task from a workflow template.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param workflowIdentiferParam The identifier of the current workflow
     * @param taskIdentifierParam    The identifier of the task to remove.
     *
     * @return A redirect to the details view of the workflow template.
     */
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

        return String.format(
            "redirect:/%s/configuration/workflows/%s",
            sectionIdentifierParam,
            workflowIdentiferParam
        );
    }

    /**
     * Adds a localized label the a task.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param workflowIdentiferParam The identifer of the current workflow.
     * @param taskIdentifierParam    The identifier of the current task.
     * @param localeParam            The locale of the value to add.
     * @param value                  The value to add.
     *
     * @return A redirect to the details view of the task.
     */
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

    /**
     * Updates a localized label of a task.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param workflowIdentiferParam The identifer of the current workflow.
     * @param taskIdentifierParam    The identifier of the current task.
     * @param localeParam            The locale of the value to update.
     * @param value                  The updated value.
     *
     * @return A redirect to the details view of the task.
     */
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

    /**
     * Removes a localized label from a task.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param workflowIdentiferParam The identifer of the current workflow.
     * @param taskIdentifierParam    The identifier of the current task.
     * @param localeParam            The locale of the value to remove.
     *
     * @return A redirect to the details view of the task.
     */
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

    /**
     * Adds a localized description to a task.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param workflowIdentiferParam The identifer of the current workflow.
     * @param taskIdentifierParam    The identifier of the current task.
     * @param localeParam            The locale of the value to add.
     * @param value                  The value to add.
     *
     * @return A redirect to the details view of the task.
     */
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

    /**
     * Updates a localized description of a task.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param workflowIdentiferParam The identifer of the current workflow.
     * @param taskIdentifierParam    The identifier of the current task.
     * @param localeParam            The locale of the value to update.
     * @param value                  The updated value.
     *
     * @return A redirect to the details view of the task.
     */
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

    /**
     * Removes a localized description from a task.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param workflowIdentiferParam The identifer of the current workflow.
     * @param taskIdentifierParam    The identifier of the current task.
     * @param localeParam            The locale of the value to remove.
     *
     * @return A redirect to the details view of the task.
     */
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

    /**
     * Adds a blocking task to a task. Both task must exist and be part of the
     * the current workflow.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param workflowIdentiferParam The identifer of the current workflow.
     * @param taskIdentifierParam    The identifier of the current task.
     * @param blockingTaskParam      The identifier of the blocking task.
     *
     * @return A redirect to the details view of the task.
     */
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

    /**
     * Removes a blocking task from a task. Both task must exist and be part of
     * the the current workflow.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param workflowIdentiferParam The identifer of the current workflow.
     * @param taskIdentifierParam    The identifier of the current task.
     * @param blockingTaskParam      The identifier of the blocking task.
     *
     * @return A redirect to the details view of the task.
     */
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

    /**
     * Helper method for retrieving a workflow template.
     *
     * @param section                 The current content section.
     * @param templateIdentifierParam The identifier of the workflow template.
     *
     * @return An {@link Optional} with the workflow template or an empty
     *         {@link Optional} if the content section has not matching workflow
     *         template.
     */
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

    /**
     * Shows the "workflow template not found" error page.
     *
     * @param section            The current content section.
     * @param templateIdentifier The identifier of the workflow template.
     *
     * @return The template for the "workflow template not found" error page.
     */
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

    /**
     * Shows the "workflow task template not found" error page.
     *
     * @param section                    The current content section.
     * @param workflowTemplateIdentifier The identifier of the workflow
     *                                   template.
     * @param taskTemplateIdentifier     The idenfifier of the task template.
     *
     * @return The template for the "workflow task template not found" error
     *         page.
     */
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

    /**
     * Helper method for building a {@link WorkflowTemplateListModel} for a
     * {@link Workflow}.
     *
     * @param workflow The workflow.
     *
     * @return A {@link WorkflowTemplateListModel} for the {@code workflow}.
     */
    private WorkflowTemplateListModel buildWorkflowTemplateListModel(
        final Workflow workflow
    ) {
        final WorkflowTemplateListModel model = new WorkflowTemplateListModel();
        model.setDescription(
            globalizationHelper.getValueFromLocalizedString(
                workflow.getDescription()
            )
        );
        model.setHasTasks(!workflow.getTasks().isEmpty());
        model.setName(
            globalizationHelper.getValueFromLocalizedString(workflow.getName())
        );
        model.setUuid(workflow.getUuid());
        model.setWorkflowId(workflow.getWorkflowId());
        return model;
    }

    /**
     * Helper method for building a {@link WorkflowTaskTemplateListModel} for a
     * {@link Task}.
     *
     * @param task The task.
     *
     * @return A {@link WorkflowTaskTemplateListModel} for the {@code task}.
     */
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
        model.setHasDependencies(
            !task.getBlockedTasks().isEmpty()
                || !task.getBlockingTasks().isEmpty()
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
