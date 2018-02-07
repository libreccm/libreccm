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

import org.libreccm.core.AbstractEntityRepository;
import org.libreccm.core.CoreConstants;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.sites.SiteRepository;

import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 * Repository for {@link Pages}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class PagesRepository extends AbstractEntityRepository<Long, Pages> {

    private static final long serialVersionUID = 7256268720843315037L;

    @Inject
    private SiteRepository siteRepo;

    /**
     * Retrieves the {@link Pages} instance for the site identified by the
     * provided domain.
     *
     * @param domainOfSite The domain of the site.
     *
     * @return The {@link Pages} instance for the site if any.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<Pages> findPagesForSite(final String domainOfSite) {

        if (siteRepo.hasSiteForDomain(domainOfSite)) {
            final TypedQuery<Pages> query = getEntityManager()
                .createNamedQuery("Pages.findForSite", Pages.class);
            query.setParameter("domain", domainOfSite);

            try {
                return Optional.of(query.getSingleResult());
            } catch (NoResultException ex) {
                return Optional.empty();
            }
        } else {
            final TypedQuery<Pages> query = getEntityManager()
                .createNamedQuery("Pages.findForDefaultSite", Pages.class);
            try {
                return Optional.of(query.getSingleResult());
            } catch (NoResultException ex) {
                return Optional.empty();
            }
        }
    }

    @Override
    public Class<Pages> getEntityClass() {
        return Pages.class;
    }

    @Override
    public String getIdAttributeName() {
        return "objectId";
    }

    @Override
    public Long getIdOfEntity(final Pages entity) {
        return entity.getObjectId();
    }

    @Override
    public boolean isNew(final Pages pages) {
        return pages.getObjectId() == 0;
    }

    @Override
    public void initNewEntity(final Pages pages) {

        super.initNewEntity(pages);

        pages.setUuid(UUID.randomUUID().toString());
        pages.setApplicationType(Pages.class.getName());
    }

    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void save(final Pages pages) {
        super.save(pages);
    }

    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void delete(final Pages pages) {
        super.delete(pages);
    }

}
