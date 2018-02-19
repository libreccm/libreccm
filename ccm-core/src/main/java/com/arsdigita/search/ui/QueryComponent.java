/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.search.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.search.SearchConstants;

import org.apache.lucene.search.Query;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.libreccm.search.SearchManager;

/**
 * A base class for generating a query specification from the state. Subclasses
 * must implement two methods, one for getting the query terms, the other for
 * getting a set of filter specs.
 */
public abstract class QueryComponent extends SimpleContainer
        implements QueryGenerator {

    public QueryComponent(final String name) {
        setTag(SearchConstants.XML_PREFIX + name);
        setNamespace(SearchConstants.XML_NS);
    }

    /**
     * Determine if a query specification is available
     *
     * @return true if the user has entered some search terms
     */
    @Override
    public boolean hasQuery(final PageState state) {
        String terms = getTerms(state);

        return (terms != null && !"".equals(terms));
    }

    /**
     * Returns the current query specification
     */
    @Override
    public Query getQuerySpecification(final PageState state) {
        
        final String terms = getTerms(state);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final SearchManager searchManager = cdiUtil.
                findBean(SearchManager.class);
        
        final QueryBuilder queryBuilder = searchManager
            .createQueryBuilder(CcmObject.class);
        
        return queryBuilder
                .keyword().onFields("displayName", 
                                    "summary", 
                                    "description", 
                                    "title")
                .matching(terms)
                .createQuery();
        
    }

    /**
     * Returns the current query terms
     *
     * @param state
     * @return the query terms, or null
     */
    protected abstract String getTerms(PageState state);

}
