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

import java.util.Optional;

/**
 * Subclasses of {@link CcmObject} can implement this interface to inherit 
 * the permissions of their parent object. This interface is processed by the 
 * {@link PermissionChecker}. 
 * 
 * @see PermissionChecker#checkPermission(java.lang.String, 
 *                                        org.libreccm.core.CcmObject) 
 * @see PermissionChecker#isPermitted(java.lang.String, 
 *                                    org.libreccm.core.CcmObject) 
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public interface InheritsPermissions {
    
    /**
     * This method needs to be overwritten by implementers of interface
     * 
     * @return The parent object of the implementing object. The 
     * {@link PermissionChecker} will use the permissions granted on the parent
     * object in addition to the permissions granted on the object itself to 
     * determine if a user is granted a specific privilege on the object.
     */
    Optional<CcmObject> getParent();
    
}
