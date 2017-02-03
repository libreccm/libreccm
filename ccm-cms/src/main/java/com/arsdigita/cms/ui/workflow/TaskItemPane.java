/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.arsdigita.cms.ui.workflow;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ui.BaseItemPane;
import com.arsdigita.cms.ui.VisibilityComponent;
import com.arsdigita.kernel.KernelConfig;

import org.librecms.workflow.CmsTask;
import org.libreccm.security.User;

import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.Property;
import com.arsdigita.toolbox.ui.PropertyList;
import com.arsdigita.toolbox.ui.Section;
import com.arsdigita.util.LockableImpl;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;
import org.libreccm.security.Shiro;
import org.libreccm.workflow.Task;
import org.libreccm.workflow.AssignableTask;
import org.libreccm.workflow.AssignableTaskManager;
import org.librecms.contentsection.privileges.AdminPrivileges;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:jross@redhat.com">Justin Ross</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
final class TaskItemPane extends BaseItemPane {

    private final WorkflowRequestLocal m_workflow;
    private final TaskRequestLocal m_task;

    private final SimpleContainer m_detailPane;

    TaskItemPane(final WorkflowRequestLocal workflow,
                 final TaskRequestLocal task,
                 final ActionLink finishLink,
                 final ActionLink editLink,
                 final ActionLink deleteLink,
                 final ActionLink backLink) {
        m_workflow = workflow;
        m_task = task;

        m_detailPane = new SimpleContainer();
        m_detailPane.add(new Navigation(backLink));
        m_detailPane.add(new SummarySection(finishLink, editLink, deleteLink));

        // Users
        final ActionLink userAddLink = new ActionLink(new Label(gz(
            "cms.ui.workflow.task.user.add")));

        final TaskAddUser userAddPane = new TaskAddUser(m_task);

        final Form search = userAddPane.getSearchForm();
        final Form add = userAddPane.getAddForm().getForm();

        // Roles
        final ActionLink roleAddLink = new ActionLink(new Label(gz(
            "cms.ui.workflow.task.role.add")));

        final TaskAddRole roleAddForm = new TaskAddRole(m_task);

        m_detailPane.add(new RoleSection(roleAddLink));

        add(m_detailPane);
        setDefault(m_detailPane);
        add(userAddPane);
        add(roleAddForm);

        userAddLink.addActionListener(new NavigationListener(userAddPane));
        search.addSubmissionListener(new CancelListener(search));
        add.addSubmissionListener(new CancelListener(add));
        add.addProcessListener(new FormNavigationListener(m_detailPane));

        connect(roleAddLink, roleAddForm);
        resume(roleAddForm, m_detailPane);
    }

    private boolean hasAdmin(final PageState state) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PermissionChecker permissionChecker = cdiUtil.findBean(
            PermissionChecker.class);

