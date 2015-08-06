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

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * This class provides methods for managing {@link Permissions}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class PermissionManager {

    @Inject
    @SuppressWarnings("PMD.LongVariable") //Nothing wrong with this name
    private transient PermissionRepository permissionRepository;

    @Inject
    @SuppressWarnings("PMD.LongVariable") //Nothing wrong with this name
    private transient PrivilegeRepository privilegeRepository;

    @Inject
    @SuppressWarnings("PMD.LongVariable") //Nothing wrong with this name
    private transient CcmObjectRepository ccmObjectRepository;

    @Inject
    private transient SubjectRepository subjectRepository;

    @Inject
    private transient UserRepository userRepository;

    /**
     * Creates a new permission granting the provided {@code privilege} on the
     * provided {@code object} to the provided {@code subject}. If the
     * permission is already granted to the provided {@code subject} this method
     * does nothing.
     *
     * @param privilege The privilege to grant. Can't be {@code null}.
     * @param object    The object on which the privilege is granted. Can be
     *                  {@code null}.
     * @param subject   The subject to grant the privilege to. Can't be
     *                  {@code null}.
     */
    public void grantPermission(final Privilege privilege,
                                final CcmObject object,
                                final Subject subject) {
        if (privilege == null) {
            throw new IllegalArgumentException(
                "Illegal argument 'null' provided for parameter privilege.");
        }

        if (subject == null) {
            throw new IllegalArgumentException(
                "Illegal argument 'null' provided for parameter subject");
        }

        if (!isPermitted(privilege, object, subject)) {
            final Permission permission = new Permission();
            permission.setGrantedPrivilege(privilege);
            permission.setObject(object);
            permission.setGrantee(subject);

            subject.addGrantedPermission(permission);
            subjectRepository.save(subject);
            if (object != null) {
                object.addPermission(permission);
                ccmObjectRepository.save(object);
            }

            permissionRepository.save(permission);
        }
    }

    /**
     * Removes the permission granting the provided {@code privilege} on the
     * provided {@code object} to the provided {@code subject}. If there is not
     * permission granting the provided privilege on the provided {@code object}
     * to the provided {@code subject} this method does nothing.
     *
     * @param privilege The privilege to revoke. Can't be {@code null}.
     * @param object    The object on which the privilege is revoked. Can be
     *                  {@code null}.
     * @param subject   The subject to revoke the privilege from. Can't be
     *                  {@code null}.
     */
    public void revokePermission(final Privilege privilege,
                                 final CcmObject object,
                                 final Subject subject) {
        final List<Permission> permissions = permissionRepository
            .findPermissionsForSubjectPrivilegeAndObject(subject,
                                                         privilege,
                                                         object);
        for (final Permission permission : permissions) {
            if (object != null) {
                object.removePermission(permission);
                ccmObjectRepository.save(object);
            }
            subject.removeGrantedPermission(permission);
            subjectRepository.save(subject);

            permissionRepository.delete(permission);
        }

    }

    /**
     * Checks if the the provided {@code subject} has a permission granting the
     * provided {@code privilege} on the provided {@code object}.
     *
     * The method will also check if the subject has a permission granting the
     * {@code admin} privilege on the provided object or on all objects. The
     * {@code admin} privilege implies that the user is granted all other
     * privileges on the object.
     *
     * If the provided subject is {@code null} the method will try to retrieve
     * the public user from the database. If there is no public user the method
     * will return {@code false}.
     *
     * @param privilege The privilege to check. Can't be {@code null}.
     * @param object    The object on which the privilege is granted. Can't be
     *                  {@code null}.
     * @param subject   The subject to which the privilege is granted. Can't be
     *                  {@code null}.
     *
     * @return {@code true} of the subject has a permission granting
     *         {@code privilege} on {@code object}, either explicit or implicit.
     *
     * @see UserRepository#retrievePublicUser()
     */
    public boolean isPermitted(final Privilege privilege,
                               final CcmObject object,
                               final Subject subject) {
        if (privilege == null) {
            throw new IllegalArgumentException(
                "Illegal value 'null' provided for parameter privilege");
        }

        //Check if the current subject is null. If yes try to retrieve the 
        //public user
        Subject subjectObj = null;
        if (subject == null) {
            final User publicUser = userRepository.retrievePublicUser();

            if (publicUser == null) {

                //If the public user is not available an null value for the 
                //subject parameter is an illegal argument.
                throw new IllegalArgumentException(
                    "Illegal value 'null' provided for parameter privilege");
            } else {
                subjectObj = publicUser;
            }
        } else {
            //Subject is not null. Use provided subject
            subjectObj = subject;
        }

        //Depending if the subject is a user or a group delegate to the correct
        //method. For users we have to check the permissions of the groups the
        //user is member of also, for group we don't have.
        if (subjectObj instanceof User) {
            return isPermitted(privilege, object, (User) subjectObj);
        } else if (subjectObj instanceof Group) {
            return isPermitted(privilege, object, (Group) subjectObj);
        } else {
            //For unknown subclasses of subject return false.
            return false;
        }
    }

    /**
     * Checks if a user has a permission granting a privilege on an object. If
     * the provided {@code object} is {@code null} the method will only check
     * for wildcard permission (permissions for all objects).
     *
     * @param privilege The privilege. Can't be {@code null}.
     * @param object    The object. Can be {@code null}.
     * @param user      The user. Can be {@code null}.
     *
     * @return {@code true} if the provided {@code user} has a permission
     *         granting the provided privilege for the provided object,
     *         {@code false} if not.
     */
    @SuppressWarnings("PMD.CyclomaticComplexity")
    private boolean isPermitted(final Privilege privilege,
                                final CcmObject object,
                                final User user) {
        boolean result = false;
        final Privilege admin = privilegeRepository.retrievePrivilege(
            Privilege.ADMIN);

        //Check if the there is direct permission granting the provided
        //privilege on the provided object. If the object is null or the
        //privilege is the admin privilege this check is not performed.
        if (object != null && !privilege.equals(admin)) {
            final List<Permission> permissions = permissionRepository
                .findPermissionsForUserPrivilegeAndObject(user, privilege,
                                                          object);
            result = !permissions.isEmpty();
        }

        //Check for an wildcard permission (a permission for all objects) for 
        //the provided privilege. This check is only performed when result is
        //still false the provided privilege is not the admin privilege.
        if (!result && !privilege.equals(admin)) {
            final List<Permission> permissions = permissionRepository
                .findPermissionsForUserPrivilegeAndObject(user, privilege, null);
            result = !permissions.isEmpty();
        }

        //Check for a permission granting the admin privilege on the provided 
        //object. This check is only performed if result is still false, 
        //the admin variable is not null (null means that there is no admin 
        //privilege) and the provided object is not null.
        if (!result && admin != null && object != null) {
            final List<Permission> permissions = permissionRepository
                .findPermissionsForUserPrivilegeAndObject(user,
                                                          admin,
                                                          object);
            result = !permissions.isEmpty();
        }

        //Check for a permission granting the admin privilege systemwide. This
        //check in only performed if result is still false and admin is not null.
        if (!result && admin != null) {
            final List<Permission> permissions = permissionRepository
                .findPermissionsForUserPrivilegeAndObject(user,
                                                          admin,
                                                          null);
            result = !permissions.isEmpty();
        }

        return result;
    }

    /**
     * Checks if a {@link Group} is granted a {@link Privilege} on a
     * {@link CcmObject} or on all {@link CcmObject}s.
     *
     * As for
     * {@link #isPermitted(org.libreccm.core.Privilege, org.libreccm.core.CcmObject, org.libreccm.core.User)},
     * this method also checks if the {@code admin} privilege was granted to the
     * group for the provided {@code object} or for all objects.
     *
     * @param privilege The privilege. Can't be {@code null}.
     * @param object    The object. Can be {@code null}.
     * @param group     The group. Can't be {@code null}.
     *
     * @return {@code true} if the group has a permission granting the provided
     *         {@code privilege} on the the provided {@code object} (or on all
     *         objects), {@code false} of not.
     *
     * @see #isPermitted(org.libreccm.core.Privilege,
     * org.libreccm.core.CcmObject, org.libreccm.core.Subject)
     * @see #isPermitted(org.libreccm.core.Privilege,
     * org.libreccm.core.CcmObject, org.libreccm.core.User)
     */
    public boolean isPermitted(final Privilege privilege,
                               final CcmObject object,
                               final Group group) {
        boolean result;

        final List<Permission> directPermissions = permissionRepository
            .findPermissionsForSubjectPrivilegeAndObject(group,
                                                         privilege,
                                                         object);
        result = !directPermissions.isEmpty();

        if (!result) {
            final List<Permission> permissions = permissionRepository
                .findPermissionsForSubjectPrivilegeAndObject(group,
                                                             privilege,
                                                             null);
            result = !permissions.isEmpty();
        }

        if (!result) {
            final Privilege admin = privilegeRepository.retrievePrivilege(
                "admin");
            if (admin != null) {
                final List<Permission> permissions = permissionRepository
                    .findPermissionsForSubjectPrivilegeAndObject(group,
                                                                 admin,
                                                                 object);
                result = !permissions.isEmpty();
            }
        }

        if (!result) {
            final Privilege admin = privilegeRepository.retrievePrivilege(
                "admin");
            if (admin != null) {
                final List<Permission> permissions = permissionRepository
                    .findPermissionsForSubjectPrivilegeAndObject(group,
                                                                 admin,
                                                                 null);
                result = !permissions.isEmpty();
            }
        }

        return result;
    }

    /**
     * Checks if the the provided {@code subject} has a permission granting the
     * provided {@code privilege} on the provided {@code object}.
     *
     * If the provided subject is {@code null} the method will try to retrieve
     * the public user from the database. If there is no public user the method
     * will return {@code false}.
     *
     * Internally this methods calls
     * {@link #isPermitted(org.libreccm.core.Privilege, org.libreccm.core.CcmObject, org.libreccm.core.Subject)}
     * and throws an {@link UnauthorizedAcccessException} if the return value is
     * {@code null}.
     *
     * @param privilege The privilege to check. Can't be {@code null}.
     * @param object    The object on which the privilege is granted. Can't be
     *                  {@code null}.
     * @param subject   The subject to which the privilege is granted. Can't be
     *                  {@code null}.
     *
     * @throws UnauthorizedAcccessException If there is no permission granting
     *                                      {@code privilege} on {@code object}
     *                                      to {@code subject}
     *
     * @see #isPermitted(org.libreccm.core.Privilege,
     * org.libreccm.core.CcmObject, org.libreccm.core.Subject)
     */
    public void checkPermission(final Privilege privilege,
                                final CcmObject object,
                                final Subject subject)
        throws UnauthorizedAcccessException {
        if (!isPermitted(privilege, object, subject)) {
            throw new UnauthorizedAcccessException(String.format(
                "Privilege \"%s\" has not been granted to subject \"%s\" "
                    + "on object \"%s\".",
                privilege.getLabel(),
                subject.toString(),
                object.toString()));
        }
    }

}
