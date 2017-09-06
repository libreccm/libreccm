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

import org.libreccm.core.AbstractEntityRepository;
import org.libreccm.core.CcmObject;
import org.libreccm.core.CoreConstants;

import javax.enterprise.context.RequestScoped;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Repository class for {@link Role} entities.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class RoleRepository extends AbstractEntityRepository<Long, Role> {

    @Override
    public Class<Role> getEntityClass() {
        return Role.class;
    }

    @Override
    public boolean isNew(final Role entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Can't save null.");
        }
        return entity.getRoleId() == 0;
    }

    public long count() {
        final TypedQuery<Long> query = getEntityManager().createNamedQuery(
            "Role.count", Long.class);
        return query.getSingleResult();
    }

    /**
     * Finds a role a its name.
     *
     * @param name The name of the role to retrieve.
     *
     * @return The role identified by the provided {@code name} or {@code null}
     *         if there is no matching role.
     */
    public Optional<Role> findByName(final String name) {
        final TypedQuery<Role> query = getEntityManager().createNamedQuery(
            "Role.findByName", Role.class);
        query.setParameter("name", name);
        final List<Role> result = query.getResultList();
        if (result.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(result.get(0));
        }
    }

    public List<Role> findAllOrderedByRoleName() {
        final TypedQuery<Role> query = getEntityManager().createNamedQuery(
            "Role.findAllOrderedByRoleName", Role.class);
        return query.getResultList();
    }

    public List<Role> findAllOrderedByRole(final int maxResults,
                                           final int firstResult) {
        final TypedQuery<Role> query = getEntityManager().createNamedQuery(
            "Role.findAllOrderedByRoleName", Role.class);
        query.setMaxResults(maxResults);
        query.setFirstResult(firstResult);
        return query.getResultList();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Role> findByParty(final Party party) {
        final TypedQuery<Role> query = getEntityManager()
            .createNamedQuery("Role.findByParty", Role.class);
        query.setParameter("member", party);

        return query.getResultList();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Role> findByPrivilege(final String privilege) {
        final TypedQuery<Role> query = getEntityManager().createNamedQuery(
            "Role.findByPrivilege", Role.class);
        query.setParameter("privilege", privilege);

        return query.getResultList();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Role> findByPrivilege(final String privilege,
                                      final CcmObject object) {
        final TypedQuery<Role> query = getEntityManager().createNamedQuery(
            "Role.findByPrivilegeAndObject", Role.class);
        query.setParameter("privilege", privilege);
        query.setParameter("object", object);

        return query.getResultList();
    }

    public List<Role> searchByName(final String name) {
        final TypedQuery<Role> query = getEntityManager().createNamedQuery(
            "Role.searchByName", Role.class);
        query.setParameter("name", name);
        return query.getResultList();
    }

    public List<Role> searchByName(final String name,
                                   final int maxResults,
                                   final int firstResult) {
        final TypedQuery<Role> query = getEntityManager().createNamedQuery(
            "Role.searchByName", Role.class);
        query.setParameter("name", name);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    public long searchByNameCount(final String name) {
        final TypedQuery<Long> query = getEntityManager().createNamedQuery(
            "Role.searchByNameCount", Long.class);
        query.setParameter("name", name);
        try {
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return 0;
        }
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void save(final Role role) {
        super.save(role);
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void delete(final Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Can't delete null.");
        }

        final Role delete = getEntityManager().find(Role.class,
                                                    role.getRoleId());

        delete.getMemberships().forEach(m -> {
            getEntityManager().remove(m);
        });

        getEntityManager().remove(delete);
    }

}
