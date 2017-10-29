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
package org.libreccm.admin.ui;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class JpqlConsoleController implements Serializable {

    private static final long serialVersionUID = 1625999285594476564L;

    @Inject
    private EntityManager entityManager;

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<?> executeQuery(final String queryStr,
                                   final int maxResults,
                                   final int offset) {

        Objects.requireNonNull(queryStr);

        final Query query = entityManager.createQuery(queryStr);
        query.setMaxResults(maxResults);
        query.setFirstResult(offset);
        return query.getResultList();
    }

}
