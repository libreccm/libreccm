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

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A decorator for a set of {@link Map.Entry} objects which returns
 * {@link SecuredEntry} instances. Used by the {@link SecuredMap}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@SuppressWarnings("PMD.TooManyMethods")
class SecuredEntrySet<E extends Map.Entry<K, V>, K, V extends CcmObject>
    implements Set<E> {

    /**
     * The decorated set.
     */
    private final Set<E> set;
    /**
     * The privilege required to access the values of the entries in the set.
     */
    private final String requiredPrivilege;
    /**
     * {@link SecuredHelper} for creating the virtual <em>Access denied</em>
     * object.
     */
    private final SecuredHelper<V> securedHelper;

    /**
     * Creates a new secured entry set.
     *
     * @param set               The set of entries to secure.
     * @param requiredPrivilege The privilege required to access the values of
     *                          the entries.
     * @param securedHelper     The {@link SecuredHelper} for creating the
     *                          virtual <em>Access denied</em> objects.
     */
    public SecuredEntrySet(final Set<E> set,
                           final String requiredPrivilege,
                           final SecuredHelper<V> securedHelper) {
        this.set = set;
        this.requiredPrivilege = requiredPrivilege;
        this.securedHelper = securedHelper;
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    @Override
    public boolean contains(final Object object) {
        return set.contains(object);
    }

    @Override
    public Iterator<E> iterator() {
        return new SecuredEntryIterator<>(set.iterator(), securedHelper);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object[] toArray() {
        final CdiUtil cdiUtil = new CdiUtil();
        final PermissionChecker permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);
        
        final Object[] entries = set.toArray();
        for (int i = 0; i < entries.length; i++) {
            final E entry = (E) entries[i];
            if (!permissionChecker.isPermitted(requiredPrivilege,
                                               entry.getValue())) {
                entries[i] = securedHelper.generateAccessDeniedObject();
            }
        }

        return entries;
    }

    @Override
    @SuppressWarnings({"unchecked", "PMD.UseVarargs"})
    public <T> T[] toArray(final T[] array) {
        final CdiUtil cdiUtil = new CdiUtil();
        final PermissionChecker permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);
        
        final E[] entries = (E[]) set.toArray(array);
        for (int i = 0; i < entries.length; i++) {
            if (!permissionChecker.isPermitted(requiredPrivilege,
                                               entries[i].getValue())) {
                entries[i] = (E) securedHelper.generateAccessDeniedObject();
            }
        }

        return (T[]) entries;
    }

    @Override
    public boolean add(final E element) {
        return set.add(element);
    }

    @Override
    public boolean remove(final Object object) {
        return set.remove(object);
    }

    @Override
    public boolean containsAll(final Collection<?> collection) {
        return set.containsAll(collection);
    }

    @Override
    public boolean addAll(final Collection<? extends E> collection) {
        return set.addAll(collection);
    }

    @Override
    public boolean retainAll(final Collection<?> collection) {
        return set.retainAll(collection);
    }

    @Override
    public boolean removeAll(final Collection<?> collection) {
        return set.removeAll(collection);
    }

    @Override
    public void clear() {
        set.clear();
    }

}
