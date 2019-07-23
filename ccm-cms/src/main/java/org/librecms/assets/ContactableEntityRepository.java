/*
 * Copyright (C) 2019 LibreCCM Foundation.
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
package org.librecms.assets;

import org.libreccm.core.AbstractEntityRepository;

import javax.enterprise.context.RequestScoped;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContactableEntityRepository 
    extends AbstractEntityRepository<Long, ContactableEntity>{

    private static final long serialVersionUID = 1L;

    @Override
    public Class<ContactableEntity> getEntityClass() {
        return ContactableEntity.class;
    }

    @Override
    public String getIdAttributeName() {
        return "objectId";
    }

    @Override
    public Long getIdOfEntity(final ContactableEntity entity) {
        
        return entity.getObjectId();
    }

    @Override
    public boolean isNew(final ContactableEntity entity) {
        
        return entity.getObjectId() == 0;
    }
    
}
