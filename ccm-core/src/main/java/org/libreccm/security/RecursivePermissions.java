/*
 * Copyright (C) 2017 LibreCCM Foundation.
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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

/**
 * Marker interface which an be added to a property to indicate that permissions
 * granted on the owing object should recursivly applied to the child objects.
 *
 * The privileges for which this applied can be limited using the
 * {@link #privileges()} property. If {@link #privileges()} is empty, all
 * permissions are applied recursivly. Otherwise only permissions granting one
 * of the listed privileges are applied recursivly.
 *
 * This annotation can only applied to fields of the following types:
 * <ul>
 * <li>{@link CcmObject}</li>
 * <li>{@link Collection}</li>
 * <li>{@link Relation}</li>
 * </ul>
 *
 * If applied to a {@link Collection} field the permissions are only recursivly
 * applied for members of the types {@link CcmObject} and {@link Relation}.
 *
 * If an association between two {@link CcmObject}s is modelled using a relation
 * object the relation object must implement the {@link Relation} interface to
 * apply to permissions recursivly.
 *
 * @see Relation
 * @see PermissionManager#grantPrivilege(java.lang.String,
 * org.libreccm.security.Role, org.libreccm.core.CcmObject)}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RecursivePermissions {

    /**
     * If set only permissions for the privileges in listed here will be applied
     * recursivly.
     *
     * @return
     */
    String[] privileges() default {};

}
