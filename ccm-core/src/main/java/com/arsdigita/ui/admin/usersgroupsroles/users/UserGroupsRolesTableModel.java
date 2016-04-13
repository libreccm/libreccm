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

import org.libreccm.security.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class UserGroupsRolesTableModel implements TableModel {

    protected static final int COL_LABEL = 0;
    protected static final int COL_VALUE = 1;
    protected static final int COL_ACTION = 2;

    protected static final int ROW_GROUPS = 0;
    protected static final int ROW_ROLES = 1;
    protected static final int ROW_ALL_ROLES = 2;

    private int row = -1;

    private final User user;

    public UserGroupsRolesTableModel(final User user) {
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
            case COL_VALUE:
                final List<String> groupNames = new ArrayList<>();
                user.getGroupMemberships().forEach(m -> {
                    groupNames.add(m.getGroup().getName());
                });

                groupNames.sort((name1, name2) -> {
                    return name1.compareTo(name2);
                });

                return String.join(
                    ", ", groupNames.toArray(new String[groupNames.size()]));

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
            case COL_VALUE:
                final List<String> roleNames = new ArrayList<>();
                user.getRoleMemberships().forEach(m -> {
                    roleNames.add(m.getRole().getName());
                });

                roleNames.sort((name1, name2) -> {
                    return name1.compareTo(name2);
                });

                return String.join(
                    ", ", roleNames.toArray(new String[roleNames.size()]));

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
            case COL_VALUE:
                final Set<String> roleNames = new HashSet<>();
                user.getRoleMemberships().forEach(m -> {
                    roleNames.add(m.getRole().getName());
                });

                user.getGroupMemberships().forEach(m -> {
                    m.getGroup().getRoleMemberships().forEach(r -> {
                        roleNames.add(r.getRole().getName());
                    });
                });

                final List<String> allRoleNames = new ArrayList<>(roleNames);
                allRoleNames.sort((name1, name2) -> {
                    return name1.compareTo(name2);
                });

                return String.join(", ", allRoleNames.toArray(
                                   new String[allRoleNames.size()]));

            case COL_ACTION:
                return "";
            default:
                throw new IllegalArgumentException();
        }
    }

}
