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
package org.libreccm.admin.ui;

import com.arsdigita.ui.admin.AdminUiConstants;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;

import java.util.ResourceBundle;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class UsersGroupsRolesTab extends CustomComponent {

    private static final long serialVersionUID = 7280416743018127366L;

//    private static final String COL_USER_NAME = "username";
//    private static final String COL_GIVEN_NAME = "given_name";
//    private static final String COL_FAMILY_NAME = "family_name";
//    private static final String COL_EMAIL = "email";
//    private static final String COL_BANNED = "banned";
//    private static final String COL_PASSWORD_RESET_REQUIRED
//                                    = "password_reset_required";
//    private static final String COL_EDIT = "edit";
//    private static final String COL_DELETE = "delete";

    private final AdminView view;

    private final TabSheet tabSheet;

//    private final Grid<User> usersTable;
//    private final TextField userNameFilter;
//    private final Button clearFiltersButton;
//    private final Button createUserButton;
    
    private final UsersTable usersTable;
    private final GroupsTable groupsTable;
    private final RolesTable rolesTable;

    private UsersTableDataProvider usersTableDataProvider;
    private GroupsTableDataProvider groupsTableDataProvider;
    private RolesTableDataProvider rolesTableDataProvider;

    public UsersGroupsRolesTab(final AdminView view) {

        this.view = view;

        final ResourceBundle bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       UI.getCurrent().getLocale());

        tabSheet = new TabSheet();
//        usersTable = new Grid<>();
//        usersTable.setWidth("100%");
//        usersTable.addColumn(User::getName)
//            .setId(COL_USER_NAME)
//            .setCaption("User name");
//        usersTable
//            .addColumn(User::getGivenName)
//            .setId(COL_GIVEN_NAME)
//            .setCaption("Given name");
//        usersTable
//            .addColumn(User::getFamilyName)
//            .setId(COL_FAMILY_NAME)
//            .setCaption("Family name");
//        usersTable
//            .addColumn(user -> user.getPrimaryEmailAddress().getAddress())
//            .setId(COL_EMAIL)
//            .setCaption("E-Mail");
//        usersTable
//            .addColumn(user -> {
//                if (user.isBanned()) {
//                    return bundle.getString("ui.admin.user.banned_yes");
//                } else {
//                    return bundle.getString("ui.admin.user.banned_no");
//                }
//            })
//            .setId(COL_BANNED)
//            .setCaption("Banned?");
//        usersTable
//            .addColumn(user -> {
//                if (user.isPasswordResetRequired()) {
//                    return bundle.getString(
//                        "ui.admin.user.password_reset_required_yes");
//                } else {
//                    return bundle.getString(
//                        "ui.admin.user.password_reset_required_no");
//                }
//            })
//            .setId(COL_PASSWORD_RESET_REQUIRED)
//            .setCaption("Password reset required");
//        usersTable
//            .addColumn(user -> bundle.getString("ui.admin.users.table.edit"),
//                       new ButtonRenderer<>(event -> {
//                           final UserEditor editor = new UserEditor(
//                               event.getItem(),
//                               this,
//                               view.getUserRepository(),
//                               view.getUserManager());
//                           editor.center();
//                           UI.getCurrent().addWindow(editor);
//                       }))
//            .setId(COL_EDIT);
//
//        final HeaderRow filterRow = usersTable.appendHeaderRow();
//        final HeaderCell userNameFilterCell = filterRow.getCell(COL_USER_NAME);
//        userNameFilter = new TextField();
//        userNameFilter.setPlaceholder("User name");
//        userNameFilter.setDescription("Filter users by username");
//        userNameFilter.addStyleName(ValoTheme.TEXTFIELD_TINY);
//        userNameFilter
//            .addValueChangeListener(event -> {
//                usersTableDataProvider
//                    .setUserNameFilter(event.getValue().toLowerCase());
//            });
//        userNameFilterCell.setComponent(userNameFilter);
//
//        final HeaderRow actionsRow = usersTable.prependHeaderRow();
//        final HeaderCell actionsCell = actionsRow.join(COL_USER_NAME,
//                                                       COL_GIVEN_NAME,
//                                                       COL_FAMILY_NAME,
//                                                       COL_EMAIL,
//                                                       COL_BANNED);
//        clearFiltersButton = new Button("Clear filters");
//        clearFiltersButton.addStyleName(ValoTheme.BUTTON_TINY);
//        clearFiltersButton.addClickListener(event -> {
////            usersTableDataProvider.setUserNameFilter(null);
//            userNameFilter.setValue("");
//        });
//        createUserButton = new Button("New User");
//        createUserButton.addStyleName(ValoTheme.BUTTON_TINY);
//        createUserButton.setIcon(VaadinIcons.PLUS);
//        createUserButton.addClickListener(event -> {
//            final UserEditor userEditor = new UserEditor(
//                this,
//                view.getUserRepository(), 
//                view.getUserManager());
//            userEditor.center();
//            UI.getCurrent().addWindow(userEditor);
//        });
//        final HorizontalLayout actionsLayout = new HorizontalLayout(
//            clearFiltersButton, 
//            createUserButton);
//        actionsCell.setComponent(actionsLayout);

        usersTable = new UsersTable(view, this);
        usersTable.setWidth("100%");
        
        groupsTable = new GroupsTable(view, this);
        groupsTable.setWidth("100%");
        
        rolesTable = new RolesTable(view, this);
        rolesTable.setWidth("100%");
        rolesTable.setHeight("100%");
        
        tabSheet.addTab(usersTable, "Users");
        tabSheet.addTab(groupsTable, "Groups");
        tabSheet.addTab(rolesTable, "Roles");

        super.setCompositionRoot(tabSheet);

    }

    public void localize() {

//        final ResourceBundle bundle = ResourceBundle
//            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
//                       UI.getCurrent().getLocale());
//
//        usersTable
//            .getColumn(COL_USER_NAME)
//            .setCaption(bundle.getString("ui.admin.users.table.screenname"));
//        usersTable
//            .getColumn(COL_GIVEN_NAME)
//            .setCaption(bundle.getString("ui.admin.users.table.givenname"));
//        usersTable
//            .getColumn(COL_FAMILY_NAME)
//            .setCaption(bundle.getString("ui.admin.users.table.familyname"));
//        usersTable
//            .getColumn(COL_EMAIL)
//            .setCaption(bundle.getString("ui.admin.users.table.primary_email"));
//        usersTable
//            .getColumn(COL_BANNED)
//            .setCaption(bundle.getString("ui.admin.users.table.banned"));
//        usersTable
//            .getColumn(COL_PASSWORD_RESET_REQUIRED)
//            .setCaption(bundle.getString(
//                "ui.admin.users.table.password_reset_required"));
//
//        userNameFilter.setPlaceholder(bundle
//            .getString("ui.admin.users.table.filter.screenname.placeholder"));
//        userNameFilter.setDescription(bundle
//            .getString("ui.admin.users.table.filter.screenname.description"));
//
//        clearFiltersButton.setCaption(bundle
//            .getString("ui.admin.users.table.filter.clear"));

        usersTable.localize();

    }

    public void setUsersTableDataProvider(final UsersTableDataProvider dataProvider) {
        usersTableDataProvider = dataProvider;
        usersTable.setDataProvider(dataProvider);
    }
    
    public void setGroupsTableDataProvider(final GroupsTableDataProvider dataProvider) {
        groupsTableDataProvider = dataProvider;
        groupsTable.setDataProvider(dataProvider);
    }
    
    public void setRolesTableDataProvider(final RolesTableDataProvider dataProvider) {
        rolesTableDataProvider = dataProvider;
        rolesTable.setDataProvider(dataProvider);
    }
    
    protected void refreshUsers() {
        usersTableDataProvider.refreshAll();
    }
    
    protected void refreshGroups() {
        groupsTableDataProvider.refreshAll();
    }
    
    protected void refreshRoles() {
        rolesTableDataProvider.refreshAll();
    }
    
}
