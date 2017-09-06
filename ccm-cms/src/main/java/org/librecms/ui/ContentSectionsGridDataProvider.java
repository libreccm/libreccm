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
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;

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
class ContentSectionsGridDataProvider
    extends AbstractBackEndDataProvider<ContentSection, String> {

    private static final long serialVersionUID = 2173898409635046482L;

    @Inject
    private EntityManager entityManager;

    @Inject
    private ContentSectionRepository sectionRepo;

    @Override
    protected Stream<ContentSection> fetchFromBackEnd(
        final Query<ContentSection, String> query) {

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ContentSection> criteriaQuery = builder
        .createQuery(ContentSection.class);
        final Root<ContentSection> from = criteriaQuery
            .from(ContentSection.class);
        
        return entityManager
            .createQuery(criteriaQuery)
            .setMaxResults(query.getLimit())
            .setFirstResult(query.getOffset())
            .getResultList()
            .stream();
    }

    @Override
    protected int sizeInBackEnd(final Query<ContentSection, String> query) {

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = builder
            .createQuery(Long.class);
        final Root<ContentSection> from = criteriaQuery
            .from(ContentSection.class);

        criteriaQuery.select(builder.count(from));

        return entityManager
            .createQuery(criteriaQuery)
            .getSingleResult()
            .intValue();
    }

}
