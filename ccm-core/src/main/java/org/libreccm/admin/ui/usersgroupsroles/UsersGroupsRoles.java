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

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;
import org.libreccm.admin.ui.AdminView;
import org.libreccm.security.User;

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
    private UsersTableDataProvider usersTableDataProvider;

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

        final HeaderRow filterRow = usersTable.appendHeaderRow();
        final HeaderCell userNameFilterCell = filterRow.getCell(COL_USER_NAME);
        final TextField userNameFilter = new TextField();
        userNameFilter.setPlaceholder("User name");
        userNameFilter.setDescription("Filter users by username");
        userNameFilter.addStyleName(ValoTheme.TEXTFIELD_TINY);
        userNameFilter
            .addValueChangeListener(event -> {
                usersTableDataProvider
                    .setUserNameFilter(event.getValue().toLowerCase());
            });
        userNameFilterCell.setComponent(userNameFilter);
        
        final HeaderRow actionsRow = usersTable.prependHeaderRow();
        final HeaderCell actionsCell = actionsRow.join(COL_USER_NAME, 
                        COL_GIVEN_NAME, 
                        COL_FAMILY_NAME, 
                        COL_EMAIL, 
                        COL_BANNED);
        final Button clearFiltersButton = new Button("Clear filters");
        clearFiltersButton.addClickListener(event -> {
            usersTableDataProvider.setUserNameFilter(null);
        });
        final HorizontalLayout actionsLayout = new HorizontalLayout(
            clearFiltersButton);
        actionsCell.setComponent(actionsLayout);

        tabSheet.addTab(usersTable, "Users");

        setCompositionRoot(tabSheet);

    }

//    public void setUsers(final List<User> users) {
//        usersTable.setItems(users);
//    }
    public void setDataProvider(final UsersTableDataProvider dataProvider) {
        this.usersTableDataProvider = dataProvider;
        usersTable.setDataProvider(dataProvider);
    }

}
