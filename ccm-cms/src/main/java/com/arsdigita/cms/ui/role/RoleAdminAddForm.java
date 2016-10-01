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
package com.arsdigita.cms.ui.role;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.cms.ui.FormSecurityListener;
import com.arsdigita.cms.ui.UserAddForm;
import com.arsdigita.util.Assert;
import org.libreccm.security.Group;
import org.libreccm.security.Role;
import org.libreccm.security.User;
import org.librecms.CmsConstants;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Michael Pih
 * @author Uday Mathur
 * @version $Id: RoleAdminAddForm.java 287 2005-02-22 00:29:02Z sskracic $
 */
class RoleAdminAddForm extends UserAddForm {

    private final SingleSelectionModel m_roles;

    private static final String NAME_FILTER = 
        "(upper(lastName) like ('%' || upper(:search) || '%'))" +
        " or " +
        "(upper(firstName) like ('%' || upper(:search) || '%'))" +
        " or " +
        "(upper(email) like ('%' || upper(:search) || '%'))";

    public RoleAdminAddForm(SingleSelectionModel roles, TextField search) {
        super(search, "RoleAddAdmin");

        m_roles = roles;

        getForm().addSubmissionListener
            (new FormSecurityListener(CmsConstants.PRIVILEGE_ADMINISTER_ROLES));
    }


    protected List<User> makeQuery(PageState s) {
        Assert.isTrue(m_roles.isSelected(s));

        /*
        Session session = SessionManager.getSession();

        // XXX: Figure out how to use role directly here
        DataQuery dq =
            session.retrieveQuery("com.arsdigita.cms.roleAdminUserSearch");

        BigDecimal roleId = new BigDecimal((String) m_roles.getSelectedKey(s));
        String searchQuery = (String) getSearchWidget().getValue(s);

        makeFilter(dq, roleId, searchQuery);
        dq.addOrder("upper(lastName), upper(firstName), upper(email)");*/
        return new LinkedList<>();
    }

    /**
     * Filters out members of the current group and users whose name or email
     * address matches the search string.
     */
    /*
    private void makeFilter(DataQuery dq, BigDecimal roleId, String search) {
        dq.setParameter("excludedRoleId", roleId);

        // Add the search filter if the search query is not null.
        if ( search != null ) {
            dq.clearFilter();
            Filter filter = dq.addFilter(NAME_FILTER);
            filter.set("search", search);
        }
    }*/

    public void process(FormSectionEvent event) throws FormProcessException {
        FormData data = event.getFormData();
        PageState state = event.getPageState();
        Assert.isTrue(m_roles.isSelected(state));

        String[] users = (String[]) data.get("users");
        /*
        if ( users != null ) {

            BigDecimal roleId =
                new BigDecimal((String) m_roles.getSelectedKey(state));

            Role role = null;
            try {
                role = new Role(roleId);
            } catch (DataObjectNotFoundException e) {
                e.printStackTrace();
                throw new FormProcessException(e);
            }

            Group group = role.getGroup();

            // Add each checked user to the role
            try {
                User user;
                for ( int i = 0; i < users.length; i++ ) {

                    user = User.retrieve(new BigDecimal(users[i]));

                    PermissionDescriptor perm =
                        new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                                 group,
                                                 user);

                    // double click protection
                    if ( !PermissionService.checkPermission(perm) ) {
                        PermissionService.grantPermission(perm);
                    }
                }
                role.save();

            } catch (DataObjectNotFoundException e) {
                e.printStackTrace();
                throw new FormProcessException(GlobalizationUtil.globalize("cms.ui.staff.cannot_add_user"));
            }

        } else {
            throw new FormProcessException(GlobalizationUtil.globalize(
                    "cms.ui.staff.no_users_were_selected"));
        }*/

        fireCompletionEvent(state);
    }
}
