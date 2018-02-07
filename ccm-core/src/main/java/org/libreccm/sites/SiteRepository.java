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
package org.libreccm.sites;

import org.libreccm.core.AbstractEntityRepository;
import org.libreccm.core.CoreConstants;
import org.libreccm.security.RequiresPrivilege;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 * Repository for {@link Site} entities.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class SiteRepository extends AbstractEntityRepository<Long, Site> {

    private static final long serialVersionUID = 3120528987720524155L;

    /**
     * Retrieve the {@link Site} for a specific domain.
     *
     * @param domain The domain of site to retrieve.
     *
     * @return If there is a {@link Site} for the provided domain an
     *         {@link Optional} containing the {@link Site} is returned. If
     *         there is not matching {@link Site} an empty {@link Optional} is
     *         returned.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<Site> findByDomain(final String domain) {

        final TypedQuery<Site> query = getEntityManager()
            .createNamedQuery("Site.findByDomain", Site.class);
        query.setParameter("domain", domain);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    /**
     * Finds the default site.
     *
     * @return An {@link Optional} containing the default site. If there is no
     *         default site an empty {@link Optional} is returned. If there
     *         multiple {@link Site}s marked as default site (which should not
     *         happen) the first result is returned (the list is ordered by the
     *         domain).
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<Site> findDefaultSite() {
        final TypedQuery<Site> query = getEntityManager()
            .createNamedQuery("Site.findDefaultSite", Site.class);

        final List<Site> result = query.getResultList();
        if (result.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(result.get(0));
        }
    }

    /**
     * Checks if there is a {@link Site} for a domain.
     *
     * @param domain The domain to check for.
     *
     * @return {@code true} if there is a {@link Site} for that domain,
     *         {@code false} otherwise.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public boolean hasSiteForDomain(final String domain) {

        final TypedQuery<Boolean> query = getEntityManager()
            .createNamedQuery("Site.hasSiteForDomain", Boolean.class);
        query.setParameter("domain", domain);

        return query.getSingleResult();
    }

    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void save(final Site site) {
        super.save(site);
    }

    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Override
    public void delete(final Site site) {
        super.delete(site);
    }

    @Override
    public Class<Site> getEntityClass() {
        return Site.class;
    }

    @Override
    public String getIdAttributeName() {
        return "objectId";
    }
    
    @Override
    public Long getIdOfEntity(final Site entity) {
        return entity.getObjectId();
    }

    @Override
    public boolean isNew(final Site site) {
        return site.getObjectId() == 0;
    }

    @Override
    public void initNewEntity(final Site site) {
        site.setUuid(UUID.randomUUID().toString());
    }

}
