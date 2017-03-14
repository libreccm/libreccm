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
import com.arsdigita.bebop.SingleSelectionModel;
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
import org.librecms.workflow.CmsTaskType;

import java.util.List;
import java.util.Locale;
import java.util.TooManyListenersException;

/**
 * @author Justin Ross
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class TaskAddForm extends BaseTaskForm {

    protected final static String ERROR_MSG = "A workflow template with that "
                                                  + "name already exists in this "
                                              + "content section.";

    private final SingleSelectionModel<Long> m_model;

    public TaskAddForm(final WorkflowRequestLocal workflow,
                       final SingleSelectionModel<Long> model) {
        super("task", gz("cms.ui.workflow.task.add"), workflow);

        m_model = model;

        try {
            getDependenciesOptionGroup()
                .addPrintListener(new DependencyPrinter());
        } catch (TooManyListenersException tmle) {
            throw new UncheckedWrapperException(tmle);
        }

        addProcessListener(new ProcessListener());
    }

    private class DependencyPrinter implements PrintListener {

        @Override
        public final void prepare(final PrintEvent event) {
            final PageState state = event.getPageState();
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final WorkflowAdminPaneController controller = cdiUtil
                .findBean(WorkflowAdminPaneController.class);
            final List<Task> tasks = controller
                .getTasksForWorkflow(getWorkflowRequestLocal()
                    .getWorkflow(state));

            final OptionGroup options = (OptionGroup) event.getTarget();
            final KernelConfig kernelConfig = KernelConfig.getConfig();
            final Locale defaultLocale = kernelConfig.getDefaultLocale();

            options.clearOptions();
            tasks.forEach(task -> options.addOption(new Option(
                Long.toString(task.getTaskId()),
                task.getLabel().getValue(defaultLocale))));
        }

    }

    private class ProcessListener implements FormProcessListener {

        @Override
        public final void process(final FormSectionEvent event)
            throws FormProcessException {

            final PageState state = event.getPageState();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final WorkflowAdminPaneController controller = cdiUtil
                .findBean(WorkflowAdminPaneController.class);

            final CmsTask task = controller.addTask(
                getWorkflowRequestLocal().getWorkflow(state),
                (String) getNameTextField().getValue(state),
                (String) getDescriptionTextArea().getValue(state),
                CmsTaskType.valueOf((String) getTypeOptionGroup()
                    .getValue(state)),
                (String[]) getDependenciesOptionGroup().getValue(state));

            m_model.setSelectedKey(state, task.getTaskId());
        }

    }

}
