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
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.privileges.AdminPrivileges;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * A helper for checking if the current user has a permission granting certain
 * privileges.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class AdminPermissionsChecker {

    /**
     * The {@link PermissionChecker} instance to use.
     */
    @Inject
    private PermissionChecker permissionChecker;

    /**
     * Checks of the the {@link AdminPrivileges#ADMINISTER_CATEGORIES} privilege
     * has been granted to the current user.
     *
     * @param section The {@link ContentSection} one which the privilege might
     *                has been granted.
     *
     * @return {@code true} if the {@link AdminPrivileges#ADMINISTER_CATEGORIES}
     *         privilege has been granted to the current user for the provided
     *         {@link ContentSection}, otherwise {@code false}.
     */
    public boolean canAdministerCategories(final ContentSection section) {
        return permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_CATEGORIES, section
        );
    }

    /**
     * Checks of the the {@link AdminPrivileges#ADMINISTER_CONTENT_TYPES}
     * privilege has been granted to the current user.
     *
     * @param section The {@link ContentSection} one which the privilege might
     *                has been granted.
     *
     * @return {@code true} if the
     *         {@link AdminPrivileges#ADMINISTER_CONTENT_TYPES} privilege has
     *         been granted to the current user for the provided
     *         {@link ContentSection}, otherwise {@code false}.
     */
    public boolean canAdministerContentTypes(final ContentSection section) {
        return permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_CONTENT_TYPES, section
        );
    }

    /**
     * Checks of the the {@link AdminPrivileges#ADMINISTER_LIFECYLES} privilege
     * has been granted to the current user.
     *
     * @param section The {@link ContentSection} one which the privilege might
     *                has been granted.
     *
     * @return {@code true} if the {@link AdminPrivileges#ADMINISTER_LIFECYLES}
     *         privilege has been granted to the current user for the provided
     *         {@link ContentSection}, otherwise {@code false}.
     */
    public boolean canAdministerLifecycles(final ContentSection section) {
        return permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_LIFECYLES, section
        );
    }

    /**
     * Checks of the the {@link AdminPrivileges#ADMINISTER_ROLES} privilege has
     * been granted to the current user.
     *
     * @param section The {@link ContentSection} one which the privilege might
     *                has been granted.
     *
     * @return {@code true} if the {@link AdminPrivileges#ADMINISTER_ROLES}
     *         privilege has been granted to the current user for the provided
     *         {@link ContentSection}, otherwise {@code false}.
     */
    public boolean canAdministerRoles(final ContentSection section) {
        return permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_ROLES, section
        );
    }

    /**
     * Checks of the the {@link AdminPrivileges#ADMINISTER_WORKFLOWS} privilege
     * has been granted to the current user.
     *
     * @param section The {@link ContentSection} one which the privilege might
     *                has been granted.
     *
     * @return {@code true} if the {@link AdminPrivileges#ADMINISTER_WORKFLOWS}
     *         privilege has been granted to the current user for the provided
     *         {@link ContentSection}, otherwise {@code false}.
     */
    public boolean canAdministerWorkflows(final ContentSection section) {
        return permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_WORKFLOWS, section
        );
    }

}
