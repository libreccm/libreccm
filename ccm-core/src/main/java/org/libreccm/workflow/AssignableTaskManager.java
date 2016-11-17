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
public class AssignableTaskManager extends TaskManager {

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
    public void assignTask(final AssignableTask task, final Role role) {
        final TaskAssignment assignment = new TaskAssignment();
        assignment.setTask(task);
        assignment.setRole(role);

        task.addAssignment(assignment);
        role.addAssignedTask(assignment);

        entityManager.persist(assignment);
        taskRepo.save(task);
        roleRepo.save(role);
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void retractTask(final AssignableTask task, final Role role) {
        final List<TaskAssignment> result = task.getAssignments().stream()
            .filter(assigned -> role.equals(assigned.getRole()))
            .collect(Collectors.toList());

        if (!result.isEmpty()) {
            final TaskAssignment assignment = result.get(0);
            task.removeAssignment(assignment);
            role.removeAssignedTask(assignment);
            entityManager.remove(assignment);
        }
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void lockTask(final AssignableTask task) {
        task.setLocked(true);
        task.setLockingUser(shiro.getUser());

        taskRepo.save(task);
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void unlockTask(final AssignableTask task) {
        task.setLocked(false);
        task.setLockingUser(null);

        taskRepo.save(task);
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public List<AssignableTask> lockedBy(final User user) {
        final TypedQuery<AssignableTask> query = entityManager.createNamedQuery(
            "UserTask.findLockedBy", AssignableTask.class);
        query.setParameter("user", user);

        return query.getResultList();
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void finish(final AssignableTask task) {
        final User currentUser = shiro.getUser();
        
        if (!currentUser.equals(task.getLockingUser())) {
            throw new IllegalArgumentException(String.format(
                "Current user %s is not locking user for task %s. Task is"
                    + "locaked by user %s.",
                Objects.toString(currentUser),
                Objects.toString(task),
                Objects.toString(task.getLockingUser())));
        }
        
        super.finish(task);
    }
    
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void finish(final AssignableTask task,
                       final String comment) {
        addComment(task, comment);
        finish(task);
    }
    
    
}
