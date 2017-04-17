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

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TabSheet;
import org.libreccm.security.User;
import org.libreccm.security.UserRepository;

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

    @Inject
    private UserRepository userRepo;

    private final TabSheet tabSheet;
    private final Grid<User> usersTable;

    public AdminView() {
        tabSheet = new TabSheet();

        final TabSheet userGroupsRoles = new TabSheet();
        usersTable = new Grid<>();
//        usersTable.setItems(userRepo.findAll());
        usersTable.addColumn(User::getName).setCaption("User name");
        usersTable.addColumn(User::getGivenName).setCaption("Given name");
        usersTable.addColumn(User::getFamilyName).setCaption("Family name");
        usersTable
            .addColumn(user -> user.getPrimaryEmailAddress().getAddress())
            .setCaption("E-Mail");
        usersTable.addColumn(User::isBanned).setCaption("Banned?");
        userGroupsRoles.addTab(usersTable, "Users");

        tabSheet.addTab(userGroupsRoles, "Users/Groups/Roles");
        setCompositionRoot(tabSheet);
    }

    @Override
    public void enter(final ViewChangeListener.ViewChangeEvent event) {
        
        usersTable.setItems(userRepo.findAll());
        
    }

}
