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
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.CMS;
import com.arsdigita.kernel.KernelConfig;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.security.Permission;
import org.libreccm.security.PermissionManager;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Represents a {@link com.arsdigita.bebop.Form Form} to edit
 * {@link Role roles}.
 *
 *
 * @author Michael Pih
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
final class RoleEditForm extends BaseRoleForm {

    private final RoleRequestLocal roleRequestLocal;

    RoleEditForm(final RoleRequestLocal role) {
        super("EditStaffRole", gz("cms.ui.role.edit"));

        roleRequestLocal = role;

        getRoleName().addValidationListener(new NameUniqueListener(
            roleRequestLocal));

        addInitListener(new InitListener());
        addProcessListener(new ProcessListener());
    }

    /**
     * Sets the initial values of a {@link Role} which were received from the
     * database.
     */
    private class InitListener implements FormInitListener {

        @Override
        public final void init(final FormSectionEvent event) {
            final PageState state = event.getPageState();
            final Role role = roleRequestLocal.getRole(state);

            final KernelConfig kernelConfig = KernelConfig.getConfig();
            final Locale defaultLocale = kernelConfig.getDefaultLocale();

            getRoleName().setValue(state, role.getName());
            getRoleDescription().setValue(
                state,
                role.getDescription().getValue(defaultLocale));

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final RoleAdminPaneController controller = cdiUtil.findBean(
                RoleAdminPaneController.class);

            final String[] permissions = controller.getGrantedPrivileges(
                role, CMS.getContext().getContentSection());

            getPrivileges().setValue(state, permissions);
        }

    }

    /**
     * Updates a role and it's permissions. It uses the
     * {@link PermissionManager} to grant and revoke permissions as needed.
     *
     */
    private class ProcessListener implements FormProcessListener {

        @Override
        public final void process(final FormSectionEvent event)
            throws FormProcessException {

            final PageState state = event.getPageState();
            final String roleName = (String) getRoleName().getValue(state);
            final String roleDesc = (String) getRoleDescription()
                .getValue(state);
            final String[] selectedPermissions = (String[]) getPrivileges()
                .getValue(state);
            final Role role = roleRequestLocal.getRole(state);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final RoleAdminPaneController controller = cdiUtil.findBean(
                RoleAdminPaneController.class);

            controller.saveRole(role, roleName, roleDesc, selectedPermissions);
        }

    }

}
