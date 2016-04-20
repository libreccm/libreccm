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

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
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
 * Form for adding a new member to group. The form contains a form for searching
 * members (uses the user name, the family name, the given name and the primary 
 * email address). After a search term was send, a table with all matching users
 * is displayed. The table also contains an action link for adding a user to the
 * group which is currently selected..
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class GroupAddMemberForm extends Form {

    private static final String MEMBER_NAME = "membername";

    private static final int COL_MEMBER_NAME = 0;
    private static final int COL_MEMBER_FAMILY_NAME = 1;
    private static final int COL_MEMBER_GIVEN_NAME = 2;
    private static final int COL_MEMBER_EMAIL = 3;
    private static final int COL_MEMBER_ADD = 4;

    private final TextField memberName;

    public GroupAddMemberForm(
        final GroupAdmin groupAdmin,
        final ParameterSingleSelectionModel<String> selectedGroupId) {

        super("groupAddMemberForm");

        final BoxPanel links = new BoxPanel(BoxPanel.VERTICAL);
        final ActionLink backToGroup = new ActionLink(new GlobalizedMessage(
            "ui.admin.group_details.add_member.back", ADMIN_BUNDLE));
        backToGroup.addActionListener(e -> {
            groupAdmin.hideGroupMemberAddForm(e.getPageState());
        });
        links.add(backToGroup);

        final Label heading = new Label();
        heading.setClassAttr("heading");
        heading.addPrintListener(e -> {
            final PageState state = e.getPageState();
            final Label target = (Label) e.getTarget();

            final GroupRepository groupRepository = CdiUtil.createCdiUtil()
                .findBean(GroupRepository.class);
            final Group group = groupRepository.findById(Long.parseLong(
                selectedGroupId.getSelectedKey(state)));

            target.setLabel(new GlobalizedMessage(
                "ui.admin.group_details.add_member.header",
                ADMIN_BUNDLE,
                new String[]{group.getName()}));

        });
        links.add(heading);
        
        add(links);

        memberName = new TextField(MEMBER_NAME);
        memberName.setLabel(new GlobalizedMessage(
            "ui.admin.group_details.add_member.find", ADMIN_BUNDLE));
        add(memberName);

        final Submit submit = new Submit(new GlobalizedMessage(
            "ui.admin.group_details.add_member.search", ADMIN_BUNDLE));
        add(submit);

        add(new UsersToAddTable(groupAdmin, selectedGroupId));
    }

    private class UsersToAddTable extends Table {

        public UsersToAddTable(
            final GroupAdmin groupAdmin,
            final ParameterSingleSelectionModel<String> selectedGroupId) {

            super();

            setEmptyView(new Label(new GlobalizedMessage(
                "ui.admin.group_details.add_member.table.empty", ADMIN_BUNDLE)));

            final TableColumnModel columnModel = getColumnModel();
            columnModel.add(new TableColumn(
                COL_MEMBER_NAME,
                new Label(new GlobalizedMessage(
                    "ui.admin.group_details.add_member.table.name",
                    ADMIN_BUNDLE))));
            columnModel.add(new TableColumn(
                COL_MEMBER_FAMILY_NAME,
                new Label(new GlobalizedMessage(
                    "ui.admin.group_details.add_member.table.family_name",
                    ADMIN_BUNDLE))));
            columnModel.add(new TableColumn(
                COL_MEMBER_GIVEN_NAME,
                new Label(new GlobalizedMessage(
                    "ui.admin.group_details.add_member.table.given_name",
                    ADMIN_BUNDLE))));
            columnModel.add(new TableColumn(
                COL_MEMBER_EMAIL,
                new Label(new GlobalizedMessage(
                    "ui.admin.group_details.add_member.table.email",
                    ADMIN_BUNDLE))));
            columnModel.add(new TableColumn(
                COL_MEMBER_ADD,
                new Label(new GlobalizedMessage(
                    "ui.admin.group_details.add_member.table.add",
                    ADMIN_BUNDLE))));

            columnModel.get(COL_MEMBER_ADD).setCellRenderer(
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
                    return link;
                }

            });

            addTableActionListener(new TableActionListener() {

                @Override
                public void cellSelected(final TableActionEvent event) {
                    final PageState state = event.getPageState();
                    final String key = (String) event.getRowKey();

                    switch (event.getColumn()) {
                        case COL_MEMBER_ADD:
                            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                            final UserRepository userRepository = cdiUtil
                                .findBean(UserRepository.class);
                            final GroupRepository groupRepository = cdiUtil
                                .findBean(GroupRepository.class);
                            final GroupManager groupManager = cdiUtil.findBean(
                                GroupManager.class);
                            final User user = userRepository.findById(Long
                                .parseLong(key));
                            final Group group = groupRepository.findById(
                                Long.parseLong(
                                    selectedGroupId.getSelectedKey(state)));
                            groupManager.addMemberToGroup(user, group);
                            groupAdmin.hideGroupMemberAddForm(state);
                            break;
                        default:
                            throw new IllegalArgumentException(
                                "Invalid value for column");
                    }

                }

                @Override
                public void headSelected(final TableActionEvent event) {
                    // Nothing
                }

            });

            setModelBuilder(new UsersToAddTableModelBuilder());
        }

    }

    private class UsersToAddTableModelBuilder extends LockableImpl
        implements TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table,
                                    final PageState state) {
            return new UsersToAddTableModel(state);
        }

    }

    private class UsersToAddTableModel implements TableModel {

        private final List<User> users;
        private int index = -1;

        public UsersToAddTableModel(final PageState state) {
            final String term = (String) memberName.getValue(state);
            if (term == null || term.isEmpty()) {
                users = new ArrayList<>();
            } else {
                final UserRepository userRepository = CdiUtil.createCdiUtil()
                    .findBean(UserRepository.class);
                users = userRepository.filtered(term);
            }
        }

        @Override
        public int getColumnCount() {
            return 5;
        }

        @Override
        public boolean nextRow() {
            index++;
            return index < users.size();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            final User user = users.get(index);
            switch (columnIndex) {
                case COL_MEMBER_NAME:
                    return user.getName();
                case COL_MEMBER_FAMILY_NAME:
                    return user.getFamilyName();
                case COL_MEMBER_GIVEN_NAME:
                    return user.getGivenName();
                case COL_MEMBER_EMAIL:
                    return user.getPrimaryEmailAddress().getAddress();
                case COL_MEMBER_ADD:
                    return new Label(new GlobalizedMessage(
                        "ui.admin.group_details.add_member.table.add",
                        ADMIN_BUNDLE));
                default:
                    throw new IllegalArgumentException(
                        "Not a valid column index");
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return users.get(index).getPartyId();
        }

    }

}
