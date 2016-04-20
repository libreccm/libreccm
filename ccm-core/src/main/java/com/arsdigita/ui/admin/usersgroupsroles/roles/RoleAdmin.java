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
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.globalization.GlobalizedMessage;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 * UI for managing roles.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class RoleAdmin extends BoxPanel {

    private final StringParameter roleIdParameter;
    private final ParameterSingleSelectionModel<String> selectedRoleId;
    private final BoxPanel rolesTablePanel;
    private final RoleForm roleForm;
    private final RoleDetails roleDetails;
    private final BoxPanel roleMembersPanel;
    private final RoleAddMemberForm roleAddMemberForm;
    private final BoxPanel rolePermissionsPanel;
    private final RolePermissionsForm rolePermissionsForm;

    public RoleAdmin() {
        super(BoxPanel.VERTICAL);

        setIdAttr("roleAdmin");

        roleIdParameter = new StringParameter("selected_role_id");
        selectedRoleId = new ParameterSingleSelectionModel<>(roleIdParameter);

        rolesTablePanel = new BoxPanel(BoxPanel.VERTICAL);
        rolesTablePanel.setIdAttr("rolesTablePanel");

        final Form filterForm = new Form("rolesTableFilter");
        final TextField rolesTableFilter = new TextField("rolesTableFilter");
        rolesTableFilter.setLabel(new GlobalizedMessage(
            "ui.admin.roles.table.filter.term", ADMIN_BUNDLE));
        filterForm.add(rolesTableFilter);
        filterForm.add(new Submit(new GlobalizedMessage(
            "ui.admin.roles.table.filter.submit", ADMIN_BUNDLE)));
        final ActionLink clearLink = new ActionLink(new GlobalizedMessage(
            "ui.admin.roles.table.filter.clear", ADMIN_BUNDLE));
        clearLink.addActionListener(e -> {
            final PageState state = e.getPageState();
            rolesTableFilter.setValue(state, null);
        });
        filterForm.add(clearLink);
        rolesTablePanel.add(filterForm);

        final RolesTable rolesTable = new RolesTable(this,
                                                     rolesTableFilter,
                                                     selectedRoleId);
        rolesTablePanel.add(rolesTable);

        final ActionLink addNewRole = new ActionLink(new GlobalizedMessage(
            "ui.admin.new_role_link", ADMIN_BUNDLE));
        addNewRole.addActionListener(e -> {
            showRoleForm(e.getPageState());
        });
        rolesTablePanel.add(addNewRole);

        add(rolesTablePanel);

        roleForm = new RoleForm(this, selectedRoleId);
        add(roleForm);

        roleDetails = new RoleDetails(this, selectedRoleId);
        add(roleDetails);

        roleMembersPanel = new BoxPanel(BoxPanel.VERTICAL);
        final Label roleMembersHeading = new Label(new GlobalizedMessage(
            "ui.admin.role_members.heading",
            ADMIN_BUNDLE));
        roleMembersHeading.setClassAttr("heading");
        roleMembersPanel.add(roleMembersHeading);
        roleMembersPanel.add(new RoleMembersTable(selectedRoleId));
        final ActionLink addRoleMember = new ActionLink(new GlobalizedMessage(
            "ui.admin.role_members.add", ADMIN_BUNDLE));
        addRoleMember.addActionListener(e -> {
            showRoleMemberAddForm(e.getPageState());
        });
        roleMembersPanel.add(addRoleMember);
        add(roleMembersPanel);

        roleAddMemberForm = new RoleAddMemberForm(this, selectedRoleId);
        add(roleAddMemberForm);

        rolePermissionsPanel = new BoxPanel(BoxPanel.VERTICAL);
        final Label rolePermissionsHeading = new Label(new GlobalizedMessage(
            "ui.admin.role_permissions.heading",
            ADMIN_BUNDLE));
        rolePermissionsHeading.setClassAttr("heading");
        rolePermissionsPanel.add(rolePermissionsHeading);
        rolePermissionsPanel.add(new Label(new GlobalizedMessage(
            "ui.admin.role_permissions.note", ADMIN_BUNDLE)));
        rolePermissionsPanel.add(new RolePermissionsTable(selectedRoleId));
        final ActionLink addRolePermission = new ActionLink(
            new GlobalizedMessage(
                "ui.admin.role_permissions.add_permission", ADMIN_BUNDLE));
        addRolePermission.addActionListener(e -> {
            showRolePermissionAddForm(e.getPageState());
        });
        rolePermissionsPanel.add(addRolePermission);
        add(rolePermissionsPanel);
        
        rolePermissionsForm = new RolePermissionsForm(this, selectedRoleId);
        add(rolePermissionsForm);
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.addGlobalStateParam(roleIdParameter);

        page.setVisibleDefault(rolesTablePanel, true);
        page.setVisibleDefault(roleForm, false);
        page.setVisibleDefault(roleDetails, false);
        page.setVisibleDefault(roleMembersPanel, false);
        page.setVisibleDefault(roleAddMemberForm, false);
        page.setVisibleDefault(rolePermissionsPanel, false);
        page.setVisibleDefault(rolePermissionsForm, false);
    }

    protected void showRoleDetails(final PageState state) {
        rolesTablePanel.setVisible(state, false);
        roleForm.setVisible(state, false);
        roleDetails.setVisible(state, true);
        roleMembersPanel.setVisible(state, true);
        roleAddMemberForm.setVisible(state, false);
        rolePermissionsPanel.setVisible(state, false);
        rolePermissionsForm.setVisible(state, false);
    }

    protected void hideRoleDetails(final PageState state) {
        selectedRoleId.clearSelection(state);

        rolesTablePanel.setVisible(state, true);
        roleForm.setVisible(state, false);
        roleDetails.setVisible(state, false);
        roleMembersPanel.setVisible(state, false);
        roleAddMemberForm.setVisible(state, false);
        rolePermissionsPanel.setVisible(state, false);
        rolePermissionsForm.setVisible(state, false);
    }

    protected void showRoleForm(final PageState state) {
        rolesTablePanel.setVisible(state, false);
        roleForm.setVisible(state, true);
        roleDetails.setVisible(state, false);
        roleMembersPanel.setVisible(state, false);
        roleAddMemberForm.setVisible(state, false);
        rolePermissionsPanel.setVisible(state, false);
        rolePermissionsForm.setVisible(state, false);
    }

    protected void hideRoleForm(final PageState state) {
        //We want to show the roles table if no role is selected and the
        //role details if a role is selected.
        boolean roleSelected = selectedRoleId.isSelected(state);

        rolesTablePanel.setVisible(state, !roleSelected);
        roleForm.setVisible(state, false);
        roleDetails.setVisible(state, roleSelected);
        roleMembersPanel.setVisible(state, roleSelected);
        roleAddMemberForm.setVisible(state, false);
        rolePermissionsPanel.setVisible(state, false);
        rolePermissionsForm.setVisible(state, false);
    }

    protected void showRoleMemberAddForm(final PageState state) {
        rolesTablePanel.setVisible(state, false);
        roleForm.setVisible(state, false);
        roleDetails.setVisible(state, false);
        roleMembersPanel.setVisible(state, false);
        roleAddMemberForm.setVisible(state, true);
        rolePermissionsPanel.setVisible(state, false);
        rolePermissionsForm.setVisible(state, false);
    }

    protected void showRoleMembersPanel(final PageState state) {
        rolesTablePanel.setVisible(state, false);
        roleForm.setVisible(state, false);
        roleDetails.setVisible(state, true);
        roleMembersPanel.setVisible(state, true);
        roleAddMemberForm.setVisible(state, false);
        rolePermissionsPanel.setVisible(state, false);
        rolePermissionsForm.setVisible(state, false);
    }

    protected void hideRoleMemberAddForm(final PageState state) {
        rolesTablePanel.setVisible(state, false);
        roleForm.setVisible(state, false);
        roleDetails.setVisible(state, true);
        roleMembersPanel.setVisible(state, true);
        roleAddMemberForm.setVisible(state, false);
        rolePermissionsPanel.setVisible(state, false);
        rolePermissionsForm.setVisible(state, false);
    }

    protected void showRolePermissionsPanel(final PageState state) {
        rolesTablePanel.setVisible(state, false);
        roleForm.setVisible(state, false);
        roleDetails.setVisible(state, true);
        roleMembersPanel.setVisible(state, false);
        roleAddMemberForm.setVisible(state, false);
        rolePermissionsPanel.setVisible(state, true);
        rolePermissionsForm.setVisible(state, false);
    }

//    protected void hideRolePermissionsPanel(final PageState state) {
//        rolesTablePanel.setVisible(state, false);
//        roleForm.setVisible(state, false);
//        roleDetails.setVisible(state, true);
//        roleMembersPanel.setVisible(state, true);
//        roleAddMemberForm.setVisible(state, false);
//        rolePermissionsPanel.setVisible(state, false);
//    }
    protected void showRolePermissionAddForm(final PageState state) {
         rolesTablePanel.setVisible(state, false);
        roleForm.setVisible(state, false);
        roleDetails.setVisible(state, false);
        roleMembersPanel.setVisible(state, false);
        roleAddMemberForm.setVisible(state, false);
        rolePermissionsPanel.setVisible(state, false);
        rolePermissionsForm.setVisible(state, true);
    }

    protected void hideRolePermissionAddForm(final PageState state) {
         rolesTablePanel.setVisible(state, false);
        roleForm.setVisible(state, false);
        roleDetails.setVisible(state, true);
        roleMembersPanel.setVisible(state, false);
        roleAddMemberForm.setVisible(state, false);
        rolePermissionsPanel.setVisible(state, true);
        rolePermissionsForm.setVisible(state, false);
    }

}
