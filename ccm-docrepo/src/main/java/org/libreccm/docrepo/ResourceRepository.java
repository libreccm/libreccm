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
package org.libreccm.docrepo;


import org.libreccm.auditing.AbstractAuditedEntityRepository;

/**
 * Repository class for retrieving, storing and deleting {@code Resource}s.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 */
public class ResourceRepository extends AbstractAuditedEntityRepository<Long, Resource> {

    @Override
    public Long getEntityId(Resource entity) {
        return entity.getObjectId();
    }

    @Override
    public Class<Resource> getEntityClass() {
        return Resource.class;
    }

    @Override
    public boolean isNew(Resource entity) {
        return false;
    }


}
