/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections.documents;

import org.libreccm.l10n.GlobalizationHelper;
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
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsSelectedDocumentModel")
public class SelectedDocumentModel {

    @Inject
    private AssignableTaskManager taskManager;

    @Inject
    private Instance<MvcAuthoringStep> authoringSteps;

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private FolderManager folderManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private PermissionChecker permissionChecker;

    @Inject
    private Shiro shiro;

    private ContentItem item;

    private String itemName;
    
    private String itemTitle;

    private String itemPath;

    private List<FolderBreadcrumbsModel> parentFolderBreadcrumbs;

    private List<AuthoringStepListEntry> authoringStepsList;

    private boolean excludeDefaultAuthoringSteps;

    private Workflow workflow;

    private String workflowName;

    private TaskListEntry currentTask;

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

    private FolderBreadcrumbsModel buildFolderBreadcrumbsModel(
        final Folder folder
    ) {
        final FolderBreadcrumbsModel model = new FolderBreadcrumbsModel();
        model.setCurrentFolder(false);
        model.setPath(folderManager.getFolderPath(folder));
        model.setPathToken(folder.getName());
        return model;
    }

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

    private List<AuthoringStepListEntry> buildAuthoringStepsList(
        final ContentItem item
    ) {
        final MvcAuthoringKit authoringKit = item
            .getClass()
            .getAnnotation(MvcAuthoringKit.class);

        final Class<? extends MvcAuthoringStep>[] stepClasses = authoringKit
            .authoringSteps();

        return Arrays
            .stream(stepClasses)
            .map(authoringSteps::select)
            .filter(instance -> instance.isResolvable())
            .map(Instance::get)
            .map(this::buildAuthoringStepListEntry)
            .collect(Collectors.toList());
    }

    private AuthoringStepListEntry buildAuthoringStepListEntry(
        final MvcAuthoringStep step
    ) {
        final AuthoringStepListEntry entry = new AuthoringStepListEntry();
        entry.setDescription(step.getDescription());
        entry.setLabel(step.getLabel());
        entry.setPathFragment(readAuthoringStepPathFragment(step));
        return entry;
    }

    private String readAuthoringStepPathFragment(final MvcAuthoringStep step) {
        return step
            .getClass()
            .getAnnotation(AuthoringStepPathFragment.class)
            .value();
    }

}
