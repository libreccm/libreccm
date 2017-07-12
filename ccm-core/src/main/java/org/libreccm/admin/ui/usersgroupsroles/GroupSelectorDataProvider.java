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

import java.util.List;
import java.util.stream.Stream;

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
public class GroupSelectorDataProvider extends AbstractDataProvider<Group, String> {

    private static final long serialVersionUID = 2237927716392108777L;

    @Inject
    private EntityManager entityManager;

    private String groupNameFilter;

    private List<Group> excludedGroups;

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public int size(final Query<Group, String> query) {

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = builder
            .createQuery(Long.class);
        final Root<Group> from = criteriaQuery.from(Group.class);
        criteriaQuery.select(builder.count(from));
        criteriaQuery.distinct(true);

        if (groupNameFilter != null && !groupNameFilter.trim().isEmpty()) {
            criteriaQuery
                .where(builder.like(builder.lower(from.get("name")),
                                    String.format("%s%%", groupNameFilter)));
        }

        if (excludedGroups != null && !excludedGroups.isEmpty()) {
            criteriaQuery.where(builder.not(from.in(excludedGroups)));
        }

        return entityManager
            .createQuery(criteriaQuery)
            .getSingleResult()
            .intValue();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public Stream<Group> fetch(final Query<Group, String> query) {

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Group> criteriaQuery = builder
            .createQuery(Group.class);
        final Root<Group> from = criteriaQuery.from(Group.class);
        criteriaQuery.distinct(true);

        if (groupNameFilter != null && !groupNameFilter.trim().isEmpty()) {
            criteriaQuery
                .where(builder.like(builder.lower(from.get("name")),
                                    String.format("%s%%", groupNameFilter)));
        }

        if (excludedGroups != null && !excludedGroups.isEmpty()) {
            criteriaQuery.where(builder.not(from.in(excludedGroups)));
        }

        criteriaQuery.orderBy(builder.asc(from.get("name")));
        
        return entityManager
            .createQuery(criteriaQuery)
            .setMaxResults(query.getLimit())
            .setFirstResult(query.getOffset())
            .getResultList()
            .stream();
    }

    public void setGroupNameFilter(final String groupNameFilter) {
        this.groupNameFilter = groupNameFilter;
    }

    public void setExcludedGroups(final List<Group> excludedGroups) {
        this.excludedGroups = excludedGroups;
    }

}
