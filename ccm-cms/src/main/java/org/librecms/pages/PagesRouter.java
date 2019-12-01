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
import org.libreccm.pagemodel.PageModelVersion;
import org.libreccm.sites.Site;
import org.libreccm.sites.SiteRepository;
import org.libreccm.theming.ThemeInfo;
import org.libreccm.theming.ThemeVersion;
import org.libreccm.theming.Themes;
import org.librecms.contentsection.ContentItemVersion;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
@Path("/")
public class PagesRouter {

    protected static final String SITE_INFO = "siteInfo";

    protected static final String SITE_INFO_NAME = "name";

    protected static final String SITE_INFO_DOMAIN = "domain";

    protected static final String SITE_INFO_HOST = "host";

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
    public Response redirectToIndexPage(@Context final UriInfo uriInfo) {

        final String domain = uriInfo.getBaseUri().getHost();
        final Pages pages = getPages(domain);
        final Category category = getCategory(domain, pages, "/");
        final String language = determineLanguage(category);

        final String indexPage = String.format("/index.%s.html", language);
        final URI uri = uriInfo.getBaseUriBuilder().path(indexPage).build();
        return Response.temporaryRedirect(uri).build();
    }

    @GET
    @Path("/{name:[\\w\\-]+}")
    @Transactional(Transactional.TxType.REQUIRED)
    public Response getRootPage(
        @Context final UriInfo uriInfo,
        @PathParam("name") final String itemName) {

        final String domain = uriInfo.getBaseUri().getHost();
        final Pages pages = getPages(domain);
        final Category category = getCategory(domain, pages, "/");
        final String language = determineLanguage(category);

        final String itemPage = String.format("/%s.%s.html", itemName, language);
        final URI uri = uriInfo.getBaseUriBuilder().path(itemPage).build();
        return Response.temporaryRedirect(uri).build();
    }

    @GET
    @Path("/{name:[\\w\\-]+}.html")
    @Transactional(Transactional.TxType.REQUIRED)
    public Response getRootPageAsHtml(
        @Context final UriInfo uriInfo,
        @PathParam("name") final String itemName) {

        final String domain = uriInfo.getBaseUri().getHost();
        final Pages pages = getPages(domain);
        final Category category = getCategory(domain, pages, "/");
        final String language = determineLanguage(category);

        final String itemPage = String.format("/%s.%s.html", itemName, language);
        final String path = uriInfo
            .getPath()
            .replace(String.format("%s.html", itemName), itemPage);

        final URI uri = uriInfo.getBaseUriBuilder().path(path).build();
        return Response.temporaryRedirect(uri).build();
    }

    @GET
    @Path("/{name:[\\w\\-]+}.{lang:\\w+}.html")
    @Produces("text/html")
    @Transactional(Transactional.TxType.REQUIRED)
    public String getRootPageAsHtml(
        @Context
        final UriInfo uriInfo,
        @PathParam("name")
        final String itemName,
        @PathParam("lang")
        final String language,
        @QueryParam("theme")
        @DefaultValue("--DEFAULT--")
        final String theme,
        @QueryParam("preview")
        @DefaultValue("")
        final String preview) {

        final Versions versions = generateFromPreviewParam(preview);

        final Map<String, Object> result;
        if ("index".equals(itemName)) {
            result = getCategoryIndexPage(uriInfo,
                                          "/",
                                          language,
                                          versions.getPageModelVersion());
        } else {
            result = getCategoryItemPage(uriInfo,
                                         "/",
                                         itemName,
                                         language,
                                         versions.getPageModelVersion());
        }
        final Site site = getSite(uriInfo);
        final ThemeInfo themeInfo = getTheme(site,
                                             theme,
                                             versions.getThemeVersion());

        return themes.process(result, themeInfo);
    }

    @GET
    @Path("/{name:[\\w\\-]+}.{lang:\\w+}.json")
    @Produces("application/json")
    @Transactional(Transactional.TxType.REQUIRED)
    public String getRootPageAsJson(
        @Context
        final UriInfo uriInfo,
        @PathParam("name")
        final String itemName,
        @PathParam("lang")
        final String language,
        @QueryParam("preview")
        @DefaultValue("")
        final String preview) {

        final Versions versions = generateFromPreviewParam(preview);

        final ObjectMapper mapper = new ObjectMapper();

        final Map<String, Object> result;
        if ("index".equals(itemName)) {
            result = getCategoryIndexPage(uriInfo,
                                          "/",
                                          language,
                                          versions.getPageModelVersion());
        } else {
            result = getCategoryItemPage(uriInfo,
                                         "/",
                                         itemName,
                                         language,
                                         versions.getPageModelVersion());
        }

        try {
            final String json = mapper.writeValueAsString(result);
            return json;
        } catch (JsonProcessingException ex) {
            throw new WebApplicationException(ex);
        }
    }

