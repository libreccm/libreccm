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

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Tree;

import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;

import static com.arsdigita.ui.admin.AdminConstants.*;


/**
 * Add group form.
 *
 * @author David Dao
 * @version $Id$
 */
class GroupAddForm extends GroupForm implements FormProcessListener {

    private Tree m_groupTree;
    private GroupAdministrationTab m_groupTab;

    public GroupAddForm(final Tree groupTree,
                        final GroupAdministrationTab tab) {
        super(GROUP_FORM_ADD);
        addProcessListener(this);
        m_groupTree = groupTree;
        m_groupTab = tab;
    }

    /**
     * Processes the form.
     */
    @Override
    public void process(final FormSectionEvent event)
        throws FormProcessException {

//        PageState ps = event.getPageState();
//
//        // Get super parent group.
//        String key = (String) m_groupTree.getSelectedKey(ps);
//
//        final Group parentGroup = null;
////        if (key != null) {
////            BigDecimal parentID = new BigDecimal(key);
////
////            try {
////                parentGroup = new Group(parentID);
////            } catch (DataObjectNotFoundException exc) {
////                // Parent group does not exist.
////                // This is normal behavior with the new group
////                // been add with no parent.
////            }
////        }
//
//        final Group group = new Group();
//
//        String name = (String) m_name.getValue(ps);
//        group.setName(name);
//
//        // Workaround for bug #189720: there is no way to remove a
//        // Party's primary email address, so we set it directly to
//        // null if it's value on the form is null.
////        InternetAddress email = (InternetAddress) m_email.getValue(ps);
////        if (email != null) {
////            group.setPrimaryEmail(new EmailAddress(email.getAddress()));
////        } else {
////            //group.set("primaryEmail", null);
////            group.setPrimaryEmail(null);
////        }
//        final CdiUtil cdiUtil = new CdiUtil();
//        final GroupRepository groupRepository;

//            groupRepository = cdiUtil.findBean(GroupRepository.class);
//        groupRepository.save(group);
//
////        if (parentGroup != null) {
////            parentGroup.addSubgroup(group);
////            parentGroup.save();
////        }
//        if (m_groupTab != null) {
//            m_groupTab.setGroup(ps, group);
//        }
        
        throw new UnsupportedOperationException();
    }

}
