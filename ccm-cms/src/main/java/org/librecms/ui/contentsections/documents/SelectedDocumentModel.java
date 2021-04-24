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

import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.Shiro;
import org.libreccm.workflow.AssignableTask;
import org.libreccm.workflow.AssignableTaskManager;
import org.libreccm.workflow.TaskState;
import org.libreccm.workflow.Workflow;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderManager;
import org.librecms.contentsection.privileges.ItemPrivileges;
import org.librecms.ui.contentsections.FolderBreadcrumbsModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Path;

/**
 * Model/named bean providing data about the currently selected document for
 * several views.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsSelectedDocumentModel")
public class SelectedDocumentModel {

    /**
     * Used to retrive information about tasks.
     */
    @Inject
    private AssignableTaskManager taskManager;

    /**
     * Checks if authoring step classes have all required annotations.
     */
    @Inject
    private AuthoringStepsValidator stepsValidator;

    /**
     * Used to get information about the item.
     */
    @Inject
    private ContentItemManager itemManager;

    /**
     * Used to get information about folders.
     */
    @Inject
    private FolderManager folderManager;

    /**
     * Used to retrieve some localized data.
     */
    @Inject
    private GlobalizationHelper globalizationHelper;

    /**
     * Used to check permissions
     */
    @Inject
    private PermissionChecker permissionChecker;

    /**
     * Used to get the current user.
     */
    @Inject
    private Shiro shiro;

    /**
     * The current content item/document.
     */
    private ContentItem item;

    /**
     * The name of the current content item.
     */
    private String itemName;

    /**
     * The title of the current content item. This value is determined from
     * {@link ContentItem#title} using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     */
    private String itemTitle;

    /**
     * The path of the current item.
     */
    private String itemPath;

    /**
     * The breadcrumb trail of the folder of the current item.
     */
    private List<FolderBreadcrumbsModel> parentFolderBreadcrumbs;

    /**
     * List of authoring steps appliable for the current item.
     */
    private List<AuthoringStepListEntry> authoringStepsList;

    /**
     * Should the default steps be excluded?
     */
    private boolean excludeDefaultAuthoringSteps;

    /**
     * The workflow assigned to the current content item.
     */
    private Workflow workflow;

    /**
     * The name of the workflow assigned to the current item. This value is
     * determined from {@link Workflow#name} using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     */
    private String workflowName;

    /**
     * The current task of the workflow assigned to the current item.
     */
    private TaskListEntry currentTask;

    /**
     * The tasks of the workflow assigned to the current item.
     */
    private List<TaskListEntry> allTasks;

    public String getItemName() {
        return itemName;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public String getItemPath() {
        return itemPath;
    }

    public List<FolderBreadcrumbsModel> getParentFolderBreadcrumbs() {
        return Collections.unmodifiableList(parentFolderBreadcrumbs);
    }

    public List<AuthoringStepListEntry> getAuthoringStepsList() {
        return Collections.unmodifiableList(authoringStepsList);
    }

    public boolean getExcludeDefaultAuthoringSteps() {
        return excludeDefaultAuthoringSteps;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public List<TaskListEntry> getAllTasks() {
        return Collections.unmodifiableList(allTasks);
    }

    public TaskListEntry getCurrentTask() {
        return currentTask;
    }

    public boolean getCanChangeWorkflow() {
        return permissionChecker.isPermitted(
            ItemPrivileges.APPLY_ALTERNATE_WORKFLOW, item
        );
    }

    /**
     * Sets the current content item/document and sets the properties of this
     * model based on the item.
     *
     * @param item
     */
    void setContentItem(final ContentItem item) {
        this.item = Objects.requireNonNull(item);
        itemName = item.getDisplayName();
        itemTitle = globalizationHelper.getValueFromLocalizedString(
            item.getTitle()
        );
        itemPath = itemManager.getItemPath(item);
        parentFolderBreadcrumbs = itemManager
            .getItemFolders(item)
            .stream()
            .map(this::buildFolderBreadcrumbsModel)
            .collect(Collectors.toList());
        excludeDefaultAuthoringSteps = item
            .getClass()
            .getAnnotation(MvcAuthoringKit.class)
            .excludeDefaultAuthoringSteps();
        authoringStepsList = buildAuthoringStepsList(item);
        workflow = item.getWorkflow();
        workflowName = globalizationHelper.getValueFromLocalizedString(
            workflow.getName()
        );
        allTasks = workflow
            .getTasks()
            .stream()
            .filter(task -> task instanceof AssignableTask)
            .map(task -> (AssignableTask) task)
            .map(this::buildTaskListEntry)
            .collect(Collectors.toList());

        currentTask = allTasks
            .stream()
            .filter(task -> task.getTaskState() == TaskState.ENABLED)
            .findFirst()
            .orElse(null);
        if (currentTask != null) {
            currentTask.setCurrentTask(true);
        }
    }

    /**
     * Helper method for building the breadcrumb trail for the folder of the
     * current item.
     *
     * @param folder The folder of the current item.
     *
     * @return The breadcrumb trail of the folder.
     */
    private FolderBreadcrumbsModel buildFolderBreadcrumbsModel(
        final Folder folder
    ) {
        final FolderBreadcrumbsModel model = new FolderBreadcrumbsModel();
        model.setCurrentFolder(false);
        model.setPath(folderManager.getFolderPath(folder));
        model.setPathToken(folder.getName());
        return model;
    }

    /**
     * Helper method for building a {@link TaskListEntry} from a
     * {@link AssignableTask}.
     *
     * @param task The task.
     *
     * @return A {@link TaskListEntry} for the task.
     */
    private TaskListEntry buildTaskListEntry(final AssignableTask task) {
        final TaskListEntry entry = new TaskListEntry();
        entry.setTaskUuid(task.getUuid());
        entry.setLabel(
            globalizationHelper.getValueFromLocalizedString(
                task.getLabel()
            )
        );
        entry.setDescription(
            globalizationHelper.getValueFromLocalizedString(
                task.getDescription()
            )
        );
        entry.setTaskState(task.getTaskState());
        entry.setAssignedToCurrentUser(
            shiro
                .getUser()
                .map(user -> taskManager.isAssignedTo(task, user))
                .orElse(false)
        );
        entry.setLocked(task.isLocked());

        return entry;
    }

    /**
     * Helper method for building the list of applicable authoring steps for the
     * current item.
     *
     * @param item The current item.
     *
     * @return The list of applicable authoring steps for the current item.
     */
    private List<AuthoringStepListEntry> buildAuthoringStepsList(
        final ContentItem item
    ) {
        final MvcAuthoringKit authoringKit = item
            .getClass()
            .getAnnotation(MvcAuthoringKit.class);

        final List<Class<?>> stepClasses = Arrays
            .stream(authoringKit.authoringSteps())
            .collect(Collectors.toList());

        return stepClasses
            .stream()
            .filter(stepsValidator::validateAuthoringStep)
            .filter(stepClass -> stepsValidator.supportsItem(stepClass, item))
            .map(this::buildAuthoringStepListEntry)
            .collect(Collectors.toList());
    }

    /**
     * Helper method for building a {@link AuthoringStepListEntry} from the
     * {@link MvcAuthoringStep}.
     *
     * @param step Th step.
     *
     * @return An {@link AuthoringStepListEntry} for the step.
     */
    private AuthoringStepListEntry buildAuthoringStepListEntry(
        final Class<?> authoringStepClass
    ) {
        final MvcAuthoringStep stepAnnotation = authoringStepClass
            .getAnnotation(MvcAuthoringStep.class);
        final Path pathAnnotation = authoringStepClass.getAnnotation(
            Path.class
        );
        final LocalizedTextsUtil textsUtil = globalizationHelper
            .getLocalizedTextsUtil(stepAnnotation.bundle());
        final AuthoringStepListEntry entry = new AuthoringStepListEntry();
        entry.setDescription(textsUtil.getText(stepAnnotation.descriptionKey()));
        entry.setLabel(textsUtil.getText(stepAnnotation.labelKey()));
        entry.setPath(createStepPath(pathAnnotation.value()));
        return entry;
    }

    private String createStepPath(final String path) {
        return path
            .replace(
                String.format(
                    "{%s}",
                    MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM
                ),
                item.getContentType().getContentSection().getLabel()
            )
            .replace(
                String.format(
                    "{%s}",
                    MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM
                ),
                itemPath
            );
    }

}
