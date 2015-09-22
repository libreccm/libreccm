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
 *
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 */
public class ResourceRepository extends AbstractAuditedEntityRepository<Long, Resource> {

    @Override
    public Long getEntityId(Resource entity) {
        return null;
    }

    @Override
    public Class<Resource> getEntityClass() {
        return null;
    }

    @Override
    public boolean isNew(Resource entity) {
        return false;
    }
}
