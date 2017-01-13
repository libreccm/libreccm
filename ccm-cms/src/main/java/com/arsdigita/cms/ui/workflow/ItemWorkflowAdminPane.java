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
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.cms.ui.BaseItemPane;
import com.arsdigita.toolbox.ui.LayoutPanel;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.workflow.WorkflowRepository;

/**
 * Panel for applying a workflow template to a content item. By
 * default this panel display a list of workflows that can be applied
 * to the content item. If a workflow is applied, it displays the item
 * details page.
 *
 * @author Uday Mathur
 * @author Michael Pih
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ItemWorkflowAdminPane extends BaseItemPane {

    private final ParameterSingleSelectionModel m_model;
    private final WorkflowRequestLocal m_workflow;

    private final LayoutPanel m_detailPane;
    private final LayoutPanel m_selectPane;

    public ItemWorkflowAdminPane(final LongParameter itemIdParameter) {
        m_model = new ItemWorkflowSelectionModel(itemIdParameter);
        m_workflow = new SelectionRequestLocal();

        final ActionLink editLink = new ActionLink
            (new Label(gz("cms.ui.workflow.edit")));

        final WorkflowEditForm editForm = new WorkflowEditForm(m_workflow);

        final ActionLink deleteLink = new ActionLink
            (new Label(gz("cms.ui.workflow.delete")));

        final WorkflowDeleteForm deleteForm = new WorkflowDeleteForm
            (m_workflow);

        m_detailPane = new LayoutPanel();
        m_detailPane.setBody(new ItemWorkflowItemPane
                             (m_workflow, editLink, deleteLink));

        final ItemWorkflowSelectForm workflowSelectForm =
            new ItemWorkflowSelectForm();

        m_selectPane = new LayoutPanel();
        m_selectPane.setBody(workflowSelectForm);

        add(m_detailPane);
        setDefault(m_detailPane);
        add(m_selectPane);
        add(editForm);
        add(deleteForm);

        connect(workflowSelectForm, m_detailPane);
        connect(editLink, editForm);
        connect(deleteLink, deleteForm);
        connect(deleteForm, m_selectPane);
    }

    private class SelectionRequestLocal extends WorkflowRequestLocal {
        @Override
        protected final Object initialValue(final PageState state) {
            final String workflowId = m_model.getSelectedKey(state).toString();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final WorkflowRepository workflowRepo = cdiUtil.findBean(WorkflowRepository.class);
            return workflowRepo.findById(Long.parseLong(workflowId));
        }
    }

    @Override
    public final void register(final Page page) {
        super.register(page);
        
        page.addActionListener(new ActionListener() {
                @Override
                public final void actionPerformed(final ActionEvent event) {
                    final PageState state = event.getPageState();

                    if (state.isVisibleOnPage(ItemWorkflowAdminPane.this)
                            && m_model.getSelectedKey(state) == null) {
                        push(state, m_selectPane);
                    }
                }
            });
    }
}
