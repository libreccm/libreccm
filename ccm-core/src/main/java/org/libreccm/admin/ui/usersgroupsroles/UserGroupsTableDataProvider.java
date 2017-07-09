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
import org.libreccm.security.GroupMembership;
import org.libreccm.security.User;

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
class UserGroupsTableDataProvider extends AbstractDataProvider<Group, String> {

    private static final long serialVersionUID = 3321330114174366998L;

    @Inject
    private EntityManager entityManager;

    private User user;

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public int size(final Query<Group, String> query) {

        Objects.requireNonNull(user,
                               "This data provider needs to be initalized "
                                   + "by calling setUser(User) before calling "
                                   + "the count method.");

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = builder
            .createQuery(Long.class);

        final Root<GroupMembership> from = criteriaQuery
            .from(GroupMembership.class);

        criteriaQuery
            .select(builder.count(from))
            .where(builder.equal(from.get("member"), user));

        return entityManager
            .createQuery(criteriaQuery)
            .getSingleResult()
            .intValue();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public Stream<Group> fetch(final Query<Group, String> query) {

        Objects.requireNonNull(user,
                               "This data provider needs to be initalized "
                                   + "by calling setUser(User) before calling "
                                   + "the fetch method.");

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        final CriteriaQuery<GroupMembership> criteriaQuery = builder
            .createQuery(GroupMembership.class);
        final Root<GroupMembership> from = criteriaQuery
            .from(GroupMembership.class);
        final Join<?, ?> join = from.join("group");
        criteriaQuery
            .where(builder.equal(from.get("member"), user))
            .orderBy(builder.asc(join.get("name")));

        return entityManager
            .createQuery(criteriaQuery)
            .setMaxResults(query.getLimit())
            .setFirstResult(query.getOffset())
            .getResultList()
            .stream()
            .map(GroupMembership::getGroup);
    }

    public void setUser(final User user) {
        this.user = user;
    }

}
