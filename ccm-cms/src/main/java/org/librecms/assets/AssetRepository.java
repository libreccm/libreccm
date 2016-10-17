/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.librecms.assets;

import org.libreccm.auditing.AbstractAuditedEntityRepository;

import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class AssetRepository
    extends AbstractAuditedEntityRepository<Long, Asset> {

    @Inject
    private EntityManager entityManager;

    @Override
    public Long getEntityId(final Asset asset) {
        return asset.getObjectId();
    }

    @Override
    public Class<Asset> getEntityClass() {
        return Asset.class;
    }

    @Override
    public boolean isNew(final Asset asset) {
        return asset.getObjectId() == 0;
    }

    public Optional<Asset> findByUuid(final String uuid) {
        final TypedQuery<Asset> query = entityManager.createNamedQuery(
            "Asset.findByUuid", Asset.class);
        query.setParameter("uuid", uuid);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public Optional<Asset> findByUuidAndType(
        final String uuid, final Class<? extends Asset> clazz) {

        final TypedQuery<Asset> query = entityManager.createNamedQuery(
            "Asset.findByUuidAndType", Asset.class);
        query.setParameter("uuid", uuid);
        query.setParameter("type", clazz.getName());

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public Optional<Asset> findByType(
        final String uuid, final Class<? extends Asset> clazz) {

        final TypedQuery<Asset> query = entityManager.createNamedQuery(
            "Asset.findByType", Asset.class);
        query.setParameter("type", clazz.getName());

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

}
