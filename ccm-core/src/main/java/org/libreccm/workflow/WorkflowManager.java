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
import org.libreccm.l10n.LocalizedString;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;
import org.libreccm.security.Shiro;
import org.libreccm.security.User;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class WorkflowManager {

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
    public Workflow createWorkflow(final WorkflowTemplate template) {
        final Workflow workflow = new Workflow();
        
        final LocalizedString name = new LocalizedString();
        template.getName().getValues().forEach(
            (locale, str) -> name.addValue(locale, str));
        workflow.setName(name);
        
        final LocalizedString description = new LocalizedString();
        template.getDescription().getValues().forEach(
        (locale, str) -> description.addValue(locale, str));
        workflow.setDescription(description);
        
        final Map<Long, Task> tasks = new HashMap<>();

        template.getTasks().forEach(taskTemplate -> createTask(taskTemplate,
                                                               tasks));
        template.getTasks().forEach(taskTemplate -> fixTaskDependencies(
            taskTemplate, tasks.get(taskTemplate.getTaskId()), tasks));

        tasks.values().forEach(task -> taskRepo.save(task));
        workflowRepo.save(workflow);

        return workflow;
    }

    private void createTask(final Task template, final Map<Long, Task> tasks) {
        final Class<? extends Task> templateClass = template.getClass();
        final Task task;
        try {
            task = templateClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
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
            } catch (IllegalAccessException |
                     IllegalArgumentException |
                     InvocationTargetException ex) {
                throw new RuntimeException();
            }

            tasks.put(template.getTaskId(), task);
        }
    }

    private void fixTaskDependencies(final Task template,
                                     final Task task,
                                     final Map<Long, Task> tasks) {
        if (template.getDependentTasks() != null
                && !template.getDependentTasks().isEmpty()) {
            template.getDependentTasks().forEach(dependent
                -> task.addDependentTask(tasks.get(dependent.getTaskId())));
        }

        if (template.getDependsOn() != null
                && !template.getDependsOn().isEmpty()) {
            template.getDependsOn().forEach(dependsOn
                -> task.addDependsOn(tasks.get(dependsOn.getTaskId())));
        }
    }

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
    public void assignTask(final UserTask task, final Role role) {
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
    public void retractTask(final UserTask task, final Role role) {
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
    public void addDependentTask(final Task parent, final Task task) {
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

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void lockTask(final UserTask task) {
        task.setLocked(true);
        task.setLockingUser(shiro.getUser());

        taskRepo.save(task);
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void unlockTask(final UserTask task) {
        task.setLocked(false);
        task.setLockingUser(null);

        taskRepo.save(task);
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public List<UserTask> lockedBy(final User user) {
        final TypedQuery<UserTask> query = entityManager.createNamedQuery(
            "UserTask.findLockedBy", UserTask.class);
        query.setParameter("user", user);

        return query.getResultList();
    }

}
