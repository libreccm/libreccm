/*
 * Copyright (C) 2012 Peter Boy <pb@zes.uni-bremen.de> All Rights Reserved.
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
package com.arsdigita.ui.admin;

import com.arsdigita.ui.admin.usersgroupsroles.UsersGroupsRolesTab;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageFactory;
import com.arsdigita.bebop.TabbedPane;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.templating.Templating;
import com.arsdigita.ui.SiteBanner;
import com.arsdigita.ui.UserBanner;
import com.arsdigita.ui.admin.applications.ApplicationsTab;
import com.arsdigita.ui.admin.categories.CategoriesTab;
import com.arsdigita.ui.admin.configuration.ConfigurationTab;
import com.arsdigita.web.BaseApplicationServlet;
import com.arsdigita.web.LoginSignal;
import com.arsdigita.xml.Document;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.subject.Subject;
import org.libreccm.security.PermissionChecker;
import org.libreccm.web.CcmApplication;

import java.io.IOException;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.ServletContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 * Web Developer Support Application Servlet class, central entry point to
 * create and process the applications UI.
 *
 * We should have subclassed BebopApplicationServlet but couldn't overwrite
 * doService() method to add permission checking. So we use our own page
 * mapping. The general logic is the same as for BebopApplicationServlet.
 *
 * {
 *
 * @see com.arsdigita.bebop.page.BebopApplicationServlet}
 *
 * @author Jens Pelzetter
 * @author pb
 */
@WebServlet(urlPatterns = {ADMIN_SERVLET_PATH})
public class AdminServlet extends BaseApplicationServlet {

    private static final long serialVersionUID = -3912367600768871630L;

    private static final Logger LOGGER = LogManager
        .getLogger(AdminServlet.class);

    /**
     * Logger instance for debugging
     */
    //private static final Logger LOGGER = Logger.getLogger(AdminServlet.class.getName());
    /**
     * URL (pathinfo) -> Page object mapping. Based on it (and the http request
     * url) the doService method to selects a page to display
     */
//    private final Map<String, Page> pages = new HashMap<String, Page>();
    private Page adminPage;

    /**
     * User extension point, overwrite this method to setup a URL - page mapping
     *
     * @throws ServletException
     */
    @Override
    public void doInit() throws ServletException {
        //addPage("/", buildAdminIndexPage());     // index page at address ~/ds
        //  addPage("/index.jsp", buildIndexPage()); // index page at address ~/ds

        adminPage = PageFactory.buildPage("admin", "LibreCCM NG Admin");
//        adminPage.addGlobalStateParam(USER_ID_PARAM);
//        adminPage.addGlobalStateParam(GROUP_ID_PARAM);
//        adminPage.addGlobalStateParam(APPLICATIONS_ID_PARAM);

        adminPage.add(new UserBanner());
        adminPage.add(new SiteBanner());

        //Create tab bar
        final TabbedPane tabbedPane = new TabbedPane();
        tabbedPane.setIdAttr("page-body");

        tabbedPane.addTab(
            new Label(new GlobalizedMessage("ui.admin.tab.applications",
                                            ADMIN_BUNDLE)),
            new ApplicationsTab());

        tabbedPane.addTab(
            new Label(new GlobalizedMessage(
                "ui.admin.tab.users_groups_roles.title",
                ADMIN_BUNDLE)),
            new UsersGroupsRolesTab());

        tabbedPane.addTab(
            new Label(new GlobalizedMessage("ui.admin.tab.categories.title",
                                            ADMIN_BUNDLE)),
            new CategoriesTab());

        tabbedPane.addTab(
            new Label(new GlobalizedMessage("ui.admin.tab.configuration.title",
                                            ADMIN_BUNDLE)),
            new ConfigurationTab());

        tabbedPane.addTab(
            new Label(new GlobalizedMessage("ui.admin.tab.workflows.title",
                                            ADMIN_BUNDLE)),
            new WorkflowAdminTab());

        tabbedPane.addTab(
            new Label(new GlobalizedMessage("ui.admin.tab.sysinfo.title",
                                            ADMIN_BUNDLE)),
            new SystemInformationTab());
        
        final ServletContext servletContext = getServletContext();
        final String develMode = servletContext.getInitParameter("ccm.develmode");
        if (develMode != null && "true".equals(develMode.toLowerCase())) {
            
        }

        //page.add(new Label("admin"));
        adminPage.add(tabbedPane);

        adminPage.lock();

    }

    /**
     * Central service method, checks for required permission, determines the
     * requested page and passes the page object to PresentationManager.
     *
     * @param sreq
     * @param sresp
     * @param app
     *
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public final void doService(final HttpServletRequest sreq,
                                final HttpServletResponse sresp,
                                final CcmApplication app) throws
        ServletException, IOException {
        // ///////    Some preparational steps                   ///////////////
        /* Determine access privilege: only logged in users may access   */
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final Subject subject = cdiUtil.findBean(Subject.class);
        final PermissionChecker permissionChecker = cdiUtil.findBean(
            PermissionChecker.class);

        final ConfigurationManager confManager = CDI.current().select(
            ConfigurationManager.class).get();
        if (confManager == null) {
            throw new IllegalStateException();
        }

