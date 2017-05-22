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
import com.vaadin.server.ClassResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import org.apache.shiro.subject.Subject;
import org.libreccm.admin.ui.usersgroupsroles.GroupsTableDataProvider;
import org.libreccm.admin.ui.usersgroupsroles.RolesTableDataProvider;
import org.libreccm.admin.ui.usersgroupsroles.UsersGroupsRoles;
import org.libreccm.admin.ui.usersgroupsroles.UsersTableDataProvider;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.GroupManager;
import org.libreccm.security.GroupRepository;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.RoleManager;
import org.libreccm.security.RoleRepository;
import org.libreccm.security.UserManager;
import org.libreccm.security.UserRepository;

import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletContext;

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
    private JpqlConsoleController jpqlConsoleController;

    @Inject
    private Subject subject;

    @Inject
    private PermissionChecker permissionChecker;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private UserRepository userRepo;

    @Inject
    private UserManager userManager;

    @Inject
    private GroupRepository groupRepo;

    @Inject
    private GroupManager groupManager;

    @Inject
    private RoleRepository roleRepo;
    
    @Inject
    private RoleManager roleManager;
    
    @Inject
    private UsersTableDataProvider usersTableDataProvider;

    @Inject
    private GroupsTableDataProvider groupsTableDataProvider;

    @Inject
    private RolesTableDataProvider rolesTableDataProvider;

    private ResourceBundle bundle;

    private final TabSheet tabSheet;
//    private final Grid<User> usersTable;
    private final TabSheet.Tab tabUsersGroupsRoles;
    private final UsersGroupsRoles usersGroupsRoles;

    private final JpqlConsole jpqlConsole;

    public AdminView() {

        tabSheet = new TabSheet();

        usersGroupsRoles = new UsersGroupsRoles(this);
        tabUsersGroupsRoles = tabSheet.addTab(usersGroupsRoles,
                                              "Users/Groups/Roles");

        final ServletContext servletContext = VaadinServlet
            .getCurrent()
            .getServletContext();
        if ("true".equals(servletContext.getInitParameter("ccm.develmode"))) {
            jpqlConsole = new JpqlConsole(this);
            tabSheet.addTab(jpqlConsole, "JPQL Console");
        } else {
            jpqlConsole = null;
        }

        final GridLayout header = new GridLayout(5, 1);
        header.setWidth("100%");
        header.addStyleName("libreccm-header");

        final Label headerInfoLine = new Label("LibreCCM");
        headerInfoLine.setId("libreccm-headerinfoline");
        header.addComponent(headerInfoLine, 3, 0, 4, 0);
        header.setComponentAlignment(headerInfoLine, Alignment.TOP_RIGHT);

        final String logoPath;
        switch (servletContext.getInitParameter("ccm.distribution")
            .toLowerCase()) {
            case "libreccm":
                logoPath = "/themes/libreccm-default/images/libreccm.png";
                break;
            case "librecms":
                logoPath = "/themes/libreccm-default/images/librecms.png";
                break;
            case "aplaws":
                logoPath = "/themes/libreccm-default/images/aplaws.png";
                break;
            case "scientificcms":
                logoPath = "/themes/libreccm-default/images/scientificcms.png";
                break;
            default:
                logoPath = "/themes/libreccm-default/images/libreccm.png";
                break;
        }

        final Image logo = new Image(null, new ClassResource(logoPath));
        logo.setId("libreccm-logo");
        logo.addStyleName("libreccm-logo");
        header.addComponent(logo, 0, 0);
        header.setComponentAlignment(logo, Alignment.MIDDLE_LEFT);

        final CssLayout footer = new CssLayout();
//        footer.setWidth("100%");
        footer.setHeight("5em");

        final VerticalLayout viewLayout = new VerticalLayout();

        viewLayout.addComponent(header);
        viewLayout.addComponent(tabSheet);
        viewLayout.addComponent(footer);

        setCompositionRoot(viewLayout);
    }

    @PostConstruct
    public void postConstruct() {
        bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       globalizationHelper.getNegotiatedLocale());

        usersGroupsRoles.setUsersTableDataProvider(usersTableDataProvider);
        usersGroupsRoles.setGroupsTableDataProvider(groupsTableDataProvider);
        usersGroupsRoles.setRolesTableDataProvider(rolesTableDataProvider);

        tabUsersGroupsRoles.setCaption(bundle
            .getString("ui.admin.tab.users_groups_roles.title"));

        usersGroupsRoles.localize();
    }

    @Override
    public void enter(final ViewChangeListener.ViewChangeEvent event) {

//        usersGroupsRoles.setUsers(userRepo.findAll());
    }

    protected JpqlConsoleController getJpqlConsoleController() {
        return jpqlConsoleController;
    }

    public UserRepository getUserRepository() {
        return userRepo;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public GroupRepository getGroupRepository() {
        return groupRepo;
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }

    public RoleRepository getRoleRepository() {
        return roleRepo;
    }
    
    public RoleManager getRoleManager() {
        return roleManager;
    }
    
}
