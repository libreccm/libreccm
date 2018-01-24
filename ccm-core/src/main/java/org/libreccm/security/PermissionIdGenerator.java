/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.security;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created on 3/23/17
 */
public class PermissionIdGenerator extends ObjectIdGenerator<String> {
    private static final long serialVersionUID = -8762745860290577542L;

    @Override
    public Class<?> getScope() {
        return Permission.class;
    }

    @Override
    public boolean canUseFor(final ObjectIdGenerator<?> gen) {
        return gen instanceof PermissionIdGenerator;
    }

    @Override
    public ObjectIdGenerator<String> forScope(final Class<?> scope) {
        return this;
    }

    @Override
    public ObjectIdGenerator<String> newForSerialization(final Object context) {
        return this;
    }

    @Override
    public IdKey key(final Object key) {
        if (key == null) {
            return null;
        }
        return new IdKey(Permission.class, Permission.class, key);
    }

    @Override
    public String generateId(final Object forPojo) {
        if (!(forPojo instanceof Permission)) {
            throw new IllegalArgumentException(
                    "Only Permission instances are supported.");
        }

        final Permission permission = (Permission) forPojo;

        if (permission.getGrantee() == null &&
            permission.getObject() == null) {
            throw new IllegalStateException();
        }

        return String.join("_",
                permission.getGrantedPrivilege(),
                String.valueOf(permission.getGrantee().getName()),
                String.valueOf(permission.getObject().getUuid())
        );
    }
}
