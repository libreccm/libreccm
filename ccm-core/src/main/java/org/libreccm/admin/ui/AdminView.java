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

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import org.apache.shiro.subject.Subject;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.User;
import org.libreccm.security.UserRepository;

import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@CDIView(value = AdminView.VIEWNAME,
         uis = {AdminUIVaadin.class})
public class AdminView extends CustomComponent implements View {

    private static final long serialVersionUID = -2959302663954819489L;

    public static final String VIEWNAME = "admin";

    private static final String COL_USER_NAME = "username";
    private static final String COL_GIVEN_NAME = "given_name";
    private static final String COL_FAMILY_NAME = "family_name";
    private static final String COL_EMAIL = "email";
    private static final String COL_BANNED = "banned";

    @Inject
    private Subject subject;

    @Inject
    private PermissionChecker permissionChecker;

    @Inject
    private GlobalizationHelper globalizationHelper;

    private ResourceBundle bundle;

    @Inject
    private UserRepository userRepo;

    private final TabSheet tabSheet;
    private final Grid<User> usersTable;

    public AdminView() {

        tabSheet = new TabSheet();

        final TabSheet userGroupsRoles = new TabSheet();
        usersTable = new Grid<>();
        usersTable.setWidth("100%");
//        usersTable.setItems(userRepo.findAll());
        usersTable.addColumn(User::getName)
            .setId(COL_USER_NAME)
            .setCaption("User name");
        usersTable
            .addColumn(User::getGivenName)
            .setId(COL_GIVEN_NAME)
            .setCaption("Given name");
        usersTable
            .addColumn(User::getFamilyName)
            .setId(COL_FAMILY_NAME)
            .setCaption("Family name");
        usersTable
            .addColumn(user -> user.getPrimaryEmailAddress().getAddress())
            .setId(COL_EMAIL)
            .setCaption("E-Mail");
        usersTable
            .addColumn(user -> {
                if (user.isBanned()) {
                    return bundle.getString("ui.admin.user.banned_yes");
                } else {
                    return bundle.getString("ui.admin.user.banned_no");
                }
            })
            .setId(COL_BANNED)
            .setCaption("Banned?");
        userGroupsRoles.addTab(usersTable, "Users");

        tabSheet.addTab(userGroupsRoles, "Users/Groups/Roles");

        final CssLayout header = new CssLayout() {

            private static final long serialVersionUID = -4372147161604688854L;

            @Override
            protected String getCss(final Component component) {
                return null;
            }

        };
//        header.setWidth("100%");
        header.setHeight("5em");

        final CssLayout footer = new CssLayout();
//        footer.setWidth("100%");
        footer.setHeight("5em");

        final VerticalLayout viewLayout = new VerticalLayout();

        viewLayout.addComponent(tabSheet);

        setCompositionRoot(viewLayout);
    }

    @PostConstruct
    public void postConstruct() {
        bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       globalizationHelper.getNegotiatedLocale());
    }

    @Override
    public void enter(final ViewChangeListener.ViewChangeEvent event) {

//        if (!subject.isAuthenticated()) {
//            getUI().getNavigator().navigateTo(LoginView.VIEWNAME);
//        }
        usersTable.setItems(userRepo.findAll());

    }

}
