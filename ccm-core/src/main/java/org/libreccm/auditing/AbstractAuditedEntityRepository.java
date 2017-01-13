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
package org.libreccm.auditing;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.exception.NotAuditedException;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.libreccm.core.AbstractEntityRepository;

import javax.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @param <K> Primary key of the entity.
 * @param <T> Type of the entity
 */
public abstract class AbstractAuditedEntityRepository<K, T>
    extends AbstractEntityRepository<K, T> {

    @Inject
    private AuditReader auditReader;

    public abstract K getEntityId(final T entity);

    /**
     * Retrieves an entity at specific revision. The entity will be in the state
     * as it was when the revision was created.
     *
     * @param entity
     * @param revision
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public T retrieveEntityAtRevision(final T entity, final Number revision) {
        final AuditQuery query = auditReader.createQuery()
            .forEntitiesAtRevision(getEntityClass(), revision);
        query.add(AuditEntity.id().eq(getEntityId(entity)));

        final Object result = query.getSingleResult();

        if (getEntityClass().isInstance(result)) {
            return (T) result;
        } else {
            throw new AuditQueryException(String.format(
                "The result is not an instance of the entity class. "
                    + "Entity class: \"%s\"; Result class: \"%s\"",
                getEntityClass().getName(),
                result.getClass().getName())
            );
        }
    }

    /**
     * Retrieves the number of revisions for a given entity.
     *
     * @param entity   The entity
     * @param objectId The primary key
     *
     * @return A list of revision numbers, at which the entity was modified,
     *         sorted in ascending order (so older revisions come first).
     *
     * @throws NotAuditedException      When entities of the given class are not
     *                                  audited.
     * @throws IllegalArgumentException If cls or primaryKey is null.
     * @throws IllegalStateException    If the associated entity manager is
     *                                  closed.
     */
    public List<Number> retrieveRevisionNumbersOfEntity(final T entity,
                                                        final Long objectId) {
        return auditReader.getRevisions(entity.getClass(), objectId);
    }

    /**
     * Retrieves the first revision of the given entity.
     *
     * @param entity   The entity.
     * @param objectId The primary key of the entity.
     *
     * @return The first revision of the entity.
     */
    public CcmRevision retrieveFirstRevision(final T entity,
                                             final Long objectId) {
        final List<Number> revisions = retrieveRevisionNumbersOfEntity(
            entity, objectId);

        return auditReader.findRevision(CcmRevision.class, revisions.get(0));
    }

    /**
     * Retrieves all revisions of the given entity. The list of revisions is
     * ordered from the oldest revision to the newest revision.
     *
     * @param entity   The entity.
     * @param objectId The primary key of the entity.
     *
     * @return A list of all revisions of the provided entity.
     */
    public List<CcmRevision> retrieveRevisions(final T entity,
                                               final Long objectId) {
        final List<Number> revisionNumbers = retrieveRevisionNumbersOfEntity(
            entity, objectId);

        return revisionNumbers.stream()
            .map(revisionNumber -> auditReader.findRevision(CcmRevision.class,
                                                            revisionNumber))
            .collect(Collectors.toList());
    }

    /**
     * Retrieves the current revision of an entity.
     *
     * @param entity   The entity.
     * @param objectId the primary key the entity.
     *
     * @return The most current revision of the entity.
     */
    public CcmRevision retrieveCurrentRevision(final T entity,
                                               final Long objectId) {
        final List<Number> revisions = retrieveRevisionNumbersOfEntity(
            entity, objectId);
        final Number lastRevision = revisions.get(revisions.size() - 1);

        return auditReader.findRevision(CcmRevision.class, lastRevision);
    }

    /**
     * Retrieves a specific revision object.
     *
     * @param entity
     * @param objectId
     * @param revision
     *
     * @return
     */
    public CcmRevision retrieveRevision(final Number revision) {
        return auditReader.findRevision(CcmRevision.class, revision);
    }

}
