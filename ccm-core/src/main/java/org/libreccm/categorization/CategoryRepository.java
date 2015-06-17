/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.categorization;

import org.libreccm.core.AbstractEntityRepository;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.TypedQuery;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class CategoryRepository extends AbstractEntityRepository<Long, Category> {

    @Override
    public Class<Category> getEntityClass() {
        return Category.class;
    }

    @Override
    public boolean isNew(final Category entity) {
        return entity.getObjectId() == 0;
    }

    /**
     * Retrieves a list of all top level categories (Categories without a 
     * parent category).
     * 
     * @return A list of all top level categories.
     */
    public List<Category> getTopLevelCategories() {
        final TypedQuery<Category> query = getEntityManager().createNamedQuery(
            "topLevelCategories", Category.class);
        
        return query.getResultList();
    }

}
