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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.libreccm.security.User;


/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class UsersTable extends Grid<User> {

    private static final long serialVersionUID = -6535913368522021496L;

    private static final String COL_USER_NAME = "username";
    private static final String COL_GIVEN_NAME = "given_name";
    private static final String COL_FAMILY_NAME = "family_name";
    private static final String COL_EMAIL = "email";
    private static final String COL_BANNED = "banned";
    private static final String COL_PASSWORD_RESET_REQUIRED
                                    = "password_reset_required";
    private static final String COL_EDIT = "edit";

    private final TextField userNameFilter;
    private final Button clearFiltersButton;
    private final Button createUserButton;

    protected UsersTable(final UsersGroupsRolesController controller) {

        super();

        setDataProvider(controller.getUsersTableDataProvider());

        final LocalizedTextsUtil adminTextsUtil = controller
            .getGlobalizationHelper()
            .getLocalizedTextsUtil(AdminUiConstants.ADMIN_BUNDLE);

        super.addColumn(User::getName)
            .setId(COL_USER_NAME)
            .setCaption(adminTextsUtil
                .getText("ui.admin.users.table.screenname"));
        super.addColumn(User::getGivenName)
            .setId(COL_GIVEN_NAME)
            .setCaption(adminTextsUtil.getText("ui.admin.users.table.givenname"));
        super.addColumn(User::getFamilyName)
            .setId(COL_FAMILY_NAME)
            .setCaption(adminTextsUtil
                .getText("ui.admin.users.table.familyname"));
        super.addColumn(user -> user.getPrimaryEmailAddress().getAddress())
            .setId(COL_EMAIL)
            .setCaption(adminTextsUtil.getText(
                "ui.admin.users.table.primary_email"));
        super.addColumn(user -> {
            if (user.isBanned()) {
                return adminTextsUtil.getText("ui.admin.user.banned_yes");
            } else {
                return adminTextsUtil.getText("ui.admin.user.banned_no");
            }
        })
            .setId(COL_BANNED)
            .setCaption("Banned?");
        super.addColumn(user -> {
            if (user.isPasswordResetRequired()) {
                return adminTextsUtil.getText(
                    "ui.admin.user.password_reset_required_yes");
            } else {
                return adminTextsUtil.getText(
                    "ui.admin.user.password_reset_required_no");
            }
        })
            .setId(COL_PASSWORD_RESET_REQUIRED)
            .setCaption(adminTextsUtil.getText(
                "ui.admin.users.table.password_reset_required"));
        super.addComponentColumn(user -> {
            final Button editButton = new Button(
                adminTextsUtil.getText("ui.admin.users.table.edit"),
                VaadinIcons.EDIT);
            editButton.addClickListener(event -> {
                final UserDetails details = new UserDetails(
                    user,
                controller.getUsersController());
                details.setModal(true);
                details.center();
                details.setWidth("66.6%");
                details.addCloseListener(closeEvent -> {
                    getDataProvider().refreshAll();
                });
                UI.getCurrent().addWindow(details);
            });
            editButton.addStyleName(ValoTheme.BUTTON_TINY);
            return editButton;
        })
            .setId(COL_EDIT);

        final HeaderRow filterRow = appendHeaderRow();
        final HeaderCell userNameFilterCell = filterRow.getCell(COL_USER_NAME);
        userNameFilter = new TextField();
        userNameFilter.setPlaceholder(adminTextsUtil
            .getText("ui.admin.users.table.filter.screenname.placeholder"));
        userNameFilter.setDescription(adminTextsUtil
            .getText("ui.admin.users.table.filter.screenname.description"));
        userNameFilter.addStyleName(ValoTheme.TEXTFIELD_TINY);
        userNameFilter
            .addValueChangeListener(event -> {
                ((UsersTableDataProvider) getDataProvider())
                    .setUserNameFilter(event.getValue().toLowerCase());
            });
        userNameFilterCell.setComponent(userNameFilter);

        final HeaderRow actionsRow = prependHeaderRow();
        final HeaderCell actionsCell = actionsRow.join(COL_USER_NAME,
                                                       COL_GIVEN_NAME,
                                                       COL_FAMILY_NAME,
                                                       COL_EMAIL,
                                                       COL_BANNED);
        clearFiltersButton = new Button(adminTextsUtil
            .getText("ui.admin.users.table.filter.clear"));
        clearFiltersButton.addStyleName(ValoTheme.BUTTON_TINY);
        clearFiltersButton.addClickListener(event -> {
            userNameFilter.setValue("");
        });

        createUserButton = new Button("New User");
        createUserButton.addStyleName(ValoTheme.BUTTON_TINY);
        createUserButton.setIcon(VaadinIcons.PLUS);
        createUserButton.addClickListener(event -> {
            final UserEditor userEditor = new UserEditor(controller
                .getUsersController());
            userEditor.addCloseListener(closeEvent -> {
                getDataProvider().refreshAll();
            });
            userEditor.center();
            UI.getCurrent().addWindow(userEditor);
        });
        final HorizontalLayout actionsLayout = new HorizontalLayout(
            clearFiltersButton,
            createUserButton);
        actionsCell.setComponent(actionsLayout);
    }

}
