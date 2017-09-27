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
import com.arsdigita.util.GraphSet;
import com.arsdigita.util.Graphs;
import com.arsdigita.util.UncheckedWrapperException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;
import org.libreccm.workflow.AssignableTaskManager;
import org.libreccm.workflow.CircularTaskDependencyException;
import org.libreccm.workflow.Task;
import org.libreccm.workflow.TaskManager;
import org.libreccm.workflow.TaskRepository;
import org.libreccm.workflow.Workflow;
import org.libreccm.workflow.WorkflowRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionManager;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.workflow.CmsTask;
import org.librecms.workflow.CmsTaskType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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

    private static final Logger LOGGER = LogManager
        .getLogger(WorkflowAdminPaneController.class);

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private ContentSectionManager sectionManager;

    @Inject
    private WorkflowRepository workflowRepo;

    @Inject
    private TaskRepository taskRepo;

    @Inject
    private TaskManager taskManager;

    @Inject
    private AssignableTaskManager assignableTaskManager;

    @Inject
    private RoleRepository roleRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Workflow> retrieveWorkflows(final ContentSection section) {

        final ContentSection contentSection = sectionRepo
            .findById(section.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContentSection with ID %d in the database. "
                + "Where did that ID come from?",
            section.getObjectId())));

        return new ArrayList<>(contentSection.getWorkflowTemplates());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Workflow createWorkflow(final ContentSection section,
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

        final Workflow workflowTemplate = new Workflow();
        workflowTemplate.setAbstractWorkflow(true);
        workflowTemplate.getName().addValue(defaultLocale, name);
        workflowTemplate.getDescription().addValue(defaultLocale, desc);

        workflowRepo.save(workflowTemplate);

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
//        task.setActive(true);

        taskRepo.save(task);

        taskManager.addTask(theWorkflow, task);

        processDependencies(task, deps);

        return task;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void updateTask(final Task task,
                           final String name,
                           final String desc,
                           final CmsTaskType type,
                           final String[] deps) {

        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);
        final Locale defaultLocale = kernelConfig.getDefaultLocale();

        final CmsTask theTask = (CmsTask) taskRepo
            .findById(task.getTaskId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No Task with ID %d in the database. Where did that ID come from?",
            task.getTaskId())));
        theTask.getLabel().addValue(defaultLocale, name);
        theTask.getDescription().addValue(defaultLocale, desc);
        theTask.setTaskType(type);

        taskRepo.save(theTask);

        processDependencies(theTask, deps);
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

    @Transactional(Transactional.TxType.REQUIRED)
    TaskTableModelData getTaskTableModelData(final Workflow workflow) {
        final KernelConfig kernelConfig = confManager
            .findConfiguration(KernelConfig.class);
        final Locale defaultLocale = kernelConfig.getDefaultLocale();

        final Map<Task, String> dependencies = new HashMap<>();

        final Iterator<Task> tasksIter = getTasksForWorkflow(workflow)
            .iterator();
        final GraphSet graphSet = new GraphSet();

        while (tasksIter.hasNext()) {
            Task task = tasksIter.next();
            final Iterator<Task> deps = task.getDependsOn().iterator();
            final StringBuffer buffer = new StringBuffer();
            while (deps.hasNext()) {
                Task dep = deps.next();
                graphSet.addEdge(task, dep, null);
                buffer
                    .append(dep.getLabel().getValue(defaultLocale))
                    .append(", ");
            }

            final int len = buffer.length();
            if (len >= 2) {
                buffer.setLength(len - 2);
            } else {
                graphSet.addNode(task);
            }
            dependencies.put(task, buffer.toString());
        }

        final List<Task> tasks = new ArrayList<>();
        outer:
        while (graphSet.nodeCount() > 0) {
            @SuppressWarnings("unchecked")
            final List<Task> list = Graphs.getSinkNodes(graphSet);
            for (final Iterator<Task> it = list.iterator(); it.hasNext();) {
                final Task currentTask = it.next();
                tasks.add(currentTask);
                graphSet.removeNode(currentTask);
                continue outer;
            }
            // break loop if no nodes removed
            LOGGER.error("found possible loop in tasks for " + workflow);
            break;
        }

        //final Iterator<Task> taskIterator = tasks.iterator();
        final Iterator<Task> taskIterator = getTasksForWorkflow(workflow)
            .iterator();

        return new TaskTableModelData(taskIterator, dependencies);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Role> findAssignees(final CmsTask task) {
        final CmsTask theTask = (CmsTask) taskRepo
            .findById(task.getTaskId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No Task with ID %d in the database. Where did that ID come from?",
            task.getTaskId())));

        return theTask
            .getAssignments()
            .stream()
            .map(assignment -> assignment.getRole())
            .collect(Collectors.toList());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Role> findRoles(final ContentSection section) {

        final ContentSection contentSection = sectionRepo
            .findById(section.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContentSection with ID %d in the database. "
                + "Where did that ID come from?",
            section.getObjectId())));

        return new ArrayList<>(contentSection.getRoles());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void assignTask(final Task task, final String[] roleIds) {

        final CmsTask theTask = (CmsTask) taskRepo
            .findById(task.getTaskId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No Task with ID %d in the database. Where did that ID come from?",
            task.getTaskId())));

        theTask.getAssignments()
            .forEach(assignment -> assignableTaskManager
            .retractTask(theTask, assignment.getRole()));

        if (roleIds != null) {
            final List<Role> roles = Arrays
                .stream(roleIds)
                .map(roleId -> Long.parseLong(roleId))
                .map(roleId -> roleRepo.findById(roleId).orElseThrow(
                () -> new IllegalArgumentException(String.format(
                    "No role with ID %d in the database. "
                        + "Where did that ID come from?", roleId))))
                .collect(Collectors.toList());

            roles.forEach(role -> assignableTaskManager
                .assignTask(theTask, role));
        }

    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void removeAssignment(final Task task, final String roleId) {

        final Role role = roleRepo
            .findById(Long.parseLong(roleId))
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No Role with ID %s in the database. Where did that ID come from?",
            roleId)));

        final CmsTask theTask = (CmsTask) taskRepo
            .findById(task.getTaskId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No Task with ID %d in the database. Where did that ID come from?",
            task.getTaskId())));

        assignableTaskManager.retractTask(theTask, role);

    }

}
