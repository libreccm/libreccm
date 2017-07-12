/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.libreccm.admin.ui.usersgroupsroles;

import com.arsdigita.ui.admin.AdminUiConstants;

import com.vaadin.data.HasValue;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.libreccm.admin.ui.ConfirmDiscardDialog;
import org.libreccm.security.Role;
import org.libreccm.security.RoleManager;
import org.libreccm.security.RoleRepository;

import java.util.ResourceBundle;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class RoleEditor extends Window {

    private static final long serialVersionUID = -2982855646090602847L;

    private final UsersGroupsRoles usersGroupsRoles;
    private final Role role;
    private final RoleRepository roleRepo;
    private final RoleManager roleManager;

    private boolean dataHasChanged = false;

    private TextField roleName;
    private TextArea roleDescription;

    public RoleEditor(final UsersGroupsRoles usersGroupsRoles,
                      final RoleRepository roleRepo,
                      final RoleManager roleManager) {

        super("Create new role");

        this.usersGroupsRoles = usersGroupsRoles;
        role = null;
        this.roleRepo = roleRepo;
        this.roleManager = roleManager;

        addWidgets();
    }

    public RoleEditor(final Role role,
                      final UsersGroupsRoles usersGroupsRoles,
                      final RoleRepository roleRepo,
                      final RoleManager roleManager) {

        super(String.format("Edit role %s", role.getName()));

        this.role = role;
        this.usersGroupsRoles = usersGroupsRoles;
        this.roleRepo = roleRepo;
        this.roleManager = roleManager;

        addWidgets();
    }

    private void addWidgets() {

        final ResourceBundle bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       UI.getCurrent().getLocale());

        final DataHasChangedListener dataHasChangedListener
                                         = new DataHasChangedListener();

        roleName = new TextField(bundle
            .getString("ui.admin.role_edit.rolename.label"));
        roleName.setRequiredIndicatorVisible(true);
        roleName.addValueChangeListener(dataHasChangedListener);

        roleDescription = new TextArea(bundle
            .getString("ui.admin.role_edit.roledescription.label"));
        roleDescription.addValueChangeListener(dataHasChangedListener);

        final Button submit = new Button();
        if (role == null) {
            submit.setCaption(bundle
                .getString("ui.admin.role.createpanel.header"));
        } else {
            submit.setCaption(bundle.getString("ui.admin.save"));
        }
        submit.addClickListener(event -> saveRole());

        final Button cancel = new Button(bundle.getString("ui.admin.cancel"));
        cancel.addClickListener(event -> close());

        final HorizontalLayout buttons = new HorizontalLayout(submit, cancel);

        final FormLayout formLayout = new FormLayout(roleName,
                                                     roleDescription);

        final VerticalLayout layout = new VerticalLayout(formLayout, buttons);

//        final Panel panel = new Panel(layout);
//        if (role == null) {
//            panel.setCaption(bundle
//                .getString("ui.admin.group.createpanel.header"));
//        } else {
//            panel.setCaption(bundle
//                .getString("ui.admin.group_details.edit"));
//        }
        if (role != null) {
            roleName.setValue(role.getName());
            final String description = role
                .getDescription()
                .getValue(UI.getCurrent().getLocale());
            if (description == null) {
                roleDescription.setValue("");
            } else {
                roleDescription.setValue(role
                    .getDescription()
                    .getValue(UI.getCurrent().getLocale()));
            }
        }

        setContent(layout);
        
        dataHasChanged = false;
    }

    @Override
    public void close() {

        if (dataHasChanged) {
            final ResourceBundle bundle = ResourceBundle
                .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                           UI.getCurrent().getLocale());

            final ConfirmDiscardDialog dialog = new ConfirmDiscardDialog(
                this,
                bundle.getString("ui.admin.role_edit.discard_confirm"));
            dialog.setModal(true);
            UI.getCurrent().addWindow(dialog);
        } else {
            super.close();
        }
    }

    protected void saveRole() {

        final ResourceBundle bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       UI.getCurrent().getLocale());

        boolean valid = true;

        if (roleName.getValue() == null
                || roleName.getValue().trim().isEmpty()) {

            roleName.setComponentError(new UserError(
                bundle.getString("ui.admin.role_edit.rolename.error.notempty")));
            valid = false;
        }

        if (!valid) {
            return;
        }

        final Role currentRole;
        if (role == null) {
            currentRole = new Role();

        } else {
            currentRole = role;
        }

        currentRole.setName(roleName.getValue());
        if (roleDescription.getValue() != null
                && !roleDescription.getValue().trim().isEmpty()) {
            currentRole.getDescription().addValue(UI.getCurrent().getLocale(),
                                                  roleDescription.getValue());
        }

        roleRepo.save(currentRole);

        dataHasChanged = false;
        if (usersGroupsRoles != null) {
            usersGroupsRoles.refreshRoles();
        }
        close();
    }

    private class DataHasChangedListener
        implements HasValue.ValueChangeListener<String> {

        private static final long serialVersionUID = -1410903365203533072L;

        @Override
        public void valueChange(final HasValue.ValueChangeEvent<String> event) {
            dataHasChanged = true;
        }

    }

}
