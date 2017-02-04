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
package com.arsdigita.cms.ui.contentcenter;

import java.math.BigDecimal;

import javax.servlet.ServletException;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PaginationModelBuilder;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ui.CMSContainer;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.CcmObjectSelectionModel;
import com.arsdigita.xml.Element;

import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentType;

/**
 *
 * @version $Id$
 */
public class TasksPanel extends CMSContainer {

    // The default number of rows to show
    private static final int DEFAULT_MAX_ROWS = 15;
    // Number of tasks to show
    private final int m_maxRows;
    private TaskList m_taskList;
    // private ActionLink m_viewAllLink;
    // private ActionLink m_viewShortLink;
    private Paginator m_paginator;
    private ActionLink m_viewLockLink;
    private ActionLink m_viewUnlockLink;
    private ActionLink m_viewAllLockLink;
    private Label m_viewLockLabel;
    private Label m_viewUnlockLabel;
    private Label m_viewAllLockLabel;
    private StringParameter m_sortDirectionParam;
    private StringParameter m_sortTypeParam;
    private StringParameter m_lockFilterParam;
    // control link variable
    private static final String TASK_PANEL_CLASS = "taskPanel";
    private static final String TASK_ACTION = "taskAction";
    private static final String SORT_DOWN = "sortActionUp";
    private static final String SORT_UP = "sortActionDown";
    private static final String LOCK_FILTER_TYPE = "lockFilterType";
    private static final String SORT_TYPE = "sortType";
    private static final String SORT_DIRECTION = "sortDirection";
    private static final String SORT_ACTION = "action";
    private static final String SORT_DATE = "date";
    private static final String SORT_STATUS = "status";
    private static final String SORT_TITLE = "title";
    private static final String SORT_USER = "user";
    private static final String SORT_WORKFLOW = "workflow";
    // IMAGES
    public static final String UP_ARROW_IMAGE
                                   = "/themes/heirfloom/images/gray-triangle-up.gif";
    public static final String DOWN_ARROW_IMAGE
                                   = "/themes/heirfloom/images/gray-triangle-down.gif";
    // CREATION PANE CONSTANTS
    private Label m_selectorLabel;
//ToDo
//    private CreationSelector m_selector;
    private ContentSectionContainer m_sections;
//    ToDo End
    private CcmObjectSelectionModel m_sectionSel;
    private CcmObjectSelectionModel m_typeSel;

//    private RootFolderSelectionModel m_folderSel;
    private BoxPanel m_creationPane;

    /**
     * Constructs a new task panel that shows no more than 15 enabled tasks by
     * default.
     *
     * @param typeModel
     * @param sectionModel
     *
     */
    public TasksPanel(CcmObjectSelectionModel typeModel,
                      CcmObjectSelectionModel sectionModel) {
        this(DEFAULT_MAX_ROWS, typeModel, sectionModel);
    }

    /**
     * Constructs a new task panel that shows a specified number enabled tasks.
     *
     * @param maxRows      the maximum number of rows to show by default
     * @param typeModel
     * @param sectionModel
     *
     * @pre maxRows != null
     *
     */
    public TasksPanel(int maxRows, 
                      CcmObjectSelectionModel typeModel,
                      CcmObjectSelectionModel sectionModel) {
        super();

        // Set class attribute
        setClassAttr(TASK_PANEL_CLASS);

        m_maxRows = maxRows;

        m_typeSel = typeModel;
        m_sectionSel = sectionModel;

        m_sortDirectionParam = new StringParameter(SORT_DIRECTION);
        m_sortTypeParam = new StringParameter(SORT_TYPE);
        m_lockFilterParam = new StringParameter(LOCK_FILTER_TYPE);
        addComponents();
    }

