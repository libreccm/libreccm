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

import java.util.Collections;
import java.util.List;


/**
 * Model for displaying the privileges granted to a role.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class PrivilegesGrantedToRoleModel {

    /**
     * The name of the role.
     */
    private String grantee;

    /**
     * The privileges granted to the role.
     */
    private List<GrantedPrivilegeModel> grantedPrivileges;

    public String getGrantee() {
        return grantee;
    }

    public void setGrantee(final String grantee) {
        this.grantee = grantee;
    }

    public List<GrantedPrivilegeModel> getGrantedPrivileges() {
        return Collections.unmodifiableList(grantedPrivileges);
    }

    public void setGrantedPrivileges(
        final List<GrantedPrivilegeModel> grantedPrivileges
    ) {
        this.grantedPrivileges = grantedPrivileges;
    }

}
