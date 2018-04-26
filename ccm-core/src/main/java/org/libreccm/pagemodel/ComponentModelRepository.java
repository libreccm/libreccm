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
package org.libreccm.pagemodel;

import org.libreccm.core.AbstractEntityRepository;
import org.libreccm.core.CoreConstants;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;

import java.util.UUID;

import javax.persistence.EntityGraph;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Repository class for managing {@link ComponentModel} entities.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ComponentModelRepository
    extends AbstractEntityRepository<Long, ComponentModel> {

    private static final long serialVersionUID = -6358512316472857971L;

    @Override
    public Class<ComponentModel> getEntityClass() {
        return ComponentModel.class;
    }

    @Override
    public String getIdAttributeName() {
        return "componentModelId";
    }

    @Override
    public Long getIdOfEntity(final ComponentModel entity) {
        return entity.getComponentModelId();
    }

    @Override
    public boolean isNew(final ComponentModel componentModel) {
        return componentModel.getComponentModelId() == 0;
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void save(final ComponentModel componentModel) {
        super.save(componentModel);
    }

    /**
     * Initialises a new entity.
     *
     * @param componentModel The new {@link ComponentModel} entity to
     *                       initialise.
     */
    @Override
    public void initNewEntity(final ComponentModel componentModel) {

        final String uuid = UUID.randomUUID().toString();

        componentModel.setUuid(uuid);
        if (componentModel.getModelUuid() == null
                || componentModel.getModelUuid().isEmpty()) {
            componentModel.setModelUuid(uuid);
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public <M extends ComponentModel> Optional<M> findById(
        final long modelId, final Class<M> modelClass) {

        return Optional.ofNullable(getEntityManager().find(modelClass,
                                                           modelId));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public <M extends ComponentModel> Optional<M> findById(
        final long modelId,
        final Class<M> modelClass,
        final String entityGraphName) {

        return Optional.ofNullable(getEntityManager().find(modelClass,
                                                           modelId));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public <M extends ComponentModel> Optional<M> findById(
        final long entityId,
        final Class<M> modelClass,
        final EntityGraph<M> entityGraph) {

        Objects.requireNonNull(entityId);
        Objects.requireNonNull(entityGraph);

        final Map<String, Object> hints = new HashMap<>();
        hints.put(FETCH_GRAPH_HINT_KEY, entityGraph);
        return Optional.ofNullable(getEntityManager().find(modelClass,
                                                           entityId,
                                                           hints));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public <M extends ComponentModel> Optional<M> findById(
        final long entityId,
        final Class<M> modelClass,
        final String... fetchJoins) {

        Objects.requireNonNull(entityId);

        final CriteriaBuilder builder = getEntityManager()
            .getCriteriaBuilder();
        final CriteriaQuery<M> criteriaQuery = builder
            .createQuery(modelClass);
        final Root<M> from = criteriaQuery.from(modelClass);
        criteriaQuery.from(getEntityClass());
        criteriaQuery.select(from);
        for (final String fetchJoin : fetchJoins) {
            from.fetch(fetchJoin);
        }

        criteriaQuery
            .where(builder.equal(from.get(getIdAttributeName()), entityId));

        final TypedQuery<M> query = getEntityManager()
            .createQuery(criteriaQuery);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<ComponentModel> findComponentByContainerAndKey(
        final ContainerModel containerModel,
        final String componentKey) {

        final TypedQuery<ComponentModel> query = getEntityManager()
        .createNamedQuery("ComponentModel.findComponentByContainerAndKey", 
                          ComponentModel.class);
        query.setParameter("container", containerModel);
        query.setParameter("key", componentKey);
        
        try {
            return Optional.of(query.getSingleResult());
        } catch(NoResultException ex) {
            return Optional.empty();
        }
    }

}