        return permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_WORKFLOW);
    }

    private class AdminVisible extends VisibilityComponent {

        public AdminVisible(final Component child) {
            super(child, AdminPrivileges.ADMINISTER_WORKFLOW);
        }

    }

    private class AssigneeVisible extends AdminVisible {

        private final Component m_child;
        private final Assigned m_assigned;

        public AssigneeVisible(final Component child) {
            super(child);

            m_child = child;
            m_assigned = new Assigned();
        }

        @Override
        public final boolean isVisible(final PageState state) {
            if (m_child.isVisible(state)) {
                return m_assigned.isAssigned(state) || hasPermission(state);
            } else {
                return false;
            }
        }

        private class Assigned extends RequestLocal {

            @Override
            protected final Object initialValue(final PageState state) {
                if (assigned(state)) {
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }
            }

            private boolean assigned(final PageState state) {
                final CmsTask task = m_task.getTask(state);

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final AssignableTaskManager taskManager = cdiUtil.findBean(
                    AssignableTaskManager.class);
                final Shiro shiro = cdiUtil.findBean(Shiro.class);

                final User user = shiro.getUser().get();

                final List<AssignableTask> tasks = taskManager.lockedBy(user);

                return tasks.contains(task);
            }

            final boolean isAssigned(final PageState state) {
                return ((Boolean) get(state));
            }

        }

    }

    private class VisibilityListener implements ActionListener {

        private final TableColumn m_column;

        VisibilityListener(final TableColumn column) {
            m_column = column;
        }

        @Override
        public final void actionPerformed(final ActionEvent event) {
            final PageState state = event.getPageState();

            if (state.isVisibleOnPage(TaskItemPane.this) && !hasAdmin(state)) {
                m_column.setVisible(state, false);
            }
        }

    }

    private class Navigation extends ActionGroup {

        Navigation(final ActionLink backLink) {
            addAction(backLink, ActionGroup.RETURN);
        }

    }

    private class SummarySection extends Section {

        SummarySection(final ActionLink finishLink,
                       final ActionLink editLink,
                       final ActionLink deleteLink) {
            setHeading(new Label(gz("cms.ui.workflow.task.details")));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(new Properties());
            group.addAction(new AssigneeVisible(new LockLink()));
            group.addAction(new AssigneeVisible(new UnlockLink()));
            group.addAction(new AssigneeVisible(finishLink));
            group.addAction(new AdminVisible(editLink), ActionGroup.EDIT);
            group.addAction(new AdminVisible(deleteLink), ActionGroup.DELETE);
        }

        private class LockLink extends ActionLink {

            LockLink() {
                super(new Label(gz("cms.ui.workflow.task.lock")));

                addActionListener(new Listener());
            }

            @Override
            public final boolean isVisible(final PageState state) {

                final CmsTask task = m_task.getTask(state);

                return task.isActive() && !task.isLocked();
            }

            private class Listener implements ActionListener {

                @Override
                public final void actionPerformed(final ActionEvent event) {
                    final PageState state = event.getPageState();

                    if (hasAdmin(state)) {
                        final CmsTask task = m_task.getTask(state);
                        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                        final AssignableTaskManager taskManager = cdiUtil
                            .findBean(AssignableTaskManager.class);
                        taskManager.lockTask(task);
                    }
                }

            }

        }

        private class UnlockLink extends ActionLink {

            UnlockLink() {
                super(new Label(gz("cms.ui.workflow.task.unlock")));

                addActionListener(new Listener());
            }

            @Override
            public final boolean isVisible(final PageState state) {
                final CmsTask task = m_task.getTask(state);

                return task.isActive() && task.isLocked();
            }

            private class Listener implements ActionListener {

                @Override
                public final void actionPerformed(final ActionEvent event) {
                    final PageState state = event.getPageState();

                    if (hasAdmin(state)) {
                        final CmsTask task = m_task.getTask(state);
                        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                        final AssignableTaskManager taskManager = cdiUtil
                            .findBean(AssignableTaskManager.class);
                        taskManager.unlockTask(task);
                    }
                }

            }

        }

        private class Properties extends PropertyList {

            @Override
            protected final List<Property> properties(final PageState state) {
                @SuppressWarnings("unchecked")
                final List<Property> props = super.properties(state);
                final CmsTask task = m_task.getTask(state);

                final KernelConfig kernelConfig = KernelConfig.getConfig();
                final Locale defaultLocale = kernelConfig.getDefaultLocale();

                props.add(new Property(gz("cms.ui.name"),
                                       task.getLabel().getValue(defaultLocale)));
                props.add(new Property(gz("cms.ui.description"),
                                       task.getDescription().getValue(
                                           defaultLocale)));
                props.add(new Property(gz("cms.ui.workflow.task.dependencies"),
                                       deps(task)));
                props.add(new Property(gz("cms.ui.workflow.task.state"),
                                       task.getTaskState().toString()));
                props.add(new Property(gz("cms.ui.workflow.task.locked"),
                                       task.isLocked()
                                           ? lz("cms.ui.yes") : lz("cms.ui.no")));

                return props;
            }

            private String deps(final CmsTask task) {
                final List<Task> dependencies = task.getDependsOn();
                final KernelConfig kernelConfig = KernelConfig.getConfig();
                final Locale defaultLocale = kernelConfig.getDefaultLocale();

                return dependencies.stream()
                    .map(dependency -> dependency.getLabel().getValue(
                    defaultLocale))
                    .collect(Collectors.joining(", "));
            }

        }

    }

    private class RoleSection extends Section {

        public RoleSection(final ActionLink roleAddLink) {
            setHeading(new Label(gz("cms.ui.workflow.task.roles")));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(new RoleTable());
            group.addAction(new AdminVisible(roleAddLink), ActionGroup.ADD);
        }

    }

    private class RoleTable extends Table {

        public RoleTable() {
            super(new RoleTableModelBuilder(m_task),
                  new String[]{
                      lz("cms.ui.name"), // XXX globz
                      lz("cms.ui.workflow.task.role.delete")
                  });

            setEmptyView(new Label(gz("cms.ui.workflow.task.role.none")));

            getColumn(1).setCellRenderer(new DefaultTableCellRenderer(true));

            addTableActionListener(new TableActionAdapter() {

                @Override
                public final void cellSelected(final TableActionEvent event) {
                    final PageState state = event.getPageState();
                    final int column = event.getColumn();

                    if (column == 1) {
                        if (hasAdmin(state)) {
                            final CmsTask task = m_task.getTask(state);
                            final Long roleId = Long.parseLong((String) event
                                .getRowKey());

                            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                            final AssignableTaskManager taskManager = cdiUtil
                                .findBean(AssignableTaskManager.class);
                            final RoleRepository roleRepo = cdiUtil.findBean(
                                RoleRepository.class);

                            final Role role = roleRepo.findById(roleId).get();
                            taskManager.retractTask(task, role);
                        }
                    }
                }

            });
        }

        @Override
        public final void register(final Page page) {
            super.register(page);

            page.addActionListener(new VisibilityListener(getColumn(1)));
        }

    }

    private static class RoleTableModelBuilder extends LockableImpl
        implements TableModelBuilder {

        private final TaskRequestLocal m_task;

        public RoleTableModelBuilder(final TaskRequestLocal task) {
            m_task = task;
        }

        @Override
        public final TableModel makeModel(final Table table,
                                          final PageState state) {
            return new Model(m_task.getTask(state));
        }

        private class Model implements TableModel {

            private final List<Role> roles;
            private Role role;
            private int index = -1;

            private Model(final CmsTask task) {
                roles = task.getAssignments().stream()
                    .map(assignment -> assignment.getRole())
                    .collect(Collectors.toList());
            }

            @Override
            public final int getColumnCount() {
                return 2;
            }

            @Override
            public final boolean nextRow() {
                index++;
                return index < roles.size();
            }

            @Override
            public final Object getKeyAt(final int column) {
                return Long.toString(role.getRoleId());
            }

            @Override
            public final Object getElementAt(final int column) {
                switch (column) {
                    case 0:
                        return role.getName();
                    case 1:
                        return lz("cms.ui.workflow.task.role.delete");
                    default:
                        throw new IllegalStateException();
                }
            }

        }

    }

}
