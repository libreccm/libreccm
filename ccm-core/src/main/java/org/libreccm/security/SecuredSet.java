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

import java.util.Set;
import org.libreccm.core.CcmObject;

/**
 * A decorator for {@link Set} which checks if the current subject is permitted
 * to access the objects from the decorated set before returning them.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @param <E> Type of the objects in the set.
 */
public class SecuredSet<E extends CcmObject>
    extends SecuredCollection<E>
    implements Set<E> {

    /**
     * Creates a new secured set.
     *
     * @param set               The {@link Set} to secure.
     * @param clazz             Class of the objects in the set.
     * @param requiredPrivilege Privilege required to access the objects in the
     *                          set.
     */
    public SecuredSet(final Set<E> set,
                      final Class<E> clazz,
                      final String requiredPrivilege) {
        super(set, clazz, requiredPrivilege);
    }

}
