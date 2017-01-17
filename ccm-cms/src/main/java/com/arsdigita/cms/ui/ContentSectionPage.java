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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.TabbedPane;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.PageLocations;

import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSection;

import com.arsdigita.cms.dispatcher.CMSPage;
//ToDo NG import com.arsdigita.cms.ui.category.CategoryAdminPane;
import com.arsdigita.cms.ui.cse.ContentSoonExpiredPane;
import com.arsdigita.cms.ui.folder.FolderAdminPane;
import com.arsdigita.cms.ui.lifecycle.LifecycleAdminPane;
//import com.arsdigita.cms.ui.role.RoleAdminPane;
import com.arsdigita.cms.ui.type.ContentTypeAdminPane;
import com.arsdigita.cms.ui.workflow.WorkflowAdminPane;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.util.Assert;

import javax.servlet.http.HttpServletRequest;

import org.arsdigita.cms.CMSConfig;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.librecms.CmsConstants;
import org.librecms.contentsection.privileges.AdminPrivileges;

/**
 * Contains the entire admin UI for a content section.
 *
 * Developers Note: It is based on the dispatcher model is is going to be
 * replaced by the newer servlet based model. @see
 * c.ad.cms.ui.contentsection.MainPage (currently not active).
 *
 * @author Jack Chung
 * @author Michael Pih
 * @author Xixi D'Moon
 * @author Justin Ross
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ContentSectionPage extends CMSPage implements ActionListener {

    /**
     * The URL parameter that can be passed in in order to set the current
     * folder. This is used in getting back to the correct level of folder
     * expansion from content item page.
     */
    public static final String SET_FOLDER = "set_folder";
    /**
     * The URL parameter that can be passed in in order to set the current
     * template (for setting the content type)
     */
    public static final String SET_TEMPLATE = "set_template";
    /**
     * The URL parameter that can be passed in in order to set the current tab.
     * This is a KLUDGE right now because the TabbedDialog's current tab is
     * selected with a local state parameter.
     */
    public static final String SET_TAB = "set_tab";
    /**
     * Index of the search tab
     */
    public static final int SEARCH_TAB = 0;
    /**
     * Index of the browse tab
     */
    public static final int BROWSE_TAB = 1;
    /**
     * Index of the roles tab
     */
    public static final int ROLES_TAB = 2;
    /**
     * Index of the workflows tab
     */
    public static final int WORKFLOW_TAB = 3;
    /**
     * Index of the lifecycles tab
     */
    public static final int LIFECYCLES_TAB = 4;
    /**
     * Index of the categories tab
     */
    public static final int CATEGORIES_TAB = 5;
    /**
     * Index of the content types tab
     */
    public static final int CONTENTTYPES_TAB = 6;
    public static final int USER_ADMIN_TAB = 7;

    private TabbedPane m_tabbedPane;
    private FolderAdminPane m_folderPane;
//ToDo NG    private BrowsePane m_browsePane;
//ToDo NG    private ItemSearch m_searchPane;
//ToDo NG    private ImagesPane m_imagesPane;
//ToDo NG    private RoleAdminPane m_rolePane;
    private WorkflowAdminPane m_workflowPane;
    private LifecycleAdminPane m_lifecyclePane;
