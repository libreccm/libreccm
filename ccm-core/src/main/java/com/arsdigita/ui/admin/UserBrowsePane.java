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
package com.arsdigita.ui.admin;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.TabbedPane;
// import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionEvent;
// import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.UI;
import com.arsdigita.web.URL;
import com.arsdigita.web.RedirectSignal;

import static com.arsdigita.ui.admin.AdminConstants.*;

import com.arsdigita.util.LockableImpl;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.User;

/**
 * This pane contains three main segmented panel which only one is visible at
 * any given time. The first panel is a table listing all available users in the
 * system. The second panel displays read only user information. And the third
 * panel is edit form.
 *
 * @author David Dao
 * @author Ron Henderson
 * @version $Id$
 */
class UserBrowsePane extends SegmentedPanel
    implements TableCellRenderer,
               TableActionListener,
               Resettable,
               ActionListener,
               ChangeListener {

    private static final Logger s_log = Logger.getLogger(UserBrowsePane.class);

    private Component m_userBrowsePanel;
    private Component m_userInfoPanel;
    private Component m_userEditPanel;
    private Component m_userPasswordPanel;
    private Component m_groupMembershipPanel;
    private Component m_actionPanel;
    private Component m_extremeActionPanel;
    private Component m_userDeleteFailedPanel;
    private GroupAdministrationTab m_groupAdministrationTab;
    private TabbedPane m_tabbedPane;

    private List m_groupList = null;

    private ArrayList m_panelList = new ArrayList();

    private RequestLocal m_user;

    @Override
    public void register(Page p) {
        for (int i = 0; i < m_panelList.size(); i++) {
            p.setVisibleDefault((Component) m_panelList.get(i), false);
        }

        p.setVisibleDefault(m_userBrowsePanel, true);
        p.addActionListener(this);
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
//        final PageState state = event.getPageState();
//        final CdiUtil cdiUtil = new CdiUtil();
//        final CcmSessionContext sessionContext;

//            sessionContext = cdiUtil.findBean(CcmSessionContext.class);
//
//        final Subject subject = sessionContext.getCurrentSubject();
//
//        final Long userID = (Long) state.getValue(USER_ID_PARAM);
//
//        // Bug #167607 remove link for current user
//        if (m_userInfoPanel.isVisible(state)) {
//            if (subject.getSubjectId() == userID) {
//                m_extremeActionPanel.setVisible(state, false);
//            } else {
//                m_extremeActionPanel.setVisible(state, true);
//            }
//        }
    }

    /**
     * Creates a new UserBrowsePane with multiple panels to help manage various
     * aspects of a user's account.
     */
    public UserBrowsePane() {
        m_user = new RequestLocal() {

            @Override
            protected Object initialValue(final PageState state) {
                final Long id = (Long) state.getValue(USER_ID_PARAM);

//                final CdiUtil cdiUtil = new CdiUtil();
//                final UserRepository userRepository;

//                    userRepository = cdiUtil.findBean(UserRepository.class);
//
//                final User user = userRepository.findById(id);
//                if (user == null) {
//                    throw new UncheckedWrapperException(String.format(
//                        "Failed to retrieve user: %d", id));
//                }
//                return user;
                
                throw new UnsupportedOperationException();
            }

        };

        m_userBrowsePanel = buildUserBrowsePanel();
        m_panelList.add(m_userBrowsePanel);

        m_userInfoPanel = buildUserInfoPanel();
        m_panelList.add(m_userInfoPanel);

        m_userEditPanel = buildUserEditPanel();
        m_panelList.add(m_userEditPanel);

        m_userPasswordPanel = buildUserPasswordPanel();
        m_panelList.add(m_userPasswordPanel);

        m_groupMembershipPanel = buildGroupMembershipPanel();
        m_panelList.add(m_groupMembershipPanel);

        m_actionPanel = buildActionPanel();
        m_panelList.add(m_actionPanel);

        m_extremeActionPanel = buildExtremeActionPanel();
        m_panelList.add(m_extremeActionPanel);

        m_userDeleteFailedPanel = buildUserDeleteFailedPanel();
        m_panelList.add(m_userDeleteFailedPanel);
    }

    /**
     * Get user object for this request.
     */
    public User getUser(PageState ps) {
        return (User) m_user.get(ps);
    }

    /**
     * Build the User Information panel
     */
    private Component buildUserInfoPanel() {
        // Edit user link

        ActionLink link = new ActionLink(new Label(new GlobalizedMessage(
            "ui.admin.user.editlink",
            BUNDLE_NAME)));
        link.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                PageState ps = e.getPageState();
                displayEditPanel(ps);
            }

        });

        link.setClassAttr("actionLink");

        BoxPanel panel = new BoxPanel();

