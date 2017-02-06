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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class CMSPermissionsTableRow {

    private String roleName;

    private List<CMSPermissionsTableColumn> columns;

    public String getRoleName() {
        return roleName;
    }

    protected void setRoleName(final String roleName) {
        this.roleName = roleName;
    }

    public List<CMSPermissionsTableColumn> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    protected void setColumns(final List<CMSPermissionsTableColumn> columns) {
        this.columns = columns;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(roleName);
        hash = 79 * hash + Objects.hashCode(columns);
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
        if (!(obj instanceof CMSPermissionsTableRow)) {
            return false;
        }
        final CMSPermissionsTableRow other = (CMSPermissionsTableRow) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(roleName, other.getRoleName())) {
            return false;
        }
        if (Objects.equals(columns, other.getColumns())) {
        } else {
            return false;
        }
        return true;
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof CMSPermissionsTableRow;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "roleName = \"%s\", "
                                 + "columns = %s%s"
                                 + " }",
                             super.toString(),
                             roleName,
                             Objects.toString(columns),
                             data);
    }

}
