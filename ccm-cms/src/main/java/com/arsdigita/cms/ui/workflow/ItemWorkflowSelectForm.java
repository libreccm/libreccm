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
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.web.Web;
import org.libreccm.workflow.Workflow;
import org.libreccm.workflow.WorkflowTemplate;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.TooManyListenersException;
import org.apache.logging.log4j.LogManager;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.workflow.WorkflowManager;
import org.libreccm.workflow.WorkflowTemplateRepository;
import org.librecms.contentsection.ContentItem;

/**
 * This panel displays a radio group of available Workflow Templates in this
 * content section that can be applied to this item.
 *
 * @author Uday Mathur
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class ItemWorkflowSelectForm extends CMSForm {

    private static final Logger LOGGER = LogManager.getLogger(
        ItemWorkflowSelectForm.class);

    private RadioGroup radioGroup;

    public ItemWorkflowSelectForm() {
        super("applyWorkflow", new SimpleContainer());

        addFormWidgets();
        addProcessListener(new ProcessListener());
    }

    protected void addFormWidgets() {
        radioGroup = new RadioGroup(new BigDecimalParameter("workflowSelect"));
        radioGroup.setClassAttr("vertical");

        try {
            radioGroup.addPrintListener(new WorkflowsOptionPrintListener());
        } catch (TooManyListenersException t) {
            LOGGER.error("Too many listeners", t);
        }

        radioGroup.addValidationListener(new NotNullValidationListener());
        add(radioGroup);

        add(new Submit("apply_wf", "Apply Workflow"));
    }

    /**
     * Adds a FormProcessListener to that applies a clone of the
     * WorkflowTemplate to this ContentItem. In case of double-click, no change
     * is made.
     */
    private class ProcessListener implements FormProcessListener {

        @Override
        public final void process(final FormSectionEvent event)
            throws FormProcessException {

            final PageState state = event.getPageState();
            final Long flowId = (Long) radioGroup.getValue(state);

            final ContentItem item = CMS.getContext().getContentItem();

            if (item.getWorkflow() == null) {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final WorkflowTemplateRepository templateRepo = cdiUtil.
                    findBean(WorkflowTemplateRepository.class);
                final WorkflowManager workflowManager = cdiUtil.findBean(
                    WorkflowManager.class);

                final WorkflowTemplate template = templateRepo.findById(flowId)
                    .get();
                workflowManager.createWorkflow(template, item);
            }
        }

    }

}
