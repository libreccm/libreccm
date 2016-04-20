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

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 * Form for creating new roles and editing existing roles. If a role
 * is selected the form is populated with the values of an existing role. When
 * the form is submitted either a new role is created or an existing group
 * is updated.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class RoleForm extends Form {

    private static final String ROLE_NAME = "rolename";

    private final TextField roleName;
    private final SaveCancelSection saveCancelSection;

    public RoleForm(
        final RoleAdmin roleAdmin,
        final ParameterSingleSelectionModel<String> selectedRoleId) {

        super("roleform");

        final Label heading = new Label(e -> {
            final PageState state = e.getPageState();

            final Label target = (Label) e.getTarget();

            final String selectedRoleIdStr = selectedRoleId.getSelectedKey(
                state);
            if (selectedRoleIdStr == null || selectedRoleIdStr.isEmpty()) {
                target.setLabel(
                    new GlobalizedMessage("ui.admin.role.create_new",
                                          ADMIN_BUNDLE));
            } else {
                target.setLabel(new GlobalizedMessage("ui.admin.role.edit",
                                                      ADMIN_BUNDLE));
            }
        });
        heading.setClassAttr("heading");
        add(heading);

        roleName = new TextField(ROLE_NAME);
        roleName.setLabel(new GlobalizedMessage("ui.admin.role_name",
                                                ADMIN_BUNDLE));
        add(roleName);

        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);

        addValidationListener(e -> {
            final PageState state = e.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final FormData data = e.getFormData();

                final String roleNameData = data.getString(ROLE_NAME);

                if (roleNameData == null || roleNameData.isEmpty()) {
                    data.addError(ROLE_NAME, new GlobalizedMessage(
                                  "ui.admin.role.name.error.notempty",
                                  ADMIN_BUNDLE));
                    return;
                }

                if (roleNameData.length() > 256) {
                    data.addError(ROLE_NAME, new GlobalizedMessage(
                                  "ui.admin.role.name.error.length",
                                  ADMIN_BUNDLE));
                    return;
                }

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final RoleRepository roleRepository = cdiUtil.findBean(
                    RoleRepository.class);
                if (roleRepository.findByName(roleNameData) != null) {
                    data.addError(ROLE_NAME, new GlobalizedMessage(
                                  "ui.admin.role.error.name_already_in_use",
                                  ADMIN_BUNDLE));
                }
            }
        });

        addInitListener(e -> {
            final PageState state = e.getPageState();

            final String selectedRoleIdStr = selectedRoleId
                .getSelectedKey(state);

            if (selectedRoleIdStr != null && !selectedRoleIdStr.isEmpty()) {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final RoleRepository roleRepository = cdiUtil.findBean(
                    RoleRepository.class);

                final Role role = roleRepository.findById(Long.parseLong(
                    selectedRoleIdStr));
                roleName.setValue(state, role.getName());
            }
        });

        addProcessListener(e -> {
            final PageState state = e.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final FormData data = e.getFormData();
                final String roleNameData = data.getString(ROLE_NAME);

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final RoleRepository roleRepository = cdiUtil.findBean(
                    RoleRepository.class);

                final String selectedRoleIdStr = selectedRoleId.getSelectedKey(
                    state);
                if (selectedRoleIdStr == null || selectedRoleIdStr.isEmpty()) {
                    final Role role = new Role();
                    role.setName(roleNameData);

                    roleRepository.save(role);
                } else {
                    final Role role = roleRepository.findById(Long.parseLong(
                        selectedRoleIdStr));
                    role.setName(roleNameData);
                    
                    roleRepository.save(role);
                }
            }

            roleAdmin.hideRoleForm(state);
        });
    }

}
