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

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 * Table showing the primary email of an user.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class PrimaryEmailTable extends Table {

    public PrimaryEmailTable(
        final UserAdmin userAdmin,
        final ParameterSingleSelectionModel<String> selectedUserId,
        final ParameterSingleSelectionModel<String> selectedEmailAddress) {

        setModelBuilder(new PrimaryEmailTableModelBuilder(selectedUserId));

        final TableColumnModel columnModel = getColumnModel();

        columnModel.add(new TableColumn(
            PrimaryEmailTableModel.COL_ADDRESS,
            new Label(new GlobalizedMessage(
                "ui.admin.user.primary_email.address",
                ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            PrimaryEmailTableModel.COL_VERIFIED,
            new Label(new GlobalizedMessage(
                "ui.admin.user.primary_email.verified",
                ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            PrimaryEmailTableModel.COL_BOUNCING,
            new Label(new GlobalizedMessage(
                "ui.admin.user.primary_email.bouncing",
                ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            PrimaryEmailTableModel.COL_ACTION,
            new Label(new GlobalizedMessage(
                "ui.admin.user.primary_email.action",
                ADMIN_BUNDLE))));
        columnModel.get(
            PrimaryEmailTableModel.COL_ACTION).setCellRenderer(
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

        addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event) {
                final String key = (String) event.getRowKey();
                selectedEmailAddress.setSelectedKey(event.getPageState(), key);
                userAdmin.showEmailForm(event.getPageState());
            }

            @Override
            public void headSelected(final TableActionEvent event) {
                //Nothing
            }

        });

    }

}
