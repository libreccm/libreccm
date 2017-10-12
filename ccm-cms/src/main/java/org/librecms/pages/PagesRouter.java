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

import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelManager;
import org.libreccm.sites.Site;
import org.libreccm.sites.SiteRepository;
import org.libreccm.theming.ThemeInfo;
import org.libreccm.theming.ThemeVersion;
import org.libreccm.theming.Themes;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/{page:.+}")
public class PagesRouter {

    @Inject
    private CategoryManager categoryManager;

    @Inject
    private CategoryRepository categoryRepo;

    @Inject
    private CmsPageBuilder pageBuilder;

    @Inject
    private EntityManager entityManager;

    @Inject
    private PagesRepository pagesRepo;

    @Inject
    private PageModelManager pageModelManager;

    @Inject
    private PageRepository pageRepo;

    @Inject
    private PageManager pageManager;

    @Inject
    private SiteRepository siteRepo;

    @Inject
    private Themes themes;

    @Path("/index.{lang}.html")
    @Produces("text/html")
    @Transactional(Transactional.TxType.REQUIRED)
    public String getCategoryIndexPageAsHtml(
        @Context
        final UriInfo uriInfo,
        @PathParam("page")
        final String page,
        @PathParam("lang")
        final String language,
        @QueryParam("theme")
        @DefaultValue("--DEFAULT--")
        final String theme,
        @QueryParam("theme-version")
        @DefaultValue("LIVE")
        final String themeVersion,
        @QueryParam("pagemodel-version")
        @DefaultValue("LIVE")
        final String pageModelVersion) {

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

        final Pages pages = pagesRepo
            .findPagesForSite(domain)
            .orElseThrow(() -> new NotFoundException(String
            .format("No Pages for domain \"%s\" available.",
                    domain)));

        final Category category = categoryRepo
            .findByPath(pages.getCategoryDomain(), page)
            .orElseThrow(() -> new NotFoundException(String.format(
            "No page for path \"%s\" in site \"%s\"",
            page,
            domain)));

        final Page pageConf = pageManager.findPageForCategory(category);

        final PageModel pageModel = pageConf.getIndexPageModel();

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("currentCategory", category);

        final Map<String, Object> buildResult;
        if (pageModel == null) {
            buildResult = pageBuilder.buildPage(parameters);
        } else {
            buildResult = pageBuilder.buildPage(pageModel, parameters);
        }

        final ThemeInfo themeInfo;
        if ("--DEFAULT--".equals(theme)) {
            themeInfo = themes
                .getTheme(site.getDefaultTheme(),
                          ThemeVersion.valueOf(themeVersion))
                .orElseThrow(() -> new WebApplicationException(
                String.format("The configured default theme \"%s\" for "
                                  + "site \"%s\" is not available.",
                              site.getDomainOfSite(),
                              site.getDefaultTheme()),
                Response.Status.INTERNAL_SERVER_ERROR));
        } else {
            themeInfo = themes.getTheme(theme,
                                        ThemeVersion.valueOf(themeVersion))
                .orElseThrow(() -> new WebApplicationException(
                String.format("The theme \"%s\" is not available.",
                              theme),
                Response.Status.BAD_REQUEST));
        }

        return themes.process(buildResult, themeInfo);
    }

    @Path("/index.{lang}.json")
    @Produces("text/html")
    @Transactional(Transactional.TxType.REQUIRED)
    public String getCategoryIndexPageAsJson(
        @Context
        final UriInfo uriInfo,
        @PathParam("page")
        final String page,
        @PathParam("lang")
        final String language,
        @QueryParam("theme")
        @DefaultValue("--DEFAULT--")
        final String theme,
        @QueryParam("theme-version")
        @DefaultValue("LIVE")
        final String themeVersion,
        @QueryParam("pagemodel-version")
        @DefaultValue("LIVE")
        final String pageModelVersion) {

        throw new UnsupportedOperationException();
    }
    
    @Path("/index.{lang}.xml")
    @Produces("text/html")
    @Transactional(Transactional.TxType.REQUIRED)
    public String getCategoryIndexPageAsXml(
        @Context
        final UriInfo uriInfo,
        @PathParam("page")
        final String page,
        @PathParam("lang")
        final String language,
        @QueryParam("theme")
        @DefaultValue("--DEFAULT--")
        final String theme,
        @QueryParam("theme-version")
        @DefaultValue("LIVE")
        final String themeVersion,
        @QueryParam("pagemodel-version")
        @DefaultValue("LIVE")
        final String pageModelVersion) {

        throw new UnsupportedOperationException();
    }

    @Path("/{name}.{lang}.html")
    public String getPage(
        @Context final UriInfo uriInfo,
        @PathParam("page") final String page,
        @PathParam("lang") final String language,
        @QueryParam("theme") @DefaultValue("--DEFAULT--") final String theme) {

        throw new UnsupportedOperationException();
    }

}
