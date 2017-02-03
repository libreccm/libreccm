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

import com.arsdigita.bebop.ActionLink;
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
 * Form for adding members (users or groups) to a role.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class RoleAddMemberForm extends Form {

    private static final String MEMBER_NAME = "membername";

    private static final int COL_MEMBER_NAME = 0;
    private static final int COL_MEMBER_TYPE = 1;
    private static final int COL_MEMBER_ADD = 2;

    private final TextField memberName;

    public RoleAddMemberForm(
        final RoleAdmin roleAdmin,
        final ParameterSingleSelectionModel<String> selectedRoleId) {

        super("roleAddMemberForm");

        final ActionLink backToRole = new ActionLink(new GlobalizedMessage(
            "ui.admin.role_members.add.back", ADMIN_BUNDLE));
        backToRole.addActionListener(e -> {
            roleAdmin.hideRoleMemberAddForm(e.getPageState());
        });
        add(backToRole);

        final Label heading = new Label();
        heading.setClassAttr("heading");
        heading.addPrintListener(e -> {
            final PageState state = e.getPageState();
            final Label target = (Label) e.getTarget();

            final RoleRepository roleRepository = CdiUtil.createCdiUtil()
                .findBean(RoleRepository.class);
            final Role role = roleRepository.findById(Long.parseLong(
                selectedRoleId.getSelectedKey(state))).get();

            target.setLabel(new GlobalizedMessage(
                "ui.admin.role_members.add.heading",
                ADMIN_BUNDLE,
                new String[]{role.getName()}));
        });
        add(heading);

        memberName = new TextField(MEMBER_NAME);
        memberName.setLabel(new GlobalizedMessage(
            "ui.admin.role_members.add.find", ADMIN_BUNDLE));
        add(memberName);

        final Submit submit = new Submit(new GlobalizedMessage(
            "ui.admin.role_members.add.search", ADMIN_BUNDLE));
        add(submit);

        add(new PartiesToAdd(roleAdmin, selectedRoleId));
    }

    private class PartiesToAdd extends Table {

        public PartiesToAdd(
            final RoleAdmin roleAdmin,
            final ParameterSingleSelectionModel<String> selectedRoleId) {

            super();

            setEmptyView(new Label(new GlobalizedMessage(
                "ui.admin.role_members.add.table.empty", ADMIN_BUNDLE)));

            final TableColumnModel columnModel = getColumnModel();
            columnModel.add(new TableColumn(
                COL_MEMBER_NAME,
                new Label(new GlobalizedMessage(
                    "ui.admin.role_members.add.table.name", ADMIN_BUNDLE))));
            columnModel.add(new TableColumn(
                COL_MEMBER_TYPE,
                new Label(new GlobalizedMessage(
                    "ui.admin.role_members.add.table.type", ADMIN_BUNDLE))));
            columnModel.add(new TableColumn(
                COL_MEMBER_ADD,
                new Label(new GlobalizedMessage(
                    "ui.admin.role_members.add.table.add", ADMIN_BUNDLE))));

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
                            final PartyRepository partyRepository = cdiUtil
                                .findBean(PartyRepository.class);
                            final RoleRepository roleRepository = cdiUtil
                                .findBean(RoleRepository.class);
                            final RoleManager roleManager = cdiUtil.findBean(
                                RoleManager.class);
                            final Party party = partyRepository.findById(
                                Long.parseLong(key)).get();
                            final Role role = roleRepository.findById(
                                Long.parseLong(
                                    selectedRoleId.getSelectedKey(state))).get();
                            roleManager.assignRoleToParty(role, party);
                            roleAdmin.hideRoleMemberAddForm(state);
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

            setModelBuilder(new PartiesToAddTableModelBuilder());
        }

    }

    private class PartiesToAddTableModelBuilder extends LockableImpl
        implements TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table,
                                    final PageState state) {
            return new PartiesToAddTableModel(state);
        }

    }

    private class PartiesToAddTableModel implements TableModel {

        private final List<Party> parties;
        private int index = -1;

        public PartiesToAddTableModel(final PageState state) {
            final String term = (String) memberName.getValue(state);
            if (term == null || term.isEmpty()) {
                parties = new ArrayList<>();
            } else {
                final PartyRepository partyRepository = CdiUtil.createCdiUtil()
                    .findBean(PartyRepository.class);
                parties = partyRepository.searchByName(term);
            }
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public boolean nextRow() {
            index++;
            return index < parties.size();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            final Party party = parties.get(index);
            switch (columnIndex) {
                case COL_MEMBER_NAME:
                    return party.getName();
                case COL_MEMBER_TYPE:
                    if (party instanceof User) {
                        return new Label(new GlobalizedMessage(
                            "ui.admin.role_members.type.user",
                            ADMIN_BUNDLE));
                    } else if (party instanceof Group) {
                        return new Label(new GlobalizedMessage(
                            "ui.admin.role_members.type.group",
                            ADMIN_BUNDLE));
                    } else {
                        return "?";
                    }
                case COL_MEMBER_ADD:
                    return new Label(new GlobalizedMessage(
                        "ui.admin.role_members.table.add"));
                default:
                    throw new IllegalArgumentException(
                        "Not a valid column index");
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return parties.get(index).getPartyId();
        }

    }

}
