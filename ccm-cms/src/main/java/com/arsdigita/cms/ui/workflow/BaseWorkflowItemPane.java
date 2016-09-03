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

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.cms.ui.BaseDeleteForm;
import com.arsdigita.cms.ui.BaseItemPane;
import com.arsdigita.cms.ui.VisibilityComponent;
import com.arsdigita.kernel.KernelConfig;

import org.librecms.workflow.CmsTask;
import org.libreccm.security.User;

import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.PropertyList;
import com.arsdigita.toolbox.ui.Section;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Web;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Shiro;
import org.libreccm.workflow.Task;
import org.libreccm.workflow.TaskRepository;
import org.libreccm.workflow.Workflow;
import org.libreccm.workflow.WorkflowManager;
import org.librecms.CmsConstants;
import org.librecms.workflow.CmsTaskTypeRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

abstract class BaseWorkflowItemPane extends BaseItemPane {

    final WorkflowRequestLocal m_workflow;
    final TaskRequestLocal m_task;

    ActionGroup m_actionGroup;
    final TaskTable m_tasks;

    final SimpleContainer m_detailPane;
    final TaskItemPane m_taskPane;
    final SummarySection m_summarySection;

    public BaseWorkflowItemPane(final WorkflowRequestLocal workflow,
                                final ActionLink editLink,
                                final ActionLink deleteLink) {
        m_workflow = workflow;

        m_tasks = new TaskTable();
        m_task = new TaskSelectionRequestLocal();

        m_detailPane = new SimpleContainer();

        // Tasks
        final FinishLink taskFinishLink = new FinishLink();

        final ActionLink taskAddLink = new ActionLink(new Label(gz(
            "cms.ui.workflow.task.add")));
        final TaskAddForm taskAddForm = new TaskAddForm(m_workflow, m_tasks
                                                        .getRowSelectionModel());

        final ActionLink taskEditLink = new ActionLink(new Label(gz(
            "cms.ui.workflow.task.edit")));
        final TaskEditForm taskEditForm = new TaskEditForm(m_workflow, m_task);

        final ActionLink taskDeleteLink = new ActionLink(new Label(gz(
            "cms.ui.workflow.task.delete")));
        final TaskDeleteForm taskDeleteForm = new TaskDeleteForm();

        final ActionLink backLink = new ActionLink(new Label(gz(
            "cms.ui.workflow.task.return")));
        backLink.addActionListener(new ResetListener());

        m_taskPane = new TaskItemPane(m_workflow, m_task,
                                      taskFinishLink, taskEditLink,
                                      taskDeleteLink, backLink);

        m_summarySection = new SummarySection(editLink, deleteLink);
        m_detailPane.add(m_summarySection);
        m_detailPane.add(new TaskSection(taskAddLink));

        add(m_detailPane);
        setDefault(m_detailPane);
        add(m_taskPane);
        add(taskAddForm);
        add(taskEditForm);
        add(taskDeleteForm);

        connect(m_tasks, 0, m_taskPane);

        connect(taskAddLink, taskAddForm);
        connect(taskAddForm, m_taskPane);

        connect(taskEditLink, taskEditForm);
        connect(taskEditForm);

        connect(taskDeleteLink, taskDeleteForm);
        connect(taskDeleteForm, m_detailPane);
    }

    protected class AdminVisible extends VisibilityComponent {

        public AdminVisible(final Component child) {
            super(child, CmsConstants.PRIVILEGE_ADMINISTER_WORKFLOW);
        }

    }

    private class FinishLink extends ActionLink {

        FinishLink() {
            super(new Label(gz("cms.ui.workflow.task.finish")));

            addActionListener(new Listener());
            addActionListener(new ResetListener());
        }

        @Override
        public final boolean isVisible(final PageState state) {
            final CmsTask task = m_task.getTask(state);
            final User lockingUser = task.getLockingUser();
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final Shiro shiro = cdiUtil.findBean(Shiro.class);
            final User currentUser = shiro.getUser();

            boolean visible = task.isLocked()
                                  && (lockingUser == null
                                      || lockingUser.equals(currentUser));
            return visible;
        }

