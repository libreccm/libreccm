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

/**
 * Model for displaying the privileges granted to a user or a role.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class GrantedPrivilegeModel {

    /**
     * The privilege.
     */
    private String privilege;

    /**
     * Is the privilege granted?
     */
    private boolean granted;

    /**
     * The the permission granting the privilege inherited from another object?
     */
    private boolean inherited;

    public String getPrivilege() {
        return privilege;
    }

    protected void setPrivilege(final String privilege) {
        this.privilege = privilege;
    }

    public boolean isGranted() {
        return granted;
    }

    protected void setGranted(final boolean granted) {
        this.granted = granted;
    }

    public boolean isInherited() {
        return inherited;
    }

    protected void setInherited(final boolean inherited) {
        this.inherited = inherited;
    }

}
