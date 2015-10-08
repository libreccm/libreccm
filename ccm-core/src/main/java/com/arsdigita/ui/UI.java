/*
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

package com.arsdigita.ui;

import com.arsdigita.ui.login.LoginServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * <p>A central location for commonly used UI services and their accessories.</p>
 *
 *
 * @author pb
 */
public abstract class UI {

    /** Private loggin instance.  */
    private static final Logger s_log = Logger.getLogger(UI.class);

    /** The UI XML namespace. */
    public static final String UI_XML_NS = "http://www.arsdigita.com/ui/1.0";

    private static final UIConfig s_config = UIConfig.getConfig();

    /** URL for systems public top level page (entry or start page).          */
    private static final String s_rootPageURL = s_config.getRootPage();
    /** URL to page a user should be redirected to after login.               */
    private static final String s_userRedirectURL = s_config.getUserRedirect();
    /** (Absolute) URL for workspace page.                                    */
    private static final String s_workspaceURL = s_config.getWorkspace();


    /**
     * Provides a handle to the UI config record.
     * 
     * @return Instance of UIConfig
     */
    public static UIConfig getConfig() {
        return s_config;
    }

    /**
     * Provides an absolute URL (leading slash) into the system top-level page
     * (entry page / start page). It is relative to document root without any 
     * constant prefix if there is one configured.
     * 
     * Method is typically called by servlets and especially by JSP's.
     * 
     * Currently just a wrapper script to getRootPageURL() because req is
     * currently ignored.
     *
     * @param req HttpServletRequest, may be used to determin the context of
     *            the current thread (application), currently not used and
     *            introduced here for backwards compatibility
     *
     * @return URL for top-level page as String
     */
    public static String getRootPageURL(HttpServletRequest req) {
        return getRootPageURL();
    }

    /**
     * Provides an absolute URL (leading slash) into the system top-level page
     * (entry page / start page). It is relative to document root without any 
     * constant prefix if there is one configured.
     * 
     * Method is typically called by servlets and especially by JSP's.
     *
     * @return URL for top-level page as String
     */
    public static String getRootPageURL() {
        return s_rootPageURL;
    }

    /**
     * Provides the absolute URL of a page, which redirects an incomming request
     * based on some clients property, usually whether the user is logged in,
     * either to a general public page or to a user (client) specific page.
     * 
     * It is relative to document root including leading slash but without any
     * constant prefix if there is one configured.
     *
     * It is typically used after login to redirect to an appropriate page, by
     * default to /peremissions/. A site should configure an application 
     * to their specific purposes. e.g ccm-cms provides a page 
     * content-center/redirect.jsp which redirects a user to the content-center 
     * if logged in.
     *
     * @return full URL of a user redirect page, may be null
     */
    public static String getUserRedirectURL() {
        return s_userRedirectURL;
    }

    /**
     * Wrapper method for {@see getUserRedirectURL()}
     * which redirects a user to the content-center if logged in.
     * 
     * Method is specifically used by various redirect JSP's.
     *
     * @param req HttpServletRequest, may be used to determin the context of
     *            the current thread (application), currently not used and
     *            introduced here for backwards compatibility
     * @return full URL of a user redirect page, may be null
     */
    public static String getUserRedirectURL(HttpServletRequest req) {
        return getUserRedirectURL();
    }

    /**
     * Provides the absolute URL for the system workspace page. It is relative
     * to document root including leading slash but without any constant prefix
     * if there is one configured.
     *
     * It is used by the user redirection page (see above) as redirection target
     * if no logged in user exists or no user specific page is configured. By
     * default it is configured as "pvt/" as well. An installation usually
     * defines a different page according to their specific purposes, e.g.
     * portal or navigation.
     *
     * @return URL for workspace page as String
     */
    public static String getWorkspaceURL() {
        return s_workspaceURL;
    }

    /**
     * Provides the absolute URL for the system workspace page.
     * It is relative to document root including leading slash but without any
     * constant prefix if there is one configured.
     * 
     * Method is typically called by servlets and especially by JSP's.
     * 
     * Currently just a wrapper script to getWorkspaceURL() because req is
     * currently ignored.
     *
     * @param req HttpServletRequest, may be used to determin the context of
     *            the current thread (application), currently not used and
     *            introduced here for backwards compatibility
     *
     * @return URL for workspace page as String
     */
    // In old LegacyInitializer
    // WORKSPACE_PAGE_KEY = page.kernel.workspace=pvt/ (mod- t0 portal/nav)
    public static String getWorkspaceURL(HttpServletRequest req) {
        return getWorkspaceURL();
    }

    
    // ////////////////////////////////////////////////////////////////////////
    // Various deprfecated methods, to be removed as soon as invoking code is
    // refactored.
    // ////////////////////////////////////////////////////////////////////////
    
    
    /**
     * Provides an absolute URL (leading slash) into the system login page.
     * It is relative to document root without any constant prefix if there is
     * one configured.
     *
     * @return URL for login page as String
     * @deprecated use Login.getLoginPageURL()() instead
     */
    public static String getLoginPageURL() {
        return LoginServlet.LOGIN_PAGE_URL;
    }

    /**
     * Provides an absolute URL (leading slash) for a cookie explanation page.
     * It is relative to document root without any constant prefix if there is
     * one configured.
     *
     * @return url String for new user registration page as String
     * @deprecated use LoginServlet.getCookiesExplainPageURL() instead
     */
    public static String getCookiesExplainPageURL() {
        return LoginServlet.getCookiesExplainPageURL();
    }

    /**
     * Provides an absolute URL (leading slash) for a password recovery page.
     * It is relative to document root without any constant prefix if there is
     * one configured.
     *
     * @return url String for new user registration page as String
     * @deprecated use LoginServlet.getRecoverPasswordPageURL() instead
     */
    public static String getRecoverPasswordPageURL() {
        return LoginServlet.getRecoverPasswordPageURL();
    }

    /**
     * Provides an absolute URL (leading slash) to a user profile editing page.
     * It is relative to document root without any constant prefix if there is
     * one configured.
     *
     * @return url String for new user registration page as String
     * @deprecated use LoginServlet.getEditUserProfilePageURL() instead
     */
    public static String getEditUserProfilePageURL() {
        return LoginServlet.getEditUserProfilePageURL();
    }

    /**
     * Provides an absolute URL (leading slash) for the system logout page. It
     * is relative to document root without any constant prefix if there is one
     * configured.
     *
     * @return URL for logout page as String
     * @deprecated use LoginServlet.getLogoutPageURL() instead
     */
    public static String getLogoutPageURL() {
        return LoginServlet.getLogoutPageURL();
    }

}
