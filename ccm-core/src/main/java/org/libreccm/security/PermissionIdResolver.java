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
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import org.libreccm.core.CcmObject;
import org.libreccm.core.CcmObjectRepository;
import org.libreccm.core.UnexpectedErrorException;

import javax.inject.Inject;
import java.util.Optional;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created on 3/23/17
 */
public class PermissionIdResolver implements ObjectIdResolver {
    @Inject
    private PermissionRepository permissionRepository;
    @Inject
    private RoleRepository roleRepository;
    @Inject
    private CcmObjectRepository ccmObjectRepository;

    @Override
    public void bindItem(final ObjectIdGenerator.IdKey id,
                         final Object pojo) {
        // According to the Jackson JavaDoc, this method can be used to keep
        // track of objects directly in a resolver implementation. We don't need
        // this here therefore this method is empty.
    }

    @Override
    public Object resolveId(final ObjectIdGenerator.IdKey id) {
        String[] customPermId = id.key.toString().split("_");

        String privilege = customPermId[0];
        final long granteeId = Long.getLong(customPermId[1]);
        final long objectId = Long.getLong(customPermId[2]);

        final Optional<Role> grantee = roleRepository.findById(granteeId);
        if (!grantee.isPresent()) {
            throw new UnexpectedErrorException(String.format(
                    "Role with id \"%s\" was not found in the database," +
                    " but has been associated with a permission.",
                    granteeId));
        }
        final Optional<CcmObject> object = ccmObjectRepository.findObjectById
                (objectId);
        Optional<Permission> permission = permissionRepository
                            .findByCustomPermId(privilege,
                                                grantee.get(),
                                                object.orElse(null));
        if (!permission.isPresent()) {
            throw new UnexpectedErrorException(String.format(
                    "Permission with privilege \"%s\", grantee \"%s and " +
                    "object \"%s\" was not found in the database.",
                    privilege, grantee.toString(), object.toString()));
        }

        return permission.get();
    }

    @Override
    public ObjectIdResolver newForDeserialization(final Object context) {
        return new PermissionIdResolver();
    }

    @Override
    public boolean canUseFor(final ObjectIdResolver resolverType) {
        return resolverType instanceof PermissionIdResolver;
    }
}
