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
public class ContactEntryRepository
    extends AbstractEntityRepository<Long, ContactEntry> {

    private static final long serialVersionUID = 1L;

    @Override
    public Class<ContactEntry> getEntityClass() {
        return ContactEntry.class;
    }

    @Override
    public String getIdAttributeName() {
        return "contactEntryId";
    }

    @Override
    public Long getIdOfEntity(final ContactEntry entity) {

        return entity.getContactEntryId();
    }

    @Override
    public boolean isNew(final ContactEntry entity) {

        return entity.getContactEntryId() == 0;
    }

}
