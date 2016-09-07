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
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.workflow.Task;
import org.libreccm.workflow.TaskRepository;
import org.librecms.workflow.CmsTaskType;
import org.librecms.workflow.CmsTaskTypeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TooManyListenersException;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @author Justin Ross
 */
class TaskEditForm extends BaseTaskForm {

    private TaskRequestLocal m_task;

    public TaskEditForm(final WorkflowRequestLocal workflow,
                        final TaskRequestLocal task) {
        super("task", gz("cms.ui.workflow.task.edit"), workflow);

        m_task = task;

        try {
            m_deps.addPrintListener(new DependencyPrinter());
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
            final List<Task> tasks = m_workflow.getWorkflow(state).getTasks();

            final OptionGroup options = (OptionGroup) event.getTarget();

            tasks.forEach(task -> addOption(task, state, options));
        }

    }

    private void addOption(final Task task,
                           final PageState state,
                           final OptionGroup options) {
        final KernelConfig kernelConfig = KernelConfig.getConfig();
        final Locale defaultLocale = kernelConfig.getDefaultLocale();
        if (m_task.getTask(state).getTaskId() != task.getTaskId()) {
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
            final CmsTask task = m_task.getTask(state);

            m_name.setValue(state, task.getLabel());
            m_description.setValue(state, task.getDescription());
            m_type.setValue(state, Long.toString(task.getTaskType()
                            .getTaskTypeId()));

            final List<Task> dependencies = task.getDependsOn();
            final List<String> depIdList =  dependencies.stream()
                .map(dependency -> Long.toString(dependency.getTaskId()))
                .collect(Collectors.toList());

            m_deps.setValue(state, depIdList.toArray());
        }

    }

    private class ProcessListener implements FormProcessListener {

        @Override
        public final void process(final FormSectionEvent event)
            throws FormProcessException {
            final PageState state = event.getPageState();
            final CmsTask task = m_task.getTask(state);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final TaskRepository taskRepo = cdiUtil.findBean(
                TaskRepository.class);
            final CmsTaskTypeRepository taskTypeRepo = cdiUtil.findBean(
                CmsTaskTypeRepository.class);
            final ConfigurationManager confManager = cdiUtil.findBean(
                ConfigurationManager.class);
            final KernelConfig kernelConfig = confManager.findConfiguration(
                KernelConfig.class);
            final Locale defaultLocale = kernelConfig.getDefaultLocale();

            task.getLabel().addValue(defaultLocale,
                                     (String) m_name.getValue(state));
            task.getDescription().addValue(
                defaultLocale,
                (String) m_description.getValue(state));

            final CmsTaskType taskType = taskTypeRepo.findById((Long) m_type
                .getValue(state));
            task.setTaskType(taskType);

            taskRepo.save(task);

            processDependencies(task, (String[]) m_deps.getValue(state));
        }

    }

}
