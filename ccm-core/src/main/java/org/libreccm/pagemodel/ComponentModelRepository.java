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

import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;

/**
 * Repository class for managing {@link ComponentModel} entities.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ComponentModelRepository extends AbstractEntityRepository<Long, ComponentModel> {

    @Override
    public Class<ComponentModel> getEntityClass() {
        return ComponentModel.class;
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

}
