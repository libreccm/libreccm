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

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.libreccm.core.CcmObject;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * An utility class for checking permissions. Uses the current {@link Subject}
 * as provided by the {@link Shiro} bean.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class PermissionChecker {

    /**
     * The current subject as provided by {@link Shiro#getSubject()}.
     */
    @Inject
    private Subject subject;

    @Inject
    private Shiro shiro;

    /**
     * Checks if the current subject has a permission granting the provided
     * privilege.
     *
     * @param privilege The privilege granted by the permission.
     *
     * @return {@code true} if the current subject has as permission granting
     *         the provided {@code privilege}, {@code false} otherwise.
     */
    public boolean isPermitted(final String privilege) {
        if (subject.isAuthenticated()) {
            return subject.isPermitted(generatePermissionString(privilege));
        } else {
            return shiro.getPublicUser().isPermitted(generatePermissionString(
                privilege));
        }
    }

    /**
     * Checks if the current subject has a permission granting the provided
     * privilege on the provided object or its parent object(s) if the object
     * implements the {@link InheritsPermissions} interface.
     *
     * @param privilege The granted privilege.
     * @param object    The object on which the privilege is granted.
     *
     * @return {@code true} if the there is a permission granting the provided
     *         {@code privilege} on the provided {@code subject}.
     */
    public boolean isPermitted(final String privilege, final CcmObject object) {
        final boolean result;
        if (subject.isAuthenticated()) {
            result = subject.isPermitted(generatePermissionString(
                privilege, object));
        } else {
            result = shiro.getPublicUser().isPermitted(generatePermissionString(
                privilege, object));
        }
        if (result) {
            return result;
        } else if (object instanceof InheritsPermissions) {
            if (((InheritsPermissions) object).getParent().isPresent()) {
                return result;
            } else {
                return isPermitted(
                    privilege,
                    ((InheritsPermissions) object).getParent().get());
            }
        } else {
            return result;
        }
    }

    /**
     * Checks if the current subject has a permission granting the provided
     * privilege. If the current subject does not have a permission granting the
     * privilege an {@link AuthorizationExeeption} is thrown.
     *
     * @param privilege The privilege to check for.
     *
     * @throws AuthorizationException If the current subject has not permission
     *                                granting the provided privilege.
     */
    public void checkPermission(final String privilege)
        throws AuthorizationException {
        if (subject.isAuthenticated()) {
            subject.checkPermission(generatePermissionString(privilege));
        } else {
            shiro.getPublicUser().checkPermission(generatePermissionString(
                privilege));
        }
    }

    /**
     * Checks if the current subject has a permission granting the provided
     * privilege on the provided object.
     *
     * If the object implements the {@link InheritsPermissions} interface the
     * method also checks the parent objects for a permission granting the
     * provided privilege.
     *
     * @param privilege The privilige to check for.
     * @param object    The object on which the privilege is granted.
     *
     * @throws AuthorizationException If there is no permission granting the
     *                                provided privilege to the current subject
     *                                on the provided object..
     */
    public void checkPermission(final String privilege,
                                final CcmObject object)
        throws AuthorizationException {
        if (object instanceof InheritsPermissions) {
            final boolean result = isPermitted(privilege, object);

            if (!result) {
                if (((InheritsPermissions) object).getParent().isPresent()) {
                    if (subject.isAuthenticated()) {
                        subject.checkPermission(generatePermissionString(
                            privilege, object));
                    } else {
                        shiro.getPublicUser().checkPermission(
                            generatePermissionString(privilege, object));
                    }
                } else {
                    checkPermission(
                        privilege,
                        ((InheritsPermissions) object).getParent().get());
                }
            }
        } else if (subject.isAuthenticated()) {
            subject.checkPermission(generatePermissionString(privilege, object));
        } else {
            shiro.getPublicUser().checkPermission(generatePermissionString(
                privilege, object));
        }
    }

    /**
     * Checks if the current subject has a permission granting the provided
     * privilege on the provided object. Returns the object of the current
     * subject is permitted to access the object. Otherwise a virtual
     * placeholder object is returned with the {@link CcmObject#displayName}
     * property set the {@code Access denied}.
     *
     * @param <T>       The type of the object to check.
     * @param privilege The privilige to check for.
     * @param object    The object on which the privilege is granted.
     * @param clazz     The class of the object.
     *
     * @return The object if the current subject is permitted to access, a
     *         placeholder object if not.
     */
    public <T extends CcmObject> T checkPermission(final String privilege,
                                                   final T object,
                                                   final Class<T> clazz) {
        final SecuredHelper<T> securedHelper = new SecuredHelper<>(clazz,
                                                                   privilege);
        return securedHelper.canAccess(object);
    }

    /**
     * Checks if a CcmObject is a virtual <i>Access Denied</i> object.
     *
     * @param object The object to check.
     *
     * @return {@code true} if the object is a <i>Access denied</i> object,
     *         {@code false} if not.
     */
    public boolean isAccessDeniedObject(final CcmObject object) {
        if (object == null) {
            return false;
        }
        return ACCESS_DENIED.equals(object.getDisplayName());
    }

    /**
     * Helper method for converting a privilege into a permission string.
     *
     * @param privilege
     *
     * @return
     */
    public String generatePermissionString(final String privilege) {
        return privilege;
    }

    /**
     * Helper method for converting a privilege into a permission string.
     *
     * @param privilege
     * @param object
     *
     * @return
     */
    public String generatePermissionString(final String privilege,
                                           final CcmObject object) {
        return String.format("%s:%d", privilege, object.getObjectId());
    }

}
