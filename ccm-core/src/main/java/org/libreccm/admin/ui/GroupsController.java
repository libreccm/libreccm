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
import org.libreccm.security.GroupManager;
import org.libreccm.security.GroupRepository;
import org.libreccm.security.RoleRepository;
import org.libreccm.security.UserRepository;

import java.io.Serializable;

import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
class GroupsController implements Serializable {

    private static final long serialVersionUID = 4112198806815494041L;

    @Inject
    private GroupManager groupManager;

    @Inject
    private GroupMembersController membersController;

    @Inject
    private GroupMembersTableDataProvider membersTableDataProvider;

    @Inject
    private GroupRepository groupRepository;

    @Inject
    private GroupsTableDataProvider groupsTableDataProvider;

    @Inject
    private GroupRolesController rolesController;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private GroupRolesTableDataProvider rolesTableDataProvider;

    @Inject
    private RoleSelectorDataProvider roleSelectorDataProvider;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserSelectorDataProvider userSelectorDataProvider;

    protected GroupManager getGroupManager() {
        return groupManager;
    }

    protected GroupMembersController getMembersController() {
        return membersController;
    }

    protected GroupMembersTableDataProvider getMembersTableDataProvider() {
        return membersTableDataProvider;
    }

    protected GroupRepository getGroupRepository() {
        return groupRepository;
    }

    protected GroupsTableDataProvider getGroupsTableDataProvider() {
        return groupsTableDataProvider;
    }

    protected GroupRolesController getRolesController() {
        return rolesController;
    }

    protected RoleRepository getRoleRepository() {
        return roleRepository;
    }

    protected GroupRolesTableDataProvider getRolesTableDataProvider() {
        return rolesTableDataProvider;
    }

    protected RoleSelectorDataProvider getRoleSelectorDataProvider() {
        return roleSelectorDataProvider;
    }

    protected UserRepository getUserRepository() {
        return userRepository;
    }

    protected UserSelectorDataProvider getUserSelectorDataProvider() {
        return userSelectorDataProvider;
    }

}
