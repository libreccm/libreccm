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
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Group;
import org.libreccm.security.GroupManager;
import org.libreccm.security.GroupRepository;
import org.libreccm.security.User;
import org.libreccm.security.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TooManyListenersException;
import java.util.TreeSet;
import java.util.stream.IntStream;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class GroupMembershipsForm extends Form {

    private static final String GROUPS_SELECTOR = "groupsselector";
    
    private final CheckboxGroup groups;
    private final SaveCancelSection saveCancelSection;
    
    public GroupMembershipsForm(
        final UserAdmin userAdmin,
        final ParameterSingleSelectionModel<String> selectedUserId) {

        super("edit-usergroupmemberships-form");
        
        final BoxPanel links = new BoxPanel(BoxPanel.VERTICAL);
        
        final Label heading = new Label(e -> {
            final PageState state = e.getPageState();
            final Label target = (Label) e.getTarget();

            final String userIdStr = selectedUserId.getSelectedKey(state);
            final UserRepository userRepository = CdiUtil.createCdiUtil()
                .findBean(UserRepository.class);
            final User user = userRepository.findById(Long.parseLong(userIdStr));

            target.setLabel(new GlobalizedMessage(
                "ui.admin.user.edit_group_memberships",
                ADMIN_BUNDLE,
                new String[]{user.getName()}));
        });
        heading.setClassAttr("heading");
        links.add(heading);

        final ActionLink backLink = new ActionLink(new GlobalizedMessage(
            "ui.admin.user.edit_group_memberships.back_to_user_details",
            ADMIN_BUNDLE));
        backLink.addActionListener(e -> {
            userAdmin.closeEditGroupMembershipsForm(e.getPageState());
        });
        links.add(backLink);

        add(links);
        
        groups = new CheckboxGroup(GROUPS_SELECTOR);
        try {
            groups.addPrintListener(e -> {
                final CheckboxGroup target = (CheckboxGroup) e.getTarget();
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final GroupRepository groupRepository = cdiUtil.findBean(
                    GroupRepository.class);

                target.clearOptions();

                final SortedSet<Group> allGroups = new TreeSet<>(
                    (g1, g2) -> {
                        return g1.getName().compareTo(g2.getName());
                    });
                allGroups.addAll(groupRepository.findAll());

                allGroups.forEach(g -> {
                    final Option option = new Option(
                        Long.toString(g.getPartyId()), new Text(g.getName()));
                    target.addOption(option);
                });
            });
        } catch (TooManyListenersException ex) {
            //This should never happen, and if its happens something is 
            //seriously wrong...
            throw new UncheckedWrapperException(ex);
        }
        add(groups);
        
        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);
        
        addInitListener(e -> {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final UserRepository userRepository = cdiUtil.findBean(
                UserRepository.class);

            final PageState state = e.getPageState();

            final User user = userRepository.findById(Long.parseLong(
                selectedUserId.getSelectedKey(state)));
            final List<Group> assignedGroups = new ArrayList<>();
            user.getGroupMemberships().forEach(m -> {
                assignedGroups.add(m.getGroup());
            });

            final String[] selectedGroups = new String[assignedGroups.size()];
            IntStream.range(0, assignedGroups.size()).forEach(i -> {
                selectedGroups[i] = Long.toString(assignedGroups.get(i)
                    .getPartyId());
            });

            groups.setValue(state, selectedGroups);
        });
        
        addProcessListener(e -> {
            final PageState state = e.getPageState();
            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final FormData data = e.getFormData();

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final UserRepository userRepository = cdiUtil.findBean(
                    UserRepository.class);
                final GroupRepository groupRepository = cdiUtil.findBean(
                    GroupRepository.class);
                final GroupManager groupManager = cdiUtil.findBean(
                    GroupManager.class);

                final String[] selectedGroupIds = (String[]) data.get(
                    GROUPS_SELECTOR);

                final User user = userRepository.findById(Long.parseLong(
                    selectedUserId.getSelectedKey(state)));
                final List<Group> selectedGroups = new ArrayList<>();
                if (selectedGroupIds != null) {
                    Arrays.stream(selectedGroupIds).forEach(id -> {
                        final Group group = groupRepository.findById(
                            Long.parseLong(id));
                        selectedGroups.add(group);
                    });
                }
                final List<Group> assignedGroups = new ArrayList<>();
                user.getGroupMemberships().forEach(m -> {
                    assignedGroups.add(m.getGroup());
                });

                //First check for newly added groups
                selectedGroups.forEach(g -> {
                    if (!assignedGroups.contains(g)) {
                        groupManager.addMemberToGroup(user, g);
                    }
                });

                //Than check for removed groups
                assignedGroups.forEach(g -> {
                    if (!selectedGroups.contains(g)) {
                        //The group is maybe detached or not fully loaded,
                        //therefore we load the group from the database.
                        final Group group = groupRepository.findById(
                            g.getPartyId());
                        groupManager.removeMemberFromGroup(user, group);
                    }
                });
            }

            userAdmin.closeEditGroupMembershipsForm(state);
        });
    }

}
