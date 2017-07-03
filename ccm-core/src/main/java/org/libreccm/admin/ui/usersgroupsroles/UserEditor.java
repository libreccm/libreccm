/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.libreccm.admin.ui.usersgroupsroles;

import com.arsdigita.ui.admin.AdminUiConstants;

import com.vaadin.data.HasValue;
import com.vaadin.data.provider.AbstractDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.admin.ui.ConfirmDiscardDialog;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.EmailAddress;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.security.ChallengeManager;
import org.libreccm.security.User;
import org.libreccm.security.UserManager;
import org.libreccm.security.UserRepository;

import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.mail.MessagingException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class UserEditor extends Window {

    private static final long serialVersionUID = 7024424532574023431L;

    private static final Logger LOGGER = LogManager.getLogger(UserEditor.class);

    private enum PasswordOptions {

        DO_NOTHING,
        GENERATE_AND_SEND,
        SET,

    }

    private final UsersGroupsRoles usersGroupsRoles;
    private final User user;
    private final UserRepository userRepo;
    private final UserManager userManager;

    private boolean dataHasChanged = false;

    private TextField userName;
    private TextField familyName;
    private TextField givenName;
    private TextField emailAddress;
    private RadioButtonGroup<PasswordOptions> passwordOptions;
    private PasswordField password;
    private PasswordField passwordConfirmation;
    private CheckBox passwordResetRequired;
    private CheckBox banned;

    public UserEditor(final UsersGroupsRoles usersGroupsRoles,
                      final UserRepository userRepo,
                      final UserManager userManager) {

        super("Create new user");

        this.usersGroupsRoles = usersGroupsRoles;
        user = null;
        this.userRepo = userRepo;
        this.userManager = userManager;

        addWidgets();
    }

    public UserEditor(final User user,
                      final UsersGroupsRoles usersGroupsRoles,
                      final UserRepository userRepo,
                      final UserManager userManager) {

        super(String.format("Edit user %s", user.getName()));

        this.user = user;
        this.usersGroupsRoles = usersGroupsRoles;
        this.userRepo = userRepo;
        this.userManager = userManager;

        addWidgets();
    }

    private void addWidgets() {

        final ResourceBundle bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       UI.getCurrent().getLocale());

        final DataHasChangedListener dataHasChangedListener
                                         = new DataHasChangedListener();

        userName = new TextField(bundle
            .getString("ui.admin.user_edit.username.label"));
        userName.setRequiredIndicatorVisible(true);
        userName.addValueChangeListener(dataHasChangedListener);

        familyName = new TextField(bundle
            .getString("ui.admin.user_edit.familyname.label"));
        familyName.addValueChangeListener(dataHasChangedListener);

        givenName = new TextField(bundle
            .getString("ui.admin.user_edit.givenname.label"));
        givenName.addValueChangeListener(dataHasChangedListener);

        emailAddress = new TextField(bundle
            .getString("ui.admin.user_edit.emailAddress.label"));
        emailAddress.setRequiredIndicatorVisible(true);
        givenName.addValueChangeListener(dataHasChangedListener);

        passwordOptions = new RadioButtonGroup<PasswordOptions>(
            bundle.getString("ui.admin.user_edit.password_options.label"),
            new AbstractDataProvider<PasswordOptions, String>() {

            private static final long serialVersionUID = 1L;

            @Override
            public boolean isInMemory() {
                return true;
            }

            @Override
            public int size(final Query<PasswordOptions, String> query) {
                if (user == null) {
                    return PasswordOptions.values().length - 1;
                } else {
                    return PasswordOptions.values().length;
                }
            }

            @Override
            public Stream<PasswordOptions> fetch(
                final Query<PasswordOptions, String> query) {
                if (user == null) {
                    return Arrays
                        .stream(PasswordOptions.values())
                        .filter(option -> option != PasswordOptions.DO_NOTHING);
                } else {
                    return Arrays.stream(PasswordOptions.values());
                }
            }

        });
        passwordOptions.setItemCaptionGenerator(
            (final PasswordOptions item) -> {
                switch (item) {
                    case GENERATE_AND_SEND:
                        return bundle.getString(
                            "ui.admin.user_edit.password_options.generate_and_send");
                    case SET:
                        return bundle.getString(
                            "ui.admin.user_edit.password_options.set");
                    case DO_NOTHING:
                        return bundle.getString(
                            "ui.admin.user_edit.password_options.do_nothing");
                    default:
                        throw new UnexpectedErrorException(String.format(
                            "Unexpected value '%s' for password options.",
                            item.toString()));
                }
            });

        password = new PasswordField(bundle
            .getString("ui.admin.user_edit.password.label"));
        password.setRequiredIndicatorVisible(true);
        password.addValueChangeListener(dataHasChangedListener);

        passwordConfirmation = new PasswordField(bundle
            .getString("ui.admin.user_set_password_confirm.label"));
        passwordConfirmation.setRequiredIndicatorVisible(true);

        passwordOptions.addValueChangeListener(event -> {
            switch (event.getValue()) {
                case GENERATE_AND_SEND:
                    password.setEnabled(false);
                    password.setVisible(false);
                    passwordConfirmation.setEnabled(false);
                    passwordConfirmation.setVisible(false);
                    break;
                case SET:
                    password.setEnabled(true);
                    password.setVisible(true);
                    passwordConfirmation.setEnabled(true);
                    passwordConfirmation.setVisible(true);
                    break;
                case DO_NOTHING:
                    password.setEnabled(false);
                    password.setVisible(false);
                    passwordConfirmation.setEnabled(false);
                    passwordConfirmation.setVisible(false);
                    break;
                default:
                    throw new UnexpectedErrorException(String.format(
                        "Unexpected value '%s' for password options.",
                        event.getValue().toString()));
            }
        });

        passwordOptions.setValue(PasswordOptions.GENERATE_AND_SEND);

        final CheckBoxHasChangedListener checkBoxHasChangedListener
                                             = new CheckBoxHasChangedListener();

        passwordResetRequired = new CheckBox(bundle
            .getString("ui.admin.user_edit.password_reset_required.label"));
        passwordResetRequired.addValueChangeListener(checkBoxHasChangedListener);

        banned = new CheckBox(bundle
            .getString("ui.admin.user_edit.banned.label"));
        banned.addValueChangeListener(checkBoxHasChangedListener);

        if (user == null) {
            banned.setVisible(false);
            banned.setEnabled(false);
        }

        final Button submit = new Button();
        if (user == null) {
            submit.setCaption(bundle
                .getString("ui.admin.user.createpanel.header"));
        } else {
            submit.setCaption(bundle.getString("ui.admin.save"));
        }
        submit.addClickListener(event -> saveUser());

        final Button cancel = new Button(bundle.getString("ui.admin.cancel"));
        cancel.addClickListener(event -> close());

        final HorizontalLayout buttons = new HorizontalLayout(submit, cancel);

        final FormLayout formLayout = new FormLayout(userName,
                                                     familyName,
                                                     givenName,
                                                     emailAddress,
                                                     passwordOptions,
                                                     password,
                                                     passwordConfirmation,
                                                     passwordResetRequired,
                                                     banned);

        final VerticalLayout layout = new VerticalLayout(formLayout, buttons);

        final Panel panel = new Panel(layout);
        if (user == null) {
            panel.setCaption(bundle
                .getString("ui.admin.user.createpanel.header"));
        } else {
            panel.setCaption(bundle
                .getString("ui.admin.user_details.edit"));
        }

        setContent(panel);

        if (user != null) {
            userName.setValue(user.getName());
            givenName.setValue(user.getGivenName());
            familyName.setValue(user.getFamilyName());
            emailAddress.setValue(user.getPrimaryEmailAddress().getAddress());
            passwordResetRequired.setValue(user.isPasswordResetRequired());
            banned.setValue(user.isBanned());
            passwordOptions.setValue(PasswordOptions.DO_NOTHING);
        }

        dataHasChanged = false;
    }

    @Override
    public void close() {

        if (dataHasChanged) {
            final ConfirmDiscardDialog dialog = new ConfirmDiscardDialog(
                this, "Are you sure to discard the changes made this user?");
            dialog.setModal(true);
            UI.getCurrent().addWindow(dialog);
        } else {
            super.close();
        }
    }

    protected void saveUser() {

        final ResourceBundle bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       UI.getCurrent().getLocale());

        boolean valid = true;

        if (userName.getValue() == null
                || userName.getValue().trim().isEmpty()) {
            userName.setComponentError(new UserError(bundle
                .getString("ui.admin.user_edit.username.error.not_empty")));
            valid = false;
        }
        if (emailAddress.getValue() == null
                || emailAddress.getValue().trim().isEmpty()) {
            emailAddress.setComponentError(new UserError(bundle
                .getString("ui.admin.user.email_form.address.not_empty")));
            valid = false;
        }

        if ((user == null || !user.getName().equals(userName.getValue()))
                && userRepo.isNameInUse(userName.getValue())) {

            userName.setComponentError(new UserError(bundle
                .getString("ui.admin.new_user_form.error"
                               + ".username_already_in_use")));
            valid = false;
        } else {
            userName.setComponentError(null);
        }

        if ((user == null
             || !user.getPrimaryEmailAddress().getAddress().equals(
             emailAddress.getValue()))
                && userRepo.isEmailAddressInUse(emailAddress.getValue())) {

            emailAddress.setComponentError(new UserError(bundle
                .getString("ui.admin.new_user_form.error.email_already_in_use")));
            valid = false;
        } else {
            if (!Pattern.matches(
                "^[^@<>\"\\t ]+@[^@<>\".\\t]+([.][^@<>\".\\n ]+)+$",
                emailAddress.getValue().trim())) {
                emailAddress.setComponentError(new UserError(bundle
                    .getString("ui.admin.user_form.email_malformed")));
                valid = false;
            } else {
                emailAddress.setComponentError(null);
            }
        }

        if (passwordOptions.getValue() == PasswordOptions.SET) {
            if (password.getValue() == null
                    || password.getValue().trim().isEmpty()) {
                password.setComponentError(new UserError(bundle
                    .getString(
                        "ui.admin.set_password.new_password.error.not_empty")));
            }

            if (!password.getValue().equals(passwordConfirmation.getValue())) {
                passwordConfirmation.setComponentError(new UserError(bundle
                    .getString("ui.admin.user_set_password.error.do_not_match")));
            }
        }

        if (!valid) {
            return;
        }

        final User currentUser;
        if (user == null) {

            final User newUser = userManager.createUser(givenName.getValue(),
                                                        familyName.getValue(),
                                                        userName.getValue(),
                                                        emailAddress.getValue(),
                                                        passwordConfirmation
                                                            .getValue());

            newUser.setPasswordResetRequired(passwordResetRequired.getValue());
            newUser.setBanned(banned.getValue());
            currentUser = newUser;

            userRepo.save(newUser);
        } else {
            user.setName(userName.getValue().trim());
            user.setGivenName(givenName.getValue().trim());
            user.setFamilyName(familyName.getValue().trim());
            final EmailAddress email = user.getPrimaryEmailAddress();
            if (!email.getAddress().equals(emailAddress.getValue())) {
                email.setAddress(emailAddress.getValue());
            }
            user.setPasswordResetRequired(passwordResetRequired.getValue());
            user.setBanned(banned.getValue());

            userRepo.save(user);
            currentUser = user;
        }

        switch (passwordOptions.getValue()) {
            case GENERATE_AND_SEND: {
                userManager.updatePassword(currentUser, null);
                final ChallengeManager challengeManager = CdiUtil
                    .createCdiUtil()
                    .findBean(ChallengeManager.class);
                try {
                    challengeManager.sendPasswordRecover(currentUser);
                } catch (MessagingException ex) {
                    setComponentError(new UserError(bundle
                        .getString("ui.admin.user_form"
                                       + ".failed_to_send_password_challenge")));
                    LOGGER.error(
                        "Failed to send password challenge.",
                        ex);
                }
                break;
            }
            case SET:
                userManager.updatePassword(currentUser,
                                           passwordConfirmation.getValue());
                break;
        }

        dataHasChanged = false;
        if (usersGroupsRoles != null) {
            usersGroupsRoles.refreshUsers();
        }
        close();
    }

    private class DataHasChangedListener
        implements HasValue.ValueChangeListener<String> {

        private static final long serialVersionUID = -4698658552890778877L;

        @Override
        public void valueChange(final HasValue.ValueChangeEvent<String> event) {
            dataHasChanged = true;
        }

    }

    private class CheckBoxHasChangedListener implements
        HasValue.ValueChangeListener<Boolean> {

        private static final long serialVersionUID = 1986372149566327203L;

        @Override
        public void valueChange(final HasValue.ValueChangeEvent<Boolean> event) {
            dataHasChanged = true;
        }

    }

}
