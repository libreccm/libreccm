/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.bebop;

import com.arsdigita.util.UncheckedWrapperException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * <p>The <code>PageFactory</code> provides a framework for
 * instantiating instances of the Bebop <code>Page</code> class in a
 * project- and application-independant manner.</p>
 *
 * <h3>History</h3>
 *
 * <p>The traditional approach to writing bebop applications is to
 * subclass the com.arsdigita.bebop.Page, adding various components to
 * provide navigation & layout and writing custom XSL for styling
 * purposes.</p>
 *
 * <p>The problem of this approach is that when multiple applications
 * are combined to form a complete site, is it difficult to produce an
 * integrated navigation infrastructure & uniform styling across all
 * pages. In addition, by placing application specific functionality
 * in a subclass of Page, the ability to reuse & embed applications
 * within each other is hampered since Page objects cannot be
 * nested.</p>
 *
 * <h3>Use Case</h3>
 *
 * <p>The PageFactory addresses the following situations</p>
 *
 * <ol>
 *   <li>It is common for all pages on a site to have a particular
 *   structure. ie, header, footer, left sidebar & main content area. </li>
 *
 *   <li>It is desirable to customize page structure without making code changes
 *   to individual applications. </li>
 * </ol>
 *
 * <h3>Example Usage</h3>
 *
 * <p>The point of interaction for PageFactory is typically in the
 * application's Dispatcher class. Rather than invoking the Page
 * constructor (ie <code>new Page(title)</code>), applications call
 * <code>PageFactory.buildPage(title, "appname")</code> This method
 * will return an instance of Page with the currently configured
 * navigation components added.</p>
 *
 * <p>The two compulsory arguments to the <code>buildPage</code>
 * method are the name of the application (ie, 'forum', 'cms'), and
 * the page title (either as a String or Label object).  The
 * application name is used as key in both the enterprise.init file
 * configuration & the XSL templates. There is optionally a third
 * string <code>id</code> parameter which provides a unique identifier
 * for the page within the application. If the page class is a
 * subclass of ApplicationPage this will be used to set the XML
 * <code>id</code> attribute.</p>
 *
 * <p>Consider the following example (based loosely on the Simple
 * Survey application):</p>
 *
 * <pre>
 * package com.arsdigita.simplesurvey.dispatcher;
 *
 * import com.arsdigita.simplesurvey.ui.IndexPanel;
 * import com.arsdigita.simplesurvey.ui.AdminPanel;
 * import com.arsdigita.simplesurvey.ui.SurveySelectionModel;
 * import com.arsdigita.bebop.BigDecimalParameter;
 * import com.arsdigita.bebop.page.BebopMapDispatcher;
 *
 * public class Dispatcher extends BebopMapDispatcher {
 *
 *   public Dispatcher() {
 *     Page index = buildIndexPage();
 *     Page admin = buildAdminPage();
 *
 *
 *     addPage("index.jsp",index);
 *     addPage("", index);
 *     addPage("admin/index.jsp",index);
 *     addPage("admin", index);
 *   }
 *
 *   private Page buildIndexPage() {
 *     SurveySelectionModel survey =
 *        new SurveySelectionModel(new BigDecimalParameter("survey"));
 *
 *     Page page = PageFactory.buildPage("simplesurvey",
 *                                       "Simple Survey",
 *                                       "index");
 *     page.add(IndexPanel(survey));
 *     page.addGlobalStateParam(survey.getStateParameter());
 *     page.lock();
 *     return page;
 *   }
 *
 *   private Page buildAdminPage() {
 *     SurveySelectionModel survey =
 *        new SurveySelectionModel(new BigDecimalParameter("survey"));
 *
 *     Page page = PageFactory.buildPage("simplesurvey",
 *                                       "Simple Survey Administration",
 *                                       "admin");
 *     page.add(AdminPanel(survey));
 *     page.addGlobalStateParam(survey.getStateParameter());
 *     page.lock();
 *     return page;
 *   }
 *
 * }
 * </pre>
 *
 * <h3>Updating existing applications</h3>
 *
 * <p>The process of updating existing applications to make use of the
 * PageFactory varies according to the complexity of the application
 * in question.</p>
 *
 * <p>In the simplest case where an application has not subclassed the
 * Page class, then it is just a case of replacing calls to <code>new
 * Page(title)</code> with <code>PageFactory.buildPage("appname",
 * title)</code>.</p>
 *
 * <p>When an application has subclassed Page, then the initial
 * approach is to change the subclass in question so that it derives
 * from SimpleContainer instead of Page. Any calls to the
 * <code>addGlobalStateParam</code> or <code>addRequestListener</code>
 * methods can be moved from the constructor into the
 * <code>register</code> method where there will be direct access to
 * the <code>Page</code> object.</p>
 *
 * <h3>Configuring the page factory</h3>
 *
 * <p>The <code>com.arsdigita.bebop.base_page</code> system property
 * may be used to specify the full name of a subclass of Page which
 * provides the constructor signature detailed in the
 * <code>setPageClass</code> method. If omitted it defaults to
 * <code>BasePage</code>.</p>
 *
 * <p>The <code>com.arsdigita.ui</code> package provides an
 * alternative subclass called <code>SimplePage</code> which supports
 * specification.</p>
 *
 * @see com.arsdigita.bebop.BasePage
 */
