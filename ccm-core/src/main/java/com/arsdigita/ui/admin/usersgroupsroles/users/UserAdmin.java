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
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.Password;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.UncheckedWrapperException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.EmailAddress;
import org.libreccm.security.ChallengeManager;
import org.libreccm.security.Group;
import org.libreccm.security.GroupManager;
import org.libreccm.security.GroupRepository;
import org.libreccm.security.Role;
import org.libreccm.security.RoleManager;
import org.libreccm.security.RoleRepository;
import org.libreccm.security.User;
import org.libreccm.security.UserManager;
import org.libreccm.security.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TooManyListenersException;
import java.util.TreeSet;
import java.util.stream.IntStream;

import javax.mail.MessagingException;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class UserAdmin extends BoxPanel {

    private static final Logger LOGGER = LogManager.getLogger(UserAdmin.class);

    private final StringParameter userIdParameter;
    private final StringParameter emailParameter;
    private final ParameterSingleSelectionModel<String> selectedUserId;
    private final ParameterSingleSelectionModel<String> selectedEmailAddress;
    private final TextField usersTableFilter;
    private final BoxPanel usersTablePanel;
    private final UsersTable usersTable;
    private final ActionLink backToUsersTable;
    private final PropertySheet userProperties;
    private final Form userEditForm;
    private final Form passwordSetForm;
    private final BoxPanel actionLinks;
//    private final UserDetails userDetails;
    private final BoxPanel userDetails;
    private final Form emailForm;
    private final Form editGroupMembershipsForm;
    private final Form editRoleMembershipsForm;
    private final Form newUserForm;

    public UserAdmin() {
        super();

        setBasicProperties();

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

        userIdParameter = new StringParameter("selected_user_id");
        selectedUserId = new ParameterSingleSelectionModel<>(userIdParameter);
        //selectedUserId = new ParameterSingleSelectionModel<>(USER_ID_PARAM);

        emailParameter = new StringParameter("selected_email_address");
        selectedEmailAddress = new ParameterSingleSelectionModel<>(
            emailParameter);

        usersTable = new UsersTable(this, usersTableFilter, selectedUserId);
        usersTablePanel.add(usersTable);

        final ActionLink addNewUserLink = new ActionLink(new GlobalizedMessage(
            "ui.admin.new_user.link", ADMIN_BUNDLE));
        addNewUserLink.addActionListener(e -> {
            showNewUserForm(e.getPageState());
        });
        usersTablePanel.add(addNewUserLink);

        add(usersTablePanel);

        userDetails = new BoxPanel();
        userDetails.setIdAttr("userDetails");

        backToUsersTable = new ActionLink(new GlobalizedMessage(
            "ui.admin.user_details.back", ADMIN_BUNDLE));
        backToUsersTable.setIdAttr("userDetailsBackLink");
        backToUsersTable.addActionListener(
            e -> closeUserDetails(e.getPageState()));
        userDetails.add(backToUsersTable);

        userProperties = new PropertySheet(new UserPropertySheetModelBuilder(
            selectedUserId));
        userProperties.setIdAttr("userProperties");
        userDetails.add(userProperties);

        userEditForm = new Form("userEditForm");
        final TextField username = new TextField("username");
        username.setLabel(new GlobalizedMessage(
            "ui.admin.user_edit.username.label", ADMIN_BUNDLE));
        username.addValidationListener(new NotEmptyValidationListener(
            new GlobalizedMessage("ui.admin.user_edit.username.error.not_empty",
                                  ADMIN_BUNDLE)));
        userEditForm.add(username);
        final TextField familyName = new TextField("familyName");
        familyName.setLabel(new GlobalizedMessage(
            "ui.admin.user_edit.familyname.label", ADMIN_BUNDLE));
        familyName.addValidationListener(new NotEmptyValidationListener(
            new GlobalizedMessage(
                "ui.admin.user_edit.familyname.error_not_empty",
                ADMIN_BUNDLE)));
        userEditForm.add(familyName);
        final TextField givenName = new TextField("givenName");
        givenName.setLabel(new GlobalizedMessage(
            "ui.admin.user_edit.givenname.label", ADMIN_BUNDLE));
        givenName.addValidationListener(new NotEmptyValidationListener(
            new GlobalizedMessage(
                "ui.admin.user_edit.givenname.error.not_empty",
                ADMIN_BUNDLE)));
        userEditForm.add(givenName);
        final CheckboxGroup banned = new CheckboxGroup("banned");
        banned.addOption(new Option(
            "banned",
            new Label(new GlobalizedMessage("ui.admin.user_edit.banned.label",
                                            ADMIN_BUNDLE))));
        userEditForm.add(banned);
        final CheckboxGroup passwordResetRequired = new CheckboxGroup(
            "password_reset_required");
        passwordResetRequired.addOption(new Option(
            "password_reset_required",
            new Label(new GlobalizedMessage(
                "ui.admin.user_edit.password_reset_required.label",
                ADMIN_BUNDLE))
        ));
        userEditForm.add(passwordResetRequired);
        final SaveCancelSection userEditFormSaveCancel = new SaveCancelSection();
        userEditForm.add(userEditFormSaveCancel);
        userEditForm.addInitListener(e -> {
            final PageState state = e.getPageState();

            final String userIdStr = selectedUserId.getSelectedKey(state);
            final UserRepository userRepository = CdiUtil.createCdiUtil()
                .findBean(UserRepository.class);
            final User user = userRepository.findById(Long.parseLong(userIdStr));

            username.setValue(state, user.getName());
            familyName.setValue(state, user.getFamilyName());
            givenName.setValue(state, user.getGivenName());
            if (user.isBanned()) {
                banned.setValue(state, "banned");
            }
            if (user.isPasswordResetRequired()) {
                passwordResetRequired.setValue(state,
                                               "password_reset_required");
            }
        });
        userEditForm.addProcessListener(e -> {
            final PageState state = e.getPageState();

            if (userEditFormSaveCancel.getSaveButton().isSelected(state)) {
                final String userIdStr = selectedUserId.getSelectedKey(state);
                final UserRepository userRepository = CdiUtil.createCdiUtil()
                    .findBean(UserRepository.class);
                final User user = userRepository.findById(Long.parseLong(
                    userIdStr));

                if (!user.getName().equals(username.getValue(state))) {
                    user.setName((String) username.getValue(state));
                }
                if (!user.getFamilyName().equals(familyName.getValue(state))) {
                    user.setFamilyName((String) familyName.getValue(state));
                }
                if (!user.getGivenName().equals(givenName.getValue(state))) {
                    user.setGivenName((String) familyName.getValue(state));
                }

                if ("banned".equals(banned.getValue(state)) && !user.isBanned()) {
                    user.setBanned(true);
                } else {
                    user.setBanned(false);
                }

                if ("password_reset_required".equals(passwordResetRequired
                    .getValue(
                        state))
                        && !user.isPasswordResetRequired()) {
                    user.setPasswordResetRequired(true);
                } else {
                    user.setPasswordResetRequired(false);
                }

                userRepository.save(user);
            }
            closeUserEditForm(state);
        });
        add(userEditForm);

        passwordSetForm = new Form("password_set_form");
        final Password newPassword = new Password("new_password");
        newPassword.setLabel(new GlobalizedMessage(
            "ui.admin.user_set_password.new_password.label", ADMIN_BUNDLE));
        newPassword.addValidationListener(new NotEmptyValidationListener(
            new GlobalizedMessage(
                "ui.admin.set_password.new_password.error.not_empty",
                ADMIN_BUNDLE)));
        passwordSetForm.add(newPassword);
        final Password passwordConfirm = new Password("password_confirm");
        passwordConfirm.addValidationListener(new NotEmptyValidationListener(
            new GlobalizedMessage(
                "ui.admin.set_password.password_confirm.error.not_empty",
                ADMIN_BUNDLE)));
        passwordConfirm.setLabel(new GlobalizedMessage(
            "ui.admin.user_set_password.confirm_password.label",
            ADMIN_BUNDLE
        ));
        passwordSetForm.add(passwordConfirm);
        final SaveCancelSection passwordSetFormSaveCancel
                                    = new SaveCancelSection();
        passwordSetForm.add(passwordSetFormSaveCancel);
        passwordSetForm.addValidationListener(e -> {
            final PageState state = e.getPageState();

            if (passwordSetFormSaveCancel.getSaveButton().isSelected(state)) {
                final FormData formData = e.getFormData();

                final String password = (String) newPassword.getValue(state);
                final String confirm = (String) passwordConfirm.getValue(state);

                if (!password.equals(confirm)) {
                    formData.addError(new GlobalizedMessage(
                        "ui.admin.user_set_password.error.do_not_match",
                        ADMIN_BUNDLE));
                }
            }
        });
        passwordSetForm.addProcessListener(e -> {
            final PageState state = e.getPageState();

            if (passwordSetFormSaveCancel.getSaveButton().isSelected(state)) {
                final String userIdStr = selectedUserId.getSelectedKey(state);
                final String password = (String) newPassword.getValue(state);

                final UserRepository userRepository = CdiUtil.createCdiUtil()
                    .findBean(UserRepository.class);
                final User user = userRepository.findById(Long.parseLong(
                    userIdStr));

                final UserManager userManager = CdiUtil.createCdiUtil()
                    .findBean(
                        UserManager.class);
                userManager.updatePassword(user, password);
            }
            closePasswordSetForm(state);
        });
        add(passwordSetForm);

        actionLinks = new BoxPanel(BoxPanel.HORIZONTAL);
        actionLinks.setIdAttr("userDetailsActionLinks");
        final ActionLink editUserDetailsLink = new ActionLink(
            new GlobalizedMessage("ui.admin.user_details.edit", ADMIN_BUNDLE));
        editUserDetailsLink.addActionListener(e -> {
            showUserEditForm(e.getPageState());
        });
        actionLinks.add(editUserDetailsLink);
        actionLinks.add(new Text(" | "));

        final ActionLink setPasswordLink = new ActionLink(
            new GlobalizedMessage("ui.admin.user_details.set_password",
                                  ADMIN_BUNDLE));
        setPasswordLink.addActionListener(e -> {
            showPasswordSetForm(e.getPageState());
        });
        actionLinks.add(setPasswordLink);
        actionLinks.add(new Text(" | "));

        final ActionLink generatePasswordLink = new ActionLink(
            new GlobalizedMessage("ui.admin.user_details.generate_password",
                                  ADMIN_BUNDLE));
        generatePasswordLink.setConfirmation(new GlobalizedMessage(
            "ui.admin.user_details.generate_password.confirm",
            ADMIN_BUNDLE));
        generatePasswordLink.addActionListener(e -> {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final UserRepository userRepository = cdiUtil.findBean(
                UserRepository.class);
            final User user = userRepository.findById(Long.parseLong(
                selectedUserId.getSelectedKey(e.getPageState())));
            final ChallengeManager challengeManager = cdiUtil.findBean(
                ChallengeManager.class);
            try {
                challengeManager.sendPasswordRecover(user);
            } catch (MessagingException ex) {
                LOGGER.error("Failed to send email to user.", ex);
            }
        });
        actionLinks.add(generatePasswordLink);
        userDetails.add(actionLinks);

        final Table primaryEmailTable = new Table();
        primaryEmailTable.setModelBuilder(
            new UserPrimaryEmailTableModelBuilder(selectedUserId));
        final TableColumnModel primaryEmailTableColModel = primaryEmailTable
            .getColumnModel();
        primaryEmailTableColModel.add(new TableColumn(
            UserPrimaryEmailTableModel.COL_ADDRESS,
            new Label(new GlobalizedMessage(
                "ui.admin.user.primary_email.address",
                ADMIN_BUNDLE))));
        primaryEmailTableColModel.add(new TableColumn(
            UserPrimaryEmailTableModel.COL_VERIFIED,
            new Label(new GlobalizedMessage(
                "ui.admin.user.primary_email.verified",
                ADMIN_BUNDLE))));
        primaryEmailTableColModel.add(new TableColumn(
            UserPrimaryEmailTableModel.COL_BOUNCING,
            new Label(new GlobalizedMessage(
                "ui.admin.user.primary_email.bouncing",
                ADMIN_BUNDLE))));
        primaryEmailTableColModel.add(new TableColumn(
            UserPrimaryEmailTableModel.COL_ACTION,
            new Label(new GlobalizedMessage(
                "ui.admin.user.primary_email.action",
                ADMIN_BUNDLE))));
        primaryEmailTableColModel.get(
            UserPrimaryEmailTableModel.COL_ACTION).setCellRenderer(
                new TableCellRenderer() {

                @Override
                public Component getComponent(final Table table,
                                              final PageState state,
                                              final Object value,
                                              final boolean isSelected,
                                              final Object key,
                                              final int row,
                                              final int column) {
                    return new ControlLink((Component) value);
                }

            });

        primaryEmailTable.addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event) {
                final String key = (String) event.getRowKey();
                selectedEmailAddress.setSelectedKey(event.getPageState(), key);
                showEmailForm(event.getPageState());
            }

            @Override
            public void headSelected(final TableActionEvent event) {
                //Nothing
            }

        });

        userDetails.add(primaryEmailTable);

        final Table emailTable = new Table();
        emailTable.setModelBuilder(
            new UserEmailTableModelBuilder(selectedUserId));
        final TableColumnModel emailTableColumnModel = emailTable
            .getColumnModel();
        emailTableColumnModel.add(new TableColumn(
            UserEmailTableModel.COL_ADDRESS,
            new Label(new GlobalizedMessage(
                "ui.admin.user.email_addresses.address",
                ADMIN_BUNDLE))));
        emailTableColumnModel.add(new TableColumn(
            UserEmailTableModel.COL_VERIFIED,
            new Label(new GlobalizedMessage(
                "ui.admin.user.email_addresses.verified",
                ADMIN_BUNDLE))));
        emailTableColumnModel.add(new TableColumn(
            UserEmailTableModel.COL_BOUNCING,
            new Label(new GlobalizedMessage(
                "ui.admin.user.email_addresses.bouncing",
                ADMIN_BUNDLE))));
        emailTableColumnModel.add(new TableColumn(
            UserEmailTableModel.COL_EDIT,
            new Label(new GlobalizedMessage(
                "ui.admin.user.email_addresses.edit",
                ADMIN_BUNDLE))));
        emailTableColumnModel.add(new TableColumn(
            UserEmailTableModel.COL_DELETE,
            new Label(new GlobalizedMessage(
                "ui.admin.user.email_addresses.delete",
                ADMIN_BUNDLE))));
        emailTableColumnModel.get(UserEmailTableModel.COL_EDIT).setCellRenderer(
            new TableCellRenderer() {

            @Override
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row,
                                          final int column) {
                return new ControlLink((Component) value);
            }

        });
        emailTableColumnModel.get(UserEmailTableModel.COL_DELETE)
            .setCellRenderer(
                new TableCellRenderer() {

                @Override
                public Component getComponent(final Table table,
                                              final PageState state,
                                              final Object value,
                                              final boolean isSelected,
                                              final Object key,
                                              final int row,
                                              final int column) {
                    final ControlLink link = new ControlLink((Component) value);
                    if (column == UserEmailTableModel.COL_DELETE) {
                        link.setConfirmation(new GlobalizedMessage(
                            "ui.admin.user.email_addresses.delete.confirm",
                            ADMIN_BUNDLE));
                    }
                    return link;
                }

            });
        emailTable.addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event) {
                final PageState state = event.getPageState();

                final String key = (String) event.getRowKey();

                switch (event.getColumn()) {
                    case UserEmailTableModel.COL_EDIT:
                        selectedEmailAddress.setSelectedKey(state, key);
                        showEmailForm(state);
                        break;
                    case UserEmailTableModel.COL_DELETE:
                        final String userIdStr = selectedUserId.getSelectedKey(
                            state);
                        final UserRepository userRepository = CdiUtil
                            .createCdiUtil().findBean(UserRepository.class);
                        final User user = userRepository.findById(Long
                            .parseLong(userIdStr));
                        EmailAddress email = null;
                        for (EmailAddress current : user.getEmailAddresses()) {
                            if (current.getAddress().equals(key)) {
                                email = current;
                                break;
                            }
                        }

                        if (email != null) {
                            user.removeEmailAddress(email);
                            userRepository.save(user);
                        }
                }
            }

            @Override
            public void headSelected(final TableActionEvent event) {
                //Nothing
            }

        });
        emailTable.setEmptyView(new Label(new GlobalizedMessage(
            "ui.admin.user.email_addresses.none", ADMIN_BUNDLE)));

        userDetails.add(emailTable);

        final Table groupsRolesTable = new Table();
        groupsRolesTable.setModelBuilder(new UserGroupsRolesTableModelBuilder(
            selectedUserId));
        final TableColumnModel groupsRolesColModel = groupsRolesTable
            .getColumnModel();
        groupsRolesColModel.add(new TableColumn(
            UserGroupsRolesTableModel.COL_LABEL));
        groupsRolesColModel
            .add(new TableColumn(UserGroupsRolesTableModel.COL_VALUE));
        groupsRolesColModel.add(
            new TableColumn(UserGroupsRolesTableModel.COL_ACTION));
        groupsRolesColModel.get(UserGroupsRolesTableModel.COL_ACTION)
            .setCellRenderer(new TableCellRenderer() {

                @Override
                public Component getComponent(final Table table,
                                              final PageState state,
                                              final Object value,
                                              final boolean isSelected,
                                              final Object key,
                                              final int row,
                                              final int column) {
                    switch (row) {
                        case UserGroupsRolesTableModel.ROW_GROUPS: {
                            return new ControlLink((Component) value);
                        }
                        case UserGroupsRolesTableModel.ROW_ROLES: {
                            return new ControlLink((Component) value);
                        }
                        case UserGroupsRolesTableModel.ROW_ALL_ROLES:
                            return new Text("");
                        default:
                            throw new IllegalArgumentException();
                    }
                }

            });
        groupsRolesTable.addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event) {
                final int selectedRow = Integer.parseInt((String) event
                    .getRowKey());
                final PageState state = event.getPageState();

                switch (selectedRow) {
                    case UserGroupsRolesTableModel.ROW_GROUPS:
                        showEditGroupMembershipsForm(state);
                        break;
                    case UserGroupsRolesTableModel.ROW_ROLES:
                        showEditRoleMembershipsForm(state);
                        break;
                }
            }

            @Override
            public void headSelected(final TableActionEvent event) {
                //Nothing
            }

        }
        );

        userDetails.add(groupsRolesTable);

        final ActionLink addEmailLink = new ActionLink(new GlobalizedMessage(
            "ui.admin.user.email_addresses.add", ADMIN_BUNDLE));
        addEmailLink.addActionListener(e -> {
            showEmailForm(e.getPageState());
        });
        userDetails.add(addEmailLink);

        emailForm = new Form("email_form");
        final TextField emailFormAddress = new TextField("email_form_address");
        emailFormAddress.setLabel(new GlobalizedMessage(
            "ui.admin.user.email_form.address", ADMIN_BUNDLE));
        emailFormAddress.addValidationListener(new NotEmptyValidationListener(
            new GlobalizedMessage("ui.admin.user.email_form.address.not_empty",
                                  ADMIN_BUNDLE)));
        emailForm.add(emailFormAddress);
        final CheckboxGroup emailFormVerified = new CheckboxGroup(
            "email_form_verified");
        emailFormVerified.addOption(
            new Option("true",
                       new Label(new GlobalizedMessage(
                           "ui.admin.user.email_form.verified",
                           ADMIN_BUNDLE))));
        emailForm.add(emailFormVerified);
        final CheckboxGroup emailFormBouncing = new CheckboxGroup(
            "email_form_bouncing");
        emailFormBouncing.addOption(
            new Option("true",
                       new Label(new GlobalizedMessage(
                           "ui.admin.user.email_form.bouncing",
                           ADMIN_BUNDLE))));
        emailForm.add(emailFormBouncing);

        emailForm.add(new SaveCancelSection());

        emailForm.addInitListener(e -> {
            final PageState state = e.getPageState();

            final String selected = selectedEmailAddress.getSelectedKey(state);
            final String userIdStr = selectedUserId.getSelectedKey(state);
            if (selected != null && !selected.isEmpty()) {
                final UserRepository userRepository = CdiUtil.createCdiUtil()
                    .findBean(UserRepository.class);
                final User user = userRepository.findById(Long.parseLong(
                    userIdStr));
                EmailAddress email = null;
                if (user.getPrimaryEmailAddress().getAddress().equals(selected)) {
                    email = user.getPrimaryEmailAddress();
                } else {
                    for (EmailAddress current : user.getEmailAddresses()) {
                        if (current.getAddress().equals(selected)) {
                            email = current;
                            break;
                        }
                    }
                }

                if (email != null) {
                    emailFormAddress.setValue(state, email.getAddress());
                    if (email.isVerified()) {
                        emailFormVerified.setValue(state, "true");
                    }
                    if (email.isBouncing()) {
                        emailFormBouncing.setValue(state, "true");
                    }
                }
            }
        });

        emailForm.addProcessListener(e -> {
            final PageState state = e.getPageState();

            final String selected = selectedEmailAddress.getSelectedKey(state);
            final String userIdStr = selectedUserId.getSelectedKey(state);

            final UserRepository userRepository = CdiUtil.createCdiUtil()
                .findBean(UserRepository.class);
            final User user = userRepository.findById(Long.parseLong(
                userIdStr));
            EmailAddress email = null;
            if (selected == null) {
                email = new EmailAddress();
                user.addEmailAddress(email);
            } else if (user.getPrimaryEmailAddress().getAddress().equals(
                selected)) {
                email = user.getPrimaryEmailAddress();
            } else {
                for (EmailAddress current : user.getEmailAddresses()) {
                    if (current.getAddress().equals(selected)) {
                        email = current;
                        break;
                    }
                }
            }

            if (email != null) {
                email.setAddress((String) emailFormAddress.getValue(state));

                final String[] verifiedValues = (String[]) emailFormVerified
                    .getValue(state);
                if (verifiedValues != null && verifiedValues.length > 0) {
                    if ("true".equals(verifiedValues[0])) {
                        email.setVerified(true);
                    } else {
                        email.setVerified(false);
                    }
                } else {
                    email.setVerified(false);
                }

                final String[] bouncingValues = (String[]) emailFormBouncing
                    .getValue(state);
                if (bouncingValues != null && bouncingValues.length > 0) {
                    if ("true".equals(bouncingValues[0])) {
                        email.setBouncing(true);
                    } else {
                        email.setBouncing(false);
                    }
                } else {
                    email.setBouncing(false);
                }
            }

            userRepository.save(user);
            closeEmailForm(e.getPageState());
        });

        emailForm.addCancelListener(e -> closeEmailForm(e.getPageState()));

        add(emailForm);

        add(userDetails);

        editGroupMembershipsForm = buildEditGroupMembershipsForm();
        add(editGroupMembershipsForm);

        editRoleMembershipsForm = buildEditRoleMembershipsForm();
        add(editRoleMembershipsForm);

        newUserForm = buildNewUserForm();
        add(newUserForm);
    }

    private void setBasicProperties() {
        setIdAttr("userAdmin");
    }

    private Form buildNewUserForm() {
        final Form form = new Form("new_user_form");

        final String username = "username";
        final TextField usernameField = new TextField(username);
        usernameField.setLabel(new GlobalizedMessage(
            "ui.admin.new_user_form.username.label", ADMIN_BUNDLE));
        usernameField.setSize(32);
        usernameField.setMaxLength(32);
        usernameField.addValidationListener(new NotEmptyValidationListener());
        usernameField.addValidationListener(new StringLengthValidationListener(
            32));
        form.add(usernameField);

        final String givenName = "givenname";
        final TextField givenNameField = new TextField(givenName);
        givenNameField.setLabel(new GlobalizedMessage(
            "ui.admin.new_user_form.givenname.label", ADMIN_BUNDLE));
        givenNameField.setSize(32);
        givenNameField.setMaxLength(256);
        givenNameField.addValidationListener(new NotEmptyValidationListener());
        givenNameField.addValidationListener(new StringLengthValidationListener(
            256));
        form.add(givenNameField);

        final String familyName = "familyName";
        final TextField familyNameField = new TextField(familyName);
        familyNameField.setLabel(new GlobalizedMessage(
            "ui.admin.new_user_form.familyname.label", ADMIN_BUNDLE));
        familyNameField.setSize(32);
        familyNameField.setMaxLength(256);
        familyNameField.addValidationListener(new NotEmptyValidationListener());
        familyNameField.addValidationListener(
            new StringLengthValidationListener(256));
        form.add(familyNameField);

        final String email = "email";
        final TextField emailField = new TextField(email);
        emailField.setLabel(new GlobalizedMessage(
            "ui.admin.new_user_form.email.label", ADMIN_BUNDLE));
        emailField.setSize(48);
        emailField.setMaxLength(256);
        emailField.addValidationListener(new NotEmptyValidationListener());
        emailField
            .addValidationListener(new StringLengthValidationListener(256));
        form.add(emailField);

        final String passwordOptions = "passwordOptions";
        final String optionSetPassword = "setPassword";
        final String optionSendPassword = "sendPassword";
        final RadioGroup passwordOptionsGroup = new RadioGroup(passwordOptions);
        final Option sendPasswordOption = new Option(
            optionSendPassword,
            new Label(new GlobalizedMessage(
                "ui.admin.new_user_form.password_options.send_password.label",
                ADMIN_BUNDLE)));
        passwordOptionsGroup.addOption(sendPasswordOption);
        final Option setPasswordOption = new Option(
            optionSetPassword,
            new Label(new GlobalizedMessage(
                "ui.admin.new_user_form.password_options.set_password",
                ADMIN_BUNDLE)));
        passwordOptionsGroup.addOption(setPasswordOption);
        form.add(passwordOptionsGroup);

        final String password = "password";
        final String passwordConfirmation = "passwordConfirmation";
        final Password passwordField = new Password(password);
        passwordField.setLabel(new GlobalizedMessage(
            "ui.admin.new_user_form.password_options.set_password.password.label",
            ADMIN_BUNDLE));
        passwordField.setMaxLength(256);
        passwordField.setSize(32);
        passwordField.addValidationListener(new NotEmptyValidationListener());
        passwordField.addValidationListener(new StringLengthValidationListener(
            256));
        form.add(passwordField);
        final Password passwordConfirmationField = new Password(
            passwordConfirmation);
        passwordConfirmationField.setLabel(new GlobalizedMessage(
            "ui.admin.new_user_form.password_options.set_password.password_confirmation.label",
            ADMIN_BUNDLE));
        passwordConfirmationField.setMaxLength(256);
        passwordConfirmationField.setSize(32);
        passwordConfirmationField.addValidationListener(
            new NotEmptyValidationListener());
        passwordConfirmationField.addValidationListener(
            new StringLengthValidationListener(256));
        form.add(passwordConfirmationField);

        final SaveCancelSection saveCancelSection = new SaveCancelSection();
        form.add(saveCancelSection);

        form.addValidationListener(e -> {
            final PageState state = e.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final FormData data = e.getFormData();

                final String usernameData = data.getString(username);
                final String emailData = data.getString(email);
                final String selectedPasswordOption = data.getString(
                    passwordOptions);

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final UserRepository userRepository = cdiUtil.findBean(
                    UserRepository.class);

                if (userRepository.findByName(usernameData) != null) {
                    data.addError(new GlobalizedMessage(
                        "ui.admin.new_user_form.error.username_already_in_use",
                        ADMIN_BUNDLE));
                    return;
                }

                if (userRepository.findByEmailAddress(emailData) != null) {
                    data.addError(new GlobalizedMessage(
                        "ui.admin.new_user_form.error.email_already_in_use",
                        ADMIN_BUNDLE));
                    return;
                }

                if (optionSetPassword.equals(selectedPasswordOption)) {
                    final String passwordData = data.getString(password);
                    final String passwordConfirmData = data.getString(
                        passwordConfirmation);

                    if (!passwordData.equals(passwordConfirmData)) {
                        data.addError(new GlobalizedMessage(
                            "ui.admin.new_user_form.error.password_do_not_match",
                            ADMIN_BUNDLE));
                    }
                }
            }
        });

        form.addProcessListener(e -> {
            final PageState state = e.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final FormData data = e.getFormData();

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final UserManager userManager = cdiUtil.findBean(
                    UserManager.class);

                final String dataUserName = data.getString(username);
                final String dataFamilyName = data.getString(familyName);
                final String dataGivenName = data.getString(givenName);
                final String dataEmail = data.getString(email);
                final String dataSelectedPasswordOption = data.getString(
                    passwordOptions);

                final String dataPassword;
                if (optionSetPassword.equals(dataSelectedPasswordOption)) {
                    dataPassword = data.getString(password);
                } else {
                    dataPassword = null;
                }

                final User user = userManager.createUser(dataGivenName,
                                                         dataFamilyName,
                                                         dataUserName,
                                                         dataEmail,
                                                         dataPassword);

                if (optionSendPassword.equals(dataSelectedPasswordOption)) {
                    final ChallengeManager challengeManager = cdiUtil.findBean(
                        ChallengeManager.class);

                    try {
                        challengeManager.sendPasswordRecover(user);
                    } catch (MessagingException ex) {
                        throw new FormProcessException(
                            "Failed to send password challenge to new user.",
                            new GlobalizedMessage(
                                "ui.admin.new_user_form.error.failed_to_send_password",
                                ADMIN_BUNDLE),
                            ex);
                    }
                }

                closeNewUserForm(state);
            }
        });

        return form;
    }

    private Form buildEditGroupMembershipsForm() {
        final Form form = new Form("edit-usergroupmemberships-form");

        final BoxPanel links = new BoxPanel(BoxPanel.VERTICAL);
        final Label header = new Label(e -> {
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
        links.add(header);

        final ActionLink backLink = new ActionLink(new GlobalizedMessage(
            "ui.admin.user.edit_group_memberships.back_to_user_details",
            ADMIN_BUNDLE));
        backLink.addActionListener(e -> {
            closeEditGroupMembershipsForm(e.getPageState());
        });
        links.add(backLink);

        form.add(links);

        final String groupsSelector = "groupsselector";
        final CheckboxGroup groups = new CheckboxGroup(groupsSelector);
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
        form.add(groups);

        final SaveCancelSection saveCancelSection = new SaveCancelSection();
        form.add(saveCancelSection);

        form.addInitListener(e -> {
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

        form.addProcessListener(e -> {
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
                    groupsSelector);

                final User user = userRepository.findById(Long.parseLong(
                    selectedUserId.getSelectedKey(state)));
                final List<Group> selectedGroups = new ArrayList<>();
                if (selectedGroupIds != null) {
                    Arrays.stream(selectedGroupIds).forEach(id -> {
                        final Group group = groupRepository.findById(
                            Long.parseLong(id));
                        selectedGroups.add(group);
                    });
//                    for (String selectedGroupId : selectedGroupIds) {
//                        final Group group = groupRepository.findById(Long
//                            .parseLong(selectedGroupId));
//                        selectedGroups.add(group);
//                    }
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

            closeEditGroupMembershipsForm(state);
        });

        return form;
    }

    public Form buildEditRoleMembershipsForm() {
        final Form form = new Form("edit-userrolesmemberships-form");

        final BoxPanel links = new BoxPanel(BoxPanel.VERTICAL);
        final Label header = new Label(e -> {
            final PageState state = e.getPageState();
            final Label target = (Label) e.getTarget();

            final String userIdStr = selectedUserId.getSelectedKey(state);
            final UserRepository userRepository = CdiUtil.createCdiUtil()
                .findBean(UserRepository.class);
            final User user = userRepository.findById(Long.parseLong(userIdStr));

            target.setLabel(new GlobalizedMessage(
                "ui.admin.user_edit_role_memberships",
                ADMIN_BUNDLE,
                new String[]{user.getName()}));
        });
        links.add(header);

        final ActionLink backLink = new ActionLink(new GlobalizedMessage(
            "ui.admin.user.edit_role_memberships.back_to_user_details",
            ADMIN_BUNDLE));
        backLink.addActionListener(e -> {
            closeEditRoleMembershipsForm(e.getPageState());
        });
        links.add(backLink);

        form.add(links);

        final String rolesSelector = "rolesselector";
        final CheckboxGroup roles = new CheckboxGroup(rolesSelector);
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
        form.add(roles);

        final SaveCancelSection saveCancelSection = new SaveCancelSection();
        form.add(saveCancelSection);

        form.addInitListener(e -> {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final UserRepository userRepository = cdiUtil.findBean(
                UserRepository.class);

            final PageState state = e.getPageState();

            final User user = userRepository.findById(Long.parseLong(
                selectedUserId.getSelectedKey(state)));
            final List<Role> assignedRoles = new ArrayList<>();
            user.getRoleMemberships().forEach(m -> {
                assignedRoles.add(m.getRole());
            });

            final String[] selectedRoles = new String[assignedRoles.size()];
            IntStream.range(0, assignedRoles.size()).forEach(i -> {
                selectedRoles[i] = Long.toString(assignedRoles.get(i)
                    .getRoleId());
            });

            roles.setValue(state, selectedRoles);
        });

        form.addProcessListener(e -> {
            final PageState state = e.getPageState();
            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final FormData data = e.getFormData();

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final UserRepository userRepository = cdiUtil.findBean(
                    UserRepository.class);
                final RoleRepository roleRepository = cdiUtil.findBean(
                    RoleRepository.class);
                final RoleManager roleManager = cdiUtil.findBean(
                    RoleManager.class);

                final String[] selectedRolesIds = (String[]) data.get(
                    rolesSelector);

                final User user = userRepository.findById(Long.parseLong(
                    selectedUserId.getSelectedKey(state)));
                final List<Role> selectedRoles = new ArrayList<>();
                if (selectedRolesIds != null) {
                    Arrays.stream(selectedRolesIds).forEach(id -> {
                        final Role role = roleRepository.findById(
                            Long.parseLong(id));
                        selectedRoles.add(role);
                    });
                }
                final List<Role> assignedRoles = new ArrayList<>();
                user.getRoleMemberships().forEach(m -> {
                    assignedRoles.add(m.getRole());
                });
                
                //First check for newly added roles
                selectedRoles.forEach(r -> {
                    if (!assignedRoles.contains(r)) {
                        roleManager.assignRoleToParty(r, user);
                    }
                });
                
                //Than check for removed roles
                assignedRoles.forEach(r -> {
                    if (!selectedRoles.contains(r)) {
                        //Role is maybe detached or not fully loaded, 
                        //therefore we load the role from the database.
                        final Role role = roleRepository.findById(r.getRoleId());
                        roleManager.removeRoleFromParty(role, user);
                    }
                });
            }
            
            closeEditRoleMembershipsForm(state);
        });

        return form;
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.addGlobalStateParam(userIdParameter);
        page.addGlobalStateParam(emailParameter);

        page.setVisibleDefault(usersTablePanel, true);
        page.setVisibleDefault(userDetails, false);
        page.setVisibleDefault(userEditForm, false);
        page.setVisibleDefault(passwordSetForm, false);
        page.setVisibleDefault(emailForm, false);
        page.setVisibleDefault(editGroupMembershipsForm, false);
        page.setVisibleDefault(editRoleMembershipsForm, false);
        page.setVisibleDefault(newUserForm, false);
    }

    protected void showUserDetails(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, true);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, false);
        editGroupMembershipsForm.setVisible(state, false);
        editRoleMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, false);
    }

    protected void closeUserDetails(final PageState state) {
        selectedUserId.clearSelection(state);
        usersTablePanel.setVisible(state, true);
        userDetails.setVisible(state, false);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, false);
        editGroupMembershipsForm.setVisible(state, false);
        editRoleMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, false);
    }

    protected void showUserEditForm(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, false);
        userEditForm.setVisible(state, true);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, false);
        editGroupMembershipsForm.setVisible(state, false);
        editRoleMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, false);
    }

    protected void closeUserEditForm(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, true);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, false);
        editGroupMembershipsForm.setVisible(state, false);
        editRoleMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, false);
    }

    protected void showPasswordSetForm(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, false);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, true);
        emailForm.setVisible(state, false);
        editGroupMembershipsForm.setVisible(state, false);
        editRoleMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, false);
    }

    protected void closePasswordSetForm(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, true);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, false);
        editGroupMembershipsForm.setVisible(state, false);
        editRoleMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, false);
    }

    protected void showEmailForm(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, false);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, true);
        editGroupMembershipsForm.setVisible(state, false);
        editRoleMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, false);
    }

    protected void closeEmailForm(final PageState state) {
        selectedEmailAddress.clearSelection(state);
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, true);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, false);
        editGroupMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, false);
    }

    protected void showEditGroupMembershipsForm(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, false);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, false);
        editGroupMembershipsForm.setVisible(state, true);
        editRoleMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, false);
    }

    protected void closeEditGroupMembershipsForm(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, true);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, false);
        editGroupMembershipsForm.setVisible(state, false);
        editRoleMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, false);
    }

    protected void showEditRoleMembershipsForm(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, false);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, false);
        editGroupMembershipsForm.setVisible(state, false);
        editRoleMembershipsForm.setVisible(state, true);
        newUserForm.setVisible(state, false);
    }

    protected void closeEditRoleMembershipsForm(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, true);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, false);
        editGroupMembershipsForm.setVisible(state, false);
        editRoleMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, false);
    }

    protected void showNewUserForm(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, false);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, false);
        editGroupMembershipsForm.setVisible(state, false);
        editRoleMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, true);
    }

    protected void closeNewUserForm(final PageState state) {
        usersTablePanel.setVisible(state, true);
        userDetails.setVisible(state, false);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, false);
        editGroupMembershipsForm.setVisible(state, false);
        editRoleMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, false);
    }

}
