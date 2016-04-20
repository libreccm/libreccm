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

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Group;
import org.libreccm.security.GroupRepository;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class GroupForm extends Form {

    private static final String GROUP_NAME = "groupname";

    private final TextField groupName;
    private final SaveCancelSection saveCancelSection;

    public GroupForm(
        final GroupAdmin groupAdmin,
        final ParameterSingleSelectionModel<String> selectedGroupId) {
        
        super("groupform");

        final Label heading = new Label(e -> {
            final PageState state = e.getPageState();

            final Label target = (Label) e.getTarget();

            final String selectedGroupIdStr = selectedGroupId.getSelectedKey(
                state);
            if (selectedGroupIdStr == null || selectedGroupIdStr.isEmpty()) {
                target.setLabel(new GlobalizedMessage(
                    "ui.admin.group.create_new",
                    ADMIN_BUNDLE));
            } else {
                target.setLabel(new GlobalizedMessage("ui.admin.group.edit",
                                                      ADMIN_BUNDLE));
            }
        });
        heading.setClassAttr("heading");
        add(heading);

        groupName = new TextField(GROUP_NAME);
        groupName.setLabel(new GlobalizedMessage("ui.admin.group.name",
                                                 ADMIN_BUNDLE));

        add(groupName);

        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);

        addValidationListener(e -> {
            final PageState state = e.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final FormData data = e.getFormData();

                final String groupNameData = data.getString(GROUP_NAME);

                if (groupNameData == null || groupNameData.isEmpty()) {
                    data.addError(GROUP_NAME, new GlobalizedMessage(
                                  "ui.admin.group.name.error.notempty",
                                  ADMIN_BUNDLE));
                    return;
                }

                if (groupNameData.length() > 256) {
                    data.addError(GROUP_NAME, new GlobalizedMessage(
                                  "ui.admin.group.name.error.length",
                                  ADMIN_BUNDLE));
                }

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final GroupRepository groupRepository = cdiUtil.findBean(
                    GroupRepository.class);

                if (groupRepository.findByName(groupNameData) != null) {
                    data.addError(GROUP_NAME, new GlobalizedMessage(
                                  "ui.admin.group.error.name_already_in_use",
                                  ADMIN_BUNDLE));
                }
            }
        });

        addInitListener(e -> {
            final PageState state = e.getPageState();

            final String selectedGroupIdStr = selectedGroupId.getSelectedKey(
                state);

            if (selectedGroupIdStr != null && !selectedGroupIdStr.isEmpty()) {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final GroupRepository groupRepository = cdiUtil.findBean(
                    GroupRepository.class);

                final Group group = groupRepository.findById(Long.parseLong(
                    selectedGroupIdStr));
                groupName.setValue(state, group.getName());
            }
        });

        addProcessListener(e -> {
            final PageState state = e.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final FormData data = e.getFormData();
                final String groupNameData = data.getString(GROUP_NAME);

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final GroupRepository groupRepository = cdiUtil.findBean(
                    GroupRepository.class);

                final String selectedGroupIdStr = selectedGroupId.
                    getSelectedKey(state);
                if (selectedGroupIdStr == null
                        || selectedGroupIdStr.isEmpty()) {
                    final Group group = new Group();
                    group.setName(groupNameData);

                    groupRepository.save(group);
                } else {
                    final Group group = groupRepository.findById(Long.parseLong(
                        selectedGroupIdStr));
                    group.setName(groupNameData);

                    groupRepository.save(group);
                }
            }

            groupAdmin.hideGroupForm(state);
        });

    }

}
