/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.libreccm.workflow;

import com.arsdigita.kernel.KernelConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.subject.Subject;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.CcmObject;
import org.libreccm.core.CoreConstants;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.security.Shiro;
import org.libreccm.security.User;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Manager for {@link Workflow}s. The logic of some of these classes has been
 * ported from the workflow implementation. The methods have only been edited to
 * fit into the new architecture.
 *
 * Most of the methods of this manager require the {@code ADMIN} privilege. To
 * use this methods with other users the caller has the check the permissions
 * first and than wrap the call to the method of this class into a new context
 * with the system user by using
 * {@link Subject#execute(java.util.concurrent.Callable)}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class WorkflowManager implements Serializable {
    private static final long serialVersionUID = -6939804120313699606L;

    private final static Logger LOGGER = LogManager.getLogger(
        WorkflowManager.class);

    @Inject
    private EntityManager entityManager;

    @Inject
    private WorkflowRepository workflowRepo;

    @Inject
    private TaskRepository taskRepo;

    @Inject
    private TaskManager taskManager;

    @Inject
    private AssignableTaskManager assignableTaskManager;

    @Inject
    private Shiro shiro;

    @Inject
    private ConfigurationManager confManager;

    private Locale defaultLocale;

    /**
     * Populates the {@link #defaultLocale} field.
     */
    @PostConstruct
    private void init() {
        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);
        defaultLocale = kernelConfig.getDefaultLocale();
    }

    /**
     * Creates an {@link Workflow} for the provided {@link CcmObject} using the
     * provided {@link WorkflowTemplate}.
     *
     * @param template The template which is used to create the new workflow.
     * @param object   The object for which th workflow is generated.
     *
     * @return The new workflow.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public Workflow createWorkflow(final Workflow template,
                                   final CcmObject object) {
        Objects.requireNonNull(template,
                               "Can't create a workflow without a template.");
        if (!template.isAbstractWorkflow()) {
            throw new IllegalArgumentException(
                "The provided template is not an abstract workflow");
        }

        Objects.requireNonNull(object,
                               "Can't create a workflow without an object.");

        final Workflow workflow = new Workflow();

        final LocalizedString name = new LocalizedString();
        template.getName().getValues().forEach(
            (locale, str) -> name.addValue(locale, str));
        workflow.setName(name);

        final LocalizedString description = new LocalizedString();
        template
            .getDescription()
            .getValues()
            .forEach((locale, str) -> description.addValue(locale, str));
        workflow.setDescription(description);

        workflowRepo.save(workflow);

        final Map<Long, Task> tasks = new HashMap<>();

        template
            .getTasks()
            .forEach(taskTemplate -> createTask(workflow, taskTemplate, tasks));
        template
            .getTasks()
            .forEach(taskTemplate -> {
                fixTaskDependencies(taskTemplate,
                                    tasks.get(taskTemplate.getTaskId()),
                                    tasks);
            });

        workflow.setObject(object);
        workflow.setState(WorkflowState.INIT);

        tasks.values().forEach(task -> taskRepo.save(task));
        workflowRepo.save(workflow);

        return workflow;
    }

    /**
     * Helper method for
     * {@link #createWorkflow(org.libreccm.workflow.WorkflowTemplate, org.libreccm.core.CcmObject)}
     * for creating the tasks of the new workflow from the tasks of the workflow
     * template.
     *
     * @param template The template for the task from the workflow template.
     * @param tasks    A map for storing the new tasks.
     */
    private void createTask(final Workflow workflow,
                            final Task template,
                            final Map<Long, Task> tasks) {

        final Class<? extends Task> templateClass = template.getClass();
        final Task task;
        try {
            task = templateClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }

        final BeanInfo templateBeanInfo;
        try {
            templateBeanInfo = Introspector.getBeanInfo(templateClass);
        } catch (IntrospectionException ex) {
            throw new RuntimeException(ex);
        }

        for (PropertyDescriptor propertyDesc : templateBeanInfo
            .getPropertyDescriptors()) {
            try {
                if ("taskId".equals(propertyDesc.getName())
                        || "workflow".equals(propertyDesc.getName())
                        || "dependentTasks".equals(propertyDesc.getName())
                        || "dependsOn".equals(propertyDesc.getName())
                        || "assignments".equals(propertyDesc.getName())
                        || "class".equals(propertyDesc.getName())) {
                    continue;
                }

                final Method readMethod = propertyDesc.getReadMethod();
                final Method writeMethod = propertyDesc.getWriteMethod();

                if (writeMethod == null) {
                    continue;
                }

                final Object value = readMethod.invoke(template);
                if (value instanceof LocalizedString) {
                    final LocalizedString localized = (LocalizedString) value;
                    final LocalizedString copy = new LocalizedString();

                    localized.getValues().forEach(
                        (locale, str) -> copy.addValue(locale, str));

                    writeMethod.invoke(task, copy);
                } else {
                    writeMethod.invoke(task, value);
                }
            } catch (IllegalAccessException
                     | IllegalArgumentException
                     | InvocationTargetException ex) {
                throw new RuntimeException();
            }
        }

        workflow.addTask(task);
        task.setWorkflow(workflow);
        tasks.put(template.getTaskId(), task);

        if (template instanceof AssignableTask) {
            final AssignableTask assignableTemplate
                                     = (AssignableTask) template;
            final AssignableTask assignableTask = (AssignableTask) task;

            assignableTemplate
                .getAssignments()
                .stream()
                .map(TaskAssignment::getRole)
                .forEach(role -> {
                    assignableTaskManager.assignTask(assignableTask, role);
                });
        }
    }

    /**
     * Helper method for
     * {@link #createWorkflow(org.libreccm.workflow.WorkflowTemplate, org.libreccm.core.CcmObject)}
     * and {@link #createTask(org.libreccm.workflow.Task, java.util.Map)} for
     * creating the task dependencies.
     *
     * @param template
     * @param task
     * @param tasks
     */
    private void fixTaskDependencies(final Task template,
                                     final Task task,
                                     final Map<Long, Task> tasks) {

        if (template.getBlockedTasks() != null
                && !template.getBlockedTasks().isEmpty()) {

            for (final TaskDependency blocked : template.getBlockedTasks()) {

                final Task blockingTask = tasks
                    .get(blocked.getBlockingTask().getTaskId());
                final Task blockedTask = tasks
                    .get(blocked.getBlockedTask().getTaskId());
                try {
                    taskManager.addDependentTask(blockingTask, blockedTask);
                } catch (CircularTaskDependencyException ex) {
                    throw new UnexpectedErrorException(ex);
                }
            }
        }

//        if (template.getDependentTasks() != null
//                && !template.getDependentTasks().isEmpty()) {
//            template.getDependentTasks().forEach(dependent
//                -> task.addDependentTask(tasks.get(dependent.getTaskId())));
//        }
        for (final TaskDependency blocking : template.getBlockingTasks()) {

            final Task blockingTask = tasks
                .get(blocking.getBlockingTask().getTaskId());
            final Task blockedTask = tasks
                .get(blocking.getBlockedTask().getTaskId());
            try {
                taskManager.addDependentTask(blockingTask, blockedTask);
            } catch(CircularTaskDependencyException ex) {
                throw new UnexpectedErrorException(ex);
            }
            
        }

//        if (template.getDependsOn() != null
//                && !template.getDependsOn().isEmpty()) {
//            template.getDependsOn().forEach(dependsOn
//                -> task.addDependsOn(tasks.get(dependsOn.getTaskId())));
//        }
    }

    /**
     * Finds the enabled {@link Task}s of a {@link Workflow}.
     *
     * @param workflow The workflow.
     *
     * @return A unmodifiable list of the enabled tasks of the provided
     *         {@code workflow}.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Task> findEnabledTasks(final Workflow workflow) {
        if (workflow.getState() == WorkflowState.DELETED
                || workflow.getState() == WorkflowState.STOPPED) {
            LOGGER.debug(String.format("Workflow state is \"%s\". Workflow "
                                           + "has no enabled tasks.",
                                       workflow.getState().toString()));
            return Collections.emptyList();
        }

        final TypedQuery<Task> query = entityManager.createNamedQuery(
            "Task.findEnabledTasks", Task.class);
        query.setParameter("workflow", workflow);

        return Collections.unmodifiableList(query.getResultList());
    }

    /**
     * Finds the finished {@link Task}s of a workflow.
     *
     * @param workflow The workflow.
     *
     * @return An unmodifiable list of the finished tasks of the provided
     *         {@code Workflow}.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Task> findFinishedTasks(final Workflow workflow) {
        final TypedQuery<Task> query = entityManager.createNamedQuery(
            "Task.findFinishedTasks", Task.class);
        query.setParameter("workflow", workflow);

        return Collections.unmodifiableList(query.getResultList());
    }

    /**
     * Finds the {@link Task}s of a {@link Workflow} which are overdue.
     *
     * @param workflow The workflow.
     *
     * @return A unmodifiable list of the overdue tasks of the provided
     *         {@code workflow}.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public List<AssignableTask> findOverdueTasks(final Workflow workflow) {
        final TypedQuery<AssignableTask> query = entityManager.createNamedQuery(
            "AssignableTask.findOverdueTasks", AssignableTask.class);
        query.setParameter("workflow", workflow);
        query.setParameter("now", new Date());

        return Collections.unmodifiableList(query.getResultList());
    }

    /**
     * Starts a {@link Workflow}.
     *
     * @param workflow The workflow to start.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void start(final Workflow workflow) {

        final WorkflowState oldState = workflow.getState();

        workflow.setState(WorkflowState.STARTED);
        if (oldState == WorkflowState.INIT) {
            workflow.setActive(true);
            updateState(workflow);

//            for (final Task current : workflow.getTasks()) {
//                current.setActive(true);
//                taskManager.updateState(current);
//            }
            final List<Task> tasks = workflow.getTasks();
            if (!tasks.isEmpty()) {
                final Task firstTask = tasks.get(0);
                firstTask.setActive(true);
                taskManager.updateState(firstTask);

                if (firstTask instanceof AssignableTask) {
                    final Optional<User> currentUser = shiro.getUser();
                    if (currentUser.isPresent()
                            && assignableTaskManager
                            .isAssignedTo((AssignableTask) firstTask,
                                          currentUser.get())) {
                        assignableTaskManager
                            .lockTask((AssignableTask) firstTask);
                    }
                }
            }
        }

        workflowRepo.save(workflow);
    }

    /**
     * Helper method for updating the state of a {@link Workflow}.
     *
     * @param workflow The workflow to update.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    private void updateState(final Workflow workflow) {
        if (workflow.getTasksState() == TaskState.ENABLED) {
            final TypedQuery<Long> query = entityManager.createNamedQuery(
                "Task.countUnfinishedAndActiveTasksForWorkflow", Long.class);
            query.setParameter("workflow", workflow);

            final Long result = query.getSingleResult();

            if (result > 0) {
                return;
            } else {
                finish(workflow);
            }
        }

        if (workflow.getTasksState() == TaskState.FINISHED) {
            final TypedQuery<Long> query = entityManager.createNamedQuery(
                "Task.countUnfinishedTasksForWorkflow", Long.class);
            query.setParameter("workflow", workflow);

            final Long result = query.getSingleResult();

            if (result > 0) {
                enable(workflow);
            }
        }
    }

    /**
     * Stops a workflow.
     *
     * @param workflow The workflow to stop.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void stop(final Workflow workflow) {
        workflow.setState(WorkflowState.STOPPED);
        workflowRepo.save(workflow);
    }

    /**
     * Finished a {@link Workflow}.
     *
     * @param workflow The workflow to finish.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void finish(final Workflow workflow) {
        if (workflow.getTasksState() != TaskState.ENABLED) {
            throw new IllegalArgumentException(String.format(
                "Workflow \"%s\" is not enabled.",
                workflow.getName().getValue(defaultLocale)));
        }

        workflow.setTasksState(TaskState.FINISHED);
        workflowRepo.save(workflow);

    }

    /**
     * Enables a {@link Workflow}.
     *
     * @param workflow The workflow to enable.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void enable(final Workflow workflow) {
        if (workflow.getTasksState() == TaskState.ENABLED) {
            return;
        }

        switch (workflow.getTasksState()) {
            case DISABLED:
                LOGGER.debug("Workflow \"{}\" is disabled; enabling it.",
                             workflow.getName().getValue(defaultLocale));
                workflow.setTasksState(TaskState.ENABLED);
                workflowRepo.save(workflow);
                break;
            case FINISHED:
                LOGGER.debug("Workflow \"{}\" is finished; reenabling it.");
                workflow.setTasksState(TaskState.ENABLED);
                workflowRepo.save(workflow);
                break;
            default:
                LOGGER.debug("Workflow \"{}\" has tasksState \"{}\", "
                                 + "#enable(Workflow) does nothing.",
                             workflow.getName().getValue(defaultLocale),
                             workflow.getTasksState());
                break;
        }
    }

    /**
     * Disables a {@link Workflow}.
     *
     * @param workflow The workflow to disable.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void disable(final Workflow workflow) {
        if (workflow.getTasksState() == TaskState.DISABLED) {
            return;
        }

        workflow.setTasksState(TaskState.DISABLED);
        workflowRepo.save(workflow);

        workflow.getTasks().forEach(task -> taskManager.disable(task));
        workflow.setState(WorkflowState.INIT);
    }

}
