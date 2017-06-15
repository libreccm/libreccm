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

import com.arsdigita.bebop.BebopConfig;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.page.PageTransformer;
import com.arsdigita.cms.CMS;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.Shiro;
import org.libreccm.security.User;
import org.libreccm.web.CcmApplication;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionServlet;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.util.Optional;


/**
 * <p>A <tt>CMSPage</tt> is a Bebop {@link com.arsdigita.bebop.Page}
 * implementation of the {@link com.arsdigita.cms.dispatcher.ResourceHandler}
 * interface.</p>
 *
 * <p>It stores the current {@link com.arsdigita.cms.ContentSection} and, if
 * applicable, the {@link com.arsdigita.cms.ContentItem} in the page state as
 * request local objects. Components that are part of the <tt>CMSPage</tt>
 * may access these objects by calling:</p>
 *     <blockquote><code><pre>
 *     getContentSection(PageState state);
 *     </pre></code></blockquote>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @author Uday Mathur (umathur@arsdigita.com)
 */
public class CMSPage extends Page implements ResourceHandler {

    private static final Logger LOGGER = LogManager.getLogger(CMSPage.class);

    /** The global assets URL stub XML parameter name.    */
    public final static String ASSETS = "ASSETS";

    /** The XML page class.     */
    public final static String PAGE_CLASS = "CMS";

    /** Map of XML parameters   */
    private HashMap m_params;

    /**    */
    private PageTransformer m_transformer;

    public CMSPage() {
        super();
        buildPage();
    }

    public CMSPage(String title) {
        super(title);
        buildPage();
    }

    public CMSPage(String title, Container panel) {
        super(title, panel);
        buildPage();
    }

    public CMSPage(Label title) {
        super(title);
        buildPage();
    }

    public CMSPage(Label title, Container panel) {
        super(title, panel);
        buildPage();
    }

    /**
     * Builds the page.
     */
    protected void buildPage() {
        // Set the class attribute value. May be overwritten by child class
        // to hold a more specific value
        setClassAttr(PAGE_CLASS);

        // Global XML params.
        // MP: This only works with older versions of Xalan.
        m_params = new HashMap();
        setXMLParameter(ASSETS, Utilities.getGlobalAssetsURL());

        // MP: This is a hack to so that the XML params work with the newer
        //     version of Xalan.
        // Sets attribute in SimpleComponent, attributes of the same name will
        // be overweritten.
        setAttribute(ASSETS, Utilities.getGlobalAssetsURL());

        // Make sure the error display gets rendered.
        getErrorDisplay().setIdAttr("page-body");

        final Class<PresentationManager> presenterClass = BebopConfig.getConfig().getPresenterClass();
        final PresentationManager pm;
        try {
            pm = presenterClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }

        if (pm instanceof PageTransformer) {
            m_transformer = (PageTransformer) pm;
        }
        else {
            m_transformer = new PageTransformer();
        }
    }

    /**
     * Finishes and locks the page. If the page is already locked, does nothing.
     *
     * This method is called by the {@link com.arsdigita.dispatcher.Dispatcher}
     * that initializes this page.
     */
    @Override
    public synchronized void init() {
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
     * @return The parameter value
     * @pre (name != null)
     */
    public String getXMLParameter(String name) {
        return (String) m_params.get(name);
    }

    /**
     * Set an XML parameter.
     *
     * @param name The parameter name
     * @param value The parameter value
     * @pre (name != null)
     */
    public void setXMLParameter(String name, String value) {
        m_params.put(name, value);
    }

    /**
     * Fetch the request-local content section.
     *
     * @param request The HTTP request
     * @return The current content section
     *
     * @deprecated use com.arsdigita.cms.CMS.getContext().getContentSection() 
     *             instead
     *             Despite of being deprecated it can not be removed because it
     *             is required by the interface Resourcehandler which is
     *             implemented by this class.
     *             On the other hand, if deprecated, implementing ResourceHandler
     *             may not be required
     */
    @Override
    public ContentSection getContentSection(HttpServletRequest request) {
        // Resets all content sections associations.
     // return ContentSectionDispatcher.getContentSection(request);
        return ContentSectionServlet.getContentSection(request);
    }

    /**
     * Fetch the request-local content section.
     *
     * @param state The page state
     * @return The current content section
     *
     * @deprecated use com.arsdigita.cms.CMS.getContext().getContentSection()
     *             instead
     *             Despite of being deprecated it can not be removed because it
     *             is required by ContentItemPage which extends CMSPage and
     *             uses this method.
     */
    public ContentSection getContentSection(PageState state) {
        return getContentSection(state.getRequest());
    }

    /**
     * Fetch the request-local content item.
     *
     * @param request The HTTP request
     * @return The current content item
     *
     * @deprecated use com.arsdigita.cms.CMS.getContext().getContentItem()
     *             instead
     *             Despite of being deprecated it can not be removed because it
     *             is required by the interface Resourcehandler which is
     *             implemented by this class.
     *             On the other hand, if deprecated, implementing ResourceHandler
     *             may not be required
     */
    public ContentItem getContentItem(HttpServletRequest request) {
        // resets all content item associations
        return ContentSectionDispatcher.getContentItem(request);
    }

    /**
     * Fetch the request-local content item.
     *
     * @param state The page state
     * @return The current content item
     * @deprecated use com.arsdigita.cms.CMS.getContext().getContentItem()
     *             instead.
     *             Despite of being deprecated it can not be removed because it
     *             is required by ContentItemPage which extends CMSPage and
     *             uses this method.
     */
    public ContentItem getContentItem(PageState state) {
        return getContentItem(state.getRequest());
    }

    /**
     * Services the Bebop page.
     *
     * @param request The servlet request object
     * @param response the servlet response object
     * @param actx The request context
     *
     * @pre m_transformer != null
     */
    @Override
    public void dispatch(final HttpServletRequest request,
                         final HttpServletResponse response ,
                         final RequestContext actx)
        throws IOException, ServletException {

        final CcmApplication app = Web.getWebContext().getApplication();
        ContentSection section = null;
        
        if (app == null) {
            //Nothing to do
        } else if(app instanceof ContentSection) {
            section = (ContentSection) app;
        } 
        
        if (section != null) {
            CMS.getContext().setContentSection(section);
        }
        
        final String itemId = request.getParameter("item_id");
        
        if (itemId != null) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ContentItemRepository itemRepo = cdiUtil.findBean(ContentItemRepository.class);
            final ContentItem item = itemRepo
                .findById(Long.parseLong(itemId)).get();
            final PermissionChecker permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);
            permissionChecker.checkPermission(ItemPrivileges.PREVIEW, 
                                              item);
            CMS.getContext().setContentItem(item);
        }
        
        final Document document = buildDocument(request, response);
        
        m_transformer.servePage(document, request, response);
    }

    /**
     * Overwrites bebop.Page#generateXMLHelper to add the name of the user
     * logged in to the page (displayed as part of the header).
     * @param ps
     * @param parent
     * @return 
     */
    @Override
    protected Element generateXMLHelper(PageState ps, Document parent) {
        Element page = super.generateXMLHelper(ps,parent);
        final Optional<User> user = CdiUtil.createCdiUtil().findBean(Shiro.class).getUser();
        if ( user.isPresent()) {
            page.addAttribute("name",user.get().getName());
        }

        return page;
    }
}
