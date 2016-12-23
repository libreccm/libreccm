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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.TabbedPane;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.BooleanParameter;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.dispatcher.CMSPage;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.web.CcmApplication;

import com.arsdigita.web.Web;
import com.arsdigita.xml.Document;

import org.arsdigita.cms.CMSConfig;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Shiro;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItemVersion;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * The Item Search page.</p>
 *
 * @author Scott Seago (scott@arsdigita.com)
 * @author Sören Bernstein <quasi@quasiweb.de>
 * @author Jens Pelzetter (jens@jp-digital.de)
 */
public class ItemSearchPage extends CMSPage {

    public static final String CONTENT_SECTION = "section_id";

    private static final boolean LIMIT_TO_CONTENT_SECTION = false;
    private final static String XSL_CLASS = "CMS Admin";

    private TabbedPane tabbedPane;
    private ItemSearchFlatBrowsePane flatBrowsePane;
    private ItemSearchBrowsePane browsePane;
    private ItemSearchPopup searchPopup;
//    private ItemSearchCreateItemPane m_create;
    private final LongParameter sectionId;
    private int lastTab;
    private static final CMSConfig CONFIG = CMSConfig.getConfig();

    /**
     * Construct a new ItemSearchPage
     */
    public ItemSearchPage() {
        super(new GlobalizedMessage("cms.ui.item_search.page_title",
                                    CmsConstants.CMS_BUNDLE).localize()
            .toString(),
              new SimpleContainer());

        setClassAttr("cms-admin");

        addGlobalStateParam(
            new BigDecimalParameter(ItemSearch.SINGLE_TYPE_PARAM));
        addGlobalStateParam(new StringParameter(ItemSearchPopup.WIDGET_PARAM));
        addGlobalStateParam(new StringParameter("searchWidget"));
        addGlobalStateParam(new StringParameter("publishWidget"));
        addGlobalStateParam(new StringParameter("defaultCreationFolder"));
        addGlobalStateParam(new IntegerParameter("lastTab"));
        addGlobalStateParam(new BooleanParameter("disableCreatePane"));
        addGlobalStateParam(new BooleanParameter("editAfterCreate"));
        addGlobalStateParam(new StringParameter("queryField"));
        sectionId = new LongParameter(CONTENT_SECTION);
        addGlobalStateParam(sectionId);

        flatBrowsePane = getFlatBrowsePane();
        browsePane = getBrowsePane();
        searchPopup = getSearchPane();
//        m_create = getCreatePane();

        tabbedPane = createTabbedPane();
        tabbedPane.setIdAttr("page-body");
        add(tabbedPane);

        addRequestListener(new RequestListener() {

            public void pageRequested(final RequestEvent event) {
                final PageState state = event.getPageState();

                final String query = (String) state.getValue(
                    new StringParameter(ItemSearchPopup.QUERY));
                final Boolean disableCreatePane = (Boolean) state.getValue(
                    new BooleanParameter("disableCreatePane"));

                BigDecimal typeParam = (BigDecimal) state.getValue(
                    new BigDecimalParameter(ItemSearch.SINGLE_TYPE_PARAM));
                if ((typeParam == null) || disableCreatePane) {
//                    tabbedPane.setTabVisible(state, m_create, false);
//                    m_create.setVisible(state, false);
                } else {
//                    tabbedPane.setTabVisible(state, m_create, true);
//                    m_create.setVisible(state, true);
                }

                if (state.getValue(new IntegerParameter("lastTab")) == null) {
                    if ((query == null) || query.isEmpty()) {
                        tabbedPane.setSelectedIndex(state, 1);
                    } else {
                        tabbedPane.setSelectedIndex(state, 0);
                    }

//                    m_tabbedPane.setTabVisible(state, m_create, false);
//                    m_create.setVisible(state, false);
                }

                state.setValue(new IntegerParameter("lastTab"), tabbedPane
                               .getSelectedIndex(state));

                if (state.getValue(new StringParameter("defaultCreationFolder"))
                        != null) {
//                    m_create.setDefaultFolder((String) state.getValue(
//                        new StringParameter("defaultCreationFolder")));
                }

                if (state.getValue(new BooleanParameter("editAfterCreate"))
                        != null) {
//                    m_create.setEditAfterCreate((Boolean) state.getValue(
//                        new BooleanParameter("editAfterCreate")));
                }

                if (state.getValue(new StringParameter("queryField")) == null) {
                    //Because of Bebops silly stateful behaviour we have to do this...
                    flatBrowsePane.resetQueryFields();
                } else {
                    flatBrowsePane.addQueryField((String) state.getValue(
                        new StringParameter("queryField")));
                }

//                if (m_lastTab != m_tabbedPane.getSelectedIndex(state)) {
//                    m_lastTab = m_tabbedPane.getSelectedIndex(state);                    
//                    return;
//                }
//
//                //If create pane is selected do nothing (else we don't stay in the create pane)
//                if (m_tabbedPane.getCurrentPane(state) == m_create) {
//                    return;
//                }
//
//                if ((query == null) || query.isEmpty()) {
//                    m_tabbedPane.setSelectedIndex(state, 1);
//                } else {
//                    m_tabbedPane.setSelectedIndex(state, 1);
//                }                
//                if (m_tabbedPane.getCurrentPane(state) == m_create) {
//                    m_tabbedPane.setTabVisible(state, m_create, false);
//                    m_create.setVisible(state, false);
//                }
//
//                m_lastTab = m_tabbedPane.getSelectedIndex(state);
            }

        });

//        m_tabbedPane.addActionListener(new ActionListener() {
//
//            public void actionPerformed(final ActionEvent event) {
//                final PageState state = event.getPageState();
//
//            }
//
//        });
//        m_flatBrowse.addProcessListener(new FormProcessListener() {
//
//            public void process(final FormSectionEvent fse) throws FormProcessException {
//                if (m_flatBrowse.getSubmit().isSelected(fse.getPageState())) {
//                    enableCreatePane(fse.getPageState());
//                }
//            }
//
//        });
    }  // END constructor

