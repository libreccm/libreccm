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
import org.libreccm.core.CcmObjectRepository;
import org.libreccm.core.CoreConstants;
import org.libreccm.security.RequiresPrivilege;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class SiteRepository extends AbstractEntityRepository<Long, Site> {

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

    @Transactional(Transactional.TxType.REQUIRED)
    public boolean hasSiteForDomain(final String domain) {

        final TypedQuery<Boolean> query = getEntityManager()
            .createNamedQuery("Site.hasSiteForDomain", Boolean.class);
        query.setParameter("domain", domain);

        return query.getSingleResult();
    }

    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
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
    public boolean isNew(final Site site) {
        return site.getObjectId() == 0;
    }
    
}
