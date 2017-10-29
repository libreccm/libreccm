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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;
import org.libreccm.security.Role;

import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class RoleSelector extends Window {

    private static final long serialVersionUID = -6893510634359633368L;

    private static final String COL_NAME = "rolename";
    
    protected RoleSelector(final String caption,
                        final String actionLabel,
                        final RoleSelectorDataProvider dataProvider,
                        final List<Role> excludedRoles,
                        final RoleSelectionAction action) {

        addWidgets(caption, actionLabel, dataProvider, excludedRoles, action);
    }

    private void addWidgets(final String caption,
                            final String actionLabel,
                            final RoleSelectorDataProvider dataProvider,
                            final List<Role> excludedRoles,
                            final RoleSelectionAction action) {

        setCaption(caption);

        final Grid<Role> rolesGrid = new Grid<>();
        rolesGrid
            .addColumn(Role::getName)
            .setId(COL_NAME)
            .setCaption("Role");

        rolesGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        rolesGrid.setWidth("100%");

        final Button actionButton = new Button(actionLabel);
        actionButton.addClickListener(event -> {
            action.action(rolesGrid.getSelectedItems());
            close();
        });
        actionButton.setIcon(VaadinIcons.PLUS_CIRCLE_O);
        actionButton.setStyleName(ValoTheme.BUTTON_TINY);

        final Button clearButton = new Button("Clear selection");
        clearButton.addClickListener(event -> {
            rolesGrid.getSelectionModel().deselectAll();
        });
        clearButton.setIcon(VaadinIcons.BACKSPACE);
        clearButton.setStyleName(ValoTheme.BUTTON_TINY);

        final HeaderRow actions = rolesGrid.prependHeaderRow();
        final HeaderCell actionsCell = actions.getCell(COL_NAME);
        actionsCell.setComponent(new HorizontalLayout(actionButton,
                                                      clearButton));

        
        dataProvider.setExcludedRoles(excludedRoles);
        rolesGrid.setDataProvider(dataProvider);

        setContent(rolesGrid);
    }

}
