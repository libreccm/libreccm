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
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.LayoutPanel;

import static com.arsdigita.ui.admin.AdminConstants.*;

import com.arsdigita.util.LockableImpl;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiLookupException;
import org.libreccm.cdi.utils.CdiUtil;

/**
 * Constructs the panel for administration of groups.
 *
 * @author David Dao
 *
 */
class GroupAdministrationTab extends LayoutPanel implements AdminConstants,
                                                            ChangeListener {

    private static final Logger LOGGER = Logger.getLogger(
        GroupAdministrationTab.class);
    private final Tree groupTree;
    private SearchAndList subMemberSearch;
    private ActionLink addSubmemberLink;
    private final Component groupInfoPanel;
    private final Component subGroupPanel;
    private final Component subMemberPanel;
    private final Component extremeActionPanel;
    private final Component groupAddPanel;
    private final Component groupEditPanel;
    private final Component groupDeleteFailedPanel;
    private final Component existingGroupAddPanel;
    private ExistingGroupAddPane m_existingGroupAdd;
    private final java.util.List<Component> panelList
                                                = new ArrayList<Component>();
    private final RequestLocal requestLocalGroup;

    /**
     *
     * @param page
     */
    @Override
    public void register(final Page page) {
        for (int i = 0; i < panelList.size(); i++) {
            page.setVisibleDefault(panelList.get(i), false);
        }

        page.setVisibleDefault(groupAddPanel, true);
    }

    /**
     *
     * @param state
     *
     * @return
     */
//    public Group getGroup(final PageState state) {
//        return (Group) requestLocalGroup.get(state);
//    }

//    public void setGroup(final PageState state, final Group group) {
//        final String groupId = Long.toString(group.getSubjectId());
//        requestLocalGroup.set(state, group);
//        groupTree.setSelectedKey(state, groupId);
//
//        if (!"-1".equals(groupId)) {
//            expandGroups(state, group);
//            groupTree.expand("-1", state);
//        }
//    }

//    private void expandGroups(final PageState state, final Group group) {
////        groupTree.expand(Long.toString(group.getSubjectId()), state);
////
////        final List< superGroups = group.getSupergroups();
////        Group superGroup;
////        while (superGroups.next()) {
////            superGroup = (Group) superGroups.getDomainObject();
////            expandGroups(state, superGroup);
////        }
//    }

    /**
     * Constructor
     */
    public GroupAdministrationTab() {
        super();

        setClassAttr("sidebarNavPanel");
        setAttribute("navbar-title", "Groups");

        requestLocalGroup = new RequestLocal() {

            @Override
            protected Object initialValue(final PageState state) {
//                String key = (String) groupTree.getSelectedKey(state);

//                Group group;
//                if (key != null) {
//                    final long id = Long.parseLong(key);
//
//                    final CdiUtil cdiUtil = new CdiUtil();
//                    final GroupRepository groupRepository;
//                    try {
//                        groupRepository = cdiUtil
//                            .findBean(GroupRepository.class);
//                    } catch (CdiLookupException ex) {
//                        throw new UncheckedWrapperException(
//                            "Failed to lookup GroupRepository", ex);
//                    }
//
//                    group = groupRepository.findById(id);
//
//                    return group;
//                }
                return null;
            }

        };

        setClassAttr("navbar");

        groupTree = new Tree(new GroupTreeModelBuilder());
        groupTree.addChangeListener(this);
        setLeft(groupTree);

        final SegmentedPanel body = new SegmentedPanel();
        body.setClassAttr("main");

        groupInfoPanel = buildGroupInfoPanel(body);
        panelList.add(groupInfoPanel);

        groupEditPanel = buildGroupEditPanel(body);
        panelList.add(groupEditPanel);

        subGroupPanel = buildSubGroupPanel(body);
        panelList.add(subGroupPanel);

        groupAddPanel = buildGroupAddPanel(body);
        panelList.add(groupAddPanel);

        existingGroupAddPanel = buildExistingGroupAddPanel(body);
        panelList.add(existingGroupAddPanel);

        subMemberPanel = buildMemberListPanel(body);
        panelList.add(subMemberPanel);

        extremeActionPanel = buildExtremeActionPanel(body);
        panelList.add(extremeActionPanel);

        groupDeleteFailedPanel = buildGroupDeleteFailedPanel(body);
        panelList.add(groupDeleteFailedPanel);

        setBody(body);
    }

    public void displayAddGroupPanel(final PageState state) {
        hideAll(state);
        groupAddPanel.setVisible(state, true);
    }

    private void displayAddExistingGroupPanel(final PageState state) {
        hideAll(state);
        existingGroupAddPanel.setVisible(state, true);
    }

    public void displayEditPanel(final PageState state) {
        hideAll(state);
        groupEditPanel.setVisible(state, true);
    }

    public void displayGroupInfoPanel(final PageState state) {
        showAll(state);
        groupEditPanel.setVisible(state, false);
        groupAddPanel.setVisible(state, false);
        groupDeleteFailedPanel.setVisible(state, false);
        existingGroupAddPanel.setVisible(state, false);
    }

    public void displayDeleteFailedPanel(final PageState state) {
        hideAll(state);
        groupDeleteFailedPanel.setVisible(state, true);
    }

    /**
     *
     * @param event
     */
    public void stateChanged(final ChangeEvent event) {

        final PageState ps = event.getPageState();
        final String key = (String) groupTree.getSelectedKey(ps);
        // added cg - reset existing group add panel to the search screen 
        // when a new group is selected from the tree
        m_existingGroupAdd.showSearch(ps);
        if (key == null || key.equals("-1")) {
            /**
             * If root node is selected then display add panel only.
             */
            displayAddGroupPanel(ps);
        } else {
            displayGroupInfoPanel(ps);
        }
        ps.setValue(GROUP_ID_PARAM, new BigDecimal(key));
    }

    /**
     * Build a panel to display group basic information.
     */
    private Component buildGroupInfoPanel(final SegmentedPanel main) {
        final BoxPanel body = new BoxPanel();

        //body.add(new GroupInfo(this));
        final ColumnPanel infoPanel = new ColumnPanel(2);

        infoPanel.add(new Label(new GlobalizedMessage("ui.admin.groups.name",
                                                      BUNDLE_NAME)));
        final Label nameLabel = new Label();
        nameLabel.addPrintListener(new PrintListener() {

            @Override
            public void prepare(final PrintEvent event) {
//                final Label target = (Label) event.getTarget();
//                final PageState state = event.getPageState();
//                final Group group = getGroup(state);
//
//                target.setLabel(group.getName());
            }

        });
        infoPanel.add(nameLabel);
        body.add(infoPanel);

        ActionLink link = new ActionLink(EDIT_GROUP_LABEL);
        link.setClassAttr("actionLink");
        link.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                PageState ps = e.getPageState();
                displayEditPanel(ps);
            }

        });
        body.add(link);

        return main.addSegment(GROUP_INFORMATION_HEADER, body);

    }

    /**
     * Build group edit form.
     */
    private Component buildGroupEditPanel(final SegmentedPanel main) {
        return main.addSegment(GROUP_EDIT_HEADER, new GroupEditForm(this));
    }

    /**
     * Build panel to display direct subgroup information.
     */
    private Component buildSubGroupPanel(final SegmentedPanel main) {
        final BoxPanel body = new BoxPanel();
        final BoxPanel labelStatus = new BoxPanel(BoxPanel.HORIZONTAL);
        labelStatus.add(SUBGROUP_COUNT_LABEL);

        final Label countLabel = new Label("");
        countLabel.addPrintListener(new PrintListener() {

            @Override
            public void prepare(final PrintEvent event) {
//                final PageState ps = event.getPageState();
//
//                final Label target = (Label) event.getTarget();
//                Group g = getGroup(ps);
//                if (g != null) {
//                    target.setLabel(String.valueOf(g.countSubgroups()));
//                }
            }

        });

        final ActionLink status = new ActionLink(countLabel);
        status.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                PageState ps = event.getPageState();
                String key = (String) groupTree.getSelectedKey(ps);
                groupTree.expand(key, ps);
            }

        });
        labelStatus.add(status);

        body.add(labelStatus);

        final List subGroupList = new List(new SubGroupListModelBuilder(this));
        subGroupList.setCellRenderer(new ListCellRenderer() {

            @Override
            public Component getComponent(final List list,
                                          final PageState state,
                                          final Object value,
                                          final String key,
                                          final int index,
                                          final boolean isSelected) {
                throw new UnsupportedOperationException();
//                final BoxPanel b = new BoxPanel(BoxPanel.HORIZONTAL);
//                b.add(new Label(((Group) value).getName()));
//                final ControlLink removeLink = new ControlLink(
//                    REMOVE_SUBGROUP_LABEL);
//                removeLink.setClassAttr("actionLink");
//                b.add(removeLink);
//                return b;
            }

        });

        subGroupList.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                final PageState state = event.getPageState();
                final String key = (String) ((List) event.getSource())
                    .getSelectedKey(state);

                if (key != null) {
//                    final Long groupId = Long.parseLong(key);
//                    final CdiUtil cdiUtil = new CdiUtil();
//                    final GroupRepository groupRepository;
//                    try {
//                        groupRepository = cdiUtil
//                            .findBean(GroupRepository.class);
//                    } catch (CdiLookupException ex) {
//                        throw new UncheckedWrapperException(
//                            "Failed to lookup GroupRepository", ex);
//                    }
//
//                    final Group group = groupRepository.findById(groupId);
//                    final Group parent = getGroup(state);
//                    if (parent != null) {
//                        groupRepository.save(parent);
//                    }
//
//                    final BigDecimal groupID = new BigDecimal(key);
//                    try {
//                        final Group group = new Group(groupID);
//                        final Group parent = getGroup(state);
//                        if (parent != null) {
//                            parent.removeSubgroup(group);
//                            parent.save();
//                        }
//                    } catch (DataObjectNotFoundException exc) {
//                    }
                }
            }

        });
        body.add(subGroupList);

        final ActionLink addLink = new ActionLink(ADD_SUBGROUP_LABEL);
        addLink.setClassAttr("actionLink");
        addLink.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent event) {
                PageState ps = event.getPageState();

                displayAddGroupPanel(ps);
            }

        });

        body.add(addLink);

        // new actionlink and anonymous ActionListener class added cg
        final ActionLink addExistingLink = new ActionLink(
            ADD_EXISTING_GROUP_TO_SUBGROUPS_LABEL);
        addExistingLink.setClassAttr("actionLink");
        addExistingLink.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                LOGGER.debug("Add existing group link pressed");
                PageState ps = event.getPageState();
                displayAddExistingGroupPanel(ps);
            }

        });

        body.add(addExistingLink);
        return main.addSegment(SUBGROUP_HEADER, body);

    }

    /**
     * Build group add form.
     */
    private Component buildGroupAddPanel(final SegmentedPanel main) {

        return main.addSegment(ADD_GROUP_LABEL,
                               new GroupAddForm(groupTree, this));
    }

    private Component buildExistingGroupAddPanel(final SegmentedPanel main) {
        m_existingGroupAdd = new ExistingGroupAddPane(groupTree, this);
        return main.addSegment(ADD_EXISTING_GROUP_TO_SUBGROUPS_LABEL,
                               m_existingGroupAdd);
    }

    /**
     * Build group's member panel.
     */
    private Component buildMemberListPanel(final SegmentedPanel main) {

        BoxPanel body = new BoxPanel() {

            @Override
            public void register(final Page page) {
                page.setVisibleDefault(subMemberSearch, false);
            }

        };
//        body.add(new SubMemberPanel(this));
//
//        addSubmemberLink = new ActionLink(ADD_SUBMEMBER_LABEL);
//        addSubmemberLink.setClassAttr("actionLink");
//        addSubmemberLink.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(final ActionEvent event) {
//                PageState ps = event.getPageState();
//                addSubmemberLink.setVisible(ps, false);
//                subMemberSearch.setVisible(ps, true);
//            }
//
//        });
//
//        subMemberSearch = new SearchAndList("searchsubmember");
//        subMemberSearch.setListModel(new UserSearchAndListModel());
//        subMemberSearch.addChangeListener(new ChangeListener() {
//
//            @Override
//            public void stateChanged(final ChangeEvent event) {
//                PageState ps = event.getPageState();
//
//                String key = (String) subMemberSearch.getSelectedKey(ps);
//                if (key != null) {
//                    final BigDecimal userID = new BigDecimal(key);
//
//                    final Group group = getGroup(ps);
//
//                    if (group != null) {
//                        try {
//                            User user = User.retrieve(userID);
//                            group.addMember(user);
//                            group.save();
//                        } catch (DataObjectNotFoundException exc) {
//                            // Ignore if user id is not valid
//                        } catch (PersistenceException pexc) {
//                            // Display error message that user
//                            // already existed in group.
//                        }
//                    }
//                }
//                subMemberSearch.reset(ps);
//                subMemberSearch.setVisible(ps, false);
//                addSubmemberLink.setVisible(ps, true);
//            }
//
//        });
//
//        body.add(subMemberSearch);
//        body.add(addSubmemberLink);
        return main.addSegment(SUBMEMBER_HEADER, body);

    }

    /**
     * Build extreme action panel.
     */
    private Component buildExtremeActionPanel(final SegmentedPanel main) {
        final BoxPanel body = new BoxPanel();

        final ActionLink deleteLink = new ActionLink(DELETE_GROUP_LABEL);
        deleteLink.setClassAttr("actionLink");
        deleteLink.setConfirmation(GROUP_DELETE_CONFIRMATION);
//        deleteLink.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(final ActionEvent event) {
//
//                PageState ps = event.getPageState();

//                final Group group = (Group) requestLocalGroup.get(ps);
//                if (group != null) {
//                    final CdiUtil cdiUtil = new CdiUtil();
//                    final GroupRepository groupRepository;
//                    try {
//                        groupRepository = cdiUtil.findBean(GroupRepository.class);
//                    } catch(CdiLookupException ex) {
//                        throw new UncheckedWrapperException(ex);
//                    }
                    
//                    groupRepository.delete(group);
                    
//                    try {
//                        group.delete();
//                        groupTree.setSelectedKey(ps, "-1");
//                    } catch (PersistenceException exc) {
//                        LOGGER.warn("Error deleting subgroup", exc);
//                        displayDeleteFailedPanel(ps);
//                    }
//                }
                // Select root node

//            }
//
//        });
//        body.add(deleteLink);
//        return main.addSegment(GROUP_EXTREME_ACTIONS_HEADER,
//                               body);
        
        throw new UnsupportedOperationException();
    }

    /**
     * Build a panel to display an error message when unable to delete group.
     */
    private Component buildGroupDeleteFailedPanel(final SegmentedPanel main) {
        final ActionLink link = new ActionLink(GROUP_ACTION_CONTINUE);
        link.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent event) {
                PageState ps = event.getPageState();
                displayGroupInfoPanel(ps);
            }

        });
        link.setClassAttr("actionLink");

        final Label label = new Label(GROUP_DELETE_FAILED_MSG);
        label.setClassAttr("deleteFailedMessage");

        final BoxPanel panel = new BoxPanel();
        panel.add(label);
        panel.add(link);

        return main.addSegment(GROUP_DELETE_FAILED_HEADER, panel);
    }

    /**
     * Hides all components of the in preparation for turning selected
     * components back on.
     */
    private void hideAll(final PageState state) {
        for (int i = 0; i < panelList.size(); i++) {
            ((Component) panelList.get(i)).setVisible(state, false);
        }
    }

    /**
     * Show all components of the in preparation for turning visibility of
     * selected components off .
     */
    private void showAll(final PageState state) {
        for (int i = 0; i < panelList.size(); i++) {
            ((Component) panelList.get(i)).setVisible(state, true);
        }
    }

}

class SubGroupListModelBuilder extends LockableImpl implements ListModelBuilder {

    private final GroupAdministrationTab parent;

    public SubGroupListModelBuilder(final GroupAdministrationTab parent) {
        this.parent = parent;
    }

    public ListModel makeModel(final List list, final PageState state) {
//        final Group group = parent.getGroup(state);

//        if (group != null) {
//            return new SubGroupListModel(group.getSubgroups());
//        }

        return new SubGroupListModel(null);
    }

}

/**
 * CLASS
 *
 */
class SubGroupListModel implements ListModel {

//    private GroupCollection m_coll;

    /**
     *
     * @param collection
     */
    public SubGroupListModel(final Object collection) {
//        m_coll = collection;
//        m_coll.addOrder("lower(" + Group.DISPLAY_NAME + ") asc");
    }

    /**
     *
     * @return
     */
    public Object getElement() {
//        return m_coll.getGroup();
        return null;
    }

    /**
     *
     * @return
     */
    public String getKey() {
//        return m_coll.getID().toString();
        return null;
    }

    /**
     *
     * @return
     */
    public boolean next() {
//        if (m_coll != null) {
//            return m_coll.next();
//        }

        return false;
    }

}
