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
package com.arsdigita.ui.admin.usersgroupsroles;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.globalization.GlobalizedMessage;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class UserDetails extends BoxPanel {

//    private final UserAdmin parent;
//    private final ParameterSingleSelectionModel<String> selectedUserId;
    private final ActionLink backLink;
//    private final PropertySheet userProperties;

    public UserDetails(
            final UserAdmin parent,
            final ParameterSingleSelectionModel<String> selectedUserId) {
        super();

//        this.parent = parent;
//        this.selectedUserId = selectedUserId;
        
        backLink = new ActionLink(new GlobalizedMessage(
                "ui.admin.user_details.back", ADMIN_BUNDLE));
        backLink.addActionListener(e -> {
            parent.closeUserDetails(e.getPageState());
        });
        add(backLink);
        
        final PropertySheet userProperties = new PropertySheet(new UserPropertySheetModelBuilder(
                parent, selectedUserId));
        add(userProperties);
    }

}
