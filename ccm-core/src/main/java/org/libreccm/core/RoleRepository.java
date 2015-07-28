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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class RoleRepository extends AbstractEntityRepository<Long, Role> {

    @Inject
    private transient EntityManager entityManager;

    @Override
    public Class<Role> getEntityClass() {
        return Role.class;
    }

    @Override
    public boolean isNew(final Role entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Role to save can't be null.");
        }
        return entity.getRoleId() == 0;
    }

    /**
     * Find role(s) by name. There can be several roles with the same name.
     * 
     * @param roleName
     * @return A list of roles identified by name.
     */
    public List<Role> findRolesForName(final String roleName) {
        final TypedQuery<Role> query = entityManager.createNamedQuery(
            "findRolesForName", Role.class);
        query.setParameter("roleName", roleName);

        return query.getResultList();
    }
    
    public List<Role> findRolesForSourceGroup(final Group group) {
        final TypedQuery<Role> query = entityManager.createNamedQuery(
            "findRolesForSourceGroup", Role.class);
        query.setParameter("sourceGroup", group);
        
        return query.getResultList();
    }
    
    public List<Role> findRolesForImplicitGroup(final Group group) {
        final TypedQuery<Role> query = entityManager.createNamedQuery(
            "findRolesForImplicitGroup", Role.class);
        query.setParameter("implicitGroup", group);
        
        return query.getResultList();
    }

}