//        panel.add(new UserInfo(this));
        final ColumnPanel colPanel = new ColumnPanel(2);

        colPanel.add(new Label(new GlobalizedMessage(
            "ui.admin.user.userinfo.name", BUNDLE_NAME)));
        final Label userName = new Label();
        userName.addPrintListener(new PrintListener() {

            @Override
            public void prepare(final PrintEvent event) {
                final Label target = (Label) event.getTarget();
                final PageState state = event.getPageState();
                final User user = getUser(state);

                target.setLabel(user.getName());
            }

        });
        colPanel.add(userName);

        colPanel.add(new Label(new GlobalizedMessage(
            "ui.admin.user.userinfo.screenname",
            BUNDLE_NAME)));
        final Label userScreenname = new Label();
        userScreenname.addPrintListener(new PrintListener() {

            @Override
            public void prepare(final PrintEvent event) {
                final Label target = (Label) event.getTarget();
                final PageState state = event.getPageState();
                final User user = getUser(state);

                target.setLabel(user.getName());
            }

        });
        colPanel.add(userScreenname);

        colPanel.add(new Label(new GlobalizedMessage(
            "ui.admin.user.userinfo.primaryemail",
            BUNDLE_NAME)));
        final Label userEmail = new Label();
        userEmail.addPrintListener(new PrintListener() {

            @Override
            public void prepare(final PrintEvent event) {
                final Label target = (Label) event.getTarget();
                final PageState state = event.getPageState();
                final User user = getUser(state);

                target.setLabel(user.getEmailAddresses().get(0).getAddress());
            }

        });
        colPanel.add(userEmail);

        panel.add(colPanel);
        panel.add(link);

        return addSegment(USER_INFO_LABEL, panel);
    }

    /**
     * Build the User Edit panel
     */
    private Component buildUserEditPanel() {
        return addSegment(USER_EDIT_PANEL_HEADER, new UserEditForm(this));
    }

    /**
     * Build the User Password Update panel
     */
    private Component buildUserPasswordPanel() {
        BoxPanel p = new BoxPanel();
        p.add(new UserPasswordForm(this));
        p.add(new SimpleContainer("admin:PasswordNote", ADMIN_XML_NS));

        return addSegment(USER_PASSWORD_PANEL_HEADER, p);
    }

    /**
     * Build the Group Membership panel
     */
    private Component buildGroupMembershipPanel() {
        m_groupList = new List();
        m_groupList.setClassAttr("UserGroupsResultList");
        m_groupList.setModelBuilder(new GroupsModelBuilder());
        m_groupList.addChangeListener(this);

        return addSegment(USER_GROUP_PANEL_HEADER, m_groupList);
    }

    /**
     * Build the Action panel
     */
    private Component buildActionPanel() {
        BoxPanel p = new BoxPanel();

        // Update password link
        ActionLink link = new ActionLink(UPDATE_USER_PASSWORD_LABEL);
        link.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                PageState ps = e.getPageState();
                displayUserPasswordPanel(ps);
            }

        });
        link.setClassAttr("actionLink");
        p.add(link);

        // Become user link
        // This will not be shown when the user is banned to prevent security issues
        link = new ActionLink(BECOME_USER_LABEL) {

            public boolean isVisible(PageState s) {
                if (!super.isVisible(s)) {
                    return false;
                }
                User u = getUser(s);
                return (!u.isBanned());
            }

        };
        link.setClassAttr("actionLink");
        link.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                final PageState state = event.getPageState();
//                final BigDecimal id = (BigDecimal) state.getValue(USER_ID_PARAM);
//
//                final CdiUtil cdiUtil = new CdiUtil();
//                final LoginManager loginManager;

