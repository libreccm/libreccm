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
import org.libreccm.security.Role;
import org.libreccm.security.RoleManager;
import org.libreccm.security.RoleRepository;
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
 * Form for editing the role memberships of a user.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class RoleMembershipsForm extends Form {

    private static final String ROLES_SELECTOR = "rolesselector";

    private final CheckboxGroup roles;
    private final SaveCancelSection saveCancelSection;

    public RoleMembershipsForm(
        final UserAdmin userAdmin,
        final ParameterSingleSelectionModel<String> selectedUserId) {

        super("edit-userrolesmemberships-form");

        final BoxPanel links = new BoxPanel(BoxPanel.VERTICAL);
        final Label heading = new Label(e -> {
            final PageState state = e.getPageState();
            final Label target = (Label) e.getTarget();

            final String userIdStr = selectedUserId.getSelectedKey(state);
            final UserRepository userRepository = CdiUtil.createCdiUtil()
                .findBean(UserRepository.class);
            final User user = userRepository.findById(Long.parseLong(userIdStr))
                .get();

            target.setLabel(new GlobalizedMessage(
                "ui.admin.user_edit_role_memberships",
                ADMIN_BUNDLE,
                new String[]{user.getName()}));
        });
        heading.setClassAttr("heading");
        links.add(heading);

        final ActionLink backLink = new ActionLink(new GlobalizedMessage(
            "ui.admin.user.edit_role_memberships.back_to_user_details",
            ADMIN_BUNDLE));
        backLink.addActionListener(e -> {
            userAdmin.closeEditRoleMembershipsForm(e.getPageState());
        });
        links.add(backLink);

        add(links);

        roles = new CheckboxGroup(ROLES_SELECTOR);
        try {
            roles.addPrintListener(e -> {
                final CheckboxGroup target = (CheckboxGroup) e.getTarget();
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();

                final RoleRepository roleRepository = cdiUtil.findBean(
                    RoleRepository.class);

                target.clearOptions();

                final SortedSet<Role> allRoles = new TreeSet<>(
                    (r1, r2) -> {
                        return r1.getName().compareTo(r2.getName());
                    });
                allRoles.addAll(roleRepository.findAll());

                allRoles.forEach(r -> {
                    final Option option = new Option(Long.toString(
                        r.getRoleId()), new Text(r.getName()));
                    target.addOption(option);
                });
            });
        } catch (TooManyListenersException ex) {
            //This should never happen, and if its happens something is 
            //seriously wrong...
            throw new UncheckedWrapperException(ex);
        }
        add(roles);

        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);

        addInitListener(event -> {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final UserRepository userRepository = cdiUtil
                .findBean(UserRepository.class);
            final UsersController controller = cdiUtil
                .findBean(UsersController.class);

            final PageState state = event.getPageState();

            final User user = userRepository
                .findById(Long.parseLong(selectedUserId.getSelectedKey(state)))
                .get();
            final List<Role> assignedRoles = controller
                .getAssignedRoles(user);

            final String[] selectedRoles = new String[assignedRoles.size()];
            IntStream.range(0, assignedRoles.size()).forEach(i -> {
                selectedRoles[i] = Long.toString(assignedRoles.get(i)
                    .getRoleId());
            });

            roles.setValue(state, selectedRoles);
        });

        addProcessListener(event -> {
            final PageState state = event.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {

                final FormData data = event.getFormData();

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final UserRepository userRepository = cdiUtil
                    .findBean(UserRepository.class);
                final RoleRepository roleRepository = cdiUtil
                    .findBean(RoleRepository.class);
                final RoleManager roleManager = cdiUtil
                    .findBean(RoleManager.class);
                final UsersController controller = cdiUtil
                    .findBean(UsersController.class);

                final String[] selectedRolesIds = (String[]) data
                    .get(ROLES_SELECTOR);

                final User user = userRepository
                    .findById(Long
                        .parseLong(selectedUserId.getSelectedKey(state)))
                    .get();
                final List<Role> selectedRoles = new ArrayList<>();
                if (selectedRolesIds != null) {
                    Arrays
                        .stream(selectedRolesIds)
                        .forEach(id -> {
                            final Role role = roleRepository
                                .findById(Long.parseLong(id))
                                .get();
                            selectedRoles.add(role);
                        });
                }
                
                controller.updateAssignedRoles(user, selectedRoles);
                
//                final List<Role> assignedRoles = controller
//                    .getAssignedRoles(user);
//
//                //First check for newly added roles
//                selectedRoles.forEach(role -> {
//                    if (!assignedRoles.contains(role)) {
//                        roleManager.assignRoleToParty(role, user);
//                    }
//                });
//
//                //Than check for removed roles
//                assignedRoles.forEach(role -> {
//                    if (!selectedRoles.contains(role)) {
//                        //Role is maybe detached or not fully loaded, 
//                        //therefore we load the role from the database.
//                        final Role roleToRemove = roleRepository
//                            .findById(role.getRoleId())
//                            .get();
//                        roleManager.removeRoleFromParty(roleToRemove, user);
//                    }
//                });
            }

            userAdmin.closeEditRoleMembershipsForm(state);
        });
    }

}
