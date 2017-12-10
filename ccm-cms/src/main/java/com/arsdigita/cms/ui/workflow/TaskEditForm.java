/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.ui.workflow;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.kernel.KernelConfig;

import org.librecms.workflow.CmsTask;

import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.workflow.Task;
import org.libreccm.workflow.TaskDependency;
import org.libreccm.workflow.Workflow;
import org.librecms.workflow.CmsTaskType;

import java.util.List;
import java.util.Locale;
import java.util.TooManyListenersException;
import java.util.stream.Collectors;

/**
 * @author Justin Ross
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class TaskEditForm extends BaseTaskForm {

    private TaskRequestLocal selectedTask;

    public TaskEditForm(final WorkflowRequestLocal workflow,
                        final TaskRequestLocal selectedTask) {
        super("task", gz("cms.ui.workflow.task.edit"), workflow);

        this.selectedTask = selectedTask;

        try {
            getDependenciesOptionGroup()
                .addPrintListener(new DependencyPrinter());
        } catch (TooManyListenersException tmle) {
            throw new UncheckedWrapperException(tmle);
        }

        addInitListener(new InitListener());
        addProcessListener(new ProcessListener());
    }

    private class DependencyPrinter implements PrintListener {

        @Override
        public final void prepare(final PrintEvent event) {
            final PageState state = event.getPageState();
            final Workflow workflow = getWorkflowRequestLocal()
                .getWorkflow(state);
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final WorkflowAdminPaneController controller = cdiUtil
                .findBean(WorkflowAdminPaneController.class);
            final List<Task> tasks = controller.getTasksForWorkflow(workflow);

            final OptionGroup options = (OptionGroup) event.getTarget();
            options.clearOptions();

            tasks.forEach(task -> addOption(task, state, options));
        }

    }

    private void addOption(final Task task,
                           final PageState state,
                           final OptionGroup options) {
        final KernelConfig kernelConfig = KernelConfig.getConfig();
        final Locale defaultLocale = kernelConfig.getDefaultLocale();
        if (selectedTask.getTask(state).getTaskId() != task.getTaskId()) {
            options.addOption(new Option(
                Long.toString(task.getTaskId()),
                task.getLabel().getValue(defaultLocale)));
        }
    }

    private class InitListener implements FormInitListener {

        @Override
        public final void init(final FormSectionEvent event)
            throws FormProcessException {
            final PageState state = event.getPageState();
            final CmsTask task = selectedTask.getTask(state);

            final Locale defaultLocale = KernelConfig
                .getConfig()
                .getDefaultLocale();

            getNameTextField().setValue(state,
                                        task.getLabel().getValue(defaultLocale));
            getDescriptionTextArea().setValue(
                state,
                task.getDescription().getValue(defaultLocale));
            getTypeOptionGroup().setValue(state, task.getTaskType().toString());

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final WorkflowAdminPaneController controller = cdiUtil.findBean(
                WorkflowAdminPaneController.class);

            final List<TaskDependency> blockedTasks = controller.getBlockedTasks(task);
            final List<String> depIdList = blockedTasks
                .stream()
                .map(TaskDependency::getBlockedTask)
                .map(blockedTask -> Long.toString(blockedTask.getTaskId()))
                .collect(Collectors.toList());

            getDependenciesOptionGroup().setValue(state, depIdList.toArray());
        }

    }

    private class ProcessListener implements FormProcessListener {

        @Override
        public final void process(final FormSectionEvent event)
            throws FormProcessException {
            final PageState state = event.getPageState();
            final CmsTask task = selectedTask.getTask(state);

            final String name = (String) getNameTextField().getValue(state);
            final String desc = (String) getDescriptionTextArea()
                .getValue(state);
            final CmsTaskType taskType = CmsTaskType
                .valueOf((String) getTypeOptionGroup().getValue(state));
            final String[] deps = (String[]) getDependenciesOptionGroup()
                .getValue(state);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final WorkflowAdminPaneController controller = cdiUtil
                .findBean(WorkflowAdminPaneController.class);
            
            controller.updateTask(task, name, desc, taskType, deps);
        }

    }

}
