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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ui.BaseForm;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.admin.GlobalizationUtil;
import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionManager;
import org.libreccm.security.Role;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.privileges.AdminPrivileges;
import org.librecms.contentsection.privileges.AssetPrivileges;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

/**
 * For more detailed information see {@link com.arsdigita.bebop.Form}.
 *
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 * @author Justin Ross &lt;jross@redhat.com&gt;
 *
 */
class BaseRoleForm extends BaseForm {

    private final Name roleName;
    private final Description roleDescription;
    private CheckboxGroup privileges;

    BaseRoleForm(final String key,
                 final GlobalizedMessage message) {
        super(key, message);

        roleName = new Name("label", 200, true);
        addField(gz("cms.ui.role.name"), roleName);

        roleDescription = new Description("description", 4000, false);
        addField(gz("cms.ui.role.description"), roleDescription);

        privileges = new CheckboxGroup("privileges");
        addField(gz("cms.ui.role.privileges"), privileges);

        try {
            privileges.addPrintListener(new PrivilegePrinter());
        } catch (TooManyListenersException tmle) {
            throw new UncheckedWrapperException(tmle);
        }

        addAction(new Finish());
        addAction(new Cancel());

        addSecurityListener(AdminPrivileges.ADMINISTER_ROLES);
    }

    protected Name getRoleName() {
        return roleName;
    }

    protected Description getRoleDescription() {
        return roleDescription;
    }

    protected CheckboxGroup getPrivileges() {
        return privileges;
    }

    private class PrivilegePrinter implements PrintListener {

        @Override
        public final void prepare(final PrintEvent event) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PermissionManager permissionManager = cdiUtil.findBean(
                PermissionManager.class);

            final CheckboxGroup target = (CheckboxGroup) event.getTarget();
            target.clearOptions();

            final List<String> adminPrivileges = permissionManager
                .listDefiniedPrivileges(AdminPrivileges.class);
            final List<String> itemPrivileges = permissionManager
                .listDefiniedPrivileges(ItemPrivileges.class);
            final List<String> assetPrivileges = permissionManager
                .listDefiniedPrivileges(AssetPrivileges.class);

            final List<String> possiblePrivileges = new ArrayList<>();
            possiblePrivileges.addAll(adminPrivileges);
            possiblePrivileges.addAll(itemPrivileges);
            possiblePrivileges.addAll(assetPrivileges);

            for (final String privilege : possiblePrivileges) {
                target.addOption(new Option(
                    privilege,
                    new Label(new GlobalizedMessage(privilege,
                                                    CmsConstants.CMS_BUNDLE))));
            }
        }

    }

    class NameUniqueListener implements ParameterListener {

        private final RoleRequestLocal roleRequestLocal;

        NameUniqueListener(final RoleRequestLocal role) {
            roleRequestLocal = role;
        }

        /**
         * Validates that there are no duplicates between the names of roles.
         */
        @Override
        public final void validate(final ParameterEvent event)
            throws FormProcessException {

            final PageState state = event.getPageState();
            final String name = (String) roleName.getValue(state);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final RoleAdminPaneController controller = cdiUtil.findBean(
                RoleAdminPaneController.class);
            final Role selectedRole;
            if (roleRequestLocal == null) {
                selectedRole = null;
            } else {
                selectedRole = roleRequestLocal.getRole(state);
            }
            
            if (!controller.validateRoleNameUniqueness(name, selectedRole)) {
                    throw new FormProcessException(GlobalizationUtil.globalize(
                        "cms.ui.role.name_not_unique"));
            }
        }

    }

}