    /**
     * Creates, and then caches, the Browse pane.
     *
     * Overriding this method to return null will prevent this tab from
     * appearing. Note: not implemented yet.
     *
     * @return
     */
    protected ItemSearchBrowsePane getBrowsePane() {
        if (browsePane == null) {
            browsePane = new ItemSearchBrowsePane();
        }

        return browsePane;
    }

    protected ItemSearchFlatBrowsePane getFlatBrowsePane() {
        if (flatBrowsePane == null) {
            //m_flatBrowse = new ItemSearchFlatBrowsePane("flatBrowse");
            flatBrowsePane = new ItemSearchFlatBrowsePane();
        }

        return flatBrowsePane;
    }

    /**
     * Creates, and then caches, the Creation pane. Overriding this method to
     * return null will prevent this tab from appearing.
     */
    protected ItemSearchPopup getSearchPane() {
        if (searchPopup == null) {
            // Always search in every content section
//            m_search = new ItemSearchPopup(ContentItem.DRAFT, CMS.getConfig().limitToContentSection());
            searchPopup = new ItemSearchPopup(ContentItemVersion.DRAFT
                .toString(),
                                              LIMIT_TO_CONTENT_SECTION);
        }

        return searchPopup;
    }

//    protected ItemSearchCreateItemPane getCreatePane() {
//        if (m_create == null) {
//            m_create = new ItemSearchCreateItemPane(this);
//        }
//
//        return m_create;
//    }
    /**
     * Created the TabbedPane to use for this page.
     *
     * Sets the class attribute for this tabbed pane. The default implementation
     * uses a {@link com.arsdigita.bebop.TabbedPane} and sets the class
     * attribute to "CMS Admin." This implementation also adds tasks, content
     * sections, and search panes.
     *
     * Developers can override this method to add only the tabs they want, or to
     * add additional tabs after the default CMS tabs are added.
     *
     * @return
     */
    protected TabbedPane createTabbedPane() {
        TabbedPane pane = new TabbedPane();
        pane.setClassAttr(XSL_CLASS);

        addToPane(pane, "flatBrowse", getFlatBrowsePane());
        addToPane(pane, "browse", getBrowsePane());
        addToPane(pane, "search", getSearchPane());
//        addToPane(pane, "create", getCreatePane());

        if ("browse".equals(CONFIG.getItemSearchDefaultTab())) {
            pane.setDefaultPane(browsePane);
        }
        if ("search".equals(CONFIG.getItemSearchDefaultTab())) {
            pane.setDefaultPane(searchPopup);
        }

        //pane.setDefaultPane(m_flatBrowse);      
        pane.setDefaultPane(browsePane);

        return pane;
    }

    /**
     * Adds the specified component, with the specified tab name, to the tabbed
     * pane only if it is not null.
     *
     * @param pane    The pane to which to add the tab
     * @param tabName The name of the tab if it's added
     * @param comp    The component to add to the pane
     */
    protected void addToPane(final TabbedPane pane,
                             final String tabName,
                             final Component comp) {
        if (comp != null) {
            pane.addTab(new GlobalizedMessage(String.format(
                "cms.ui.item_search.%s", tabName), CmsConstants.CMS_BUNDLE)
                .localize().toString(),
                        comp);
        }
    }

    /**
     * This strange voodoo from Dan. No idea what it does.
     *
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void dispatch(final HttpServletRequest request,
                         final HttpServletResponse response,
                         final RequestContext actx) {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final Shiro shiro = cdiUtil.findBean(Shiro.class);

        shiro.getSystemUser().execute(new Runnable() {

            @Override
            public void run() {

                final CcmApplication application = Web.getWebContext()
                    .getApplication();

                final CdiUtil util = CdiUtil.createCdiUtil();
                final ContentSectionRepository sectionRepo = util.findBean(
                    ContentSectionRepository.class);

                final ContentSection section;
                if (application instanceof ContentSection) {
                    section = (ContentSection) application;
                } else {
                    section = sectionRepo.findById(Long.parseLong(
                        (String) sectionId.transformValue(request)));
                }
                
//                Web.getWebContext().setContentSection(section);
                
                final Document doc;
                try {
                    doc = buildDocument(request, response);
                } catch (ServletException ex) {
                    throw new UncheckedWrapperException(ex);
                }
                final PresentationManager pm = Templating
                    .getPresentationManager();

                pm.servePage(doc, request, response);
            }

        });
    }

    protected void setTabActive(final PageState state, final Component component,
                                final boolean value) {
        tabbedPane.setTabVisible(state, component, value);
    }

    protected void setTabActive(final PageState state, final int index,
                                final boolean value) {
        tabbedPane.setTabVisible(state, index, value);
    }

//    protected void setDefaultCreationFolder(final Folder folder) {
//        m_create.setDefaultFolder(folder.getOID().toString());
//    }
//
//    protected void setEditAfterCreate(final boolean editAfterCreate) {
//        m_create.setEditAfterCreate(editAfterCreate);
//    }

}
