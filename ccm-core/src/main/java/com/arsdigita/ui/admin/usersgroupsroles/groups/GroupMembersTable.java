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
package com.arsdigita.ui.admin.usersgroupsroles.groups;

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
import org.libreccm.security.Group;
import org.libreccm.security.GroupManager;
import org.libreccm.security.GroupRepository;
import org.libreccm.security.User;
import org.libreccm.security.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 * Table which all members of a group.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class GroupMembersTable extends Table {

    private static final int COL_MEMBER_NAME = 0;
    private static final int COL_MEMBER_FAMILY_NAME = 1;
    private static final int COL_MEMBER_GIVEN_NAME = 2;
    private static final int COL_MEMBER_EMAIL = 3;
    private static final int COL_MEMBER_REMOVE = 4;

    public GroupMembersTable(
        final ParameterSingleSelectionModel<String> selectedGroupId) {

        super();
        setIdAttr("groupMembersTable");

        setEmptyView(new Label(new GlobalizedMessage(
            "ui.admin.group_details.members.none", ADMIN_BUNDLE)));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
            COL_MEMBER_NAME,
            new Label(new GlobalizedMessage(
                "ui.admin.group_details.members_table.cols.name",
                ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_MEMBER_FAMILY_NAME,
            new Label(new GlobalizedMessage(
                "ui.admin.group_details.members_table.cols.family_name",
                ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_MEMBER_GIVEN_NAME,
            new Label(new GlobalizedMessage(
                "ui.admin.group_details.members_table.cols.given_name",
                ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_MEMBER_EMAIL,
            new Label(new GlobalizedMessage(
                "ui.admin.group_details.members_table.cols.email",
                ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_MEMBER_REMOVE,
            new Label(new GlobalizedMessage(
                "ui.admin.group_details.members_table.cols.remove",
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
                    "ui.admin.group_details.members_table.member.remove",
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
                        final UserRepository userRepository = cdiUtil
                            .findBean(UserRepository.class);
                        final GroupRepository groupRepository = cdiUtil
                            .findBean(GroupRepository.class);
                        final GroupManager groupManager = cdiUtil.findBean(
                            GroupManager.class);
                        final User user = userRepository.findById(Long
                            .parseLong(key)).get();
                        final Group group = groupRepository.findById(
                            Long.parseLong(
                                selectedGroupId.getSelectedKey(state))).get();
                        groupManager.removeMemberFromGroup(user, group);
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

        setModelBuilder(new GroupMembersTableModelBuilder(selectedGroupId));
    }

    private class GroupMembersTableModelBuilder extends LockableImpl
        implements TableModelBuilder {

        private final ParameterSingleSelectionModel<String> selectedGroupId;

        public GroupMembersTableModelBuilder(
            final ParameterSingleSelectionModel<String> selectedGroupId) {

            this.selectedGroupId = selectedGroupId;
        }

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);

            return new GroupMembersTableModel(selectedGroupId, state);
        }

    }

    private class GroupMembersTableModel implements TableModel {

        private final List<User> members;
        private int index = -1;

        public GroupMembersTableModel(
            final ParameterSingleSelectionModel<String> selectedGroupId,
            final PageState state) {

            final GroupRepository groupRepository = CdiUtil.createCdiUtil()
                .findBean(GroupRepository.class);
            final Group group = groupRepository.findById(Long.parseLong(
                selectedGroupId.getSelectedKey(state))).get();

            members = new ArrayList<>();

            group.getMemberships().forEach(m -> {
                members.add(m.getMember());
            });

            members.sort((m1, m2) -> {
                return m1.getName().compareTo(m2.getName());
            });
        }

        @Override
        public int getColumnCount() {
            return 5;
        }

        @Override
        public boolean nextRow() {
            index++;
            return index < members.size();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            final User member = members.get(index);
            switch (columnIndex) {
                case COL_MEMBER_NAME:
                    return member.getName();
                case COL_MEMBER_FAMILY_NAME:
                    return member.getFamilyName();
                case COL_MEMBER_GIVEN_NAME:
                    return member.getGivenName();
                case COL_MEMBER_EMAIL:
                    return member.getPrimaryEmailAddress().getAddress();
                case COL_MEMBER_REMOVE:
                    return new Label(new GlobalizedMessage(
                        "ui.admin.group_details.members_table.remove",
                        ADMIN_BUNDLE));
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
