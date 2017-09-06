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

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * A decorator for {@link List}s of {@link CcmObject}s which checks if the
 * current subject is permitted to access the objects in the list before
 * returning them.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @param <E> The type of the objects in the list.
 */
public class SecuredList<E extends CcmObject>
    extends SecuredCollection<E>
    implements List<E> {

    /**
     * The decorated list.
     */
    private final List<E> list;
    /**
     * The class of the objects in the list.
     */
    private final Class<E> clazz;
    /**
     * The privilege required to access the objects in the list.
     */
    private final String requiredPrivilege;
    /**
     * {@link SecuredHelper} used by the list.
     */
    private final SecuredHelper<E> securedHelper;

    /**
     * Creates a new secured list.
     *
     * @param list              The {@link List} to secure.
     * @param clazz             Class of the objects in the list.
     * @param requiredPrivilege The privilege required to access the objects in
     *                          the list.
     */
    public SecuredList(final List<E> list,
                       final Class<E> clazz,
                       final String requiredPrivilege) {
        super(list, clazz, requiredPrivilege);
        this.list = list;
        this.clazz = clazz;
        this.requiredPrivilege = requiredPrivilege;
        this.securedHelper = new SecuredHelper<>(clazz, requiredPrivilege);
    }

    @Override
    public boolean addAll(final int index,
                          final Collection<? extends E> collection) {
        return list.addAll(index, collection);
    }

    @Override
    public E get(final int index) {
        return securedHelper.canAccess(list.get(index));
    }

    @Override
    public E set(final int index, final E element) {
        return list.set(index, element);
    }

    @Override
    public void add(final int index, final E element) {
        list.add(index, element);
    }

    @Override
    public E remove(final int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(final Object object) {
        return list.indexOf(object);
    }

    @Override
    public int lastIndexOf(final Object object) {
        return list.lastIndexOf(object);
    }

    @Override
    public ListIterator<E> listIterator() {
        return new SecuredListIterator<>(list.listIterator(),
                                         clazz,
                                         requiredPrivilege);
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        return new SecuredListIterator<>(list.listIterator(index),
                                         clazz,
                                         requiredPrivilege);
    }

    @Override
    public List<E> subList(final int index1, final int index2) {
        return new SecuredList<>(list.subList(index1, index2),
                                 clazz,
                                 requiredPrivilege);
    }

}
