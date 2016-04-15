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

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.libreccm.core.CcmObject;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;

/**
 * Manager class for granting and revoking permissions.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class PermissionManager {

    @SuppressWarnings("PMD.LongVariable")
    private static final String QUERY_PARAM_OBJECT = "object";
    @SuppressWarnings("PMD.LongVariable")
    private static final String QUERY_PARAM_GRANTEE = "grantee";
    @SuppressWarnings("PMD.LongVariable")
    private static final String QUERY_PARAM_PRIVILEGE = "privilege";

    
    @Inject
    private EntityManager entityManager;

    /**
     * Retrieves a permission by its ID. Useful for UI classes.
     * 
     * @param permissionId The id of the permission to retrieve.
     * @return The permission identified by the provided {@code permissionId).
     */
    public Permission findById(final long permissionId) {
        return entityManager.find(Permission.class, permissionId);
    }
    
    /**
     * Grants a privilege on an object to a role.  If the privilege was already 
     * granted, the method does nothing.
     * 
     * @param privilege The privilege to grant.
     * @param grantee The role to which the privilege is granted.
     * @param object The object on which the privilege is granted.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void grantPrivilege(final String privilege,
                               final Role grantee,
                               final CcmObject object) {
        if (privilege == null || privilege.isEmpty()) {
            throw new IllegalArgumentException(
                "Can't grant a permission without a privilege.");
        }

        if (grantee == null) {
            throw new IllegalArgumentException(
                "Can't grant a permission to grantee null.");
        }
        
        if (object == null) {
            throw new IllegalArgumentException(
                "Can't grant a permission on object NULL.");
        }

        if (!existsPermission(privilege, grantee, object)) {
            final Permission permission = new Permission();
            permission.setGrantee(grantee);
            permission.setGrantedPrivilege(privilege);
            permission.setObject(object);

            entityManager.persist(permission);
        }
    }

    /**
     * Grants a privilege to a role. If the privilege was already granted, the 
     * method does nothing.
     * 
     * @param privilege The privilege to grant.
     * @param grantee The role to which the privilege is granted.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void grantPrivilege(final String privilege,
                               final Role grantee) {
        if (privilege == null || privilege.isEmpty()) {
            throw new IllegalArgumentException(
                "Can't grant a permission without a privilege.");
        }

        if (grantee == null) {
            throw new IllegalArgumentException(
                "Can't grant a permission to grantee null.");
        }

        if (!existsPermission(privilege, grantee)) {
            final Permission permission = new Permission();
            permission.setGrantee(grantee);
            permission.setGrantedPrivilege(privilege);
            permission.setObject(null);

            entityManager.persist(permission);
        }
    }

    /**
     * Revokes the permissions granting a privilege on an object from a role.
     * If no matching permission exists the method will do nothing.
     * 
     * @param privilege The privilege granted by the permission to revoke.
     * @param grantee The role to which the privilege was granted.
     * @param object The object on which the privilege was granted.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void revokePrivilege(final String privilege,
                                final Role grantee,
                                final CcmObject object) {
        if (privilege == null || privilege.isEmpty()) {
            throw new IllegalArgumentException(
                "Can't revoke a permission without a privilege.");
        }

        if (grantee == null) {
            throw new IllegalArgumentException(
                "Can't revoke a permission from grantee null.");
        }
        
        if (object == null) {
            throw new IllegalArgumentException(
                "Can't revoke a permission from object NULL.");
        }

        if (existsPermission(privilege, grantee, object)) {
            final Query query = entityManager.createQuery(
                "DELETE FROM Permission p "
                    + "WHERE p.grantedPrivilege = :privilege "
                    + "AND p.grantee = :grantee "
                    + "AND p.object = :object");
            query.setParameter(QUERY_PARAM_PRIVILEGE, privilege);
            query.setParameter(QUERY_PARAM_GRANTEE, grantee);
            query.setParameter(QUERY_PARAM_OBJECT, object);
            query.executeUpdate();
        }
    }
    
     /**
     * Revokes the permissions granting a privilege from a role.
     * If no matching permission exists the method will do nothing.
     * 
     * @param privilege The privilege granted by the permission to revoke.
     * @param grantee The role to which the privilege was granted.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void revokePrivilege(final String privilege,
                                final Role grantee) {
        if (privilege == null || privilege.isEmpty()) {
            throw new IllegalArgumentException(
                "Can't revoke a permission without a privilege.");
        }

        if (grantee == null) {
            throw new IllegalArgumentException(
                "Can't revoke a permission from grantee null.");
        }

        if (existsPermission(privilege, grantee)) {
            final Query query = entityManager.createQuery(
                "DELETE FROM Permission p "
                    + "WHERE p.grantedPrivilege = :privilege "
                    + "AND p.grantee = :grantee "
                    + "AND p.object IS NULL");
            query.setParameter(QUERY_PARAM_PRIVILEGE, privilege);
            query.setParameter(QUERY_PARAM_GRANTEE, grantee);
            query.executeUpdate();
        }
    }

    /**
     * Copy the permissions from on {@link CcmObject} to another. The 
     * permissions granted on the {@code target} object will not be removed. 
     * Instead the permissions from {@code source} object are added the the 
     * permissions.
     * 
     * 
     * @param source
     * @param target 
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void copyPermissions(final CcmObject source,
                                final CcmObject target) {
        if (source == null) {
            throw new IllegalArgumentException(
                "Can't copy permissions from source NULL.");
        }
        
        if (target == null) {
            throw new IllegalArgumentException(
                "Can't copy permissions to target NULL.");
        }

        final TypedQuery<Permission> query = entityManager.createNamedQuery(
            "Permission.findPermissionsForCcmObject", Permission.class);
        query.setParameter(QUERY_PARAM_OBJECT, source);
        final List<Permission> result = query.getResultList();

        for (final Permission permission : result) {
            grantPrivilege(permission.getGrantedPrivilege(),
                           permission.getGrantee(),
                           target);
        }
    }

    /**
     * Checks if a permission granting the provided {@code privilege} on the
     * provided {@code object} to the provided {@code role} exists.
     * 
     * @param privilege The privilege granted by the permission.
     * @param grantee The role to which the privilege was granted.
     * @param object The object on which the privilege is granted.
     * @return {@code true} if there is a matching permission, {@code false} if
     * not.
     */
    private boolean existsPermission(final String privilege,
                                     final Role grantee,
                                     final CcmObject object) {
        final TypedQuery<Long> query = entityManager.createNamedQuery(
            "Permission.existsForPrivilegeRoleObject", Long.class);
        query.setParameter(QUERY_PARAM_PRIVILEGE, privilege);
        query.setParameter(QUERY_PARAM_GRANTEE, grantee);
        query.setParameter(QUERY_PARAM_OBJECT, object);

        return query.getSingleResult() > 0;
    }

    /**
     * Checks if a permission granting the provided {@code privilege}to the 
     * provided {@code role} exists.
     * 
     * @param privilege The privilege granted by the permission.
     * @param grantee The role to which the privilege was granted.
     * @return {@code true} if there is a matching permission, {@code false} if
     * not.
     */
    private boolean existsPermission(final String privilege,
                                     final Role grantee) {
        final TypedQuery<Long> query = entityManager.createNamedQuery(
            "Permission.existsForPrivilegeAndRole", Long.class);
        query.setParameter(QUERY_PARAM_PRIVILEGE, privilege);
        query.setParameter(QUERY_PARAM_GRANTEE, grantee);

        return query.getSingleResult() > 0;
    }

}
