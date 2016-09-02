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
import com.arsdigita.cms.CMS;
import com.arsdigita.kernel.KernelConfig;

import org.librecms.contentsection.ContentSection;
import org.libreccm.workflow.WorkflowTemplate;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.workflow.WorkflowTemplateRepository;
import org.librecms.contentsection.ContentSectionManager;

import java.util.Locale;

/**
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @author Uday Mathur
 * @author Michael Pih
 * @author <a href="mailto:jross@redhat.com">Justin Ross</a>
 */
class WorkflowAddForm extends BaseWorkflowForm {

    private final SingleSelectionModel<Long> m_model;

    WorkflowAddForm(final SingleSelectionModel<Long> model) {
        super("workflow", gz("cms.ui.workflow.add"));

        m_model = model;

        addProcessListener(new ProcessListener());
    }

    private class ProcessListener implements FormProcessListener {
        
        @Override
        public final void process(final FormSectionEvent event)
                throws FormProcessException {
            final PageState state = event.getPageState();

            final String label = (String) m_title.getValue(state);
            final String description = (String) m_description.getValue(state);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final WorkflowTemplateRepository workflowTemplateRepository = cdiUtil.findBean(
                WorkflowTemplateRepository.class);
            final ContentSectionManager sectionManager = cdiUtil.findBean(
                ContentSectionManager.class);
            final ConfigurationManager confManager = cdiUtil.findBean(ConfigurationManager.class);
            final KernelConfig kernelConfig = confManager.findConfiguration(
                KernelConfig.class);
            final Locale defaultLocale = kernelConfig.getDefaultLocale();
            
            final WorkflowTemplate workflow = new WorkflowTemplate();
            workflow.getName().addValue(defaultLocale, label);
            workflow.getDescription().addValue(defaultLocale, description);
            
            workflowTemplateRepository.save(workflow);

            final ContentSection section =
                CMS.getContext().getContentSection();
            sectionManager.addWorkflowTemplateToContentSection(workflow, section);

            m_model.setSelectedKey(state, workflow.getWorkflowId());
        }
    }
}
