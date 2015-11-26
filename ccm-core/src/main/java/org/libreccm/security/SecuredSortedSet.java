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
import java.util.Set;
import java.util.SortedSet;
import org.libreccm.core.CcmObject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <E>
 */
public class SecuredSortedSet<E extends CcmObject>
        extends SecuredSet<E>
        implements SortedSet<E> {

    private final SortedSet<E> set;
    private final Class<E> clazz;
    private final String requiredPrivilege;
    private final SecuredHelper<E> securedHelper;

    public SecuredSortedSet(final SortedSet<E> set,
                            final Class<E> clazz,
                            final String requiredPrivilege) {
        super(set, clazz, requiredPrivilege);
        this.set = set;
        this.clazz = clazz;
        this.requiredPrivilege = requiredPrivilege;
        this.securedHelper = new SecuredHelper(clazz, requiredPrivilege);
    }

    @Override
    public Comparator<? super E> comparator() {
        return set.comparator();
    }

    @Override
    public SortedSet<E> subSet(final E element1,
                               final E element2) {
        return new SecuredSortedSet<>(set.subSet(element1, element2),
                                      clazz,
                                      requiredPrivilege);
    }

    @Override
    public SortedSet<E> headSet(final E element) {
        return new SecuredSortedSet<>(set.headSet(element),
                                      clazz,
                                      requiredPrivilege);
    }

    @Override
    public SortedSet<E> tailSet(final E element) {
        return new SecuredSortedSet<>(set.tailSet(element),
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
