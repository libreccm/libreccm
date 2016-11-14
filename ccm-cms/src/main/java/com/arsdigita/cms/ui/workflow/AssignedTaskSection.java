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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.Section;
import com.arsdigita.web.Web;

import org.libreccm.workflow.Workflow;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Shiro;
import org.libreccm.workflow.Task;
import org.libreccm.workflow.UserTask;
import org.libreccm.workflow.UserTaskRepository;
import org.libreccm.workflow.WorkflowConstants;
import org.libreccm.workflow.WorkflowManager;
import org.libreccm.workflow.WorkflowRepository;
import org.librecms.CmsConstants;
import org.librecms.workflow.CmsTask;
import org.librecms.workflow.CmsTaskType;

import java.util.List;

/**
 * @author unknown
 * @author Sören Bernstein <quasi@quasiweb.de>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class AssignedTaskSection extends Section {

    private final WorkflowRequestLocal m_workflow;
    private final WorkflowFacade m_facade;

    public AssignedTaskSection(final WorkflowRequestLocal workflow,
                               final Component subject) {
        super(gz("cms.ui.workflow.task.assigned"));

        m_workflow = workflow;
        m_facade = new WorkflowFacade(m_workflow);

        final ActionGroup group = new ActionGroup();
        setBody(group);

        group.setSubject(subject);
        group.addAction(new RestartLink());
        //jensp 2014-06-06 Removed this two links because the funcationality they provide should
        //be accessible from this place. 
        //group.addAction(new LockLink());
        //group.addAction(new UnlockLink());
    }

    @Override
    public final boolean isVisible(final PageState state) {
        return m_workflow.getWorkflow(state) != null;
    }

    private class RestartLink extends ActionLink {

        RestartLink() {
            super(new Label(gz("cms.ui.workflow.restart_stopped_workflow")));

            setClassAttr("restartWorkflowLink");
            addActionListener(new Listener());
        }

        @Override
        public final boolean isVisible(final PageState state) {
            return m_facade.workflowState(state, WorkflowConstants.INIT) 
                   || m_facade.workflowState(state, WorkflowConstants.STOPPED);
        }

        private class Listener implements ActionListener {

            @Override
            public final void actionPerformed(final ActionEvent event) {
                m_facade.restartWorkflow(event.getPageState());
            }

        }

    }

    private class LockLink extends ActionLink {

        LockLink() {
            super(new Label(gz("cms.ui.workflow.task.assigned.lock_all")));

            addActionListener(new Listener());
        }

        @Override
        public final boolean isVisible(final PageState state) {
            return m_facade.workflowState(state, WorkflowConstants.STARTED) 
                   && m_facade.tasksExist(state)
                   && !m_facade.tasksLocked(state);
        }

        private class Listener implements ActionListener {

            @Override
            public final void actionPerformed(final ActionEvent event) {
                m_facade.lockTasks(event.getPageState());
            }

        }

    }

    private class UnlockLink extends ActionLink {

        UnlockLink() {
            super(new Label(gz("cms.ui.workflow.task.assigned.unlock_all")));

            addActionListener(new UnlockLink.Listener());
        }

        @Override
        public final boolean isVisible(final PageState state) {
            return m_facade.workflowState(state, WorkflowConstants.STARTED) 
                   && m_facade.tasksExist(state)
                   && m_facade.tasksLocked(state);
        }

        private class Listener implements ActionListener {

            @Override
            public final void actionPerformed(final ActionEvent event) {
                m_facade.unlockTasks(event.getPageState());
            }

        }

    }

    private class WorkflowFacade {

        private final WorkflowRequestLocal m_flow;
        private final TaskListRequestLocal m_tasks;

        WorkflowFacade(final WorkflowRequestLocal flow) {
            m_flow = flow;
            m_tasks = new TaskListRequestLocal();
        }

        private class TaskListRequestLocal extends RequestLocal {

            @Override
            protected final Object initialValue(final PageState state) {
                final Workflow workflow = m_flow.getWorkflow(state);
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final UserTaskRepository userTaskRepo = cdiUtil.findBean(
                    UserTaskRepository.class);
                final Shiro shiro = cdiUtil.findBean(Shiro.class);
                return userTaskRepo.findEnabledTasksForWorkflow(shiro.getUser(), 
                                                                workflow);
            }

            @SuppressWarnings("unchecked")
            final List<UserTask> getTasks(final PageState state) {
                return (ArrayList<UserTask>) get(state);
            }

        }

        final void restartWorkflow(final PageState state) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final WorkflowManager workflowManager = cdiUtil.findBean(
                    WorkflowManager.class);
            final Workflow workflow = m_flow.getWorkflow(state);
            workflowManager.start(workflow);

            // Lock tasks if not locked
            if (!tasksLocked(state)) {
                lockTasks(state);
            }
        }

        final void lockTasks(final PageState state) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final WorkflowManager workflowManager = cdiUtil.findBean(WorkflowManager.class);
            
            for(final UserTask task : m_tasks.getTasks(state)) {
                if (relevant(task) && !task.isLocked()) {
                    workflowManager.lockTask(task);
                }
            }
        }

        final void unlockTasks(final PageState state) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final WorkflowManager workflowManager = cdiUtil.findBean(WorkflowManager.class);
            
            for(final UserTask task : m_tasks.getTasks(state)) {
                if (relevant(task) && task.isLocked()) {
                    workflowManager.unlockTask(task);
                }
            }
        }

        final boolean tasksLocked(final PageState state) {
            for(final UserTask task : m_tasks.getTasks(state)) {
                if (relevant(task) && !task.isLocked()) {
                    return false;
                }
            }
            
            return true;
        }

        final boolean workflowState(final PageState state, int processState) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final WorkflowManager workflowManager = cdiUtil.findBean(WorkflowManager.class);
            final Workflow workflow = m_flow.getWorkflow(state);
            
            return workflowManager.getState(workflow) == processState;
        }

        final boolean tasksExist(final PageState state) {
            return !m_tasks.getTasks(state).isEmpty();
        }

        private boolean relevant(final UserTask task) {
            return true;
            
//            ToDo
//            return task.getTaskType().getID().equals(CMSTaskType.AUTHOR)
//                       || task.getTaskType().getID().equals(CMSTaskType.EDIT);
        }

    }

    protected final static GlobalizedMessage gz(final String key) {
        return new GlobalizedMessage(key, CmsConstants.CMS_BUNDLE);
    }

    protected final static String lz(final String key) {
        return (String) gz(key).localize();
    }

}
