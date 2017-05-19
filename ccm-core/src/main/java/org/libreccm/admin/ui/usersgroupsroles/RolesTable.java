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
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.libreccm.admin.ui.AdminView;
import org.libreccm.security.Role;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class RolesTable extends Grid<Role> {

    private static final long serialVersionUID = 8298191390811634176L;

    private final static String COL_NAME = "name";
    private final static String COL_DESCRIPTION = "description";
    private final static String COL_EDIT = "edit";
    private final static String COL_DELETE = "delete";

    private final TextField roleNameFilter;
    private final Button clearFiltersButton;
    private final Button createRoleButton;

    public RolesTable(final AdminView adminView,
                      final UsersGroupsRoles usersGroupsRoles) {

        super();

        final ResourceBundle bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       UI.getCurrent().getLocale());

        addColumn(Role::getName)
            .setId(COL_NAME)
            .setCaption("Name");
        addColumn(role -> {
            if (role.getDescription().hasValue(UI.getCurrent().getLocale())) {
                return role.getDescription()
                    .getValue(UI.getCurrent().getLocale());
            } else {
                final Optional<Locale> locale = role
                    .getDescription()
                    .getAvailableLocales()
                    .stream()
                    .sorted((locale1, locale2) -> {
                        return locale1.toString().compareTo(locale2.toString());
                    })
                    .findFirst();
                if (locale.isPresent()) {
                return role.getDescription().getValue(locale.get());
                } else {
                    return "";
                }
            }

        })
            .setId(COL_DESCRIPTION)
            .setCaption("Description");
        addColumn(user -> bundle.getString("ui.admin.roles.table.edit"),
                  new ButtonRenderer<>(event -> {
                      //ToDo Open GroupEditor window
                  }))
            .setId(COL_EDIT);
        addColumn(user -> bundle.getString("ui.admin.roles.table.delete"),
                  new ButtonRenderer<>(event -> {
                      //ToDo Display Confirm dialog
                  }))
            .setId(COL_DELETE);

        final HeaderRow filterRow = appendHeaderRow();
        final HeaderCell GroupNameFilterCell = filterRow.getCell(COL_NAME);
        roleNameFilter = new TextField();
        roleNameFilter.setPlaceholder("Role name");
        roleNameFilter.setDescription("Filter Roles by name");
        roleNameFilter.addStyleName(ValoTheme.TEXTFIELD_TINY);
        roleNameFilter
            .addValueChangeListener(event -> {
                ((RolesTableDataProvider) getDataProvider())
                    .setRoleNameFilter(event.getValue().toLowerCase());
            });
        GroupNameFilterCell.setComponent(roleNameFilter);

        final HeaderRow actionsRow = prependHeaderRow();
        final HeaderCell actionsCell = actionsRow.join(COL_NAME,
                                                       COL_DESCRIPTION,
                                                       COL_EDIT,
                                                       COL_DELETE);
        clearFiltersButton = new Button("Clear filters");
        clearFiltersButton.setStyleName(ValoTheme.BUTTON_TINY);
        clearFiltersButton.setIcon(VaadinIcons.BACKSPACE);
        clearFiltersButton.addClickListener(event -> {
            roleNameFilter.setValue("");
        });

        createRoleButton = new Button("New role");
        createRoleButton.setStyleName(ValoTheme.BUTTON_TINY);
        createRoleButton.setIcon(VaadinIcons.PLUS);
        createRoleButton.addClickListener(event -> {
            //ToDo Open GroupEditor
        });
        final HorizontalLayout actionsLayout = new HorizontalLayout(
            clearFiltersButton,
            createRoleButton);
        actionsCell.setComponent(actionsLayout);
    }

    public void localize() {

        final ResourceBundle bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       UI.getCurrent().getLocale());

        getColumn(COL_NAME)
            .setCaption(bundle.getString("ui.admin.roles.table.name"));

        getColumn(COL_DESCRIPTION)
            .setCaption(bundle.getString("ui.admin.roles.table.description"));

        roleNameFilter.setPlaceholder(
            bundle
                .getString("ui.admin.users.table.filter.rolename.placeholder"));
        roleNameFilter.setDescription(bundle
            .getString("ui.admin.users.table.filter.rolename.description"));

        clearFiltersButton.setCaption(bundle
            .getString("ui.admin.users.table.filter.clear"));

    }

}
