/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.ui.admin.sites;

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.core.CoreConstants;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.sites.Site;
import org.libreccm.sites.SiteRepository;
import org.libreccm.ui.Message;
import org.libreccm.ui.MessageType;
import org.libreccm.ui.admin.AdminMessages;

import java.util.Arrays;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Controller for processing the {@code POST} requests from the form for
 * creating and editing sites.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/sites")
public class SiteFormController {

    @Inject
    private AdminMessages adminMessages;

    @Inject
    private SiteDetailsModel siteDetailsModel;

    @Inject
    private SiteRepository siteRepository;

    @Inject
    private IdentifierParser identifierParser;

    @FormParam("domain")
    private String domainOfSite;

    @FormParam("defaultSite")
    private String defaultSite;

    @FormParam("defaultTheme")
    private String defaultTheme;

    /**
     * Create a new site.
     * 
     * @return Redirect to the sites overview.
     */
    @POST
    @Path("/new")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String createSite() {
        final Site site = new Site();
        site.setDomainOfSite(domainOfSite);
        if (defaultSite != null) {
            resetDefaultSite();
            site.setDefaultSite(true);
        }
        site.setDefaultTheme(defaultTheme);

        siteRepository.save(site);

        return "redirect:sites";
    }

    /**
     * Update a site with the data from the form.
     * 
     * @param siteIdentifierParam The identifier of the site to update.
     * @return Redirect to the details page of the site.
     */
    @POST
    @Path("/{siteIdentifier}/edit")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateSite(
        @PathParam("siteIdentifier") final String siteIdentifierParam
    ) {
        final Identifier siteIdentifier = identifierParser.parseIdentifier(
            siteIdentifierParam
        );

        final Optional<Site> result;
        switch (siteIdentifier.getType()) {
            case ID:
                result = siteRepository.findById(
                    Long.parseLong(siteIdentifier.getIdentifier())
                );
                break;
            default:
                result = siteRepository.findByUuid(
                    siteIdentifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            final Site site = result.get();

            site.setDomainOfSite(domainOfSite);
            site.setDefaultTheme(defaultTheme);

            final boolean isDefaultSite = defaultSite != null;
            if (isDefaultSite != site.isDefaultSite()) {
                resetDefaultSite();
                site.setDefaultSite(isDefaultSite);
            }
            siteRepository.save(site);

            return String.format(
                "redirect:sites/ID-%d/details", site.getObjectId()
            );
        } else {
            siteDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "sites.not_found_message",
                        Arrays.asList(siteIdentifierParam)
                    ),
                    MessageType.WARNING
                )
            );

            return "org/libreccm/ui/admin/sites/site-not-found.xhtml";
        }
    }

    /**
     * Helper method for resetting the default site of an installation.
     */
    private void resetDefaultSite() {
        final Optional<Site> result = siteRepository
            .findDefaultSite();
        if (result.isPresent()) {
            final Site site = result.get();
            site.setDefaultSite(false);
            siteRepository.save(site);
        }
    }

}
