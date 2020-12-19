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
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityGraph;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * A repository for executing CRUD operations on {@link Domain} objects.
 *
 * Note: This repository does the permission checks when retrieving
 * {@link Domain}s from the database. This is the responsibility of the
 * application using the {@link Domain}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class DomainRepository extends AbstractEntityRepository<Long, Domain> {

    private static final long serialVersionUID = -924664711844102643L;

    @Override
    public Class<Domain> getEntityClass() {
        return Domain.class;
    }

    @Override
    public String getIdAttributeName() {
        return "objectId";
    }

    @Override
    public Long getIdOfEntity(final Domain entity) {
        return entity.getObjectId();
    }

    @Override
    public boolean isNew(final Domain entity) {
        return entity.getObjectId() == 0;
    }

    @Override
    public void initNewEntity(final Domain domain) {
        domain.setUuid(UUID.randomUUID().toString());
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Domain> findAll() {
        final TypedQuery<Domain> query = getEntityManager()
            .createNamedQuery("Domain.findAll", Domain.class);
        return query.getResultList();
    }

    /**
     * Find the {@link Domain} identified by the provided {@code domainKey}.
     *
     * @param domainKey The domain key of the {@code Domain} to find.
     *
     * @return The {@code Domain} identified by {@code domainKey} or
     *         {@code null} if there is no such {@code Domain}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<Domain> findByDomainKey(final String domainKey) {
        final TypedQuery<Domain> query = getEntityManager()
            .createNamedQuery("Domain.findByKey", Domain.class);
        query.setParameter("key", domainKey);

        final EntityGraph<?> graph = getEntityManager()
            .getEntityGraph("Domain.allCategories");
        query.setHint("javax.persistence.fetchgraph", graph);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    /**
     * Find the {@link Domain} identified the provided {@code uri}.
     *
     * @param uri The URI of the domain to find.
     *
     * @return The {@code Domain} identified by the provided URI or {@code null}
     *         if there is so such {@code Domain}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Domain findByUri(final URI uri) {
        final TypedQuery<Domain> query = getEntityManager()
            .createNamedQuery("Domain.findByUri", Domain.class);
        query.setParameter("uri", uri);

        return query.getSingleResult();
    }

    /**
     * Finds a {@link Domain} by its uuid.
     *
     * @param uuid The uuid of the item to find
     *
     * @return An optional either with the found item or empty
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<Domain> findByUuid(final String uuid) {
        try {
            return Optional.of(
                getEntityManager()
                    .createNamedQuery("Domain.findByUuid", Domain.class)
                    .setParameter("uuid", uuid)
                    .getSingleResult()
            );
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<Domain> findByRootCategory(final Category rootCategory) {
        try {
            return Optional.of(
                getEntityManager()
                    .createNamedQuery(
                        "Domain.findByRootCategory", Domain.class
                    )
                    .setParameter("root", rootCategory)
                    .getSingleResult()
            );
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Domain> search(final String term) {
        final TypedQuery<Domain> query = getEntityManager()
            .createNamedQuery("Domain.search", Domain.class);
        query.setParameter("term", term);
        final EntityGraph<?> graph = getEntityManager()
            .getEntityGraph("Domain.withOwners");
        query.setHint("javax.persistence.fetchgraph", graph);

        return query.getResultList();
    }

    @AuthorizationRequired
    @RequiresPrivilege(CategorizationConstants.PRIVILEGE_MANAGE_DOMAINS)
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void save(final Domain domain) {
        super.save(domain);
    }

    @AuthorizationRequired
    @RequiresPrivilege(CategorizationConstants.PRIVILEGE_MANAGE_DOMAINS)
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void delete(final Domain domain) {
        super.delete(domain);
    }

}
