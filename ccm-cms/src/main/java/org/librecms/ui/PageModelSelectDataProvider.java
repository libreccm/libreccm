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
package org.librecms.ui;

import com.vaadin.cdi.ViewScoped;
import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.Query;
import org.libreccm.pagemodel.PageModel;

import java.util.stream.Stream;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
class PageModelSelectDataProvider
    extends AbstractBackEndDataProvider<PageModel, String> {

    private static final long serialVersionUID = 3102935928982631262L;

    @Inject
    private EntityManager entityManager;

    @Override
    protected Stream<PageModel> fetchFromBackEnd(
        final Query<PageModel, String> query) {
        
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<PageModel> criteriaQuery = builder
        .createQuery(PageModel.class);
        final Root<PageModel> from = criteriaQuery.from(PageModel.class);
        criteriaQuery.select(from);
        
        return entityManager
            .createQuery(criteriaQuery)
            .setFirstResult(query.getOffset())
            .setMaxResults(query.getLimit())
            .getResultList()
            .stream();
    }

    @Override
    protected int sizeInBackEnd(final Query<PageModel, String> query) {

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = builder
            .createQuery(Long.class);
        final Root<PageModel> from = criteriaQuery.from(PageModel.class);
        
        criteriaQuery.select(builder.count(from));
       
        return entityManager
            .createQuery(criteriaQuery)
            .getSingleResult()
            .intValue();
    }

}
