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

import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.PageState;


import com.arsdigita.bebop.FormProcessException;

import org.libreccm.cdi.utils.CdiUtil;
//import org.libreccm.core.Group;
//import org.libreccm.core.GroupRepository;

import static com.arsdigita.ui.admin.AdminConstants.*;

/**
 * Edit group form.
 *
 * @author David Dao
 * @version $Id$
 */
class GroupEditForm extends GroupForm implements FormInitListener,
                                                 FormProcessListener {

    private GroupAdministrationTab m_parent;

    public GroupEditForm() {
        this(null);
    }

    public GroupEditForm(final GroupAdministrationTab parent) {
        super(GROUP_FORM_EDIT);
        addInitListener(this);
        addProcessListener(this);

        m_parent = parent;
    }

    /**
     * Initializes form elements by retrieving their values from the database.
     */
    @Override
    public void init(final FormSectionEvent event) {
        final PageState state = event.getPageState();
        final Long id = (Long) state.getValue(USER_ID_PARAM);

//        if (id != null) {
//            final CdiUtil cdiUtil = new CdiUtil();
//            final GroupRepository groupRepository;
//

//                groupRepository = cdiUtil.findBean(
//                    GroupRepository.class);
//
//            final Group group = groupRepository.findById(id);
//
//            m_name.setValue(state, group.getName());
//        }
    }

    /**
     * Processes the form.
     */
    @Override
    public void process(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();
        final Long id = (Long) state.getValue(GROUP_ID_PARAM);
        final CdiUtil cdiUtil = new CdiUtil();
//        final GroupRepository groupRepository;
//            groupRepository = cdiUtil.findBean(GroupRepository.class);

        if (id == null) {
            throw new FormProcessException(GlobalizationUtil.globalize(
                "ui.admin.groups.ID_is_null"));
        }

//        final Group group = groupRepository.findById(id);
//        if (group == null) {
//            throw new FormProcessException(GlobalizationUtil.globalize(
//                "ui.admin.groups.couldnt_find_specified_group"));
//        }
//        
//
//        final String name = (String) m_name.getValue(state);
//        group.setName(name);
//
//        groupRepository.save(group);
//
        if (m_parent != null) {
            m_parent.displayGroupInfoPanel(state);
        }
    }

}
