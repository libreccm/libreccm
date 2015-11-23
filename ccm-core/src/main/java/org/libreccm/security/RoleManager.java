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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 * Manager for roles providing methods for assigning the role the {@link Party}
 * entities and for removing them.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class RoleManager {

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private PartyRepository partyRepository;

    @Inject
    private EntityManager entityManager;

    /**
     * Assigns a role to a party and saves the changed {@code Role} and
     * {@code Party} entities. If the provided {@code role} has already been
     * assigned to the provided {@code party} the method will to nothing.
     *
     * @param role  The role to assign.
     * @param party The party which to which to role is assigned.
     */
    public void assignRoleToParty(final Role role, final Party party) {
        if (role == null) {
            throw new IllegalArgumentException("Can't add party to null role");
        }

        if (party == null) {
            throw new IllegalArgumentException("Can't add party null to role");
        }

        if (hasRole(party, role)) {
            return;
        }

        final RoleMembership membership = new RoleMembership();
        membership.setRole(role);
        membership.setMember(party);

        role.addMembership(membership);
        party.addRoleMembership(membership);

        entityManager.persist(membership);
        roleRepository.save(role);
        partyRepository.save(party);
    }

    /**
     * Removes a role from a party and saves the changed {@code Role} and
     * {@code Party} entities. If the provided {@code role} is not assigned to
     * the provided {@code party} the method does nothing.
     *
     * @param role
     * @param party
     */
    public void removeRoleFromParty(final Role role, final Party party) {
        if (role == null) {
            throw new IllegalArgumentException("Can't add party to null role");
        }

        if (party == null) {
            throw new IllegalArgumentException("Can't add party null to role");
        }

        final TypedQuery<RoleMembership> query = entityManager
            .createNamedQuery("RoleMembership.findByRoleAndMember",
                              RoleMembership.class);
        query.setParameter("member", party);
        query.setParameter("role", role);

        final RoleMembership delete;
        try {
            delete = query.getSingleResult();
        } catch (NoResultException ex) {
            return;
        }

        role.removeMembership(delete);
        party.removeRoleMembership(delete);
        entityManager.remove(delete);
        roleRepository.save(role);
        partyRepository.save(party);
    }

    /**
     * Determines if a role is assigned to a party.
     *  
     * @param party The party to check.
     * @param role The role to check.
     * @return {@code true} if the provided {@code role} is assigned to the
     * provided {@code party}.
     */
    public boolean hasRole(final Party party, final Role role) {
        final TypedQuery<RoleMembership> query = entityManager
            .createNamedQuery("RoleMembership.findByRoleAndMember",
                              RoleMembership.class);
        query.setParameter("member", party);
        query.setParameter("role", role);

        final List<RoleMembership> result = query.getResultList();
        return !result.isEmpty();
    }

}
