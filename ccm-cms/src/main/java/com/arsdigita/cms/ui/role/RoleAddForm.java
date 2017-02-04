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
import com.arsdigita.kernel.KernelConfig;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.LocalizedString;


import org.libreccm.security.PermissionManager;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;

/**
 * Provides a {@link com.arsdigita.bebop.Form} for adding {@link Role roles}.
 *
 
 * @author Michael Pih
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 */
final class RoleAddForm extends BaseRoleForm {

    private SingleSelectionModel m_model;

    RoleAddForm(SingleSelectionModel model) {
        super("AddStaffRole", gz("cms.ui.role.add"));

        m_model = model;

        m_name.addValidationListener(new NameUniqueListener(null));

        addProcessListener(new ProcessListener());
    }

    /**
     * The {@link Role} gets saved to the database and permissions are granted as needed.
     *
     * NOTE: The part about granting and revoking privileges is mostly Copy & Paste from {@link RoleEditForm}.
     * If you find any bugs or errors in this code, be sure to change it there accordingly.
     */
    private class ProcessListener implements FormProcessListener {
        @Override
        public final void process(final FormSectionEvent e)
                throws FormProcessException {
            final PageState state = e.getPageState();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PermissionManager permissionManager = cdiUtil.findBean(PermissionManager.class);
            final ConfigurationManager manager = cdiUtil.findBean(ConfigurationManager.class);
            final KernelConfig config = manager.findConfiguration(KernelConfig.class);
            final RoleRepository roleRepository = cdiUtil.findBean(RoleRepository.class);

            final Role role = new Role();

            role.setName((String) m_name.getValue(state));

            LocalizedString localizedDescription = role.getDescription();
            localizedDescription.addValue(config.getDefaultLocale(), (String) m_description.getValue(state));
            role.setDescription(localizedDescription);

            //We don't now if the permissions list is empty, so we have to save beforehand to not lose data.
            roleRepository.save(role);

            String[] selectedPermissions = (String[]) m_privileges.getValue(state);

            for (String s : selectedPermissions) {
                permissionManager.grantPrivilege(s, role);
            }

            m_model.setSelectedKey(state, Long.toString(role.getRoleId()));
        }
    }
}
