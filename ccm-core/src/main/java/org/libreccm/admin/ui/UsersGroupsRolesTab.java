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

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TabSheet;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class UsersGroupsRolesTab extends CustomComponent {

    private static final long serialVersionUID = 7280416743018127366L;

    private final TabSheet tabSheet;

    private final UsersTable usersTable;
    private final GroupsTable groupsTable;
    private final RolesTable rolesTable;

    private UsersTableDataProvider usersTableDataProvider;
    private GroupsTableDataProvider groupsTableDataProvider;
    private RolesTableDataProvider rolesTableDataProvider;

    protected UsersGroupsRolesTab(final UsersGroupsRolesController controller) {

        tabSheet = new TabSheet();

        usersTable = new UsersTable(controller);
        usersTable.setWidth("100%");

        groupsTable = new GroupsTable(controller);
        groupsTable.setWidth("100%");

        rolesTable = new RolesTable(controller);
        rolesTable.setWidth("100%");
        rolesTable.setHeight("100%");

        tabSheet.addTab(usersTable, "Users");
        tabSheet.addTab(groupsTable, "Groups");
        tabSheet.addTab(rolesTable, "Roles");

        super.setCompositionRoot(tabSheet);

    }

}
