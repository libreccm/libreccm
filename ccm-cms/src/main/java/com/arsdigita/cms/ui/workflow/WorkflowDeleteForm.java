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
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ui.BaseDeleteForm;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.workflow.Workflow;
import org.libreccm.workflow.WorkflowRepository;

/*
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: WorkflowDeleteForm.java 287 2005-02-22 00:29:02Z sskracic $
 */

class WorkflowDeleteForm extends BaseDeleteForm {

    final WorkflowRequestLocal m_workflow;

    WorkflowDeleteForm(final WorkflowRequestLocal workflow) {
        super(new Label(gz("cms.ui.workflow.delete_prompt")));

        m_workflow = workflow;
    }

    @Override
    public final void process(final FormSectionEvent event)
            throws FormProcessException {
        
        final Workflow workflow = m_workflow.getWorkflow(event.getPageState());

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final WorkflowRepository workflowRepo = cdiUtil.findBean(WorkflowRepository.class);
        workflowRepo.delete(workflow);
    }
}
