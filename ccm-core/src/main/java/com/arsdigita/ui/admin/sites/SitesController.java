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
package com.arsdigita.ui.admin.sites;

import org.libreccm.sites.Site;
import org.libreccm.sites.SiteRepository;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class SitesController implements Serializable {

    private static final long serialVersionUID = -7758130361475180380L;

    @Inject
    private SiteRepository sitesRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<SitesTableRow> findSites() {

        return sitesRepo
            .findAll()
            .stream()
            .map(this::buildRow)
            .collect(Collectors.toList());

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

        return !sitesRepo.findByDomain(domainOfSite).isPresent();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void deleteSite(final long siteId) {

        final Site site = sitesRepo
            .findById(siteId)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Site with ID %d in the database.",
                    siteId)));

        sitesRepo.delete(site);
    }

    private SitesTableRow buildRow(final Site site) {

        final SitesTableRow row = new SitesTableRow();

        row.setSiteId(Long.toString(site.getObjectId()));
        row.setDomainOfSite(site.getDomainOfSite());
        row.setDefaultSite(site.isDefaultSite());
        row.setDefaultTheme(site.getDefaultTheme());
        row.setDeletable(site.getApplications().isEmpty());

        final List<String> applications = site
            .getApplications()
            .stream()
            .map(application -> application.getPrimaryUrl())
            .collect(Collectors.toList());

        row.setApplications(applications);

        return row;
    }

}
