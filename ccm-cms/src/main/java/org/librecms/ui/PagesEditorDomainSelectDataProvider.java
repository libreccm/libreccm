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
import org.libreccm.categorization.Domain;

import java.util.stream.Stream;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
class PagesEditorDomainSelectDataProvider
    extends AbstractBackEndDataProvider<Domain, String> {

    private static final long serialVersionUID = 7172310455833118907L;

    @Inject
    private EntityManager entityManager;

    @Override
    protected Stream<Domain> fetchFromBackEnd(
        final Query<Domain, String> query) {

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Domain> criteriaQuery = builder
            .createQuery(Domain.class);

        final Root<Domain> from = criteriaQuery.from(Domain.class);
        if (query.getFilter().isPresent()) {
            criteriaQuery.where(builder.like(from.get("domainKey"),
                                             ":filter%"));
        }

        final TypedQuery<Domain> entityQuery = entityManager
            .createQuery(criteriaQuery);

        if (query.getFilter().isPresent()) {
            entityQuery.setParameter("filter", query.getFilter().get());
        }

        return entityQuery
            .setFirstResult(query.getOffset())
            .setMaxResults(query.getLimit())
            .getResultList()
            .stream();
    }

    @Override
    protected int sizeInBackEnd(final Query<Domain, String> query) {

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = builder
            .createQuery(Long.class);

        final Root<Domain> from = criteriaQuery.from(Domain.class);
        criteriaQuery.select(builder.count(from));
        if (query.getFilter().isPresent()) {
            criteriaQuery.where(builder.like(from.get("domainKey"),
                                             ":filter%"));
        }

        final TypedQuery<Long> entityQuery = entityManager
            .createQuery(criteriaQuery);

        if (query.getFilter().isPresent()) {
            entityQuery.setParameter("filter", query.getFilter().get());
        }

        return entityQuery.getSingleResult().intValue();
    }

}
