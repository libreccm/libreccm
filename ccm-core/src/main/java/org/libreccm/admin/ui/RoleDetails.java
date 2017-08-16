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
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Party;
import org.libreccm.security.PartyRepository;
import org.libreccm.security.Role;
import org.libreccm.security.RoleManager;
import org.libreccm.security.RoleRepository;

import java.util.ResourceBundle;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class RoleDetails extends Window {

    private static final long serialVersionUID = 8109931561947913438L;

    private static final String COL_MEMBER_NAME = "partyname";
    private static final String COL_MEMBER_REMOVE = "member_remove";

    private final UsersGroupsRolesTab usersGroupsRoles;
    private final Role role;
    private final RoleRepository roleRepo;
    private final RoleManager roleManager;

    public RoleDetails(final Role role,
                       final UsersGroupsRolesTab usersGroupsRoles,
                       final RoleRepository roleRepo,
                       final RoleManager roleManager) {

        super(String.format("Details of role %s", role.getName()));

        this.usersGroupsRoles = usersGroupsRoles;
        this.role = role;
        this.roleRepo = roleRepo;
        this.roleManager = roleManager;

        addWidgets();
    }

    private void addWidgets() {

        final ResourceBundle bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       UI.getCurrent().getLocale());

        final Label roleName = new Label(role.getName());
        roleName.setCaption(bundle
            .getString("ui.admin.role_edit.rolename.label"));

        //description
        final Label roleDescription = new Label(role.getName());
        roleDescription.setCaption(bundle
            .getString("ui.admin.role_edit.description.label"));

        final FormLayout formLayout = new FormLayout(roleName,
                                                     roleDescription);

        final Button editButton = new Button(
            bundle.getString("ui.admin.roles.table.edit"),
            event -> {
                final RoleEditor editor = new RoleEditor(role,
                                                         usersGroupsRoles,
                                                         roleRepo,
                                                         roleManager);
                editor.center();
                UI.getCurrent().addWindow(editor);
            });

        final VerticalLayout layout = new VerticalLayout(formLayout,
                                                         editButton);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();

        final RolePartiesController partiesController = cdiUtil.findBean(
            RolePartiesController.class);
        final Grid<Party> partiesGrid = new Grid<>();
        partiesGrid
            .addColumn(Party::getName)
            .setId(COL_MEMBER_NAME)
            .setCaption("Name");
        partiesGrid
            .addColumn(party -> bundle
            .getString("ui.role.parties.remove"),
                       new ButtonRenderer<>(event -> {
                           partiesController
                               .removePartyFromRole(event.getItem(), role);
                           partiesGrid.getDataProvider().refreshAll();
                       }))
            .setId(COL_MEMBER_REMOVE);

        partiesGrid.setWidth("100%");

        final PartyRepository partyRepo = cdiUtil
            .findBean(PartyRepository.class);

        final HeaderRow partiesGridHeader = partiesGrid.prependHeaderRow();
        final Button addPartyButton = new Button("Add member");
        addPartyButton.setIcon(VaadinIcons.PLUS);
        addPartyButton.setStyleName(ValoTheme.BUTTON_TINY);
        addPartyButton.addClickListener(event -> {
            final PartySelector partySelector = new PartySelector(
                "Select parties to add to role", 
                "Add selected parties to role", 
                usersGroupsRoles,
                partyRepo.findByRole(role), 
                selectedParties -> {
                    selectedParties.forEach(party -> {
                        partiesController.assignPartyToRole(party, role);
                    });
                    partiesGrid.getDataProvider().refreshAll();
                });
            partySelector.center();
            partySelector.setWidth("80%");
            UI.getCurrent().addWindow(partySelector);
        });
        final HeaderCell partiesGridHeaderCell = partiesGridHeader
        .join(COL_MEMBER_NAME, COL_MEMBER_REMOVE);
        partiesGridHeaderCell
            .setComponent(new HorizontalLayout(addPartyButton));
        
        final RolePartiesDataProvider partiesDataProvider = cdiUtil
        .findBean(RolePartiesDataProvider.class);
        partiesDataProvider.setRole(role);
        partiesGrid.setDataProvider(partiesDataProvider);
        
        final TabSheet tabs = new TabSheet();
        tabs.addTab(layout, "Details");
        tabs.addTab(partiesGrid, "Members");
        
        
        setContent(tabs);
    }

}