    /**
     * Adds the components to this tasks panel
     */
    private void addComponents() {
        m_creationPane = new BoxPanel(BoxPanel.VERTICAL);

        // A label that says "Create $content_type in $section"
        m_selectorLabel = new Label(new PrintListener() {

            @Override
            public void prepare(PrintEvent e) {
                PageState s = e.getPageState();
                Label t = (Label) e.getTarget();

                ContentType type = (ContentType) m_typeSel.getSelectedObject(s);
                ContentSection sec = (ContentSection) m_sectionSel
                    .getSelectedObject(s);

                StringBuilder buf = new StringBuilder(
                    new GlobalizedMessage("cms.ui.create",
                                          CmsConstants.CMS_BUNDLE).localize()
                        + " ");
                buf.append(type.getLabel());
                buf.append(" in ");
                buf.append(sec.getLabel());

                t.setLabel(buf.toString());
                t.setFontWeight(Label.BOLD);
                t.setClassAttr("creationLabel");
            }

        });
        m_selectorLabel.setClassAttr("creationLabel");
        m_creationPane.add(m_selectorLabel);

//ToDo        
//        m_folderSel = new RootFolderSelectionModel(m_sectionSel);
//        m_selector = new CreationSelector(m_typeSel, m_folderSel);
//        m_creationPane.add(m_selector);
//ToDo End
        m_creationPane.setClassAttr("itemCreationPane");
        add(m_creationPane);

        // The section list UIx
//ToDo
        m_sections = new ContentSectionContainer(m_typeSel, m_sectionSel);
        add(m_sections);
//ToDo End
        // When a new type is selected, show the creation UI.
        // When the selection is cleared, return to section list
        m_typeSel.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                PageState s = e.getPageState();
                boolean isSelected = m_typeSel.isSelected(s);

//ToDo
//                m_sections.setVisible(s, !isSelected);
//ToDo End
                m_creationPane.setVisible(s, isSelected);
            }

        });

        m_viewLockLink = new ActionLink(new Label(new GlobalizedMessage(
            "cms.ui.workflow.task.view_locked", CmsConstants.CMS_BUNDLE)));
        m_viewLockLink.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PageState ps = e.getPageState();
                ps.setValue(m_lockFilterParam, "lock");
            }

        });

        m_viewUnlockLink = new ActionLink(new Label(new GlobalizedMessage(
            "cms.ui.workflow.task.view_unlocked", CmsConstants.CMS_BUNDLE)));
        m_viewUnlockLink.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PageState ps = e.getPageState();
                ps.setValue(m_lockFilterParam, "unlock");
            }

        });

        m_viewAllLockLink = new ActionLink(new Label(new GlobalizedMessage(
            "cms.ui.workflow.task.view_all", CmsConstants.CMS_BUNDLE)));
        m_viewAllLockLink.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PageState ps = e.getPageState();
                ps.setValue(m_lockFilterParam, "all");
            }

        });

        m_viewLockLabel = new Label(new GlobalizedMessage(
            "cms.ui.workflow.task.view_locked", CmsConstants.CMS_BUNDLE));
        m_viewLockLabel.setFontWeight(Label.BOLD);
        m_viewUnlockLabel = new Label(new GlobalizedMessage(
            "cms.ui.workflow.task.view_unlocked", CmsConstants.CMS_BUNDLE));
        m_viewUnlockLabel.setFontWeight(Label.BOLD);
        m_viewAllLockLabel = new Label(new GlobalizedMessage(
            "cms.ui.workflow.task.view_all", CmsConstants.CMS_BUNDLE));
        m_viewAllLockLabel.setFontWeight(Label.BOLD);

        add(new Label("<br />", false));
        add(m_viewLockLink);
        add(m_viewLockLabel);
        add(new Label("&nbsp;", false));
        add(m_viewUnlockLink);
        add(m_viewUnlockLabel);
        add(new Label("&nbsp;", false));
        add(m_viewAllLockLink);
        add(m_viewAllLockLabel);
        add(new Label("<br />", false));
        add(new Label("<br />", false));

        add(getTasksList());
        add(getPaginator());

