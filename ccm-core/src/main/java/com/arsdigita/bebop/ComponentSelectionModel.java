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
 * Encapsulates the selection of a single object from many
 * possibilities. Similar to {@link SingleSelectionModel SingleSelectionModel},
 * but ties a component to the selection.
 *
 * <p> A call to the {@link #getComponent getComponent} method returns a
 * component that can be used to display the current selection.
 *
 * @author David Lutterkort 
 * @author Stanislav Freidin 
 * @param <T> 
 */
public interface ComponentSelectionModel<T> extends SingleSelectionModel<T> {


    /**
     * Returns the component that should be used to output the currently
     * selected element.
     *
     * @param state the state of the current request
     * @return the component used to output the selected element.
     */
    Component getComponent(PageState state);

    /**
     * Return the selected object. The concrete type of the returned object
     * depends on the implementation of the model.
     *
     * @param state represents the state of the current request
     * @return the selected object
     */
    //Object getElement(PageState state);

    /**
     * Return an iterator over all the components that can possibly be used
     * in rendering selected objects. If one component may be used to render
     * various objects, for example for displaying detail information about
     * each of a number of objects of the same type, the component needs to
     * occur only once in the iterator.
     *
     * @return an iterator of components listing all the components that can
     * may be used in displaying selected objects
     */
    //Iterator components();

}
