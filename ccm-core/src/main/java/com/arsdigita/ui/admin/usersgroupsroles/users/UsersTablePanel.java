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
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.globalization.GlobalizedMessage;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class UsersTablePanel extends BoxPanel {

    private final TextField usersTableFilter;

    public UsersTablePanel(
        final UserAdmin userAdmin,
        final ParameterSingleSelectionModel<String> selectedUserId) {

        setIdAttr("usersTablePanel");

        final Form filterForm = new Form("usersTableFilterForm");
        usersTableFilter = new TextField("usersTableFilter");
        usersTableFilter.setLabel(new GlobalizedMessage(
            "ui.admin.users.table.filter.term", ADMIN_BUNDLE));
        filterForm.add(usersTableFilter);
        filterForm.add(new Submit(new GlobalizedMessage(
            "ui.admin.users.table.filter.submit", ADMIN_BUNDLE)));
        final ActionLink clearLink = new ActionLink(new GlobalizedMessage(
            "ui.admin.users.table.filter.clear", ADMIN_BUNDLE));
        clearLink.addActionListener((e) -> {
            final PageState state = e.getPageState();
            usersTableFilter.setValue(state, null);
        });
        filterForm.add(clearLink);
        add(filterForm);

        add(new UsersTable(userAdmin, usersTableFilter, selectedUserId));

        final ActionLink addNewUserLink = new ActionLink(new GlobalizedMessage(
            "ui.admin.new_user.link", ADMIN_BUNDLE));
        addNewUserLink.addActionListener(e -> {
            userAdmin.showNewUserForm(e.getPageState());
        });
        add(addNewUserLink);

    }

}
