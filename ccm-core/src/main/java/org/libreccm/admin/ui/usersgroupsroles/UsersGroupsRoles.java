/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.libreccm.admin.ui.usersgroupsroles;

import com.arsdigita.ui.admin.AdminUiConstants;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import org.libreccm.admin.ui.AdminView;
import org.libreccm.security.User;

import java.util.List;
import java.util.ResourceBundle;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class UsersGroupsRoles extends CustomComponent {

    private static final long serialVersionUID = 7280416743018127366L;

    private static final String COL_USER_NAME = "username";
    private static final String COL_GIVEN_NAME = "given_name";
    private static final String COL_FAMILY_NAME = "family_name";
    private static final String COL_EMAIL = "email";
    private static final String COL_BANNED = "banned";

    private final AdminView view;

    private final TabSheet tabSheet;

    private final Grid<User> usersTable;

    public UsersGroupsRoles(final AdminView view) {

        this.view = view;

        final ResourceBundle bundle = ResourceBundle.getBundle(
            AdminUiConstants.ADMIN_BUNDLE, UI.getCurrent().getLocale());

        tabSheet = new TabSheet();
        usersTable = new Grid<>();
        usersTable.setWidth("100%");
//        usersTable.setItems(userRepo.findAll());
        usersTable.addColumn(User::getName)
            .setId(COL_USER_NAME)
            .setCaption("User name");
        usersTable
            .addColumn(User::getGivenName)
            .setId(COL_GIVEN_NAME)
            .setCaption("Given name");
        usersTable
            .addColumn(User::getFamilyName)
            .setId(COL_FAMILY_NAME)
            .setCaption("Family name");
        usersTable
            .addColumn(user -> user.getPrimaryEmailAddress().getAddress())
            .setId(COL_EMAIL)
            .setCaption("E-Mail");
        usersTable
            .addColumn(user -> {
                if (user.isBanned()) {
                    return bundle.getString("ui.admin.user.banned_yes");
                } else {
                    return bundle.getString("ui.admin.user.banned_no");
                }
            })
            .setId(COL_BANNED)
            .setCaption("Banned?");
        
        
        tabSheet.addTab(usersTable, "Users");

        setCompositionRoot(tabSheet);

    }

//    public void setUsers(final List<User> users) {
//        usersTable.setItems(users);
//    }
    
    public void setDataProvider(final UsersTableDataProvider dataProvider) {
        usersTable.setDataProvider(dataProvider);
    }

}
