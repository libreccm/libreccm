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
package com.arsdigita.cms;

import com.arsdigita.bebop.Page;
import com.arsdigita.cms.dispatcher.CMSPage;
import com.arsdigita.cms.ui.CMSApplicationPage;

import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.kernel.security.Util;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import com.arsdigita.ui.login.LoginHelper;
import com.arsdigita.web.ApplicationFileResolver;
import com.arsdigita.web.BaseApplicationServlet;
import com.arsdigita.web.LoginSignal;
import com.arsdigita.web.WebConfig;
import com.arsdigita.xml.Document;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authz.AuthorizationException;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.Shiro;
import org.libreccm.web.CcmApplication;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * CMS ContentCenter (content-center) application servlet serves all request
 * made within the Content Center application.
 *
 * @author Peter Boy <pboy@barkhof.uni-bremen.de>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@WebServlet(urlPatterns = "/content-center/*")
public class ContentCenterServlet extends BaseApplicationServlet {

    private static final long serialVersionUID = 16543266935651171L;

    /**
     * URL (pathinfo) -> Page object mapping. Based on it (and the http request
     * url) the doService method to selects a page to display
     */
    private final Map<String, Page> pages = new HashMap<>();

    /**
     * Path to directory containg ccm-cms template files
     */
    private String m_templatePath;
    /**
     * Resolvers to find templates (JSP) and other stuff stored in file system.
     */
    private ApplicationFileResolver m_resolver;

    private static final Logger LOGGER = LogManager.getLogger(
        ContentCenterServlet.class);

    /**
     * Use parent's class initialisation extension point to perform additional
     * initialisation tasks.
     */
    @Override
    protected void doInit() {
        LOGGER.info("starting doInit method");

        // NEW STUFF here used to process the pages in this servlet
        // Addresses previously noted in WEB-INF/resources/content-center-map.xml
        // Obviously not required.

//ToDo        
//        addPage("/", new MainPage());     // index page at address ~/cc
//        addPage("/index", new MainPage());
//ToDo End

// addPage("/item-search", new CMSItemSearchPage()); 
        //  Old style
        //addPage("/item-search", new ItemSearchPage());
        //addPage("/searchredirect", new CMSSearchResultRedirector());

        //  STUFF to use for JSP extension, i.e. jsp's to try for URLs which are not
        //  handled by the this servlet directly.
        /**
         * Set Template base path for JSP's
         */
        // ToDo: Make it configurable by an appropriate config registry entry!
        //        m_templatePath = CMS.getConfig().getTemplateRoot();
        m_templatePath = "/templates/ccm-cms/content-center";
        /**
         * Set TemplateResolver class
         */
        m_resolver = WebConfig.getConfig().getResolver();
    }

