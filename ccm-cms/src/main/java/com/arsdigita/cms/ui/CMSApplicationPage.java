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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.BebopConfig;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.page.PageTransformer;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Shiro;
import org.libreccm.security.User;
import org.libreccm.web.CcmApplication;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A <tt>CMSApplicationPage</tt> is a Bebop {@link com.arsdigita.bebop.Page}
 * implementation serving as a base for any CMS pageElement served by a servlet.
 *
 * It stores the current {@link com.arsdigita.cms.ContentSection} and, if
 * applicable, the {@link com.arsdigita.cms.ContentItem} in the pageElement
 * state as request local objects. Components that are part of the
 * <tt>CMSPage</tt> may access these objects by calling:
 * <blockquote><code><pre>
 *     getContentSection(PageState state);
 * </pre></code></blockquote>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @author Uday Mathur (umathur@arsdigita.com)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CMSApplicationPage extends Page {

    private static final Logger LOGGER = LogManager.getLogger(
        CMSApplicationPage.class);

    /**
     * The global assets URL stub XML parameter name.
     */
    public final static String ASSETS = "ASSETS";

    /**
     * The XML pageElement class.
     */
    public final static String PAGE_CLASS = "CMS";

    /**
     * Map of XML parameters
     */
    private Map<String, String> parameters;

    /**
     *
     */
    private PageTransformer pageTransformer;

    public CMSApplicationPage() {
        super();
        buildPage();
    }

    public CMSApplicationPage(final String title) {
        super(title);
        buildPage();
    }

    public CMSApplicationPage(final String title, final Container panel) {
        super(title, panel);
        buildPage();
    }

    public CMSApplicationPage(final Label title) {
        super(title);
        buildPage();
    }

    public CMSApplicationPage(final Label title, final Container panel) {
        super(title, panel);
        buildPage();
    }

    /**
     * Builds the pageElement.
     */
    private void buildPage() {

        // Set the class attribute value (down in SimpleComponent).
        setClassAttr(PAGE_CLASS);

        // Global XML params.
        // MP: This only works with older versions of Xalan.
        parameters = new HashMap<>();
        setXMLParameter(ASSETS, Utilities.getGlobalAssetsURL());

        // MP: This is a hack to so that the XML params work with the newer
        //     version of Xalan.
        setAttribute(ASSETS, Utilities.getGlobalAssetsURL());

        // Make sure the error display gets rendered.
        getErrorDisplay().setIdAttr("page-body");

        final Class<PresentationManager> presenterClass = BebopConfig
            .getConfig().getPresenterClass();
        final PresentationManager presenter;
        try {
            presenter = presenterClass.class.getDeclaredConstructor().newInstance();
        } catch (InstantiationException |
                 IllegalAccessException ex) {
            throw new RuntimeException("Failed to create PresentationManager",
                                       ex);
        }

        if (presenter instanceof PageTransformer) {
            pageTransformer = (PageTransformer) presenter;
        } else {
            pageTransformer = new PageTransformer();
        }
    }

    /**
     * Finishes and locks the pageElement. If the pageElement is already locked,
     * does nothing.
     *
     * Client classes may overwrite this method to add context specific bits to
     * the page before it is locked.
     *
     * This method is called by the various servlets serving the various pages
     * of the CMS package, before serving and displaying the page.
     *
     * @param request
     * @param response
     * @param app
     */
    public synchronized void init(final HttpServletRequest request,
                                  final HttpServletResponse response,
                                  final CcmApplication app) {
        LOGGER.debug("Initializing the page");

        if (!isLocked()) {
            LOGGER.debug("The page hasn't been locked; locking it now");

            lock();
        }
    }

    /**
     * Fetches the value of the XML parameter.
     *
     * @param name The parameter name
     *
     * @return The parameter value
     *
     * @pre (name != null)
     */
    public String getXMLParameter(final String name) {
        return parameters.get(name);
    }

    /**
     * Set an XML parameter.
     *
     * @param name  The parameter name
     * @param value The parameter value
     *
     * @pre (name != null)
     */
    public void setXMLParameter(String name, String value) {
        parameters.put(name, value);
    }
    
/**
     * Overwrites bebop.Page#generateXMLHelper to add the name of the user
     * logged in to the pageElement (displayed as part of the header).
     * @param state
     * @param parent
     * @return pageElement for use in generateXML
     */
    @Override
    protected Element generateXMLHelper(final PageState state, 
                                        final Document parent) {

        /* Retain elements already included.                                  */
        Element pageElement = super.generateXMLHelper(state,parent);

        /* Add name of user logged in.                                        */
        // Note: There are at least 2 ways in the API to determin the user
        // TODO: Check for differences, determin the best / recommended way and
        //       document it in the classes. Probably remove one ore the other
        //       way from the API if possible.
        final Shiro shiro = CdiUtil.createCdiUtil().findBean(Shiro.class);
        final Optional<User> user = shiro.getUser();
        // User user = Web.getWebContext().getUser();
        if (user.isPresent()) {
            pageElement.addAttribute("name",user.get().getName());
        }

        return pageElement;
    }

}