//        m_actionLabel = new Label(GlobalizationUtil.globalize("cms.ui.action"));
//        m_actionLabel.setClassAttr("action");
    }

    /**
     *
     * @param p
     */
    @Override
    public void register(Page p) {
        super.register(p);

        p.setVisibleDefault(m_creationPane, false);
//ToDo
//        p.addComponentStateParam(this, m_folderSel.getStateParameter());
//ToDo End

        p.addGlobalStateParam(m_lockFilterParam);
        p.addGlobalStateParam(m_sortTypeParam);
        p.addGlobalStateParam(m_sortDirectionParam);

        p.setVisibleDefault(m_viewLockLabel, false);
        p.setVisibleDefault(m_viewUnlockLabel, false);
        p.setVisibleDefault(m_viewAllLockLink, false);

        p.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final PageState state = e.getPageState();

                String lockFilterType = getLockFilterType(state);

                if (lockFilterType.equals("lock")) {
                    m_viewLockLabel.setVisible(state, true);
                    m_viewLockLink.setVisible(state, false);
                    m_viewUnlockLabel.setVisible(state, false);
                    m_viewUnlockLink.setVisible(state, true);
                    m_viewAllLockLabel.setVisible(state, false);
                    m_viewAllLockLink.setVisible(state, true);
                } else if (lockFilterType.equals("unlock")) {
                    m_viewLockLabel.setVisible(state, false);
                    m_viewLockLink.setVisible(state, true);
                    m_viewUnlockLabel.setVisible(state, true);
                    m_viewUnlockLink.setVisible(state, false);
                    m_viewAllLockLabel.setVisible(state, false);
                    m_viewAllLockLink.setVisible(state, true);
                } else {
                    m_viewLockLabel.setVisible(state, false);
                    m_viewLockLink.setVisible(state, true);
                    m_viewUnlockLabel.setVisible(state, false);
                    m_viewUnlockLink.setVisible(state, true);
                    m_viewAllLockLabel.setVisible(state, true);
                    m_viewAllLockLink.setVisible(state, false);
                }
            }

        });
    }

    public void reset(PageState state) {
        m_typeSel.clearSelection(state);
        m_sectionSel.clearSelection(state);
    }

    protected Paginator getPaginator() {
        if (m_paginator == null) {
            m_paginator = new Paginator(new TasksPaginationModelBuilder(),
                                        m_maxRows);
        }
        return m_paginator;
    }

    /**
     * Returns the bebop List component to display the tasks
     *
     */
    private TaskList getTasksList() {
        if (m_taskList == null) {
            m_taskList = new TaskList(true);
        }
        return m_taskList;
    }

    /**
     * Returns the number of enabled tasks for the specified user.
     *
     * @param state
     *
     * @return
     *
     */
    protected int numberTasksForUser(PageState state) {
        return m_taskList.size(state);
    }

    protected String getSortType(PageState state) {
        String sortType = (String) state.getValue(m_sortTypeParam);
        if (sortType == null) {
            sortType = SORT_DATE;
        }
        return sortType;
    }

    protected String getSortDirection(PageState state) {
        String sortDirection = (String) state.getValue(m_sortDirectionParam);
        if (sortDirection == null) {
            sortDirection = SORT_UP;
        }
        return sortDirection;
    }

    protected String getLockFilterType(PageState state) {
        String lockFilterType = (String) state.getValue(m_lockFilterParam);
        if (lockFilterType == null || lockFilterType.equals("")) {
            lockFilterType = "lock";
        }
        return lockFilterType;
    }

    /*
     * Adds filters to the task query
     */
