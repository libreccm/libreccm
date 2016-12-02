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

import java.util.UUID;

import javax.enterprise.context.RequestScoped;

/**
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

    @Override
    public void initNewEntity(final ComponentModel componentModel) {
        componentModel.setUUid(UUID.randomUUID().toString());
    }
    
}