//                    loginManager = cdiUtil.findBean(
//                    LoginManager.class);
//                
//                loginManager.login(CLASS, CLASS);
//
//                try {
//                    UserContext uc = Web.getUserContext();
//                    uc.login(id);
//                } catch (javax.security.auth.login.LoginException ex) {
//                    throw new UncheckedWrapperException("access denied", ex);
//                }

                // Redirect to workspace URL
                final String path = UI.getUserRedirectURL(state.getRequest());

                final URL url = URL.there(state.getRequest(), path);

                throw new RedirectSignal(url, true);
            }

        });
        p.add(link);

        // Show all users
        link = new ActionLink(new Label(new GlobalizedMessage(
            "ui.admin.user.browselink",
            BUNDLE_NAME)));
        link.setClassAttr("actionLink");
        link.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PageState ps = e.getPageState();
                displayUserBrowsePanel(ps);
            }

        });
        p.add(link);

        return addSegment(USER_ACTION_PANEL_HEADER, p);
    }

    /**
     * Build the Extreme Action panel
     */
    private Component buildExtremeActionPanel() {

        ActionLink deleteLink = new ActionLink(USER_DELETE_LABEL) {

            @Override
            public boolean isVisible(PageState s) {
                if (!super.isVisible(s)) {
                    return false;
                }
                // We show the delete link if the user has never published an item
                // This implicitly checks whether the user is banned - if they
                // are deletable they cannot ever have been banned
                User u = getUser(s);
                return false;
            }

        };
        deleteLink.setClassAttr("actionLink");
        deleteLink.setConfirmation(USER_DELETE_CONFIRMATION.localize()
            .toString());
        deleteLink.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                final PageState state = event.getPageState();
                final User user = getUser(state);

//                final CdiUtil cdiUtil = new CdiUtil();
//                final UserRepository userRepository;

//                    userRepository = cdiUtil.findBean(UserRepository.class);

//                userRepository.delete(user);

                displayUserBrowsePanel(state);

            } // End ActionPerformed method

        } // End of new ActionListener definition
        );

        // Add link inside a BoxPanel for correct alignment with other
        // page elements.
        ActionLink banLink = new ActionLink(USER_BAN_LABEL) {

            @Override
            public boolean isVisible(PageState s) {
                if (!super.isVisible(s)) {
                    return false;
                }
                // We show the ban link if the user is not already banned and has never published 
                // an item
                User u = getUser(s);
                return (!u.isBanned());
            }

        };
        banLink.setClassAttr("actionLink");
        banLink.setConfirmation(USER_BAN_CONFIRMATION.localize().toString());
        banLink.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
//                PageState state = e.getPageState();
//                User user = getUser(state);
//                user.setBanned(true);
//
//                final CdiUtil cdiUtil = new CdiUtil();
//                final UserRepository userRepository;

//                    userRepository = cdiUtil.findBean(UserRepository.class);
//                userRepository.save(user);
            } // End ActionPerformed method

        } // End of new ActionListener definition
        );

        ActionLink unbanLink = new ActionLink(USER_UNBAN_LABEL) {

            public boolean isVisible(PageState s) {
                if (!super.isVisible(s)) {
                    return false;
                }
                PageState state = s.getPageState();
                User user = getUser(state);
                return user.isBanned();
            }

        };
        unbanLink.setClassAttr("actionLink");
        unbanLink.setConfirmation(USER_UNBAN_CONFIRMATION.localize().toString());
        unbanLink.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
//                PageState state = e.getPageState();
//                User user = getUser(state);
//                user.setBanned(false);
//
//                final CdiUtil cdiUtil = new CdiUtil();
//                final UserRepository userRepository;

//                    userRepository = cdiUtil.findBean(UserRepository.class);
//                userRepository.save(user);
            } // End ActionPerformed method

        } // End of new ActionListener definition
        );

        // Add link inside a BoxPanel for correct alignment with other
        // page elements.
        BoxPanel p = new BoxPanel();
        p.add(deleteLink);
        p.add(banLink);
        p.add(unbanLink);
        return addSegment(USER_TAB_EXTREME_ACTION_LABEL, p);
    }

    /**
     * Build a panel to display an error message when unable to delete a user.
     */
    private Component buildUserDeleteFailedPanel() {
        ActionLink link = new ActionLink(USER_ACTION_CONTINUE);
        link.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                PageState state = e.getPageState();
                displayUserInfoPanel(state);
            }

        });

        Label label = new Label(USER_DELETE_FAILED_MSG);
        label.setClassAttr("deleteFailedMessage");

        BoxPanel p = new BoxPanel();
        p.add(label);
        p.add(link);

        return addSegment(USER_DELETE_FAILED_PANEL_HEADER, p);
    }

    /**
     * Build the Browse User panel. Displays a list of all registered users.
     */
    private Component buildUserBrowsePanel() {
        String headers[] = new String[]{
            "ID", "Name", "Screen Name", "Email", "SSO login"
        };

        Table table = new Table(new UserTableModelBuilder(), headers);
        table.setClassAttr("AlternateTable");
        table.setDefaultCellRenderer(this);
        table.addTableActionListener(this);

        return addSegment(BROWSE_USER_PANEL_HEADER, table);
    }

    private class GroupsModelBuilder extends LockableImpl
        implements ListModelBuilder {

        @Override
        public ListModel makeModel(final List list, final PageState state) {
            final User user = getUser(state);
//            final java.util.List<GroupMembership> memberships = user
//                .getGroupMemberships();
//            final java.util.List<Subject> groups = new ArrayList<>();
//            for (GroupMembership membership : memberships) {
//                groups.add(membership.getGroup());
//            }

//            return new PartyListModel(groups);
            throw new UnsupportedOperationException();
        }

    }

    void displayUserInfoPanel(PageState ps) {
        hideAll(ps);
        m_userInfoPanel.setVisible(ps, true);
        m_groupMembershipPanel.setVisible(ps, true);
        m_actionPanel.setVisible(ps, true);
        m_extremeActionPanel.setVisible(ps, true);
    }

    void displayEditPanel(PageState ps) {
        hideAll(ps);
        m_userEditPanel.setVisible(ps, true);
    }

    public void displayUserBrowsePanel(PageState ps) {
        hideAll(ps);
        m_userBrowsePanel.setVisible(ps, true);
    }

    public void displayUserPasswordPanel(PageState ps) {
        hideAll(ps);
        m_userPasswordPanel.setVisible(ps, true);
    }

    public void displayUserDeleteFailedPanel(PageState ps) {
        hideAll(ps);
        m_userDeleteFailedPanel.setVisible(ps, true);
    }

    /**
     * Hides all components of the UserBrowsePane in preparation for turning
     * selected components back on.
     */
    private void hideAll(PageState ps) {
        for (int i = 0; i < m_panelList.size(); i++) {
            ((Component) m_panelList.get(i)).setVisible(ps, false);
        }
    }

    @Override
    public Component getComponent(Table table, PageState state, Object value,
                                  boolean isSelected, Object key, int row,
                                  int col) {
        if (col == 0) {
            ControlLink link = new ControlLink(value.toString());
            return link;
        } else {
            if (value != null) {
                return new Label(value.toString());
            } else {
                return new Label("&nbsp;", false);
            }
        }
    }

    @Override
    public void cellSelected(TableActionEvent e) {
        PageState ps = e.getPageState();

        ps.setValue(USER_ID_PARAM, new BigDecimal((String) e.getRowKey()));
        displayUserInfoPanel(ps);

    }

    @Override
    public void headSelected(TableActionEvent e) {
        // Empty
    }

    @Override
    public void reset(PageState ps) {
        displayUserBrowsePanel(ps);
    }

    public void setTabbedPane(TabbedPane tabbedPane) {
        m_tabbedPane = tabbedPane;
    }

    public void setGroupAdministrationTab(
        GroupAdministrationTab groupAdministrationTab) {
        m_groupAdministrationTab = groupAdministrationTab;
    }

