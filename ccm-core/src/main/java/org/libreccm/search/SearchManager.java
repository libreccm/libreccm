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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationScoped
public class SearchManager {
    
    @Inject
    private EntityManager entityManager;
    
    private FullTextEntityManager fullTextEntityManager;
    
    public SearchManager() {
        
    }
    
    @PostConstruct
    private void init() {
        fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
    }
    
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public Future<?> rebuildIndex() {
        final MassIndexer indexer = fullTextEntityManager.createIndexer();
        return indexer.start();
    }
    
    public QueryBuilder createQueryBuilder(final Class<?> entityClass) {
        final SearchFactory searchFactory = fullTextEntityManager.getSearchFactory();
        return searchFactory.buildQueryBuilder().forEntity(entityClass).get();
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    public List<?> executeQuery(final Query query) {
        final javax.persistence.Query jpaQuery = fullTextEntityManager
            .createFullTextQuery(query);
        
        return jpaQuery.getResultList();
    }
    
}
