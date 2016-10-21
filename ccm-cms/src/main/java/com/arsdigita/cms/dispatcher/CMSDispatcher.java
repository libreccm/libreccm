/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.dispatcher;

import com.arsdigita.dispatcher.ChainedDispatcher;
import com.arsdigita.dispatcher.Dispatcher;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.JSPApplicationDispatcher;
import com.arsdigita.dispatcher.RedirectException;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.web.LoginSignal;
import com.arsdigita.web.URL;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.AuthorizationException;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.Shiro;
import org.libreccm.security.User;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.contentsection.privileges.ItemPrivileges;

/**
 * <p>
 * The CMS Dispatcher serves all request made within a content section. This
 * dispatcher is called by the Subsite dispatcher.</p>
 *
 * <p>
 * Here are the steps for a request to
 * <tt>http://yourserver/cms/cheese</tt> in excruciating detail:</p>
 *
 * <ol>
 * <li><p>
 * A client sends a request to the web server, which passes it on to the global
 * ACS dispatcher.</p></li>
 *
 * <li><p>
 * The global ACS dispatcher examines the first part of the URL, notices that
 * CMS is mounted at <tt>/cms</tt> and hands the request to the CMS
 * dispatcher.</p></li>
 *
 * <li><p>
 * The CMS dispatcher determines whether a <tt>Page</tt> has been registered to
 * the URL <tt>/cheese</tt> in this section via its
 * {@link com.arsdigita.cms.dispatcher.PageResolver}.</p></li>
 *
 * <li><p>
 * Since no page is registered to the URL, the CMS dispatcher asks the content
 * section (via its {@link com.arsdigita.cms.dispatcher.ItemResolver}) for a
 * content item for <tt>/cheese</tt> in this content section. The result of this
 * process is a {@link com.arsdigita.cms.ContentItem} object.</p></li>
 *
 * <li><p>
 * The CMS dispatcher asks the content section for a <tt>Page</tt>
 * to use as the "master template" for this item. The content section may apply
 * item-, type-, or request-specific rules to make this decision (for example,
 * check a user preference for normal or accessible style, or a query parameter
 * for a printable version).</p></li>
 *
 * <li><p>
 * The CMS dispatcher hands the master <tt>Page</tt> object to the
 * {@link com.arsdigita.sitenode.SiteNodePresentationManager} to serve the
 * page.</p></li>
 *
 * <li><p>
 * The presentation manager asks the master <tt>Page</tt> object for an XML
 * document representing the data for the page.</p></li>
 *
 * <li><p>
 * The master template begins walking through its component hierarchy,
 * converting each component to XML by calling its
 * <tt>generateXML</tt> method. The component responsible for rendering the
 * content item uses an {@link com.arsdigita.cms.dispatcher.XMLGenerator} to
 * convert the content item to XML.</p></li>
 *
 * <li><p>
 * The presentation manager receives the completed XML document, and selects an
 * XSL transformer to use for generating the HTML. The stylesheet on which the
 * transformer is based contains templates for all styles and all content types
 * in the content section, in particular those from the file
 * <tt>cms-item.xsl</tt>.</p></li>
 * </ol>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @author Uday Mathur (umathur@arsdigita.com)
 * @author Jack Chung (flattop@arsdigita.com)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CMSDispatcher implements Dispatcher, ChainedDispatcher {

    private static Logger s_log = Logger.getLogger(CMSDispatcher.class);

    public static final String CONTENT_SECTION
                                   = "com.arsdigita.cms.dispatcher.section";

    public static final String CONTENT_ITEM
                                   = "com.arsdigita.cms.dispatcher.item";

    public static final String[] INDEX_FILES = {
        "index.jsp", "index.html", "index.htm"};

    private static final String DEBUG = "/debug";
    private static final String ADMIN_SECTION = "admin";

    public static final String ADMIN_URL = "admin/index";

    /**
     * The context for previewing items
     */
    public static final String PREVIEW = "preview";

    // Content section cache
    private static HashMap s_pageResolverCache = new HashMap();
    private static HashMap s_itemResolverCache = new HashMap();
    private static HashMap s_xmlGeneratorCache = new HashMap();

    private boolean m_adminPagesOnly = false;

    public CMSDispatcher() {
        this(false);
    }

    public CMSDispatcher(boolean adminOnly) {
        m_adminPagesOnly = adminOnly;
    }

    /**
     * Handles requests made to a CMS package instance. 1) fetches the current
     * content section 2) fetches the resource mapped to the current section/URL
     * 3) if no resource, fetches the item associated with the current
     * section/URL 4) if no item, passes request to the JSP dispatcher, which
     * serves JSP's, HTML pages, and media from the cms/packages/www directory
     *
     * @param request  The request
     * @param response The response
     * @param actx     The request context
     */
    public void dispatch(HttpServletRequest request,
                         HttpServletResponse response,
                         RequestContext actx)
        throws IOException, ServletException {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Dispatching request for " + new URL(request)
                .toDebugString());
        }

        // This is the path to the current site node.
        String processedUrl = actx.getProcessedURLPart();
        String webappURLContext = request.getContextPath();
        if (processedUrl.startsWith(webappURLContext)) {
            processedUrl = processedUrl.substring(webappURLContext.length());
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Determined the path to the current site node; it "
                            + "is '" + processedUrl + "' according to the "
                            + "request context");
        }

        // This is the path within the site node.
        String remainingUrl = actx.getRemainingURLPart();
        if (remainingUrl.endsWith("/")) {
            remainingUrl = remainingUrl.substring(0, remainingUrl.length() - 1);
        } else if (remainingUrl.endsWith(ItemDispatcher.FILE_SUFFIX)) {
            remainingUrl = remainingUrl.substring(0, remainingUrl.length()
                                                         - ItemDispatcher.FILE_SUFFIX
                                                  .length());
        } else if (remainingUrl.equals("")) {
            remainingUrl = "index";
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Determined the path after the current site node; "
                            + "it is '" + remainingUrl + "'");
        }

        // Fetch the current content section.
        ContentSection section = null;
        try {
            section = findContentSection(processedUrl);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
        request.setAttribute(CONTENT_SECTION, section);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Found content section '" + section + "'");
        }

        // Check user access to this page and deny access if necessary.
        checkUserAccess(request, response, actx);

        // Look for a site-node-specific asset (if any).
        // KG: This hack will be replaced by a ChainedDispatcher
        try {
            s_log.debug("Looking for a site node asset");

            String siteNodeAssetURL = getSiteNodeAsset(request, actx);
            if (siteNodeAssetURL != null) {
                s_log.debug("Site node asset found at '" + siteNodeAssetURL
                                + "'");

                DispatcherHelper.cacheDisable(response);
                DispatcherHelper.setRequestContext(request, actx);
                DispatcherHelper.forwardRequestByPath(siteNodeAssetURL,
                                                      request, response);
                return;
            }

            s_log.debug("No site node asset found; proceeding with normal "
                            + "dispatching");
        } catch (RedirectException e) {
            throw new ServletException(e);
        }

        // Fetch the requested resource (if any).
        ResourceHandler resource = getResource(section, remainingUrl);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Got a resource '" + resource + "'");
        }

        if (resource != null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Found resource '" + remainingUrl + "'; "
                                + "dispatching to it");
            }

            s_log.info("resource dispatch for " + remainingUrl);
            // Found resource, now serve it.
            // NB, ResouceHandler implementations should take care of caching options
            resource.dispatch(request, response, actx);

        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug("No resource found at '" + remainingUrl + "'; "
                                + "searching for a previewable content item at "
                                + "this path");
            }

            // If the remaining URL starts with "preview/", then try and
            // preview this item.  Otherwise look for the live item.
            boolean preview = false;
            if (remainingUrl.startsWith(PREVIEW)) {
                remainingUrl = remainingUrl.substring(PREVIEW.length());
                preview = true;
            }

            // Check for published / previewable item.
            ContentItem item = null;

            // Check if the user has access to view public pages
            final PermissionChecker permissionChecker = CdiUtil.createCdiUtil()
                .findBean(PermissionChecker.class);

            if (permissionChecker.isPermitted(
                ItemPrivileges.VIEW_PUBLISHED, item)) {
                if (preview) {
                    item = getContentItem(section,
                                          remainingUrl,
                                          CMSDispatcher.PREVIEW);
                } else {
                    item = getContentItem(section,
                                          remainingUrl,
                                          "live");
                }
                if (item != null) {
                    request.setAttribute(CONTENT_ITEM, item);
                }
            }

            if (item != null) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Found item " + item + "; serving it");
                }

                DispatcherHelper.cacheDisable(response);
                preview(request, response, actx);
            } else {
                s_log.debug("No item to preview found; falling back to "
                                + "JSP dispatcher to look for some concrete "
                                + "resource in the file system");

                // If no resource was found, look for a JSP page.
                JSPApplicationDispatcher jsp = JSPApplicationDispatcher
                    .getInstance();
                //DispatcherHelper.cacheDisable(response);
                jsp.dispatch(request, response, actx);
            }

        }

    }

    public int chainedDispatch(HttpServletRequest request,
                               HttpServletResponse response,
                               RequestContext actx)
        throws IOException, ServletException {
        if (m_adminPagesOnly) {
            String url = actx.getRemainingURLPart();

            if (url.endsWith(ItemDispatcher.FILE_SUFFIX)) {
                url = url.substring(0, url.length() - ItemDispatcher.FILE_SUFFIX
                                    .length());
            } else if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }

            if (url.equals(ADMIN_URL)) {
                if (s_log.isInfoEnabled()) {
                    s_log.info("Resolving admin URL '" + url + "'");
                }

                dispatch(request, response, actx);

                return ChainedDispatcher.DISPATCH_BREAK;
            } else {
                return ChainedDispatcher.DISPATCH_CONTINUE;
            }
        }

        dispatch(request, response, actx);
        return ChainedDispatcher.DISPATCH_BREAK;
    }

    /**
     * Verify that the user is logged in and is able to view the page.
     * Subclasses can override this method if they need to, but should always be
     * sure to call super.checkUserAccess(...)
     *
     * @param request  The HTTP request
     * @param response The HTTP response
     * @param actx     The request context
     *
     * @exception AccessDeniedException if the user does not have access.
     *
     */
    protected void checkUserAccess(HttpServletRequest request,
                                   HttpServletResponse response,
                                   RequestContext actx)
        throws ServletException, AuthorizationException {

        final Shiro shiro = CdiUtil.createCdiUtil().findBean(Shiro.class);
        User user = shiro.getUser();
        final PermissionChecker permissionChecker = CdiUtil.createCdiUtil()
            .findBean(PermissionChecker.class);

        ContentSection section = getContentSection(request);

        if (isAdminPage(actx.getRemainingURLPart())) {

            // Handle admin page requests.
            // If the user is not logged in, redirect to the login page.
            // Otherwise, perform the Admin Pages access check.
            if (user == null) {
                redirectToLoginPage(request, response);
                return;
            }
            //if (!sm.canAccess(user, SecurityManager.ADMIN_PAGES)) {
            permissionChecker.checkPermission(ItemPrivileges.EDIT,
                                              section.getRootDocumentsFolder());
        } else {
            // For public page requests, use the SecurityManager to check access
            // SecurityManager.canAccess(user, SecurityManager.PUBLIC_PAGES) must
            permissionChecker.checkPermission(
                ItemPrivileges.VIEW_PUBLISHED,
                section.getRootDocumentsFolder());
        }
    }

    /**
     * Fetches the content section from the request attributes.
     *
     * @param request The HTTP request
     *
     * @return The content section
     *
     * @pre ( state != null )
     */
    public static ContentSection getContentSection(HttpServletRequest request) {
        return (ContentSection) request.getAttribute(CONTENT_SECTION);
    }

    /**
     * Fetches the content item from the request attributes.
     *
     * @param request The HTTP request
     *
     * @return The content item
     *
     * @pre ( state != null )
     */
    public static ContentItem getContentItem(HttpServletRequest request) {
        return (ContentItem) request.getAttribute(CONTENT_ITEM);
    }

    /**
     * Looks up the current content section using the remaining URL stored in
     * the request context object and the SiteNode class.
     *
     * @param url The section URL stub
     *
     * @return The current Content Section
     */
    protected ContentSection findContentSection(String url) {

        // MP: This is a hack to get the debugging info in
        //     SiteNodePresentationManager.servePage, but since it's
        //     debugging info...
        // Remove /debug from the start of the URL if it exists.
        if (url.startsWith(DEBUG)) {
            url = url.substring(DEBUG.length());
        }

        final String debugXMLString = "/xml";
        if (url.startsWith(debugXMLString)) {
            url = url.substring(debugXMLString.length());
        }

        final String debugXSLString = "/xsl";
        if (url.startsWith(debugXSLString)) {
            url = url.substring(debugXSLString.length());
        }

        // Fetch the current site node from the URL.
        final ContentSectionRepository sectionRepo = CdiUtil.createCdiUtil()
            .findBean(ContentSectionRepository.class);
        ContentSection section = sectionRepo.findByLabel(url);
        return section;
    }

    /**
     * Fetch a resource based on the URL stub.
     *
     * @param section The current content section
     * @param url     The section-relative URL
     *
     * @return A ResourceHandler resource or null if none exists.
     *
     * @pre (url != null)
     */
    protected ResourceHandler getResource(ContentSection section, String url)
        throws ServletException {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Searching for a resource for the URL fragment '" + url
                            + "' under " + section);
        }

        final PageResolver pageResolver = CMSDispatcher.getPageResolver(section);

        final ResourceHandler handler = pageResolver.getPage(url);

        return handler;
    }

    /**
     * Lookup a content item by section and URL.
     *
     * @param section The content section
     * @param url     The URL relative to the content section
     * @param context The use context
     *
     * @return The item associated with the URL, or null if no such item exists
     */
    protected ContentItem getContentItem(ContentSection section, String url,
                                         String context)
        throws ServletException {

        ItemResolver itemResolver = CMSDispatcher.getItemResolver(section);

        ContentItem item = null;
        item = itemResolver.getItem(section, url, context);

        return item;
    }

    /**
     * Preview the current content item.
     *
     * @param request  The HTTP request
     * @param response The HTTP response
     * @param actx     The request context
     */
    protected void preview(HttpServletRequest request,
                           HttpServletResponse response,
                           RequestContext actx)
        throws IOException, ServletException {

        ContentSection section = getContentSection(request);
        ContentItem item = getContentItem(request);

        ItemResolver itemResolver = CMSDispatcher.getItemResolver(section);
        CMSPage page = itemResolver.getMasterPage(item, request);
        page.dispatch(request, response, actx);
    }

    /**
     * Flushes the page resolver cache.
     *
     * @param section The current content section
     * @param url     The section-relative URL
     */
    public static void releaseResource(ContentSection section, String url) {
        final String pageResolverClassName = section.getPageResolverClass();
        final PageResolver pageResolver;
        try {
            pageResolver = (PageResolver) Class.forName(pageResolverClassName)
                .newInstance();
        } catch (ClassNotFoundException |
                 IllegalAccessException |
                 InstantiationException ex) {
            throw new RuntimeException(ex);
        }
        pageResolver.releasePage(url);
    }

    /**
     * Fetches the PageResolver for a content section. Checks cache first.
     *
     * @param section The content section
     *
     * @return The PageResolver associated with the content section
     */
    public static PageResolver getPageResolver(ContentSection section) {
        s_log.debug("Getting the page resolver");

        final String name = section.getLabel();
        PageResolver pr = (PageResolver) s_pageResolverCache.get(name);

        if (pr == null) {
            s_log.debug("The page resolver was not cached; fetching a new "
                            + "one and placing it in the cache");

            final String pageResolverClassName = section.getPageResolverClass();
            final PageResolver pageResolver;
            try {
                pageResolver = (PageResolver) Class.forName(
                    pageResolverClassName)
                    .newInstance();
            } catch (ClassNotFoundException |
                     IllegalAccessException |
                     InstantiationException ex) {
                throw new RuntimeException(ex);
            }
            s_pageResolverCache.put(name, pageResolver);
        }

        return pr;
    }

    /**
     * Fetches the ItemResolver for a content section. Checks cache first.
     *
     * @param section The content section
     *
     * @return The ItemResolver associated with the content section
     */
    public static ItemResolver getItemResolver(ContentSection section) {
        String name = section.getLabel();
        ItemResolver itemResolver = (ItemResolver) s_itemResolverCache.get(name);
        if (itemResolver == null) {
            final String itemResolverClassName = section.getItemResolverClass();
            try {
                itemResolver = (ItemResolver) Class.forName(
                    itemResolverClassName).newInstance();
            } catch (ClassNotFoundException |
                     IllegalAccessException |
                     InstantiationException ex) {
                throw new RuntimeException(ex);
            }
            s_itemResolverCache.put(name, itemResolver);
        }
        return itemResolver;
    }

    /**
     * Fetches the XMLGenerator for a content section. Checks cache first.
     *
     * @param section The content section
     *
     * @return The XMLGenerator associated with the content section
     */
    public static XMLGenerator getXMLGenerator(ContentSection section) {
        String name = section.getLabel();
        XMLGenerator xmlGenerator = (XMLGenerator) s_xmlGeneratorCache.get(name);
        if (xmlGenerator == null) {
            final String xmlGeneratorClassName = section.getXmlGeneratorClass();
            try {
                xmlGenerator = (XMLGenerator) Class.forName(
                    xmlGeneratorClassName).newInstance();
            } catch (ClassNotFoundException |
                     IllegalAccessException |
                     InstantiationException ex) {
                throw new RuntimeException(ex);
            }
            s_xmlGeneratorCache.put(name, xmlGenerator);
        }

        return xmlGenerator;
    }

    /**
     * Does this URL correspond to an admin page?
     */
    protected boolean isAdminPage(String url) {

        // MP: This is a hack to get the debugging info in
        //     SiteNodePresentationManager.servePage, but since it's
        //     debugging info...
        // Remove /debug from the start of the URL if it exists.
        if (url.startsWith(DEBUG)) {
            url = url.substring(DEBUG.length());
        }

        return (url != null && (url.startsWith(ADMIN_SECTION) || url.startsWith(
                                PREVIEW)));
    }

    /**
     * Redirects the client to the login page, setting the return url to the
     * current request URI.
     *
     * @exception ServletException If there is an exception thrown while trying
     *                             to redirect, wrap that exception in a
     *                             ServletException
     *
     */
    protected void redirectToLoginPage(HttpServletRequest req,
                                       HttpServletResponse resp)
        throws ServletException {
        throw new LoginSignal(req);
    }

    // modified from JSPApplicationDispatcher
    private String getSiteNodeAsset(HttpServletRequest request,
                                    RequestContext actx)
        throws RedirectException {

        String siteNodeAssetURL = null;

        ServletContext sctx = actx.getServletContext();
        String processedURL = actx.getProcessedURLPart();
        String remainingURL = actx.getRemainingURLPart();
        // REMOVE THIS HACK ONCE we have working publish to file code in the build
        //String templateRoot = PublishToFile.getDefaultDestinationForType(Template.class);
        String templateRoot = null;

        /* Allow a graceful early exit if publishToFile is not initialized */
        if (null == templateRoot) {
            return null;
        }

        File siteNodeAssetRoot = new File(templateRoot, processedURL);
        File assetFile = new File(siteNodeAssetRoot, remainingURL);

        String contextPath = request.getContextPath();

        if (assetFile.isDirectory()) {

            if (remainingURL.endsWith("/")) {
                throw new RedirectException(actx.getOriginalURL() + "/");
            }

            for (int i = 0; i < INDEX_FILES.length; i++) {
                File indexFile = new File(assetFile, INDEX_FILES[i]);
                if (indexFile.exists()) {
                    assetFile = indexFile;
                }
            }
        }

        if (assetFile.exists()) {
            siteNodeAssetURL = contextPath + "/" + templateRoot
                                   + processedURL + remainingURL;
        }

        return siteNodeAssetURL;
    }

}
