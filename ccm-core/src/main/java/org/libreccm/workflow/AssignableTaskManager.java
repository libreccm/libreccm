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

import org.libreccm.core.CoreConstants;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;
import org.libreccm.security.Shiro;
import org.libreccm.security.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
public class AssignableTaskManager {

    @Inject
    private EntityManager entityManager;

    @Inject
    private TaskRepository taskRepo;

    @Inject
    private TaskManager taskManager;

    @Inject
    private RoleRepository roleRepo;

    @Inject
    private Shiro shiro;

    /**
     * Assigns a task to role.
     *
     * @param task The task to assign.
     * @param role The role to which the task is assigned.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void assignTask(final AssignableTask task, final Role role) {
        if (task == null) {
            throw new IllegalArgumentException(
                "Can't assign task null to a role.");
        }

        if (role == null) {
            throw new IllegalArgumentException(
                "Can't assign a task to role null.");
        }

        final TaskAssignment assignment = new TaskAssignment();
        assignment.setTask(task);
        assignment.setRole(role);

        task.addAssignment(assignment);
        role.addAssignedTask(assignment);

        entityManager.persist(assignment);
        taskRepo.save(task);
        roleRepo.save(role);
    }

    /**
     * Deletes a task assignments.
     *
     * @param task
     * @param role
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void retractTask(final AssignableTask task, final Role role) {
        if (task == null) {
            throw new IllegalArgumentException(
                "Can't retract task null from a role.");
        }

        if (role == null) {
            throw new IllegalArgumentException(
                "Can't retract a task from role null.");
        }

        final Optional<TaskAssignment> result = task
        .getAssignments()
        .stream()
        .filter(assigned -> role.getRoleId() == assigned.getRole().getRoleId())
        .findAny();

        if (result.isPresent()) {
            final TaskAssignment assignment = result.get();
            task.removeAssignment(assignment);
            role.removeAssignedTask(assignment);
            entityManager.remove(assignment);
        }
    }

    /**
     *
     * @param task
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void lockTask(final AssignableTask task) {
        if (task == null) {
            throw new IllegalArgumentException("Can't lock task null.");
        }

        if (task.isLocked()) {
            throw new IllegalArgumentException(String.format(
                "Task %s is already locked by user \"%s\".",
                Objects.toString(task),
                task.getLockingUser().getName()));
        }

        final Optional<User> user = shiro.getUser();
        if (user.isPresent()) {
            task.setLocked(true);
            task.setLockingUser(user.get());
        } else {
            throw new IllegalStateException("No current user.");
        }

        taskRepo.save(task);
    }

    /**
     * Unlocks a task.
     *
     * @param task The task to unlock.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void unlockTask(final AssignableTask task) {
        if (task == null) {
            throw new IllegalArgumentException("Can't unlock task null.");
        }

        task.setLocked(false);
        task.setLockingUser(null);

        taskRepo.save(task);
    }

    /**
     * Retrieves a list of all tasks locked by a specific user.
     *
     * @param user The user which locks the tasks.
     *
     * @return A list with all tasks locked by the specified user.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public List<AssignableTask> lockedBy(final User user) {

        if (user == null) {
            throw new IllegalArgumentException("user can't be null.");
        }

        final TypedQuery<AssignableTask> query = entityManager.createNamedQuery(
            "AssignableTask.findLockedBy", AssignableTask.class);
        query.setParameter("user", user);

        return query.getResultList();
    }

    /**
     * Finishes a {@link AssignableTask}.
     *
     * @param task The task to finish.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void finish(final AssignableTask task) {
        final Optional<User> currentUser = shiro.getUser();

        if (!currentUser.isPresent()
            || !currentUser.get().equals(task.getLockingUser())) {
            throw new IllegalArgumentException(String.format(
                "Current user %s is not locking user for task %s. Task is"
                    + "locaked by user %s.",
                Objects.toString(currentUser),
                Objects.toString(task),
                Objects.toString(task.getLockingUser())));
        }

        taskManager.finish(task);
    }

    /**
     * Finishes a {@link AssignableTask} with a comment.
     *
     * @param task    The task to finish.
     * @param comment The comment to add to the task.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void finish(final AssignableTask task,
                       final String comment) {
        taskManager.addComment(task, comment);
        finish(task);
    }

    public List<AssignableTask> findAssignedTasks(final Workflow workflow,
                                                  final List<Role> roles) {
        final TypedQuery<AssignableTask> query = entityManager.createNamedQuery(
            "AssignableTask.findAssignedTasks", AssignableTask.class);
        query.setParameter("workflow", workflow);
        query.setParameter("roles", roles);

        return query.getResultList();
    }

}
