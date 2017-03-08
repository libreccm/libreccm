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

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Role;

/**
 * Provides a {@link com.arsdigita.bebop.Form} for adding {@link Role roles}.
 *
 *
 * @author Michael Pih
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 */
final class RoleAddForm extends BaseRoleForm {

    private final SingleSelectionModel<String> selectionModel;

    RoleAddForm(final SingleSelectionModel<String> selectionModel) {
        super("AddStaffRole", gz("cms.ui.role.add"));

        this.selectionModel = selectionModel;

        getRoleName().addValidationListener(new NameUniqueListener(null));

        addProcessListener(new ProcessListener());
    }

    /**
     * The {@link Role} gets saved to the database and permissions are granted
     * as needed.
     *
     * NOTE: The part about granting and revoking privileges is mostly Copy &
     * Paste from {@link RoleEditForm}. If you find any bugs or errors in this
     * code, be sure to change it there accordingly.
     */
    private class ProcessListener implements FormProcessListener {

        @Override
        public final void process(final FormSectionEvent event)
            throws FormProcessException {

            final PageState state = event.getPageState();
            final String roleName = (String) getRoleName().getValue(state);
            final String roleDesc = (String) getRoleDescription()
                .getValue(state);
            final String[] selectedPrivileges = (String[]) getPrivileges()
                .getValue(state);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final RoleAdminPaneController controller = cdiUtil.findBean(
                RoleAdminPaneController.class);
            
            final Role role = controller.addRole(roleName, 
                                                 roleDesc, 
                                                 selectedPrivileges);

            selectionModel
                .setSelectedKey(state, Long.toString(role.getRoleId()));
        }

    }

}
