/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.librecms.contentsection;

import com.arsdigita.bebop.Page;
import com.arsdigita.cms.dispatcher.CMSPage;
import com.arsdigita.cms.dispatcher.ContentItemDispatcher;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.ui.CMSApplicationPage;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import com.arsdigita.util.Assert;
import com.arsdigita.util.Classes;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.ApplicationFileResolver;
import com.arsdigita.web.BaseApplicationServlet;
import com.arsdigita.web.LoginSignal;
import com.arsdigita.web.Web;
import com.arsdigita.web.WebConfig;
import com.arsdigita.xml.Document;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.Shiro;
import org.libreccm.web.CcmApplication;
import org.librecms.CmsConstants;
import org.librecms.contentsection.privileges.ItemPrivileges;
import org.librecms.lifecycle.Lifecycle;

import java.util.Date;

import javax.servlet.RequestDispatcher;

/*
 * This servlet will maybe removed. Our current plan is to integrate the navigation
 * application into ccm-cms, and to deliver all content using that app. Then 
 * this servlet becomes useless.
 */

 /*
 * NOTE:
 * Repaired ItemURLCache to save multilingual items with automatic
 * language negotiation. The cache now uses the remaining url part
 * and the language concatinated as a hash table key. The delimiter
 * is CACHE_KEY_DELIMITER.
 */

 /*
 * NOTE 2:
 * In a process of refactoring from legacy compatible to legacy free applications.
 * TODO:
 * - replace url check using RequestContext which resolves to SiteNodeRequest
 *   implementation (due to SiteNodeRequest used in BaseApplicationServlet). 
 * - Refactor content item UI bebop ApplicationPage or PageFactory instead of
 *   legacy infected sitenode / package dispatchers.
 */
/**
 * Content Section's Application Servlet according CCM core web application
 * structure {
 *
 * @see com.arsdigita.web.Application} implements the content section UI.
 *
 * It handles the UI for content items and delegates the UI for sections and
 * folders to jsp templates.
 *
 * @author unknown
 * @author <a href="mailto:pboy@barkhof.uni-bremen.de">Peter Boy</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@WebServlet(urlPatterns = {CmsConstants.CONTENT_SECTION_SERVLET_PATH})
public class ContentSectionServlet extends BaseApplicationServlet {

    private static final long serialVersionUID = 8061725145564728637L;

    private static final Logger LOGGER = LogManager.getLogger(
        ContentSectionServlet.class);

    /**
     * Literal for the prefix (in url) for previewing items
     */
    public static final String PREVIEW = "/preview";
    /**
     * Literal Template files suffix
     */
    public static final String FILE_SUFFIX = ".jsp";
    /**
     * Literal of URL Stub for index file name (includes leading slash)
     */
    public static final String INDEX_FILE = "/index";
    public static final String XML_SUFFIX = ".xml";
    public static final String XML_MODE = "xmlMode";
    public static final String MEDIA_TYPE = "templateContext";
    private static final String CACHE_KEY_DELIMITER = "%";

    public static final String CONTENT_ITEM
                                   = "com.arsdigita.cms.dispatcher.item";
    public static final String CONTENT_SECTION
                                   = "com.arsdigita.cms.dispatcher.section";

    private final ContentItemDispatcher m_disp = new ContentItemDispatcher();
    private static Map<String, ItemResolver> itemResolverCache = Collections
        .synchronizedMap(new HashMap<>());
    private static Map s_itemURLCacheMap = null;
    /**
     * Whether to cache the content items
     */
