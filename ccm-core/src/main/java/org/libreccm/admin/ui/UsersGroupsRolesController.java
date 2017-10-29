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

import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.GroupManager;
import org.libreccm.security.GroupRepository;
import org.libreccm.security.RoleManager;
import org.libreccm.security.RoleRepository;
import org.libreccm.security.UserManager;
import org.libreccm.security.UserRepository;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class UsersGroupsRolesController implements Serializable {

    private static final long serialVersionUID = -1994224681148412678L;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private GroupManager groupManager;

    @Inject
    private GroupRepository groupRepository;

    @Inject
    private GroupsController groupsController;

    @Inject
    private GroupsTableDataProvider groupsTableDataProvider;

    @Inject
    private RolesController rolesController;

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
    private UsersController usersController;

    @Inject
    private UsersTableDataProvider usersTableDataProvider;

    protected GlobalizationHelper getGlobalizationHelper() {
        return globalizationHelper;
    }

    protected GroupManager getGroupManager() {
        return groupManager;
    }

    protected GroupRepository getGroupRepository() {
        return groupRepository;
    }

    protected GroupsController getGroupsController() {
        return groupsController;
    }

    protected GroupsTableDataProvider getGroupsTableDataProvider() {
        return groupsTableDataProvider;
    }

    protected RolesController getRolesController() {
        return rolesController;
    }

    protected RoleManager getRoleManager() {
        return roleManager;
    }

    protected RoleRepository getRoleRepository() {
        return RoleRepository;
    }

    protected RolesTableDataProvider getRolesTableDataProvider() {
        return rolesTableDataProvider;
    }

    protected UserManager getUserManager() {
        return userManager;
    }

    protected UserRepository getUserRepository() {
        return userRepository;
    }

    protected UsersController getUsersController() {
        return usersController;
    }

    protected UsersTableDataProvider getUsersTableDataProvider() {
        return usersTableDataProvider;
    }

}