//ToDo NG    private CategoryAdminPane m_categoryPane;
    private ContentTypeAdminPane m_typePane;
    //private LayoutPanel m_userAdminPane;
    private LayoutPanel m_csePane;
    private ReportPane m_reportPane;

    /**
     * Creates the content section index page containing - a Navigaton bar for
     * the various tasks (items, search, images, ....) - a breadcrumb - ....
     * Contains the UI for administering a content section.
     */
    public ContentSectionPage() {
        super(new Label(new TitlePrinter()), new SimpleContainer());

        setClassAttr("cms-admin");

        add(new GlobalNavigation());
        add(new ContentSectionContextBar());

        // Initialize the individual panes
        m_folderPane = getFolderAdminPane();
//ToDo NG        m_browsePane = getBrowsePane();
//ToDo NG        m_searchPane = getSearchPane();
//ToDo NG        m_imagesPane = getImagesPane();
//ToDo NG        m_rolePane = getRoleAdminPane();
        m_workflowPane = getWorkflowAdminPane();
        m_lifecyclePane = getLifecycleAdminPane();
//ToDo NG        m_categoryPane = getCategoryAdminPane();
        m_typePane = getContentTypeAdminPane();
        // userAdminPane removed, used to contain just one item (reset user
        // folder) which moved to the FolderAdminPane
        //m_userAdminPane = getUserAdminPane();
        m_csePane = getCSEPane();
        m_reportPane = getReportPane();

        // The panes
        m_tabbedPane = createTabbedPane();
        m_tabbedPane.setIdAttr("page-body");
        m_tabbedPane.addActionListener(this);
        add(m_tabbedPane);

        addActionListener(new ActionListener() {

            @Override
            public final void actionPerformed(ActionEvent e) {
                final PageState state = e.getPageState();

                final String tab = state.getRequest().getParameter(SET_TAB);

                if (tab != null) {
                    m_tabbedPane.setSelectedIndex(state, Integer.valueOf(tab)
                                                  .intValue());
                }

                final PermissionChecker permissionChecker = CdiUtil
                        .createCdiUtil().findBean(PermissionChecker.class);

                if (CMSConfig.getConfig().isHideAdminTabs()) {
                    m_tabbedPane.setTabVisible(
                            state,
                            m_workflowPane,
                            permissionChecker.isPermitted(
                                    AdminPrivileges.ADMINISTER_WORKFLOW));
//ToDo NG
//                    m_tabbedPane.setTabVisible(
//                        state, m_categoryPane,
//                        permissionChecker.isPermitted(
//                            AdminPrivileges.ADMINISTER_CATEGORIES));
                    m_tabbedPane.setTabVisible(
                            state,
                            m_lifecyclePane,
                            permissionChecker.isPermitted(
                                    AdminPrivileges.ADMINISTER_LIFECYLES));
                    m_tabbedPane.setTabVisible(
                            state,
                            m_typePane,
                            permissionChecker.isPermitted(
                                    AdminPrivileges.ADMINISTER_CONTENT_TYPES));
//ToDo NG
//m_tabbedPane.setTabVisible(
//                        state,
//                        m_rolePane,
//                        permissionChecker.isPermitted(
//                            AdminPrivileges.ADMINISTER_ROLES));
                    // csePane: should check permission
                    m_tabbedPane.setTabVisible(state, m_csePane, true);
                    // TODO Check for reportPane as well
                }
            }

        });
    }

    /**
     * Creates, and then caches, the browse pane. Overriding this method to
     * return null will prevent this tab from appearing.
     *
     * @return
     */
    protected FolderAdminPane getFolderAdminPane() {
        if (m_folderPane == null) {
            m_folderPane = new FolderAdminPane();
        }
        return m_folderPane;
    }

    /**
     * Creates, and then caches, the browse pane. Overriding this method to
     * return null will prevent this tab from appearing.
     *
     * @return
     */
//    ToDo NG
//    protected BrowsePane getBrowsePane() {
//        if (m_browsePane == null) {
//            m_browsePane = new BrowsePane();
//        }
//        return m_browsePane;
//    }
    /**
     * Creates, and then caches, the search pane. Overriding this method to
     * return null will prevent this tab from appearing.
     *
     * @return
     */
