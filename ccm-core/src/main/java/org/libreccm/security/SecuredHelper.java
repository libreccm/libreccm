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


import static org.libreccm.core.CoreConstants.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;

/**
 * A helper class used by the secured collections provided by this package.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class SecuredHelper<E extends CcmObject> {

    private final static Logger LOGGER = LogManager.getLogger(
            SecuredHelper.class);

    /**
     * Class of the objects in the collection.
     */
    private final Class<E> clazz;
    /**
     * Privilege required to access the objects in the collection.
     */
    private final String requiredPrivilege;

    protected SecuredHelper(final Class<E> clazz,
                            final String requiredPrivilege) {
        this.clazz = clazz;
        this.requiredPrivilege = requiredPrivilege;
    }

    /**
     * Check if the current subject has the permission to access to provided
     * object with the provided privilege.
     *
     * @param object The object to check.
     * @return The provided {@code object} if the current subject has the
     * permission to access it with the provided {@code privilege}. Otherwise a
     * placeholder object is returned. The {@link CcmObject#displayName}
     * property of these object is set to {@code Access denied}.
     */
    protected E canAccess(final E object) {
        if (object == null) {
            return null;
        }
        
        final CdiUtil cdiUtil = new CdiUtil();
        final PermissionChecker permissionChecker = cdiUtil.findBean(
                    PermissionChecker.class);
        
        if (permissionChecker.isPermitted(requiredPrivilege, object)) {
            return object;
        } else {
            return generateAccessDeniedObject();
        }
    }

    /**
     * Helper method for creating an <em>Access denied</em> placeholder object.
     *
     * @return An object of the provided {@link #clazz} with it's
     * {@link CcmObject#displayName} property set to {@code Access denied}.
     */
    protected E generateAccessDeniedObject() {
        try {
            final E placeholder = clazz.newInstance();
            placeholder.setDisplayName(ACCESS_DENIED);
            
            return placeholder;
        } catch (InstantiationException | IllegalAccessException ex) {
            LOGGER.error(
                    "Failed to create placeholder object. Returing null.", ex);
            return null;
        }
    }

}
