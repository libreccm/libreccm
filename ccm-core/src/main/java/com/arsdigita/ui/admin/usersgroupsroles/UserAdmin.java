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
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.globalization.GlobalizedMessage;

import org.dom4j.tree.BackedList;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class UserAdmin extends BoxPanel {

    private final LongParameter userIdParameter;
    private final ParameterSingleSelectionModel<String> selectedUserId;
    private final TextField usersTableFilter;
    private final BoxPanel usersTablePanel;
    private final UsersTable usersTable;
    private final ActionLink backToUsersTable;
    private final PropertySheet userProperties;
    private final BoxPanel actionLinks;
//    private final UserDetails userDetails;
    private final BoxPanel userDetails;

    public UserAdmin() {
        super();

        setIdAttr("userAdmin");
        
        usersTablePanel = new BoxPanel();
        usersTablePanel.setIdAttr("usersTablePanel");
        
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
        usersTablePanel.add(filterForm);

        userIdParameter = new LongParameter("selected_user_id");
        selectedUserId = new ParameterSingleSelectionModel<>(userIdParameter);
        //selectedUserId = new ParameterSingleSelectionModel<>(USER_ID_PARAM);

        usersTable = new UsersTable(this, usersTableFilter, selectedUserId);
        usersTablePanel.add(usersTable);

        add(usersTablePanel);
        
//        final Text text = new Text();
//        text.setPrintListener((final PrintEvent e) -> {
//            final Text target = (Text) e.getTarget();
//            final PageState state = e.getPageState();
//            if (selectedUserId.isSelected(state)) {
//                target.setText(selectedUserId.getSelectedKey(state));
//            }
//        });
//        add(text);

//        userDetails = new UserDetails(this, selectedUserId);
//        add(new UserDetails(this, selectedUserId));
    
        userDetails = new BoxPanel();
        userDetails.setIdAttr("userDetails");

        backToUsersTable = new ActionLink(new GlobalizedMessage(
            "ui.admin.user_details.back", ADMIN_BUNDLE));
        backToUsersTable.setIdAttr("userDetailsBackLink");
        backToUsersTable.addActionListener(
            e -> closeUserDetails(e.getPageState()));
        userDetails.add(backToUsersTable);
        
        userProperties = new PropertySheet(new UserPropertySheetModelBuilder(
            this, selectedUserId));
        userProperties.setIdAttr("userProperties");
        userDetails.add(userProperties);
        
        actionLinks = new BoxPanel(BoxPanel.HORIZONTAL);
        actionLinks.setIdAttr("userDetailsActionLinks");
        final ActionLink editUserDetailsLink = new ActionLink(
            new GlobalizedMessage("ui.admin.user_details.edit", ADMIN_BUNDLE));
        editUserDetailsLink.addActionListener(e -> {
            //ToDo
        });
        actionLinks.add(editUserDetailsLink);
        actionLinks.add(new Text(" | "));
        
        final ActionLink setPasswordLink = new ActionLink(
            new GlobalizedMessage("ui.admin.user_details.set_password", 
                ADMIN_BUNDLE));
        setPasswordLink.addActionListener(e -> {
            //ToDo
        });
        actionLinks.add(setPasswordLink);
        actionLinks.add(new Text(" | "));
        
        final ActionLink generatePasswordLink = new ActionLink(
        new GlobalizedMessage("ui.admin.user_details.generate_password", 
            ADMIN_BUNDLE));
        generatePasswordLink.addActionListener(e -> {
            //ToDo
        });
        actionLinks.add(generatePasswordLink);
        
        userDetails.add(actionLinks);
        
        add(userDetails);
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.addGlobalStateParam(userIdParameter);
        
        page.setVisibleDefault(usersTablePanel, true);
        page.setVisibleDefault(userDetails, false);
    }

    protected void showUserDetails(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, true);
    }
    
    protected void closeUserDetails(final PageState state) {
        selectedUserId.clearSelection(state);
        usersTablePanel.setVisible(state, true);
        userDetails.setVisible(state, false);
    }

}