//    ToDo NG
//    protected ItemSearch getSearchPane() {
//        if (m_searchPane == null) {
//            m_searchPane
//                = new ItemSearch(
//                    ContentItemVersion.DRAFT.toString(),
//                    CMSConfig.getConfig().isLimitItemSearchToContentSection());
//        }
//        return m_searchPane;
//    }
//    ToDo NG
//    protected ImagesPane getImagesPane() {
//        if (m_imagesPane == null) {
//            m_imagesPane = new ImagesPane();
//        }
//        return m_imagesPane;
//    }
//   ToDo NG
//    protected RoleAdminPane getRoleAdminPane() {
//        if (m_rolePane == null) {
//            m_rolePane = new RoleAdminPane();
//        }
//        return m_rolePane;
//    }
    /**
     * Creates, and then caches, the workflow administration pane. Overriding
     * this method to return null will prevent this tab from appearing.
     */
    protected WorkflowAdminPane getWorkflowAdminPane() {
        if (m_workflowPane == null) {
            m_workflowPane = new WorkflowAdminPane();
        }
        return m_workflowPane;
    }

    /**
     * Creates, and then caches, the lifecycle administration pane. Overriding
     * this method to return null will prevent this tab from appearing.
     */
    protected LifecycleAdminPane getLifecycleAdminPane() {
        if (m_lifecyclePane == null) {
            m_lifecyclePane = new LifecycleAdminPane();
        }
        return m_lifecyclePane;
    }

    /**
     * Creates, and then caches, the category administration pane. Overriding
     * this method to return null will prevent this tab from appearing.
     */
//ToDo NG
//    protected CategoryAdminPane getCategoryAdminPane() {
//        if (m_categoryPane == null) {
//            m_categoryPane = new CategoryAdminPane();
//        }
//        return m_categoryPane;
//    }
    /**
     * Creates, and then caches, the content type administration pane.
     * Overriding this method to return null will prevent this tab from
     * appearing.
     *
     * @return
     */
    protected ContentTypeAdminPane getContentTypeAdminPane() {
        if (m_typePane == null) {
            m_typePane = new ContentTypeAdminPane();
        }
        return m_typePane;
    }

