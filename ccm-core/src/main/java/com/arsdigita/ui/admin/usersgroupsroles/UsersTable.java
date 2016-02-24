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
package com.arsdigita.ui.admin.usersgroupsroles;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.User;
import org.libreccm.security.UserRepository;

import java.util.List;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class UsersTable extends Table {

    private static final Logger LOGGER = LogManager.getLogger(UsersTable.class);

    private static final int COL_SCREEN_NAME = 0;
    private static final int COL_GIVEN_NAME = 1;
    private static final int COL_FAMILY_NAME = 2;
    private static final int COL_PRIMARY_EMAIL = 3;
    private static final int COL_BANNED = 4;

    public UsersTable() {
        super();

        setEmptyView(new Label(new GlobalizedMessage(
            "ui.admin.users.table.no_users", ADMIN_BUNDLE)));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
            COL_SCREEN_NAME,
            new Label(new GlobalizedMessage("ui.admin.users.table.screenname",
                                            ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_GIVEN_NAME,
            new Label(new GlobalizedMessage("ui.admin.users.table.givenname",
                                            ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_FAMILY_NAME,
            new Label(new GlobalizedMessage("ui.admin.users.table.familyname",
                                            ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_PRIMARY_EMAIL,
            new Label(new GlobalizedMessage(
                "ui.admin.users.table.primary_email", ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_BANNED,
            new Label(new GlobalizedMessage(
                "ui.admin.users.table.banned", ADMIN_BUNDLE))));

        setModelBuilder(new UsersTableModelBuilder());
    }

    private class UsersTableModelBuilder extends LockableImpl
        implements TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);

            return new UsersTableModel();
        }

    }

    private class UsersTableModel implements TableModel {

        private final List<User> users;
        private int index = -1;

        public UsersTableModel() {
            LOGGER.debug("Creating UsersTableModel...");
            final UserRepository userRepository = CdiUtil.createCdiUtil()
                .findBean(UserRepository.class);
            users = userRepository.findAll();
            LOGGER.debug("Found {} users in database.", users.size());
        }

        @Override
        public int getColumnCount() {
            return 6;
        }

        @Override
        public boolean nextRow() {
            index++;
            LOGGER.debug("Next row called. Index is {}", index);
            return index < users.size();
//            index++;
//            LOGGER.debug("Result is '{}'. Index is now {}", result, index);
//            return result;
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            LOGGER.debug("Getting element for row {}, column {}...",
                         index,
                         columnIndex);
            final User user = users.get(index);
            switch (columnIndex) {
                case COL_BANNED:
                    return Boolean.toString(user.isBanned());
                case COL_FAMILY_NAME:
                    return user.getFamilyName();
                case COL_GIVEN_NAME:
                    return user.getGivenName();
                case COL_PRIMARY_EMAIL:
                    return user.getPrimaryEmailAddress().getAddress();
                case COL_SCREEN_NAME:
                    return user.getName();
                default:
                    throw new IllegalArgumentException(
                        "No a valid column index.");
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            LOGGER.debug("Getting key for row {}, column {}...",
                         index,
                         columnIndex);
            return users.get(index).getPartyId();
        }

    }

}
