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
import javax.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.libreccm.core.CoreConstants.ACCESS_DENIED;


/**
 * A repository class for {@link CcmObject} entities.
 *
 * Please note that the {@code CcmObjectRepository} does not do any
 * authorisation checks. {@code CcmObjectRepository} can't do that because the
 * permissions are application specific. Checking permissions is the
 * responsibility of the developers which use this class. It is recommended the
 * create a repository class for each entity type which performs the
 * applications specific permission checks.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class CcmObjectRepository extends AbstractEntityRepository<Long, CcmObject> {

    private static final long serialVersionUID = 5033157795875521195L;
    
    @Override
    public Class<CcmObject> getEntityClass() {
        return CcmObject.class;
    }
    
    @Override
    public String getIdAttributeName() {
        return "objectId";
    }
    
    @Override
    public Long getIdOfEntity(final CcmObject entity) {
        return entity.getObjectId();
    }

    @Override
    public boolean isNew(final CcmObject entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Can't save null.");
        }

        if (ACCESS_DENIED.equals(entity.getDisplayName())) {
            throw new IllegalArgumentException(
                "Can't save the Access Denied object.");
        }

        return entity.getObjectId() == 0;
    }

    @Override
    public void initNewEntity(final CcmObject entity) {
        entity.setUuid(UUID.randomUUID().toString());
    }

    /**
     * Finds a {@link CcmObject} by its id.
     *
     * @param objectId The id of the item to find
     *
     * @return An optional either with the found item or empty
     */
    public Optional<CcmObject> findObjectById(final long objectId) {
        final TypedQuery<CcmObject> query = getEntityManager().createNamedQuery(
            "CcmObject.findById", CcmObject.class);
        query.setParameter("id", objectId);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }
    
    /**
     * Finds a {@link CcmObject} by its uuid.
     *
     * @param uuid The uuid of the item to find
     *
     * @return An optional either with the found item or empty
     */
    public Optional<CcmObject> findObjectByUuid(final String uuid) {
        final TypedQuery<CcmObject> query = getEntityManager().createNamedQuery(
            "CcmObject.findByUuid", CcmObject.class);
        query.setParameter("uuid", uuid);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void delete(final CcmObject object) {
        super.delete(object);
    }

}
