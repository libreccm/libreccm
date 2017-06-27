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
package org.libreccm.admin.ui.usersgroupsroles;

import com.vaadin.cdi.ViewScoped;
import com.vaadin.data.provider.AbstractDataProvider;
import com.vaadin.data.provider.Query;
import org.libreccm.security.Group;
import org.libreccm.security.Role;
import org.libreccm.security.RoleMembership;

import java.util.Objects;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
class GroupRolesTableDataProvider
    extends AbstractDataProvider<Role, String> {

    private static final long serialVersionUID = 7981493169013788121L;

    @Inject
    private EntityManager entityManager;

    private Group group;

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public int size(Query<Role, String> query) {

        Objects.requireNonNull(group,
                               "This data provider needs to be initalized "
                                   + "by calling setGroup(Group) before calling "
                               + "the count method.");

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = builder
            .createQuery(Long.class);

        final Root<RoleMembership> from = criteriaQuery
            .from(RoleMembership.class);

        criteriaQuery
            .select(builder.count(from))
            .where(builder.equal(from.get("member"), group));

        return entityManager
            .createQuery(criteriaQuery)
            .getSingleResult()
            .intValue();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public Stream<Role> fetch(Query<Role, String> query) {

        Objects.requireNonNull(group,
                               "This data provider needs to be initalized "
                                   + "by calling setGroup(Group) before calling "
                               + "the fetch method.");

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        final CriteriaQuery<RoleMembership> criteriaQuery = builder
            .createQuery(RoleMembership.class);
        final Root<RoleMembership> from = criteriaQuery
            .from(RoleMembership.class);
        final Join<?, ?> join = from.join("role");
        criteriaQuery
            .where(builder.equal(from.get("member"), group))
            .orderBy(builder.asc(join.get("name")));

        return entityManager
            .createQuery(criteriaQuery)
            .setMaxResults(query.getLimit())
            .setFirstResult(query.getOffset())
            .getResultList()
            .stream()
            .map(RoleMembership::getRole);
    }

    public void setGroup(final Group group) {
        Objects.requireNonNull(group);
        this.group = group;
    }

}
