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
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import java.util.ResourceBundle;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@CDIView(value = AdminView.VIEWNAME,
         uis = {AdminUIVaadin.class})
public class AdminView extends CustomComponent implements View {

    private static final long serialVersionUID = -2959302663954819489L;

    public static final String VIEWNAME = "admin";

    private final ResourceBundle bundle;

    private final AdminViewController controller;

    private final TabSheet tabSheet;
    private final TabSheet.Tab tabUsersGroupsRoles;
    private final UsersGroupsRolesTab usersGroupsRoles;

    private final JpqlConsole jpqlConsole;

    private final ConfigurationTab configurationTab;
    
    private final SystemInformationTab sysInfoTab;

    @Inject
    protected AdminView(final AdminViewController controller) {

        this.controller = controller;

        bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       controller.getGlobalizationHelper().getNegotiatedLocale());

        tabSheet = new TabSheet();

        usersGroupsRoles = new UsersGroupsRolesTab(controller
            .getUsersGroupsRolesController());
        tabUsersGroupsRoles = tabSheet
            .addTab(usersGroupsRoles,
                    bundle.getString("ui.admin.tab.users_groups_roles.title"));
        
        final ServletContext servletContext = VaadinServlet
            .getCurrent()
            .getServletContext();
        if ("true".equals(servletContext.getInitParameter("ccm.develmode"))) {
            jpqlConsole = new JpqlConsole(controller.getJpqlConsoleController());
            tabSheet.addTab(jpqlConsole, "JPQL Console");
        } else {
            jpqlConsole = null;
        }

        configurationTab = new ConfigurationTab();
        tabSheet.addTab(configurationTab, "Configuration");

        sysInfoTab = new SystemInformationTab(controller);
        tabSheet.addTab(sysInfoTab, "System Information");
        
        final CssLayout footer = new CssLayout();
        footer.setHeight("5em");

        final VerticalLayout viewLayout = new VerticalLayout(new Header(),
                                                             tabSheet,
                                                             footer);

        viewLayout.addStyleName("libreccm-main-margin-top");

        super.setCompositionRoot(viewLayout);
    }

    

    
}
