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

import com.arsdigita.kernel.KernelConfig;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.libreccm.configuration.ConfigurationManager;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * This bean provides several services for the {@link CcmShiroRealm}. It wraps
 * several calls into a transaction to avoid
 * {@code LazyInitializationException}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class CcmShiroRealmController {

    @Inject
    private UserRepository userRepo;

    @Inject
    private GroupRepository groupRepo;

    @Inject
    private RoleRepository roleRepo;

    @Inject
    private ConfigurationManager confManager;

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<Role> retrieveAllRoles() {
        return roleRepo.findAll();
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
    @Transactional(Transactional.TxType.REQUIRED)
    protected User findUser(final String userIdentifier) {
        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);

        final User user;
        if ("email".equals(kernelConfig.getPrimaryUserIdentifier())) {
            user = userRepo.findByEmailAddress(userIdentifier);
        } else {
            user = userRepo.findByName(userIdentifier);
        }

        // If no matching user is found throw an AuthenticationException
        if (user
                == null) {
            throw new AuthenticationException(String.format(
                "No user identified by principal \"%s\" was found. Primary user "
                + "identifier is \"%s\".",
                userIdentifier, kernelConfig.getPrimaryUserIdentifier()));
        }

        return user;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected AuthorizationInfo createAuthorizationInfo(
        final String userIdentifier) {

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
