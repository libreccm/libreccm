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
package org.libreccm.admin.ui;

import com.arsdigita.ui.admin.AdminUiConstants;

import com.vaadin.data.HasValue;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.libreccm.admin.ui.ConfirmDiscardDialog;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Group;
import org.libreccm.security.GroupRepository;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;
import org.libreccm.security.User;
import org.libreccm.security.UserRepository;

import java.util.ResourceBundle;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class GroupDetails extends Window {

    private static final long serialVersionUID = 4252189590984878037L;

    private static final String COL_USER_NAME = "username";
    private static final String COL_GIVEN_NAME = "given_name";
    private static final String COL_FAMILY_NAME = "family_name";
    private static final String COL_EMAIL = "email";
    private static final String COL_REMOVE = "remove";

    private static final String COL_ROLE_NAME = "role_name";
    private static final String COL_ROLE_REMOVE = "remove";

    private final UsersGroupsRolesTab usersGroupsRoles;
    private final Group group;
    private final GroupRepository groupRepo;

    private boolean dataHasChanged = false;

    private Label groupNameDisplay;
    private TextField groupNameField;
    private Button editButton;
    private HorizontalLayout saveCancelButtons;

    public GroupDetails(final Group group,
                        final UsersGroupsRolesTab usersGroupsRoles,
                        final GroupRepository groupRepo) {

        super(String.format("Edit group %s", group.getName()));

        this.group = group;
        this.usersGroupsRoles = usersGroupsRoles;
        this.groupRepo = groupRepo;

        addWidgets();
    }

    private void addWidgets() {

        final ResourceBundle bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       UI.getCurrent().getLocale());

        final GroupDetails.DataHasChangedListener dataHasChangedListener
                                                      = new GroupDetails.DataHasChangedListener();

        groupNameDisplay = new Label();
        groupNameDisplay.setCaption(bundle
            .getString("ui.admin.group_edit.groupname.label"));

        groupNameField = new TextField(bundle
            .getString("ui.admin.group_edit.groupname.label"));
        groupNameField.setRequiredIndicatorVisible(true);

        editButton = new Button("ui.admin.group.edit");
        editButton.addClickListener(event -> {
            groupNameDisplay.setVisible(false);
            groupNameField.setVisible(true);
            editButton.setVisible(false);
            saveCancelButtons.setVisible(true);
        });

        final Button submit = new Button();
        submit.setCaption(bundle.getString("ui.admin.save"));
        submit.addClickListener(event -> saveGroup());

        final Button cancel = new Button(bundle.getString("ui.admin.cancel"));
        cancel.addClickListener(event -> {
            groupNameField.setValue(group.getName());
            groupNameField.setVisible(false);
            saveCancelButtons.setVisible(false);
            editButton.setVisible(true);
            groupNameDisplay.setVisible(true);
        });

        saveCancelButtons = new HorizontalLayout(submit, cancel);

        final FormLayout formLayout = new FormLayout(groupNameDisplay,
                                                     groupNameField);
        groupNameField.setVisible(false);

        groupNameField.addValueChangeListener(dataHasChangedListener);

        final VerticalLayout layout = new VerticalLayout(formLayout,
                                                         editButton,
                                                         saveCancelButtons);
        saveCancelButtons.setVisible(false);

        final Panel propertiesPanel = new Panel(layout);
        propertiesPanel.setCaption(bundle
            .getString("ui.admin.group_details.edit"));

        groupNameDisplay.setValue(group.getName());
        groupNameField.setValue(group.getName());

        dataHasChanged = false;

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();

        final GroupMembersController membersController = cdiUtil
            .findBean(GroupMembersController.class);

        final Grid<User> membersGrid = new Grid<>();
        membersGrid.addColumn(User::getName)
            .setId(COL_USER_NAME)
            .setCaption("User Name");
        membersGrid.addColumn(User::getGivenName)
            .setId(COL_GIVEN_NAME)
            .setCaption("Given name");
        membersGrid.addColumn(User::getFamilyName)
            .setId(COL_FAMILY_NAME)
            .setCaption("Family name");
        membersGrid
            .addColumn(user -> user.getPrimaryEmailAddress().getAddress())
            .setId(COL_EMAIL)
            .setCaption("E-Mail");
        membersGrid.addColumn(user -> bundle.getString(
            "ui.groups.members.remove"),
                              new ButtonRenderer<>(event -> {
                                  membersController
                                      .removeMemberFromGroup(event.getItem(),
                                                             group);
                                  membersGrid.getDataProvider().refreshAll();
                              }))
            .setId(COL_REMOVE);

        membersGrid.setWidth("100%");

        final UserRepository userRepo = cdiUtil.findBean(UserRepository.class);

        final HeaderRow membersGridHeader = membersGrid.prependHeaderRow();
        final Button addMemberButton = new Button("Add member");
        addMemberButton.setIcon(VaadinIcons.PLUS);
        addMemberButton.setStyleName(ValoTheme.BUTTON_TINY);
        addMemberButton.addClickListener(event -> {
            final UserSelector userSelector = new UserSelector(
                "Select users to add to group",
                "Add selected users to group",
                usersGroupsRoles,
                userRepo.findByGroup(group),
                (selectedUsers -> {
                    selectedUsers.forEach(user -> {
                        membersController
                            .addMembersToGroup(selectedUsers, group);
                        membersGrid.getDataProvider().refreshAll();
                    });
                }));
            userSelector.center();
            userSelector.setWidth("80%");
            UI.getCurrent().addWindow(userSelector);
        });
        final HeaderCell membersGridHeaderCell = membersGridHeader
            .join(COL_USER_NAME,
                  COL_GIVEN_NAME,
                  COL_FAMILY_NAME,
                  COL_EMAIL,
                  COL_REMOVE);
        membersGridHeaderCell
            .setComponent(new HorizontalLayout(addMemberButton));

        final GroupMembersTableDataProvider usersDataProvider = cdiUtil
            .findBean(GroupMembersTableDataProvider.class);
        usersDataProvider.setGroup(group);
        membersGrid.setDataProvider(usersDataProvider);

        final GroupRolesController rolesController = cdiUtil
            .findBean(GroupRolesController.class);
        final Grid<Role> rolesGrid = new Grid<>();
        rolesGrid
            .addColumn(Role::getName)
            .setId(COL_ROLE_NAME)
            .setCaption("Role Name");
        rolesGrid
            .addColumn(role -> bundle
            .getString("ui.groups.roles.remove"),
                       new ButtonRenderer<>(event -> {
                           rolesController
                               .removeRoleFromGroup(event.getItem(), group);
                           rolesGrid.getDataProvider().refreshAll();
                       }))
            .setId(COL_ROLE_REMOVE);

        rolesGrid.setWidth("100%");

        final RoleRepository roleRepo = cdiUtil.findBean(RoleRepository.class);

        final HeaderRow rolesGridHeader = rolesGrid.prependHeaderRow();
        final Button addRoleButton = new Button("Add role");
        addRoleButton.setIcon(VaadinIcons.PLUS);
        addRoleButton.setStyleName(ValoTheme.BUTTON_TINY);
        addRoleButton.addClickListener(event -> {
            final RoleSelector roleSelector = new RoleSelector(
                "Select role(s) to add to group",
                "Add selected role(s) to group",
                usersGroupsRoles,
                roleRepo.findByParty(group),
                (selectedRoles -> {
                    selectedRoles.forEach(role -> {
                        rolesController.assignRoleToGroup(role, group);
                        rolesGrid.getDataProvider().refreshAll();
                    });
                }));
            roleSelector.center();
            roleSelector.setWidth("80%");
            UI.getCurrent().addWindow(roleSelector);
        });
        final HeaderCell rolesGridHeaderCell = rolesGridHeader
            .join(COL_ROLE_NAME,
                  COL_ROLE_REMOVE);
        rolesGridHeaderCell
            .setComponent(new HorizontalLayout(addRoleButton));

        final GroupRolesTableDataProvider rolesDataProvider = cdiUtil
            .findBean(GroupRolesTableDataProvider.class);
        rolesDataProvider.setGroup(group);
        rolesGrid.setDataProvider(rolesDataProvider);

        final TabSheet tabs = new TabSheet();
        tabs.addTab(membersGrid, "Members");
        tabs.addTab(rolesGrid, "Roles");
        
        final VerticalLayout windowLayout = new VerticalLayout(propertiesPanel,
                                                               tabs);

        setContent(windowLayout);
    }

    @Override
    public void close() {

        if (dataHasChanged) {
            final ResourceBundle bundle = ResourceBundle
                .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                           UI.getCurrent().getLocale());

            final ConfirmDiscardDialog dialog = new ConfirmDiscardDialog(
                this,
                bundle.getString("ui.admin.group_edit.discard_confirm"));
            dialog.setModal(true);
            UI.getCurrent().addWindow(dialog);
        } else {
            super.close();
        }
    }

    protected void saveGroup() {

        final ResourceBundle bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       UI.getCurrent().getLocale());

        boolean valid = true;

        if (groupNameField.getValue() == null
                || groupNameField.getValue().trim().isEmpty()) {

            groupNameField.setComponentError(new UserError(
                bundle.getString("ui.admin.group_edit.groupname.error.notempty")));
            valid = false;
        }

        if (!valid) {
            return;
        }

        final String notificationText;
        group.setName(groupNameField.getValue());
        notificationText = String.format("Saved changes to group %s",
                                         group.getName());

        groupRepo.save(group);

        dataHasChanged = false;
        if (usersGroupsRoles != null) {
            usersGroupsRoles.refreshGroups();
        }
        close();
        new Notification(notificationText, Notification.Type.TRAY_NOTIFICATION)
            .show(Page.getCurrent());
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
