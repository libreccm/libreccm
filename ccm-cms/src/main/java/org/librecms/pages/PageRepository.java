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
package org.librecms.pages;

import org.libreccm.categorization.Category;
import org.libreccm.core.AbstractEntityRepository;
import org.libreccm.security.RequiresPrivilege;

import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 * Repository for {@link Page} entities.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class PageRepository extends AbstractEntityRepository<Long, Page> {

    private static final long serialVersionUID = -338101684757468443L;

    /**
     * Find the {@link Page} associated with a {@link Category}.
     *
     * @param category The {@link Category} associated with the {@link Page}.
     *
     * @return
     */
    @RequiresPrivilege(PagesPrivileges.ADMINISTER_PAGES)
    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<Page> findPageForCategory(final Category category) {

        final TypedQuery<Page> query = getEntityManager()
            .createNamedQuery("Page.findForCategory", Page.class);
        query.setParameter("category", category);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    protected void initNewEntity(final Page entity) {

        super.initNewEntity(entity);

        if (isNew(entity)) {
            entity.setUuid(UUID.randomUUID().toString());
        }
    }

    @RequiresPrivilege(PagesPrivileges.ADMINISTER_PAGES)
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void save(final Page page) {
        super.save(page);
    }

    @RequiresPrivilege(PagesPrivileges.ADMINISTER_PAGES)
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void delete(final Page page) {
        super.delete(page);
    }

    @Override
    public Class<Page> getEntityClass() {
        return Page.class;
    }

    @Override
    public boolean isNew(final Page page) {
        return page.getObjectId() == 0;
    }

}
