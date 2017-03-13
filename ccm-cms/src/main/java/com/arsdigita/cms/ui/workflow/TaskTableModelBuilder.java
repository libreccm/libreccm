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
import com.arsdigita.kernel.KernelConfig;

import org.apache.logging.log4j.LogManager;
import org.libreccm.workflow.Task;
import org.libreccm.workflow.Workflow;

import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;

import java.util.Locale;

class TaskTableModelBuilder extends AbstractTableModelBuilder {

    private static final Logger LOGGER = LogManager.getLogger(
        TaskTableModelBuilder.class);

    private final WorkflowRequestLocal workflow;

    TaskTableModelBuilder(final WorkflowRequestLocal workflow) {
        this.workflow = workflow;
    }

    @Override
    public final TableModel makeModel(final Table table,
                                      final PageState state) {
        LOGGER.debug("Creating a new table model for the current request");

        return new Model(workflow.getWorkflow(state));
    }

    private static class Model implements TableModel {

        private Task currentTask;
        private Iterator<Task> tasksIterator;
        private Map<Task, String> dependencies;

        private Model(final Workflow workflow) {

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final WorkflowAdminPaneController controller = cdiUtil.findBean(
                WorkflowAdminPaneController.class);

            final TaskTableModelData data = controller
                .getTaskTableModelData(workflow);
            tasksIterator = data.getTasks();
            dependencies = data.getDependencies();
            

//            final Iterator<Task> tasksIter = controller
//                .getTasksForWorkflow(workflow)
//                .iterator();
//            GraphSet graphSet = new GraphSet();
//
//            while (tasksIter.hasNext()) {
//                Task task = tasksIter.next();
//                final Iterator<Task> deps = task.getDependsOn().iterator();
//                final StringBuffer buffer = new StringBuffer();
//                while (deps.hasNext()) {
//                    Task dep = deps.next();
//                    graphSet.addEdge(task, dep, null);
//                    buffer
//                        .append(dep.getLabel())
//                        .append(", ");
//                }
//
//                final int len = buffer.length();
//                if (len >= 2) {
//                    buffer.setLength(len - 2);
//                } else {
//                    graphSet.addNode(task);
//                }
//                m_dependencies.put(task, buffer.toString());
//            }
//
//            List tasks = new ArrayList();
//            outer:
//            while (graphSet.nodeCount() > 0) {
//                List list = Graphs.getSinkNodes(graphSet);
//                for (Iterator it = list.iterator(); it.hasNext();) {
//                    Task t = (Task) it.next();
//                    tasks.add(t);
//                    graphSet.removeNode(t);
//                    continue outer;
//                }
//                // break loop if no nodes removed
//                LOGGER.error("found possible loop in tasks for " + workflow);
//                break;
//            }
//
//            m_tasks = tasks.iterator();
        }

        @Override
        public final int getColumnCount() {
            return 4;
        }

        @Override
        public final boolean nextRow() {
            if (tasksIterator.hasNext()) {
                currentTask = tasksIterator.next();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public final Object getKeyAt(final int column) {
            return currentTask.getTaskId();
        }

        @Override
        public final Object getElementAt(final int column) {
            final Locale defaultLocale = KernelConfig.getConfig().getDefaultLocale();
            
            switch (column) {
                case 0:
                    return currentTask.getLabel().getValue(defaultLocale);
                case 1:
                    return currentTask.getDescription().getValue(defaultLocale);
                case 2:
                    return dependencies.get(currentTask);
                case 3:
                    return "";
//                    return m_task.getStateString();
                default:
                    throw new IllegalStateException();
            }
        }

    }

}
