/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <K>
 * @param <E>
 */
public abstract class AbstractEntityRepository<K, E> {

    @Inject
    private transient EntityManager entityManager;

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public abstract Class<E> getEntityClass();

    public E findById(final K entityId) {
        return entityManager.find(getEntityClass(), entityId);
    }

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

    public abstract boolean isNew(final E entity);

    public void save(final E entity) {
        if (isNew(entity)) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    public void delete(final E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Can't delete a null entity.");
        }

        entityManager.remove(entity);
    }

}
