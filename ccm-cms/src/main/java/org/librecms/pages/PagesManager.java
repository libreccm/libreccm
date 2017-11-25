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

import org.libreccm.categorization.Domain;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.sites.Site;
import org.libreccm.sites.SiteManager;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Manager class for {@link Pages}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class PagesManager implements Serializable {

    private static final long serialVersionUID = 888880071212859827L;

    @Inject
    private PagesRepository pagesRepo;

    @Inject
    private SiteManager siteManager;

    @RequiresPrivilege(PagesPrivileges.ADMINISTER_PAGES)
    @Transactional(Transactional.TxType.REQUIRED)
    public Pages createPages(final String primaryUrl,
                             final Site site,
                             final Domain domain) {

        final Pages pages = new Pages();
        pages.setPrimaryUrl(primaryUrl);
        
        pagesRepo.save(pages);
        
        pages.setCategoryDomain(domain);
        pagesRepo.save(pages);

        siteManager.addApplicationToSite(site, pages);

        return pages;
    }

}
