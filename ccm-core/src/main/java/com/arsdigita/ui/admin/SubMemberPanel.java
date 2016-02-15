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

import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ActionEvent;

import org.libreccm.cdi.utils.CdiUtil;

import static com.arsdigita.ui.admin.AdminConstants.*;


/**
 *
 *
 * @author David Dao
 *
 */
class SubMemberPanel extends BoxPanel {

    private List m_memberList;


    private GroupAdministrationTab m_mainTab;

    public SubMemberPanel(final GroupAdministrationTab tab) {
        m_mainTab = tab;
        m_memberList = new List(new SubMemberListModelBuilder(tab));
        m_memberList.setCellRenderer(new ListCellRenderer() {
                @Override
                public Component getComponent(final List list, 
                                              final PageState state, 
                                              final Object value,
                                              final String key, 
                                              final int index, 
                                              final boolean isSelected) {
                    
                    final BoxPanel panel = new BoxPanel(BoxPanel.HORIZONTAL);

//                    Label label = new Label(((User) value).getScreenName());
//                    panel.add(label);

                    ControlLink removeLink = new ControlLink(REMOVE_SUBMEMBER_LABEL);
                    removeLink.setClassAttr("actionLink");

                    panel.add(removeLink);
                    return panel;
                }
            });
        m_memberList.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent event) {
                    
                    final PageState state = event.getPageState();
                    final String key = (String) m_memberList.getSelectedKey(state);

                    if (key != null) {
                        final Long userID = new Long(key);
                        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
//                        final UserRepository userRepository;
//                        final GroupManager groupManager;
//                        final GroupRepository groupRepository;

//                            userRepository = cdiUtil.findBean(UserRepository.class);
//                            groupManager = cdiUtil.findBean(GroupManager.class);
//                            groupRepository = cdiUtil.findBean(GroupRepository.class);
//                        
//                        final User user = userRepository.findById(userID);
//                        final Group group = m_mainTab.getGroup(state);
//                        if (group != null) {
//                            groupManager.removeUserFromGroup(user, group);
//                            groupRepository.save(group);
//                        }
                    }

                }
            });
        add(m_memberList);
    }
}

class SubMemberListModelBuilder extends LockableImpl
    implements ListModelBuilder {

    private GroupAdministrationTab m_mainTab;
    public SubMemberListModelBuilder(final GroupAdministrationTab tab) {
        m_mainTab = tab;
    }

    @Override
    public ListModel makeModel(final List list, final PageState state) {

//        final Group group = m_mainTab.getGroup(state);
//        final java.util.List<GroupMembership> members;
//        if (group == null) {
//            members = null;
//        } else {
//            members = group.getMembers();
//        }
        
//        return new SubMemberListModel(members);
        throw new UnsupportedOperationException();

    }
}

//class SubMemberListModel implements ListModel {
//
////    private final java.util.List<GroupMembership> members;
//    private int index;
//    
//
//    public SubMemberListModel(final java.util.List<GroupMembership> members) {
//        this.members = members;
//    }
//
//    @Override
//    public Object getElement() {
//        return members.get(index);
//    }
//
//    @Override
//    public String getKey() {
//        return Long.toString(members.get(index).getMembershipId());
//    }
//
//    @Override
//    public boolean next() {
//        if (index < members.size()) {
//            index++;
//            return true;
//        } else {
//            return false;
//        }
//    }
//}
