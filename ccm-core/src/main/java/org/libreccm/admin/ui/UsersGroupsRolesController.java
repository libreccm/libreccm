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

import org.libreccm.security.GroupManager;
import org.libreccm.security.GroupRepository;
import org.libreccm.security.RoleManager;
import org.libreccm.security.RoleRepository;
import org.libreccm.security.UserManager;
import org.libreccm.security.UserRepository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class UsersGroupsRolesController {

    @Inject
    private GroupManager groupManager;

    @Inject
    private GroupRepository groupRepository;

    @Inject
    private GroupsTableDataProvider groupsTableDataProvider;

    @Inject
    private RoleManager roleManager;

    @Inject
    private RoleRepository RoleRepository;

    @Inject
    private RolesTableDataProvider rolesTableDataProvider;

    @Inject
    private UserManager userManager;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UsersTableDataProvider usersTableDataProvider;

    public GroupManager getGroupManager() {
        return groupManager;
    }

    public GroupRepository getGroupRepository() {
        return groupRepository;
    }

    public GroupsTableDataProvider getGroupsTableDataProvider() {
        return groupsTableDataProvider;
    }

    public RoleManager getRoleManager() {
        return roleManager;
    }

    public RoleRepository getRoleRepository() {
        return RoleRepository;
    }

    public RolesTableDataProvider getRolesTableDataProvider() {
        return rolesTableDataProvider;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public UsersTableDataProvider getUsersTableDataProvider() {
        return usersTableDataProvider;
    }

}
