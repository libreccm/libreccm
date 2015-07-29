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
package org.libreccm.core;

import javax.enterprise.context.RequestScoped;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class PermissionManager {

    /**
     * Creates a new permission granting the provided {@code privilege} on the
     * provided {@code object} to the provided {@code subject}. If the
     * permission is already granted to the provided {@code subject} this method
     * does nothing.
     *
     * @param privilege The privilege to grant.
     * @param object    The object on which the privilege is granted.
     * @param subject   The subject to grant the privilege to.
     */
    public void grantPermission(final Privilege privilege,
                                final CcmObject object,
                                final Subject subject) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes the permission granting the provided {@code privilege} on the
     * provided {@code object} to the provided {@code subject}. If there is not
     * permission granting the provided privilege on the provided {@code object}
     * to the provided {@code subject} this method does nothing.
     *
     * @param privilege The privilege to revoke
     * @param object    The object on which the privilege is revoked.
     * @param subject   The subject to revoke the privilege from.
     */
    public void revokePermission(final Privilege privilege,
                                 final CcmObject object,
                                 final Subject subject) {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if the the provided {@code subject} has a permission granting the
     * provided {@code privilege} on the provided {@code object}.
     *
     * @param privilege The privilege to check.
     * @param object    The object on which the privilege is granted.
     * @param subject   The subject to which the privilege is granted.
     *
     * @return {@code true} of the subject has a permission granting
     *         {@code privilege} on {@code object}, either explicit or implicit.
     */
    public boolean isPermitted(final Privilege privilege,
                               final CcmObject object,
                               final Subject subject) {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if the the provided {@code subject} has a permission granting the
     * provided {@code privilege} on the provided {@code object}.
     *
     * @param privilege The privilege to check.
     * @param object    The object on which the privilege is granted.
     * @param subject   The subject to which the privilege is granted.
     *
     * @throws UnauthorizedAcccessException If there is no permission granting
     *                                      {@code privilege} on {@code object}
     *                                      to {@code subject}
     *
     */
    public void checkPermission(final Privilege privilege,
                                final CcmObject object,
                                final Subject subject)
        throws UnauthorizedAcccessException {
        throw new UnsupportedOperationException();
    }

}
