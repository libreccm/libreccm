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

import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class CMSPermissionsTableColumn {

    private String privilege;
    private boolean permitted;

    public String getPrivilege() {
        return privilege;
    }

    protected void setPrivilege(final String privilege) {
        this.privilege = privilege;
    }

    public boolean isPermitted() {
        return permitted;
    }

    protected void setPermitted(final boolean permitted) {
        this.permitted = permitted;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(privilege);
        hash = 83 * hash + (permitted ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CMSPermissionsTableColumn)) {
            return false;
        }
        final CMSPermissionsTableColumn other
                                            = (CMSPermissionsTableColumn) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (permitted != other.isPermitted()) {
            return false;
        }
        return Objects.equals(privilege, other.getPrivilege());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof CMSPermissionsTableColumn;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "privilege = \"%s\", "
                                 + "permitted = %b%s"
                                 + " }",
                             super.toString(),
                             privilege,
                             permitted,
                             data);
    }

}
