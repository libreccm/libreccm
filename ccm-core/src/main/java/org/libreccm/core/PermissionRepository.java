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
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 * Repository class for retrieving, storing and deleting permissions.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class PermissionRepository
    extends AbstractEntityRepository<Long, Permission> {

    @Inject
    private transient EntityManager entityManager;

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Class<Permission> getEntityClass() {
        return Permission.class;
    }

    /**
     * {@inheritDoc }
     *
     * @param entity {@inheritDoc }
     *
     * @return {@inheritDoc }
     */
    @Override
    public boolean isNew(final Permission entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity to save can't be null");
        }
        return entity.getPermissionId() == 0;
    }

    /**
     * Finds all permissions granted to a specific subject (either a user or
     * group).
     *
     * Please note that this method does <strong>not</strong> find permissions
     * granted to a user by the groups a user is member of. To get these
     * permissions also use the
     * {@link #findPermissionsForUser(org.libreccm.core.User)} method instead.
     *
     * @param subject The subject.
     *
     * @return All permissions granted to the provided subject.
     */
    public List<Permission> findPermissionsForSubject(final Subject subject) {
        if (subject == null) {
            throw new IllegalArgumentException(
                "Illegal value 'null' provided for parameter subject.");
        }

        final TypedQuery<Permission> query = entityManager.createNamedQuery(
            "findPermissionsForSubject", Permission.class);
        query.setParameter("subject", subject);

        return query.getResultList();
    }

    /**
     * Finds a permissions granted to a user and to the groups the user is
     * member of.
     *
     * If you only need the permissions assigned to the user itself use the
     * {@link #findPermissionsForSubject(org.libreccm.core.Subject)} method.
     *
     * @param user The user.
     *
     * @return All permissions granted to the user or the groups the user is
     *         member of.
     */
    public List<Permission> findPermissionsForUser(final User user) {
        if (user == null) {
            throw new IllegalArgumentException(
                "Illegal value 'null' provided for parameter user");
        }

        final TypedQuery<Permission> query = entityManager.createNamedQuery(
            "findPermissionsForUser", Permission.class);
        query.setParameter("user", user);

        return query.getResultList();
    }

    /**
     * Finds all permissions granted on a object.
     *
     * @param object The object.
     *
     * @return All permissions granted on the object.
     */
    public List<Permission> findPermissionsForCcmObject(final CcmObject object) {
        if (object == null) {
            throw new IllegalArgumentException(
                "Illegal value 'null' provided for parameter object.");
        }

        final TypedQuery<Permission> query = entityManager.createNamedQuery(
            "findPermissionsForCcmObject", Permission.class);
        query.setParameter("object", object);

        return query.getResultList();
    }

    public List<Permission> findPermissionsForUserPrivilegeAndObject(
        final User user,
        final Privilege privilege,
        final CcmObject object) {

        if (user == null) {
            throw new IllegalArgumentException(
                "Illegal value 'null' provided for parameter user");
        }

        if (privilege == null) {
            throw new IllegalArgumentException(
                "Illegal value 'null' provided for parameter privilege");
        }

        final TypedQuery<Permission> query;
        if (object == null) {
            query = entityManager.createNamedQuery(
                "findWildcardPermissionsForUserPrivilegeAndObject",
                Permission.class);

        } else {
            query = entityManager.createNamedQuery(
                "findPermissionsForUserPrivilegeAndObject", Permission.class);
            query.setParameter("object", object);
        }

        query.setParameter("user", user);
        query.setParameter("privilege", privilege);

        return query.getResultList();
    }

    public List<Permission> findPermissionsForSubjectPrivilegeAndObject(
        final Subject subject,
        final Privilege privilege,
        final CcmObject object) {

        if (subject == null) {
            throw new IllegalArgumentException(
                "Illegal value 'null' provided for parameter subject");
        }

        if (privilege == null) {
            throw new IllegalArgumentException(
                "Illegal value 'null' provided for parameter privilege");
        }

        final TypedQuery<Permission> query;

        if (object == null) {
            query = entityManager.createNamedQuery(
                "findWildcardPermissionsForSubjectPrivilegeAndObject",
                Permission.class);
        } else {
            query = entityManager.createNamedQuery(
                "findPermissionsForSubjectPrivilegeAndObject", Permission.class);

            query.setParameter("object", object);
        }

        query.setParameter("subject", subject);
        query.setParameter("privilege", privilege);

        return query.getResultList();
    }

}
