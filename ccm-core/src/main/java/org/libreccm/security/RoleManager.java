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

import org.libreccm.core.CoreConstants;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manager for roles providing methods for assigning the role the {@link Party}
 * entities and for removing them.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class RoleManager implements Serializable {

    private static final long serialVersionUID = -3012991584385998270L;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private PartyRepository partyRepository;

    @Inject
    private EntityManager entityManager;

    @Inject
    private PermissionChecker permissionChecker;

    /**
     * Assigns a role to a party and saves the changed {@code Role} and
     * {@code Party} entities. If the provided {@code role} has already been
     * assigned to the provided {@code party} the method will to nothing.
     *
     * @param role  The role to assign.
     * @param party The party which to which to role is assigned.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
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
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
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
     * @param role  The role to check.
     *
     * @return {@code true} if the provided {@code role} is assigned to the
     *         provided {@code party}.
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

    /**
     * Finds all roles directly or indirectly assigned to a user.
     *
     * @param user The user
     *
     * @return A list of all roles assigned to the user or to a group the user
     *         is a member of, sorted by name.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Role> findAllRolesForUser(final User user) {

        final List<Role> directlyAssigned = user
            .getRoleMemberships()
            .stream()
            .map(membership -> membership.getRole())
            .collect(Collectors.toList());

        final Set<Role> roles = new HashSet<>(directlyAssigned);

        final List<Group> groups = user
            .getGroupMemberships()
            .stream()
            .map(membership -> membership.getGroup())
            .collect(Collectors.toList());

        for (final Group group : groups) {
            roles.addAll(group
                .getRoleMemberships()
                .stream()
                .map(membership -> membership.getRole())
                .collect(Collectors.toList()));
        }

        return new ArrayList<>(roles);
    }

}