        LOGGER.debug("Checking if subject {} is authenticated...",
                     subject.toString());
        LOGGER.debug("Current session is: {}", sreq.getSession().getId());
        LOGGER.debug("Current Shiro session is {}",
                     subject.getSession().getId().toString());
        if (!subject.isAuthenticated()) {
            LOGGER.debug(
                "Subject {} is not authenticated, redirecting to login...",
                subject.toString());
            throw new LoginSignal(sreq);
        }

        /* Determine access privilege: Admin privileges must be granted     */
        LOGGER.debug(
            "Subject is loggedin, checking if subject has required permissions...");
        if (!permissionChecker.isPermitted("admin")) {
            LOGGER.debug("Subject does *not* have required permissions. "
                             + "Access denied.");
            throw new AccessDeniedException("User is not an administrator");
        }

        LOGGER.debug("Serving admin page...");
        /* Want admin to always show the latest stuff... */
        DispatcherHelper.cacheDisable(sresp);

        // ///////   Everything OK here - DO IT                  ///////////////
//        String pathInfo = sreq.getPathInfo();
//        Assert.exists(pathInfo, "String pathInfo");
//        if (pathInfo.length() > 1 && pathInfo.endsWith("/")) {
//            /* NOTE: ServletAPI specifies, pathInfo may be empty or will 
//             * start with a '/' character. It currently carries a 
//             * trailing '/' if a "virtual" page, i.e. not a real jsp, but 
//             * result of a servlet mapping. But Application requires url 
//             * NOT to end with a trailing '/' for legacy free applications.  */
//            pathInfo = pathInfo.substring(0, pathInfo.length() - 1);
//        }
//        final Page page = pages.get(pathInfo);
//        if (page == null) {
//            sresp.sendError(404, "No such page for path " + pathInfo);
//        } else {
//            final Document doc = page.buildDocument(sreq, sresp);
//            Templating.getPresentationManager().servePage(doc, sreq, sresp);
//        }
        final Document doc = adminPage.buildDocument(sreq, sresp);
        Templating.getPresentationManager().servePage(doc, sreq, sresp);
    }

    /**
     * Adds one pair of Url - Page to the internal hash map, used as a cache.
     *
     * @param pathInfo url stub for a page to display
     * @param page     Page object to display
     */
//    private void addPage(final String pathInfo, final Page page) {
//        Assert.exists(pathInfo, String.class);
//        Assert.exists(page, Page.class);
//        // Current Implementation requires pathInfo to start with a leading '/'
//        // SUN Servlet API specifies: "PathInfo *may be empty* or will start
//        // with a '/' character."
//        Assert.isTrue(pathInfo.charAt(0) == '/', "path starts not with '/'");
//
//        pages.put(pathInfo, page);
//    }
    /**
     * Index page for the admin section
     */
//    private Page buildAdminIndexPage() {
//
//        final Page page = PageFactory.buildPage("admin", PAGE_TITLE_LABEL);
//        page.addGlobalStateParam(USER_ID_PARAM);
//        page.addGlobalStateParam(GROUP_ID_PARAM);
//        page.addGlobalStateParam(APPLICATIONS_ID_PARAM);
//
//        /* 
//         * Create User split panel. 
//         * Note: Will change soon. 
//         */
//        //final AdminSplitPanel userSplitPanel = new AdminSplitPanel(USER_NAVBAR_TITLE);
////        final UserBrowsePane browsePane = new UserBrowsePane();
////        userSplitPanel.addTab(USER_TAB_SUMMARY, new UserSummaryPane(userSplitPanel, browsePane));
////        userSplitPanel.addTab(USER_TAB_BROWSE, browsePane);
////        userSplitPanel.addTab(USER_TAB_SEARCH, new UserSearchPane(userSplitPanel, browsePane));
////        userSplitPanel.addTab(USER_TAB_CREATE_USER, new CreateUserPane(userSplitPanel));
//        // Create the Admin's page tab bar
//        final TabbedPane tabbedPane = new TabbedPane();
//        tabbedPane.setIdAttr("page-body");
//
//        /**
//         * Create and add info tab
//         */
//        //tabbedPane.addTab(INFO_TAB_TITLE, new AdminInfoTab());        
//        /*
//         * Create and add the user and group tabs.
//         */
//        //tabbedPane.addTab(USER_TAB_TITLE, userSplitPanel);
//        final GroupAdministrationTab groupAdminTab
//                                         = new GroupAdministrationTab();
//        tabbedPane.addTab(USER_TAB_TITLE, new UserAdministrationTab(tabbedPane,
//                                                                    groupAdminTab));
//        tabbedPane.addTab(GROUP_TAB_TITLE, groupAdminTab);
//
//        /*
//         * Create application administration panel
//         */
//        tabbedPane.addTab(APPLICATIONS_TAB_TITLE,
//                          new ApplicationsAdministrationTab());
//
////        browsePane.setTabbedPane(tabbedPane);
////        browsePane.setGroupAdministrationTab(groupAdminTab);      
//        //Add System information tab
//        tabbedPane.addTab(SYSINFO_TAB_TITLE, new SystemInformationTab());
//
//        page.add(tabbedPane);
//        page.lock();
//
//        return page;
//    }
}
