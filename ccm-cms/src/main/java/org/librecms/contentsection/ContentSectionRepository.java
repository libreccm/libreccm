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

import org.libreccm.core.AbstractEntityRepository;
import org.libreccm.core.CoreConstants;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;

import javax.enterprise.context.RequestScoped;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContentSectionRepository
    extends AbstractEntityRepository<Long, ContentSection> {

    public ContentSection findByLabel(final String label) {
        if (label == null || label.isEmpty()) {
            throw new IllegalArgumentException(
                "The label of a ContentSection can't be empty.");
        }
        
        final TypedQuery<ContentSection> query = getEntityManager()
            .createNamedQuery("ContentSection.findByLabel", 
                              ContentSection.class);
        query.setParameter("label", label);
        
        return query.getSingleResult();
    }

    @Override
    public Class<ContentSection> getEntityClass() {
        return ContentSection.class;
    }

    @Override
    public boolean isNew(final ContentSection section) {
        return section.getObjectId() == 0;
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.ADMIN_PRIVILEGE)
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void save(final ContentSection section) {
        super.save(section);
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.ADMIN_PRIVILEGE)
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void delete(final ContentSection section) {
        super.delete(section);
    }

}
