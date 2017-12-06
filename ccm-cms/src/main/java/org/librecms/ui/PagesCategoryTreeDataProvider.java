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
import com.vaadin.data.provider.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.data.provider.HierarchicalQuery;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.Domain;

import java.util.stream.Stream;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
class PagesCategoryTreeDataProvider
    extends AbstractBackEndHierarchicalDataProvider<Category, String> {

    private static final long serialVersionUID = -4953505403671944088L;


    @Inject
    private EntityManager entityManager;


    private Domain domain;

    protected Domain getDomain() {
        return domain;
    }

    protected void setDomain(final Domain domain) {
        this.domain = domain;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    protected Stream<Category> fetchChildrenFromBackEnd(
        final HierarchicalQuery<Category, String> query) {

        if (query.getParentOptional().isPresent()) {

            final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<Category> criteriaQuery = builder
                .createQuery(Category.class);

            final Root<Category> from = criteriaQuery.from(Category.class);
            criteriaQuery.where(builder.equal(from.get("parentCategory"),
                                              query.getParentOptional().get()));
            final TypedQuery<Category> entityQuery = entityManager
                .createQuery(criteriaQuery);
            return entityQuery
                .setFirstResult(query.getOffset())
                .setMaxResults(query.getLimit())
                .getResultList()
                .stream();
        } else {

            return Stream.of(domain.getRoot());
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public int getChildCount(
        final HierarchicalQuery<Category, String> query) {

        if (query.getParentOptional().isPresent()) {

            final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<Long> criteriaQuery = builder
                .createQuery(Long.class);

            final Root<Category> from = criteriaQuery.from(Category.class);
            criteriaQuery.select(builder.count(from));
            criteriaQuery.where(builder.equal(from.get("parentCategory"),
                                              query.getParentOptional().get()));
            final TypedQuery<Long> entityQuery = entityManager
                .createQuery(criteriaQuery);
            return entityQuery.getSingleResult().intValue();
        } else {
            return 1;
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public boolean hasChildren(final Category item) {

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = builder
            .createQuery(Long.class);

        final Root<Category> from = criteriaQuery.from(Category.class);
        criteriaQuery.select(builder.count(from));
        criteriaQuery.where(builder.equal(from.get("parentCategory"), item));
        final TypedQuery<Long> entityQuery = entityManager
            .createQuery(criteriaQuery);
        return entityQuery.getSingleResult().intValue() > 0;
    }
}
