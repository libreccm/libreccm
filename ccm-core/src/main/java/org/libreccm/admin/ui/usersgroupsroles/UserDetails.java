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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.libreccm.security.User;
import org.libreccm.security.UserManager;
import org.libreccm.security.UserRepository;

import java.util.ResourceBundle;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class UserDetails extends Window {

    private static final long serialVersionUID = 7852981019990845392L;

    private final UsersGroupsRoles usersGroupsRoles;
    private final User user;
    private final UserRepository userRepo;
    private final UserManager userManager;

    public UserDetails(final User user,
                       final UsersGroupsRoles usersGroupsRoles,
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

        final TabSheet tabs = new TabSheet();
        tabs.addTab(layout, "Details");
        tabs.addTab(layout, "Groups");
        tabs.addTab(layout, "Roles");

        setContent(tabs);
    }

}
