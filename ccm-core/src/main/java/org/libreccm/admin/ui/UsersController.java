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

import com.vaadin.cdi.ViewScoped;
import org.libreccm.security.ChallengeManager;
import org.libreccm.security.GroupRepository;
import org.libreccm.security.RoleRepository;
import org.libreccm.security.UserManager;
import org.libreccm.security.UserRepository;

import java.io.Serializable;

import javax.inject.Inject;

/**
 * Contains all injection points
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
class UsersController implements Serializable {

    private static final long serialVersionUID = -3133073086999101093L;

    @Inject
    private GroupRepository groupRepository;

    @Inject
    private GroupSelectorDataProvider groupSelectorDataProvider;

    @Inject
    private UserGroupsController groupsController;

    @Inject
    private UserGroupsTableDataProvider groupsTableDataProvider;

    @Inject
    private UserRolesController rolesController;

    @Inject
    private UserRolesTableDataProvider rolesTableDataProvider;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private RoleSelectorDataProvider roleSelectorDataProvider;

    @Inject
    private UserManager userManager;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UsersTableDataProvider usersTableDataProvider;

    @Inject
    private ChallengeManager challengeManager;

    protected GroupRepository getGroupRepository() {
        return groupRepository;
    }

    protected GroupSelectorDataProvider getGroupSelectorDataProvider() {
        return groupSelectorDataProvider;
    }

    protected UserGroupsController getGroupsController() {
        return groupsController;
    }

    protected UserGroupsTableDataProvider getGroupsTableDataProvider() {
        return groupsTableDataProvider;
    }

    protected UserManager getUserManager() {
        return userManager;
    }

    protected UserRepository getUserRepository() {
        return userRepository;
    }

    protected RoleRepository getRoleRepository() {
        return roleRepository;
    }

    protected UserRolesTableDataProvider getRolesTableDataProvider() {
        return rolesTableDataProvider;
    }

    protected UserRolesController getRolesController() {
        return rolesController;
    }

    protected RoleSelectorDataProvider getRoleSelectorDataProvider() {
        return roleSelectorDataProvider;
    }

    protected UsersTableDataProvider getUsersTableDataProvider() {
        return usersTableDataProvider;
    }

    public ChallengeManager getChallengeManager() {
        return challengeManager;
    }

}
