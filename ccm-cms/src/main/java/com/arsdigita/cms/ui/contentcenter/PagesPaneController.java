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
package com.arsdigita.cms.ui.contentcenter;

import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainRepository;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.sites.Site;
import org.libreccm.sites.SiteManager;
import org.libreccm.sites.SiteRepository;
import org.librecms.pages.Pages;
import org.librecms.pages.PagesManager;
import org.librecms.pages.PagesRepository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import static org.primefaces.component.calendar.Calendar.PropertyKeys.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class PagesPaneController {

    @Inject
    private DomainRepository domainRepo;

    @Inject
    private PagesRepository pagesRepo;

    @Inject
    private PagesManager pagesManager;

    @Inject
    private SiteManager siteManager;

    @Inject
    private SiteRepository siteRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    protected void createPages(final String primaryUrl,
                               final long siteId,
                               final long categoryDomainId) {

        final Site site = siteRepo
            .findById(siteId)
            .orElseThrow(() -> new UnexpectedErrorException(String
            .format("No Site with ID %d in the database.",
                    siteId)));

        final Domain domain = domainRepo
            .findById(categoryDomainId)
            .orElseThrow(() -> new UnexpectedErrorException(String
            .format("No (Category) Domain with ID %d in the database.",
                    categoryDomainId)));

        final Pages pages = pagesManager.createPages(primaryUrl, site, domain);
        pages.setPrimaryUrl(primaryUrl);

        pagesRepo.save(pages);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void updatePages(final long pagesId,
                               final String primaryUrl) {

        final Pages pages = pagesRepo
            .findById(pagesId)
            .orElseThrow(() -> new UnexpectedErrorException(String
            .format("No Pages with ID %d in the database.",
                    pagesId)));

        pages.setPrimaryUrl(primaryUrl);
        
        pagesRepo.save(pages);
    }

}
