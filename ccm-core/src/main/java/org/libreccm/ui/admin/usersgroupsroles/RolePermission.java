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

import org.libreccm.security.Permission;

import java.util.Objects;

/**
 * Model friendly representation of a permission granted to a role.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class RolePermission implements Comparable<RolePermission> {

    private long permissionId;

    private String permissionUuid;

    private String grantedPrivilege;

    private String objectName;

    private boolean objectPermission;

    public RolePermission() {
        // Nothing
    }

    public RolePermission(final Permission permission) {
        permissionId = permission.getPermissionId();
        permissionUuid = permission.getUuid();
        grantedPrivilege = permission.getGrantedPrivilege();
        objectPermission = permission.getObject() != null;
        if (objectPermission) {
            objectName = permission.getObject().getDisplayName();
        }
    }

    public long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(final long permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionUuid() {
        return permissionUuid;
    }

    public void setPermissionUuid(final String permissionUuid) {
        this.permissionUuid = permissionUuid;
    }

    public String getGrantedPrivilege() {
        return grantedPrivilege;
    }

    public void setGrantedPrivilege(final String grantedPrivilege) {
        this.grantedPrivilege = grantedPrivilege;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(final String objectName) {
        this.objectName = objectName;
    }

    public boolean isObjectPermission() {
        return objectPermission;
    }

    public void setObjectPermission(final boolean objectPermission) {
        this.objectPermission = objectPermission;
    }

    @Override
    public int compareTo(final RolePermission other) {
        int result = Objects.compare(
            grantedPrivilege,
            Objects.requireNonNull(other).getGrantedPrivilege(),
            (privilege1, privilege2) -> privilege1.compareTo(privilege2)
        );
        if (result == 0 && isObjectPermission()) {
            return Objects.compare(
                objectName,
                Objects.requireNonNull(other).getObjectName(),
                (name1, name2) -> name1.compareTo(name2)
            );
        } else {
            return result;
        }
    }

}
