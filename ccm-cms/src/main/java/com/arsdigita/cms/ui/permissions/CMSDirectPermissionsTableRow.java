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

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class CMSDirectPermissionsTableRow {
    
    private long granteeKey;
    
    private String granteeName;
    
    private boolean permitted;
    
    private UserPrivilegeKey userPrivilegeKey;

    public long getGranteeKey() {
        return granteeKey;
    }
    
    protected void setGranteeKey(final long granteeKey) {
        this.granteeKey = granteeKey;
    }
    
    public String getGranteeName() {
        return granteeName;
    }

    protected void setGranteeName(final String granteeName) {
        this.granteeName = granteeName;
    }

    public boolean isPermitted() {
        return permitted;
    }

    protected void setPermitted(final boolean permitted) {
        this.permitted = permitted;
    }

    public UserPrivilegeKey getUserPrivilegeKey() {
        return userPrivilegeKey;
    }

    protected void setUserPrivilegeKey(final UserPrivilegeKey userPrivilegeKey) {
        this.userPrivilegeKey = userPrivilegeKey;
    }
    
    
    
}
