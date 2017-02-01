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


import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.libreccm.cdi.utils.CdiUtil;

import java.util.List;

/**
 * Implementation of Shiro's {@link AuthorizingRealm} to provide Shiro with the
 * users, groups, roles and permissions stored in CCM's database.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@SuppressWarnings({"PMD.CyclomaticComplexity",
                   "PMD.ModifiedCyclomaticComplexity",
                   "PMD.StdCyclomaticComplexity"})
public class CcmShiroRealm extends AuthorizingRealm {

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(
        final PrincipalCollection principals) {

        final CcmShiroRealmController controller;
        try {
            controller = CdiUtil.createCdiUtil()
                .findBean(CcmShiroRealmController.class);
        } catch (IllegalStateException ex) {
            throw new AuthenticationException(
                "Failed to retrieve CcmShiroRealmController", ex);
        }

        // Get the pricipal (object identifing the user).
        final Object principal = principals.getPrimaryPrincipal();

        // This realm expects the principal to be a string.
        if (!(principal instanceof String)) {
            throw new AuthenticationException(String.format(
                "Can' process principal of "
                    + "type \"%s\".",
                principal.getClass().getName()));
        }
        // Convert the pricipal to a string.
        final String userIdentifier = (String) principal;

        // Return the permissions of the system user
        if ("system-user".equals(userIdentifier)) {
            // The system user is a virtual user which has all roles and all
            // privileges

            final List<Role> roles = controller.retrieveAllRoles();

            final SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            for (final Role role : roles) {
                info.addRole(role.getName());
            }
            info.addStringPermission("*");

            return info;
        }

        return controller.createAuthorizationInfo(userIdentifier);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
        final AuthenticationToken token)
        throws AuthenticationException {

        // Get the pricipal identifing the user
        final Object principal = token.getPrincipal();

        // This realm expects the pricipal to be a string
        if (!(principal instanceof String)) {
            throw new AuthenticationException(String.format(
                "Can' process authentication token with a principal of "
                    + "type \"%s\".",
                principal.getClass().getName()));
        }

        final CcmShiroRealmController controller;
        try {
            controller = CdiUtil.createCdiUtil()
                .findBean(CcmShiroRealmController.class);
        } catch (IllegalStateException ex) {
            throw new AuthenticationException(
                "Failed to retrieve CcmShiroRealmController", ex);
        }
        
        // Convert the pricipal to a string.
        final String userIdentifier = (String) principal;
        // Find the user identified by the pricipal.
        final User user = controller.findUser(userIdentifier);

        // Return a SimpleAuthenticationInfo with the information relevant
        // for Shiro
        return new SimpleAuthenticationInfo(token.getPrincipal(),
                                            user.getPassword(),
                                            "CcmShiroRealm");
    }

    /**
     * Helper method for converting a {@link Permission} to the string format
     * used by Shiro.
     *
     * @param permission The permission to convert.
     *
     * @return A Shiro permission string.
     */
    private String permissionToString(final Permission permission) {
        if (permission.getObject() == null) {
            return permission.getGrantedPrivilege();
        } else {
            return String.format("%s:%d",
                                 permission.getGrantedPrivilege(),
                                 permission.getObject().getObjectId());
        }
    }

}
