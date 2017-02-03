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
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.libreccm.security.Permission;
import org.libreccm.security.PermissionManager;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;

import java.util.ArrayList;
import java.util.List;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 * Table displaying all permissions granted to a role.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class RolePermissionsTable extends Table {

    private static final int COL_PRIVILEGE = 0;
    private static final int COL_ON_OBJECT = 1;
    private static final int COL_REVOKE = 2;

    public RolePermissionsTable(
        final ParameterSingleSelectionModel<String> selectedRoleId) {

        super();
        setIdAttr("rolePermissionsTable");

        setEmptyView(new Label(new GlobalizedMessage(
            "ui.admin.role_permissions.none", ADMIN_BUNDLE)));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
            COL_PRIVILEGE,
            new Label(new GlobalizedMessage(
                "ui.admin.role_permissions.privilege", ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_ON_OBJECT,
            new Label(new GlobalizedMessage(
                "ui.admin.role_permissions.on_object", ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_REVOKE,
            new Label(new GlobalizedMessage(
                "ui.admin.role_permissions.revoke", ADMIN_BUNDLE))));

        columnModel.get(COL_REVOKE).setCellRenderer(new TableCellRenderer() {

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
                    "ui.admin.role_permissions.revoke.confirm", ADMIN_BUNDLE));
                return link;
            }

        });

        addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event) {
                final PageState state = event.getPageState();
                final String key = (String) event.getRowKey();

                switch (event.getColumn()) {
                    case COL_REVOKE:
                        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                        final RoleRepository roleRepository = cdiUtil.findBean(
                            RoleRepository.class);
                        final PermissionManager permissionManager = cdiUtil
                            .findBean(PermissionManager.class);
                        final Role role = roleRepository.findById(
                            Long.parseLong(selectedRoleId.getSelectedKey(state))).get();
                        final Permission permission = permissionManager
                            .findById(Long.parseLong(key));
                        if (permission.getObject() == null) {
                            permissionManager.revokePrivilege(
                                permission.getGrantedPrivilege(), role);
                        } else {
                            permissionManager.revokePrivilege(
                                permission.getGrantedPrivilege(),
                                role,
                                permission.getObject());
                        }
                        break;
                    default:
                        throw new IllegalArgumentException(
                            "Invalid value for column");
                }
            }

            @Override
            public void headSelected(final TableActionEvent event) {
                //Nothing
            }

        });

        setModelBuilder(new RolePermissionsTableModelBuilder(selectedRoleId));
    }

    private class RolePermissionsTableModelBuilder extends LockableImpl
        implements TableModelBuilder {

        private final ParameterSingleSelectionModel<String> selectedRoleId;

        public RolePermissionsTableModelBuilder(
            final ParameterSingleSelectionModel<String> selectedRoleId) {

            this.selectedRoleId = selectedRoleId;
        }

        @Override
        public TableModel makeModel(final Table table,
                                    final PageState state) {
            table.getRowSelectionModel().clearSelection(state);

            return new RolePermissionsTableModel(selectedRoleId, state);
        }

    }

    private class RolePermissionsTableModel implements TableModel {

        private final List<Permission> permissions;
        private int index = -1;

        public RolePermissionsTableModel(
            final ParameterSingleSelectionModel<String> selectedRoleId,
            final PageState state) {

            final RoleRepository roleRepository = CdiUtil.createCdiUtil()
                .findBean(RoleRepository.class);
            final Role role = roleRepository.findById(
                Long.parseLong(selectedRoleId.getSelectedKey(state)),
                Role.ENTITY_GRPAH_WITH_PERMISSIONS).get();

            permissions = new ArrayList<>(role.getPermissions());

            permissions.sort((p1, p2) -> {
                final int result = p1.getGrantedPrivilege()
                    .compareTo(p2.getGrantedPrivilege());
                if (result != 0) {
                    return result;
                } else if (p1.getObject() != null
                               && p1.getObject().getDisplayName() != null
                               && p2.getObject() != null) {
                    return p1.getObject().getDisplayName()
                        .compareTo(p2.getObject().getDisplayName());
                } else {
                    return result;
                }
            });
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public boolean nextRow() {
            index++;
            return index < permissions.size();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            final Permission permission = permissions.get(index);
            switch (columnIndex) {
                case COL_PRIVILEGE:
                    return permission.getGrantedPrivilege();
                case COL_ON_OBJECT:
                    if (permission.getObject() == null) {
                        return "";
                    } else {
                        final CcmObject object = permission.getObject();
                        return String.join(" ",
                                           Long.toString(object.getObjectId()),
                                           object.getDisplayName());
                    }
                case COL_REVOKE:
                    return new Label(new GlobalizedMessage(
                        "ui.admin.role_permissions.revoke", ADMIN_BUNDLE));
                default:
                    throw new IllegalArgumentException(
                        "Not a valid column index");
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return permissions.get(index).getPermissionId();
        }

    }

}
