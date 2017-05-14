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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.libreccm.admin.ui.AdminView;
import org.libreccm.security.User;
import org.libreccm.security.UserRepository;

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
    private static final String COL_PASSWORD_RESET_REQUIRED
                                    = "password_reset_required";
    private static final String COL_EDIT = "edit";
    private static final String COL_DELETE = "delete";

    private final AdminView view;

    private final TabSheet tabSheet;

    private final Grid<User> usersTable;
    private final TextField userNameFilter;
    private final Button clearFiltersButton;
    private final Button createUserButton;

    private UsersTableDataProvider usersTableDataProvider;

    public UsersGroupsRoles(final AdminView view) {

        this.view = view;

        final ResourceBundle bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       UI.getCurrent().getLocale());

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
        usersTable
            .addColumn(user -> {
                if (user.isPasswordResetRequired()) {
                    return bundle.getString(
                        "ui.admin.user.password_reset_required_yes");
                } else {
                    return bundle.getString(
                        "ui.admin.user.password_reset_required_no");
                }
            })
            .setId(COL_PASSWORD_RESET_REQUIRED)
            .setCaption("Password reset required");
        usersTable
            .addColumn(user -> bundle.getString("ui.admin.users.table.edit"),
                       new ButtonRenderer<>(event -> {
                           final UserEditor editor = new UserEditor(
                               event.getItem(),
                               this,
                               view.getUserRepository(),
                               view.getUserManager());
                           editor.center();
                           UI.getCurrent().addWindow(editor);
                       }))
            .setId(COL_EDIT);

        final HeaderRow filterRow = usersTable.appendHeaderRow();
        final HeaderCell userNameFilterCell = filterRow.getCell(COL_USER_NAME);
        userNameFilter = new TextField();
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
        clearFiltersButton = new Button("Clear filters");
        clearFiltersButton.addStyleName(ValoTheme.BUTTON_TINY);
        clearFiltersButton.addClickListener(event -> {
//            usersTableDataProvider.setUserNameFilter(null);
            userNameFilter.setValue("");
        });
        createUserButton = new Button("New User");
        createUserButton.addStyleName(ValoTheme.BUTTON_TINY);
        createUserButton.setIcon(VaadinIcons.PLUS);
        createUserButton.addClickListener(event -> {
            final UserEditor userEditor = new UserEditor(
                this,
                view.getUserRepository(), 
                view.getUserManager());
            userEditor.center();
            UI.getCurrent().addWindow(userEditor);
        });
        final HorizontalLayout actionsLayout = new HorizontalLayout(
            clearFiltersButton, 
            createUserButton);
        actionsCell.setComponent(actionsLayout);

        tabSheet.addTab(usersTable, "Users");

        setCompositionRoot(tabSheet);

    }

    public void localize() {

        final ResourceBundle bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       UI.getCurrent().getLocale());

        usersTable
            .getColumn(COL_USER_NAME)
            .setCaption(bundle.getString("ui.admin.users.table.screenname"));
        usersTable
            .getColumn(COL_GIVEN_NAME)
            .setCaption(bundle.getString("ui.admin.users.table.givenname"));
        usersTable
            .getColumn(COL_FAMILY_NAME)
            .setCaption(bundle.getString("ui.admin.users.table.familyname"));
        usersTable
            .getColumn(COL_EMAIL)
            .setCaption(bundle.getString("ui.admin.users.table.primary_email"));
        usersTable
            .getColumn(COL_BANNED)
            .setCaption(bundle.getString("ui.admin.users.table.banned"));
        usersTable
            .getColumn(COL_PASSWORD_RESET_REQUIRED)
            .setCaption(bundle.getString(
                "ui.admin.users.table.password_reset_required"));

        userNameFilter.setPlaceholder(bundle
            .getString("ui.admin.users.table.filter.screenname.placeholder"));
        userNameFilter.setDescription(bundle
            .getString("ui.admin.users.table.filter.screenname.description"));

        clearFiltersButton.setCaption(bundle
            .getString("ui.admin.users.table.filter.clear"));

    }

//    public void setUsers(final List<User> users) {
//        usersTable.setItems(users);
//    }
    public void setDataProvider(final UsersTableDataProvider dataProvider) {
        this.usersTableDataProvider = dataProvider;
        usersTable.setDataProvider(dataProvider);
    }
    
    protected void refreshUsers() {
        usersTableDataProvider.refreshAll();
    }
    
}
