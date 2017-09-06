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

import java.util.ListIterator;

/**
 * A decorator for {@link ListIterator} which checks if the current subject is
 * permitted to access an object from the list the iterator iterates over before
 * returning it.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <E>
 */
public class SecuredListIterator<E extends CcmObject>
    extends SecuredIterator<E>
    implements ListIterator<E> {

    /**
     * The decorated {@link ListIterator}.
     */
    private final ListIterator<E> iterator;
    /**
     * {@link SecuredHelper} used by this iterator.
     */
    private final SecuredHelper<E> securedHelper;

    /**
     * Creates a new secured list iterator.
     *
     * @param iterator          The iterator to secure.
     * @param clazz             The class of the objects in the list.
     * @param requiredPrivilege The privilege required to access the objects in
     *                          list.
     */
    public SecuredListIterator(final ListIterator<E> iterator,
                               final Class<E> clazz,
                               final String requiredPrivilege) {
        super(iterator, clazz, requiredPrivilege);
        this.iterator = iterator;
        this.securedHelper = new SecuredHelper<>(clazz, requiredPrivilege);
    }

    @Override
    public boolean hasPrevious() {
        return iterator.hasPrevious();
    }

    @Override
    public E previous() {
        return securedHelper.canAccess(iterator.previous());
    }

    @Override
    public int nextIndex() {
        return iterator.nextIndex();
    }

    @Override
    public int previousIndex() {
        return iterator.previousIndex();
    }

    @Override
    public void remove() {
        iterator.remove();
    }

    @Override
    public void set(final E element) {
        iterator.set(element);
    }

    @Override
    public void add(final E element) {
        iterator.add(element);
    }

}
