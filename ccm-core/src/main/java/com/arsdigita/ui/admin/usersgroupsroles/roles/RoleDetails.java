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
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class RoleDetails extends BoxPanel {

    public RoleDetails(
        final RoleAdmin roleAdmin,
        final ParameterSingleSelectionModel<String> selectedRoleId) {

        super(BoxPanel.VERTICAL);

        final ActionLink backLink = new ActionLink(new GlobalizedMessage(
            "ui.admin.role_details.back", ADMIN_BUNDLE));
        backLink.setClassAttr("back-link");
        backLink.addActionListener(e -> {
            roleAdmin.hideRoleDetails(e.getPageState());
        });
        add(backLink);

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
                "ui.admin.role_details.heading",
                ADMIN_BUNDLE,
                new String[]{role.getName()}));
        });
        add(heading);

        final PropertySheet propertySheet = new PropertySheet(
            new RolePropertySheetModelBuilder(selectedRoleId));
        add(propertySheet);

        final BoxPanel links = new BoxPanel(BoxPanel.HORIZONTAL);
        
        final ActionLink editProperties = new ActionLink(new GlobalizedMessage(
        "ui.admin.role_details.edit_properties", ADMIN_BUNDLE));
        editProperties.addActionListener(e -> {
            roleAdmin.showRoleForm(e.getPageState());
        });
        links.add(editProperties);
        
        final ActionLink manageMembers = new ActionLink(new GlobalizedMessage(
            "ui.admin.role_details.manage_members", ADMIN_BUNDLE));
        manageMembers.addActionListener(e -> {
            roleAdmin.showRoleMembersPanel(e.getPageState());
        });
        links.add(manageMembers);
        
          
        final ActionLink managePermissions = new ActionLink(new GlobalizedMessage(
            "ui.admin.role_details.manage_permissions", ADMIN_BUNDLE));
        managePermissions.addActionListener(e -> {
            roleAdmin.showRolePermissionsPanel(e.getPageState());
        });
        links.add(managePermissions);
        
        add(links);
    }

}
