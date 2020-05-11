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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

/**
 * A base class providing common method needed by every repository.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <K> Type of the primary key of the entity
 * @param <E> Type of the entity.
 */
public abstract class AbstractEntityRepository<K, E> implements Serializable {

    private static final long serialVersionUID = 8462548002420023652L;

    protected static final String FETCH_GRAPH_HINT_KEY
                                      = "javax.persistence.fetchgraph";

    /**
     * The {@link EntityManager} instance to use. Provided by the container via
     * CDI.
     */
    @Inject
    private EntityManager entityManager;

    /**
     * Getter method for retrieving the injected {@link EntityManager}.
     *
     * @return The {@code EntityManager} used by the repository.
     */
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Create an {@link EntityGraph} for the entity class of this repository.
     *
     * For more details about entity graphs/fetch graphs refer to the JPA
     * documentation. Internally this method uses
     * {@link EntityManager#createEntityGraph(java.lang.Class)}.
     *
     * @return An EntityGraph for this entity graph.
     */
    public EntityGraph<E> createEntityGraph() {
        return entityManager.createEntityGraph(getEntityClass());
    }

    /**
     * Helper method for retrieving a single result from a query. In contrast to
     * {@link #getSingleResultOrNull(javax.persistence.TypedQuery)} this method
     * return an {@link Optional} for the result.
     *
     * @param query The query from which the result is retrieved.
     *
     * @return An {@link Optional} instance wrapping the first result of the
     *         query. If there is no result the {@code Optional} is empty.
     */
    protected Optional<E> getSingleResult(final TypedQuery<E> query) {
        final List<E> result = query.getResultList();
        if (result.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(result.get(0));
        }
    }

    /**
     * Creates a mutable copy of a named entity graph which an be further
     * customised.
     *
     * Internally this method uses
     * {@link EntityManager#createEntityGraph(java.lang.String)}.
     *
     * @param entityGraphName The name of the named entity graph.
     *
     * @return A mutable copy of the named entity graph identified by the
     *         provided name or {@code null} if there is no such named entity
     *         graph.
     */
    @SuppressWarnings("unchecked")
    public EntityGraph<E> createEntityGraph(final String entityGraphName) {
        return (EntityGraph<E>) entityManager.createEntityGraph(
            entityGraphName);
    }

    /**
     * The class of entities for which this repository can be used. For creating
     * a repository class overwrite this method.
     *
     * @return The {@code Class} of the Entity which are managed by this
     *         repository.
     */
    public abstract Class<E> getEntityClass();

    /**
     * Used by some methods to create queries using the JPA Criteria API.
     *
     * @return The name of the ID attribute/property for entities managed by a
     *         repository.
     */
    public abstract String getIdAttributeName();

    /**
     * Get the primary key/id of a entity.
     *
     * @param entity The entity
     *
     * @return The ID of the provided {@code entity}.
     */
    public abstract K getIdOfEntity(E entity);

    /**
     * Finds an entity by it ID.
     *
     * @param entityId The ID of the entity to retrieve.
     *
     * @return An {@link Optional} containing the entity identified by the
     *         provided ID or am empty {@link Optional} if there is no such
     *         entity.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<E> findById(final K entityId) {

        Objects.requireNonNull(entityId);

        return Optional.ofNullable(entityManager.find(getEntityClass(),
                                                      entityId));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<E> findById(final K entityId, final String entityGraphName) {

        Objects.requireNonNull(entityId);
        Objects.requireNonNull(entityGraphName);

        @SuppressWarnings("unchecked")
        final EntityGraph<E> entityGraph = (EntityGraph<E>) entityManager.
            getEntityGraph(entityGraphName);
        return findById(entityId, entityGraph);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<E> findById(final K entityId,
                                final EntityGraph<E> entityGraph) {

        Objects.requireNonNull(entityId);
        Objects.requireNonNull(entityGraph);

        final Map<String, Object> hints = new HashMap<>();
        hints.put(FETCH_GRAPH_HINT_KEY, entityGraph);
        return Optional.ofNullable(entityManager.find(getEntityClass(),
                                                      entityId,
                                                      hints));
    }

    /**
     * Finds an entity by its ID and does fetch joins for the provided
     * attributes. Be careful with this method. Do not a too many attributes to
     * the fetch joins, only those which you really need. Otherwise there can be
     * a serious performance impact.
     *
     * @param entityId   The ID of the entity to retrieve.
     * @param fetchJoins The names of the attributes to join using fetch joins.
     *
     * @return An {@link Optional} containing the entity identified by the
     *         provided {@code entityId} or an empty {@link Optional} if there
     *         is no such entity.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<E> findById(final K entityId,
                                final String... fetchJoins) {

        Objects.requireNonNull(entityId);

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<E> criteriaQuery = builder
            .createQuery(getEntityClass());
        final Root<E> from = criteriaQuery.from(getEntityClass());
        criteriaQuery.from(getEntityClass());
        for (final String fetchJoin : fetchJoins) {
            from.fetch(fetchJoin);
        }

        criteriaQuery
            .where(builder.equal(from.get(getIdAttributeName()), entityId));

        final TypedQuery<E> query = entityManager.createQuery(criteriaQuery);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public E reload(final E entity, final String... fetchJoins) {

        return findById(getIdOfEntity(entity), fetchJoins)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Entity of type \"%s\" with ID %s in the database.",
                    getEntityClass().getName(),
                    Objects.toString(getIdOfEntity(entity)))));
    }

    /**
     * Finds all instances of the entity of the type this repository is
     * responsible for.
     *
     * @return The list of entities in the database which are of the type
     *         provided by {@link #getEntityClass()}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<E> findAll() {
        // We are using the Critiera API here because otherwise we can't 
        // pass the type of the entity dynmacially.
        return executeCriteriaQuery(createCriteriaQuery());
    }

    public List<E> findAll(final int limit, final int offset) {
        return executeCriteriaQuery(createCriteriaQuery(), limit, offset);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<E> findAll(final String entityGraphName) {
        @SuppressWarnings("unchecked")
        final EntityGraph<E> entityGraph = (EntityGraph<E>) entityManager
            .getEntityGraph(entityGraphName);

        return findAll(entityGraph);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<E> findAll(final EntityGraph<E> entityGraph) {
        // We are using the Critiera API here because otherwise we can't 
        // pass the type of the entity dynmacially.
        return executeCriteriaQuery(createCriteriaQuery(), entityGraph);
    }

    public CriteriaQuery<E> createCriteriaQuery() {
        final CriteriaBuilder criteriaBuilder = entityManager
            .getCriteriaBuilder();
        final CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(
            getEntityClass());
        final Root<E> root = criteriaQuery.from(getEntityClass());
        return criteriaQuery.select(root);
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return entityManager.getCriteriaBuilder();
    }

    public List<E> executeCriteriaQuery(final CriteriaQuery<E> criteriaQuery) {
        final TypedQuery<E> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<E> executeCriteriaQuery(
        final CriteriaQuery<E> criteriaQuery,
        final int limit,
        final int offset
    ) {
        return entityManager
            .createQuery(criteriaQuery)
            .setFirstResult(offset)
            .setMaxResults(limit)
            .getResultList();
    }

    public List<E> executeCriteriaQuery(final CriteriaQuery<E> criteriaQuery,
                                        final String graphName) {
        @SuppressWarnings("unchecked")
        final EntityGraph<E> entityGraph = (EntityGraph< E>) entityManager
            .getEntityGraph(
                graphName);
        return executeCriteriaQuery(criteriaQuery, entityGraph);
    }

    public List<E> executeCriteriaQuery(final CriteriaQuery<E> criteriaQuery,
                                        final EntityGraph<E> entityGraph) {
        final TypedQuery<E> query = entityManager.createQuery(criteriaQuery);
        query.setHint(FETCH_GRAPH_HINT_KEY, entityGraph);

        return query.getResultList();
    }

    /**
     * Used by {@link #save(java.lang.Object)} to determine if the provided
     * entity is a a new one.
     *
     * @param entity The entity to check.
     *
     * @return {@code true} if the entity is new (isn't in the database yet),
     *         {@code false} otherwise.
     */
    public abstract boolean isNew(final E entity);

    /**
     * Save a new or changed entity.
     *
     * @param entity The entity to save.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void save(final E entity) {

        Objects.requireNonNull(entity, "Can't save null.");

        if (isNew(entity)) {
            initNewEntity(entity);
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    /**
     * Overwrite this method to initialise new entities with default values. One
     * example is assigning a (random) UUID to new entity which implements the
     * {@link Identifiable} interface.
     *
     * @param entity The entity to initialise.
     */
    protected void initNewEntity(final E entity) {
        //Empty default implementation
    }

    /**
     * Deletes an entity from the database.
     *
     * @param entity The entity to delete.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void delete(final E entity) {

        Objects.requireNonNull(entity,
                               "Can't delete a null entity.");

        //We need to make sure we use a none detached entity, therefore the merge
        entityManager.remove(entityManager.merge(entity));
    }

}
