/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.toolbox.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PageEvent;

import javax.persistence.criteria.CriteriaQuery;

/**
 * This event is fired by the {@link DataTable} class
 *
 * @param <T> Type of the entities retrieved by the query.
 * @see QueryListener
 */
public class QueryEvent<T> extends PageEvent {

    private static final long serialVersionUID = -3616223193853983580L;

    private CriteriaQuery<T> query;

    public QueryEvent(final Component source, 
                      final PageState state, 
                      final CriteriaQuery<T> query) {
        super(source, state);
        this.query = query;
    }

    public CriteriaQuery<T> getDataQuery() {
        return query;
    }

}
