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
package org.librecms.contentsection;

import org.apache.lucene.search.Query;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.libreccm.search.SearchManager;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * Provides search queries (using Hibernate Search and Lucene) for ContentItems.
 * For details about the different search methods ({@code match}, {@code fuzzy}
 * and {@code wildcard} please refer to chapter 5 of the
 * <a href="https://docs.jboss.org/hibernate/stable/search/reference/en-US/html_single/#search-query">Hibernate
 * Search documentation</a>.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContentItemSearchManager {

    /**
     * The fields in which the search engine looks for the provided term.
     */
    private static final String[] FIELDS = {"id",
                                            "uuid",
                                            "displayName",
                                            "title",
                                            "description"};

    @Inject
    private SearchManager searchManager;

    /**
     * Create and execute a {@code match} query.
     *
     * @param term The term(s) to search for.
     *
     * @return A list containing all results matching the query.
     */
    @SuppressWarnings("unchecked")
    public List<ContentItem> searchItemMatch(final String term) {
        final QueryBuilder queryBuilder = searchManager.createQueryBuilder(
            ContentItem.class);

        final Query luceneQuery = queryBuilder
            .keyword()
            .onFields(FIELDS)
            .matching(term)
            .createQuery();

        return (List<ContentItem>) searchManager.executeQuery(luceneQuery);
    }

    /**
     * Create and execute a {@code fuzzy} query.
     *
     * @param term The term(s) to search for.
     *
     * @return A list containing all results matching the query.
     */
    @SuppressWarnings("unchecked")
    public List<ContentItem> searchItemFuzzy(final String term) {
        final QueryBuilder queryBuilder = searchManager.createQueryBuilder(
            ContentItem.class);

        final Query luceneQuery = queryBuilder
            .keyword()
            .fuzzy()
            .onFields(FIELDS)
            .matching(term)
            .createQuery();

        return (List<ContentItem>) searchManager.executeQuery(luceneQuery);
    }

    /**
     * Create and execute a {@code wildcard} query.
     *
     * @param term The term(s) to search for.
     *
     * @return A list containing all results matching the query.
     */
    @SuppressWarnings("unchecked")
    public List<ContentItem> searchItemWildcard(final String term) {
        final QueryBuilder queryBuilder = searchManager.createQueryBuilder(
            ContentItem.class);

        final Query luceneQuery = queryBuilder
            .keyword()
            .wildcard()
            .onFields(FIELDS)
            .matching(term)
            .createQuery();

        return (List<ContentItem>) searchManager.executeQuery(luceneQuery);
    }

}
