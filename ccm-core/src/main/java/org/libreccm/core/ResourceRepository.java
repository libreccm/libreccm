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

import javax.enterprise.context.RequestScoped;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Optional;
import java.util.UUID;

/**
 * A repository for executing CRUD operations on {@link Resource} objects.
 *
 * Note: This repository does the permission checks when retrieving
 * {@link Resource}s from the database. This is the responsibility of the
 * application using the {@link Resource}.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 8/10/17
 */
@RequestScoped
public class ResourceRepository extends AbstractEntityRepository<Long,
        Resource> {

    private static final long serialVersionUID = 4593206445936878071L;

    @Override
    public Class<Resource> getEntityClass() {
        return Resource.class;
    }

    @Override
    public boolean isNew(final Resource entity) {
        return entity.getObjectId() == 0;
    }

    @Override
    public void initNewEntity(final Resource resource) {
        resource.setUuid(UUID.randomUUID().toString());
    }

    /**
     * Finds a {@link Resource} by its uuid.
     *
     * @param uuid The uuid of the item to find
     *
     * @return An optional either with the found item or empty
     */
    public Optional<Resource> findByUuid(final String uuid) {
        final TypedQuery<Resource> query = getEntityManager()
                .createNamedQuery("Resource.findByUuid", Resource.class);
        query.setParameter("uuid", uuid);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }
}