    @GET
    @Path("/{name:[\\w\\-]+}.{lang:\\w+}.xml")
    @Produces("text/xml")
    @Transactional(Transactional.TxType.REQUIRED)
    public String getRootPageAsXml(
        @Context
        final UriInfo uriInfo,
        @PathParam("name")
        final String itemName,
        @PathParam("lang")
        final String language,
        @QueryParam("preview")
        @DefaultValue("")
        final String preview) {

        final Versions versions = generateFromPreviewParam(preview);

        final JacksonXmlModule xmlModule = new JacksonXmlModule();
        final ObjectMapper mapper = new XmlMapper(xmlModule);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        final Map<String, Object> result;
        if ("index".equals(itemName)) {
            result = getCategoryIndexPage(uriInfo,
                                          "/",
                                          language,
                                          versions.getPageModelVersion());
        } else {
            result = getCategoryItemPage(uriInfo,
                                         "/",
                                         itemName,
                                         language,
                                         versions.getPageModelVersion());
        }

        try {
            final String html = mapper
                .writer()
                .withRootName("page")
                .writeValueAsString(result);
            return html;
        } catch (JsonProcessingException ex) {
            throw new WebApplicationException(ex);
        }
    }

    /**
     * Retrieve the item page for a category and the content item associated
     * with the category and identified by {@code itemName}.
     *
     * Redirects to
     * {@link #getPageAsHtml(javax.ws.rs.core.UriInfo, java.lang.String, java.lang.String)}.
     *
     * @param uriInfo
     * @param page
     * @param itemName
     *
     * @return
     */
    @GET
    @Path("/{page:[\\w/]+}/{name:[\\w\\-]+}")
    @Transactional(Transactional.TxType.REQUIRED)
    public Response getPage(
        @Context final UriInfo uriInfo,
        @PathParam("page") final String page,
        @PathParam("name") final String itemName) {

        final String domain = uriInfo.getBaseUri().getHost();
        final Pages pages = getPages(domain);
        final Category category = getCategory(domain, pages, page);
        final String language = determineLanguage(category);

        final String redirectTo;
        if (uriInfo.getPath().endsWith("/")) {
            redirectTo = String.format("%sindex.%s.html",
                                       uriInfo.getPath(),
                                       language);
        } else {
            final String itemPath = String.format("%s.%s.html",
                                                  itemName,
                                                  language);
            redirectTo = uriInfo.getPath().replace(itemName, itemPath);
        }

        final URI uri = uriInfo.getBaseUriBuilder().path(redirectTo).build();
        return Response.temporaryRedirect(uri).build();
    }

