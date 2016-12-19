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
package org.libreccm.search;

import org.apache.lucene.search.Query;
import org.hibernate.search.MassIndexer;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.libreccm.core.CoreConstants;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;

import java.util.List;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

/**
 * Provides an interface to Hibernate search. The methods here can be used to
 * reduce the boilerplate code for writing Hibernate Search queries.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationScoped
public class SearchManager {

    @Inject
    private EntityManager entityManager;

    private FullTextEntityManager fullTextEntityManager;

    /**
     * Initialises the class by creating a {@link FullTextEntityManager}.
     */
    @PostConstruct
    private void init() {
        fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
    }

    /**
     * Rebuild the complete index. This methods requires {@code admin}
     * privileges.
     *
     * @return A {@link Future} object for controlling/monitoring the index
     *         process.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public Future<?> rebuildIndex() {
        final MassIndexer indexer = fullTextEntityManager.createIndexer();
        return indexer.start();
    }

    /**
     * Creates a {@link QueryBuilder} for the provided entity class.
     *
     * @param entityClass The entity class.
     *
     * @return A {@link QueryBuilder} which can be used to create a Hibernate
     *         Search/Lucene query for entities of the provided class.
     */
    public QueryBuilder createQueryBuilder(final Class<?> entityClass) {
        final SearchFactory searchFactory = fullTextEntityManager
            .getSearchFactory();
        return searchFactory.buildQueryBuilder().forEntity(entityClass).get();
    }

    /**
     * Executes a Hibernate Search/Lucene query. This method contains uses the
     * {@link #fullTextEntityManager} to wrap the Lucene query into an
     * Hibernate/JPA query. To avoid lazy loading issues this method is
     * transactional.
     *
     * @param query The query to execute.
     *
     * @return A result list containing all entities matching the query.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<?> executeQuery(final Query query) {
        final javax.persistence.Query jpaQuery = fullTextEntityManager
            .createFullTextQuery(query);

        return jpaQuery.getResultList();
    }

}
