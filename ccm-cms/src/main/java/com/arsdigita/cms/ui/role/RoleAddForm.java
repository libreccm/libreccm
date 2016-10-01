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
import com.arsdigita.cms.CMS;
import com.arsdigita.kernel.KernelConfig;
import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.security.*;
import org.librecms.contentsection.ContentSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * For more detailed information see {@link com.arsdigita.bebop.Form}.
 *
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 * @author Michael Pih
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: RoleAddForm.java 287 2005-02-22 00:29:02Z sskracic $
 */
final class RoleAddForm extends BaseRoleForm {

    private static final Logger s_log = Logger.getLogger(RoleAddForm.class);

    private SingleSelectionModel m_model;
    private final boolean m_useViewersGroup;

    public RoleAddForm(SingleSelectionModel model, boolean useViewersGroup) {
        super("AddStaffRole", gz("cms.ui.role.add"), useViewersGroup);

        m_model = model;
        m_useViewersGroup = useViewersGroup;

        m_name.addValidationListener(new NameUniqueListener(null));

        addProcessListener(new ProcessListener());
    }

    private class ProcessListener implements FormProcessListener {
        public final void process(final FormSectionEvent e)
                throws FormProcessException {
            final PageState state = e.getPageState();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PermissionManager permissionManager = cdiUtil.findBean(PermissionManager.class);
            final ConfigurationManager manager = cdiUtil.findBean(ConfigurationManager.class);
            final KernelConfig config = manager.findConfiguration(KernelConfig.class);

            final Role role = new Role();

            role.setName((String) m_name.getValue(state));

            LocalizedString localizedDescription = role.getDescription();
            localizedDescription.addValue(config.getDefaultLocale(), (String) m_description.getValue(state));
            role.setDescription(localizedDescription);

            List<Permission> newPermissions = new ArrayList<>();
            String[] selectedPermissions = (String[]) m_privileges.getValue(state);

            for (Permission p : role.getPermissions()) {
                if (Arrays.stream(selectedPermissions).anyMatch(x -> x.equals(p.getGrantedPrivilege()))) {
                    newPermissions.add(p);
                } else {
                    permissionManager.revokePrivilege(p.getGrantedPrivilege(), role);
                }
            }

            for (String s : selectedPermissions) {
                if (newPermissions.stream().noneMatch(x -> x.getGrantedPrivilege().equals(s))) {
                    permissionManager.grantPrivilege(s, role);
                }
            }

            m_model.setSelectedKey(state, Long.toString(role.getRoleId()));
        }
    }
}
