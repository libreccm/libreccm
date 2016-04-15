/*
 * Copyright (C) 2016 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.ui.admin.usersgroupsroles.roles;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.PropertySheetModel;
import com.arsdigita.bebop.PropertySheetModelBuilder;
import com.arsdigita.util.LockableImpl;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class RolePropertySheetModelBuilder extends LockableImpl
    implements PropertySheetModelBuilder {

    private final ParameterSingleSelectionModel<String> selectedRoleId;

    public RolePropertySheetModelBuilder(
        final ParameterSingleSelectionModel<String> selectedRoleId) {
        this.selectedRoleId = selectedRoleId;
    }

    @Override
    public PropertySheetModel makeModel(final PropertySheet sheet,
                                        final PageState state) {
        final String roleIdStr = selectedRoleId.getSelectedKey(state);
        final Role selectedRole;
        if (roleIdStr == null || roleIdStr.isEmpty()) {
            selectedRole = null;
        } else {
            final RoleRepository roleRepository = CdiUtil.createCdiUtil()
                .findBean(RoleRepository.class);
            selectedRole = roleRepository.findById(Long.parseLong(roleIdStr));
        }
        
        return new RolePropertySheetModel(selectedRole);
    }

}
