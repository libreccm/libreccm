/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libreccm.core;

import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class AbstractEntityRepository<K, E> {
    
    @Inject
    private transient EntityManager entityManager;
    
    protected EntityManager getEntityManager() {
        return entityManager;
    }
    
    public abstract Class<E> getEntityClass();
    
    public abstract boolean isNew(final E entity);
    
    public void save(final E entity) {
        if (isNew(entity)) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }
    
    public E findById(final K entityId) {
        return entityManager.find(getEntityClass(), entityId);
    }
    
    public abstract List<E> findAll();
}
