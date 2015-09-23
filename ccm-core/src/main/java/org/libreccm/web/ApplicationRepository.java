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
package org.libreccm.web;

import org.libreccm.core.AbstractEntityRepository;

import javax.enterprise.context.RequestScoped;
import javax.persistence.TypedQuery;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ApplicationRepository
    extends AbstractEntityRepository<Long, Application> {

    @Override
    public Class<Application> getEntityClass() {
        return Application.class;
    }

    @Override
    public boolean isNew(final Application application) {
        return application.getObjectId() == 0;
    }

    public Application retrieveApplicationForPath(final String path) {
        final TypedQuery<Application> query = getEntityManager()
            .createNamedQuery(
                "retrieveApplicationForPath", Application.class);

        return query.getSingleResult();
    }

}
