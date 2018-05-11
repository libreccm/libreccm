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
package com.arsdigita.cms.ui.contentcenter;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.TabbedPane;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.cms.ui.CMSApplicationPage;
import com.arsdigita.cms.ui.GlobalNavigation;
import com.arsdigita.cms.ui.WorkspaceContextBar;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.CcmObjectSelectionModel;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentType;

//  ////////////////////////////////////////////////////////////////////////////
//
//  Developer's Note:
//  Replaces the (old) Dispatcher based Code cms.ui.CMSPageWorkspacePage
//  Note should be removed as soon as the migration process is competed (in-
//  cluding content section pages).
//
//  ////////////////////////////////////////////////////////////////////////////
/**
 * <p>
 * The Content Center main page. </p>
 *
 * The page contains the general header and footer, the breadcrumb, and the
 * complete content page including the tab bar, the sections/tasks page, the
 * search page, and the listener to switch between the tabs.
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @author Michael Pih (pihman@arsdigita.com)
 * @author Peter Boy (pboy@barkhof.uni-bremen.de)
 */
public class MainPage extends CMSApplicationPage implements ActionListener {

    private final static String XSL_CLASS = "CMS Admin";

    public static final String CONTENT_TYPE = "type_id";
    public static final String CONTENT_SECTION = "section_id";

    private final CcmObjectSelectionModel<ContentType> typeSelection;
    private final CcmObjectSelectionModel<ContentSection> sectionSelection;

    private final TabbedPane tabbedPane;

    private TasksPane tasksPanel;
//    private ItemSearch m_search;
//    private IdSearchTab m_IdSearch;

    /**
     * Construct a new MainPage.
     *
     * Creates the complete page ready to be included in the page cache of
     * ContentCenterServlet.
     */
    public MainPage() {

        super(new Label(new GlobalizedMessage("cms.ui.content_center",
                                              CmsConstants.CMS_BUNDLE)),
              new SimpleContainer());


        /* Set the class attribute value (down in SimpleComponent).           */
        super.setClassAttr("cms-admin");

        final LongParameter typeId = new LongParameter(CONTENT_TYPE);
        super.addGlobalStateParam(typeId);
        typeSelection = new CcmObjectSelectionModel<>(ContentType.class, typeId);

        final LongParameter sectionId = new LongParameter(CONTENT_SECTION);
        super.addGlobalStateParam(sectionId);
        sectionSelection = new CcmObjectSelectionModel<>(ContentSection.class,
                                                         sectionId
        );

        super.add(new WorkspaceContextBar());
        super.add(new GlobalNavigation());

        tasksPanel = getTasksPane(typeSelection, sectionSelection);
//        m_search = getSearchPane();
//        m_IdSearch = getIdSearchPane();

        tabbedPane = createTabbedPane();
        tabbedPane.setIdAttr("page-body");
        super.add(tabbedPane);

//        add(new DebugPanel());
    }

    /**
     * Creates, and then caches, the Tasks pane. Overriding this method to
     * return null will prevent this tab from appearing.
     *
     * @param typeModel
     * @param sectionModel
     *
     * @return
     */
    protected TasksPane getTasksPane(
        final CcmObjectSelectionModel<ContentType> typeModel,
        final CcmObjectSelectionModel<ContentSection> sectionModel) {

        if (tasksPanel == null) {
            tasksPanel = new TasksPane(typeModel, sectionModel);
        }
        return tasksPanel;
    }

//    /**
//     * Creates, and then caches, the Search pane. Overriding this method to
//     * return null will prevent this tab from appearing.
//     *
//     */
//    protected ItemSearch getSearchPane() {
//        if (m_search == null) {
//            m_search = new ItemSearch(ContentItem.DRAFT);
//        }
//
//        return m_search;
//    }
//
//    protected IdSearchTab getIdSearchPane() {
//        if (m_IdSearch == null) {
//            m_IdSearch = new IdSearchTab("idsearch");
//        }
//
//        return m_IdSearch;
//    }
    /**
     * Created the TabbedPane to use for this page. Sets the class attribute for
     * this tabbed pane. The default implementation uses a
     * {@link com.arsdigita.bebop.TabbedPane} and sets the class attribute to
     * "CMS Admin." This implementation also adds tasks, content sections, and
     * search panes.
     *
     * Developers can override this method to add only the tabs they want, or to
     * add additional tabs after the default CMS tabs are added.
     *
     * @return
     */
    protected TabbedPane createTabbedPane() {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PermissionChecker permissionChecker = cdiUtil
            .findBean(PermissionChecker.class);

        final TabbedPane pane = new TabbedPane();
        pane.setClassAttr(XSL_CLASS);
        Label taskLabel = new Label(new GlobalizedMessage(
            "cms.ui.contentcenter.mainpage.taskssections",
            CmsConstants.CMS_BUNDLE));
        Label pagesLabel = new Label(new GlobalizedMessage(
            "cms.ui.contentcenter.mainpage.pages",
            CmsConstants.CMS_BUNDLE));
        Label searchLabel = new Label(new GlobalizedMessage(
            "cms.ui.contentcenter.mainpage.search", CmsConstants.CMS_BUNDLE));
        Label IdsearchLabel = new Label("ID Search");

        addToPane(pane,
                  taskLabel,
                  getTasksPane(typeSelection, sectionSelection));
        
//        if (permissionChecker.isPermitted(PagesPrivileges.ADMINISTER_PAGES)) {
            addToPane(pane,
                      pagesLabel,
                      new PagesPane());
//        }
//        addToPane(tabbedPane,
//                  new Label(new GlobalizedMessage(
//                      "cms.ui.contentcenter.mainpage.search", 
//                      CmsConstants.CMS_BUNDLE)),
//                  getSearchPane());
//        addToPane(tabbedPane,
//                  IdsearchLabel,
//                  getIdSearchPane());

        pane.addActionListener(this);
        return pane;
    }

//  /**
//   * Adds the specified component, with the specified tab name, to the
//   * tabbed pane only if it is not null.
//   *
//   * @param pane The pane to which to add the tab
//   * @param tabName The name of the tab if it's added
//   * @param comp The component to add to the pane
//   * @deprecated  refactor to use addToPane(Label, Component) instead to
//   *              enable localized tab strips.
//   */
//  protected void addToPane(TabbedPane pane, String tabName, Component comp) {
//      if (comp != null) {
//          pane.addTab(tabName, comp);
//      }
//  }
    /**
     * Adds the specified component, with the specified Label as tab name, to
     * the tabbed pane only if it is not null.
     *
     * @param pane      The pane to which to add the tab
     * @param tabName   The name of the tab if it's added
     * @param component The component to add to the pane
     */
    protected void addToPane(final TabbedPane pane,
                             final Label tabName,
                             final Component component) {
        if (component != null) {
            pane.addTab(tabName, component);
        }
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
        final Component pane = tabbedPane.getCurrentPane(state);

        if (pane == tasksPanel) {
            tasksPanel.reset(state);
        }
//        else if (pane == m_search) {
//            m_search.reset(state);
//        } else if (pane == m_IdSearch) {
//            m_IdSearch.reset(state);
//        }
    }

}
