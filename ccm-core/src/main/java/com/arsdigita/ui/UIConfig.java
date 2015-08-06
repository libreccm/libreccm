/*
 * Copyright (C) 2010 pboy (pboy@barkhof.uni-bremen.de) All Rights Reserved.
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

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringArrayParameter;
import com.arsdigita.util.parameter.StringParameter;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * A configuration record for configuration of the core UI package
 * (layout of main UI components).
 *
 * Accessors of this class may return null. Developers should take care
 * to trap null return values in their code.
 *
 * @author Peter Boy &lt;pboy@barkhof.uni-bremen.de&gt;
 * @version $Id: $
 */
public class UIConfig extends AbstractConfig {

    /** A logger instance.  */
    private static final Logger s_log = Logger.getLogger(UIConfig.class);

    /** Singelton config object.  */
    private static UIConfig s_conf;

    /**
     * Gain a UIConfig object.
     *
     * Singelton pattern, don't instantiate a lifecacle object using the
     * constructor directly!
     * @return
     */
    public static synchronized UIConfig getConfig() {
        if (s_conf == null) {
            s_conf = new UIConfig();
            s_conf.load();
        }

        return s_conf;
    }

    /**
     * Default set of page component objects defining the default layout for the
     * <strong>SimplePage</strong> class.
     *
     * Format expected by clients:
     * list  { { "margin_position", "class_name_of_bebop_component"} ,
     *         { "margin_position", "class_name_of_bebop_component"} ,
     *         ...
     *         { "margin_position", "class_name_of_bebop_component"} 
     *        }
     * Elements are optional and may have 0 ...n pairs of position/classname.
     *
     * Example: 
     *  defaultLayout = { { "top", "com.arsdigita.ui.UserBanner" },
     *                    { "bottom", "com.arsdigita.ui.SiteBanner" },
     *                    { "bottom", "com.arsdigita.ui.DebugPanel" }
     *                    { "left", "com.arsdigita.x.y.z" },
     *                    { "right", "com.arsdigita.x.y.z" }
     *                  };
     *
     * Currently there is no list parameter model available. We use a
     * StringArrayParameter instead, where each String element contains a
     * colon separated position:class entry. It is converted to a list by
     * the getter method.
     */
    // Quick 'md Dirty, we reeally need a StringListParameter class
    private final Parameter m_defaultLayout = 
            new StringArrayParameter(
                    "core.ui.default_layout",
                    Parameter.REQUIRED,
                    new String[]
                        { "top:com.arsdigita.ui.UserBanner"
                         ,"bottom:com.arsdigita.ui.SiteBanner"
                         ,"bottom:com.arsdigita.ui.DebugPanel"
                      //,"left:com.arsdigita.x.y.zl",
                      //,"right:com.arsdigita.x.y.zr",
                        }
                );

    /**
     * The customized layout for applications using the SimplePage class
     * Format: list presumably same format as m_defaultLayout. details unknown
     * 
     * According to a comment in old enterprise.init file:
     * //  Application specific page components
     * //  applicationLayouts = {
     * //    { "forums",
     * //      {
     * //        { "top", "com.arsdigita.x.y.z" },
     * //        { "left", "com.arsdigita.x.y.z" },
     * //        { "bottom", "com.arsdigita.x.y.z" },
     * //        { "right", "com.arsdigita.x.y.z" }
     * //      }
     * //    },
     * //    { "search",
     * //      {
     * //        { "top", "com.arsdigita.x.y.z" },
     * //        { "left", "com.arsdigita.x.y.z" },
     * //        { "bottom", "com.arsdigita.x.y.z" },
     * //        { "right", "com.arsdigita.x.y.z" }
     * //      }
     * //    }
     * //  };
     *
     */
    private final Parameter m_applicationLayouts = 
            new StringArrayParameter(
                    "core.ui.application_layouts",
                    Parameter.OPTIONAL,
                    null
                );

    /** String containing the relative URL for the top level page
     *  (or entry page / home page) of the site, Without leading slash but
     *  with trailing slash in case of a directory.
     *  By default it is the login page, but usually the root page of the
     *  main presentation application, e.g. portal, navigation, forum, etc.   */
    private final Parameter m_rootPageURL = 
            new StringParameter("core.ui.pagemap.root_page_url", 
                                Parameter.REQUIRED, "/register/");

