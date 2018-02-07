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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * Repository class for retrieving, storing and deleting {@code BlobObject}s.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 20/01/2016
 */
@RequestScoped
public class BlobObjectRepository extends
    AbstractAuditedEntityRepository<Long, BlobObject> {

    private static final long serialVersionUID = 35679591875538616L;

    @Inject
    private EntityManager entityManager;

    @Override
    public Long getEntityId(BlobObject entity) {
        return entity.getBlobObjectId();
    }

    @Override
    public String getIdAttributeName() {
        return "blobObjectId";
    }

    @Override
    public Long getIdOfEntity(final BlobObject entity) {
        return entity.getBlobObjectId();
    }

    @Override
    public Class<BlobObject> getEntityClass() {
        return BlobObject.class;
    }

    @Override
    public boolean isNew(BlobObject entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity to save can't be null.");
        }
        return entity.getBlobObjectId() == 0;
    }

}
