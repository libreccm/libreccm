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
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Group;

import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class GroupSelector extends Window {

    private static final long serialVersionUID = -6227551833159691370L;

    private static final String COL_NAME = "groupname";

    public GroupSelector(final String caption,
                         final String actionLabel,
                         final UsersGroupsRoles usersGroupsRoles,
                         final List<Group> excludedGroups,
                         final GroupSelectionAction action) {

        addWidgets(caption, actionLabel, excludedGroups, action);
    }

    private void addWidgets(final String caption,
                            final String actionLabel,
                            final List<Group> excludedGroups,
                            final GroupSelectionAction action) {

        setCaption(caption);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();

        final Grid<Group> groupsGrid = new Grid<>();
        groupsGrid
            .addColumn(Group::getName)
            .setId(COL_NAME)
            .setCaption("Group");

        groupsGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        groupsGrid.setWidth("100%");

        final Button actionButton = new Button(actionLabel);
        actionButton.addClickListener(event -> {
            action.action(groupsGrid.getSelectedItems());
            close();
        });
        actionButton.setIcon(VaadinIcons.PLUS_CIRCLE_O);
        actionButton.setStyleName(ValoTheme.BUTTON_TINY);

        final Button clearButton = new Button("Clear selection");
        clearButton.addClickListener(event -> {
            groupsGrid.getSelectionModel().deselectAll();
        });
        clearButton.setIcon(VaadinIcons.BACKSPACE);
        clearButton.setStyleName(ValoTheme.BUTTON_TINY);

        final HeaderRow actions = groupsGrid.prependHeaderRow();
        final HeaderCell actionsCell = actions.getCell(COL_NAME);
        actionsCell.setComponent(new HorizontalLayout(actionButton,
                                                      clearButton));
        
        final GroupSelectorDataProvider dataProvider = cdiUtil
        .findBean(GroupSelectorDataProvider.class);
        
        dataProvider.setExcludedGroups(excludedGroups);
        
        groupsGrid.setDataProvider(dataProvider);
        
        setContent(groupsGrid);
    }

}
