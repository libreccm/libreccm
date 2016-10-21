/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ui.BaseAdminPane;
import com.arsdigita.cms.ui.BaseDeleteForm;
import com.arsdigita.cms.ui.VisibilityComponent;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.workflow.Workflow;
import org.libreccm.workflow.WorkflowRepository;
import org.libreccm.workflow.WorkflowTemplateRepository;
import org.librecms.CmsConstants;

import org.librecms.contentsection.privileges.AdminPrivileges;

/**
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @author <a href="mailto:jross@redhat.com">Justin Ross</a>
 */
public final class WorkflowAdminPane extends BaseAdminPane {

    private final WorkflowRequestLocal m_workflow;

    public WorkflowAdminPane() {
        super(gz("cms.ui.workflows"), new WorkflowListModelBuilder());

        m_workflow = new SelectionRequestLocal();

        setAdd(gz("cms.ui.workflow.add"),
               new WorkflowAddForm(getSelectionModel()));
        setEdit(gz("cms.ui.workflow.edit"), new WorkflowEditForm(m_workflow));
        setDelete(gz("cms.ui.workflow.delete"), new DeleteForm());

        setIntroPane(new Label(gz("cms.ui.workflow.intro")));
        setItemPane(new WorkflowItemPane(m_workflow,
                                         getEditLink(),
                                         getDeleteLink()));

        addAction(new VisibilityComponent(
            getAddLink(), AdminPrivileges.ADMINISTER_WORKFLOW));
    }

    private class DeleteForm extends BaseDeleteForm {

        DeleteForm() {
            super(gz("cms.ui.workflow.delete_prompt"));

            addSecurityListener(AdminPrivileges.ADMINISTER_WORKFLOW);
        }

        @Override
        public final void process(final FormSectionEvent event)
            throws FormProcessException {
            final PageState state = event.getPageState();

            final CdiUtil cdiUtil= CdiUtil.createCdiUtil();
            final WorkflowRepository workflowRepo= cdiUtil.findBean(WorkflowRepository.class);

            final Workflow workflow = m_workflow.getWorkflow(state);
            workflowRepo.delete(workflow);

            getSelectionModel().clearSelection(state);
        }

    }

    private class SelectionRequestLocal extends WorkflowRequestLocal {

        @Override
        protected final Object initialValue(final PageState state) {
            final String id = getSelectionModel().getSelectedKey(state)
                .toString();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final WorkflowTemplateRepository templateRepo = cdiUtil.findBean(
                WorkflowTemplateRepository.class);
            
            return templateRepo.findById(Long.parseLong(id));
        }

    }

}
