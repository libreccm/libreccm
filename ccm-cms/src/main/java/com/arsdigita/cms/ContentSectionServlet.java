/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms;

import com.arsdigita.bebop.Page;
import com.arsdigita.cms.dispatcher.CMSDispatcher;
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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.arsdigita.cms.CMSConfig;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.Shiro;
import org.libreccm.web.CcmApplication;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.privileges.ItemPrivileges;

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
 * @author Sören Bernstein <quasi@quasiweb.de>
 * @author Peter Boy <pboy@barkhof.uni-bremen.de>
 */
public class ContentSectionServlet extends BaseApplicationServlet {

    /**
     * Internal logger instance to faciliate debugging. Enable logging output by
     * editing /WEB-INF/conf/log4j.properties int hte runtime environment and
     * set com.arsdigita.cms.ContentSectionServlet=DEBUG by uncommenting or
     * adding the line.
     */
    private static final Logger s_log = Logger.getLogger(
        ContentSectionServlet.class);
    /**
     * Stringarray of file name patterns for index files.
     */
//  private static final String[] WELCOME_FILES = new String[]{
//      "index.jsp", "index.html"
//   };

    // Some literals
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
    public static Map s_itemResolverCache = Collections
        .synchronizedMap(new HashMap());
//    private static Map s_itemURLCacheMap = null;
    /**
     * Whether to cache the content items
     */
    private static final boolean s_cacheItems = true;
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
        if (templatePath == null) {
            m_templatePath = CMSConfig.getConfig().getTemplateRootPath();
        } else {
            m_templatePath = config.getInitParameter("template-path");
        }

        Assert.exists(m_templatePath, String.class);
        Assert.isTrue(m_templatePath.startsWith("/"),
                      "template-path must start with '/'");
        Assert.isTrue(!m_templatePath.endsWith("/"),
                      "template-path must not end with '/'");

        // optional init-param named file-resolver from ~/WEB-INF/web.xml
        String resolverName = config.getInitParameter("file-resolver");
        if (resolverName == null) {
            m_resolver = WebConfig.getConfig().getResolver();
        } else {
            m_resolver = (ApplicationFileResolver) Classes.newInstance(
                resolverName);
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("Template path is " + m_templatePath + " with resolver "
                            + m_resolver.
                getClass().getName());
        }

