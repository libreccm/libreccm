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
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.privileges.TypePrivileges;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * Checks permissions on content types.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class TypePermissionsChecker {

    /**
     * {@link PermissionChecker} instance used for performing the permission
     * check.
     */
    @Inject
    private PermissionChecker permissionChecker;

    /**
     * Checks if the current user is permitted to use the provided
     * {@link ContentType}.
     *
     * @param type The content type.
     *
     * @return {@code true} if the current user is permitted to use the provided
     *         {@code type}, {@code false} otherwise.
     */
    public boolean canUseType(final ContentType type) {
        return permissionChecker.isPermitted(
            TypePrivileges.USE_TYPE, type
        );
    }

}
