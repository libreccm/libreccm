/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.libreccm.pagemodel;

import org.libreccm.core.AbstractEntityRepository;
import org.libreccm.web.CcmApplication;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.persistence.TypedQuery;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class PageModelRepository extends AbstractEntityRepository<Long, PageModel> {

    @Override
    public Class<PageModel> getEntityClass() {
        return PageModel.class;
    }

    @Override
    public boolean isNew(final PageModel pageModel) {
        if (pageModel == null) {
            throw new IllegalArgumentException("PageModel can't be null.");
        }

        return pageModel.getPageModelId() == 0;
    }

    @Override
    public void initNewEntity(final PageModel pageModel) {
        if (pageModel == null) {
            throw new IllegalArgumentException("PageModel can't be null.");
        }

        pageModel.setUuid(UUID.randomUUID().toString());
    }

    public List<PageModel> findByApplication(final CcmApplication application) {
        if (application == null) {
            throw new IllegalArgumentException(
                "Can't find page models for application null");
        }

        final TypedQuery<PageModel> query = getEntityManager().createNamedQuery(
            "PageModel.findByApplication", PageModel.class);
        query.setParameter("application", application);

        return query.getResultList();
    }

    public long countByApplication(final CcmApplication application) {
        if (application == null) {
            throw new IllegalArgumentException(
                "Can't count page models for application null");
        }

        final TypedQuery<Long> query = getEntityManager().createNamedQuery(
            "PageModel.countByApplication", Long.class);
        query.setParameter("application", application);

        return query.getSingleResult();
    }

    public Optional<PageModel> findByApplicationAndName(
        final CcmApplication application,
        final String name) {

        if (application == null) {
            throw new IllegalArgumentException(
                "Can't find page models for application null");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "The name of a page model can't be null or empty.");
        }

        final long count = countByApplicationAndName(application, name);
        if (count == 0) {
            return Optional.empty();
        }

        final TypedQuery<PageModel> query = getEntityManager().createNamedQuery(
            "PageModel.findByApplicationAndName", PageModel.class);
        query.setParameter("application", application);
        query.setParameter("name", name);

        return Optional.of(query.getSingleResult());
    }

    public long countByApplicationAndName(final CcmApplication application,
                                          final String name) {
        
        if (application == null) {
            throw new IllegalArgumentException(
                "Can't count page models for application null");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "The name of a page model can't be null or empty.");
        }

        final TypedQuery<Long> query = getEntityManager().createNamedQuery(
            "PageModel.countByApplicationAndName", Long.class);
        query.setParameter("application", application);
        query.setParameter("name", name);

        return query.getSingleResult();
    }

}