//    private static final boolean s_cacheItems = true;
    //  NEW STUFF here used to process the pages in this servlet
    /**
     * URL (pathinfo) -> Page object mapping. Based on it (and the http request
     * url) the doService method selects a page to display
     */
    private final Map m_pages = new HashMap();
    /**
     * Path to directory containg ccm-cms template (jsp) files
     */
    private String m_templatePath;

    /**
     * Resolver to actually use to find templates (JSP). JSP may be stored in
     * file system or otherwise, depends on resolver. Resolver is retrieved from
     * configuration. (probably used for other stuff as JSP's as well)
     */
    private ApplicationFileResolver m_resolver;

    /**
     * Init method overwrites parents init to pass in optional parameters
     * {@link com.arsdigita.web.BaseServlet}. If not specified system wide
     * defaults are used.
     *
     * @param config
     *
     * @throws javax.servlet.ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {

        super.init(config);

        // optional init-param named template-path from ~/WEB-INF/web.xml
        // may overwrite configuration parameters
        String templatePath = config.getInitParameter("template-path");
        //ToDo
        /*if (templatePath == null) {
            m_templatePath = ContentSection.getConfig().getTemplateRoot();
        } else {
            m_templatePath = config.getInitParameter("template-path");
        }*/

        // optional init-param named file-resolver from ~/WEB-INF/web.xml
        String resolverName = config.getInitParameter("file-resolver");

        //ToDo
        if (resolverName == null) {
            m_resolver = WebConfig.getConfig().getResolver();
        } else {
            m_resolver = (ApplicationFileResolver) Classes.newInstance(
                resolverName);
        }
        LOGGER.debug("Template path is {} with resolver {}",
                     m_templatePath,
                     m_resolver.getClass().getName());

        //  NEW STUFF here will be used to process the pages in this servlet
        //  Currently NOT working
        //   addPage("/admin", new MainPage());     // index page at address ~/cs
        //   addPage("/admin/index.jsp", new MainPage());     
        //   addPage("/admin/item.jsp", new MainPage());     
    }

    /**
     * Internal service method, adds one pair of Url - Page to the internal hash
     * map, used as a cache.
     *
     * @param pathInfo url stub for a page to display
     * @param page     Page object to display
     */
    private void addPage(final String pathInfo, final Page page) {
        m_pages.put(pathInfo, page);
    }

    /**
     * Implementation of parent's (abstract) doService method checks HTTP
     * request to determine whether to handle a content item or other stuff
     * which is delegated to jsp templates. {
     *
     * @see com.arsdigita.web.BaseApplicationServlet#doService
     * (HttpServletRequest, HttpServletResponse, Application)}
     *
     * @param request
     * @param response
     * @param app
     *
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    protected void doService(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final CcmApplication app)
        throws ServletException, IOException {

        if (!(app instanceof ContentSection)) {
            throw new IllegalArgumentException(
                "The provided application instance is not a content section.");
        }

        final ContentSection section = (ContentSection) app;

        final RequestContext ctx = DispatcherHelper.getRequestContext();
        final String url = ctx.getRemainingURLPart();
        
        //Only for testing PageModel
        if (url != null && url.endsWith("page-model/")) {
            getServletContext()
                    .getRequestDispatcher("/page-model.bebop")
                    .include(request, response);
            return;
        }
        //End Test PageModel
        
        LOGGER.info("Resolving URL {} and trying as item first.");
        final ItemResolver itemResolver = getItemResolver(section);

        String pathInfo = request.getPathInfo();

        final ContentItem item = getItem(section, pathInfo, itemResolver);

        Assert.exists(pathInfo, "String pathInfo");
        if (pathInfo.length() > 1 && pathInfo.endsWith("/")) {
            /* NOTE: ServletAPI specifies, pathInfo may be empty or will 
             * start with a '/' character. It currently carries a 
             * trailing '/' if a "virtual" page, i.e. not a real jsp, but 
             * result of a servlet mapping. But Application requires url 
             * NOT to end with a trailing '/' for legacy free applications.  */
            pathInfo = pathInfo.substring(0, pathInfo.length() - 1);
        }
        final Page page = (Page) m_pages.get(pathInfo);
        // ////////////////////////////////////////////////////////////////////
        // Serve the page
        // ////////////////////////////////////////////////////////////////////
        /* FIRST try new style servlet based service */
        if (page != null) {

            // Check user access.
            // checkUserAccess(request, response);  // done in individual pages ??
            if (page instanceof CMSPage) {
                // backwards compatibility fix until migration completed
                final CMSPage cmsPage = (CMSPage) page;
                //  final RequestContext ctx = DispatcherHelper.getRequestContext();
                cmsPage.init();
                cmsPage.dispatch(request, response, ctx);
            } else {
                final CMSApplicationPage cmsAppPage = (CMSApplicationPage) page;
                cmsAppPage.init(request, response, app);
                // Serve the page.            
                final Document doc = cmsAppPage.buildDocument(request, response);

                final PresentationManager pm = Templating
                    .getPresentationManager();
                pm.servePage(doc, request, response);
            }

            /* SECONDLY try if we have to serve an item (old style dispatcher based */
        } else if (item != null) {

            /* We have to serve an item here                                 */
            String param = request.getParameter("transID");

            serveItem(request, response, section, item);

            /* OTHERWISE delegate to a JSP in file system */
        } else {
            /* We have to deal with a content-section, folder or another bit  */
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("NOT serving content item");
            }

            /* Store content section in http request to make it available
             * for admin/index.jsp                                            */
            request.setAttribute(CONTENT_SECTION, section);

            RequestDispatcher rd = m_resolver.resolve(m_templatePath,
                                                      request, response, app);
            if (rd != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Got dispatcher " + rd);
                }
                rd.forward(DispatcherHelper.restoreOriginalRequest(request),
                           response);
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("No dispatcher found for" + rd);
                }
                String requestUri = request.getRequestURI(); // same as ctx.getRemainingURLPart()
                response.sendError(404, requestUri
                                            + " not found on this server.");
            }
        }

    }

    private void serveItem(final HttpServletRequest request,
                           final HttpServletResponse response,
                           final ContentSection section,
                           final ContentItem item)
        throws ServletException, IOException {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("serving content item");
        }

        RequestContext ctx = DispatcherHelper.getRequestContext();
        String url = ctx.getRemainingURLPart();

        final ItemResolver itemResolver = getItemResolver(section);

        //set the content item in the request
        request.setAttribute(CONTENT_ITEM, item);

        //set the template context
