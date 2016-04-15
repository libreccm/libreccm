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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Group;
import org.libreccm.security.GroupRepository;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class GroupDetails extends BoxPanel {

    public GroupDetails(
        final GroupAdmin groupAdmin,
        final ParameterSingleSelectionModel<String> selectedGroupId) {
        super(BoxPanel.VERTICAL);

        
        final ActionLink backLink = new ActionLink(new GlobalizedMessage(
            "ui.admin.group_details.back", ADMIN_BUNDLE));
        backLink.setClassAttr("back-link");
        backLink.addActionListener(e -> {
            groupAdmin.hideGroupDetails(e.getPageState());
        });
        add(backLink);

        final Label header = new Label();
        header.setClassAttr("heading");
        header.addPrintListener(e -> {
            final PageState state = e.getPageState();
            final Label target = (Label) e.getTarget();
            final GroupRepository groupRepository = CdiUtil.createCdiUtil()
                .findBean(GroupRepository.class);
            final Group group = groupRepository.findById(Long.parseLong(
                selectedGroupId.getSelectedKey(state)));
            target.setLabel(new GlobalizedMessage(
                "ui.admin.group_details.header",
                ADMIN_BUNDLE, new String[]{group.getName()}));
        });
        add(header);

        final PropertySheet propertySheet = new PropertySheet(
            new GroupPropertySheetModelBuilder(selectedGroupId));

        add(propertySheet);

        final BoxPanel links = new BoxPanel(BoxPanel.HORIZONTAL);
        final ActionLink editProperties = new ActionLink(new GlobalizedMessage(
            "ui.admin.group_details.edit_properties", ADMIN_BUNDLE));
        editProperties.addActionListener(e -> {
            groupAdmin.showGroupForm(e.getPageState());
        });
        links.add(editProperties);

        add(links);

        final GroupMembersTable membersTable = new GroupMembersTable(
            selectedGroupId);
        add(membersTable);

        final ActionLink addMember = new ActionLink(new GlobalizedMessage(
            "ui.admin.group_details.add_member", ADMIN_BUNDLE));
        addMember.addActionListener(e -> {
            groupAdmin.showGroupMemberAddForm(e.getPageState());
        });
        add(addMember);
    }

}
