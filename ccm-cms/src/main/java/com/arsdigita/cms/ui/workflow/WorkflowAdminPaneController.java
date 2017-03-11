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
package com.arsdigita.cms.ui.workflow;

import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.workflow.CircularTaskDependencyException;
import org.libreccm.workflow.Task;
import org.libreccm.workflow.TaskManager;
import org.libreccm.workflow.TaskRepository;
import org.libreccm.workflow.Workflow;
import org.libreccm.workflow.WorkflowRepository;
import org.libreccm.workflow.WorkflowTemplate;
import org.libreccm.workflow.WorkflowTemplateMarshaller;
import org.libreccm.workflow.WorkflowTemplateRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionManager;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.workflow.CmsTask;
import org.librecms.workflow.CmsTaskType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class WorkflowAdminPaneController {

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private ContentSectionManager sectionManager;

    @Inject
    private WorkflowRepository workflowRepo;

    @Inject
    private WorkflowTemplateRepository workflowTemplateRepo;

    @Inject
    private TaskRepository taskRepo;

    @Inject
    private TaskManager taskManager;

    @Transactional(Transactional.TxType.REQUIRED)
    public List<WorkflowTemplate> retrieveWorkflows(final ContentSection section) {

        final ContentSection contentSection = sectionRepo
            .findById(section.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContentSection with ID %d in the database. "
                + "Where did that ID come from?",
            section.getObjectId())));

        return new ArrayList<>(contentSection.getWorkflowTemplates());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public WorkflowTemplate createWorkflow(final ContentSection section,
                                           final String name,
                                           final String desc) {

        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);
        final Locale defaultLocale = kernelConfig.getDefaultLocale();

        final ContentSection contentSection = sectionRepo
            .findById(section.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContentSection with ID %d in the database. "
                + "Where did that ID come from?",
            section.getObjectId())));

        final WorkflowTemplate workflowTemplate = new WorkflowTemplate();
        workflowTemplate.getName().addValue(defaultLocale, name);
        workflowTemplate.getDescription().addValue(defaultLocale, desc);

        workflowTemplateRepo.save(workflowTemplate);

        sectionManager.addWorkflowTemplateToContentSection(workflowTemplate,
                                                           contentSection);

        return workflowTemplate;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Task> getTasksForWorkflow(final Workflow workflow) {

        final Workflow theWorkflow = workflowRepo
            .findById(workflow.getWorkflowId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No Workflow with ID %d in the database. Where did that ID come from?",
            workflow.getWorkflowId())));

        return new ArrayList<>(theWorkflow.getTasks());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public CmsTask addTask(final Workflow workflow,
                        final String name,
                        final String desc,
                        final CmsTaskType type,
                        final String[] deps) {
        final KernelConfig kernelConfig = confManager
            .findConfiguration(KernelConfig.class);
        final Locale defaultLocale = kernelConfig.getDefaultLocale();

        final Workflow theWorkflow = workflowRepo
            .findById(workflow.getWorkflowId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No Workflow with ID %d in the database. "
                + "Where did that ID come from?",
            workflow.getWorkflowId())));
        final CmsTask task = new CmsTask();

        task.getLabel().addValue(defaultLocale, name);
        task.getDescription().addValue(defaultLocale, desc);
        task.setTaskType(type);
        task.setActive(true);

        taskRepo.save(task);

        taskManager.addTask(theWorkflow, task);

        processDependencies(task, deps);

        return task;
    }

    /**
     * This method decides which dependencies have to be removed and which ones
     * newly added. Unfortunately we cannot just do "remove all", and add the
     * new ones in since it is possible that Tasks will fire events when
     * dependencies are added and removed.
     *
     */
    private void processDependencies(final Task task,
                                     final String[] selectedDependencies) {
        final List<Task> dependencies = task.getDependentTasks();
        final Map<Long, Task> toAdd = new HashMap<>();
        // Everything is to be removed unless it is in the array.
        final Map<Long, Task> toRemove = dependencies.stream()
            .collect(Collectors.toMap(Task::getTaskId,
                                      dependency -> dependency));

        Long selectedId;
        Object addedTask;
        if (selectedDependencies != null) {
            for (String selectedDependency : selectedDependencies) {
                selectedId = Long.parseLong(selectedDependency);
                addedTask = toRemove.remove(selectedId);
                if (addedTask == null) {
                    toAdd.put(selectedId, taskRepo.findById(selectedId).get());
                }
            }
        }

        for (final Task taskToRemove : toRemove.values()) {
            taskManager.removeDependentTask(task, taskToRemove);
        }

        for (final Task taskToAdd : toAdd.values()) {
            try {
                taskManager.addDependentTask(task, taskToAdd);
            } catch (CircularTaskDependencyException ex) {
                throw new UncheckedWrapperException(ex);
            }
        }
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Task> getDependencies(final Task task) {
        
        final Task theTask = taskRepo
            .findById(task.getTaskId())
        .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No Task with ID %d in the database. Where did that ID come from?",
            task.getTaskId())));
        
        return new ArrayList<>(theTask.getDependsOn());
    }

}
