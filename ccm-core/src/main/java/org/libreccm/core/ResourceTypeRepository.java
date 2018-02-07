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

/**
 * A repository for executing CRUD operations on {@link ResourceType} objects.
 *
 * Note: This repository does the permission checks when retrieving
 * {@link ResourceType}s from the database. This is the responsibility of the
 * application using the {@link ResourceType}.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * 
 */
@RequestScoped
public class ResourceTypeRepository
    extends AbstractEntityRepository<Long, ResourceType> {

    private static final long serialVersionUID = -6313169146990554867L;

    @Override
    public Class<ResourceType> getEntityClass() {
        return ResourceType.class;
    }

    @Override
    public String getIdAttributeName() {
        return "resourceTypeId";
    }
    
    @Override
    public Long getIdOfEntity(final ResourceType entity) {
        return entity.getResourceTypeId();
    }

    @Override
    public boolean isNew(final ResourceType entity) {
        return entity.getTitle() == null;
    }

    /**
     * Finds a {@link ResourceType} by its title.
     *
     * @param title The title of the item to find
     *
     * @return An optional either with the found item or empty
     */
    public Optional<ResourceType> findByTitle(final String title) {
        final TypedQuery<ResourceType> query = getEntityManager()
            .createNamedQuery("ResourceType.findByTitle", ResourceType.class);
        query.setParameter("title", title);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

}