//    // This is how we check if a user is banned or not
//    private boolean hasUserPublishedItems(User user) {
//        DataQuery query = user.getSession().retrieveQuery(
//            "com.arsdigita.versioning.UserPublications");
//        query.setParameter("value", new Integer(user.getID().intValue()));
//        query.next();
//        Integer count = (Integer) query.get("theCount");
//        query.close();
//        return count.intValue() != 0;
//    }
    /**
     * Display group information panel when a group name is clicked.
     */
    @Override
    public void stateChanged(final ChangeEvent event) {
        if (event.getSource() == m_groupList) {
            if (m_tabbedPane != null && m_groupAdministrationTab != null) {
                PageState ps = event.getPageState();
                String id = (String) m_groupList.getSelectedKey(ps);
                if (id != null) {
                    final CdiUtil cdiUtil = new CdiUtil();
//                    final GroupRepository groupRepository;

//                        groupRepository = cdiUtil
//                            .findBean(GroupRepository.class);
//                    final Group group = groupRepository.findById(Long.parseLong(
//                        id));
//                    m_groupAdministrationTab.setGroup(ps, group);
                    m_groupAdministrationTab.displayGroupInfoPanel(ps);
                    m_tabbedPane.setSelectedIndex(ps, GROUP_TAB_INDEX);
                } else {
                    reset(ps);
                }
            }
        }
    }

}

class UserTableModelBuilder extends LockableImpl implements TableModelBuilder {

    public TableModel makeModel(Table t, PageState s) {
        return new UserTableModel();

    }

}

class UserTableModel implements TableModel {

    private final java.util.List<User> users;
    private int index = 0;

    public UserTableModel() {
        final CdiUtil cdiUtil = new CdiUtil();
//        final UserRepository userRepository;

//            userRepository = cdiUtil.findBean(UserRepository.class);

//        users = userRepository.findAll();
        
        users = null;
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getElementAt(final int columnIndex) {
        final User user = users.get(index);
        
//        if (columnIndex == 0) {
//            return user.getSubjectId();
//        } else if (columnIndex == 1) {
//            return String.format("%s %s", 
//                                 user.getName().getGivenName(), 
//                                 user.getName().getFamilyName());
//        } else if (columnIndex == 2) {
//            return user.getScreenName();
//        } else if (columnIndex == 3) {
//            return user.getEmailAddresses().get(0).getAddress();
//        } else if (columnIndex == 4) {
//            return user.getSsoLogin();
//        } else {
//            return null;
//        }
        
        return null;
    }

    @Override
    public Object getKeyAt(final int columnIndex) {
        return users.get(index).getPartyId();
    }

    @Override
    public boolean nextRow() {
        index++;
        return index < users.size();
    }

}
