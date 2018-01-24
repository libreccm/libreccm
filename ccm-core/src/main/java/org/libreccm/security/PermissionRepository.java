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

import org.libreccm.core.AbstractEntityRepository;

import javax.enterprise.context.RequestScoped;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Optional;

/**
 * A repository class for {@link Permission}.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created on 3/29/17
 */
@RequestScoped
public class PermissionRepository
        extends AbstractEntityRepository<Long, Permission> {

    private static final long serialVersionUID = -4240674229117593486L;

    @Override
    public Class<Permission> getEntityClass() {
        return Permission.class;
    }

    @Override
    public boolean isNew(Permission entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Can't save null");
        }
        return entity.getPermissionId() == 0;
    }

    /**
     * Finds a {@link Permission} by the privilege, the grantee and the
     * object. Where the grantee has been granted the given privilege on the
     * given object.
     *
     * @param privilege The privilege, beeing granted
     * @param grantee The grantee, having the privilege
     * @param object The object, the privilege has been granted on
     *
     * @return An optional either with the found item or empty
     */
    public Optional<Permission> findByCustomPermId(final String privilege,
                                                   final Role grantee,
                                                   final Object object) {
        final TypedQuery<Permission> query = getEntityManager().createNamedQuery(
                "Permission.findByCustomPermId", Permission.class);
        query.setParameter("privilege", privilege);
        query.setParameter("grantee", grantee);
        if (object != null)
            query.setParameter("object", object);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }
}
