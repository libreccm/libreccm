/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.security;

import org.libreccm.core.CcmObject;

import java.util.Iterator;

/**
 * Iterator implementation for {@link CcmObject}s which checks if the current
 * subject is permitted to access an object before returning it.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <E>
 */
public class SecuredIterator<E extends CcmObject> implements Iterator<E> {

    private final Iterator<E> iterator;
    
    private final SecuredHelper<E> securedHelper;

    /**
     * Create a new secured iterator which secures the provided iterator.
     *
     * @param iterator          The iterator to secure.
     * @param clazz             The base class of the objects returned by the
     *                          iterator.
     * @param requiredPrivilege The privilege required to access the objects.
     */
    public SecuredIterator(final Iterator<E> iterator,
                           final Class<E> clazz,
                           final String requiredPrivilege) {
        this.iterator = iterator;
        this.securedHelper = new SecuredHelper<>(clazz, requiredPrivilege);
    }

    /**
     * @inheritDoc
     *
     * @return @inheritDoc
     */
    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /**
     * Returns the next object of the current subject it permitted to access it
     * or a special "Access denied" object if not.
     *
     * The method gets the next object from the wrapped {@code Iterator} and
     * checks if the current subject has a permission granting the privilege
     * provided to the constructor on the object. If the current subject is
     * permitted to access the object the object is returned. Otherwise a
     * placeholder object is created using the {@link Class#newInstance()}
     * method on the {@code Class} provided to the constructor. The
     * {@link CcmObject#displayName} of these placeholder objects is set the
     * {@code Access denied}.
     *
     * @return The next object or a special "Access denied" placeholder object.
     */
    @Override
    public E next() {
        return securedHelper.canAccess(iterator.next());
    }

}