        //ToDo addPage(CmsConstants.CONTENT_SECTION_PAGE, new ContentSectionPage());     // index page at address ~/cs
        //ToDo   addPage(CmsConstants.CONTENT_SECTION_ITEM_PAGE, new ContentSectionPage());     
    }

    /**
     * Internal service method, adds one pair of Url - Page to the internal hash
     * map, used as a cache.
     *
     * @param pathInfo url stub for a page to display
     * @param page     Page object to display
     */
    private void addPage(final String pathInfo, final Page page) {

        Assert.exists(pathInfo, String.class);
        Assert.exists(page, Page.class);
        // Current Implementation requires pathInfo to start with a leading '/'
        // SUN Servlet API specifies: "PathInfo *may be empty* or will start
        // with a '/' character."
        Assert.isTrue(pathInfo.startsWith("/"), "path starts not with '/'");

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
     * @param sreq
     * @param sresp
     * @param app
     *
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    protected void doService(HttpServletRequest sreq,
                             HttpServletResponse sresp,
                             CcmApplication app)
        throws ServletException, IOException {

        ContentSection section = (ContentSection) app;

        // ////////////////////////////////////////////////////////////////////
        // Prepare OLD style dispatcher based page service
        // ////////////////////////////////////////////////////////////////////
        /*
         * NOTE:
         * Used to resolve to SiteNodeRequestContext (old style applications)
         * which has been removed.
         * Resolves currently to 
         * KernelRequestContext which will be removed as well.
         */
        RequestContext ctx = DispatcherHelper.getRequestContext();
        String url = ctx.getRemainingURLPart();  // here KernelRequestContext now
        if (s_log.isInfoEnabled()) {
            s_log.info("Resolving URL " + url + " and trying as item first.");
        }
        final ItemResolver itemResolver = getItemResolver(section);

        // ////////////////////////////////////////////////////////////////////
        // Prepare NEW style servlet based bebob page service
        // ////////////////////////////////////////////////////////////////////
        String pathInfo = sreq.getPathInfo();

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
            // checkUserAccess(sreq, sresp);  // done in individual pages ??
            if (page instanceof CMSPage) {
                // backwards compatibility fix until migration completed
                final CMSPage cmsPage = (CMSPage) page;
                //  final RequestContext ctx = DispatcherHelper.getRequestContext();
                cmsPage.init();
                cmsPage.dispatch(sreq, sresp, ctx);
            } else {
                final CMSApplicationPage cmsAppPage = (CMSApplicationPage) page;
                cmsAppPage.init(sreq, sresp, app);
                // Serve the page.            
                final Document doc = cmsAppPage.buildDocument(sreq, sresp);

                PresentationManager pm = Templating.getPresentationManager();
                pm.servePage(doc, sreq, sresp);
            }

            /* SECONDLY try if we have to serve an item (old style dispatcher based */
        } else if (item != null) {

            serveItem(sreq, sresp, section, item);

            /* OTHERWISE delegate to a JSP in file system */
        } else {

            /* We have to deal with a content-section, folder or another bit  */
            if (s_log.isInfoEnabled()) {
                s_log.info("NOT serving content item");
            }

            /* Store content section in http request to make it available
             * for admin/index.jsp                                            */
            sreq.setAttribute(CONTENT_SECTION, section);

            RequestDispatcher rd = m_resolver.resolve(m_templatePath,
                                                      sreq, sresp, app);
            if (rd != null) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Got dispatcher " + rd);
                }
                sreq = DispatcherHelper.restoreOriginalRequest(sreq);
                rd.forward(sreq, sresp);
            } else {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("No dispatcher found for" + rd);
                }
                String requestUri = sreq.getRequestURI(); // same as ctx.getRemainingURLPart()
                sresp.sendError(404, requestUri + " not found on this server.");
            }
        }
    }   // END doService

    /**
     *
     * @param sreq
     * @param sresp
     * @param section
     * @param item
     *
     * @throws ServletException
     * @throws IOException
     */
    private void serveItem(HttpServletRequest sreq,
                           HttpServletResponse sresp,
                           ContentSection section,
                           ContentItem item)
        throws ServletException, IOException {

        if (s_log.isInfoEnabled()) {
            s_log.info("serving content item");
        }

        RequestContext ctx = DispatcherHelper.getRequestContext();
        String url = ctx.getRemainingURLPart();

        final ItemResolver itemResolver = getItemResolver(section);

        //set the content item in the request
        sreq.setAttribute(CONTENT_ITEM, item);

        //set the template context
        //TemplateResolver templateResolver = m_disp.getTemplateResolver(section);
        String templateURL = url;
        if (!templateURL.startsWith("/")) {
            templateURL = "/" + templateURL;
        }
        if (templateURL.startsWith(PREVIEW)) {
            templateURL = templateURL.substring(PREVIEW.length());
        }

        String sTemplateContext = itemResolver.getTemplateFromURL(templateURL);
        if (s_log.isDebugEnabled()) {
            s_log.debug("setting template context to " + sTemplateContext);
        }
        //templateResolver.setTemplateContext(sTemplateContext, sreq);

        // Work out how long to cache for....
        // We take minimum(default timeout, lifecycle expiry)
        //ToDo
        /*Lifecycle cycle = item.getLifecycle();
         */
        int expires = DispatcherHelper.getDefaultCacheExpiry();
        /*
        if (cycle != null) {
            Date endDate = cycle.getEndDate();

            if (endDate != null) {
                int maxAge = (int) ((endDate.getTime() - System
                                     .currentTimeMillis()) / 1000l);
                if (maxAge < expires) {
                    expires = maxAge;
                }
            }
        }*/
        //ToDo End

        // NB, this is not the same as the security check previously
        // We are checking if anyone can access - ie can we allow
        // this page to be publically cached
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentItemManager contentItemManager = cdiUtil.findBean(
            ContentItemManager.class);
        final PermissionChecker permissionChecker = cdiUtil.findBean(
            PermissionChecker.class);
        if (s_cacheItems && contentItemManager.isLive(item)) {
            if (permissionChecker.isPermitted(
                ItemPrivileges.VIEW_PUBLISHED, item)) {
                DispatcherHelper.cacheForWorld(sresp, expires);
            } else {
                DispatcherHelper.cacheForUser(sresp, expires);
            }
        } else {
            DispatcherHelper.cacheDisable(sresp);
        }

        //use ContentItemDispatcher
        m_disp.dispatch(sreq, sresp, ctx);
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

    /**
     * Fetches the ItemResolver for a content section. Checks cache first.
     *
     * @param section The content section
     *
     * @return The ItemResolver associated with the content section
     */
    public ItemResolver getItemResolver(ContentSection section) {

        String path = section.getPrimaryUrl();
        ItemResolver ir = (ItemResolver) s_itemResolverCache.get(path);

        if (ir == null) {
            final String itemResolverClassName = section.getItemResolverClass();
            try {
                ir = (ItemResolver) Class
                    .forName(section.getItemResolverClass()).newInstance();
            } catch (ClassNotFoundException |
                     IllegalAccessException |
                     InstantiationException ex) {
                throw new RuntimeException(ex);
            }
            s_itemResolverCache.put(path, ir);
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("using ItemResolver " + ir.getClass().getName());
        }

        return ir;
    }

    /**
     *
     * @param section
     * @param url
     * @param itemResolver
     *
     * @return
     */
    public ContentItem getItem(ContentSection section, String url,
                               ItemResolver itemResolver) {

        if (s_log.isDebugEnabled()) {
            s_log.debug("getting item at url " + url);
        }
        HttpServletRequest request = Web.getRequest();

        //first sanitize the url
        if (url.endsWith(XML_SUFFIX)) {
            request.setAttribute(XML_MODE, Boolean.TRUE);
            s_log.debug("StraightXML Requested");
            url = "/" + url.substring(0, url.length() - XML_SUFFIX.length());
        } else {
            request.setAttribute(XML_MODE, Boolean.FALSE);
            if (url.endsWith(FILE_SUFFIX)) {
                url = "/" + url
                    .substring(0, url.length() - FILE_SUFFIX.length());
            } else if (url.endsWith("/")) {
                url = "/" + url.substring(0, url.length() - 1);
            }
        }

        if (!url.startsWith("/")) {
            url = "/" + url;
        }

        ContentItem item;
        // Check if the user has access to view public or preview pages
        final PermissionChecker permissionChecker = CdiUtil.createCdiUtil()
            .findBean(PermissionChecker.class);
        boolean hasPermission = true;

        // If the remaining URL starts with "preview/", then try and
        // preview this item.  Otherwise look for the live item.
        boolean preview = false;
        if (url.startsWith(PREVIEW)) {
            url = url.substring(PREVIEW.length());
            preview = true;
        }

        if (preview) {
            if (s_log.isInfoEnabled()) {
                s_log.info("Trying to get item for PREVIEW");
            }

            item = itemResolver.getItem(section, url, CMSDispatcher.PREVIEW);
            if (item != null) {
                hasPermission = permissionChecker.isPermitted(
                    ItemPrivileges.PREVIEW, item);
            }
        } else {
            if (s_log.isInfoEnabled()) {
                s_log.info("Trying to get LIVE item");
            }

            //check if this item is in the cache
            //we only cache live items
            if (s_log.isDebugEnabled()) {
                s_log.debug("Trying to get content item for URL " + url
                                + " from cache");
            }

            // Get the negotiated locale
            String lang = CdiUtil.createCdiUtil().findBean(
                GlobalizationHelper.class).getNegotiatedLocale()
                .getLanguage();

            // XXX why assign a value and afterwards null??
            // Effectively it just ignores the cache and forces a fallback to
            // itemResover in any case. Maybe otherwise language selection /
            // negotiation doesn't work correctly?
//            item = itemURLCacheGet(section, url, lang);
            item = null;

            if (item == null) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Did not find content item in cache, so trying "
                                    + "to retrieve and cache...");
                }
                //item not cached, so retreive it and cache it
                item = itemResolver.getItem(section, url, "live");
//                itemURLCachePut(section, url, lang, item);
            } else if (s_log.isDebugEnabled()) {
                s_log.debug("Found content item in cache");
            }

            //ToDo
