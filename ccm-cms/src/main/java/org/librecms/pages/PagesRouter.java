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

import com.arsdigita.kernel.KernelConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelManager;
import org.libreccm.sites.Site;
import org.libreccm.sites.SiteRepository;
import org.libreccm.theming.ThemeInfo;
import org.libreccm.theming.ThemeVersion;
import org.libreccm.theming.Themes;

import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static org.librecms.pages.PagesConstants.*;

/**
 * JAX-RS class providing access to the pages.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
//@Path("/{page:.+}")
@Path("/")
public class PagesRouter {

    @Inject
    private CategoryRepository categoryRepo;

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private CmsPageRenderer pageBuilder;

    @Inject
    private PagesRepository pagesRepo;

    @Inject
    private PageManager pageManager;

    @Inject
    private PageModelManager pageModelManager;

    @Inject
    private SiteRepository siteRepo;

    @Inject
    private Themes themes;

    private Locale defaultLocale;

    @PostConstruct
    private void init() {
        final KernelConfig kernelConfig = confManager
            .findConfiguration(KernelConfig.class);
        defaultLocale = kernelConfig.getDefaultLocale();
    }
    
    @GET
    @Path("/")
    @Transactional(Transactional.TxType.REQUIRED)
    public Response getIndexPage(@Context UriInfo uriInfo) {

        final String domain = uriInfo.getBaseUri().getHost();
        final Pages pages = getPages(domain);
        final Category category = getCategory(domain, pages, "/");

        final Locale negoidatedLocale = globalizationHelper
            .getNegotiatedLocale();

        final String language;
        if (category.getTitle().hasValue(negoidatedLocale)) {
            language = negoidatedLocale.toString();
        } else if (category.getTitle().hasValue(defaultLocale)) {
            language = defaultLocale.toString();
        } else {
            throw new NotFoundException();
        }

        final String indexPage = String.format("/index.%s.html", language);
        final URI uri = uriInfo.getBaseUriBuilder().path(indexPage).build();
        return Response.temporaryRedirect(uri).build();
    }

    @GET
    @Path("/index.html")
    @Transactional(Transactional.TxType.REQUIRED)
    public Response getIndexPageAsHtml(@Context final UriInfo uriInfo) {

        final String domain = uriInfo.getBaseUri().getHost();
        final Pages pages = getPages(domain);
        final Category category = getCategory(domain, pages, "/");

        final Locale negoiatedLocale = globalizationHelper
            .getNegotiatedLocale();
        final String language;
        if (category.getTitle().hasValue(negoiatedLocale)) {
            language = negoiatedLocale.toString();
        } else if (category.getTitle().hasValue(defaultLocale)) {
            language = defaultLocale.toString();
        } else {
            throw new NotFoundException();
        }

        final String indexPage = String.format("/index.%s.html", language);
        final String path = uriInfo.getPath().replace("index.html", indexPage);

        final URI uri = uriInfo.getBaseUriBuilder().replacePath(path).build();
        return Response.temporaryRedirect(uri).build();
    }

    @GET
    @Path("/index.{lang}.html")
    @Produces("text/html")
    @Transactional(Transactional.TxType.REQUIRED)
    public String getIndexPageAsHtml(
        @Context
        final UriInfo uriInfo,
        @PathParam("lang")
        final String language,
        @QueryParam("theme")
        @DefaultValue("--DEFAULT--")
        final String theme,
        @QueryParam("theme-version")
        @DefaultValue("LIVE")
        final String themeVersion,
        @QueryParam("pagemodel-version")
        @DefaultValue("LIVE") final String pageModelVersion) {

        final Map<String, Object> buildResult = getCategoryIndexPage(
            uriInfo, "/", language, pageModelVersion);
        final Site site = getSite(uriInfo);
        final ThemeInfo themeInfo = getTheme(site, theme, themeVersion);

        return themes.process(buildResult, themeInfo);
    }

    @GET
    @Path("/index.{lang}.json")
    @Produces("text/json")
    @Transactional(Transactional.TxType.REQUIRED)
    public String getIndexPageAsJson(
        @Context
        final UriInfo uriInfo,
        @PathParam("lang")
        final String language,
        @QueryParam("pagemodel-version")
        @DefaultValue("LIVE")
        final String pageModelVersion) {

        final ObjectMapper mapper = new ObjectMapper();

        try {
            final Map<String, Object> result = getCategoryIndexPage(uriInfo,
                                                         "/",
                                                         language,
                                                         pageModelVersion);
            final String json = mapper.writeValueAsString(result);
            return json;
//            return mapper
//                .writeValueAsString(getCategoryIndexPage(uriInfo,
//                                                         "/",
//                                                         language,
//                                                         pageModelVersion));
        } catch (JsonProcessingException ex) {
            throw new WebApplicationException(ex);
        }
//        return getCategoryIndexPage(uriInfo, "/", language, pageModelVersion);
    }

    @GET
    @Path("/index.{lang}.xml")
    @Produces("text/xml")
    @Transactional(Transactional.TxType.REQUIRED)
    public String getIndexPageAsXml(
        @Context
        final UriInfo uriInfo,
        @PathParam("lang")
        final String language,
        @QueryParam("pagemodel-version")
        @DefaultValue("LIVE")
        final String pageModelVersion) {

        final JacksonXmlModule xmlModule = new JacksonXmlModule();
        final ObjectMapper mapper = new XmlMapper(xmlModule);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            final Map<String, Object> result = getCategoryIndexPage(uriInfo, 
                                                         "/",
                                                         language,
                                                         pageModelVersion);
            final String html = mapper
                .writer()
                .withRootName("page")
                .writeValueAsString(result);
            return html;
//            return mapper
//                .writeValueAsString(getCategoryIndexPage(uriInfo, 
//                                                         "/",
//                                                         language,
//                                                         pageModelVersion));
        } catch (JsonProcessingException ex) {
            throw new WebApplicationException(ex);
        }
//        return getCategoryIndexPage(uriInfo, "/", language, pageModelVersion);
    }

    @GET
    @Path("/{name}")
    @Transactional(Transactional.TxType.REQUIRED)
    public Response getRootItemPage(
        @Context final UriInfo uriInfo,
        @PathParam("name") final String itemName) {

        final String domain = uriInfo.getBaseUri().getHost();
        final Pages pages = getPages(domain);
        final Category category = getCategory(domain, pages, "/");

        final Locale negoiatedLocale = globalizationHelper
            .getNegotiatedLocale();

        final String language;
        if (category.getTitle().hasValue(negoiatedLocale)) {
            language = negoiatedLocale.toString();
        } else if (category.getTitle().hasValue(defaultLocale)) {
            language = defaultLocale.toString();
        } else {
            throw new NotFoundException();
        }

        final String itemPage = String.format("/%s.%s.html", itemName, language);
        final URI uri = uriInfo.getBaseUriBuilder().path(itemPage).build();
        return Response.temporaryRedirect(uri).build();
    }

    @GET
    @Path("/{name}.html")
    @Transactional(Transactional.TxType.REQUIRED)
    public Response getRootItemPageAsHtml(
        @Context final UriInfo uriInfo,
        @PathParam("name") final String itemName) {

        final String domain = uriInfo.getBaseUri().getHost();
        final Pages pages = getPages(domain);
        final Category category = getCategory(domain, pages, "/");

        final Locale negoiatedLocale = globalizationHelper
            .getNegotiatedLocale();

        final String language;
        if (category.getTitle().hasValue(negoiatedLocale)) {
            language = negoiatedLocale.toString();
        } else if (category.getTitle().hasValue(defaultLocale)) {
            language = defaultLocale.toString();
        } else {
            throw new NotFoundException();
        }

        final String itemPage = String.format("/%s.%s.html", itemName, language);
        final String path = uriInfo
            .getPath()
            .replace(String.format("%s.html", itemName), itemPage);

        final URI uri = uriInfo.getBaseUriBuilder().replacePath(path).build();
        return Response.temporaryRedirect(uri).build();
    }

    @GET
    @Path("/{name}.{lang}.html")
    @Produces("text/html")
    @Transactional(Transactional.TxType.REQUIRED)
    public String getRootItemPageAsHtml(
        @Context
        final UriInfo uriInfo,
        @PathParam("name")
        final String itemName,
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

        final Map<String, Object> buildResult = getCategoryItemPage(
            uriInfo, "/", itemName, language, pageModelVersion);
        final Site site = getSite(uriInfo);
        final ThemeInfo themeInfo = getTheme(site, "/", themeVersion);

        return themes.process(buildResult, themeInfo);
    }

    @GET
    @Path("/{name}.{lang}.json")
    @Produces("text/json")
    @Transactional(Transactional.TxType.REQUIRED)
    public Map<String, Object> getRootItemPageAsJson(
        @Context
        final UriInfo uriInfo,
        @PathParam("name")
        final String itemName,
        @PathParam("lang")
        final String language,
        @QueryParam("pagemodel-version")
        @DefaultValue("LIVE")
        final String pageModelVersion) {

        return getCategoryItemPage(uriInfo,
                                   "/",
                                   itemName,
                                   language,
                                   pageModelVersion);
    }

    @GET
    @Path("/{name}.{lang}.xml")
    @Produces("text/xml")
    @Transactional(Transactional.TxType.REQUIRED)
    public Map<String, Object> getItemPageAsXml(
        @Context
        final UriInfo uriInfo,
        @PathParam("name")
        final String itemName,
        @PathParam("lang")
        final String language,
        @QueryParam("pagemodel-version")
        @DefaultValue("LIVE")
        final String pageModelVersion) {

        return getCategoryItemPage(uriInfo,
                                   "/",
                                   itemName,
                                   language,
                                   pageModelVersion);
    }

    /**
     * Retrieve the index page of a category. Redirects to
     * {@link #getCategoryIndexPageAsHtml(javax.ws.rs.core.UriInfo, java.lang.String)}.
     *
     * @param uriInfo
     * @param page
     *
     * @return
     */
    @GET
    @Path("/{page:.+}/")
    @Transactional(Transactional.TxType.REQUIRED)
    public Response getCategoryIndexPage(
        @Context
        final UriInfo uriInfo,
        @PathParam("page")
        final String page) {

        final String domain = uriInfo.getBaseUri().getHost();
        final Pages pages = getPages(domain);
        final Category category = getCategory(domain, pages, page);

        final Locale negoiatedLocale = globalizationHelper
            .getNegotiatedLocale();

        final String language;
        if (category.getTitle().hasValue(negoiatedLocale)) {
            language = negoiatedLocale.toString();
        } else if (category.getTitle().hasValue(defaultLocale)) {
            language = defaultLocale.toString();
        } else {
            throw new NotFoundException();
        }

        final String indexPage = String.format("/index.%s.html", language);
        final URI uri = uriInfo.getBaseUriBuilder().path(indexPage).build();
        return Response.temporaryRedirect(uri).build();
    }

    /**
     * Retrieves the category index page. Redirects to
     * {@link #getCategoryIndexPageAsHtml(javax.ws.rs.core.UriInfo, java.lang.String)}.
     *
     * @param uriInfo
     * @param page
     *
     * @return
     */
    @GET
    @Path("/{page:.+}/index.html")
    @Transactional(Transactional.TxType.REQUIRED)
    public Response getCategoryIndexPageAsHtml(
        @Context
        final UriInfo uriInfo,
        @PathParam("page")
        final String page) {

        final String domain = uriInfo.getBaseUri().getHost();
        final Pages pages = getPages(domain);
        final Category category = getCategory(domain, pages, page);

        final Locale negoiatedLocale = globalizationHelper
            .getNegotiatedLocale();
        final String language;
        if (category.getTitle().hasValue(negoiatedLocale)) {
            language = negoiatedLocale.toString();
        } else if (category.getTitle().hasValue(defaultLocale)) {
            language = defaultLocale.toString();
        } else {
            throw new NotFoundException();
        }

        final String indexPage = String.format("/index.%s.html", language);
        final String path = uriInfo.getPath().replace("index.html", indexPage);

        final URI uri = uriInfo.getBaseUriBuilder().replacePath(path).build();
        return Response.temporaryRedirect(uri).build();
    }

    /**
     * Retrieves the category index page.
     *
     * @param uriInfo          Data about the URI called.
     * @param page             The path of the category to show.
     * @param language         The selected language.
     * @param theme            The theme to use.
     * @param themeVersion     The version of the theme to use.
     * @param pageModelVersion The version of the page model to use.
     *
     * @return The HTML representation of the index page.
     */
    @GET
    @Path("/{page:.+}/index.{lang}.html")
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

        final Map<String, Object> buildResult = getCategoryIndexPage(
            uriInfo, page, language, pageModelVersion);
        final Site site = getSite(uriInfo);
        final ThemeInfo themeInfo = getTheme(site, theme, themeVersion);

        return themes.process(buildResult, themeInfo);
    }

    /**
     * Retrieves the category index page as JSON:
     *
     * @param uriInfo
     * @param page
     * @param language
     * @param pageModelVersion
     *
     * @return
     */
    @GET
    @Path("/{page:.+}/index.{lang}.json")
    @Produces("text/json")
    @Transactional(Transactional.TxType.REQUIRED)
    public Map<String, Object> getCategoryIndexPageAsJson(
        @Context
        final UriInfo uriInfo,
        @PathParam("page")
        final String page,
        @PathParam("lang")
        final String language,
        @QueryParam("pagemodel-version")
        @DefaultValue("LIVE")
        final String pageModelVersion) {

        return getCategoryIndexPage(uriInfo, page, language, pageModelVersion);
    }

    /**
     * Retrieves the category index page as XML.
     *
     * @param uriInfo
     * @param page
     * @param language
     * @param pageModelVersion
     *
     * @return
     */
    @GET
    @Path("/{page:.+}/index.{lang}.xml")
    @Produces("text/xml")
    @Transactional(Transactional.TxType.REQUIRED)
    public Map<String, Object> getCategoryIndexPageAsXml(
        @Context
        final UriInfo uriInfo,
        @PathParam("page")
        final String page,
        @PathParam("lang")
        final String language,
        @QueryParam("pagemodel-version")
        @DefaultValue("LIVE")
        final String pageModelVersion) {

        return getCategoryIndexPage(uriInfo, page, language, pageModelVersion);
    }

    /**
     * Retrieve the item page for a category and the content item associated
     * with the category and identified by {@code itemName}.
     *
     * Redirects to
     * {@link #getItemPageAsHtml(javax.ws.rs.core.UriInfo, java.lang.String, java.lang.String)}.
     *
     * @param uriInfo
     * @param page
     * @param itemName
     *
     * @return
     */
    @GET
    @Path("/{page:.+}/{name}")
    @Transactional(Transactional.TxType.REQUIRED)
    public Response getItemPage(
        @Context final UriInfo uriInfo,
        @PathParam("page") final String page,
        @PathParam("name") final String itemName) {

        final String domain = uriInfo.getBaseUri().getHost();
        final Pages pages = getPages(domain);
        final Category category = getCategory(domain, pages, page);

        final Locale negoiatedLocale = globalizationHelper
            .getNegotiatedLocale();

        final String language;
        if (category.getTitle().hasValue(negoiatedLocale)) {
            language = negoiatedLocale.toString();
        } else if (category.getTitle().hasValue(defaultLocale)) {
            language = defaultLocale.toString();
        } else {
            throw new NotFoundException();
        }

        final String itemPage = String.format("/%s.%s.html", itemName, language);
        final URI uri = uriInfo.getBaseUriBuilder().path(itemPage).build();
        return Response.temporaryRedirect(uri).build();
    }

    /**
     * Retrieve the item page for a category and the content item associated
     * with the category and identified by {@code itemName}. Redirects to
     * {@link #getItemPageAsHtml(javax.ws.rs.core.UriInfo, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
     *
     * @param uriInfo
     * @param page
     * @param itemName
     *
     * @return
     */
    @GET
    @Path("/{page:.+}/{name}.html")
    @Transactional(Transactional.TxType.REQUIRED)
    public Response getItemPageAsHtml(
        @Context final UriInfo uriInfo,
        @PathParam("page") final String page,
        @PathParam("name") final String itemName) {

        final String domain = uriInfo.getBaseUri().getHost();
        final Pages pages = getPages(domain);
        final Category category = getCategory(domain, pages, page);

        final Locale negoiatedLocale = globalizationHelper
            .getNegotiatedLocale();

        final String language;
        if (category.getTitle().hasValue(negoiatedLocale)) {
            language = negoiatedLocale.toString();
        } else if (category.getTitle().hasValue(defaultLocale)) {
            language = defaultLocale.toString();
        } else {
            throw new NotFoundException();
        }

        final String itemPage = String.format("/%s.%s.html", itemName, language);
        final String path = uriInfo
            .getPath()
            .replace(String.format("%s.html", itemName), itemPage);

        final URI uri = uriInfo.getBaseUriBuilder().replacePath(path).build();
        return Response.temporaryRedirect(uri).build();
    }

    /**
     * Retrieve the item page as HTML for a category and the content item
     * associated with the category and identified by {@code itemName}.
     *
     * @param uriInfo
     * @param page
     * @param itemName
     * @param language
     * @param theme
     * @param themeVersion
     * @param pageModelVersion
     *
     * @return
     */
    @GET
    @Path("/{page:.+}/{name}.{lang}.html")
    @Transactional(Transactional.TxType.REQUIRED)
    public String getItemPageAsHtml(
        @Context
        final UriInfo uriInfo,
        @PathParam("page")
        final String page,
        @PathParam("name")
        final String itemName,
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

        final Map<String, Object> buildResult = getCategoryItemPage(
            uriInfo, page, itemName, language, pageModelVersion);
        final Site site = getSite(uriInfo);
        final ThemeInfo themeInfo = getTheme(site, page, themeVersion);

        return themes.process(buildResult, themeInfo);
    }

    /**
     * Retrieve the item page as JSON for a category and the content item
     * associated with the category and identified by {@code itemName}.
     *
     * @param uriInfo
     * @param page
     * @param itemName
     * @param language
     * @param pageModelVersion
     *
     * @return
     */
    @GET
    @Path("/{page:.+}/{name}.{lang}.json")
    @Produces("text/json")
    @Transactional(Transactional.TxType.REQUIRED)
    public Map<String, Object> getItemPageAsJson(
        @Context
        final UriInfo uriInfo,
        @PathParam("page")
        final String page,
        @PathParam("name")
        final String itemName,
        @PathParam("lang")
        final String language,
        @QueryParam("pagemodel-version")
        @DefaultValue("LIVE")
        final String pageModelVersion) {

        return getCategoryItemPage(uriInfo,
                                   page,
                                   itemName,
                                   language,
                                   pageModelVersion);
    }

    /**
     * Retrieve the item page as XML for a category and the content item
     * associated with the category and identified by {@code itemName}.
     *
     * @param uriInfo
     * @param page
     * @param itemName
     * @param language
     * @param pageModelVersion
     *
     * @return
     */
    @GET
    @Path("/{page:.+}/{name}.{lang}.xml")
    @Produces("text/xml")
    @Transactional(Transactional.TxType.REQUIRED)
    public Map<String, Object> getItemPageAsXml(
        @Context
        final UriInfo uriInfo,
        @PathParam("page")
        final String page,
        @PathParam("name")
        final String itemName,
        @PathParam("lang")
        final String language,
        @QueryParam("pagemodel-version")
        @DefaultValue("LIVE")
        final String pageModelVersion) {

        return getCategoryItemPage(uriInfo,
                                   page,
                                   itemName,
                                   language,
                                   pageModelVersion);
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

    private Pages getPages(final String domain) {

        return pagesRepo
            .findPagesForSite(domain)
            .orElseThrow(() -> new NotFoundException(String
            .format("No Pages for domain \"%s\" available.",
                    domain)));
    }

    private Category getCategory(final String domain,
                                 final Pages pages,
                                 final String pagePath) {

        return categoryRepo
            .findByPath(pages.getCategoryDomain(), pagePath)
            .orElseThrow(() -> new NotFoundException(String.format(
            "No Page for path \"%s\" in site \"%s\"",
            pagePath,
            domain)));

    }

    private ThemeInfo getTheme(final Site site,
                               final String theme,
                               final String themeVersion) {

        if ("--DEFAULT--".equals(theme)) {
            return themes
                .getTheme(site.getDefaultTheme(),
                          ThemeVersion.valueOf(themeVersion))
                .orElseThrow(() -> new WebApplicationException(
                String.format("The configured default theme \"%s\" for "
                                  + "site \"%s\" is not available.",
                              site.getDefaultTheme(),
                              site.getDomainOfSite()),
                Response.Status.INTERNAL_SERVER_ERROR));
        } else {
            return themes.getTheme(theme,
                                   ThemeVersion.valueOf(themeVersion))
                .orElseThrow(() -> new WebApplicationException(
                String.format("The theme \"%s\" is not available.",
                              theme),
                Response.Status.BAD_REQUEST));
        }
    }

    private Page getPage(final UriInfo uriInfo,
                         final String pagePath,
                         final String language,
                         final Map<String, Object> parameters) {

        Objects.requireNonNull(uriInfo);
        Objects.requireNonNull(pagePath);
        Objects.requireNonNull(parameters);

        final String domain = uriInfo.getBaseUri().getHost();
        final Pages pages = getPages(domain);
        final Category category = getCategory(domain, pages, pagePath);

        final Locale locale = new Locale(language);
        if (!category.getTitle().hasValue(locale)) {
            throw new NotFoundException();
        }

        globalizationHelper.setSelectedLocale(locale);

        parameters.put(PARAMETER_CATEGORY, category);
        return pageManager.findPageForCategory(category);
    }

    private Map<String, Object> buildPage(
        final PageModel pageModel,
        final Map<String, Object> parameters) {

        Objects.requireNonNull(pageModel);
        Objects.requireNonNull(parameters);

        final Map<String, Object> result;
        if (pageModel == null) {
            result = pageBuilder.renderPage(parameters);
        } else {
            result = pageBuilder.renderPage(pageModel, parameters);
        }

        return result;
    }

    private Map<String, Object> getCategoryIndexPage(
        final UriInfo uriInfo,
        final String pagePath,
        final String language,
        final String pageModelVersion) {

        final Map<String, Object> parameters = new HashMap<>();
        final Page page = getPage(uriInfo, pagePath, language, parameters);

        final PageModel pageModel;
        if ("DRAFT".equals(pageModelVersion)) {
            pageModel = pageModelManager
                .getDraftVersion(page.getIndexPageModel());
        } else {
            pageModel = pageModelManager
                .getLiveVersion(page.getIndexPageModel())
                .orElseThrow(() -> new NotFoundException(String
                .format("The PageModel for the index page of the category"
                            + "\"%s\" is not available as live version.",
                        pagePath)));
        }

        parameters.put(PARAMETER_LANGUAGE, language);

        return buildPage(pageModel, parameters);
    }

    private Map<String, Object> getCategoryItemPage(
        final UriInfo uriInfo,
        final String pagePath,
        final String itemName,
        final String language,
        final String pageModelVersion) {

        final Map<String, Object> parameters = new HashMap<>();
        final Page page = getPage(uriInfo, pagePath, language, parameters);

        final PageModel pageModel;
        if ("DRAFT".equals(pageModelVersion)) {
            pageModel = pageModelManager.getDraftVersion(page
                .getItemPageModel());
        } else {
            pageModel = pageModelManager
                .getLiveVersion(page.getItemPageModel())
                .orElseThrow(() -> new NotFoundException(String
                .format("The PageModel for the index page of the category"
                            + "\"%s\" is not available as live version.",
                        pagePath)));
        }

        parameters.put(PARAMETER_ITEMNAME, itemName);
        parameters.put(PARAMETER_LANGUAGE, language);

        return buildPage(pageModel, parameters);
    }

}
