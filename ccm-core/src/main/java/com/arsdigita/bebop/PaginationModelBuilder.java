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
package com.arsdigita.bebop;

/**
 *  A model builder for
 * the {@link Paginator} component.
 *
 * <p>The {@link #getTotalSize(Paginator, PageState)} method of this
 * class is called during the generation of page links for a
 * <code>Paginator</code> component. When using a
 * <code>Paginator</code> component with a {@link List} or a {@link
 * Table}, you can achieve greater flexibility in terms of caching and
 * performance by having the model builder implement this interface.
 *
 * <p>Unlike other model builder classes in Bebop, there is no
 * PaginationModel class, as this would only be an <code>int</code>.
 *
 * @see Paginator
 *
 * @author Phong Nguyen 
 * @version $Id$
 * @since 4.6.10
 **/
public interface PaginationModelBuilder {

    // $Change: 44247 $
    // $Revision$
    // $DateTime: 2004/08/16 18:10:38 $
    // $Author$

    /**
     * Returns the total number of results to paginate.
     *
     * @param paginator the Paginator instance that invoked this method
     * @param state the current page state
     * @return the total number of results to paginate.
     **/
    int getTotalSize(Paginator paginator, PageState state);

    /**
     * Determines whether the paginator should be visible in the request
     * represented by <code>state</code>.
     * This should normally delegate to the isVisible method of the
     * associated displayed component.
     *
     * @return <code>true</code> if the paginator is visible in the request;
     * <code>false</code> otherwise.
     *
     * @param state represents the current request
     * @return <code>true</code> if the component is visible; <code>false</code>
     * otherwise.
     * @pre state  != null
     */
    boolean isVisible(PageState state);

}
