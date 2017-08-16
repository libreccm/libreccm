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
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.User;

import java.util.List;
import java.util.ResourceBundle;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class UserSelector extends Window {

    private static final long serialVersionUID = -6227551833159691370L;

    private static final String COL_USER_NAME = "username";
    private static final String COL_GIVEN_NAME = "given_name";
    private static final String COL_FAMILY_NAME = "family_name";
    private static final String COL_EMAIL = "email";


    public UserSelector(final String caption,
                        final String actionLabel,
                        final UsersGroupsRolesTab usersGroupsRoles,
                        final List<User> excludedUsers,
                        final UserSelectionAction action) {

        addWidgets(caption, actionLabel, excludedUsers, action);
    }

    private void addWidgets(final String caption,
                            final String actionLabel,
                            final List<User> excludedUsers,
                            final UserSelectionAction action) {

        setCaption(caption);

        final ResourceBundle bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       UI.getCurrent().getLocale());

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();

        final Grid<User> usersGrid = new Grid<>();
        usersGrid
            .addColumn(User::getName)
            .setId(COL_USER_NAME)
            .setCaption("User Name");
        usersGrid
            .addColumn(User::getGivenName)
            .setId(COL_GIVEN_NAME)
            .setCaption("Given name");
        usersGrid
            .addColumn(User::getFamilyName)
            .setId(COL_FAMILY_NAME)
            .setCaption("Family name");
        usersGrid
            .addColumn(user -> user.getPrimaryEmailAddress().getAddress())
            .setId(COL_EMAIL)
            .setCaption("E-Mail");

        usersGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        usersGrid.setWidth("100%");

        final Button actionButton = new Button(actionLabel);
        actionButton.addClickListener(event -> {
            action.action(usersGrid.getSelectedItems());
            close();
        });
        actionButton.setIcon(VaadinIcons.PLUS_CIRCLE_O);
        actionButton.setStyleName(ValoTheme.BUTTON_TINY);

        final Button clearButton = new Button("Clear selection");
        clearButton.addClickListener(event -> {
            usersGrid.getSelectionModel().deselectAll();
        });
        clearButton.setIcon(VaadinIcons.BACKSPACE);
        clearButton.setStyleName(ValoTheme.BUTTON_TINY);

        final HeaderRow actions = usersGrid.prependHeaderRow();
        final HeaderCell actionsCell = actions.join(COL_USER_NAME,
                                                    COL_GIVEN_NAME,
                                                    COL_FAMILY_NAME,
                                                    COL_EMAIL);
        actionsCell.setComponent(new HorizontalLayout(actionButton,
                                                      clearButton));

        final UserSelectorDataProvider dataProvider = cdiUtil
            .findBean(UserSelectorDataProvider.class);
        dataProvider.setExcludedUsers(excludedUsers);
        usersGrid.setDataProvider(dataProvider);

        setContent(usersGrid);
    }

}
