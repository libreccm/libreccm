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
package com.arsdigita.ui.admin.usersgroupsroles.groups;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.login.UserForm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class GroupAdmin extends BoxPanel {

    private final static Logger LOGGER = LogManager.getLogger(GroupAdmin.class);

    private final StringParameter groupIdParameter;
    private final ParameterSingleSelectionModel<String> selectedGroupId;
    private final TextField groupsTableFilter;
    private final BoxPanel groupsTablePanel;
    private final GroupsTable groupsTable;
    private final GroupForm groupForm;

    public GroupAdmin() {
        super();

        setBasicProperties();

        groupsTablePanel = new BoxPanel();
        groupsTablePanel.setIdAttr("groupsTablePanel");

        final Form filterForm = new Form("groupsTableFilterForm");
        groupsTableFilter = new TextField("groupsTableFilter");
        groupsTableFilter.setLabel(new GlobalizedMessage(
                "ui.admin.groups.table.filter.term", ADMIN_BUNDLE));
        filterForm.add(groupsTableFilter);
        filterForm.add(new Submit(new GlobalizedMessage(
                "ui.admin.groups.filter.submit", ADMIN_BUNDLE)));
        final ActionLink clearLink = new ActionLink(new GlobalizedMessage(
                "ui.admin.groups.table.filter.clear", ADMIN_BUNDLE));
        clearLink.addActionListener(e -> {
            final PageState state = e.getPageState();
            groupsTableFilter.setValue(state, null);
        });
        filterForm.add(clearLink);
        groupsTablePanel.add(filterForm);

        groupIdParameter = new StringParameter("selected_group_id");
        selectedGroupId = new ParameterSingleSelectionModel<>(
                groupIdParameter);

        groupsTable = new GroupsTable(this, groupsTableFilter, selectedGroupId);
        groupsTablePanel.add(groupsTable);

        final ActionLink addNewGroupLink = new ActionLink(new GlobalizedMessage(
                "ui.admin.new_group_link", ADMIN_BUNDLE));
        addNewGroupLink.addActionListener(e -> {
            showGroupForm(e.getPageState());
        });
        groupsTablePanel.add(addNewGroupLink);

        add(groupsTablePanel);
        
        groupForm = new GroupForm(this, selectedGroupId);
        add(groupForm);

    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.addGlobalStateParam(groupIdParameter);

        page.setVisibleDefault(groupsTablePanel, true);
        page.setVisibleDefault(groupForm, false);
    }

    private void setBasicProperties() {
        setIdAttr("groupAdmin");

    }

    protected void showGroupDetails(final PageState state) {
        groupsTablePanel.setVisible(state, false);
        groupForm.setVisible(state, false);
    }
    
    protected void hideGroupDetails(final PageState state) {
        groupsTablePanel.setVisible(state, true);
        groupForm.setVisible(state, false);
    }
    
    protected void showGroupForm(final PageState state) {
        groupsTablePanel.setVisible(state, false);
        groupForm.setVisible(state, true);
    }
    
    protected void hideGroupForm(final PageState state) {
        groupsTablePanel.setVisible(state, true);
        groupForm.setVisible(state, false);
    }
    

}
