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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
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
public abstract class AbstractEntityRepository<K, E> {

    private static final Logger LOGGER = LogManager.getLogger(
        AbstractEntityRepository.class);

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

    protected void applyDefaultEntityGraph(final TypedQuery<E> query) {
        if (getEntityClass().isAnnotationPresent(DefaultEntityGraph.class)) {
            LOGGER.debug("The following EntityGraphs are available for the "
                             + "entity class {}:",
                         getEntityClass().getName());
            getEntityManager().getEntityGraphs(getEntityClass()).stream()
                .forEach(g -> LOGGER.debug("\t{}", g.getName()));
            LOGGER.debug("Entity class {} has default entity graphs:",
                         getEntityClass().getName());
            LOGGER.debug("Applying entity graph {}",
                         getEntityClass().getAnnotation(
                             DefaultEntityGraph.class).value());
            query.setHint(FETCH_GRAPH_HINT_KEY,
                          entityManager.getEntityGraph(
                              getEntityClass().getAnnotation(
                                  DefaultEntityGraph.class).value()));
        }
    }

    /**
     * Helper method for retrieving a single result from a query.
     *
     * @param query The query from which the result is retrieved.
     *
     * @return A first result or the query or {@code null} of there is no
     *         result.
     */
    protected E getSingleResultOrNull(final TypedQuery<E> query) {
        final List<E> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
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
     * Finds an entity by it ID.
     *
     * @param entityId The ID of the entity to retrieve.
     *
     * @return The entity identified by the provided ID of {@code null} if there
     *         is no such entity.
     */
    public E findById(final K entityId) {
        if (getEntityClass().isAnnotationPresent(DefaultEntityGraph.class)) {
            return findById(entityId, getEntityClass().getAnnotation(
                            DefaultEntityGraph.class).value());
        } else {
            return entityManager.find(getEntityClass(), entityId);
        }
    }

    public E findById(final K entityId, final String entityGraphName) {
        @SuppressWarnings("unchecked")
        final EntityGraph<E> entityGraph = (EntityGraph<E>) entityManager.
            getEntityGraph(entityGraphName);
        return findById(entityId, entityGraph);
    }

    public E findById(final K entityId, final EntityGraph<E> entityGraph) {
        final Map<String, Object> hints = new HashMap<>();
        hints.put(FETCH_GRAPH_HINT_KEY, entityGraph);
        return entityManager.find(getEntityClass(), entityId, hints);
    }

    /**
     * Finds all instances of the entity of the type this repository is
     * responsible for.
     *
     * @return The list of entities in the database which are of the type
     *         provided by {@link #getEntityClass()}.
     */
    public List<E> findAll() {
        // We are using the Critiera API here because otherwise we can't 
        // pass the type of the entity dynmacially.
        return executeCriteriaQuery(createCriteriaQuery());
    }

    public List<E> findAll(final String entityGraphName) {
        @SuppressWarnings("unchecked")
        final EntityGraph<E> entityGraph = (EntityGraph<E>) entityManager
            .getEntityGraph(entityGraphName);

        return findAll(entityGraph);
    }

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
        if (hasDefaultEntityGraph()) {
            return executeCriteriaQuery(criteriaQuery, getDefaultEntityGraph());
        } else {
            final TypedQuery<E> query = entityManager.createQuery(criteriaQuery);
            return query.getResultList();
        }
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
     * @param entity The entity to init.
     */
    public void initNewEntity(final E entity) {
        //Empty default implementation
    }

    /**
     * Deletes an entity from the database.
     *
     * @param entity The entity to delete.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void delete(final E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Can't delete a null entity.");
        }
        
        //We need to make sure we use a none detached entity, therefore the merge
        entityManager.remove(entityManager.merge(entity));
    }

    protected boolean hasDefaultEntityGraph() {
        return getEntityClass().isAnnotationPresent(DefaultEntityGraph.class);
    }

    protected String getDefaultEntityGraph() {
        if (hasDefaultEntityGraph()) {
            return getEntityClass().getAnnotation(DefaultEntityGraph.class)
                .value();
        } else {
            throw new IllegalArgumentException(String.format(
                "Entity class \"%s\" has no DefaultEntityGraph!",
                getEntityClass().getName()));
        }
    }

}