    /** String containing the URL of a page, a servlet or a jsp, to which a 
     *  user after login will be redirected to. 
     *  In case of a jsp or servlet it may contain user specific logic to
     *  redirect to a page specific for the user or a group of users. 
     *  By default it is the /permissions/ page, but usually it is an
     *  application like personal-portal or content-center.                   */
    private final Parameter m_userRedirectURL  = 
            new StringParameter("core.ui.pagemap.user_redirect_url", 
                                Parameter.REQUIRED, "/permissions/");
    
    /** String containing the URL for the workspace of the site.              */
    // Old initializer: waf.pagemap.workspace
    // XXX  url pvt seems not to exist anymore! (pboy 2011-02-03)
    private final Parameter m_workspaceURL  = new StringParameter
        ("core.ui.pagemap.workspace_url", Parameter.REQUIRED, "pvt/");

    /**
     * Constructs an empty RuntimeConfig object.
     *
     */
    public UIConfig() {
    // pboy: According to the comment for the getConfig() method a singleton
    // pattern is to be used. Therefore the constructor must be changed to
    // private!
    // private UIConfig() {
        register(m_defaultLayout);
        register(m_applicationLayouts);

        register(m_rootPageURL);
        register(m_userRedirectURL);
        register(m_workspaceURL);

        loadInfo();

    }

    /**
     * Retrieve the set of default page component objects defining
     * the default layout for SimplePage class.
     */
    public List getDefaultLayout() {

        /** List contain the default layout used to create a SimplePage. */
        ArrayList defaultLayout = new ArrayList();
        /** Value of the defaultLayout parameter, a string array of
            pair of position:class strings                               */
        String[] layoutParameter = (String[]) get(m_defaultLayout) ;

        for (int i = 0; i < layoutParameter.length ; ++i) {
            String[] layoutSection = StringUtils.split(layoutParameter[i],':');
            defaultLayout.add(Arrays.asList(layoutSection));
        }
        return defaultLayout;
    }

    /**
     * Retrieve the set of customized layout for applications using the
     * SimplePage class.
     * Parameter is optional, method may return null!
     */
    public List getApplicationLayouts() {

        /** Value of the customLayout parameter, a string array of
            pair of position:class strings                               */
        String[] customParameter = (String[]) get(m_applicationLayouts) ;

        if (customParameter != null) {
            // This part of method could NOT be tested yet!
            /** List contain the application layout used to create a SimplePage. */
            ArrayList customLayout = new ArrayList();
            for (int i = 0; i < customParameter.length ; ++i) {
                String[] layoutSection = StringUtils.split(customParameter[i],':');
                customLayout.add(Arrays.asList(layoutSection));
            }
            return customLayout;
        } else {

            return null;

        }
    }

    /**
     * Retrieve the site's root page url - i.e. the front page, the 
     * (usually public) top level entry page for the site.
     * By default it is the login page, but usually the root page of the main
     * presentation application, e.g. portal, navigation, forum, etc. 
     * 
     * @return root page url
     */
    public String getRootPage() {
        String rootPageURL = (String)get(m_rootPageURL) ;
        // Previous configurations required NO leading slash. Just in case an
        // old configuration is in place we have to translate.
        return ( rootPageURL.startsWith("/") ? 
                 rootPageURL : "/"+rootPageURL );
    }

    /**
     * Retrieve systems user login redirect page url, that is the page, a
     * servlet oder a JSP ti which a user is redirected to after login.
     * By default it is the /permissions/ page, but usually it is an application
     * like personal-portal or content-center.
     *
     * @return user login redirect page url
     */
    public String getUserRedirect() {
        String userRedirectURL = (String)get(m_userRedirectURL);
        // Previous configurations required NO leading slash. Just in case an
        // old configuration is in place we have to translate.
        return ( userRedirectURL.startsWith("/") ? 
                 userRedirectURL : "/"+userRedirectURL );
    }

    /**
     * Retrieve systems workspace url.
     *
     * @return workspace page url
     */
    public String getWorkspace() {
        String workspaceURL = (String)get(m_workspaceURL);
        // Previous configurations required NO leading slash. Just in case an
        // old configuration is in place we have to translate.
        return ( workspaceURL.startsWith("/") ? 
                 workspaceURL : "/"+workspaceURL );
    }

}
