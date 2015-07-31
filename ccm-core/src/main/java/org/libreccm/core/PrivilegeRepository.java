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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 * Repository class for retrieving and creating new {@link Privilege}s.
 *
 * When the {@link Privilege} class is refactored to an enum this class will be
 * removed.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class PrivilegeRepository {

    @Inject
    private transient EntityManager entityManager;

    /**
     * Finds the {@link Privilege} identified by {@code name}.
     *
     * @param privilege The name of the privilege to return.
     *
     * @return
     */
    public Privilege retrievePrivilege(final String privilege) {
        final TypedQuery<Privilege> query = entityManager.createNamedQuery(
            "findPrivilegeByName", Privilege.class);
        query.setParameter("name", privilege);

        return query.getSingleResult();
    }

    /**
     * Creates a new {@link Privilege}.
     *
     * This method can only be invoked by the system user.
     *
     * ToDo: Check if current user is system user.
     *
     * @param privilegeName The privilege to create.
     *
     * @return The new privilege.
     */
    public Privilege createPrivilege(final String privilegeName) {
        final Privilege privilege = new Privilege();
        privilege.setPrivilege(privilegeName);

        entityManager.persist(privilege);

        return privilege;
    }

    /**
     * Deletes a {@link Privilege}.
     *
     * ToDo: Check if current user is system user.
     *
     * @param privilegeName The privilege to delete.
     */
    public void deletePrivilege(final String privilegeName) {
        final Privilege privilege = retrievePrivilege(privilegeName);

        if (isPrivilegeInUse(privilegeName)) {
            throw new IllegalArgumentException(
                "Provided privilage can't be removed because its still in use");
        }

        if (privilege != null) {
            entityManager.remove(privilege);
        }
    }

    public boolean isPrivilegeInUse(final String privilegeName) {
        final TypedQuery<Integer> query = entityManager.createNamedQuery(
            "isPrivilegeInUse", Integer.class);
        query.setParameter("name", privilegeName);

        final Integer result = query.getSingleResult();

        return result > 0;
    }

}