//    protected LayoutPanel getUserAdminPane() {
//        if (m_userAdminPane == null) {
//            m_userAdminPane = new LayoutPanel();
//            m_userAdminPane.setLeft(new SimpleComponent());
//            m_userAdminPane.setBody(new UserAdminPane());
//        }
//        return m_userAdminPane;
//    }
    protected LayoutPanel getCSEPane() {
        if (m_csePane == null) {
            m_csePane = new LayoutPanel();
            m_csePane.setLeft(new SimpleComponent());
            m_csePane.setBody(new ContentSoonExpiredPane());
        }
        return m_csePane;
    }

    protected ReportPane getReportPane() {
        if (m_reportPane == null) {
            m_reportPane = new ReportPane();
        }
        return m_reportPane;
    }

    /**
     * Adds the specified component, with the specified tab name, to the tabbed
     * pane only if it is not null.
     *
     * @param pane The pane to which to add the tab
     * @param tabName The name of the tab if it's added
     * @param comp The component to add to the pane
     */
    protected void addToPane(final TabbedPane pane,
                             final String tabName,
                             final Component comp) {
        if (comp != null) {
            pane.addTab(new Label(tabName), comp);
        }
    }

    private void tab(final TabbedPane pane,
                     final String key,
                     final Component tab) {
        if (tab != null) {
            pane.addTab(new Label(gz(key)), tab);
        }
    }

    /**
     * <p>
     * Created the TabbedPane to use for this page. Adds the tabs to the pane.
     * The default implementation uses a {@link
     * com.arsdigita.bebop.TabbedPane}. This implementation also adds browse,
     * search, staff admin, viewers admin, workflow admin, category admin, and
     * content type panes.</p>
     *
     * <p>
     * Developers can override this method to add only the tabs they want, or to
     * add additional tabs after the default CMS tabs are added.</p>
     *
     * @return
     */
    protected TabbedPane createTabbedPane() {
        final TabbedPane pane = new TabbedPane();

        //tab(pane, "cms.ui.folders", getFolderAdminPane());
//        ToDo NG tab(pane, "cms.ui.browse", getBrowsePane());
//        ToDo NG tab(pane, "cms.ui.search", getSearchPane());
//        ToDo NG tab(pane, "cms.ui.images", getImagesPane());
//        ToDo NG tab(pane, "cms.ui.roles", getRoleAdminPane());
        tab(pane, "cms.ui.workflows", getWorkflowAdminPane());
        tab(pane, "cms.ui.lifecycles", getLifecycleAdminPane());
//       ToDo NG  tab(pane, "cms.ui.categories", getCategoryAdminPane());
        tab(pane, "cms.ui.content_types", getContentTypeAdminPane());
        tab(pane, "cms.ui.cse", getCSEPane());
//     ToDo NG       tab(pane, "cms.ui.reports", getReportPane());

        return pane;
    }

    /**
     * Fetch the request-local content section.
     *
     * @param request The HTTP request
     *
     * @return The current content section
     */
    @Override
    public ContentSection getContentSection(final HttpServletRequest request) {
        // Resets all content sections associations.
        ContentSection section = super.getContentSection(request);
        Assert.exists(section);
        return section;
    }

    /**
     * When a new tab is selected, reset the state of the formerly-selected
     * pane.
     *
     * @param event The event fired by selecting a tab
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        final PageState state = event.getPageState();

        final Component pane = m_tabbedPane.getCurrentPane(state);

//ToDo NG        if (pane == m_searchPane) {
//            m_searchPane.reset(state);
//        } else if (pane == m_imagesPane) {
//            m_imagesPane.reset(state);
//        } else 
        if (pane == m_folderPane) {
            m_folderPane.reset(state);
//ToDo NG        } else if (pane == m_browsePane) {
//            m_browsePane.reset(state);
//        } else if (pane == m_rolePane) {
//            m_rolePane.reset(state);
        } else if (pane == m_workflowPane) {
            m_workflowPane.reset(state);
        } else if (pane == m_lifecyclePane) {
            m_lifecyclePane.reset(state);
//ToDo NG        } else if (pane == m_categoryPane) {
//            m_categoryPane.reset(state);
        } else if (pane == m_typePane) {
            m_typePane.reset(state);
//        } else if (pane == m_userAdminPane) {
            //m_userAdminPane.reset(state);
        } else if (pane == m_csePane) {
            m_csePane.reset(state);
        }
    }

    /**
     * Construct a URL for displaying the tab
     *
     * @param item The item from which we get the corresponding content section
     * @param tab The index of the tab to display
     *
     * @return
     */
    public static String getSectionURL(final ContentItem item, final int tab) {
        // Get the content section associated with the content item.
        final ContentSection section = item.getContentType().getContentSection();

        final String url = section.getPrimaryUrl() + PageLocations.SECTION_PAGE
                                   + "?" + SET_TAB + "=" + tab;

        return url;
    }

    private static GlobalizedMessage gz(final String key) {
        return new GlobalizedMessage(key, CmsConstants.CMS_BUNDLE);
    }

    /**
     * Getting the GlobalizedMessage using a CMS Class targetBundle.
     *
     * @param key The resource key
     *
     * @return
     *
     * @pre key != null
     */
    public static GlobalizedMessage globalize(final String key) {
        return new GlobalizedMessage(key, CmsConstants.CMS_BUNDLE);
    }

    /**
     *
     * @param key
     * @param args
     *
     * @return
     */
    public static GlobalizedMessage globalize(final String key,
                                              final Object[] args) {
        return new GlobalizedMessage(key, CmsConstants.CMS_BUNDLE, args);
    }

    /**
     * Helper class to be able to use a PrintListener to set the titel of the
     * page.
     */
    private static class TitlePrinter implements PrintListener {

        /**
         *
         * @param event
         */
        @Override
        public void prepare(final PrintEvent event) {
            final Label l = (Label) event.getTarget();

            l.setLabel(CMS.getContext().getContentSection().getLabel());
        }

    }

}
