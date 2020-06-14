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
package org.librecms.lifecycle;

import org.libreccm.core.AbstractEntityRepository;

import java.util.UUID;

import javax.enterprise.context.RequestScoped;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class LifecycleRepository
    extends AbstractEntityRepository<Long, Lifecycle> {

    private static final long serialVersionUID = 1L;

    @Override
    public Class<Lifecycle> getEntityClass() {
        return Lifecycle.class;
    }

    @Override
    public String getIdAttributeName() {
        return "lifecycleId";
    }

    @Override
    public Long getIdOfEntity(final Lifecycle entity) {
        return entity.getLifecycleId();
    }

    @Override
    public boolean isNew(final Lifecycle lifecycle) {
        return lifecycle.getLifecycleId() == 0;
    }

    @Override
    protected void initNewEntity(final Lifecycle entity) {
        super.initNewEntity(entity);
        entity.setUuid(UUID.randomUUID().toString());
    }

}
