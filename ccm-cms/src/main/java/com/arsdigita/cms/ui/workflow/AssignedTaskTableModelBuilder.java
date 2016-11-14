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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.RowData;
import com.arsdigita.bebop.table.TableModel;


import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.Assert;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.workflow.Workflow;
import org.libreccm.workflow.WorkflowConstants;
import org.libreccm.workflow.WorkflowManager;
import org.librecms.CmsConstants;

import java.util.Collections;
import java.util.Iterator;

/**
 *
 *
 */
class AssignedTaskTableModelBuilder extends AbstractTableModelBuilder {

    private final WorkflowRequestLocal m_workflow;

    public AssignedTaskTableModelBuilder(final WorkflowRequestLocal workflow) {
        m_workflow = workflow;
    }

    @Override
    public TableModel makeModel(final Table table, final PageState state) {
        return new Model(m_workflow.getWorkflow(state));
    }

    private static class Model implements TableModel {

        private final Iterator<RowData<Long>> m_iter;
//        private CmsTask m_task;
        private RowData<Long> rowData;

        Model(final Workflow workflow) {
            Assert.exists(workflow, Workflow.class);

            final CdiUtil cdiUtil= CdiUtil.createCdiUtil();
            final WorkflowManager workflowManager = cdiUtil.findBean(WorkflowManager.class);
    
            if (workflowManager.getState(workflow) == WorkflowConstants.STARTED) {
                final AssignedTaskController controller = cdiUtil.findBean(
                    AssignedTaskController.class);
                m_iter = controller.getAssignedTasks(workflow).iterator();
            } else {
                m_iter = Collections.emptyIterator();
            }
        }

        @Override
        public final int getColumnCount() {
            return 3;
        }

        @Override
        public final boolean nextRow() {
            if (m_iter.hasNext()) {
                rowData = m_iter.next();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public final Object getKeyAt(final int column) {
            return rowData.getRowKey();
        }

        @Override
        public final Object getElementAt(final int column) {
            switch (column) {
                case 0:
                    return rowData.getColData(0);
                case 1:
                    return rowData.getColData(1);
                case 2:
                    return rowData.getColData(2);
                default:
                    throw new IllegalArgumentException(String.format(
                        "Illegal column index %d. Valid column index: 0, 1, 2",
                        column));
            }
        }
    }

    protected final static GlobalizedMessage gz(final String key) {
        return new GlobalizedMessage(key, CmsConstants.CMS_BUNDLE);
    }

    protected final static String lz(final String key) {
        return (String) gz(key).localize();
    }
}
