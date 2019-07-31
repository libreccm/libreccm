/*
 * Copyright (C) 2019 LibreCCM Foundation.
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
package org.libreccm.core;

import java.util.Objects;

/**
 * Some helper functions
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class CcmObjects {

    private CcmObjects() {

        // Nothing
    }

    public static int hashCodeUsingUuid(final CcmObject obj) {

        if (obj == null) {
            return 0;
        } else {
            return Objects.hashCode(obj.getUuid());
        }

    }

    public static boolean equalsUsingUuid(final CcmObject obj1,
                                          final CcmObject obj2) {

        if (obj1 == null && obj2 == null) {
            return true;
        } else if (obj1 == obj2) {
            return true;
        } else if (obj1 == null && obj2 != null) {
            return false;
        } else if (obj1 != null && obj2 == null) {
            return false;
        } else if (obj1 != null && obj2 != null) {

            final Class<?> class1 = obj1.getClass();
            final Class<?> class2 = obj2.getClass();

            if (class1.isAssignableFrom(class2)
                    && class2.isAssignableFrom(class1)) {
                final String uuid1 = obj1.getUuid();
                final String uuid2 = obj2.getUuid();

                return Objects.equals(uuid1, uuid2);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
