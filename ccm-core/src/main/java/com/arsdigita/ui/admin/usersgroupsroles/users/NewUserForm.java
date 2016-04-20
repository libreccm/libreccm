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

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.Password;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.globalization.GlobalizedMessage;

import org.apache.logging.log4j.util.Strings;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.ChallengeManager;
import org.libreccm.security.User;
import org.libreccm.security.UserManager;
import org.libreccm.security.UserRepository;

import javax.mail.MessagingException;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class NewUserForm extends Form {

    private static final String USER_NAME = "userName";
    private static final String FAMILY_NAME = "familyName";
    private static final String GIVEN_NAME = "givenName";
    private static final String EMAIL = "email";
    private static final String PASSWORD_OPTIONS = "passwordOptions";
    private static final String PASSWORD_OPTION_SET = "passwordOptionSet";
    private static final String PASSWORD_OPTION_SEND = "passwordOptionSend";
    private static final String PASSWORD = "password";
    private static final String PASSWORD_CONFIRMATION = "passwordConfirmation";

    private final TextField userName;
    private final TextField familyName;
    private final TextField givenName;
    private final TextField email;
    private final RadioGroup passwordOptionsGroup;
    private final Password password;
    private final Password passwordConfirmation;

    private final SaveCancelSection saveCancelSection;

    public NewUserForm(
        final UserAdmin userAdmin,
        final ParameterSingleSelectionModel<String> selectedUserId) {

        super("new_user_form");

        userName = new TextField(USER_NAME);
        userName.setLabel(new GlobalizedMessage(
            "ui.admin.new_user_form.username.label", ADMIN_BUNDLE));
        userName.setSize(32);
        userName.setMaxLength(32);
        add(userName);

        familyName = new TextField(FAMILY_NAME);
        familyName.setLabel(new GlobalizedMessage(
            "ui.admin.new_user_form.familyname.label", ADMIN_BUNDLE));
        familyName.setSize(32);
        familyName.setMaxLength(256);
        add(familyName);

        givenName = new TextField(GIVEN_NAME);
        givenName.setLabel(new GlobalizedMessage(
            "ui.admin.new_user_form.givenname.label", ADMIN_BUNDLE));
        givenName.setSize(32);
        givenName.setMaxLength(256);
        add(givenName);

        email = new TextField(EMAIL);
        email.setLabel(new GlobalizedMessage(
            "ui.admin.new_user_form.email.label", ADMIN_BUNDLE));
        email.setSize(48);
        email.setMaxLength(256);
        add(email);

        passwordOptionsGroup = new RadioGroup(PASSWORD_OPTIONS);
        final Option sendPasswordOption = new Option(
            PASSWORD_OPTION_SEND,
            new Label(new GlobalizedMessage(
                "ui.admin.new_user_form.password_options.send_password.label",
                ADMIN_BUNDLE)));
        passwordOptionsGroup.addOption(sendPasswordOption);
        final Option setPasswordOption = new Option(
            PASSWORD_OPTION_SET,
            new Label(new GlobalizedMessage(
                "ui.admin.new_user_form.password_options.set_password",
                ADMIN_BUNDLE)));
        passwordOptionsGroup.addOption(setPasswordOption);
        add(passwordOptionsGroup);

        password = new Password(PASSWORD);
        password.setLabel(new GlobalizedMessage(
            "ui.admin.new_user_form.password_options.set_password.password.label",
            ADMIN_BUNDLE));
        password.setMaxLength(256);
        password.setSize(32);
        add(password);

        passwordConfirmation = new Password(PASSWORD_CONFIRMATION);
        passwordConfirmation.setLabel(new GlobalizedMessage(
            "ui.admin.new_user_form.password_options."
                + "set_password.password_confirmation.label",
            ADMIN_BUNDLE));
        passwordConfirmation.setMaxLength(256);
        passwordConfirmation.setSize(32);
        add(passwordConfirmation);

        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);

        addValidationListener(e -> {
            final PageState state = e.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final FormData data = e.getFormData();

                final String userNameData = data.getString(USER_NAME);
                final String familyNameData = data.getString(FAMILY_NAME);
                final String givenNameData = data.getString(GIVEN_NAME);
                final String emailData = data.getString(EMAIL);

                if (Strings.isBlank(userNameData)) {
                    data.addError(
                        USER_NAME,
                        new GlobalizedMessage(
                            "ui.admin.new_user_form.error.username.is_empty",
                            ADMIN_BUNDLE));
                }
                if (userNameData != null && userNameData.length() > 32) {
                    data.addError(
                        USER_NAME,
                        new GlobalizedMessage(
                            "ui.admin.new_user_form.error.username.too_long",
                            ADMIN_BUNDLE));
                }

                if (Strings.isBlank(familyNameData)) {
                    data.addError(
                        FAMILY_NAME,
                        new GlobalizedMessage(
                            "ui.admin.new_user_form.error.familyname.is_empty",
                            ADMIN_BUNDLE));
                }
                if (familyNameData != null && familyNameData.length() > 256) {
                    data.addError(
                        FAMILY_NAME,
                        new GlobalizedMessage(
                            "ui.admin.new_user_form.error.familyname.too_long",
                            ADMIN_BUNDLE));
                }

                if (Strings.isBlank(givenNameData)) {
                    data.addError(
                        GIVEN_NAME,
                        new GlobalizedMessage(
                            "ui.admin.new_user_form.error.givenname.is_empty",
                            ADMIN_BUNDLE));
                }
                if (givenNameData != null && givenNameData.length() > 256) {
                    data.addError(
                        GIVEN_NAME,
                        new GlobalizedMessage(
                            "ui.admin.new_user_form.error.givenname.too_long",
                            ADMIN_BUNDLE));
                }

                if (Strings.isBlank(emailData)) {
                    data.addError(
                        EMAIL,
                        new GlobalizedMessage(
                            "ui.admin.new_user_form.error.email.is_empty",
                            ADMIN_BUNDLE));
                }
                if (emailData != null && emailData.length() > 256) {
                    data.addError(
                        EMAIL,
                        new GlobalizedMessage(
                            "ui.admin.new_user_form.error.email.too_long",
                            ADMIN_BUNDLE));
                }

                final String selectedPasswordOption = data.getString(
                    PASSWORD_OPTIONS);

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final UserRepository userRepository = cdiUtil.findBean(
                    UserRepository.class);

                if (userRepository.findByName(userNameData) != null) {
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

                if (PASSWORD_OPTION_SET.equals(selectedPasswordOption)) {
                    final String passwordData = data.getString(PASSWORD);
                    final String passwordConfirmData = data.getString(
                        PASSWORD_CONFIRMATION);

                    if (Strings.isBlank(passwordData)) {
                        data.addError(
                            PASSWORD,
                            new GlobalizedMessage(
                                "ui.admin.new_user_form.error.password.is_empty",
                                ADMIN_BUNDLE));
                    }

                    if (Strings.isBlank(passwordConfirmData)) {
                        data.addError(
                            PASSWORD_CONFIRMATION,
                            new GlobalizedMessage(
                                "ui.admin.new_user_form.error.password.is_empty",
                                ADMIN_BUNDLE));
                    }

                    if (!passwordData.equals(passwordConfirmData)) {
                        data.addError(
                            PASSWORD,
                            new GlobalizedMessage(
                                "ui.admin.new_user_form.error.password_do_not_match",
                                ADMIN_BUNDLE));
                    }
                }
            }
        });
        
        addProcessListener(e -> {
            final PageState state = e.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final FormData data = e.getFormData();

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final UserManager userManager = cdiUtil.findBean(
                    UserManager.class);

                final String dataUserName = data.getString(USER_NAME);
                final String dataFamilyName = data.getString(FAMILY_NAME);
                final String dataGivenName = data.getString(GIVEN_NAME);
                final String dataEmail = data.getString(EMAIL);
                final String dataSelectedPasswordOption = data.getString(
                    PASSWORD_OPTIONS);

                final String dataPassword;
                if (PASSWORD_OPTION_SET.equals(dataSelectedPasswordOption)) {
                    dataPassword = data.getString(PASSWORD);
                } else {
                    dataPassword = null;
                }

                final User user = userManager.createUser(dataGivenName,
                                                         dataFamilyName,
                                                         dataUserName,
                                                         dataEmail,
                                                         dataPassword);

                if (PASSWORD_OPTION_SEND.equals(dataSelectedPasswordOption)) {
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
            }
            
             userAdmin.closeNewUserForm(state);
        });
    }

}
