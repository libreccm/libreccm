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
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.libreccm.core.CcmObjectRepository;
import org.libreccm.core.UnexpectedErrorException;

import javax.enterprise.context.RequestScoped;
import java.io.Serializable;
import java.util.Optional;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created on 3/23/17
 */
@RequestScoped
public class PermissionIdResolver implements Serializable, ObjectIdResolver {
    private static final long serialVersionUID = -8397366681202009916L;

    @Override
    public void bindItem(final ObjectIdGenerator.IdKey id,
                         final Object pojo) {
        // According to the Jackson JavaDoc, this method can be used to keep
        // track of objects directly in a resolver implementation. We don't need
        // this here therefore this method is empty.
    }

    @Override
    public Object resolveId(final ObjectIdGenerator.IdKey id) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final CcmObjectRepository ccmObjectRepository = cdiUtil
                .findBean(CcmObjectRepository.class);
        final RoleRepository roleRepository = cdiUtil
                .findBean(RoleRepository.class);
        final PermissionRepository permissionRepository = cdiUtil
                .findBean(PermissionRepository.class);

        String[] customPermId = id.key.toString().split("_");

        String privilege = customPermId[0];
        final String granteeName = customPermId[1];
        final String objectUuid = customPermId[2];

        final Optional<CcmObject> object = ccmObjectRepository
                .findObjectByUuid(objectUuid);
        final Optional<Role> grantee = roleRepository
                .findByName(granteeName);
        if (!grantee.isPresent()) {
            throw new UnexpectedErrorException(String.format(
                    "Role with id \"%s\" was not found in the database," +
                            " but has been associated with a permission.",
                    granteeName));
        }
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
