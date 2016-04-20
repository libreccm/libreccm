/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package com.arsdigita.ui.admin.usersgroupsroles.users;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.globalization.GlobalizedMessage;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 * Displays the properties of a user.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class UserDetails extends BoxPanel {

    public UserDetails(
        final UserAdmin userAdmin,
        final ParameterSingleSelectionModel<String> selectedUserId,
        final ParameterSingleSelectionModel<String> selectedEmailAddress) {

        super(BoxPanel.VERTICAL);

        setIdAttr("userDetails");

        final ActionLink backToUsersTable = new ActionLink(
            new GlobalizedMessage("ui.admin.user_details.back", ADMIN_BUNDLE));
        backToUsersTable.setIdAttr("userDetailsBackLink");
        backToUsersTable.addActionListener(e -> {
            userAdmin.closeUserDetails(e.getPageState());
        });
        add(backToUsersTable);

        final PropertySheet userProperties = new PropertySheet(
            new UserPropertySheetModelBuilder(selectedUserId));
        userProperties.setIdAttr("userProperties");
        add(userProperties);

        add(new ActionLinks(userAdmin, selectedUserId));
        add(new PrimaryEmailTable(userAdmin,
                                  selectedUserId,
                                  selectedEmailAddress));
        add(new EmailTable(userAdmin, selectedUserId, selectedEmailAddress));
        final ActionLink addEmailLink = new ActionLink(new GlobalizedMessage(
            "ui.admin.user.email_addresses.add", ADMIN_BUNDLE));
        addEmailLink.addActionListener(e -> {
            userAdmin.showEmailForm(e.getPageState());
        });
        add(addEmailLink);

        add(new GroupsRolesTable(userAdmin, selectedUserId));

    }

}
