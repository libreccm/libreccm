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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class GroupsRolesTable extends Table {

    public GroupsRolesTable(
        final UserAdmin userAdmin,
        final ParameterSingleSelectionModel<String> selectedUserId) {

        setModelBuilder(new GroupsRolesTableModelBuilder(
            selectedUserId));
        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
            GroupsRolesTableModel.COL_LABEL));
        columnModel
            .add(new TableColumn(GroupsRolesTableModel.COL_VALUE));
        columnModel.add(
            new TableColumn(GroupsRolesTableModel.COL_ACTION));
        columnModel.get(GroupsRolesTableModel.COL_ACTION)
            .setCellRenderer(new TableCellRenderer() {

                @Override
                public Component getComponent(final Table table,
                                              final PageState state,
                                              final Object value,
                                              final boolean isSelected,
                                              final Object key,
                                              final int row,
                                              final int column) {
                    switch (row) {
                        case GroupsRolesTableModel.ROW_GROUPS: {
                            return new ControlLink((Component) value);
                        }
                        case GroupsRolesTableModel.ROW_ROLES: {
                            return new ControlLink((Component) value);
                        }
                        case GroupsRolesTableModel.ROW_ALL_ROLES:
                            return new Text("");
                        default:
                            throw new IllegalArgumentException();
                    }
                }

            });
        addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event) {
                final int selectedRow = Integer.parseInt((String) event
                    .getRowKey());
                final PageState state = event.getPageState();

                switch (selectedRow) {
                    case GroupsRolesTableModel.ROW_GROUPS:
                        userAdmin.showEditGroupMembershipsForm(state);
                        break;
                    case GroupsRolesTableModel.ROW_ROLES:
                        userAdmin.showEditRoleMembershipsForm(state);
                        break;
                }
            }

            @Override
            public void headSelected(final TableActionEvent event) {
                //Nothing
            }

        });
    }

}
