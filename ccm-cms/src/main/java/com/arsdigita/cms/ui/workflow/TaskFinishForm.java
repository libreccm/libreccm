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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.parameters.BooleanParameter;

import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.util.UncheckedWrapperException;

import org.librecms.workflow.CmsTask;

import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;

import org.libreccm.workflow.Task;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.arsdigita.cms.CMSConfig;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.security.PermissionChecker;
import org.libreccm.workflow.AssignableTask;
import org.libreccm.workflow.TaskDependency;
import org.libreccm.workflow.Workflow;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.workflow.CmsTaskType;

import java.util.List;

/**
 * <p>
 * A form that prompts the user to comment on and approve tasks and then
 * finishes the task if it was approved.</p>
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class TaskFinishForm extends CommentAddForm {

    private static final Logger LOGGER = LogManager.getLogger(
        TaskFinishForm.class);
    private final TaskRequestLocal m_task;
    private final Label m_approvePrompt;
    private final RadioGroup m_approve;

    public TaskFinishForm(final TaskRequestLocal task) {
        super(task);

        m_task = task;

        m_approve = new RadioGroup(new BooleanParameter("approve"));
        m_approve.addOption(new Option(
            "true",
            new Label(gz("cms.ui.workflow.task.approve"))));
        m_approve.addOption(new Option(
            "false",
            new Label(gz("cms.ui.workflow.task.reject"))));

        m_approvePrompt = new Label(gz("cms.ui.workflow.task.approve_prompt"));

        addComponent(m_approvePrompt);
        addComponent(m_approve);

        addInitListener(new InitListener());
        addValidationListener(new ValidationListener());
        addProcessListener(new ProcessListener());
    }

    private class InitListener implements FormInitListener {

        @Override
        public final void init(final FormSectionEvent e) {
            LOGGER.debug("Initializing task finish");

            final PageState state = e.getPageState();

            if (isVisible(state)) {
                final CmsTask task = m_task.getTask(state);

                if (requiresApproval(task)) {
                    m_approvePrompt.setVisible(state, true);
                    m_approve.setVisible(state, true);
                } else {
                    m_approvePrompt.setVisible(state, false);
                    m_approve.setVisible(state, false);
                }
            }
        }

    }

    private class ValidationListener implements FormValidationListener {

        @Override
        public final void validate(final FormSectionEvent e)
            throws FormProcessException {
            LOGGER.debug("Validating task finish");

            final PageState state = e.getPageState();
            final CmsTask task = m_task.getTask(state);

            if (requiresApproval(task) && m_approve.getValue(state) == null) {
                throw new FormProcessException(new GlobalizedMessage(
                    "cms.ui.workflow.task.approval_or_reject_required",
                    CmsConstants.CMS_BUNDLE));
            }
        }

    }

    private class ProcessListener implements FormProcessListener {

        @Override
        public final void process(final FormSectionEvent event)
            throws FormProcessException {
            LOGGER.debug("Processing task finish");

            final PageState state = event.getPageState();
            final CmsTask task = m_task.getTask(state);
            boolean finishedTask = false;

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PermissionChecker permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);
            final ContentItemRepository itemRepo = cdiUtil.findBean(
                ContentItemRepository.class);
            final Optional<ContentItem> item = itemRepo.findItemWithWorkflow(
                task.getWorkflow());

            if (!item.isPresent()) {
                throw new UncheckedWrapperException(
                    "Workflow not assigned to an item");
            }

            permissionChecker.checkPermission(task.getTaskType().getPrivilege(),
                                              item.get());

            final TaskFinishFormController controller = cdiUtil
                .findBean(TaskFinishFormController.class);
            final ConfigurationManager confManager = cdiUtil.findBean(
                ConfigurationManager.class);
            final KernelConfig kernelConfig = confManager.findConfiguration(
                KernelConfig.class);

            if (requiresApproval(task)) {
                LOGGER.debug("The task requires approval; checking to see "
                                 + "if it's approved");

                // XXX I think the fact that this returns a Boolean is
                // the effect of broken parameter marshalling in
                // Bebop.
                final Boolean isApproved = (Boolean) m_approve.getValue(state);

                if (isApproved.equals(Boolean.TRUE)) {
                    LOGGER.debug("The task is approved; finishing the task");

                    controller.finish(task);
                    finishedTask = true;
                } else {
                    LOGGER.debug("The task is rejected; reenabling dependent "
                                     + "tasks");

                    // Reenable the previous tasks.
                    for (final TaskDependency blockedTask : task
                        .getBlockedTasks()) {
                        LOGGER.debug("Reenabling task {}",
                                     blockedTask
                                         .getBlockedTask()
                                         .getLabel()
                                         .getValue(kernelConfig
                                             .getDefaultLocale()));

                        controller.enable(blockedTask.getBlockedTask());
                    }
                }
            } else {
                LOGGER.debug("The task does not require approval; finishing it");

                controller.finish(task);
                finishedTask = true;
            }
            if (finishedTask) {
                final Workflow workflow = task.getWorkflow();
                final List<AssignableTask> tasks = controller.findEnabledTasks(
                    workflow);
                for (final AssignableTask currentTask : tasks) {
                    if (!(currentTask instanceof CmsTask)) {
                        continue;
                    }

                    final CmsTask currentCmsTask = (CmsTask) currentTask;
                    final String privilege = currentCmsTask.getTaskType()
                        .getPrivilege();
                    if (permissionChecker.isPermitted(privilege,
                                                      workflow.getObject())) {
                        //Lock task for current user
                        controller.lock(currentCmsTask);

                        if (CmsTaskType.DEPLOY == currentCmsTask.getTaskType()) {

                        } else {
                            throw new RedirectSignal(
                                URL.there(
                                    state.getRequest(),
                                    controller
                                        .getContentItemPublishUrl(item.get())),
                                true);
                        }

                    }
                }

                // redirect to /content-center if streamlined creation mode is active.
                final CMSConfig cmsConfig = confManager.findConfiguration(
                    CMSConfig.class);
                if (cmsConfig.isUseStreamlinedCreation()) {
                    throw new RedirectSignal(
                        URL.there(state.getRequest(),
                                  CmsConstants.CONTENT_CENTER_URL),
                        true);
                }

            }
        }

    }

    private static boolean requiresApproval(final CmsTask task) {
        return task.getTaskType() != CmsTaskType.AUTHOR;
    }

}
