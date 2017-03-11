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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.util.Assert;
import com.arsdigita.util.GraphSet;
import com.arsdigita.util.Graphs;

import org.apache.logging.log4j.LogManager;
import org.libreccm.workflow.Task;
import org.libreccm.workflow.Workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;

class TaskTableModelBuilder extends AbstractTableModelBuilder {

    private static final Logger LOGGER = LogManager.getLogger(
        TaskTableModelBuilder.class);

    private final WorkflowRequestLocal m_workflow;

    TaskTableModelBuilder(final WorkflowRequestLocal workflow) {
        m_workflow = workflow;
    }

    @Override
    public final TableModel makeModel(final Table table,
                                      final PageState state) {
        LOGGER.debug("Creating a new table model for the current request");

        return new Model(m_workflow.getWorkflow(state));
    }

    private static class Model implements TableModel {

        private Task m_task;
        private Iterator m_tasks;
        private Map m_dependencies = new HashMap();

        private Model(final Workflow workflow) {

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final WorkflowAdminPaneController controller = cdiUtil.findBean(
                WorkflowAdminPaneController.class);

            final Iterator<Task> tasksIter = controller
                .getTasksForWorkflow(workflow)
                .iterator();
            GraphSet g = new GraphSet();

            while (tasksIter.hasNext()) {
                Task t = tasksIter.next();
                final Iterator<Task> deps = t.getDependsOn().iterator();
                final StringBuffer buffer = new StringBuffer();
                while (deps.hasNext()) {
                    Task dep = deps.next();
                    g.addEdge(t, dep, null);
                    buffer.append(dep.getLabel() + ", ");
                }

                final int len = buffer.length();
                if (len >= 2) {
                    buffer.setLength(len - 2);
                } else {
                    g.addNode(t);
                }
                m_dependencies.put(t, buffer.toString());
            }

            List tasks = new ArrayList();
            outer:
            while (g.nodeCount() > 0) {
                List l = Graphs.getSinkNodes(g);
                for (Iterator it = l.iterator(); it.hasNext();) {
                    Task t = (Task) it.next();
                    tasks.add(t);
                    g.removeNode(t);
                    continue outer;
                }
                // break loop if no nodes removed
                LOGGER.error("found possible loop in tasks for " + workflow);
                break;
            }

            m_tasks = tasks.iterator();
        }

        @Override
        public final int getColumnCount() {
            return 4;
        }

        @Override
        public final boolean nextRow() {
            if (m_tasks.hasNext()) {
                m_task = (Task) m_tasks.next();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public final Object getKeyAt(final int column) {
            return m_task.getTaskId();
        }

        @Override
        public final Object getElementAt(final int column) {
            switch (column) {
                case 0:
                    return m_task.getLabel();
                case 1:
                    return m_task.getDescription();
                case 2:
                    return m_dependencies.get(m_task);
                case 3:
                    return "";
//                    return m_task.getStateString();
                default:
                    throw new IllegalStateException();
            }
        }

    }

}
