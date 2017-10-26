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

    @RequiresPrivilege(PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void addApplicationToSite(final Site site, 
                                     final SiteAwareApplication application) {
        
        site.addApplication(application);
        application.setSite(site);
        
        siteRepo.save(site);
        applicationRepo.save(application);
    }
    
    @RequiresPrivilege(PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void removeApplicationFromSite(final Site site,
                                     final SiteAwareApplication application) {
        
        site.removeApplication(application);
        application.setSite(null);
        
        siteRepo.save(site);
        applicationRepo.save(application);
    }

}
