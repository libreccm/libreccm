/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.librecms.pages;

import org.libreccm.categorization.Category;
import org.libreccm.core.AbstractEntityRepository;
import org.libreccm.core.CoreConstants;
import org.libreccm.security.RequiresPrivilege;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class PageRepository extends AbstractEntityRepository<Long, Page>{

    public Optional<Page> findPageForCategory(final Category category) {
        
        final TypedQuery<Page> query = getEntityManager()
        .createNamedQuery("Page.findForCategory", Page.class);
        query.setParameter("category", category);
        
        try {
            return Optional.of(query.getSingleResult());
        } catch(NoResultException ex) {
            return Optional.empty();
        }
    }
    
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Override
    public void save(final Page page) {
        super.save(page);
    }
    
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Override
    public void delete(final Page page) {
        super.delete(page);
    }
    
    
    @Override
    public Class<Page> getEntityClass() {
        return Page.class;
    }

    @Override
    public boolean isNew(final Page page) {
        return page.getObjectId() == 0;
    }
    
    
    
}
