/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui.contentsections;

import org.libreccm.security.PermissionChecker;
import org.libreccm.security.PermissionManager;
import org.librecms.contentsection.Folder;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * A helper class to build {@link GrantedPrivilegeModel} for a specific category
 * of privileges granted to the current user.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
abstract class AbstractCurrentUserPermissions {

    /**
     * CDI managed {@link PermissionChecker} instance used to check the
     * permissions of the current user.
     */
    @Inject
    private PermissionChecker permissionChecker;

    /**
     * CDI managed {@link PermissionManager} instance used to enumerate the
     * permission constants in the class provided by
     * {@link #getPrivilegesClass()}.
     */
    @Inject
    private PermissionManager permissionManager;

    /**
     * Builds a list of {@link GrantedPrivilegeModel}s for the current user and
     * the privileges returned by {@link #getPrivilegesClass() }.
     *
     * @param folder The folder for which the permissions are checked.
     *
     * @return A list of {@link GrantedPrivilegeModel} instances.
     */
    public List<GrantedPrivilegeModel> buildCurrentUserPermissions(
        final Folder folder
    ) {
        return permissionManager
            .listDefiniedPrivileges(getPrivilegesClass())
            .stream()
            .map(privilege -> buildCurrentUserPermission(folder, privilege))
            .collect(Collectors.toList());
    }

    /**
     * Provides the class with privilege constants to use.
     *
     * @return The class with privilege constants to use.
     */
    protected abstract Class<?> getPrivilegesClass();

    /**
     * Build a {@link GrantedPrivilegeModel} for a folder and a privilege.
     *
     * @param folder    The folder to use.
     * @param privilege The privilege.
     *
     * @return A {@link GrantedPrivilegeModel} for the current user, the
     *         provided folder and the provided privilege.
     */
    private GrantedPrivilegeModel buildCurrentUserPermission(
        final Folder folder, final String privilege
    ) {
        final GrantedPrivilegeModel model = new GrantedPrivilegeModel();
        model.setPrivilege(privilege);
        model.setGranted(permissionChecker.isPermitted(privilege, folder));
        return model;
    }

}