//        ToDo
//        final TemplateResolver templateResolver = m_disp.getTemplateResolver(
//            section);
//
//        String templateURL = url;
//        if (!templateURL.startsWith("/")) {
//            templateURL = "/" + templateURL;
//        }
//        if (templateURL.startsWith(PREVIEW)) {
//            templateURL = templateURL.substring(PREVIEW.length());
//        }
//
//        final String sTemplateContext = itemResolver.getTemplateFromURL(
//            templateURL);
//        LOGGER.debug("setting template context to {}", sTemplateContext);
//
//        templateResolver.setTemplateContext(sTemplateContext, request);
//        ToDo End

        // Work out how long to cache for....
        // We take minimum(default timeout, lifecycle expiry)
        Lifecycle cycle = item.getLifecycle();
        int expires = DispatcherHelper.getDefaultCacheExpiry();
        if (cycle != null) {
            Date endDate = cycle.getEndDateTime();

            if (endDate != null) {
                int maxAge = (int) ((endDate.getTime() - System
                                     .currentTimeMillis()) / 1000l);
                if (maxAge < expires) {
                    expires = maxAge;
                }
            }
        }

        //use ContentItemDispatcher
        m_disp.dispatch(request, response, ctx);
    }

    /**
     * Fetches the content section from the request attributes.
     *
     * @param request The HTTP request
     *
     * @return The content section
     *
     * @pre ( request != null )
     */
    public static ContentSection getContentSection(HttpServletRequest request) {
        return (ContentSection) request.getAttribute(CONTENT_SECTION);
    }

    @SuppressWarnings("unchecked")
    public ItemResolver getItemResolver(final ContentSection section) {

        final String path = section.getPrimaryUrl();
        final ItemResolver itemResolver;
        if (itemResolverCache.containsKey(path)) {
            itemResolver = itemResolverCache.get(path);
        } else {
            final String className = section.getItemResolverClass();
            final Class<ItemResolver> clazz;
            try {
                clazz = (Class<ItemResolver>) Class.forName(className);
                itemResolver = clazz.newInstance();
            } catch (ClassNotFoundException
                     | IllegalAccessException
                     | InstantiationException ex) {
                throw new UncheckedWrapperException(ex);
            }

            itemResolverCache.put(path, itemResolver);
        }

        LOGGER.debug("Using ItemResolver implementation \"{}\"...",
                     itemResolver.getClass().getName());

        return itemResolver;
    }

    public ContentItem getItem(final ContentSection section,
                               final String url,
                               final ItemResolver itemResolver) {

        LOGGER.debug("getting item at url {}", url);
        final HttpServletRequest request = Web.getRequest();

        //first sanitize the url
        String itemUrl = url;
        if (url.endsWith(XML_SUFFIX)) {
            request.setAttribute(XML_MODE, Boolean.TRUE);
            LOGGER.debug("StraightXML Requested");
            itemUrl = String.format(
                "/%s",
                url.substring(0, url.length() - XML_SUFFIX.length()));
            itemUrl = "/" + url.substring(0, url.length() - XML_SUFFIX.length());
        } else {
            request.setAttribute(XML_MODE, Boolean.FALSE);
            if (url.endsWith(FILE_SUFFIX)) {
                itemUrl = String.format(
                    "/%s",
                    url.substring(0, url.length() - FILE_SUFFIX.length()));
            } else if (url.endsWith("/")) {
                itemUrl = String.format("/%s",
                                        url.substring(0, url.length() - 1));
            }
        }

        if (!itemUrl.startsWith("/")) {
            itemUrl = String.format("/%s", itemUrl);
        }

        ContentItem item;
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PermissionChecker permissionChecker = cdiUtil.findBean(
            PermissionChecker.class);
        final ContentItemManager itemManager = cdiUtil.findBean(
            ContentItemManager.class);
        // Check if the user has access to view public or preview pages
        boolean hasPermission = true;

        // If the remaining URL starts with "preview/", then try and
        // preview this item.  Otherwise look for the live item.
        boolean preview = false;
        if (itemUrl.startsWith(PREVIEW)) {
            itemUrl = itemUrl.substring(PREVIEW.length());
            preview = true;
        }

        if (preview) {
            LOGGER.info("Trying to get item for PREVIEW");

            item = itemResolver.getItem(section,
                                        itemUrl,
                                        ContentItemVersion.DRAFT.toString());
            if (item != null) {
                hasPermission = permissionChecker.isPermitted(
                    ItemPrivileges.PREVIEW, item);
            }
        } else {
            LOGGER.info("Trying to get LIVE item");

            //check if this item is in the cache
            //we only cache live items
            LOGGER.debug("Trying to get content item for URL {}from cache",
                         itemUrl);

            // Get the negotiated locale
            final GlobalizationHelper globalizationHelper = cdiUtil.findBean(
                GlobalizationHelper.class);
            final String lang = globalizationHelper.getNegotiatedLocale()
                .getLanguage();

            // XXX why assign a value and afterwards null??
            // Effectively it just ignores the cache and forces a fallback to
            // itemResover in any case. Maybe otherwise language selection /
            // negotiation doesn't work correctly?
            item = null;

            if (item == null) {
                LOGGER.debug("Did not find content item in cache, so trying "
                                 + "to retrieve and cache...");
                //item not cached, so retreive it and cache it
                item = itemResolver.getItem(section,
                                            itemUrl,
                                            ContentItemVersion.LIVE.toString());

                if (LOGGER.isDebugEnabled() && item != null) {
                    LOGGER.debug("Sanity check: item.getPath() is {}",
                                 itemManager.getItemPath(item));
                }

                if (item != null) {
                    LOGGER.debug("Content Item is not null");

                    hasPermission = permissionChecker.isPermitted(
                        ItemPrivileges.VIEW_PUBLISHED, item);
                }
            }

        }

        if (item == null && itemUrl.endsWith(INDEX_FILE)) {

            if (item == null) {
                LOGGER.info("no item found");
            }

            // look up folder if it's an index
            itemUrl = itemUrl.substring(0, itemUrl.length() - INDEX_FILE
                                        .length());
            LOGGER.info("Attempting to match folder " + itemUrl);

            item = itemResolver.getItem(section,
                                        itemUrl,
                                        ContentItemVersion.LIVE.toString());
            if (item != null) {
                hasPermission = permissionChecker.isPermitted(
                    ItemPrivileges.VIEW_PUBLISHED, item);
            }
        }

        if (!hasPermission) {

            // first, check if the user is logged-in
            // if he isn't, give him a chance to do so...
            final Shiro shiro = cdiUtil.findBean(Shiro.class);

            if (shiro.getSubject().isAuthenticated()) {
                throw new LoginSignal(request);
            }

            throw new AccessDeniedException();
        }

        return item;
    }

    public ContentItem getItem(final ContentSection section, final String url) {
        final ItemResolver itemResolver = getItemResolver(section);

        return getItem(section, url, itemResolver);
    }

    //  synchronize access to the item-url cache
