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
package org.libreccm.core;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class PermissionRepository
    extends AbstractEntityRepository<Long, Permission> {

    @Inject
    private EntityManager entityManager;

    @Override
    public Class<Permission> getEntityClass() {
        return Permission.class;
    }

    @Override
    public boolean isNew(final Permission entity) {
        if (entity == null) {
            throw new UnsupportedOperationException(
                "Entity to save can't be null");
        }
        return entity.getPermissionId() == 0;
    }

    public List<Permission> findPermissionsForSubject(final Subject subject) {
        if (subject == null) {
            throw new IllegalArgumentException(
                "Illegal value 'null' provided for parameter subject.");
        }

        final TypedQuery<Permission> query = entityManager.createNamedQuery(
            "findPermissionsForSubject", Permission.class);
        query.setParameter("subject", subject);

        return query.getResultList();
    }

    public List<Permission> findPermissionsForUser(final User user) {
        if (user == null) {
            throw new IllegalArgumentException(
                "Illegal value 'null' provided for parameter user");
        }

        final TypedQuery<Permission> query = entityManager.createNamedQuery(
            "findPermissionsForUser", Permission.class);
        query.setParameter("user", user);
        
        return query.getResultList();
    }

    public List<Permission> findPermissionsForCcmObject(final CcmObject object) {
        if (object == null) {
            throw new IllegalArgumentException(
                "Illegal value 'null' provided for parameter object.");
        }

        final TypedQuery<Permission> query = entityManager.createNamedQuery(
            "findPermissionsForCcmObject", Permission.class);
        query.setParameter("object", object);

        return query.getResultList();
    }

}
