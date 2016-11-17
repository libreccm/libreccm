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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.core.CoreConstants;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;
import org.libreccm.security.Shiro;
import org.libreccm.security.User;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class TaskManager {

    private static final Logger LOGGER = LogManager.getLogger(TaskManager.class);
    
    @Inject
    private EntityManager entityManager;

    @Inject
    private WorkflowRepository workflowRepo;

    @Inject
    private TaskRepository taskRepo;

    @Inject
    private RoleRepository roleRepo;

    @Inject
    private Shiro shiro;

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void addTask(final Workflow workflow, final Task task) {
        workflow.addTask(task);
        task.setWorkflow(workflow);

        workflowRepo.save(workflow);
        taskRepo.save(task);
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void removeTask(final Workflow workflow, final Task task) {
        workflow.removeTask(task);
        task.setWorkflow(null);

        workflowRepo.save(workflow);
        taskRepo.save(task);
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void addDependentTask(final Task parent, final Task task)
        throws CircularTaskDependencyException {

        checkForCircularDependencies(parent, task);

        parent.addDependentTask(task);
        task.addDependsOn(parent);

        taskRepo.save(task);
        taskRepo.save(parent);
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void removeDependentTask(final Task parent, final Task task) {
        parent.removeDependentTask(task);
        task.removeDependsOn(parent);

        taskRepo.save(task);
        taskRepo.save(parent);
    }

    private void checkForCircularDependencies(final Task task1,
                                              final Task task2)
        throws CircularTaskDependencyException {

        if (dependsOn(task1, task2)) {
            throw new CircularTaskDependencyException();
        }
    }

    private boolean dependsOn(final Task task, final Task dependsOn) {
        for (final Task current : task.getDependsOn()) {
            if (current.equals(dependsOn)) {
                return true;
            }

            if (current.getDependsOn() != null
                    && !current.getDependsOn().isEmpty()) {
                return dependsOn(current, dependsOn);
            }
        }

        return false;
    }

    public void addComment(final Task task, final String comment) {
        addComment(task, shiro.getUser(), comment);
    }

    public void addComment(final Task task,
                           final User author,
                           final String comment) {
        final TaskComment taskComment = new TaskComment();
        taskComment.setAuthor(author);
        taskComment.setComment(comment);
        
        task.addComment(taskComment);
        
        entityManager.persist(taskComment);
        taskRepo.save(task);
    }

    public void removeComment(final Task task, final TaskComment comment) {
        task.removeComment(comment);
        taskRepo.save(task);
    }

    public void enable(final Task task) {
        switch(task.getTaskState()) {
            case DISABLED:
                task.setTaskState(TaskState.ENABLED);
                taskRepo.save(task);
                break;
            case FINISHED:
                task.setTaskState(TaskState.ENABLED);
                taskRepo.save(task);
                break;
            default:
                LOGGER.debug("Task {} is in state \"{}\"; doing nothing.",
                             Objects.toString(task),
                             Objects.toString(task.getTaskState()));
                break;
        }
    }

    public void disable(final Task task) {
        task.setTaskState(TaskState.DISABLED);
        taskRepo.save(task);
    }
    
    public void finish(final Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Can't finished null...");
        }
        
        if (task.getTaskState() != TaskState.ENABLED) {
            throw new IllegalArgumentException(String.format(
            "Task %s is not enabled.",
            Objects.toString(task)));
        }
        
        task.setTaskState(TaskState.FINISHED);
        taskRepo.save(task);
        
        task.getDependentTasks().forEach(dependent -> updateState(dependent));
    }

    protected void updateState(final Task task) {
        LOGGER.debug("Updating state for task {}...",
                     Objects.toString(task));
        
        boolean dependenciesSatisfied = true;
        
        if (task.getTaskState() == TaskState.DELETED || !task.isActive()) {
            return;
        }
        
        for(final Task dependsOnTask : task.getDependsOn()) {
            LOGGER.debug("Checking dependency {}...",
                         Objects.toString(dependsOnTask));
            if (dependsOnTask.getTaskState() != TaskState.FINISHED
                && dependsOnTask.isActive()) {
                
                LOGGER.debug("Dependency is not yet satisfied.");
                
                dependenciesSatisfied = false;
                break;
            }
        }
        
        LOGGER.debug("Dependencies state is {}", dependenciesSatisfied);
        
        // Rollback case. Previously finished task, but parent tasks
        // are re-enabled.
        if (task.getTaskState() == TaskState.FINISHED) {
            if (dependenciesSatisfied) {
                enable(task);
                return;
            } else {
                disable(task);
                return;
            }
        }
        
        if (task.getTaskState() == TaskState.ENABLED) {
            if (!dependenciesSatisfied) {
                disable(task);
                return;
            }
        }
        
        if (task.getTaskState() == TaskState.DISABLED) {
            if (dependenciesSatisfied) {
                enable(task);
            }
        }
     }

}
