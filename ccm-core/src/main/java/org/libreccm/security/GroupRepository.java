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
import javax.persistence.TypedQuery;

import org.libreccm.core.AbstractEntityRepository;
import org.libreccm.core.CoreConstants;

import javax.transaction.Transactional;

/**
 * Repository for groups.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class GroupRepository extends AbstractEntityRepository<Long, Group> {

    @Override
    public Class<Group> getEntityClass() {
        return Group.class;
    }

    @Override
    public boolean isNew(final Group entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Can't save null");
        }

        return entity.getPartyId() == 0;
    }

    /**
     * Finds a group by its name.
     *
     * @param name The name of the group to find.
     *
     * @return The group identified by the provided name. If there multiple
     *         groups with the provided name only the first one is returned. If
     *         there is no group identified by the provided name {@code null} is
     *         returned.
     */
    public Group findByName(final String name) {
        final TypedQuery<Group> query = getEntityManager().createNamedQuery(
            "Group.findByName", Group.class);
        query.setParameter("name", name);
        final List<Group> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    /**
     * Tries to find a group which name contains a provided token.
     *
     * @param name The name or part of the name of the group to find.
     *
     * @return A list of a matching groups.
     */
    public List<Group> searchGroupByName(final String name) {
        final TypedQuery<Group> query = getEntityManager().createNamedQuery(
            "Group.searchByName", Group.class);
        query.setParameter("name", name);
        return query.getResultList();
    }

    public List<Group> findAllOrderedByGroupName() {
        final TypedQuery<Group> query = getEntityManager().createNamedQuery(
            "Group.findAllOrderedByGroupName", Group.class);
        return query.getResultList();
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.ADMIN_PRIVILEGE)
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void save(final Group group) {
        super.save(group);
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.ADMIN_PRIVILEGE)
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void delete(final Group entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Can't delete null");
        }

        final Group delete = getEntityManager().find(Group.class,
                                                     entity.getPartyId());

        delete.getMemberships().forEach(m -> {
            getEntityManager().remove(m);
        });

        getEntityManager().remove(delete);
    }

}
