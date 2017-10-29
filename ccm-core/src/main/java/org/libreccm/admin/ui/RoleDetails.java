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
import com.vaadin.ui.themes.ValoTheme;
import org.libreccm.security.Party;
import org.libreccm.security.PartyRepository;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;

import java.util.ResourceBundle;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class RoleDetails extends Window {

    private static final long serialVersionUID = 8109931561947913438L;

    private static final String COL_MEMBER_NAME = "partyname";
    private static final String COL_MEMBER_REMOVE = "member_remove";

    private final Role role;
    private final RolesController controller;

    protected RoleDetails(final Role role,
                          final RolesController controller) {

        super(String.format("Details of role %s", role.getName()));

        this.role = role;
        this.controller = controller;

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
                final RoleEditor editor = new RoleEditor(
                    role,
                    controller.getRoleRepository());
                editor.center();
                UI.getCurrent().addWindow(editor);
            });

        final VerticalLayout layout = new VerticalLayout(formLayout,
                                                         editButton);

        final RolePartiesController partiesController = controller
            .getPartiesController();

        final Grid<Party> partiesGrid = new Grid<>();
        partiesGrid
            .addColumn(Party::getName)
            .setId(COL_MEMBER_NAME)
            .setCaption("Name");
        partiesGrid
            .addComponentColumn(party -> {
                final Button removeButton = new Button(
                    bundle.getString("ui.role.parties.remove"),
                    VaadinIcons.CLOSE_CIRCLE_O);
                removeButton.addClickListener(event -> {
                    partiesController.removePartyFromRole(party, role);
                    partiesGrid.getDataProvider().refreshAll();
                });
                removeButton.addStyleNames(ValoTheme.BUTTON_TINY,
                                           ValoTheme.BUTTON_DANGER);
                return removeButton;
            })
            .setId(COL_MEMBER_REMOVE);

        partiesGrid.setWidth("100%");

        final PartyRepository partyRepo = controller.getPartyRepository();

        final HeaderRow partiesGridHeader = partiesGrid.prependHeaderRow();
        final Button addPartyButton = new Button("Add member");
        addPartyButton.setIcon(VaadinIcons.PLUS);
        addPartyButton.setStyleName(ValoTheme.BUTTON_TINY);
        addPartyButton.addClickListener(event -> {
            final PartySelector partySelector = new PartySelector(
                "Select parties to add to role",
                "Add selected parties to role",
                controller.getPartySelectorDataProvider(),
                partyRepo.findByRole(role),
                selectedParties -> {
                    selectedParties.forEach(party -> {
                        partiesController.assignPartyToRole(party, role);
                    });
                    partiesGrid.getDataProvider().refreshAll();
                });
            partySelector.addCloseListener(closeEvent -> {
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

        final RolePartiesDataProvider partiesDataProvider = controller
            .getRolePartiesDataProvider();
        partiesDataProvider.setRole(role);
        partiesGrid.setDataProvider(partiesDataProvider);

        final TabSheet tabs = new TabSheet();
        tabs.addTab(layout, "Details");
        tabs.addTab(partiesGrid, "Members");

        setContent(tabs);
    }

}
