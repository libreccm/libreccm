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
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Party;

import java.util.List;
import java.util.ResourceBundle;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class PartySelector extends Window {

    private static final long serialVersionUID = 6915710902238111484L;

    private static final String COL_PARTY_NAME = "partyname";

    public PartySelector(final String caption,
                         final String actionLabel,
                         final UsersGroupsRolesTab usersGroupsRoles,
                         final List<Party> excludedParties,
                         final PartySelectionAction action) {

        addWidgets(caption, actionLabel, excludedParties, action);
    }

    private void addWidgets(final String caption,
                            final String actionLabel,
                            final List<Party> excludedParties,
                            final PartySelectionAction action) {

        setCaption(caption);

        final ResourceBundle bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       UI.getCurrent().getLocale());

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();

        final Grid<Party> partiesGrid = new Grid<>();
        partiesGrid
            .addColumn(Party::getName)
            .setId(COL_PARTY_NAME)
            .setCaption("Party name");

        partiesGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        partiesGrid.setWidth("100%");

        final Button actionButton = new Button(actionLabel);
        actionButton.addClickListener(event -> {
            action.action(partiesGrid.getSelectedItems());
            close();
        });
        actionButton.setIcon(VaadinIcons.PLUS_CIRCLE_O);
        actionButton.setStyleName(ValoTheme.BUTTON_TINY);

        final Button clearButton = new Button("Clear selection");
        clearButton.addClickListener(event -> {
            partiesGrid.getSelectionModel().deselectAll();
        });
        clearButton.setIcon(VaadinIcons.BACKSPACE);
        clearButton.setStyleName(ValoTheme.BUTTON_TINY);

        final HeaderRow actions = partiesGrid.prependHeaderRow();
        final HeaderCell actionsCell = actions.getCell(COL_PARTY_NAME);
        actionsCell.setComponent(new HorizontalLayout(actionButton,
                                                      clearButton));
        
        final PartySelectorDataProvider dataProvider = cdiUtil
        .findBean(PartySelectorDataProvider.class);
        dataProvider.setExcludedParties(excludedParties);
        partiesGrid.setDataProvider(dataProvider);
        
        setContent(partiesGrid);
    }

}
