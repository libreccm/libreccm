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


import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * A base class providing common method needed by every repository. 
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <K> Type of the primary key of the entity
 * @param <E> Type of the entity.
 */
public abstract class AbstractEntityRepository<K, E> {

    /**
     * The {@link EntityManager} instance to use. Provided by the container via
     * CDI.
     */
    @Inject
    private transient EntityManager entityManager;

    /**
     * Getter method for retrieving the injected {@link EntityManager}.
     * 
     * @return The {@code EntityManager} used by the repository.
     */
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * The class of entities for which this repository can be used. 
     * For creating a repository class overwrite this method.
     * 
     * @return The {@code Class} of the Entity which are managed by this 
     * repository.
     */
    public abstract Class<E> getEntityClass();

    /**
     * Finds an entity by it ID.
     * 
     * @param entityId The ID of the entity to retrieve.
     * 
     * @return The entity identified by the provided ID of {@code null} if there
     * is no such entity.
     */
    public E findById(final K entityId) {
        return entityManager.find(getEntityClass(), entityId);
    }

    /**
     * Finds all instances of the entity of the type this repository is 
     * responsible for. 
     * 
     * @return The list of entities in the database which are of the type 
     * provided by {@link #getEntityClass()}.
     */
    public List<E> findAll() {
        // We are using the Critiera API here because otherwise we can't 
        // pass the type of the entity dynmacially.
        final CriteriaBuilder criteriaBuilder = entityManager
            .getCriteriaBuilder();
        final CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(
            getEntityClass());
        final Root<E> root = criteriaQuery.from(getEntityClass());
        criteriaQuery.select(root);

        final TypedQuery<E> query = entityManager.createQuery(criteriaQuery);

        return query.getResultList();
    }

    /**
     * Used by {@link #save(java.lang.Object)} to determine if the provided 
     * entity is a a new one.
     * 
     * @param entity The entity to check.
     * @return {@code true} if the entity is new (isn't in the database yet), 
     * {@code false} otherwise.
     */
    public abstract boolean isNew(final E entity);

    /**
     * Save a new or changed entity.
     * 
     * @param entity The entity to save.
     */
    public void save(final E entity) {
        if (isNew(entity)) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    /**
     * Deletes an entity from the database.
     * 
     * @param entity The entity to delete.
     */
    public void delete(final E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Can't delete a null entity.");
        }

        entityManager.remove(entity);
    }

}
