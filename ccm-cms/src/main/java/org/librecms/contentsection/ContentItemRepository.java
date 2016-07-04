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
package org.librecms.contentsection;

import org.libreccm.auditing.AbstractAuditedEntityRepository;
import org.libreccm.core.CcmObject;
import org.libreccm.core.CcmObjectRepository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContentItemRepository 
    extends AbstractAuditedEntityRepository<Long, ContentItem>{

    @Inject
    private CcmObjectRepository ccmObjectRepo; 
    
    @Override
    public Long getEntityId(final ContentItem item) {
        return item.getObjectId();
    }

    @Override
    public Class<ContentItem> getEntityClass() {
        return ContentItem.class;
    }

    @Override
    public boolean isNew(final ContentItem item) {
        return ccmObjectRepo.isNew(item);
    }
    
    public ContentItem findById(final long itemId) {
        final CcmObject result = ccmObjectRepo.findObjectById(itemId);
        if (result instanceof ContentItem) {
            return (ContentItem) result;
        } else {
            return null;
        }
    }
    
    public ContentItem findByUuid(final String uuid) {
        final CcmObject result = ccmObjectRepo.findObjectByUuid(uuid);
        if (result instanceof ContentItem) {
            return (ContentItem) result;
        } else {
            return null;
        }
    }
    
    //ToDo: Methods for finding items by name, path, content type etc.
    
}
