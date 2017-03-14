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
import com.arsdigita.toolbox.ui.Property;
import com.arsdigita.toolbox.ui.PropertyList;
import com.arsdigita.toolbox.ui.Section;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Shiro;
import org.libreccm.workflow.Task;
import org.libreccm.workflow.TaskManager;
import org.libreccm.workflow.TaskRepository;
import org.libreccm.workflow.Workflow;
import org.librecms.contentsection.privileges.AdminPrivileges;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

abstract class BaseWorkflowItemPane extends BaseItemPane {

    final WorkflowRequestLocal workflowRequestLocal;
    final TaskRequestLocal taskRequestLocal;

    ActionGroup actionGroup;
    final TaskTable taskTable;

    final SimpleContainer detailPane;
    final TaskItemPane taskItemPane;
    final SummarySection summarySection;

    public BaseWorkflowItemPane(final WorkflowRequestLocal workflow,
                                final ActionLink editLink,
                                final ActionLink deleteLink) {
        workflowRequestLocal = workflow;

        taskTable = new TaskTable();
        taskRequestLocal = new TaskSelectionRequestLocal();

        detailPane = new SimpleContainer();

        // Tasks
        final FinishLink taskFinishLink = new FinishLink();

        final ActionLink taskAddLink = new ActionLink(new Label(gz(
            "cms.ui.workflow.task.add")));
        final TaskAddForm taskAddForm = new TaskAddForm(workflowRequestLocal,
                                                        taskTable
                                                            .getRowSelectionModel());

        final ActionLink taskEditLink = new ActionLink(new Label(gz(
            "cms.ui.workflow.task.edit")));
        final TaskEditForm taskEditForm = new TaskEditForm(workflowRequestLocal,
                                                           taskRequestLocal);

        final ActionLink taskDeleteLink = new ActionLink(new Label(gz(
            "cms.ui.workflow.task.delete")));
        final TaskDeleteForm taskDeleteForm = new TaskDeleteForm();

        final ActionLink backLink = new ActionLink(new Label(gz(
            "cms.ui.workflow.task.return")));
        backLink.addActionListener(new ResetListener());

        taskItemPane = new TaskItemPane(workflowRequestLocal, taskRequestLocal,
                                        taskFinishLink, taskEditLink,
                                        taskDeleteLink, backLink);

        summarySection = new SummarySection(editLink, deleteLink);
        detailPane.add(summarySection);
        detailPane.add(new TaskSection(taskAddLink));

        add(detailPane);
        setDefault(detailPane);
        add(taskItemPane);
        add(taskAddForm);
        add(taskEditForm);
        add(taskDeleteForm);

        connect(taskTable, 0, taskItemPane);

        connect(taskAddLink, taskAddForm);
        connect(taskAddForm, taskItemPane);

        connect(taskEditLink, taskEditForm);
        connect(taskEditForm);

        connect(taskDeleteLink, taskDeleteForm);
        connect(taskDeleteForm, detailPane);
    }

    protected class AdminVisible extends VisibilityComponent {

        public AdminVisible(final Component child) {
            super(child, AdminPrivileges.ADMINISTER_WORKFLOW);
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
            final CmsTask task = taskRequestLocal.getTask(state);
            final User lockingUser = task.getLockingUser();
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final Shiro shiro = cdiUtil.findBean(Shiro.class);
            final User currentUser = shiro.getUser().get();

            return task.isLocked() && (lockingUser == null
                                       || lockingUser.equals(currentUser));
        }

        private class Listener implements ActionListener {

            @Override
            public final void actionPerformed(final ActionEvent event) {
                final PageState state = event.getPageState();

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final TaskManager taskManager = cdiUtil.findBean(
                    TaskManager.class);

                final Task task = taskRequestLocal.getTask(state);
                taskManager.finish(task);
            }

        }

    }

    @Override
    public void reset(final PageState state) {
        super.reset(state);

        taskTable.getRowSelectionModel().clearSelection(state);
    }

    private class TaskDeleteForm extends BaseDeleteForm {

        TaskDeleteForm() {
            super(new Label(gz("cms.ui.workflow.task.delete_prompt")));

            addSecurityListener(AdminPrivileges.ADMINISTER_WORKFLOW);
        }

        @Override
        public final void process(final FormSectionEvent event)
            throws FormProcessException {
            final PageState state = event.getPageState();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final TaskRepository taskRepo = cdiUtil.findBean(
                TaskRepository.class);

            final Task task = taskRequestLocal.getTask(state);
            taskRepo.delete(task);

            taskTable.getRowSelectionModel().clearSelection(state);
        }

    }

    private class TaskSelectionRequestLocal extends TaskRequestLocal {

        @Override
        protected final Object initialValue(final PageState state) {

            final Long key = Long.parseLong(taskTable.getRowSelectionModel()
                .getSelectedKey(state).toString());

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final TaskRepository taskRepo = cdiUtil.findBean(
                TaskRepository.class);

            final CmsTask task = (CmsTask) taskRepo
                .findById(key)
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                "No Task with ID %d in the database. "
                    + "Where did that ID come from?",
                key)));

            return task;
        }

    }

    class SummarySection extends Section {

        SummarySection(final ActionLink editLink,
                       final ActionLink deleteLink) {
            setHeading(new Label(gz("cms.ui.workflow.details")));

            actionGroup = new ActionGroup();
            setBody(actionGroup);

            actionGroup.setSubject(new Properties());
            actionGroup.addAction(new AdminVisible(editLink),
                                  ActionGroup.EDIT);
            actionGroup.addAction(new AdminVisible(deleteLink),
                                  ActionGroup.DELETE);
        }

        private class Properties extends PropertyList {

            @Override
            protected final List<Property> properties(final PageState state) {
                @SuppressWarnings("unchecked")
                final List<Property> props = super.properties(state);
                @SuppressWarnings("unchecked")
                final Workflow workflow
                                   = ((Optional<Workflow>) workflowRequestLocal
                                      .get(state)).get();

                final KernelConfig kernelConfig = KernelConfig.getConfig();
                final Locale defaultLocale = kernelConfig.getDefaultLocale();

                props.add(new Property(gz("cms.ui.workflow.name"),
                                       workflow.getName()
                                           .getValue(defaultLocale)));
                props.add(new Property(
                    gz("cms.ui.workflow.description"),
                    workflow.getDescription().getValue(defaultLocale)));
                if (workflow.getState() == null) {
                    props.add(new Property(gz("cms.ui.workflow.current_state"),
                                           gz("cms.ui.workflow.current_state.none")));
                } else {
                    props.add(new Property(gz("cms.ui.workflow.current_state"),
                                           workflow.getState().toString()));
                }
                return props;
            }

        }

    }

    class TaskSection extends Section {

        TaskSection(final ActionLink taskAddLink) {
            setHeading(new Label(gz("cms.ui.workflow.tasks")));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(taskTable);
            group.addAction(new AdminVisible(taskAddLink), ActionGroup.ADD);
        }

    }

    private static final String[] COLUMNS = new String[]{
        lz("cms.ui.workflow.task.name"),
        lz("cms.ui.workflow.task.description"),
        lz("cms.ui.workflow.task.dependencies"),
        lz("cms.ui.workflow.task.state")
    };

    private class TaskTable extends Table {

        public TaskTable() {
            super(new TaskTableModelBuilder(workflowRequestLocal), COLUMNS);

            setEmptyView(new Label(gz("cms.ui.workflow.task.none")));

            getColumn(0).setCellRenderer(new DefaultTableCellRenderer(true));
        }

    }

}