//ToDo
//    protected void addQueryFilters(DataQuery query, Party party, PageState state) {
//        query.setParameter("userId", party.getID());
//
//        FilterFactory ff = query.getFilterFactory();
//
//        // TODO: remove this hard coding of "Author", "Edit", and "Deploy"
//        // TODO: remove this hard coding of "Author", "Edit", and "Deploy"
//        CompoundFilter authorFilter = ff.and();
//        //cg query changed to refer to task type id
//        authorFilter.addFilter(ff.equals("taskType", CMSTaskType.AUTHOR));
//        authorFilter.addFilter(getTaskFilter(CMSTaskType.retrieve(
//            CMSTaskType.AUTHOR),
//                                             party, ff));
//
//        CompoundFilter approveFilter = ff.and();
//        approveFilter.addFilter(ff.equals("taskType", CMSTaskType.EDIT));
//        approveFilter.addFilter(getTaskFilter(CMSTaskType.retrieve(
//            CMSTaskType.EDIT),
//                                              party, ff));
//
//        CompoundFilter deployFilter = ff.and();
//        deployFilter.addFilter(ff.equals("taskType", CMSTaskType.DEPLOY));
//        deployFilter.addFilter(getTaskFilter(CMSTaskType.retrieve(
//            CMSTaskType.DEPLOY),
//                                             party, ff));
//
//        CompoundFilter permissionFilter = ff.or();
//        permissionFilter.addFilter(authorFilter);
//        permissionFilter.addFilter(approveFilter);
//        permissionFilter.addFilter(deployFilter);
//
//        query.addFilter(permissionFilter);
//
//        String lockFilterType = getLockFilterType(state);
//        if (lockFilterType.equals("lock")) {
//            query.addEqualsFilter("isLocked", "t");
//            query.addEqualsFilter("status", "1");
//        } else if (lockFilterType.equals("unlock")) {
//            query.addEqualsFilter("isLocked", "f");
//        } // else show all
//    }
//    private static class RootFolderSelectionModel
//        extends FolderSelectionModel {
//
//        CcmObjectSelectionModel m_sectionSel;
//
//        public RootFolderSelectionModel(CcmObjectSelectionModel sectionSel) {
//            super("f");
//            m_sectionSel = sectionSel;
//        }
//
//        @Override
//        protected BigDecimal getRootFolderID(PageState s) {
//            ContentSection sec = (ContentSection) m_sectionSel
//                .getSelectedObject(s);
//            Assert.exists(sec);
//
//            User user = Web.getWebContext().getUser();
//            if (user != null) {
//                Folder folder = Folder.getUserHomeFolder(user, sec);
//                if (folder != null) {
//                    return folder.getID();
//                }
//            }
//            return sec.getRootFolder().getID();
//        }
//
//    }
//ToDo End
    /**
     *
     */
    private class TasksPaginationModelBuilder implements PaginationModelBuilder {

        @Override
        public int getTotalSize(Paginator paginator,
                                PageState state) {
            return numberTasksForUser(state);
        }

        @Override
        public boolean isVisible(PageState state) {
            return numberTasksForUser(state) > m_maxRows;
        }

    }

    private class TaskList extends SimpleComponent {

        private final static String QUERY_NAME
                                        = "com.arsdigita.cms.workflow.getEnabledUserTasks";
        private boolean m_paginate = false;

        public TaskList(boolean paginate) {
            m_paginate = paginate;
        }

//        private DataQuery makeQuery(PageState state) {
//            User user = (User) Kernel.getContext().getParty();
//
//            DataQuery query = SessionManager.getSession()
//                .retrieveQuery(QUERY_NAME);
//            addQueryFilters(query, user, state);
//
//            return query;
//        }
        public int size(PageState ps) {
            return ((Integer)m_taskCount.get(ps)).intValue();
        }

        private RequestLocal m_taskCount = new RequestLocal() {

            @Override
            public Object initialValue(PageState state) {
//                DataQuery query = makeQuery(state);
//                return new Long(query.size());
                return 0;
            }

        };

        @Override
        public void generateXML(PageState state,
                                Element parent) {
            Element content = parent.newChildElement("cms:tasksPanel",
                                                     CMS.CMS_XML_NS);
            exportAttributes(content);

//            DataQuery query = makeQuery(state);
            String lockFilterType = getLockFilterType(state);
            content.addAttribute("lockFilterType", lockFilterType);

            if (m_paginate) {
//                query.setRange(new Integer(m_paginator.getFirst(state)),
//                               new Integer(m_paginator.getLast(state) + 1));
            }

            String sortKey = getSortType(state);
            String sortDirection = getSortDirection(state);
            String sortPostfix = " asc";
            if (sortDirection.equals(SORT_DOWN)) {
                sortPostfix = " desc";
            }

//            if (sortKey.equals(SORT_TITLE)) {
//                query.setOrder("lower(pageTitle) " + sortPostfix
//                                   + ", lower(status) asc, dueDate desc");
//            } else if (sortKey.equals(SORT_DATE)) {
//                query.setOrder("dueDate " + sortPostfix
//                                   + ", lower(status) asc, lower(pageTitle) asc");
//            } else if (sortKey.equals(SORT_USER)) {
//                query.setOrder("lockingUserID " + sortPostfix
//                                   + ", lower(status) asc, dueDate desc "
//                                   + ", lower(pageTitle) asc");
//            } else if (sortKey.equals(SORT_STATUS)) {
//                query.setOrder("lower(status) " + sortPostfix
//                                   + ", dueDate desc "
//                                   + ", lower(pageTitle) asc");
//
//            } else if (sortKey.equals(SORT_ACTION)) {
//                query.setOrder("taskType " + sortPostfix
//                                   + ", lower(status) asc, dueDate desc "
//                                   + ", lower(pageTitle) asc");
//            } else if (sortKey.equals(SORT_WORKFLOW)) {
//                query.setOrder("processLabel " + sortPostfix
//                                   + ", lower(status) asc, dueDate desc "
//                                   + ", lower(pageTitle) asc");
//            }
//
//            HashMap sections = new HashMap();
//
//            while (query.next()) {
//                BigDecimal sectionID = (BigDecimal) query.get("sectionID");
//                String sectionPath = (String) sections.get(sectionID);
//                if (sectionPath == null) {
//                    try {
//                        ContentSection section
//                                           = (ContentSection) DomainObjectFactory
//                            .newInstance(new OID(
//                                ContentSection.BASE_DATA_OBJECT_TYPE,
//                                sectionID));
//                        sectionPath = section.getPath();
//                    } catch (DataObjectNotFoundException ex) {
//                        throw new UncheckedWrapperException(
//                            "cannot find content section", ex);
//                    }
//                    sections.put(sectionID, sectionPath);
//                }
//
//                Element task = content.newChildElement("cms:tasksPanelTask",
//                                                       CMS.CMS_XML_NS);
//
//                BigDecimal itemID = (BigDecimal) query.get("itemID");
//                String taskType = (String) query.get("taskType");
//
//                task.addAttribute("taskID", query.get("taskID").toString());
//                task
//                    .addAttribute("processID", query.get("processID").toString());
//                task.addAttribute("taskLabel", (String) query.get("taskLabel"));
//                task.addAttribute("taskDescription", (String) query.get(
//                                  "taskDescription"));
//                task.addAttribute("processLabel", (String) query.get(
//                                  "processLabel"));
//
//                String isLocked = (String) query.get("isLocked");
//                task.addAttribute("isLocked", isLocked);
//                if (query.get("dueDate") != null) {
//                    java.util.Date d = (java.util.Date) query.get("dueDate");
//                    SimpleDateFormat df = new SimpleDateFormat(
//                        "EEE, MMM d, yyyy");
//
//                    task.addAttribute("dueDate", df.format(d));
//                }
//
//                task.addAttribute("itemID", itemID.toString());
//                task
//                    .addAttribute("sectionID", query.get("sectionID").toString());
//                task.addAttribute("sectionPath", sectionPath);
//                task.addAttribute("pageName", (String) query.get("pageName"));
//                task.addAttribute("pageTitle", (String) query.get("pageTitle"));
//
//                BigDecimal lockingUserID = (BigDecimal) query.get(
//                    "lockingUserID");
//                if (lockingUserID != null) {
//                    task.addAttribute("lockingUserID", lockingUserID.toString());
//                    if (!"f".equals(isLocked)) {
//                        User lockingUser = User.retrieve(lockingUserID);
//                        if (lockingUser != null) {
//                            task.addAttribute("assignee", lockingUser
//                                              .getDisplayName());
//                        }
//                    }
//                }
//                task.addAttribute("taskType", taskType);
//                task.addAttribute("taskTypeClass", (String) query.get(
//                                  "taskTypeClass"));
//                task.addAttribute("status", query.get("status").toString());
//
//                // control event for locking a task
//                try {
//                    state.setControlEvent(this, TASK_ACTION, itemID.toString());
//                    task.addAttribute("actionURL", state.stateAsURL());
//                    state.clearControlEvent();
//                } catch (java.io.IOException ex) {
//                    s_log.warn("Error: " + ex.getMessage());
//                }
//
//                if ("Deploy".equals(taskType)) {
//                    task.addAttribute("editTabNumber",
//                                      String.valueOf(
//                                          ContentItemPage.PUBLISHING_TAB));
//                } else {
//                    task.addAttribute("editTabNumber",
//                                      String.valueOf(
//                                          ContentItemPage.AUTHORING_TAB));
//                }
        }

        // m_actionLabel.generateXML(state, content);
        String[][] sortableHeaders = {{SORT_TITLE,
                                       "cms.ui.workflow.task.item_title"},
                                      {SORT_ACTION, "cms.ui.action"},
                                      {SORT_DATE, "cms.ui.tasks_due_date"},
                                      {SORT_STATUS,
                                       "cms.ui.tasks_status_no_colon"},
                                      {SORT_USER,
                                       "cms.ui.workflow.task.locking_user"},
                                      {SORT_WORKFLOW, "cms.ui.workflow"}};
//            for (int i = 0; i < sortableHeaders.length; i++) {
//                String header = sortableHeaders[i][0];
//                String labelKey = sortableHeaders[i][1];
//                if (sortDirection.equals(SORT_UP) && header.equals(sortKey)) {
//                    state.setControlEvent(this, SORT_DOWN, header);
//                } else {
//                    state.setControlEvent(this, SORT_UP, header);
//                }
//                SimpleContainer container = new SimpleContainer();
//                container.add(new Label(GlobalizationUtil.globalize(labelKey)));
//                if (header.equals(sortKey)) {
//                    String imageURLStub = null;
//                    if (SORT_UP.equals(sortDirection)) {
//                        imageURLStub = UP_ARROW_IMAGE;
//                    } else {
//                        imageURLStub = DOWN_ARROW_IMAGE;
//                    }
//                    Image image = new Image(imageURLStub);
//                    image.setBorder("0");
//                    container.add(image);
//                }
//
//                ControlLink link = new ControlLink(container);
//                link.setHint(GlobalizationUtil
//                    .globalize("cms.ui.contentcenter.task_panel_control"));
//                link.setClassAttr(header);
//                link.generateXML(state, content);
//                state.clearControlEvent();
//            }

    }

    @Override
    public void respond(PageState state) throws ServletException {
        String key = state.getControlEventName();
        String value = state.getControlEventValue();
        if (TASK_ACTION.equals(key)) {
            BigDecimal itemID = new BigDecimal(value);
//
//                try {
//                    ContentItem item = new ContentItem(itemID);
//                    Workflow wf = Workflow.getObjectWorkflow(item);
//                    int tabNumber = ContentItemPage.AUTHORING_TAB;
//                    String sectionPath = item.getContentSection().getPath();
//
//                    if (wf != null) {
//                        User user = Web.getWebContext().getUser();
//                        Engine engine = Engine.getInstance(
//                            CMSEngine.CMS_ENGINE_TYPE);
//                        Iterator i = engine.getEnabledTasks(user, wf.getID())
//                            .iterator();
//                        if (i.hasNext()) {
//                            CMSTask task = (CMSTask) i.next();
//                            Integer taskType = task.getTaskType().getID();
//
//                            if (taskType.equals(CMSTaskType.DEPLOY)) {
//                                tabNumber = ContentItemPage.PUBLISHING_TAB;
//                            } else // see if item is locked; if not, lock
//                            {
//                                if (!task.isLocked()) {
//                                    task.lock(user);
//                                }
//                            }
//                        }
//                    }
//
//                    String redirectURL = Web.getConfig()
//                        .getDispatcherServletPath()
//                                             + sectionPath
//                                             + "/admin/item.jsp?item_id="
//                                             + itemID + "&set_tab=" + tabNumber;
//                    throw new RedirectSignal(redirectURL, true);
//                } catch (DataObjectNotFoundException ex) {
//                    throw new ServletException("Unknown content ID" + itemID);
//                }
//            } else 
            if (SORT_UP.equals(key) || SORT_DOWN.equals(key)) {
                state.setValue(m_sortTypeParam, value);
                if (SORT_DOWN.equals(key)) {
                    state.setValue(m_sortDirectionParam, SORT_DOWN);
                } else {
                    state.setValue(m_sortDirectionParam, SORT_UP);
                }
            } else {
                throw new ServletException("Unknown control event: " + key);
            }
        }

    }

//    private static Filter getTaskFilter(CMSTaskType taskType, Party party,
//                                        FilterFactory factory) {
//        PrivilegeDescriptor privilege;
//        String queryName;
//        String queryType;
//        OID partyOID = party.getOID();
//        privilege = taskType.getPrivilege();
//
//        return PermissionService.getFilterQuery(factory, "itemID", privilege,
//                                                partyOID);
//    }
}
