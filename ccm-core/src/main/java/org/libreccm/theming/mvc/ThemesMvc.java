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
package org.libreccm.theming.mvc;

import org.libreccm.sites.Site;
import org.libreccm.sites.SiteRepository;
import org.libreccm.theming.ThemeInfo;
import org.libreccm.theming.ThemeVersion;
import org.libreccm.theming.Themes;
import org.libreccm.theming.manifest.ThemeManifest;
import org.libreccm.theming.manifest.ThemeTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ThemesMvc {

    @Inject
    private SiteRepository siteRepo;

    @Inject
    private Themes themes;

    public String getMvcTemplate(
        final UriInfo uriInfo,
        final String application
    ) {
        final Site site = getSite(uriInfo);
        final String theme = parseThemeParam(uriInfo);
        final ThemeVersion themeVersion = parsePreviewParam(uriInfo);
        final ThemeInfo themeInfo = getTheme(
            site,
            theme,
            themeVersion
        );
        final ThemeManifest manifest = themeInfo.getManifest();
        final Map<String, ThemeTemplate> applicationTemplates = manifest
            .getMvcTemplatesOfCategory("applications")
            .orElseThrow(
                () -> new WebApplicationException(
                    String.format(
                        "Manifest of theme %s has no application templates.",
                        themeInfo.getName()
                    ),
                    Response.Status.INTERNAL_SERVER_ERROR
                )
            );
        final ThemeTemplate themeTemplate;
        if (applicationTemplates.containsKey(application)) {
            themeTemplate =  applicationTemplates.get(application);
        } else {
            themeTemplate = Optional.ofNullable(
                applicationTemplates.get("@default")
            ).orElseThrow(
                () -> new WebApplicationException(
                    String.format(
                        "Theme %s does not provide a template for application "
                            + "%s and has not default template for "
                            + "applications.",
                        theme,
                        application
                    )
                )
            );
        }

        return String.format(
            "@themes/%s/%s/%s",
            theme,
            Objects.toString(themeVersion),
            themeTemplate.getPath()
        );
    }

    private Site getSite(final UriInfo uriInfo) {
        Objects.requireNonNull(uriInfo);

        final String domain = uriInfo.getBaseUri().getHost();

        final Site site;
        if (siteRepo.hasSiteForDomain(domain)) {
            site = siteRepo.findByDomain(domain).get();
        } else {
            site = siteRepo
                .findDefaultSite()
                .orElseThrow(() -> new NotFoundException(
                "No matching Site and no default Site."));
        }

        return site;
    }

    private ThemeInfo getTheme(
        final Site site,
        final String theme,
        final ThemeVersion themeVersion) {
        if ("--DEFAULT--".equals(theme)) {
            return themes
                .getTheme(site.getDefaultTheme(), themeVersion)
                .orElseThrow(
                    () -> new WebApplicationException(
                        String.format(
                            "The configured default theme \"%s\" for "
                                + "site \"%s\" is not available.",
                            site.getDefaultTheme(),
                            site.getDomainOfSite()
                        ),
                        Response.Status.INTERNAL_SERVER_ERROR
                    )
                );
        } else {
            return themes
                .getTheme(theme, themeVersion)
                .orElseThrow(
                    () -> new WebApplicationException(
                        String.format(
                            "The theme \"%s\" is not available.",
                            theme
                        ),
                        Response.Status.BAD_REQUEST
                    )
                );
        }
    }

    private String parseThemeParam(final UriInfo uriInfo) {
        if (uriInfo.getQueryParameters().containsKey("theme")) {
            return uriInfo.getQueryParameters().getFirst("theme");
        } else {
            return "--DEFAULT--";
        }
    }

    private ThemeVersion parsePreviewParam(final UriInfo uriInfo) {
        if (uriInfo.getQueryParameters().containsKey("preview")) {
            final List<String> values = uriInfo
                .getQueryParameters()
                .get("preview");
            if (values.contains("theme") || values.contains("all")) {
                return ThemeVersion.DRAFT;
            } else {
                return ThemeVersion.LIVE;
            }
        } else {
            return ThemeVersion.LIVE;
        }
    }

}
