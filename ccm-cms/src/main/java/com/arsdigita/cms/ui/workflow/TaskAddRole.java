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

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.cms.CMS;

import org.librecms.contentsection.ContentSection;

import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.cms.ui.ListOptionPrintListener;

import org.librecms.workflow.CmsTask;

import com.arsdigita.globalization.GlobalizedMessage;

import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;
import org.libreccm.workflow.AssignableTaskManager;
import org.libreccm.workflow.TaskAssignment;
import org.libreccm.workflow.WorkflowManager;
import org.librecms.CmsConstants;
import org.librecms.contentsection.privileges.AdminPrivileges;

import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.stream.Collectors;

class TaskAddRole extends CMSForm {

    private final TaskRequestLocal m_task;
    private final OptionGroup m_roles;
    private final Submit m_add;
    private final Submit m_cancel;

    TaskAddRole(final TaskRequestLocal task) {
        super("GroupAssignForm");

        m_task = task;

        add(new Label(gz("cms.ui.workflow.task.roles")), ColumnPanel.TOP);

        m_roles = new CheckboxGroup("roles");
        add(m_roles);

        try {
            m_roles.addPrintListener(new RoleOptionPrintListener());
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException("TooManyListeners: " + ex
                .getMessage(), ex);
        }

        final SimpleContainer submits = new SimpleContainer();
        add(submits, GridPanel.FULL_WIDTH | GridPanel.CENTER);

        m_add = new Submit("add", gz("cms.ui.finish"));
        submits.add(m_add);

        m_cancel = new Submit("cancel", gz("cms.ui.cancel"));
        submits.add(m_cancel);

        addInitListener(new InitListener());
        addSubmissionListener(new SubmissionListener());
        addProcessListener(new ProcessListener());
    }

    private class InitListener implements FormInitListener {

        @Override
        public final void init(final FormSectionEvent event)
            throws FormProcessException {
            final PageState state = event.getPageState();
            final CmsTask task = m_task.getTask(state);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final WorkflowAdminPaneController controller = cdiUtil
                .findBean(WorkflowAdminPaneController.class);

            final List<Role> roles = controller.findAssignees(task);

            m_roles.setValue(state, roles);
        }

    }

    private class ProcessListener implements FormProcessListener {

        @Override
        public final void process(final FormSectionEvent event)
            throws FormProcessException {
            final PageState state = event.getPageState();
            if (m_add.isSelected(state)) {
                final CmsTask task = m_task.getTask(state);

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final WorkflowAdminPaneController controller = cdiUtil
                .findBean(WorkflowAdminPaneController.class);
//                final AssignableTaskManager taskManager = cdiUtil.findBean(
//                    AssignableTaskManager.class);
//                final RoleRepository roleRepository = cdiUtil.findBean(
//                    RoleRepository.class);
//
//                task.getAssignments().forEach(assignment -> {
//                    taskManager.retractTask(task, assignment.getRole());
//                });

                final String[] roleIds = (String[]) m_roles.getValue(state);

//                if (roleIds != null) {
//                    for (final String roleId : roleIds) {
//                        final Role role = roleRepository.findById(Long
//                            .parseLong(roleId)).get();
//                        taskManager.assignTask(task, role);
//                    }
//                }

                controller.assignTask(task, roleIds);
            }
        }

    }

    private class SubmissionListener implements FormSubmissionListener {

        @Override
        public final void submitted(final FormSectionEvent event)
            throws FormProcessException {
            final PageState state = event.getPageState();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PermissionChecker permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);

            if (!permissionChecker.isPermitted(AdminPrivileges.ADMINISTER_WORKFLOWS)) {
                throw new FormProcessException(
                    new GlobalizedMessage(
                        "cms.ui.workflow.insufficient_privileges",
                        CmsConstants.CMS_BUNDLE));
            }
        }

    }

    private class RoleOptionPrintListener extends ListOptionPrintListener<Role> {

        public static final String QUERY_NAME
                                       = "com.arsdigita.cms.getStaffRoles";

        public RoleOptionPrintListener() {
            super();
        }

        @Override
        protected List<Role> getDataQuery(final PageState state) {
            final ContentSection section = CMS.getContext().getContentSection();
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final WorkflowAdminPaneController controller = cdiUtil
                .findBean(WorkflowAdminPaneController.class);

            return controller.findRoles(section);
        }

        @Override
        public String getKey(final Role role) {
            return Long.toString(role.getRoleId());
        }

        @Override
        public String getValue(final Role role) {
            return role.getName();
        }

    }

    private static GlobalizedMessage gz(final String key) {
        return new GlobalizedMessage(key, CmsConstants.CMS_BUNDLE);
    }

}
