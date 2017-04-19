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

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.cdi.URLMapping;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import org.apache.shiro.subject.Subject;
import org.libreccm.security.PermissionChecker;

import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@URLMapping("vaadin")
@CDIUI("admin")
public class AdminUIVaadin extends UI {

    private static final long serialVersionUID = -1352590567964037112L;

//    private TabSheet tabSheet;
//    @Inject
//    private UserRepository userRepo;
    @Inject
    private CDIViewProvider viewProvider;

    @Inject
    private Subject subject;

    @Inject
    private PermissionChecker permissionChecker;

    @Override
    protected void init(VaadinRequest request) {

        final Navigator navigator = new Navigator(this, this);
        navigator.addProvider(viewProvider);

        navigator.addViewChangeListener(new AuthNavListener());

        if (subject.isAuthenticated()) {
            navigator.navigateTo(AdminView.VIEWNAME);
        } else {
            navigator.navigateTo(LoginView.VIEWNAME);
        }

//        tabSheet = new TabSheet();
//
//        final TabSheet userGroupsRoles = new TabSheet();
//        final Grid<User> usersTable = new Grid<>();
//        usersTable.setItems(userRepo.findAll());
//        usersTable.addColumn(User::getName).setCaption("User name");
//        usersTable.addColumn(User::getGivenName).setCaption("Given name");
//        usersTable.addColumn(User::getFamilyName).setCaption("Family name");
//        usersTable
//            .addColumn(user -> user.getPrimaryEmailAddress().getAddress())
//            .setCaption("E-Mail");
//        usersTable.addColumn(User::isBanned).setCaption("Banned?");
//        userGroupsRoles.addTab(usersTable, "Users");
//
//        tabSheet.addTab(userGroupsRoles, "Users/Groups/Roles");
//        setContent(tabSheet);
    }

    private class AuthNavListener implements ViewChangeListener {

        private static final long serialVersionUID = -693722234602948170L;

        @Override
        public boolean beforeViewChange(final ViewChangeEvent event) {

            if (event.getNewView() instanceof AdminView) {

                if (subject.isAuthenticated()) {

                    if (!permissionChecker.isPermitted("admin")) {
                        Notification.show(
                            "Access denied",
                            "Your are not allowed to access the LibreCCM admin application.",
                            Notification.Type.ERROR_MESSAGE);
                        return false;
                    }
                } else {
                    event.getNavigator().navigateTo(LoginView.VIEWNAME);
                }
            }

            return true;
        }

    }

}