    /**
     * Retrieve the item page for a category and the content item associated
     * with the category and identified by {@code itemName}. Redirects to
     * {@link #getPageAsHtml(javax.ws.rs.core.UriInfo, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
     *
     * @param uriInfo
     * @param page
     * @param itemName
     *
     * @return
     */
    @GET
    @Path("/{page:[\\w/]+}/{name:[\\w\\-]+}.html")
    @Transactional(Transactional.TxType.REQUIRED)
    public Response getPageAsHtml(
        @Context final UriInfo uriInfo,
        @PathParam("page") final String page,
        @PathParam("name") final String itemName) {

        final String domain = uriInfo.getBaseUri().getHost();
        final Pages pages = getPages(domain);
        final Category category = getCategory(domain, pages, page);
        final String language = determineLanguage(category);

        final String redirectTo;
        if (uriInfo.getPath().endsWith("/")) {
            redirectTo = String.format("%sindex.%s.html",
                                       uriInfo.getPath(),
                                       language);
        } else {
            final String itemPath = String.format("%s.%s.html",
                                                  itemName,
                                                  language);
            redirectTo = uriInfo.getPath().replace(itemName, itemPath);
        }

        final URI uri = uriInfo.getBaseUriBuilder().path(redirectTo).build();
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
     * @param preview
     *
     * @return
     */
    @GET
    @Path("/{page:[\\w/]+}/{name:[\\w\\-]+}.{lang:\\w+}.html")
    @Produces("text/html")
    @Transactional(Transactional.TxType.REQUIRED)
    public String getPageAsHtml(
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
        @QueryParam("preview")
        @DefaultValue("")
        final String preview) {

        final Versions versions = generateFromPreviewParam(preview);

        final Map<String, Object> result;
        if ("index".equals(itemName)) {
            result = getCategoryIndexPage(uriInfo,
                                          page,
                                          language,
                                          versions.getPageModelVersion());
        } else {
            result = getCategoryItemPage(uriInfo,
                                         page,
                                         itemName,
                                         language,
                                         versions.getPageModelVersion());
        }

        final Site site = getSite(uriInfo);
        final ThemeInfo themeInfo = getTheme(site,
                                             theme,
                                             versions.getThemeVersion());
        return themes.process(result, themeInfo);
    }

    /**
     * Retrieve the item page as JSON for a category and the content item
     * associated with the category and identified by {@code itemName}.
     *
     * @param uriInfo
     * @param page
     * @param itemName
     * @param language
     * @param preview
     *
     * @return
     */
    @GET
    @Path("/{page:[\\w/]+}/{name:[\\w\\-]+}.{lang:\\w+}.json")
    @Produces("application/json")
    @Transactional(Transactional.TxType.REQUIRED)
    public String getPageAsJson(
        @Context
        final UriInfo uriInfo,
        @PathParam("page")
        final String page,
        @PathParam("name")
        final String itemName,
        @PathParam("lang")
        final String language,
        @QueryParam("preview")
        @DefaultValue("")
        final String preview) {

        final Versions versions = generateFromPreviewParam(preview);

        final ObjectMapper mapper = new ObjectMapper();

        final Map<String, Object> result;
        if ("index".equals(itemName)) {
            result = getCategoryIndexPage(uriInfo,
                                          page,
                                          language,
                                          versions.getPageModelVersion());
        } else {
            result = getCategoryItemPage(uriInfo,
                                         page,
                                         itemName,
                                         language,
                                         versions.getPageModelVersion());
        }

        try {
            final String json = mapper.writeValueAsString(result);
            return json;
        } catch (JsonProcessingException ex) {
            throw new WebApplicationException(ex);
        }
    }

    /**
     * Retrieve the item page as XML for a category and the content item
     * associated with the category and identified by {@code itemName}.
     *
     * @param uriInfo
     * @param page
     * @param itemName
     * @param language
     * @param preview
     *
     * @return
     */
    @GET
    @Path("/{page:[\\w/]+}/{name:[\\w\\-]+}.{lang:\\w+}.xml")
    @Produces("text/xml")
    @Transactional(Transactional.TxType.REQUIRED)
    public String getPageAsXml(
        @Context
        final UriInfo uriInfo,
        @PathParam("page")
        final String page,
        @PathParam("name")
        final String itemName,
        @PathParam("lang")
        final String language,
        @QueryParam("preview")
        @DefaultValue("")
        final String preview) {

        final Versions versions = generateFromPreviewParam(preview);

        final JacksonXmlModule xmlModule = new JacksonXmlModule();
        final ObjectMapper mapper = new XmlMapper(xmlModule);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        final Map<String, Object> result;
        if ("index".equals(itemName)) {
            result = getCategoryIndexPage(uriInfo,
                                          page,
                                          language,
                                          versions.getPageModelVersion());
        } else {
            result = getCategoryItemPage(uriInfo,
                                         page,
                                         itemName,
                                         language,
                                         versions.getPageModelVersion());
        }

        try {
            final String html = mapper
                .writer()
                .withRootName("page")
                .writeValueAsString(result);
            return html;
        } catch (JsonProcessingException ex) {
            throw new WebApplicationException(ex);
        }
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

    private String determineLanguage(final Category category) {

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

        return language;
    }

    private ThemeInfo getTheme(final Site site,
                               final String theme,
                               final ThemeVersion themeVersion) {

        if ("--DEFAULT--".equals(theme)) {
            return themes
                .getTheme(site.getDefaultTheme(), themeVersion)
                .orElseThrow(() -> new WebApplicationException(
                String.format("The configured default theme \"%s\" for "
                                  + "site \"%s\" is not available.",
                              site.getDefaultTheme(),
                              site.getDomainOfSite()),
                Response.Status.INTERNAL_SERVER_ERROR));
        } else {
            return themes.getTheme(theme, themeVersion)
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
        final Map<String, Object> siteInfo = new HashMap<>();
        siteInfo.put(SITE_INFO_HOST, uriInfo.getBaseUri().getHost());
        siteInfo.put(SITE_INFO_DOMAIN, pages.getSite().getDomainOfSite());
        siteInfo.put(SITE_INFO_NAME, pages.getSite().getDisplayName());
        parameters.put(SITE_INFO, siteInfo);
        final Category category = getCategory(domain, pages, pagePath);

        final Locale locale = new Locale(language);
        // disabled. Needs to be decided if the available languages of the 
        // index item or of the category are
        // used to decide if a NotFoundException is thrown.
//        if (!category.getTitle().hasValue(locale)) {
//            throw new NotFoundException();
//        }

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
        final PageModelVersion pageModelVersion) {

        final Map<String, Object> parameters = new HashMap<>();
        final Page page = getPage(uriInfo,
                                  pagePath,
                                  language,
                                  parameters);

        final PageModel pageModel;
        if (pageModelVersion == PageModelVersion.DRAFT) {
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
        final PageModelVersion pageModelVersion) {

        final Map<String, Object> parameters = new HashMap<>();
        final Page page = PagesRouter.this.getPage(uriInfo, pagePath, language,
                                                   parameters);

        final PageModel pageModel;
        if (pageModelVersion == PageModelVersion.DRAFT) {
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

    /**
     * Parse the value of the {@code preview} query parameter.
     *
     * @param value The value of the {@code preview} query parameter to parse.
     *
     * @return If the provided value is {@code all} a {@link Versions} object
     *         with all versions set to the draft versions is created and
     *         returned. If the provided value is {@code null} or empty a
     *         {@link Versions} object with fields set the the live versions is
     *         returned. Otherwise the values is split into tokens (separated by
     *         commas). The values of the returned {@link Versions} depend on
     *         presence of certain tokens. At the moment to following tokens are
     *         recognised:
     * <dl>
     * <dt>{@code content}</dt>
     * <dd>{@link Versions#contentVersion} is set to
     * {@link ContentItemVersion#DRAFT}</dd>
     * <dt>{@code pagemodel}</dt>
     * <dd>{@link Versions#pageModelVersion} is set to
     * {@link PageModelVersion#DRAFT}</dd>
     * <dt>{@code theme}</dt>
     * <dd>{@link Versions#themeVersion} is set to
     * {@link ThemeVersion#DRAFT}.</dd>
     * </dl>
     *
     */
    private Versions generateFromPreviewParam(final String value) {

        if (value == null || value.isEmpty() || value.matches("\\s*")) {
            return new Versions(ContentItemVersion.LIVE,
                                PageModelVersion.LIVE,
                                ThemeVersion.LIVE);
        } else if ("all".equals(value.toLowerCase(Locale.ROOT))) {
            return new Versions(ContentItemVersion.DRAFT,
                                PageModelVersion.DRAFT,
                                ThemeVersion.DRAFT);
        } else {
            final Set<String> values = new HashSet<>();
            Collections.addAll(values,
                               value.toLowerCase(Locale.ROOT).split(","));

            final Versions result = new Versions();
            if (values.contains("content")) {
                result.setContentVersion(ContentItemVersion.DRAFT);
            }
            if (values.contains("pagemodel")) {
                result.setPageModelVersion(PageModelVersion.DRAFT);
            }
            if (values.contains("theme")) {
                result.setThemeVersion(ThemeVersion.DRAFT);
            }
            return result;
        }
    }

    /**
     * Encapsulate the result of converting the value of the {@code preview}
     * query parameter.
     */
    private class Versions {

        /**
         * Version of content to use
         */
        private ContentItemVersion contentVersion;
        /**
         * Version of {@link PageModel} to use.
         */
        private PageModelVersion pageModelVersion;
        /**
         * Version of theme to use.
         */
        private ThemeVersion themeVersion;

        /**
         * Creates a new {@code Versions} object with all fields set to
         * {@code live} versions.
         */
        public Versions() {
            this.contentVersion = ContentItemVersion.LIVE;
            this.pageModelVersion = PageModelVersion.LIVE;
            this.themeVersion = ThemeVersion.LIVE;
        }

        /**
         * Create a new {@code Versions} object with the provided parameters.
         *
         * @param contentVersion
         * @param pageModelVersion
         * @param themeVersion
         */
        public Versions(final ContentItemVersion contentVersion,
                        final PageModelVersion pageModelVersion,
                        final ThemeVersion themeVersion) {
            this.contentVersion = contentVersion;
            this.pageModelVersion = pageModelVersion;
            this.themeVersion = themeVersion;
        }

        public ContentItemVersion getContentVersion() {
            return contentVersion;
        }

        public void setContentVersion(final ContentItemVersion contentVersion) {
            this.contentVersion = contentVersion;
        }

        public PageModelVersion getPageModelVersion() {
            return pageModelVersion;
        }

        public void setPageModelVersion(final PageModelVersion pageModelVersion) {
            this.pageModelVersion = pageModelVersion;
        }

        public ThemeVersion getThemeVersion() {
            return themeVersion;
        }

        public void setThemeVersion(final ThemeVersion themeVersion) {
            this.themeVersion = themeVersion;
        }

    }

}
