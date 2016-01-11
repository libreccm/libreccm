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

import com.arsdigita.kernel.LegacyKernelConfig;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;

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
            final RoleRepository roleRepository;
            final BeanManager beanManager = CDI.current().getBeanManager();
            final Set<Bean<?>> beans = beanManager.
                getBeans(RoleRepository.class);
            final Iterator<Bean<?>> iterator = beans.iterator();
            if (iterator.hasNext()) {
                @SuppressWarnings("unchecked")
                final Bean<RoleRepository> bean
                                           = (Bean<RoleRepository>) iterator.
                    next();
                final CreationalContext<RoleRepository> ctx = beanManager.
                    createCreationalContext(bean);

                roleRepository = (RoleRepository) beanManager.getReference(
                    bean, RoleRepository.class, ctx);
            } else {
                throw new AuthenticationException(
                    "Failed to retrieve RoleRepository");
            }

            final List<Role> roles = roleRepository.findAll();

            final SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            for (final Role role : roles) {
                info.addRole(role.getName());
            }
            info.addStringPermission("*");

            return info;
        }

        //Find the user identified by the provided pricipal.
        final User user = findUser(userIdentifier);

        // Create a SimpleAuthorizationInfo instance. Contains the information
        // from the database in the format expected by Shiro.
        final SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // Get the Roles directly assigned to the user.
        for (final RoleMembership roleMembership : user.getRoleMemberships()) {
            // Add the role to the AuthorizationInfo object.
            info.addRole(roleMembership.getRole().getName());

            // Add the permissions assigned to the role to the AuthorizatonInfo. 
            for (final Permission permission : roleMembership.getRole()
                .getPermissions()) {
                info.addStringPermission(permissionToString(permission));
            }
        }

        //Get the Roles assigned to the groups of which the user is member of.
        for (final GroupMembership membership : user.getGroupMemberships()) {
            // Get the roles assigned to the group
            for (final RoleMembership roleMembership : membership.getGroup()
                .getRoleMemberships()) {
                // Add the role to the AuthorizationInfo
                info.addRole(roleMembership.getRole().getName());
                // Add the permissions assigned to the role to the 
                // AuthorizationInfo
                for (final Permission permission : roleMembership.getRole()
                    .getPermissions()) {
                    info.addStringPermission(permissionToString(permission));
                }
            }
        }

        return info;
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

        // Convert the pricipal to a string.
        final String userIdentifier = (String) principal;
        // Find the user identified by the pricipal.
        final User user = findUser(userIdentifier);

        // Return a SimpleAuthenticationInfo with the information relevant
        // for Shiro
        return new SimpleAuthenticationInfo(token.getPrincipal(),
                                            user.getPassword(),
                                            "CcmShiroRealm");
    }

    /**
     * Helper method for finding a user by its identifier. Depending on the
     * configuration of CCM this is either the name of the user or the email
     * address of the user.
     *
     * @param userIdentifier The identifier of the user.
     *
     * @return The User identified by the provided {@code userIdentifier}.
     *
     * @throws AuthenticationException if no user for the provided identifier
     *                                 could be retrieved.
     */
    private User findUser(final String userIdentifier) {
        // For some reason we can't use the the CdiUtil class here, therefore
        // we have to do the lookup for the UserRepository be ourself.
        final UserRepository userRepository;
        final BeanManager beanManager = CDI.current().getBeanManager();
        final Set<Bean<?>> beans = beanManager.getBeans(
            UserRepository.class);
        final Iterator<Bean<?>> iterator = beans.iterator();
        if (iterator.hasNext()) {
            @SuppressWarnings("unchecked")
            final Bean<UserRepository> bean = (Bean<UserRepository>) iterator
                .next();
            final CreationalContext<UserRepository> ctx = beanManager
                .createCreationalContext(bean);

            userRepository = (UserRepository) beanManager.getReference(
                bean, UserRepository.class, ctx);
        } else {
            throw new AuthenticationException(
                "Failed to retrieve UserRepository.");
        }

        // Depending of the configuration of CCM use the appropriate method
        // for finding the user in the database.
        final LegacyKernelConfig config = LegacyKernelConfig.getConfig();
        final User user;
        if ("email".equals(config.getPrimaryUserIdentifier())) {
            user = userRepository.findByEmailAddress(userIdentifier);
        } else {
            user = userRepository.findByName(userIdentifier);
        }

        // If no matching user is found throw an AuthenticationException
        if (user == null) {
            throw new AuthenticationException(String.format(
                "No user identified by principal \"%s\" was found. Primary user "
                + "identifier is \"%s\".",
                userIdentifier, config.getPrimaryUserIdentifier()));
        }

        return user;
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