//    private static synchronized void itemURLCachePut(
//        final ContentSection section,
//        final String sURL,
//        final String lang,
//        final Long itemID) {
//
//        getItemURLCache(section).put(String.format(
//            "%s" + CACHE_KEY_DELIMITER + "%s", sURL, lang), itemID);
//    }
//
//    /**
//     * Maps the content item to the URL in a cache
//     *
//     * @param section the content section in which the content item is published
//     * @param url     the URL at which the content item s published
//     * @param lang
//     * @param item    the content item at the URL
//     */
//    public static synchronized void itemURLCachePut(
//        final ContentSection section,
//        final String url,
//        final String lang,
//        final ContentItem item) {
//
//        if (url == null || item == null) {
//            return;
//        }
//        LOGGER.debug("adding cached entry for url {} and language {}",
//                     url,
//                     lang);
//
//        itemURLCachePut(section, url, lang, item.getObjectId());
//    }
//
//    /**
//     * Removes the cache entry for the URL, sURL
//     *
//     * @param section the content section in which to remove the key
//     * @param url     the cache entry key to remove
//     * @param lang
//     */
//    public static synchronized void itemURLCacheRemove(
//        final ContentSection section,
//        final String url,
//        final String lang) {
//
//        LOGGER.debug("removing cached entry for url {} and language {} ",
//                     url,
//                     lang);
//
//        getItemURLCache(section).remove(url + CACHE_KEY_DELIMITER + lang);
//    }
//    
//    /**
//     * Fetches the ContentItem published at that URL from the cache.
//     * 
//     * @param section the content section in which the content item is published
//     * @param url the URL for the item to fetch
//     * @param lang 
//     * @return the ContentItem in the cache, or null
//     */
//    public static ContentItem itemURLCacheGet(final ContentSection section,
//                                              final String url,
//                                              final String lang) {
//        final Long itemID = (Long) getItemURLCache(section).get(
//                url + CACHE_KEY_DELIMITER + lang);
//
//        if (itemID == null) {
//            return null;
//        } else {
//            final ContentItemRepository itemRepo = CdiUtil.createCdiUtil().findBean(ContentItemRepository.class);
//            try {
//                return itemRepo.findById(itemID);
//            } catch(NoResultException ex) {
//                return null;
//            }
//        }
//    }
//    
//        private static synchronized CacheTable getItemURLCache(ContentSection section) {
//        Assert.exists(section, ContentSection.class);
//        if (s_itemURLCacheMap == null) {
//            initializeItemURLCache();
//        }
//        
//        if (s_itemURLCacheMap.get(section.getPath()) == null) {
//            final CacheTable cache = new CacheTable("ContentSectionServletItemURLCache" + 
//                                                    section.getID().toString());
//            s_itemURLCacheMap.put(section.getPath(), cache);
//        }
//        
//        return (CacheTable) s_itemURLCacheMap.get(section.getPath());
//    }
}
