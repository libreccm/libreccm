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
package com.arsdigita.ui.admin.usersgroupsroles.roles;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;

import java.util.List;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 * Filterable table showing all roles.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class RolesTable extends Table {

    private static final int COL_ROLE_NAME = 0;
    private static final int COL_DELETE = 1;

    private final TextField rolesTableFilter;
    private final ParameterSingleSelectionModel<String> selectedRoleId;

    public RolesTable(
        final RoleAdmin parent,
        final TextField rolesTableFilter,
        final ParameterSingleSelectionModel<String> selectedRoleId) {

        super();

        setIdAttr("rolesTable");
        setStyleAttr("width: 30em");

        this.rolesTableFilter = rolesTableFilter;
        this.selectedRoleId = selectedRoleId;

        setEmptyView(new Label(new GlobalizedMessage(
            "ui.admin.roles.table.empty", ADMIN_BUNDLE)));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
            COL_ROLE_NAME,
            new Label(new GlobalizedMessage("ui.admin.roles.table.name",
                                            ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_DELETE,
            new Label(new GlobalizedMessage("ui.admin.roles.table.delete",
                                            ADMIN_BUNDLE))));

        columnModel.get(COL_ROLE_NAME).setCellRenderer(new TableCellRenderer() {

            @Override
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row,
                                          final int column) {
                return new ControlLink((String) value);
            }

        });

        columnModel.get(COL_DELETE).setCellRenderer(new TableCellRenderer() {

            @Override
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row,
                                          final int column) {
                final ControlLink link = new ControlLink((Component) value);
                link.setConfirmation(new GlobalizedMessage(
                    "ui.admin.roles.delete.confirm", ADMIN_BUNDLE));
                return link;
            }

        });

        addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event) {
                final PageState state = event.getPageState();
                final String key = (String) event.getRowKey();

                switch (event.getColumn()) {
                    case COL_ROLE_NAME:
                        selectedRoleId.setSelectedKey(state, key);
                        parent.showRoleDetails(state);
                        break;
                    case COL_DELETE:
                        final RoleRepository roleRepository = CdiUtil
                            .createCdiUtil().findBean(RoleRepository.class);
                        final Role role = roleRepository.findById(Long
                            .parseLong(key));
                        roleRepository.delete(role);
                        break;
                    default:
                        throw new IllegalArgumentException(
                            "Invalid value for column.");
                }
            }

            @Override
            public void headSelected(final TableActionEvent event) {
                //Nothing
            }

        });

        setModelBuilder(new RolesTableModelBuilder());
    }

    private class RolesTableModelBuilder extends LockableImpl
        implements TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table,
                                    final PageState state) {
            table.getRowSelectionModel().clearSelection(state);

            return new RolesTableModel(state);
        }

    }

    private class RolesTableModel implements TableModel {

        private final List<Role> roles;
        private int index = -1;

        public RolesTableModel(final PageState state) {
            final String filterTerm = (String) rolesTableFilter.getValue(state);
            final RoleRepository roleRepository = CdiUtil.createCdiUtil()
                .findBean(RoleRepository.class);
            if (filterTerm == null || filterTerm.isEmpty()) {
                roles = roleRepository.findAllOrderedByRoleName();
            } else {
                roles = roleRepository.searchByName(filterTerm);
            }
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public boolean nextRow() {
            index++;
            return index < roles.size();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            final Role role = roles.get(index);
            switch (columnIndex) {
                case COL_ROLE_NAME:
                    return role.getName();
                case COL_DELETE:
                    return new Label(new GlobalizedMessage(
                        "ui.admin.roles.table.delete", ADMIN_BUNDLE));
                default:
                    throw new IllegalArgumentException(
                        "Not a valid column index");
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return roles.get(index).getRoleId();
        }

    }

}
