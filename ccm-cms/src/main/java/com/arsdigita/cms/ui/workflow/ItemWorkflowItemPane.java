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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.cms.CMS;

import org.librecms.workflow.CmsTask;
import org.libreccm.workflow.Workflow;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.libreccm.workflow.TaskRepository;
import org.libreccm.workflow.WorkflowManager;
import org.libreccm.workflow.WorkflowState;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.privileges.AdminPrivileges;

/**
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
final class ItemWorkflowItemPane extends BaseWorkflowItemPane {

    private final AssignedTaskTable assignedTaskTable;

    public ItemWorkflowItemPane(final WorkflowRequestLocal workflowRequestLocal,
                                final ActionLink editLink,
                                final ActionLink deleteLink) {

        super(workflowRequestLocal, editLink, deleteLink);

        actionGroup.addAction(new AdminVisible(new StartLink()));
        actionGroup.addAction(new AdminVisible(new StopLink()));

        assignedTaskTable = new AssignedTaskTable(workflowRequestLocal);
        detailPane.add(new AssignedTaskSection(workflowRequestLocal,
                                               assignedTaskTable));

        final TaskFinishForm taskFinishForm = new TaskFinishForm(
                new TaskSelectionRequestLocal());
        add(taskFinishForm);

        connect(assignedTaskTable, 2, taskFinishForm);
        connect(taskFinishForm);
    }

    private final class TaskSelectionRequestLocal extends TaskRequestLocal {

        @Override
        protected final Object initialValue(final PageState state) {
            final String taskId = assignedTaskTable.getRowSelectionModel().
                    getSelectedKey(state).toString();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final TaskRepository taskRepo = cdiUtil.findBean(
                    TaskRepository.class);

            return (CmsTask) taskRepo.findById(Long.parseLong(taskId)).get();
        }

    }

    private boolean hasAdmin(final PageState state) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PermissionChecker permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);

        final ContentSection section = CMS.getContext().getContentSection();

        return permissionChecker.isPermitted(
                AdminPrivileges.ADMINISTER_WORKFLOW, section);

    }

    private class StopLink extends ActionLink {

        StopLink() {
            super(new Label(gz("cms.ui.item.workflow.stop")));

            addActionListener(new Listener());
        }

        @Override
        public final boolean isVisible(final PageState state) {
            final Workflow workflow = workflowRequestLocal.getWorkflow(state);

            return workflow.getState() == WorkflowState.STARTED;
        }

        private class Listener implements ActionListener {

            @Override
            public final void actionPerformed(final ActionEvent event) {
                final PageState state = event.getPageState();

                if (hasAdmin(state)) {
                    final Workflow workflow = workflowRequestLocal.getWorkflow(
                            state);

                    final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                    final WorkflowManager workflowManager = cdiUtil.findBean(
                            WorkflowManager.class);
                    workflowManager.stop(workflow);
                }
            }

        }

    }

    private class StartLink extends ActionLink {

        StartLink() {
            super(new Label(gz("cms.ui.item.workflow.start")));

            addActionListener(new Listener());
        }

        @Override
        public final boolean isVisible(final PageState state) {
            final Workflow workflow = workflowRequestLocal.getWorkflow(state);

            // Start link should be visible if the workflow state is stopped 
            // or init
            return workflow.getState() == WorkflowState.STOPPED
                           || workflow.getState() == WorkflowState.INIT;
        }

        private class Listener implements ActionListener {

            @Override
            public final void actionPerformed(final ActionEvent event) {
                final PageState state = event.getPageState();

                if (hasAdmin(state)) {
                    final Workflow workflow = workflowRequestLocal.getWorkflow(
                            state);

                    final CdiUtil cdiUtil  =CdiUtil.createCdiUtil();
                    final WorkflowManager workflowManager = cdiUtil.findBean(
                            WorkflowManager.class);
                    workflowManager.start(workflow);
                }
            }

        }

    }

}
