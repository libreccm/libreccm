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
import javax.persistence.NoResultException;
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
     * @param label The name of the privilege to return.
     *
     * @return The requested privilege.
     *
     * @throws UnknownPrivilegeException if there is no privilege identified by
     *                                   the provided {@code name}.
     */
    public Privilege retrievePrivilege(final String label) {
        final TypedQuery<Privilege> query = entityManager.createNamedQuery(
            "Privilege.findPrivilegeByName", Privilege.class);
        query.setParameter("label", label);

        try {
            return query.getSingleResult();
        } catch (NoResultException ex) {
            throw new UnknownPrivilegeException(String.format(
                "There is no privilege \"%s\".", label), ex);
        }
    }

    /**
     * Creates a new {@link Privilege}.
     *
     * This method can only be invoked by the system user.
     *
     * ToDo: Check if current user is system user.
     *
     * @param label The privilege to create.
     *
     * @return The new privilege.
     */
    public Privilege createPrivilege(final String label) {
        final Privilege privilege = new Privilege();
        privilege.setLabel(label);

        entityManager.persist(privilege);

        return privilege;
    }

    /**
     * Deletes a {@link Privilege}.
     *
     * ToDo: Check if current user is system user.
     *
     * @param privilegeName The privilege to delete.
     *
     * @throws UnknownPrivilegeException if there is no privilege identified by
     *                                   the provided {@code name}.
     */
    public void deletePrivilege(final String privilegeName) {
        if (isPrivilegeInUse(privilegeName)) {
            throw new IllegalArgumentException(
                "Provided privilage can't be removed because its still in use");
        }

        final Privilege privilege = retrievePrivilege(privilegeName);
        entityManager.remove(privilege);
    }

    /**
     * Checks a {@link Privilege} is in use.
     *
     * @param label The name of the privilege to check.
     *
     * @return {@code true} if the privilege is in use (there is a least one
     *         permission using it), {@code false} otherwise.
     */
    public boolean isPrivilegeInUse(final String label) {
        final TypedQuery<Long> query = entityManager.createNamedQuery(
            "Privilege.isPrivilegeInUse", Long.class);
        query.setParameter("label", label);

        final Long result = query.getSingleResult();

        return result > 0;
    }

}
