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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.globalization.GlobalizedMessage;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class GroupAdmin extends BoxPanel {

    private final StringParameter groupIdParameter;
    private final ParameterSingleSelectionModel<String> selectedGroupId;
    private final TextField groupsTableFilter;
    private final BoxPanel groupsTablePanel;
    private final GroupsTable groupsTable;
    private final GroupForm groupForm;
    private final GroupDetails groupDetails;
    private final GroupAddMemberForm groupAddMemberForm;

    public GroupAdmin() {
        super(BoxPanel.VERTICAL);

        setBasicProperties();

        final Label heading = new Label(new GlobalizedMessage(
            "ui.admin.groups.heading", ADMIN_BUNDLE));
        heading.setClassAttr("heading");
        add(heading);

        groupsTablePanel = new BoxPanel();
        groupsTablePanel.setIdAttr("groupsTablePanel");

        final Form filterForm = new Form("groupsTableFilterForm");
        groupsTableFilter = new TextField("groupsTableFilter");
        groupsTableFilter.setLabel(new GlobalizedMessage(
            "ui.admin.groups.table.filter.term", ADMIN_BUNDLE));
        filterForm.add(groupsTableFilter);
        filterForm.add(new Submit(new GlobalizedMessage(
            "ui.admin.groups.table.filter.submit", ADMIN_BUNDLE)));
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

        groupDetails = new GroupDetails(this, selectedGroupId);
        add(groupDetails);

        groupAddMemberForm = new GroupAddMemberForm(this, selectedGroupId);
        add(groupAddMemberForm);

    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.addGlobalStateParam(groupIdParameter);

        page.setVisibleDefault(groupsTablePanel, true);
        page.setVisibleDefault(groupForm, false);
        page.setVisibleDefault(groupDetails, false);
        page.setVisibleDefault(groupAddMemberForm, false);
    }

    private void setBasicProperties() {
        setIdAttr("groupAdmin");

    }

    protected void showGroupDetails(final PageState state) {
        groupsTablePanel.setVisible(state, false);
        groupForm.setVisible(state, false);
        groupDetails.setVisible(state, true);
        groupAddMemberForm.setVisible(state, false);
    }

    protected void hideGroupDetails(final PageState state) {
        selectedGroupId.clearSelection(state);
        groupsTablePanel.setVisible(state, true);
        groupForm.setVisible(state, false);
        groupDetails.setVisible(state, false);
        groupAddMemberForm.setVisible(state, false);
    }

    protected void showGroupForm(final PageState state) {
        groupsTablePanel.setVisible(state, false);
        groupForm.setVisible(state, true);
        groupDetails.setVisible(state, false);
        groupAddMemberForm.setVisible(state, false);
    }

    protected void hideGroupForm(final PageState state) {
        //We want to show the groups table if no group is selected and the
        //group details if a group is selected.
        boolean groupSelected = selectedGroupId.isSelected(state);

        groupsTablePanel.setVisible(state, !groupSelected);
        groupForm.setVisible(state, false);
        groupDetails.setVisible(state, groupSelected);
        groupAddMemberForm.setVisible(state, false);
    }

    protected void showGroupMemberAddForm(final PageState state) {
        groupsTablePanel.setVisible(state, false);
        groupForm.setVisible(state, false);
        groupDetails.setVisible(state, false);
        groupAddMemberForm.setVisible(state, true);
    }

    protected void hideGroupMemberAddForm(final PageState state) {
        groupsTablePanel.setVisible(state, false);
        groupForm.setVisible(state, false);
        groupDetails.setVisible(state, true);
        groupAddMemberForm.setVisible(state, false);
    }

}
