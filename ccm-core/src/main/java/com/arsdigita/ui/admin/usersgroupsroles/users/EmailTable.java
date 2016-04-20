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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.EmailAddress;
import org.libreccm.security.User;
import org.libreccm.security.UserRepository;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class EmailTable extends Table {
    
    public EmailTable(
        final UserAdmin userAdmin,
        final ParameterSingleSelectionModel<String> selectedUserId,
        final ParameterSingleSelectionModel<String> selectedEmailAddress) {
        
        setModelBuilder(
            new EmailTableModelBuilder(selectedUserId));
        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
            EmailTableModel.COL_ADDRESS,
            new Label(new GlobalizedMessage(
                "ui.admin.user.email_addresses.address",
                ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            EmailTableModel.COL_VERIFIED,
            new Label(new GlobalizedMessage(
                "ui.admin.user.email_addresses.verified",
                ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            EmailTableModel.COL_BOUNCING,
            new Label(new GlobalizedMessage(
                "ui.admin.user.email_addresses.bouncing",
                ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            EmailTableModel.COL_EDIT,
            new Label(new GlobalizedMessage(
                "ui.admin.user.email_addresses.edit",
                ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            EmailTableModel.COL_DELETE,
            new Label(new GlobalizedMessage(
                "ui.admin.user.email_addresses.delete",
                ADMIN_BUNDLE))));
        columnModel.get(EmailTableModel.COL_EDIT).setCellRenderer(
            new TableCellRenderer() {

            @Override
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row,
                                          final int column) {
                return new ControlLink((Component) value);
            }

        });
        columnModel.get(EmailTableModel.COL_DELETE)
            .setCellRenderer(
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
                    if (column == EmailTableModel.COL_DELETE) {
                        link.setConfirmation(new GlobalizedMessage(
                            "ui.admin.user.email_addresses.delete.confirm",
                            ADMIN_BUNDLE));
                    }
                    return link;
                }

            });
        addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event) {
                final PageState state = event.getPageState();

                final String key = (String) event.getRowKey();

                switch (event.getColumn()) {
                    case EmailTableModel.COL_EDIT:
                        selectedEmailAddress.setSelectedKey(state, key);
                        userAdmin.showEmailForm(state);
                        break;
                    case EmailTableModel.COL_DELETE:
                        final String userIdStr = selectedUserId.getSelectedKey(
                            state);
                        final UserRepository userRepository = CdiUtil
                            .createCdiUtil().findBean(UserRepository.class);
                        final User user = userRepository.findById(Long
                            .parseLong(userIdStr));
                        EmailAddress email = null;
                        for (EmailAddress current : user.getEmailAddresses()) {
                            if (current.getAddress().equals(key)) {
                                email = current;
                                break;
                            }
                        }

                        if (email != null) {
                            user.removeEmailAddress(email);
                            userRepository.save(user);
                        }
                }
            }

            @Override
            public void headSelected(final TableActionEvent event) {
                //Nothing
            }

        });
        setEmptyView(new Label(new GlobalizedMessage(
            "ui.admin.user.email_addresses.none", ADMIN_BUNDLE)));

        
    }
    
}
