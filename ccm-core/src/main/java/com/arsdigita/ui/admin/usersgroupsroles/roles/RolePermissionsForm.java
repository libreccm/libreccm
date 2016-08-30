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

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.globalization.GlobalizedMessage;
import java.util.Optional;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.libreccm.core.CcmObjectRepository;
import org.libreccm.security.PermissionManager;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 * A basic form for add permissions to role. Usually the applications should
 * provide better and more comfortable forms for editing their specific
 * permissions. This form is provided to give the administrator the possibility
 * to manage permissions in a generic way.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class RolePermissionsForm extends Form {

    private static final String OBJECT_ID = "objectId";
    private static final String PRIVILEGE = "privilege";

    private final TextField objectId;
    private final TextField privilege;
    private final SaveCancelSection saveCancelSection;

    public RolePermissionsForm(
            final RoleAdmin roleAdmin,
            final ParameterSingleSelectionModel<String> selectedRoleId) {

        super("rolePermissionsForm");

        final ActionLink backToRole = new ActionLink(new GlobalizedMessage(
                "ui.admin.group_details.add_permission.back",
                ADMIN_BUNDLE));
        backToRole.addActionListener(e -> {
            roleAdmin.showRolePermissionsPanel(e.getPageState());
        });
        add(backToRole);

        final Label heading = new Label();
        heading.setClassAttr("heading");
        heading.addPrintListener(e -> {
            final PageState state = e.getPageState();
            final Label target = (Label) e.getTarget();

            final RoleRepository roleRepository = CdiUtil.createCdiUtil()
                    .findBean(RoleRepository.class);
            final Role role = roleRepository.findById(Long.parseLong(
                    selectedRoleId.getSelectedKey(state)));

            target.setLabel(new GlobalizedMessage(
                    "ui.admin.role_details.add_permission.heading",
                    ADMIN_BUNDLE,
                    new String[]{role.getName()}));
        });
        add(heading);

        objectId = new TextField(OBJECT_ID);
        objectId.setLabel(new GlobalizedMessage(
                "ui.admin.role_details.add_permission.object_id.label",
                ADMIN_BUNDLE));
        objectId.setHint(new GlobalizedMessage(
                "ui.admin.role_details.add_permission.object_id.hint",
                ADMIN_BUNDLE
        ));
        add(objectId);

        privilege = new TextField(PRIVILEGE);
        privilege.setLabel(new GlobalizedMessage(
                "ui.admin.role_details.add_permission.privilege.label",
                ADMIN_BUNDLE));
        privilege.setHint(new GlobalizedMessage(
                "ui.admin.role_details.add_permission.privilege.hint",
                ADMIN_BUNDLE
        ));
        add(privilege);

        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);

        addValidationListener(event -> {
            final PageState state = event.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final FormData data = event.getFormData();

                final String privilegeData = data.getString(PRIVILEGE);
                if (privilegeData == null || privilegeData.isEmpty()) {
                    data.addError(PRIVILEGE, new GlobalizedMessage(
                                  "ui.admin.role_details.add_permission."
                                          + "privilege.error.notempty",
                                  ADMIN_BUNDLE));
                }

                final String objectIdData = data.getString(OBJECT_ID);
                if (objectIdData != null && !objectIdData.isEmpty()) {
                    final CcmObjectRepository objectRepository = CdiUtil
                            .createCdiUtil().findBean(CcmObjectRepository.class);

                    try {
                        Long.parseLong(objectIdData);
                    } catch (NumberFormatException ex) {
                        data.addError(
                                OBJECT_ID,
                                new GlobalizedMessage(
                                        "ui.admin.role_details.add_permission.object_id"
                                        + ".error.nan",
                                        ADMIN_BUNDLE));
                        return;
                    }

                    final Optional<CcmObject> object = objectRepository.
                            findObjectById(
                                    Long.parseLong(objectIdData));
                    if (!object.isPresent()) {
                        data.addError(
                                OBJECT_ID,
                                new GlobalizedMessage(
                                        "ui.admin.role_details.add_permission.object_id"
                                        + ".error.no_object",
                                        ADMIN_BUNDLE));
                    }
                }
            }
        });

        addProcessListener(event -> {
            final PageState state = event.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final FormData data = event.getFormData();

                final String privilegeData = data.getString(PRIVILEGE);
                final String objectIdData = data.getString(OBJECT_ID);

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final RoleRepository roleRepository = cdiUtil.findBean(
                        RoleRepository.class);

                final Role role = roleRepository.findById(Long.parseLong(
                        selectedRoleId.getSelectedKey(state)));
                final PermissionManager permissionManager = cdiUtil.findBean(
                        PermissionManager.class);
                if (objectIdData == null || objectIdData.isEmpty()) {
                    permissionManager.grantPrivilege(privilegeData, role);
                } else {
                    final CcmObjectRepository objectRepository = cdiUtil
                            .findBean(CcmObjectRepository.class);
                    final Optional<CcmObject> object = objectRepository
                            .findObjectById(Long.parseLong(objectIdData));
                    permissionManager.grantPrivilege(privilegeData,
                                                     role,
                                                     object.get());
                }
            }

            roleAdmin.showRolePermissionsPanel(state);
        });
    }

}
