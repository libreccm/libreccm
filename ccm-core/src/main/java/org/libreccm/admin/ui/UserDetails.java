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
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.*;

import java.util.ResourceBundle;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class UserDetails extends Window {

    private static final long serialVersionUID = 7852981019990845392L;

    private static final String COL_GROUP_NAME = "group_name";
    private static final String COL_GROUP_REMOVE = "group_remove";

    private static final String COL_ROLE_NAME = "role_name";
    private static final String COL_ROLE_REMOVE = "role_remove";

    private final UsersGroupsRolesTab usersGroupsRoles;
    private final User user;
    private final UserRepository userRepo;
    private final UserManager userManager;

    public UserDetails(final User user,
                       final UsersGroupsRolesTab usersGroupsRoles,
                       final UserRepository userRepo,
                       final UserManager userManager) {

        super(String.format("Details of user %s", user.getName()));

        this.usersGroupsRoles = usersGroupsRoles;
        this.user = user;
        this.userRepo = userRepo;
        this.userManager = userManager;

        addWidgets();
    }

    private void addWidgets() {

        final ResourceBundle bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       UI.getCurrent().getLocale());

        final Label userName = new Label(user.getName());
        userName.setCaption(bundle
            .getString("ui.admin.user_edit.username.label"));

        final Label familyName = new Label(user.getFamilyName());
        familyName.setCaption(bundle
            .getString("ui.admin.user_edit.familyname.label"));

        final Label givenName = new Label(user.getGivenName());
        givenName.setCaption(bundle
            .getString("ui.admin.user_edit.givenname.label"));

        final Label emailAddress = new Label(user.getPrimaryEmailAddress()
            .getAddress());
        emailAddress.setCaption(bundle
            .getString("ui.admin.user_edit.emailAddress.label"));

        final Label passwordResetRequired = new Label();
        if (user.isPasswordResetRequired()) {
            passwordResetRequired.setValue("Yes");
        } else {
            passwordResetRequired.setValue("No");
        }
        passwordResetRequired.setCaption(bundle
            .getString("ui.admin.user_edit.password_reset_required.label"));

        final Label banned = new Label();
        if (user.isBanned()) {
            banned.setValue("Yes");
        } else {
            banned.setValue("No");
        }
        banned.setCaption(bundle.getString("ui.admin.user_edit.banned.label"));

        final FormLayout formLayout = new FormLayout(userName,
                                                     familyName,
                                                     givenName,
                                                     emailAddress,
                                                     passwordResetRequired,
                                                     banned);

        final Button editButton = new Button(
            bundle.getString("ui.admin.users.table.edit"),
            event -> {
                final UserEditor editor = new UserEditor(user,
                                                         usersGroupsRoles,
                                                         userRepo,
                                                         userManager);
                editor.center();
                UI.getCurrent().addWindow(editor);
            });

        final VerticalLayout layout = new VerticalLayout(formLayout,
                                                         editButton);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();

        final UserGroupsController groupsController = cdiUtil
            .findBean(UserGroupsController.class);
        final Grid<Group> groupsGrid = new Grid<>();
        groupsGrid
            .addColumn(Group::getName)
            .setId(COL_GROUP_NAME)
            .setCaption("Group");
        groupsGrid
            .addColumn(group -> bundle
            .getString("ui.user.groups.remove"),
                       new ButtonRenderer<>(event -> {
                           groupsController
                               .removeUserFromGroup(user, event.getItem());
                           groupsGrid.getDataProvider().refreshAll();
                       }))
            .setId(COL_GROUP_REMOVE);

        groupsGrid.setWidth("100%");

        final GroupRepository groupRepo = cdiUtil
            .findBean(GroupRepository.class);

        final HeaderRow groupsGridHeader = groupsGrid.prependHeaderRow();
        final Button addGroupButton = new Button("Add group");
        addGroupButton.setIcon(VaadinIcons.PLUS);
        addGroupButton.setStyleName(ValoTheme.BUTTON_TINY);
        addGroupButton.addClickListener(event -> {
            final GroupSelector groupSelector = new GroupSelector(
                "Select group(s) to which the user is added.",
                "Add user to selected groups",
                usersGroupsRoles,
                groupRepo.findByMember(user),
                selectedGroups -> {
                    selectedGroups.forEach(group -> {
                        groupsController.addUserToGroup(user, group);
                    });
                    groupsGrid.getDataProvider().refreshAll();
                });
            groupSelector.center();
            groupSelector.setWidth("80%");
            UI.getCurrent().addWindow(groupSelector);
        });
        final HeaderCell groupsGridHeaderCell = groupsGridHeader
            .join(COL_GROUP_NAME, COL_GROUP_REMOVE);
        groupsGridHeaderCell
            .setComponent(new HorizontalLayout(addGroupButton));

        final UserGroupsTableDataProvider groupsDataProvider = cdiUtil
            .findBean(UserGroupsTableDataProvider.class);
        groupsDataProvider.setUser(user);
        groupsGrid.setDataProvider(groupsDataProvider);

        final UserRolesController rolesController = cdiUtil
            .findBean(UserRolesController.class);
        final Grid<Role> rolesGrid = new Grid<>();
        rolesGrid
            .addColumn(Role::getName)
            .setId(COL_ROLE_NAME)
            .setCaption("Role");
        rolesGrid
            .addColumn(role -> bundle
            .getString("ui.user.roles.remove"),
                       new ButtonRenderer<>(event -> {
                           rolesController
                               .removeRoleFromUser(event.getItem(), user);
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
                roleRepo.findByParty(user),
                (selectedRoles -> {
                    selectedRoles.forEach(role -> {
                        rolesController.assignRoleToUser(role, user);
                    });
                    rolesGrid.getDataProvider().refreshAll();
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

        final UserRolesTableDataProvider rolesDataProvider = cdiUtil
            .findBean(UserRolesTableDataProvider.class);
        rolesDataProvider.setUser(user);
        rolesGrid.setDataProvider(rolesDataProvider);

        final TabSheet tabs = new TabSheet();
        tabs.addTab(layout, "Details");
        tabs.addTab(groupsGrid, "Groups");
        tabs.addTab(rolesGrid, "Roles");

        setContent(tabs);
    }

}
