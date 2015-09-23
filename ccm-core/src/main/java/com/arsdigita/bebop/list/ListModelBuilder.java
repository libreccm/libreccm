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
package com.arsdigita.bebop.list;

import com.arsdigita.util.Lockable;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.List;

/**
 * Produce a new {@link ListModel ListModels} for each
 * request. The builder will often run a database query, constructed from
 * request specific information in the <code>state</code> variable passed
 * to {@link #makeModel makeModel} and wrap the result set in a lightweight
 * implementation of <code>ListModel</code>.
 *
 * <p> The <code>ListModelBuilder</code> is used by the {@link List}
 * component whenever it needs to service a request: it calls
 * <code>makeModel</code> on the builder to generate a request-specific
 * <code>ListModel</code> which it then uses for outputting the items in
 * the list.
 *
 * <p> <b>Warning:</b> The signature of <code>makeModel</code> will be
 * changed to <code>ListModel makeModel(List, PageState)</code> after ACS
 * 4.6 </p>
 *
 * @author David Lutterkort
 * @version $Id$ */
public interface ListModelBuilder extends Lockable {

    /**
     *  Produce a {@link
     * ListModel} for the request specified by <code>state</code>. This
     * method is called at least once, and usually only once, for each
     * request served by the {@link List} that this
     * <code>ListModelBuilder</code> has been added to.
     *
     * @param state contains the request specific data for the request
     * @return the abstract representation of the list item for this request */
    ListModel makeModel(List l, PageState state);

}