    @Override
    protected void doService(final HttpServletRequest sreq,
                             final HttpServletResponse sresp,
                             final CcmApplication app) throws ServletException,
                                                              IOException {
        LOGGER.info("starting doService method");

        //   ContentCenter workspace = (ContentCenter) app;

        /*       Check user and privilegies                                   */
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final Shiro shiro = cdiUtil.findBean(Shiro.class);
        if (shiro.getSubject().isAuthenticated()) {
            throw new LoginSignal(sreq);            // send to login page
        }
        final PermissionChecker permissionChecker = cdiUtil.findBean(
            PermissionChecker.class);
        final ContentSectionRepository sectionRepo = cdiUtil.findBean(
            ContentSectionRepository.class);
        final List<ContentSection> sections = sectionRepo.findAll();
        boolean hasAccess = false;
        for (final ContentSection section : sections) {
            if (permissionChecker.isPermitted(CmsConstants.PRIVILEGE_ITEMS_EDIT,
                                              section.getRootDocumentsFolder())) {
                hasAccess = true;
                break;
            }
        }

        if (!hasAccess) {    // user has no access privilege 
            throw new AuthorizationException(
                "User is not entitled to access any content section");
            // throw new LoginSignal(sreq);            // send to login page
        }

        // New way to fetch the page
        String pathInfo = sreq.getPathInfo();
        if (pathInfo.length() > 1 && pathInfo.endsWith("/")) {
            /* NOTE: ServletAPI specifies, pathInfo may be empty or will 
             * start with a '/' character. It currently carries a 
             * trailing '/' if a "virtual" page, i.e. not a real jsp, but 
             * result of a servlet mapping. But Application requires url 
             * NOT to end with a trailing '/' for legacy free applications.  */
            pathInfo = pathInfo.substring(0, pathInfo.length() - 1);
        }

        // An empty remaining URL or a URL which doesn't end in trailing slash:
        // probably want to redirect.
        // Probably DEPRECATED with new access method or only relevant for jsp
        // extension
        //  if (m_trailingSlashList.contains(url) && !originalUrl.endsWith("/")) {
        //      DispatcherHelper.sendRedirect(sresp, originalUrl + "/");
        //      return;
        //  }
        final Page page = (Page) pages.get(pathInfo);
        if (page != null) {

            // Check user access.
            checkUserAccess(sreq, sresp);

            if (page instanceof CMSPage) {
                // backwards compatibility fix until migration completed
                final CMSPage cmsPage = (CMSPage) page;
                final RequestContext ctx = DispatcherHelper.getRequestContext();
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

        } else {
            // Fall back on the JSP application dispatcher.
            // NOTE: The JSP must ensure the proper authentication and
            //       authorisation if required!
            LOGGER.info("NO page registered to serve the requst url.");

            RequestDispatcher rd = m_resolver.resolve(m_templatePath,
                                                      sreq, sresp, app);
            if (rd != null) {
                LOGGER.debug("Got dispatcher " + rd);

                final HttpServletRequest origreq = DispatcherHelper
                    .restoreOriginalRequest(sreq);
                rd.forward(origreq, sresp);
            } else {

                sresp.sendError(404, sreq.getRequestURI()
                                         + " not found on this server.");
            }

        }

        LOGGER.info("doService method completed");

    }    //  END doService()

    /**
     * Internal service mechod, adds one pair of Url - Page to the internal hash
     * map, used as a cache.
     *
     * @param pathInfo url stub for a page to display
     * @param page     Page object to display
     */
    private void addPage(final String pathInfo, final Page page) {

        // Current Implementation requires pathInfo to start with a leading '/'
        // SUN Servlet API specifies: "PathInfo *may be empty* or will start
        // with a '/' character."
        pages.put(pathInfo, page);

    }

//    /**
//     * Service Method returns the URL stub for the class name, can return null
//     * if not mapped
//     */
//    // Currently still in use by c.ad.cms.ui.ItemSearchWidget
//    public static String getURLStubForClass(String classname) {
//        LOGGER.debug("Getting URL Stub for : " + classname);
//        Iterator itr = s_pageURLs.keySet().iterator();
//        while (itr.hasNext()) {
//            String classname2 = (String) itr.next();
//            s_log.debug("key: " + classname + " value: "
//                            + (String) s_pageURLs.get(classname2));
//        }
//        String url = (String) s_pageURLs.get(classname);
//        return url;
//    }
    /**
     * Verify that the user is logged in and is able to view the page.
     * Subclasses can override this method if they need to, but should always be
     * sure to call super.checkUserAccess(...)
     *
     * @param request  The HTTP request
     * @param response The HTTP response
     * @param actx     The request context
     *
     */
    protected void checkUserAccess(final HttpServletRequest request,
                                   final HttpServletResponse response //,
    ///                                 final RequestContext actx
    )
        throws ServletException {

        if (CdiUtil.createCdiUtil().findBean(Shiro.class).getSubject()
            .isAuthenticated()) {
            throw new LoginSignal(request);
        }
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
        String url = Util.getSecurityHelper()
            .getLoginURL(req)
                         + "?" + LoginHelper.RETURN_URL_PARAM_NAME
                         + "=" + DispatcherHelper.encodeReturnURL(req);
        try {
            LoginHelper.sendRedirect(req, resp, url);
        } catch (IOException e) {
            LOGGER.error("IO Exception", e);
            throw new ServletException(e.getMessage(), e);
        }
    }


}
