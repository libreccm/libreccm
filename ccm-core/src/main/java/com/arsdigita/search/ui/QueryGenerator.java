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
import org.apache.lucene.search.Query;

/**
 * This interface provides the API for retrieving a query specification based on
 * the current state. The ResultsPane component uses an instance of this class
 * to retrieve the query spec and display a list of results
 *
 * @see com.arsdigita.search.ui.QueryComponent
 * @see com.arsdigita.search.ui.ResultsPane
 */
public interface QueryGenerator {

    /**
     * Determines whether a query spec currently exists.
     *
     * @param state The current page state.
     * @return true if a query spec is available.
     */
    boolean hasQuery(PageState state);

    /**
     * Retrieves the current query spec. This method can only be called if
     * hasQuery(state) returns true.
     *
     * @param state The current page.
     * @return The query
     */
    Query getQuerySpecification(PageState state);

}
