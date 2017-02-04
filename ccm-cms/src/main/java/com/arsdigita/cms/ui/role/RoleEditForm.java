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
import com.arsdigita.kernel.KernelConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

/**
 * Represents a {@link com.arsdigita.bebop.Form Form} to edit
 * {@link Role roles}.
 *
 *
 * @author Michael Pih
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 */
final class RoleEditForm extends BaseRoleForm {

    private static final Logger LOGGER = LogManager
        .getLogger(RoleEditForm.class);

    private final RoleRequestLocal m_role;

    RoleEditForm(RoleRequestLocal role) {
        super("EditStaffRole", gz("cms.ui.role.edit"));

        m_role = role;

        m_name.addValidationListener(new NameUniqueListener(m_role));

        addInitListener(new InitListener());
        addProcessListener(new ProcessListener());
    }

    /**
     * Sets the initial values of a {@link Role} which were received from the
     * database.
     */
    private class InitListener implements FormInitListener {

        @Override
        public final void init(final FormSectionEvent e) {
            final PageState state = e.getPageState();
            final Role role = m_role.getRole(state);

            m_name.setValue(state, role.getName());
            m_description.setValue(state, role.getDescription());

            final String[] permissions = role.getPermissions().stream().
                map(Permission::getGrantedPrivilege).toArray(String[]::new);

            m_privileges.setValue(state, permissions);
        }

    }

    /**
     * Updates a role and it's permissions. It uses the
     * {@link PermissionManager} to grant and revoke permissions as needed.
     *
     * NOTE: The part about granting and revoking privileges is mostly identical
     * to {@link RoleAddForm}. If you find any bugs or errors in this code, be
     * sure to change it there accordingly.
     */
    private class ProcessListener implements FormProcessListener {

        @Override
        public final void process(final FormSectionEvent e)
            throws FormProcessException {
            final PageState state = e.getPageState();

            final Role role = m_role.getRole(state);
            role.setName((String) m_name.getValue(state));

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PermissionManager permissionManager = cdiUtil.findBean(
                PermissionManager.class);
            final ConfigurationManager manager = cdiUtil.findBean(
                ConfigurationManager.class);
            final KernelConfig config = manager.findConfiguration(
                KernelConfig.class);
            final RoleRepository roleRepository = cdiUtil.findBean(
                RoleRepository.class);

            LocalizedString localizedDescription = role.getDescription();
            localizedDescription.addValue(config.getDefaultLocale(),
                                          (String) m_description.getValue(state));
            role.setDescription(localizedDescription);

            //We don't now if the permissions list is empty, so we have to save beforehand to not lose data.
            roleRepository.save(role);

            List<Permission> newPermissions = new ArrayList<>();
            String[] selectedPermissions = (String[]) m_privileges.getValue(
                state);

            for (Permission p : role.getPermissions()) {
                if (Arrays.stream(selectedPermissions).anyMatch(x -> x.equals(p
                    .getGrantedPrivilege()))) {
                    newPermissions.add(p);
                } else {
                    permissionManager.revokePrivilege(p.getGrantedPrivilege(),
                                                      role);
                }
            }

            for (String s : selectedPermissions) {
                if (newPermissions.stream().noneMatch(x -> x
                    .getGrantedPrivilege().equals(s))) {
                    permissionManager.grantPrivilege(s, role);
                }
            }
        }

    }

}
