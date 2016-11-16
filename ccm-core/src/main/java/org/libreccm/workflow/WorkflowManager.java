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
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.CcmObject;
import org.libreccm.core.CoreConstants;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.security.Shiro;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
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
    private Shiro shiro;

    @Inject
    private ConfigurationManager confManager;

    private Locale defaultLocale;

    @PostConstruct
    private void init() {
        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);
        defaultLocale = kernelConfig.getDefaultLocale();
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public Workflow createWorkflow(final WorkflowTemplate template,
                                   final CcmObject object) {
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

        workflow.setObject(object);
        workflow.setState(WorkflowState.INIT);

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
            } catch (IllegalAccessException
                     | IllegalArgumentException
                     | InvocationTargetException ex) {
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

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Task> findFinishedTasks(final Workflow workflow) {
        final TypedQuery<Task> query = entityManager.createNamedQuery(
            "Task.findFinishedTasks", Task.class);
        query.setParameter("workflow", workflow);

        return Collections.unmodifiableList(query.getResultList());
    }

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

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void start(final Workflow workflow) {
        final WorkflowState oldState = workflow.getState();

        workflow.setState(WorkflowState.STARTED);
        if (oldState == WorkflowState.INIT) {
            workflow.setActive(true);
            updateState(workflow);

            for (final Task current : workflow.getTasks()) {
                current.setActive(true);
                taskManager.updateState(current);
            }
        }

        workflowRepo.save(workflow);
    }

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

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void stop(final Workflow workflow) {
        workflow.setState(WorkflowState.STOPPED);
        workflowRepo.save(workflow);
    }

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
