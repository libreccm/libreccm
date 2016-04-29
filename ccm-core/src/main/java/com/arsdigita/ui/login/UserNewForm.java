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
package com.arsdigita.ui.login;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.form.Password;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.security.SecurityConfig;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;

import org.apache.logging.log4j.util.Strings;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.security.RegistrationManager;
import org.libreccm.security.Shiro;
import org.libreccm.security.UserRepository;


import javax.mail.MessagingException;

import static com.arsdigita.ui.login.LoginConstants.*;
import static com.arsdigita.ui.login.LoginServlet.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class UserNewForm extends Form {

//    private static final Logger LOGGER = LogManager.getLogger(UserNewForm.class);
    private static final String USERNAME = "username";
    private static final String GIVEN_NAME = "givenname";
    private static final String FAMILY_NAME = "familyname";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String PASSWORD_CONFIRMATION = "passwordconfirmation";

    private BoxPanel formPanel;
    private TextField userName;
    private TextField givenName;
    private TextField familyName;
    private TextField email;
    private Password password;
    private Password passwordConfirm;
    private SaveCancelSection saveCancelSection;
    private BoxPanel finishedMessagePanel;

    public UserNewForm() {
        super("user-new");

        addWidgets();
        addListeners();
    }

    private void addWidgets() {
        formPanel = new BoxPanel(BoxPanel.VERTICAL);

        userName = new TextField(USERNAME);
        userName.setLabel(new GlobalizedMessage(
            "login.form.new_user.username.label", LOGIN_BUNDLE));
        userName.setHint(new GlobalizedMessage(
            "login.form.new_user.username.hint", LOGIN_BUNDLE));
        userName.setMaxLength(32);
        userName.setSize(32);
        formPanel.add(userName);

        givenName = new TextField(GIVEN_NAME);
        givenName.setLabel(new GlobalizedMessage(
            "login.form.new_user.givenname.label", LOGIN_BUNDLE));
        givenName.setHint(new GlobalizedMessage(
            "login.form.new_user.givenname.hint", LOGIN_BUNDLE));
        givenName.setMaxLength(256);
        givenName.setSize(32);
        formPanel.add(givenName);

        familyName = new TextField(FAMILY_NAME);
        familyName.setLabel(new GlobalizedMessage(
            "login.form.new_user.familyname.label", LOGIN_BUNDLE));
        familyName.setHint(new GlobalizedMessage(
            "login.form.new_user.familyname.hint", LOGIN_BUNDLE));
        familyName.setMaxLength(256);
        familyName.setSize(32);
        formPanel.add(familyName);

        email = new TextField(EMAIL);
        email.setLabel(new GlobalizedMessage("login.form.new_user.email.label",
                                             LOGIN_BUNDLE));
        email.setHint(new GlobalizedMessage("login.form.new_user.email.hint",
                                            LOGIN_BUNDLE));
        email.setMaxLength(256);
        email.setSize(48);
        formPanel.add(email);

        password = new Password(PASSWORD);
        password.setLabel(new GlobalizedMessage(
            "login.form.new_user.password.label", LOGIN_BUNDLE));
        password.setHint(new GlobalizedMessage(
            "login.form.new_user.password.hint", LOGIN_BUNDLE));
        password.setMaxLength(256);
        password.setSize(32);
        formPanel.add(password);

        passwordConfirm = new Password(PASSWORD_CONFIRMATION);
        passwordConfirm.setLabel(new GlobalizedMessage(
            "login.form.new_user.password_confirmation.label", LOGIN_BUNDLE));
        passwordConfirm.setHint(new GlobalizedMessage(
            "login.form.new_user.password_confirmation.hint", LOGIN_BUNDLE));
        passwordConfirm.setMaxLength(256);
        passwordConfirm.setSize(32);
        formPanel.add(passwordConfirm);

        saveCancelSection = new SaveCancelSection();
        formPanel.add(saveCancelSection);

        add(formPanel);

        finishedMessagePanel = new BoxPanel(BoxPanel.VERTICAL);
        finishedMessagePanel.add(new Label(new GlobalizedMessage(
            "login.form.new_user.finshed_message", LOGIN_BUNDLE)));
        final Link link = new Link(
            new Label(
                new GlobalizedMessage(
                    "login.form.new_user.finished_message.activate_link",
                    LOGIN_BUNDLE)),
            LOGIN_PAGE_URL + ACTIVATE_ACCOUNT_PATH_INFO);
        finishedMessagePanel.add(link);

        add(finishedMessagePanel);
    }

    private void addListeners() {
        addSubmissionListener(e -> {
            final PageState state = e.getPageState();

            if (saveCancelSection.getCancelButton().isSelected(state)) {
                throw new RedirectSignal(URL.there(state.getRequest(),
                                                   LOGIN_PAGE_URL),
                                         false);
            }
        });

        addValidationListener(e -> {
            final PageState state = e.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final FormData data = e.getFormData();

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ConfigurationManager confManager = cdiUtil.findBean(
                    ConfigurationManager.class);
                final SecurityConfig securityConfig = confManager
                    .findConfiguration(
                        SecurityConfig.class);
                if (!securityConfig.isAutoRegistrationEnabled()) {
                    data.addError(new GlobalizedMessage(
                        "login.form.new_user.error.autoregistration_not_enabled",
                        LOGIN_BUNDLE));
                    return;
                }

                final String userNameData = data.getString(USERNAME);
                final String givenNameData = data.getString(GIVEN_NAME);
                final String familyNameData = data.getString(FAMILY_NAME);
                final String emailData = data.getString(EMAIL);
                final String passwordData = data.getString(PASSWORD);
                final String passwordConfirmationData = data.getString(
                    PASSWORD_CONFIRMATION);

                if (Strings.isBlank(userNameData)) {
                    data.addError(
                        USERNAME,
                        new GlobalizedMessage(
                            "login.form.new_user.error.username.is_blank",
                            LOGIN_BUNDLE));
                    return;
                }
                if (userNameData != null && userNameData.length() > 32) {
                    data.addError(
                        USERNAME,
                        new GlobalizedMessage(
                            "login.form.new_user.error.username.too_long",
                            LOGIN_BUNDLE));
                    return;
                }

                if (Strings.isBlank(givenNameData)) {
                    data.addError(
                        GIVEN_NAME,
                        new GlobalizedMessage(
                            "login.form.new_user.error.givenname.is_blank",
                            LOGIN_BUNDLE));
                    return;
                }
                if (givenNameData != null && givenNameData.length() > 256) {
                    data.addError(
                        GIVEN_NAME,
                        new GlobalizedMessage(
                            "login.form.new_user.error.givename.too_long",
                            LOGIN_BUNDLE));
                    return;
                }

                if (Strings.isBlank(familyNameData)) {
                    data.addError(
                        FAMILY_NAME,
                        new GlobalizedMessage(
                            "login.form.new_user.error.familyname.is_blank",
                            LOGIN_BUNDLE));
                    return;
                }
                if (familyNameData != null && familyNameData.length() > 256) {
                    data.addError(
                        FAMILY_NAME,
                        new GlobalizedMessage(
                            "login.form.new_user.error.familyname.too_long",
                            LOGIN_BUNDLE));
                    return;
                }

                if (Strings.isBlank(emailData)) {
                    data.addError(
                        EMAIL,
                        new GlobalizedMessage(
                            "login.form.new_user.error.email.is_blank",
                            LOGIN_BUNDLE));
                    return;
                }
                if (emailData != null && emailData.length() > 256) {
                    data.addError(
                        EMAIL,
                        new GlobalizedMessage(
                            "login.form.new_user.error.email.too_long",
                            LOGIN_BUNDLE));
                    return;
                }

                if (Strings.isBlank(passwordData)) {
                    data.addError(
                        PASSWORD,
                        new GlobalizedMessage(
                            "login.form.new_user.error.password.is_blank",
                            LOGIN_BUNDLE));
                    return;
                }

                if (Strings.isBlank(passwordConfirmationData)) {
                    data.addError(
                        PASSWORD_CONFIRMATION,
                        new GlobalizedMessage(
                            "login.form.new_user.error.password.is_blank",
                            LOGIN_BUNDLE));
                    return;
                }

                final UserRepository userRepository = cdiUtil.findBean(
                    UserRepository.class);
                //check if there is already an account for the provided email
                if (userRepository.findByEmailAddress(emailData) != null) {
                    data.addError(
                        EMAIL,
                        new GlobalizedMessage(
                            "login.form.new_user.error.email_already_registered",
                            LOGIN_BUNDLE));
                    return;
                }

                //check if username is already in use
                if (userRepository.findByName(userNameData) != null) {
                    data.addError(
                        USERNAME,
                        new GlobalizedMessage(
                            "login.form.new_user.error.username_already_in_use",
                            LOGIN_BUNDLE));
                    return;
                }

                //Check if password and confirmation match
                if (!passwordData.equals(passwordConfirmationData)) {
                    data.addError(
                        PASSWORD,
                        new GlobalizedMessage(
                            "login.form.new_user.error.passwords_do_not_match",
                            LOGIN_BUNDLE));
                }
            }
        });

        addProcessListener(e -> {
            final PageState state = e.getPageState();
            if (saveCancelSection.getSaveButton().isSelected(state)) {
                //Neuen User anlegen, mit banned = true
                final FormData data = e.getFormData();

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();

                final Shiro shiro = cdiUtil.findBean(Shiro.class);
                shiro.getSystemUser().execute(() -> {
//                    final UserRepository userRepository = cdiUtil.findBean(
//                        UserRepository.class);
//                    final UserManager userManager = cdiUtil.findBean(
//                        UserManager.class);
//
//                    final String givenNameData = (String) data.get(
//                        GIVEN_NAME);
//                    final String familyNameData = (String) data
//                        .get(FAMILY_NAME);
//                    final String username = (String) data.get(USERNAME);
//                    final String emailAddress = (String) data.get(EMAIL);
//                    final String passwordData = (String) data.get(PASSWORD);
//                    final User user = userManager.createUser(givenNameData,
//                                                             familyNameData,
//                                                             username,
//                                                             emailAddress,
//                                                             passwordData);
//                    user.setBanned(true);
//                    userRepository.save(user);
//
//                    //challenge erzeugen
//                    final ChallengeManager challengeManager = cdiUtil
//                        .findBean(ChallengeManager.class);
//                    try {
//                        challengeManager.sendAccountActivation(user);
//                    } catch (MessagingException ex) {
//                        throw new FormProcessException(
//                            "Failed to send account activation challenge.",
//                            new GlobalizedMessage(
//                                "login.form_new_user.error.creating_challenge_failed",
//                                LOGIN_BUNDLE), ex);
//                    }

                    final RegistrationManager registrationManager = cdiUtil
                        .findBean(RegistrationManager.class);
                    try {
                        registrationManager.registerUser(
                            data.getString(USERNAME),
                            data.getString(FAMILY_NAME),
                            data.getString(GIVEN_NAME),
                            data.getString(EMAIL),
                            data.getString(PASSWORD));
                    } catch (MessagingException ex) {
                        throw new FormProcessException(
                            "Failed to send account activation challenge.",
                            new GlobalizedMessage(
                                "login.form_new_user.error.creating_challenge_failed",
                                LOGIN_BUNDLE), ex);
                    }

                    formPanel.setVisible(state, false);
                    finishedMessagePanel.setVisible(state, true);
                    return null;
                });
            }
        });
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.setVisibleDefault(formPanel, true);
        page.setVisibleDefault(finishedMessagePanel, false);
    }

}
