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

import org.libreccm.admin.ui.UsersGroupsRoles;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Group;
import org.libreccm.security.Party;
import org.libreccm.security.PartyRepository;
import org.libreccm.security.Role;
import org.libreccm.security.RoleManager;
import org.libreccm.security.RoleRepository;
import org.libreccm.security.User;

import java.util.ArrayList;
import java.util.List;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 * Table showing all members (users or groups) of a role.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class RoleMembersTable extends Table {

    private static final int COL_MEMBER_NAME = 0;
    private static final int COL_MEMBER_TYPE = 1;
    private static final int COL_MEMBER_REMOVE = 2;

    public RoleMembersTable(
        final ParameterSingleSelectionModel<String> selectedRoleId) {

        super();
        setIdAttr("roleMembersTable");

        setEmptyView(new Label(new GlobalizedMessage(
            "ui.admin.role_members.none", ADMIN_BUNDLE)));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
            COL_MEMBER_NAME,
            new Label(new GlobalizedMessage("ui.admin.role_members.name",
                                            ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_MEMBER_TYPE,
            new Label(new GlobalizedMessage("ui.admin.role_members.type",
                                            ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_MEMBER_REMOVE,
            new Label(new GlobalizedMessage("ui.admin.role_members.remove",
                                            ADMIN_BUNDLE))));

        columnModel.get(COL_MEMBER_REMOVE).setCellRenderer(
            new TableCellRenderer() {

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
                    "ui.admin.role_members.remove_member.confirm",
                    ADMIN_BUNDLE));
                return link;
            }

        });

        addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event) {
                final PageState state = event.getPageState();
                final String key = (String) event.getRowKey();

                switch (event.getColumn()) {
                    case COL_MEMBER_REMOVE:
                        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                        final PartyRepository partyRepository = cdiUtil
                            .findBean(PartyRepository.class);
                        final RoleRepository roleRepository = cdiUtil.findBean(
                            RoleRepository.class);
                        final RoleManager roleManager = cdiUtil.findBean(
                            RoleManager.class);
                        final Party party = partyRepository.findById(Long
                            .parseLong(key)).get();
                        final Role role = roleRepository.findById(
                            Long.parseLong(selectedRoleId.getSelectedKey(state)))
                            .get();
                        roleManager.removeRoleFromParty(role, party);
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

        setModelBuilder(new RoleMembersTableModelBuilder(selectedRoleId));
    }

    private class RoleMembersTableModelBuilder extends LockableImpl
        implements TableModelBuilder {

        private final ParameterSingleSelectionModel<String> selectedRoleId;

        public RoleMembersTableModelBuilder(
            final ParameterSingleSelectionModel<String> selectedRoleId) {

            this.selectedRoleId = selectedRoleId;
        }

        @Override
        public TableModel makeModel(final Table table,
                                    final PageState state) {
            table.getRowSelectionModel().clearSelection(state);

            return new RoleMembersTableModel(selectedRoleId, state);
        }

    }

    private class RoleMembersTableModel implements TableModel {

        private final List<Party> members;
        private int index = -1;

        public RoleMembersTableModel(
            final ParameterSingleSelectionModel<String> selectedRoleId,
            final PageState state) {

             final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final RoleRepository roleRepository = cdiUtil
                .findBean(RoleRepository.class);
            final Role role = roleRepository.findById(Long.parseLong(
                selectedRoleId.getSelectedKey(state))).get();
//
//            members = new ArrayList<>();
//
//            role.getMemberships().forEach(m -> {
//                members.add(m.getMember());
//            });
//
//            members.sort((m1, m2) -> {
//                return m1.getName().compareTo(m2.getName());
//            });

            final RolesController controller = cdiUtil
                .findBean(RolesController.class);
            members = controller.getMembersOfRole(role);
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public boolean nextRow() {
            index++;
            return index < members.size();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            final Party member = members.get(index);
            switch (columnIndex) {
                case COL_MEMBER_NAME:
                    return member.getName();
                case COL_MEMBER_TYPE:
                    if (member instanceof User) {
                        return new Label(new GlobalizedMessage(
                            "ui.admin.role_members.type.user",
                            ADMIN_BUNDLE));
                    } else if (member instanceof Group) {
                        return new Label(new GlobalizedMessage(
                            "ui.admin.role_members.type.group",
                            ADMIN_BUNDLE));
                    } else {
                        return "?";
                    }
                case COL_MEMBER_REMOVE:
                    return new Label(new GlobalizedMessage(
                        "ui.admin.role_members.remove", ADMIN_BUNDLE));
                default:
                    throw new IllegalArgumentException(
                        "Not a valid column index");
            }

        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return members.get(index).getPartyId();
        }

    }

}
