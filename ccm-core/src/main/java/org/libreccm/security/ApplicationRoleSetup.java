/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
import org.libreccm.web.AbstractCcmApplicationSetup;

import javax.persistence.EntityManager;

/**
 * A helper class used by the {@link AbstractCcmApplicationSetup} to create
 * roles and permissions for the roles. This class is necessary because some
 * constructors and methods of the classes {@link Role} and {@link Permission}
 * are only accessible from this package.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ApplicationRoleSetup {

    private final EntityManager entityManager;

    public ApplicationRoleSetup(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Role createRole(final String name) {
        final Role role = new Role();
        role.setName(name);

        entityManager.persist(role);

        return role;
    }
    
    private Permission createPermission(final Role role, final String privilege) {
        final Permission permission = new Permission();
        permission.setGrantedPrivilege(privilege);
        permission.setGrantee(role);
        role.addPermission(permission);
        
        return permission;
    }
    
    public void grantPermission(final Role role, final String privilege) {
        final Permission permission = createPermission(role, privilege);
        
        entityManager.persist(permission);
        entityManager.merge(role);
    }
    
    public void grantPermission(final Role role, 
                                final String privilege,
                                final CcmObject ccmObject) {
        final Permission permission = createPermission(role, privilege);
        permission.setObject(ccmObject);
        
        entityManager.persist(permission);
        entityManager.merge(role);
    }

}
