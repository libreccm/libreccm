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

import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.cdi.utils.CdiLookupException;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;

import java.util.Collection;
import java.util.Iterator;

/**
 * A decorator for collections which checks if the current user is permitted to
 * access the objects in the collection before returning them.
 *
 * This implementation of the {@link Collection} interface decorates a given
 * collection. The methods which manipulate the collection are simply calling
 * the methods of decorated collection. The methods which retrieve objects from
 * the collection are first calling the method of the decorated collection, and
 * check if the current subject is permitted to access the object. If the
 * current subject is permitted to access the object the object is returned.
 * Otherwise the object is replaced with a virtual object were the
 * {@link CcmObject#displayName} property is set to {@code Access Denied}. 
 * Methods which return arrays or collections of objects from the decorated 
 * collection check each object in the array or collection and replace the
 * objects which the current subject is not permitted to access with a 
 * <em>Access denied</em> object.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @param <E> Type of the objects in the collection. Must extend 
 * {@link CcmObject}.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class SecuredCollection<E extends CcmObject> implements Collection<E> {

    /**
     * The decorated collection.
     */
    private final Collection<E> collection;

    /**
     * The class of the objects in the collection. Required for creating the 
     * virtual <em>Access denied</em> object
     */
    private final Class<E> clazz;

    /**
     * The privilege required for accessing the objects in the collection.
     */
    private final String requiredPrivilege;

    /**
     * Helper class for creating the <em>Access denied</em> object.
     */
    private final SecuredHelper<E> securedHelper;

    /**
     * Create a new secured collection for the provided collection.
     * 
     * @param collection The collection to secure.
     * @param clazz The class of the objects in the collection.
     * @param requiredPrivilege The privilege required to access the objects
     * in the collection.
     */
    public SecuredCollection(final Collection<E> collection,
                             final Class<E> clazz,
                             final String requiredPrivilege) {
        this.collection = collection;
        this.clazz = clazz;
        this.requiredPrivilege = requiredPrivilege;
        this.securedHelper = new SecuredHelper<>(clazz, requiredPrivilege);
    }

    @Override
    public int size() {
        return collection.size();
    }

    @Override
    public boolean isEmpty() {
        return collection.isEmpty();
    }

    @Override
    public boolean contains(final Object object) {
        return collection.contains(object);
    }

    @Override
    public Iterator<E> iterator() {
        return new SecuredIterator<>(collection.iterator(), clazz,
                                     requiredPrivilege);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object[] toArray() {
        final PermissionChecker permissionChecker;
        final CdiUtil cdiUtil = new CdiUtil();
        try {
            permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);
        } catch (CdiLookupException ex) {
            throw new UncheckedWrapperException(ex);
        }

        final Object[] objects = collection.toArray();
        for (int i = 0; i < objects.length; i++) {
            if (!permissionChecker
                .isPermitted(requiredPrivilege, (E) objects[i])) {
                objects[i] = securedHelper.generateAccessDeniedObject();
            }
        }

        return objects;
    }

    @Override
    @SuppressWarnings({"unchecked", "PMD.UseVarargs"})
    public <T> T[] toArray(final T[] array) {
        final PermissionChecker permissionChecker;
        final CdiUtil cdiUtil = new CdiUtil();
        try {
            permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);
        } catch (CdiLookupException ex) {
            throw new UncheckedWrapperException(ex);
        }

        final T[] objects = collection.toArray(array);
        for (int i = 0; i < objects.length; i++) {
            if (!permissionChecker.isPermitted(requiredPrivilege,
                                               (CcmObject) objects[i])) {
                objects[i] = (T) securedHelper.generateAccessDeniedObject();
            }
        }
        return objects;
    }

    @Override
    public boolean add(final E element) {
        return collection.add(element);
    }

    @Override
    public boolean remove(final Object object) {
        return collection.remove(object);
    }

    @Override
    public boolean containsAll(final Collection<?> collection) {
        return this.collection.containsAll(collection);
    }

    @Override
    public boolean addAll(final Collection<? extends E> collection) {
        return this.collection.addAll(collection);
    }

    @Override
    public boolean removeAll(final Collection<?> collection) {
        return this.collection.removeAll(collection);

    }

    @Override
    public boolean retainAll(final Collection<?> collection) {
        return this.collection.retainAll(collection);
    }

    @Override
    public void clear() {
        collection.clear();
    }

}