        private class Listener implements ActionListener {

            @Override
            public final void actionPerformed(final ActionEvent event) {
                final PageState state = event.getPageState();

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final WorkflowManager workflowManager = cdiUtil.findBean(
                    WorkflowManager.class);

                final Task task = m_task.getTask(state);
                task.setTaskState("finished");

            }

        }

    }

    public void reset(final PageState state) {
        super.reset(state);

        m_tasks.getRowSelectionModel().clearSelection(state);
    }

    private class TaskDeleteForm extends BaseDeleteForm {

        TaskDeleteForm() {
            super(new Label(gz("cms.ui.workflow.task.delete_prompt")));

            addSecurityListener(CmsConstants.PRIVILEGE_ADMINISTER_WORKFLOW);
        }

        @Override
        public final void process(final FormSectionEvent event)
            throws FormProcessException {
            final PageState state = event.getPageState();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final TaskRepository taskRepo = cdiUtil.findBean(
                TaskRepository.class);

            final Task task = m_task.getTask(state);
            taskRepo.delete(task);

            m_tasks.getRowSelectionModel().clearSelection(state);
        }

    }

    private class TaskSelectionRequestLocal extends TaskRequestLocal {

        @Override
        protected final Object initialValue(final PageState state) {
            final String id = m_tasks.getRowSelectionModel().getSelectedKey(
                state).toString();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final CmsTaskTypeRepository taskRepo = cdiUtil.findBean(
                CmsTaskTypeRepository.class);

            return taskRepo.findById(Long.parseLong(id));
        }

    }

    class SummarySection extends Section {

        SummarySection(final ActionLink editLink,
                       final ActionLink deleteLink) {
            setHeading(new Label(gz("cms.ui.workflow.details")));

            m_actionGroup = new ActionGroup();
            setBody(m_actionGroup);

            m_actionGroup.setSubject(new Properties());
            m_actionGroup.addAction(new AdminVisible(editLink),
                                    ActionGroup.EDIT);
            m_actionGroup.addAction(new AdminVisible(deleteLink),
                                    ActionGroup.DELETE);
//            m_actionGroup.addAction(new AdminVisible(new StartLink()));
//            m_actionGroup.addAction(new AdminVisible(new StopLink()));
        }

        private class Properties extends PropertyList {

            @Override
            protected final List<Property> properties(final PageState state) {
                @SuppressWarnings("unchecked")
                final List<Property> props = super.properties(state);
                final Workflow flow = (Workflow) m_workflow.get(state);

                final KernelConfig kernelConfig = KernelConfig.getConfig();
                final Locale defaultLocale = kernelConfig.getDefaultLocale();

                props.add(new Property(gz("cms.ui.name"),
                                       flow.getName().getValue(defaultLocale)));
                props.add(new Property(
                    gz("cms.ui.description"),
                    flow.getDescription().getValue(defaultLocale)));
//                props.add(new Property(gz("cms.ui.workflow.current_state"),
//                                       flow.getStateString()));
                props.add(new Property(gz("cms.ui.workflow.num_tasks"),
                                       String.valueOf(flow.getTasks().size())));

                return props;
            }

        }

    }

    class TaskSection extends Section {

        TaskSection(final ActionLink taskAddLink) {
            setHeading(new Label(gz("cms.ui.workflow.tasks")));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(m_tasks);
            group.addAction(new AdminVisible(taskAddLink), ActionGroup.ADD);
        }

    }

    // XXX Fix this.
    private static final String[] s_columns = new String[]{
        lz("cms.ui.name"),
        lz("cms.ui.description"),
        lz("cms.ui.workflow.task.dependencies"),
        lz("cms.ui.workflow.task.state")
    };

    private class TaskTable extends Table {

        public TaskTable() {
            super(new TaskTableModelBuilder(m_workflow), s_columns);

            setEmptyView(new Label(gz("cms.ui.workflow.task.none")));

            getColumn(0).setCellRenderer(new DefaultTableCellRenderer(true));
        }

    }

}
