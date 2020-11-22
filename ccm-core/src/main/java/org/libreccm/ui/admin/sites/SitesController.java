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
import org.libreccm.theming.Themes;
import org.libreccm.ui.Message;
import org.libreccm.ui.MessageType;
import org.libreccm.ui.admin.AdminMessages;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/sites")
public class SitesController {

    @Inject
    private AdminMessages adminMessages;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private Models models;

    @Inject
    private SiteDetailsModel siteDetailsModel;

    @Inject
    private SiteRepository siteRepository;

    @Inject
    private Themes themes;

    @GET
    @Path("/")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String getSites() {
        final List<Site> sites = siteRepository.findAll();
        models.put(
            "sites",
            sites
                .stream()
                .map(this::buildSiteTableRow)
                .collect(Collectors.toList())
        );

        return "org/libreccm/ui/admin/sites/sites.xhtml";
    }

    @GET
    @Path("/{siteIdentifier}/details")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String getSite(
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
            siteDetailsModel.setSite(result.get());
            siteDetailsModel.setAvailableThemes(themes.getAvailableThemes());

            return "org/libreccm/ui/admin/sites/site-details.xhtml";
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

    @GET
    @Path("/new")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String createNewSite() {
        siteDetailsModel.setAvailableThemes(themes.getAvailableThemes());
        return "org/libreccm/ui/admin/sites/site-form.xhtml";
    }

    @GET
    @Path("/{siteIdentifier}/edit")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String editSite(
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
            siteDetailsModel.setSite(result.get());
            siteDetailsModel.setAvailableThemes(themes.getAvailableThemes());

            return "org/libreccm/ui/admin/sites/site-form.xhtml";
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
    
    @POST
    @Path("/{identifier}/delete")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String deleteSite(
        @PathParam("identifier") final String siteIdentifierParam,
        @FormParam("confirmed") final String confirmed
    ) {
        if ("true".equals(confirmed)) {
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
            siteRepository.delete(result.get());
        }
        }
        
        return "redirect:sites";
    }

    private SiteTableRow buildSiteTableRow(final Site site) {
        final SiteTableRow row = new SiteTableRow();
        row.setSiteId(site.getObjectId());
        row.setUuid(site.getUuid());
        row.setDomain(site.getDomainOfSite());
        row.setDefaultSite(site.isDefaultSite());
        row.setDefaultTheme(site.getDefaultTheme());

        return row;
    }

}
