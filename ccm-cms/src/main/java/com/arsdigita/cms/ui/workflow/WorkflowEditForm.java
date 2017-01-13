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
import com.arsdigita.kernel.KernelConfig;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.workflow.Workflow;
import org.libreccm.workflow.WorkflowRepository;

import java.util.Locale;

/**
 * @author Uday Mathur
 * @author <a href="mailto:jross@redhat.com">Justin Ross</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class WorkflowEditForm extends BaseWorkflowForm {

    private final WorkflowRequestLocal m_workflow;

    WorkflowEditForm(final WorkflowRequestLocal workflow) {
        super("workflow", gz("cms.ui.workflow.edit"));

        m_workflow = workflow;

        addInitListener(new InitListener());
        addProcessListener(new ProcessListener());
    }

    private class ProcessListener implements FormProcessListener {

        @Override
        public final void process(final FormSectionEvent event)
            throws FormProcessException {

            final PageState state = event.getPageState();
            final Workflow workflow = m_workflow.getWorkflow(state);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final WorkflowRepository workflowRepo = cdiUtil.findBean(
                WorkflowRepository.class);
            final ConfigurationManager confManager = cdiUtil.findBean(
                ConfigurationManager.class);
            final KernelConfig kernelConfig = confManager.findConfiguration(
                KernelConfig.class);
            final Locale defaultLocale = kernelConfig.getDefaultLocale();

            workflow.getName().addValue(defaultLocale,
                                        (String) m_title.getValue(state));
            workflow.getDescription().addValue(
                defaultLocale,
                (String) m_description.getValue(state));

            workflowRepo.save(workflow);
        }

    }

    private class InitListener implements FormInitListener {

        @Override
        public final void init(final FormSectionEvent event) {
            final PageState state = event.getPageState();
            final Workflow workflow = m_workflow.getWorkflow(state);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ConfigurationManager confManager = cdiUtil.findBean(
                ConfigurationManager.class);
            final KernelConfig kernelConfig = confManager.findConfiguration(
                KernelConfig.class);
            final Locale defaultLocale = kernelConfig.getDefaultLocale();

            m_title.setValue(state, workflow.getName().getValue(defaultLocale));
            m_description.setValue(state, workflow.getDescription());
        }

    }

}
