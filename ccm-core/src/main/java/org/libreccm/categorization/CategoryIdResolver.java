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

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import org.libreccm.cdi.utils.CdiUtil;

import javax.enterprise.context.RequestScoped;
import java.io.Serializable;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version created on 3/23/17
 */
@Deprecated
@RequestScoped
public class CategoryIdResolver implements Serializable, ObjectIdResolver {
    private static final long serialVersionUID = -5750627754502675522L;

    @Override
    public void bindItem(ObjectIdGenerator.IdKey idKey,
                         Object pojo) {
        // According to the Jackson JavaDoc, this method can be used to keep
        // track of objects directly in a resolver implementation. We don't need
        // this here therefore this method is empty.
    }

    @Override
    public Object resolveId(ObjectIdGenerator.IdKey id) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final CategoryRepository categoryRepository = cdiUtil
                .findBean(CategoryRepository.class);

        return categoryRepository
                .findByUuid(id.key.toString())
                .orElseThrow(() -> new IllegalArgumentException(String
                .format("No Category with uuid %s in the database.",
                        id.key.toString())));
    }

    @Override
    public ObjectIdResolver newForDeserialization(Object context) {
        return new CategoryIdResolver();
    }

    @Override
    public boolean canUseFor(ObjectIdResolver resolverType) {
        return resolverType instanceof CategoryIdResolver;
    }
}
