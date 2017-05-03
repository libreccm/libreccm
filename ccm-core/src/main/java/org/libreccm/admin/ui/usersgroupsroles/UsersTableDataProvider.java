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
import org.libreccm.security.User;

import java.util.stream.Stream;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
public class UsersTableDataProvider extends AbstractDataProvider<User, String> {

    private static final long serialVersionUID = 8849235775786370772L;

    @Inject
    private EntityManager entityManager;

    private String userNameFilter;

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public int size(final Query<User, String> query) {
        final CriteriaBuilder builder = entityManager
            .getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder
            .createQuery(Long.class);
        final Root<User> from = criteriaQuery.from(User.class);

        criteriaQuery = criteriaQuery.select(builder.count(from));

        if (userNameFilter != null && !userNameFilter.trim().isEmpty()) {
            criteriaQuery
                .where(builder.like(builder.lower(from.get("name")),
                                     String.format("%s%%", userNameFilter)));
        }
        
        return entityManager
            .createQuery(criteriaQuery)
            .getSingleResult()
            .intValue();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public Stream<User> fetch(final Query<User, String> query) {
        final CriteriaBuilder builder = entityManager
            .getCriteriaBuilder();
        final CriteriaQuery<User> criteriaQuery = builder
            .createQuery(User.class);
        final Root<User> from = criteriaQuery.from(User.class);

        if (userNameFilter != null && !userNameFilter.trim().isEmpty()) {
            criteriaQuery
                .where(builder.like(builder.lower(from.get("name")),
                                     String.format("%s%%", userNameFilter)));
        }

        return entityManager
            .createQuery(criteriaQuery)
            .setMaxResults(query.getLimit())
            .setFirstResult(query.getOffset())
            .getResultList()
            .stream();
    }

    public void setUserNameFilter(final String userNameFilter) {
        this.userNameFilter = userNameFilter;
        refreshAll();
    }

}
