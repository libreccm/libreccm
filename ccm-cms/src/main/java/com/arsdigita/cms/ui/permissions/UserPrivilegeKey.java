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
 * Utility class to encode a user privilege in the bebop table.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
final class UserPrivilegeKey {

    private final String objectId;
    private final String granteeId;
    private final String privilege;
    private final boolean granted;

    public UserPrivilegeKey(final Long objectId,
                            final Long granteeId,
                            final String privilege,
                            final boolean granted) {
        this.objectId = objectId.toString();
        this.granteeId = granteeId.toString();
        this.privilege = privilege;
        this.granted = granted;
    }

    @Override
    public String toString() {
        return String.format("%s{ %s }",
                             super.toString(),
                             String.join(".", privilege,
                                         objectId,
                                         granteeId,
                                         Boolean.toString(granted)));
    }

    /**
     * Decodes the information in a key into the helper class
     *
     * @see PermissionStatus
     */
    static PermissionStatus undescribe(final String key) {

        final int i = key.indexOf(".");
        final int j = key.indexOf(".", i + 1);
        final int k = key.lastIndexOf(".");

        final String privilege = key.substring(0, i);
        final Long oID = Long.parseLong(key.substring(i + 1, j));
        final Long gID = Long.parseLong(key.substring(j + 1, k));

        boolean granted = false;
        final CMSUserObjectStruct uos;
        try {
            granted = Boolean.parseBoolean(key.substring(k + 1, k + 2));
            uos = new CMSUserObjectStruct(gID, oID);
        } catch (NumberFormatException ex) {
            // cannot decode
            throw new IllegalArgumentException(ex.getMessage());
        }

        return new PermissionStatus(privilege,
                                    uos.getObject(),
                                    uos.getRole(),
                                    granted);
    }

}
