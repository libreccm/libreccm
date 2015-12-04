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

import java.util.Comparator;
import java.util.SortedSet;

import org.libreccm.core.CcmObject;

/**
 * Decorator for {@link SortedSet} which checks if the current subject is
 * permitted to access the objects from the decorated sorted set before
 * returning them.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @param <E> Type of the objects in the decorated sorted set.
 */
public class SecuredSortedSet<E extends CcmObject>
    extends SecuredSet<E>
    implements SortedSet<E> {

    /**
     * The decorated sorted set.
     */
    private final SortedSet<E> set;
    /**
     * The class of the objects in the decorated sorted set.
     */
    private final Class<E> clazz;
    /**
     * The privilege required to access the objects in the decorated set.
     */
    private final String requiredPrivilege;
    /**
     * {@link SecuredHelper} used by this decorator.
     */
    private final SecuredHelper<E> securedHelper;

    /**
     * Creates new secured sorted set.
     *
     * @param set               The sorted set to secure.
     * @param clazz             The class of the objects in the set.
     * @param requiredPrivilege The privilege required to access the objects in
     *                          the set.
     */
    public SecuredSortedSet(final SortedSet<E> set,
                            final Class<E> clazz,
                            final String requiredPrivilege) {
        super(set, clazz, requiredPrivilege);
        this.set = set;
        this.clazz = clazz;
        this.requiredPrivilege = requiredPrivilege;
        this.securedHelper = new SecuredHelper<>(clazz, requiredPrivilege);
    }

    @Override
    public Comparator<? super E> comparator() {
        return set.comparator();
    }

    @Override
    public SortedSet<E> subSet(final E fromElement,
                               final E toElement) {
        return new SecuredSortedSet<>(set.subSet(fromElement, toElement),
                                      clazz,
                                      requiredPrivilege);
    }

    @Override
    public SortedSet<E> headSet(final E toElement) {
        return new SecuredSortedSet<>(set.headSet(toElement),
                                      clazz,
                                      requiredPrivilege);
    }

    @Override
    public SortedSet<E> tailSet(final E fromElement) {
        return new SecuredSortedSet<>(set.tailSet(fromElement),
                                      clazz,
                                      requiredPrivilege);
    }

    @Override
    public E first() {
        return securedHelper.canAccess(set.first());
    }

    @Override
    public E last() {
        return securedHelper.canAccess(set.last());
    }

}
