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

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.table.TableCellRenderer;

import org.librecms.workflow.CmsTask;

import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.web.Web;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Shiro;
import org.libreccm.security.User;
import org.libreccm.workflow.UserTask;
import org.libreccm.workflow.UserTaskRepository;
import org.libreccm.workflow.WorkflowManager;
import org.librecms.CmsConstants;
import org.librecms.workflow.CmsTaskTypeRepository;

public final class AssignedTaskTable extends Table {

    public AssignedTaskTable(final WorkflowRequestLocal workflow) {

        super(new AssignedTaskTableModelBuilder(workflow),
              new String[]{lz("cms.ui.name"), "", ""});

        // XXX The string array and setHeader(null) are a product of
        // messed up Table behavior.
        setEmptyView(new Label(gz("cms.ui.workflow.task.assigned.none")));

        addTableActionListener(new LockListener());

        getColumn(1).setCellRenderer(new CellRenderer());
        getColumn(2).setCellRenderer(new DefaultTableCellRenderer(true));
    }

    private static class LockListener extends TableActionAdapter {

        @Override
        public final void cellSelected(final TableActionEvent event) {
            final int column = event.getColumn();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final UserTaskRepository userTaskRepo = cdiUtil.findBean(
                UserTaskRepository.class);
            final WorkflowManager workflowManager = cdiUtil.findBean(WorkflowManager.class);
            final Shiro shiro = cdiUtil.findBean(Shiro.class);

            if (column == 1) {
                final UserTask task = userTaskRepo.findById((Long) event
                    .getRowKey());
                final User currentUser = shiro.getUser();
                final User lockingUser = task.getLockingUser();
                if (task.isLocked()
                        && lockingUser != null
                        && lockingUser.equals(currentUser)) {
                    workflowManager.unlockTask(task);
                } else {
                    workflowManager.lockTask(task);
                }
            }
        }

    }

    private class CellRenderer implements TableCellRenderer {

        @Override
        public final Component getComponent(final Table table,
                                            final PageState state,
                                            final Object value,
                                            final boolean isSelected,
                                            final Object key, 
                                            final int row,
                                            final int column) {
            // SF patch [ 1587168 ] Show locking user
            BoxPanel p = new BoxPanel();
            User lockingUser = (User) value;
            if (lockingUser != null) {
                final StringBuilder sb = new StringBuilder("Locked by <br />");
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final Shiro shiro = cdiUtil.findBean(Shiro.class);
                if (lockingUser.equals(shiro.getUser())) {
                    sb.append("you");
                    p.add(new ControlLink(new Label(
                        gz("cms.ui.workflow.task.unlock"))));
                } else {
                    sb.append(lockingUser.getName());
                    p.add(new ControlLink(new Label(
                        gz("cms.ui.workflow.task.takeover"))));
                }
                p.add(new Label(sb.toString(), false));
            } else {
                p.add(new ControlLink(
                    new Label(gz("cms.ui.workflow.task.lock"))));
            }
            return p;
        }

    }

    protected final static GlobalizedMessage gz(final String key) {
        return new GlobalizedMessage(key, CmsConstants.CMS_BUNDLE);
    }

    protected final static String lz(final String key) {
        return (String) gz(key).localize();
    }

}
