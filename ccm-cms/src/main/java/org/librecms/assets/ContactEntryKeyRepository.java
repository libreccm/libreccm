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
public class ContactEntryKeyRepository 
    extends AbstractEntityRepository<Long, ContactEntryKey>{ 

    private static final long serialVersionUID = 1L;

    @Override
    public Class<ContactEntryKey> getEntityClass() {
        
        return ContactEntryKey.class;
    }

    @Override
    public String getIdAttributeName() {
        
        return "keyId";
    }

    @Override
    public Long getIdOfEntity(final ContactEntryKey entity) {
        
        return entity.getKeyId();
    }

    @Override
    public boolean isNew(final ContactEntryKey entity) {
        
        return entity.getKeyId() == 0;
    }
    
}
