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
 * Primary controller for the UI for managing sites.
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

    /**
     * Show all available sites.
     *
     * @return The template to use.
     */
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

    /**
     * Show the details of a site.
     *
     * @param siteIdentifierParam Identifier of the site to show.
     *
     * @return The template to use.
     */
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

    /**
     * Show the form for creating a new site.
     *
     * @return The template to use.
     */
    @GET
    @Path("/new")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String createNewSite() {
        siteDetailsModel.setAvailableThemes(themes.getAvailableThemes());
        return "org/libreccm/ui/admin/sites/site-form.xhtml";
    }

    /**
     * Show the form for editing a site.
     *
     * @param siteIdentifierParam The identifier of the site to edit.
     *
     * @return The template to use.
     */
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

    /**
     * Delete a site.
     *
     * @param siteIdentifierParam The identifier of the site to delete.
     * @param confirmed           Was the deletion confirmed by the user?
     *
     * @return Redirect to the list of all available sites.
     */
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

    /**
     * Helper method for building a
     * {@link org.libreccm.ui.admin.sites.SiteTableRow} instance for a
     * {@link Site}.
     *
     * @param site The site.
     *
     * @return A {@link SiteTableRow} instance for the site.
     */
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
