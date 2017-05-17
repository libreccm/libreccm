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
import org.libreccm.security.Group;

import java.util.ResourceBundle;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class GroupsTable extends Grid<Group> {

    private static final long serialVersionUID = 2731047837262813862L;

    private final static String COL_NAME = "name";
    private final static String COL_EDIT = "edit";
    private final static String COL_DELETE = "delete";

    private final TextField groupNameFilter;
    private final Button clearFiltersButton;
    private final Button createGroupButton;

    public GroupsTable(final AdminView view,
                       final UsersGroupsRoles usersGroupsRoles) {

        super();

        final ResourceBundle bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       UI.getCurrent().getLocale());

        addColumn(Group::getName)
            .setId(COL_NAME)
            .setCaption("Name");
        addColumn(user -> bundle.getString("ui.admin.groups.table.edit"),
                  new ButtonRenderer<>(event -> {
                      //ToDo Open GroupEditor window
                  }))
            .setId(COL_EDIT);
        addColumn(user -> bundle.getString("ui.admin.groups.table.delete"),
                  new ButtonRenderer<>(event -> {
                      //ToDo Display Confirm dialog
                  }))
            .setId(COL_DELETE);

        final HeaderRow filterRow = appendHeaderRow();
        final HeaderCell GroupNameFilterCell = filterRow.getCell(COL_NAME);
        groupNameFilter = new TextField();
        groupNameFilter.setPlaceholder("User name");
        groupNameFilter.setDescription("Filter Groups by Groupname");
        groupNameFilter.addStyleName(ValoTheme.TEXTFIELD_TINY);
        groupNameFilter
            .addValueChangeListener(event -> {
                ((GroupsTableDataProvider) getDataProvider())
                    .setGroupNameFilter(event.getValue().toLowerCase());
            });
        GroupNameFilterCell.setComponent(groupNameFilter);

        final HeaderRow actionsRow = prependHeaderRow();
        final HeaderCell actionsCell = actionsRow.join(COL_NAME,
                                                       COL_EDIT,
                                                       COL_DELETE);
        clearFiltersButton = new Button("Clear filters");
        clearFiltersButton.setStyleName(ValoTheme.BUTTON_TINY);
        clearFiltersButton.setIcon(VaadinIcons.BACKSPACE);
        clearFiltersButton.addClickListener(event -> {
            groupNameFilter.setValue("");
        });

        createGroupButton = new Button("New group");
        createGroupButton.setStyleName(ValoTheme.BUTTON_TINY);
        createGroupButton.setIcon(VaadinIcons.PLUS);
        createGroupButton.addClickListener(event -> {
            //ToDo Open GroupEditor
        });
        final HorizontalLayout actionsLayout = new HorizontalLayout(
            clearFiltersButton,
            createGroupButton);
        actionsCell.setComponent(actionsLayout);
    }

    public void localize() {

        final ResourceBundle bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       UI.getCurrent().getLocale());

        getColumn(COL_NAME)
            .setCaption(bundle.getString("ui.admin.groups.table.name"));

        groupNameFilter.setPlaceholder(
            bundle
                .getString("ui.admin.users.table.filter.groupname.placeholder"));
        groupNameFilter.setDescription(bundle
            .getString("ui.admin.users.table.filter.groupname.description"));

        clearFiltersButton.setCaption(bundle
            .getString("ui.admin.users.table.filter.clear"));
    }

}
