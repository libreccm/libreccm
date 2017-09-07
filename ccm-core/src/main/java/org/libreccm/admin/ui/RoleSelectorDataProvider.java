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
package org.libreccm.admin.ui;

import com.vaadin.cdi.ViewScoped;
import com.vaadin.data.provider.AbstractDataProvider;
import com.vaadin.data.provider.Query;
import org.libreccm.security.Role;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
public class RoleSelectorDataProvider extends AbstractDataProvider<Role, String> {

    private static final long serialVersionUID = 6142912046579055420L;

    @Inject
    private EntityManager entityManager;

    private String roleNameFilter;

    private List<Role> excludedRoles;

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public int size(final Query<Role, String> query) {

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = builder
            .createQuery(Long.class);
        final Root<Role> from = criteriaQuery.from(Role.class);
        criteriaQuery.select(builder.count(from));
        criteriaQuery.distinct(true);

        if (roleNameFilter != null && !roleNameFilter.trim().isEmpty()) {
            criteriaQuery
                .where(builder.like(builder.lower(from.get("name")),
                                    String.format("%s%%", roleNameFilter)));
        }

        if (excludedRoles != null && !excludedRoles.isEmpty()) {
            criteriaQuery.where(builder.not(from.in(excludedRoles)));
        }

        return entityManager
            .createQuery(criteriaQuery)
            .getSingleResult()
            .intValue();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public Stream<Role> fetch(final Query<Role, String> query) {

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Role> criteriaQuery = builder
            .createQuery(Role.class);
        final Root<Role> from = criteriaQuery.from(Role.class);
        criteriaQuery.distinct(true);

        if (roleNameFilter != null && !roleNameFilter.trim().isEmpty()) {
            criteriaQuery
                .where(builder.like(builder.lower(from.get("name")),
                                    String.format("%s%%", roleNameFilter)));
        }

        if (excludedRoles != null && !excludedRoles.isEmpty()) {
            criteriaQuery.where(builder.not(from.in(excludedRoles)));
        }

        criteriaQuery.orderBy(builder.asc(from.get("name")));

        return entityManager
            .createQuery(criteriaQuery)
            .setMaxResults(query.getLimit())
            .setFirstResult(query.getOffset())
            .getResultList()
            .stream();
    }

    public void setRoleNameFilter(final String roleNameFilter) {
        this.roleNameFilter = roleNameFilter;
        refreshAll();
    }

    public void setExcludedRoles(final List<Role> excludedRoles) {
        this.excludedRoles = new ArrayList<>(excludedRoles);
        refreshAll();
    }

}
