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

import com.vaadin.cdi.CDIUI;
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
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;

import java.util.List;
import java.util.ResourceBundle;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class RoleSelector extends Window {
    
    private static final long serialVersionUID = -1437536052155383270L;
    
    private static final String COL_NAME = "rolename";
    
    private final RoleRepository roleRepo;
    
    private final RoleSelectionAction roleSelectionAction;
    
    public RoleSelector(final String caption,
                        final String actionLabel,
                        final UsersGroupsRoles usersGroupsRoles,
                        final List<Role> excludedRoles,
                        final RoleSelectionAction action) {
        
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        roleRepo = cdiUtil.findBean(RoleRepository.class);
        this.roleSelectionAction = action;
        
        addWidgets(caption, actionLabel, excludedRoles, action);
    }
    
    private void addWidgets(final String caption,
                            final String actionLabel,
                            final List<Role> excludedRoles,
                            final RoleSelectionAction action) {
        
        setCaption(caption);
        
        final ResourceBundle bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       UI.getCurrent().getLocale());

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        
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
        final HeaderCell actionsCell = actions.join(COL_NAME);
        actionsCell.setComponent(new HorizontalLayout(actionButton,
                                                      clearButton));

        final RoleSelectorDataProvider dataProvider = cdiUtil
            .findBean(RoleSelectorDataProvider.class);

        dataProvider.setExcludedRoles(excludedRoles);

        rolesGrid.setDataProvider(dataProvider);

        setContent(rolesGrid);
    }
}
