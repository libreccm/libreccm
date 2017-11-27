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

import static org.libreccm.core.CoreConstants.*;

import org.libreccm.security.RequiresPrivilege;
import org.libreccm.web.ApplicationRepository;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Manages {@link Site} entities.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class SiteManager implements Serializable {

    private static final long serialVersionUID = 1834820718630385805L;

    @Inject
    private ApplicationRepository applicationRepo;

    @Inject
    private SiteRepository siteRepo;

    /**
     * Adds an instance of {@link SiteAwareApplication} to a {@link Site}.
     *
     * @param application The application to associate with the site.
     * @param site        The site to which the application instance is added.
     */
    @RequiresPrivilege(PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void addApplicationToSite(final SiteAwareApplication application,
                                     final Site site) {

        site.addApplication(application);
        application.setSite(site);

        siteRepo.save(site);
        applicationRepo.save(application);
    }

    /**
     * Removes an application from a site. The application instance is
     * <em>not</em> deleted!
     *
     * @param application The application to remove from the site.
     * @param site        The site from which the application is removed.
     */
    @RequiresPrivilege(PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void removeApplicationFromSite(final SiteAwareApplication application,
                                          final Site site) {

        site.removeApplication(application);
        application.setSite(null);

        siteRepo.save(site);
        applicationRepo.save(application);
    }

}
