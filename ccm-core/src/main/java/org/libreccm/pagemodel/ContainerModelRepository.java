/*
 * Copyright (C) 2018 LibreCCM Foundation.
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

import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContainerModelRepository
    extends AbstractEntityRepository<Long, ContainerModel> {

    private static final long serialVersionUID = 6613005988522263867L;

    @Override
    public Class<ContainerModel> getEntityClass() {
        return ContainerModel.class;
    }

    @Override
    public String getIdAttributeName() {
        return "containerId";
    }

    @Override
    public Long getIdOfEntity(final ContainerModel container) {
        return container.getContainerId();
    }

    @Override
    public boolean isNew(final ContainerModel container) {
        return container.getContainerId() == 0;
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void save(final ContainerModel container) {

        super.save(container);
    }

    @Override
    public void initNewEntity(final ContainerModel container) {

        final String uuid = UUID.randomUUID().toString();
        container.setUuid(uuid);

        if (container.getContainerUuid() == null
                || container.getContainerUuid().isEmpty()) {

            container.setContainerUuid(uuid);
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<ContainerModel> findContainerByKeyAndPageModel(
        final String key, final PageModel pageModel) {

        final TypedQuery<ContainerModel> query = getEntityManager()
            .createNamedQuery("ContainerModel.findByKeyAndPage",
                              ContainerModel.class);
        query.setParameter("key", key);
        query.setParameter("pageModel", pageModel);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

}