//            if (s_log.isDebugEnabled() && item != null) {
//                s_log.debug("Sanity check: item.getPath() is " + item.getPath());
//            }
            if (item != null) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Content Item is not null");
                }

                hasPermission = permissionChecker.isPermitted(
                    ItemPrivileges.VIEW_PUBLISHED, item);

                if (hasPermission) {
                }
            }
        }

        if (item == null && url.endsWith(INDEX_FILE)) {

            if (item == null) {
                if (s_log.isInfoEnabled()) {
                    s_log.info("no item found");
                }
            }

            // look up folder if it's an index
            url = url.substring(0, url.length() - INDEX_FILE.length());
            if (s_log.isInfoEnabled()) {
                s_log.info("Attempting to match folder " + url);
            }
            item = itemResolver.getItem(section, url, "live");
            if (item != null) {
                hasPermission = permissionChecker.isPermitted(
                    ItemPrivileges.VIEW_PUBLISHED, item);
            }
        }

        if (!hasPermission) {

            // first, check if the user is logged-in
            // if he isn't, give him a chance to do so...
            if (!CdiUtil.createCdiUtil().findBean(Shiro.class).getSubject()
                .isAuthenticated()) {
                throw new LoginSignal(request);
            }

            throw new AccessDeniedException();
        }

        return item;
    }

    public ContentItem getItem(ContentSection section, String url) {
        ItemResolver itemResolver = getItemResolver(section);

        return getItem(section, url, itemResolver);
    }

    //  synchronize access to the item-url cache