public class PageFactory {

    private static Class s_page;
    private static Constructor s_cons;
    private static boolean initialized = false;

    static void init() {
        if (initialized) {
            return;
        }
        setPageClass(Bebop.getConfig().getBasePageClass());
    }

    /**
     * Sets the page class to instantiate. The
     * class should have a public constructor that
     * takes three arguments. The first 'String'
     * argument is the name of the application, the
     * second 'Label' is the page title, and the
     * third (optionally null) argument is a page
     * id (unique string with this application).
     *
     * The common case is to inherit from ApplicationPage
     * and pass these three arguments straight through
     * to its constructor.
     *
     * @param page the page class
     */
    public static void setPageClass(Class page) {
        try {
            s_cons = page.getConstructor(new Class[] {
                String.class, Label.class, String.class
            });
            s_page = page;
            initialized = true;
        } catch (NoSuchMethodException ex) {
            throw new UncheckedWrapperException(
                "cannot find constructor " + s_page.getName() +
                "(String application, Label title, String id)",
                ex
            );
        } catch (SecurityException ex) {
            throw new UncheckedWrapperException(
                "cannot retrieve constructor for " + s_page.getName(),
                ex
            );
        }
    }

    /**
     * Instantiates a new instance of the Page class.
     *
     * @param application the application name
     * @param title the page title
     * @return a subclass of com.arsdigita.bebop.Page
     */
    public static Page buildPage(String application,
                                 String title) {
        return buildPage(application, new Label(title));
    }

    /**
     * Instantiates a new instance of the Page class,
     * with the optional unique page id string.
     *
     * @param application the application name
     * @param title the page title
     * @param id the page id within the application
     * @return a subclass of com.arsdigita.bebop.Page
     */
    public static Page buildPage(String application,
                                 String title,
                                 String id) {
        return buildPage(application, new Label(title), id);
    }

    /**
     * Instantiates a new instance of the Page class.
     *
     * @param application the application name
     * @param title the label for the page title
     * @return a subclass of com.arsdigita.bebop.Page
     */
    public static Page buildPage(String application,
                                 Label title) {
        return buildPage(application, title, null);
    }

    /**
     * Instantiates a new instance of the Page class,
     * with the optional unique page id string.
     *
     * @param application the application name
     * @param title the label for the page title
     * @param id the page id within the application
     * @return a subclass of com.arsdigita.bebop.Page
     */
    public static Page buildPage(String application,
                                 Label title,
                                 String id) {
		init();
        Page page = null;
        try {
            page = (Page)s_cons.newInstance(new Object[] {
                application, title, id
            });
        } catch (InstantiationException ex) {
            throw new UncheckedWrapperException(
                "cannot instantiate page class " + s_page.getName(), ex
            );
        } catch (IllegalArgumentException ex) {
            throw new UncheckedWrapperException(
                "cannot instantiate page class " + s_page.getName(), ex
            );
        } catch (IllegalAccessException ex) {
            throw new UncheckedWrapperException(
                "constructor for page class " + s_page.getName() +
                " is not public", ex
            );
        } catch (InvocationTargetException ex) {
            throw new UncheckedWrapperException(
                "cannot instantiate page class " + s_page.getName(), ex
            );
        }

        return page;
    }

}
