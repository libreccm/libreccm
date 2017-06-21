/*
 * Copyright (C) 2016 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.ui.admin.usersgroupsroles.users;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 * Model for the {@link GroupsRolesTable}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class GroupsRolesTableModel implements TableModel {

    protected static final int COL_LABEL = 0;
    protected static final int COL_VALUE = 1;
    protected static final int COL_ACTION = 2;

    protected static final int ROW_GROUPS = 0;
    protected static final int ROW_ROLES = 1;
    protected static final int ROW_ALL_ROLES = 2;

    private int row = -1;

    private final User user;

    public GroupsRolesTableModel(final User user) {
        this.user = user;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public boolean nextRow() {
        row++;
        return row < 3;
    }

    @Override
    public Object getElementAt(final int columnIndex) {
        switch (row) {
            case ROW_GROUPS:
                return buildGroupRow(columnIndex);
            case ROW_ROLES:
                return buildRolesRow(columnIndex);
            case ROW_ALL_ROLES:
                return buildAllRolesRow(columnIndex);
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public Object getKeyAt(final int columnIndex) {
        return row;
    }

    private Object buildGroupRow(final int columnIndex) {
        switch (columnIndex) {
            case COL_LABEL:
                return new Label(new GlobalizedMessage("ui.admin.user.groups",
                                                       ADMIN_BUNDLE));
            case COL_VALUE: {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final UsersGroupsRolesController controller = cdiUtil
                    .findBean(UsersGroupsRolesController.class);

                return controller.getNamesOfAssignedGroups(user);
            }
            case COL_ACTION:
                return new Label(new GlobalizedMessage(
                    "ui.admin.user.groups.edit", ADMIN_BUNDLE));
            default:
                throw new IllegalArgumentException();
        }
    }

    private Object buildRolesRow(final int columnIndex) {
        switch (columnIndex) {
            case COL_LABEL:
                return new Label(new GlobalizedMessage("ui.admin.user.roles",
                                                       ADMIN_BUNDLE));
            case COL_VALUE: {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final UsersGroupsRolesController controller = cdiUtil
                    .findBean(UsersGroupsRolesController.class);

                return controller.getNamesOfAssignedGroups(user);
            }
            case COL_ACTION:
                return new Label(new GlobalizedMessage(
                    "ui.admin.user.roles.edit", ADMIN_BUNDLE));
            default:
                throw new IllegalArgumentException();
        }
    }

    private Object buildAllRolesRow(final int columnIndex) {
        switch (columnIndex) {
            case COL_LABEL:
                return new Label(new GlobalizedMessage(
                    "ui.admin.user.all_roles", ADMIN_BUNDLE));
            case COL_VALUE: {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final UsersGroupsRolesController controller = cdiUtil
                    .findBean(UsersGroupsRolesController.class);

                return controller.getNamesOfAllAssignedRoles(user);
            }
            case COL_ACTION:
                return "";
            default:
                throw new IllegalArgumentException();
        }
    }

}
