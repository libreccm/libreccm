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
import com.arsdigita.web.ApplicationFileResolver;
import com.arsdigita.web.BaseApplicationServlet;
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
import org.libreccm.web.CcmApplication;
import org.librecms.CmsConstants;

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

    //ToDo: private final ContentItemDispatcher m_disp = new ContentItemDispatcher();
    public static Map s_itemResolverCache = Collections
            .synchronizedMap(new HashMap());
    private static Map s_itemURLCacheMap = null;
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
//        if (resolverName == null) {
//            m_resolver = Web.getConfig().getApplicationFileResolver();
//        } else {
//            m_resolver = (ApplicationFileResolver) Classes.newInstance(
//                    resolverName);
//        }
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
     * @param page Page object to display
     */
    private void addPage(final String pathInfo, final Page page) {
        m_pages.put(pathInfo, page);
    }

    /**
     * Implementation of parent's (abstract) doService method checks HTTP
     * request to determine whether to handle a content item or other stuff
     * which is delegated to jsp templates.
     * {
     *
     * @see com.arsdigita.web.BaseApplicationServlet#doService
     * (HttpServletRequest, HttpServletResponse, Application)}
     *
     * @param request
     * @param response
     * @param app
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    protected void doService(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final CcmApplication app)
            throws ServletException, IOException {

        //ToDo
        throw new UnsupportedOperationException();
    }

}
