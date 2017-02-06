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
package com.arsdigita.cms.ui.permissions;

import org.libreccm.core.CcmObject;
import org.libreccm.security.Role;

/**
 *
 * Structure to hold a permission and its current grant state
 *
 *
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
final class PermissionStatus {

    private final boolean granted;
    private final CcmObject object;
    private final Role role;
    private final String privilege;

    PermissionStatus(final String privilege,
                     final CcmObject object,
                     final Role role,
                     final boolean granted) {

        this.granted = granted;

        this.object = object;
        this.role = role;
        this.privilege = privilege;
    }

    boolean isGranted() {
        return granted;
    }

    CcmObject getObject() {
        return object;
    }

    Role getRole() {
        return role;
    }

    String getPrivilege() {
        return privilege;
    }

}
