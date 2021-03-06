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
import java.util.NavigableSet;

/**
 * A decorated for {@link NavigableSet} which checks if the current subject is
 * permitted to access the objects from the decorated navigable set before
 * returning them.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * 
 * @param <E> Type of the objects in the navigable set.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class SecuredNavigableSet<E extends CcmObject>
    extends SecuredSortedSet<E>
    implements NavigableSet<E> {

    /**
     * The decorated navigable set.
     */
    private final NavigableSet<E> set;
    /**
     * Class of the objects in the navigable set.
     */
    private final Class<E> clazz;
    /**
     * The privilege required to access the objects of the navigable set.
     */
    private final String requiredPrivilege;
    /**
     * {@link SecuredHelper} used by this decorator.
     */
    private final SecuredHelper<E> securedHelper;

    /**
     * Creates a new secured navigable set.
     *
     * @param set               The set to secure.
     * @param clazz             Class of the objects in the set.
     * @param requiredPrivilege The privilege required to access the objects in
     *                          the set.
     */
    public SecuredNavigableSet(final NavigableSet<E> set,
                               final Class<E> clazz,
                               final String requiredPrivilege) {
        super(set, clazz, requiredPrivilege);
        this.set = set;
        this.clazz = clazz;
        this.requiredPrivilege = requiredPrivilege;
        this.securedHelper = new SecuredHelper<>(clazz, requiredPrivilege);
    }

    @Override
    public E lower(final E element) {
        return securedHelper.canAccess(set.lower(element));
    }

    @Override
    public E floor(final E element) {
        return securedHelper.canAccess(set.floor(element));
    }

    @Override
    public E ceiling(final E element) {
        return securedHelper.canAccess(set.ceiling(element));
    }

    @Override
    public E higher(final E element) {
        return securedHelper.canAccess(set.higher(element));
    }

    @Override
    public E pollFirst() {
        return securedHelper.canAccess(set.pollFirst());
    }

    @Override
    public E pollLast() {
        return securedHelper.canAccess(set.pollLast());
    }

    @Override
    public NavigableSet<E> descendingSet() {
        return new SecuredNavigableSet<>(set.descendingSet(),
                                         clazz,
                                         requiredPrivilege);
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new SecuredIterator<>(set.descendingIterator(),
                                     clazz,
                                     requiredPrivilege);
    }

    @Override
    public NavigableSet<E> subSet(final E fromElement,
                                  final boolean fromInclusive,
                                  final E toElement,
                                  final boolean toInclusive) {
        return new SecuredNavigableSet<>(set.subSet(toElement,
                                                    toInclusive,
                                                    toElement,
                                                    toInclusive),
                                         clazz,
                                         requiredPrivilege);
    }

    @Override
    public NavigableSet<E> headSet(final E toElement,
                                   final boolean inclusive) {
        return new SecuredNavigableSet<>(set.headSet(toElement, inclusive),
                                         clazz,
                                         requiredPrivilege);
    }

    @Override
    public NavigableSet<E> tailSet(final E fromElement,
                                   final boolean inclusive) {
        return new SecuredNavigableSet<>(set.tailSet(fromElement, inclusive),
                                         clazz,
                                         requiredPrivilege);
    }

}
