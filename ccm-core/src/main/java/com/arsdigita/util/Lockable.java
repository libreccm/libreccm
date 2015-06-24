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
package com.arsdigita.util;

/**
 * A common interface for all lockable parts of ACS. The locking mechanism makes
 * it possible to safely split all data structures that are used inside a web
 * server into ones that are constant across all requests and those that may
 * change during a request.
 *
 * <p>
 * The distinction betwen static and request-specific data helps in optimizing
 * the amount of memory that needs to be allocated for each request. Data
 * structures that are static can be allocated and initialized ahead of time,
 * e.g., in the <code>init</code> method of a servlet. The initialized data
 * structures are then <em>locked</em> to make them immutable. This mechanism
 * ensures that static structures that are supposed to be shared by many
 * requests, often even concurrently, do not change and are not "polluted" by
 * request-specific data.
 *
 * <p>
 * There is no automatic mechanism that makes an object immutable by itself. The
 * right checks and operations need to be implemented by each class implementing
 * <code>Lockable</code>.
 *
 * <p>
 * Bebop parameters are a good example of how one logical structure is split
 * into two classes: the class {@link
 * com.arsdigita.bebop.parameters.ParameterModel} is <code>Lockable</code> and
 * only contains the description of the parameter in an HTTP request that is
 * static and does not change on a per-request basis, such as the name of the
 * parameter and the (Java) type that the parameter value should be converted
 * to. The class {@link
 * com.arsdigita.bebop.parameters.ParameterData} contains all the
 * request-specific information for a parameter, such as the value of the
 * parameter.
 *
 * <p>
 * Any class that implements <code>Lockable</code> is expected to be fully
 * modifiable until its {@link #lock} method is called. From that point on, it
 * is read-only and should throw exceptions whenever an attempt is made to
 * modify it.
 *
 * @author David Lutterkort
 * @version $Id$
 */
public interface Lockable {

    /**
     * Lock an object. Locked objects are to be considered immutable. Any
     * attempt to modify them, e.g., through a <code>setXXX</code> method should
     * lead to an exception.
     *
     * <p>
     * Most lockable Bebop classes throw an {@link
     * java.lang.IllegalStateException} if an attempt is made to modify a locked
     * instance.
     */
    void lock();

    /**
     * Return whether an object is locked and thus immutable, or can still be
     * modified.
     *
     * @return
     */
    boolean isLocked();

}
