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
package org.libreccm.categorization;

import org.libreccm.core.AbstractEntityRepository;

import java.net.URI;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * A repository for executing CRUD operations on {@link Domain} objects.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class DomainRepository extends AbstractEntityRepository<Long, Domain> {

    @Inject
    private EntityManager entityManager;
    
    @Override
    public Class<Domain> getEntityClass() {
        return Domain.class;
    }

    @Override
    public boolean isNew(final Domain entity) {
        return entity.getObjectId() == 0;
    }

    /**
     * Find the {@link Domain} identified by the provided {@code domainKey}.
     *
     * @param domainKey The domain key of the {@code Domain} to find.
     *
     * @return The {@code Domain} identified by {@code domainKey} or
     *         {@code null} if there is no such {@code Domain}.
     */
    public Domain findByDomainKey(final String domainKey) {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

    /**
     * Find the {@link Domain} identified the provided {@code uri}.
     *
     * @param uri The URI of the domain to find.
     *
     * @return The {@code Domain} identified by the provided URI or {@code null}
     *         if there is so such {@code Domain}.
     */
    public Domain findByUri(final URI uri) {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

}
