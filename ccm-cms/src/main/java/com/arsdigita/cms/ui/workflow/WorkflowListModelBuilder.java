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

import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.list.AbstractListModelBuilder;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.cms.CMS;
import com.arsdigita.kernel.KernelConfig;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.workflow.Workflow;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Builds a list of workflow templates registered to the current content
 * section.
 *
 * @author Michael Pih
 * @author <a href="mailto:jross@redhat.com">Justin Ross</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class WorkflowListModelBuilder extends AbstractListModelBuilder {

    protected static final String WORKFLOW_TEMPLATE_ID = "workflowTemplateId";

    protected static final String WORKFLOW_TEMPLATE_NAME
                                  = "workflowTemplateName";

    @Override
    public final ListModel makeModel(final List list, final PageState state) {
        return new Model();
    }

    private class Model implements ListModel {

        private final Iterator<Map<String, String>> templates;

        private Map<String, String> currentTemplate;

        public Model() {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final WorkflowAdminPaneController controller = cdiUtil.findBean(
                WorkflowAdminPaneController.class);

            templates = controller
                .listWorkflowTemplates(CMS.getContext().getContentSection())
                .iterator();
        }

        @Override
        public boolean next() {
            if (templates.hasNext()) {
                currentTemplate = templates.next();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Object getElement() {
            return currentTemplate.get(WORKFLOW_TEMPLATE_NAME);
        }

        @Override
        public String getKey() {
            return currentTemplate.get(WORKFLOW_TEMPLATE_ID);
        }

    }

}
