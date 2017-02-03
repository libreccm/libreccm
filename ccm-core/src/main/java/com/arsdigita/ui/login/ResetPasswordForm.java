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
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.web.URL;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.security.ChallengeFailedException;
import org.libreccm.security.ChallengeManager;
import org.libreccm.security.OneTimeAuthConfig;
import org.libreccm.security.OneTimeAuthManager;
import org.libreccm.security.OneTimeAuthToken;
import org.libreccm.security.OneTimeAuthTokenPurpose;
import org.libreccm.security.User;
import org.libreccm.security.UserRepository;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import static com.arsdigita.ui.login.LoginConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ResetPasswordForm extends Form {

    private static final String EMAIL = "email";
    private static final String AUTH_TOKEN = "authToken";
    private static final String PASSWORD = "password";
    private static final String PASSWORD_CONFIRMATION = "passwordconfirmation";

    private BoxPanel formPanel;
    private TextField email;
    private TextField authToken;
    private Password password;
    private Password passwordConfirmation;
    private SaveCancelSection saveCancelSection;
    private BoxPanel successPanel;

    public ResetPasswordForm() {
        super("reset-password");

        addWidgets();
        addListeners();
    }

    private void addWidgets() {
        formPanel = new BoxPanel(BoxPanel.VERTICAL);

        email = new TextField(EMAIL);
        email.setLabel(new GlobalizedMessage(
            "login.form.reset_password.email.label",
            LOGIN_BUNDLE));
        email.setHint(new GlobalizedMessage(
            "login.form.reset_password.email.hint",
            LOGIN_BUNDLE));
        email.setMaxLength(256);
        email.setSize(48);
        email.addValidationListener(new NotEmptyValidationListener());
        email.addValidationListener(new StringLengthValidationListener(256));
        formPanel.add(email);

        final ConfigurationManager confManager = CdiUtil.createCdiUtil()
            .findBean(ConfigurationManager.class);
        final OneTimeAuthConfig oneTimeAuthConfig = confManager
            .findConfiguration(OneTimeAuthConfig.class);
        authToken = new TextField(AUTH_TOKEN);
        authToken.setLabel(new GlobalizedMessage(
            "login.form.reset_password.auth_token.label", LOGIN_BUNDLE));
        authToken.setHint(new GlobalizedMessage(
            "login.form.reset_password.auth_token.hint", LOGIN_BUNDLE));
        authToken.setMaxLength(oneTimeAuthConfig.getTokenLength());
        authToken.setSize(oneTimeAuthConfig.getTokenLength());
        formPanel.add(authToken);

        password = new Password(PASSWORD);
        password.setLabel(new GlobalizedMessage(
            "login.form.reset_password.password.label", LOGIN_BUNDLE));
        password.setHint(new GlobalizedMessage(
            "login.form.reset_password.password.hint", LOGIN_BUNDLE));
        password.setMaxLength(256);
        password.setSize(32);
        password.addValidationListener(new NotEmptyValidationListener());
        password.addValidationListener(new StringLengthValidationListener(256));
        formPanel.add(password);

        passwordConfirmation = new Password(PASSWORD_CONFIRMATION);
        passwordConfirmation.setLabel(new GlobalizedMessage(
            "login.form.reset_password.password_confirmation.label",
            LOGIN_BUNDLE));
        passwordConfirmation.setHint(new GlobalizedMessage(
            "login.form.reset_password.password_confirmation.hint",
            LOGIN_BUNDLE));
        passwordConfirmation.setMaxLength(256);
        passwordConfirmation.setSize(32);
        passwordConfirmation.addValidationListener(
            new NotEmptyValidationListener());
        passwordConfirmation.addValidationListener(
            new StringLengthValidationListener(256));
        formPanel.add(passwordConfirmation);

        saveCancelSection = new SaveCancelSection();
        formPanel.add(saveCancelSection);

        add(formPanel);

        successPanel = new BoxPanel(BoxPanel.VERTICAL);
        successPanel.add(new Label(new GlobalizedMessage(
            "login.form.reset_password.scucess", LOGIN_BUNDLE)));
        successPanel.add(new Link(new Label(new GlobalizedMessage(
            "login.form.reset_password.scucess.login",
            LOGIN_BUNDLE)),
                                  URL.there(LOGIN_PAGE_URL, null).getURL()));

        add(successPanel);

    }

    private void addListeners() {
        addInitListener(e -> {
            final PageState state = e.getPageState();
            final HttpServletRequest request = state.getRequest();

            final String paramEmail = request.getParameter("email");
            final String paramToken = request.getParameter("token");

            if (paramEmail != null) {
                email.setValue(state, paramEmail);
            }

            if (paramToken != null) {
                authToken.setValue(state, paramToken);
            }
        });

        addValidationListener(e -> {
            final PageState state = e.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final FormData data = e.getFormData();

                final String emailData = data.getString(EMAIL);
                final String authTokenData = data.getString(AUTH_TOKEN);
                final String passwordData = data.getString(PASSWORD);
                final String passwordConfirmationData = data.getString(
                    PASSWORD_CONFIRMATION);

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final UserRepository userRepository = cdiUtil.findBean(
                    UserRepository.class);

                final Optional<User> user = userRepository.findByEmailAddress(
                    emailData);
                if (!user.isPresent()) {
                    data.addError(new GlobalizedMessage(
                        "login.form.reset_password.error", LOGIN_BUNDLE));
                    return;
                }

                final OneTimeAuthManager oneTimeAuthManager = cdiUtil.findBean(
                    OneTimeAuthManager.class);
                if (!oneTimeAuthManager.validTokenExistsForUser(
                    user.get(), OneTimeAuthTokenPurpose.RECOVER_PASSWORD)) {

                    data.addError(new GlobalizedMessage(
                        "login.form.reset_password.error", LOGIN_BUNDLE));
                    return;
                }

                final List<OneTimeAuthToken> tokens = oneTimeAuthManager
                    .retrieveForUser(
                        user.get(), OneTimeAuthTokenPurpose.RECOVER_PASSWORD);

                boolean result = false;
                for (OneTimeAuthToken token : tokens) {
                    if (oneTimeAuthManager.verify(token, authTokenData)) {
                        result = true;
                        break;
                    }
                }

                if (!result) {
                    data.addError(new GlobalizedMessage(
                        "login.form.reset_password.error", LOGIN_BUNDLE));
                    return;
                }

                if (!passwordData.equals(passwordConfirmationData)) {
                    data.addError(new GlobalizedMessage(
                        "login.form.reset_password.error.password_mismatch",
                        LOGIN_BUNDLE));
                }
            }
        });

        addProcessListener(e -> {
            final PageState state = e.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final FormData data = e.getFormData();

                final String emailData = data.getString(EMAIL);
                final String authTokenData = data.getString(AUTH_TOKEN);
                final String passwordData = data.getString(PASSWORD);

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final UserRepository userRepository = cdiUtil.findBean(
                    UserRepository.class);

                final Optional<User> user = userRepository.findByEmailAddress(
                    emailData);
                if (!user.isPresent()) {
                    throw new FormProcessException(
                        "No matching user found. This should not happen because "
                        + "we verified that just a few moments ago.",
                        new GlobalizedMessage(
                            "login.form.reset_password.error"));
                }

                final ChallengeManager challengeManager = cdiUtil.findBean(
                    ChallengeManager.class);
                try {
                    challengeManager.finishPasswordRecover(user.get(),
                                                           authTokenData,
                                                           passwordData);
                } catch (ChallengeFailedException ex) {
                    throw new FormProcessException(
                        "Failed to finish password recovery.",
                        new GlobalizedMessage(
                            "login.form.password_reset.error.failed"),
                        ex);
                }

                formPanel.setVisible(state, false);
                successPanel.setVisible(state, true);
            }
        });
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.setVisibleDefault(formPanel, true);
        page.setVisibleDefault(successPanel, false);
    }

}