//    private static synchronized void itemURLCachePut(ContentSection section,
//                                                     String sURL,
//                                                     String lang,
//                                                     Long itemID) {
//
//        getItemURLCache(section).put(sURL + CACHE_KEY_DELIMITER + lang, itemID);
//    }
    /**
     * Maps the content item to the URL in a cache
     *
     * @param section the content section in which the content item is published
     * @param sURL    the URL at which the content item s published
     * @param lang
     * @param item    the content item at the URL
     */
//    public static synchronized void itemURLCachePut(ContentSection section,
//                                                    String sURL,
//                                                    String lang,
//                                                    ContentItem item) {
//        if (sURL == null || item == null) {
//            return;
//        }
//        if (s_log.isDebugEnabled()) {
//            s_log.debug("adding cached entry for url " + sURL + " and language "
//                            + lang);
//        }
//
//        itemURLCachePut(section, sURL, lang, item.getObjectId());
//    }
    /**
     * Removes the cache entry for the URL, sURL
     *
     * @param section the content section in which to remove the key
     * @param sURL    the cache entry key to remove
     * @param lang
     */
//    public static synchronized void itemURLCacheRemove(ContentSection section,
//                                                       String sURL,
//                                                       String lang) {
//        if (s_log.isDebugEnabled()) {
//            s_log.debug("removing cached entry for url " + sURL
//                            + "and language " + lang);
//        }
//        getItemURLCache(section).remove(sURL + CACHE_KEY_DELIMITER + lang);
//    }
    /**
     * Fetches the ContentItem published at that URL from the cache.
     *
     * @param section the content section in which the content item is published
     * @param sURL    the URL for the item to fetch
     * @param lang
     *
     * @return the ContentItem in the cache, or null
     */
//    public static ContentItem itemURLCacheGet(ContentSection section,
//                                              final String sURL,
//                                              final String lang) {
//        final Long itemID = (Long) getItemURLCache(section).get(
//            sURL + CACHE_KEY_DELIMITER + lang);
//
//        if (itemID == null) {
//            return null;
//        } else {
//            
//                final ContentItemRepository itemRepo = CdiUtil.createCdiUtil().findBean(ContentItemRepository.class);
//                return itemRepo.findById(itemID);
//            
//        }
//    }
//    private static synchronized CacheTable getItemURLCache(
//        ContentSection section) {
//        Assert.exists(section, ContentSection.class);
//        if (s_itemURLCacheMap == null) {
//            initializeItemURLCache();
//        }
//
//        if (s_itemURLCacheMap.get(section.getPath()) == null) {
//            final CacheTable cache = new CacheTable(
//                "ContentSectionServletItemURLCache" + section.getID().toString());
//            s_itemURLCacheMap.put(section.getPath(), cache);
//        }
//
//        return (CacheTable) s_itemURLCacheMap.get(section.getPath());
//    }
//
//    private static synchronized void initializeItemURLCache() {
//        ContentSectionCollection sections = ContentSection.getAllSections();
//        s_itemURLCacheMap = new HashMap();
//        while (sections.next()) {
//            ContentSection section = sections.getContentSection();
//            String idStr = section.getID().toString();
//            String path = section.getPath();
//            CacheTable itemURLCache = new CacheTable(
//                "ContentSectionServletItemURLCache" + idStr);
//            s_itemURLCacheMap.put(path, itemURLCache);
//
//        }
//    }
    /**
     * Checks that the current user has permission to access the admin pages.
     *
     * @param request
     * @param section
     *
     * @return
     *
     */
    public static boolean checkAdminAccess(HttpServletRequest request,
                                           ContentSection section) {
        return CdiUtil.createCdiUtil().findBean(PermissionChecker.class)
            .isPermitted(ItemPrivileges.EDIT,
                         section.getRootDocumentsFolder());
    }

}
