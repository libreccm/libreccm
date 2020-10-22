/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.ui.admin.usersgroupsroles;

import org.libreccm.ui.admin.AdminConstants;
import org.libreccm.ui.admin.AdminPage;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationScoped
public class UsersGroupsRolesPage implements AdminPage {

    @Override
    public Set<Class<?>> getControllerClasses() {
        final Set<Class<?>> classes = new HashSet<>();
        classes.add(UsersGroupsRolesController.class);
        classes.add(GroupsController.class);
        classes.add(GroupFormController.class);
        classes.add(GroupMembersRolesController.class);
        classes.add(RolesController.class);
        classes.add(RoleFormController.class);
        classes.add(RoleMembersController.class);
        classes.add(UsersController.class);
        classes.add(UserFormController.class);
        classes.add(UserGroupsRolesController.class);
        classes.add(EmailFormController.class);
        return classes;
    }

    @Override
    public String getUriIdentifier() {
        return String.format(
            "%s#getOverview",
            UsersGroupsRolesController.class.getSimpleName()
        );
    }

    @Override
    public String getLabelBundle() {
        return AdminConstants.ADMIN_BUNDLE;
    }

    @Override
    public String getLabelKey() {
        return "usersgroupsroles.label";
    }

    @Override
    public String getDescriptionBundle() {
        return AdminConstants.ADMIN_BUNDLE;
    }

    @Override
    public String getDescriptionKey() {
        return "usersgroupsroles.description";
    }

    @Override
    public String getIcon() {
        return "people-fill";
    }

    @Override
    public int getPosition() {
        return 10;
    }

}
