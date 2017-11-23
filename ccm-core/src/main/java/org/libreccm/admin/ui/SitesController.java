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
import org.libreccm.sites.Site;
import org.libreccm.sites.SiteRepository;
import org.libreccm.theming.Themes;

import java.io.Serializable;

import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
class SitesController implements Serializable {

    private static final long serialVersionUID = 112502641827852807L;

    @Inject
    private SiteRepository siteRepository;

    @Inject
    private SitesTableDataProvider sitesTableDataProvider;

    @Inject
    private Themes themes;

    protected SitesTableDataProvider getSitesTableDataProvider() {
        return sitesTableDataProvider;
    }

    protected Themes getThemes() {
        return themes;
    }

    protected SiteRepository getSiteRepository() {
        return siteRepository;
    }

    /**
     * Check if there no site with the provided domain.
     *
     * @param domainOfSite
     *
     * @return {@code true} if there is no site with the provided domain,
     *         {@code false} otherwise.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected boolean isUnique(final String domainOfSite) {

        return !siteRepository.findByDomain(domainOfSite).isPresent();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void delete(final long siteId) {
        final Site site = siteRepository
            .findById(siteId)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No site with ID %d in the database.",
                    siteId)));
        siteRepository.delete(site);
        sitesTableDataProvider.refreshAll();
    }

}
