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
 * The common interface that is implemented by all Bebop containers. 
 * 
 * The Container interface extends the Component interface.  A container is 
 * simply a component that contains other components.
 *
 * @author David Lutterkort 
 * @author Uday Mathur 
 * @version $Id: Container.java 287 2005-02-22 00:29:02Z sskracic $
 * */
public interface Container extends Component {

    /**
     * Adds a component to this container.
     *
     * @param pc component to add to this container
     * @pre pc != null
     */
    void add(Component pc);

    /**
     * Adds a component with the specified layout constraints to this
     * container. Layout constraints are defined in each layout container as
     * static ints. To specify multiple constraints, uses bitwise OR.
     *
     * @param pc component to add to this container
     *
     * @param constraints layout constraints (a
     * bitwise OR of static ints in the particular layout)
     *
     * @pre c != null
     */
    void add(Component c, int constraints);

    /**
     * Returns <code>true</code> if this list contains the specified element.
     * More formally, returns <code>true</code> if and only if this list
     * contains at least
     * one element e such that (o==null ? e==null : o.equals(e)).
     * <P>
     * This method returns <code>true</code> only if the object has been
     * directly added to this container. If this container contains another
     * container that contains this object, this method returns
     * <code>false</code>.
     *
     * @param  o element whose presence in this container is to be tested
     *
     * @return <code>true</code> if this container contains the  specified
     * object directly; <code>false</code> otherwise.
     * @pre o != null
     */
    boolean contains(Object o);

    /**
     *  Gets the component
     * at the specified position. Each call to the add method increments
     * the index. Since the user has no control over the index of added
     * components (other than counting each call to the add method),
     * this method should be used in conjunction with indexOf.
     *
     * @param index the index of the item to be retrieved from this
     * container
     *
     * @return the component at the specified position in this container.
     *
     * @pre index >= 0 && index < size()
     * @post return != null */
    Component get(int index);

    /**
     * 
     *
     * @param pc component to search for
     *
     * @return the index in this list of the first occurrence of
     * the specified element, or -1 if this list does not contain this
     * element.
     *
     * @pre pc != null
     * @post contains(pc) implies (return >= 0 && return < size())
     * @post ! contains(pc) implies return == -1
     */
    int indexOf(Component pc);

    /**
     * Returns <code>true</code> if the container contains no components.
     *
     * @return <code>true</code> if this container contains no components;
     * <code>false</code> otherwise.
     * @post return == ( size() == 0 )
     */
    boolean isEmpty();

    /**
     * Returns the number of elements in this container. This does not
     * recursively count components that are indirectly contained in this container.
     *
     * @return the number of components directly in this container.
     * @post size() >= 0
     */
    int size();
}
