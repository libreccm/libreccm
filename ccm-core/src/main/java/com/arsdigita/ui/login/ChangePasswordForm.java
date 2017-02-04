/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.ui.login;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Password;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.URLParameter;
import com.arsdigita.ui.UI;
import com.arsdigita.web.URL;
import com.arsdigita.web.ReturnSignal;

import javax.servlet.http.HttpServletRequest;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.User;

import org.libreccm.security.Shiro;
import org.libreccm.security.UserManager;

import java.util.Optional;

/**
 * A Form that allows a user to change their password by entering their old
 * password, a new password, and a confirmation of their new password. Requires
 * that the user is logged in. Requires that new password differ from old and
 * meet strong password requirements. If the user is recovering from a lost
 * password (UserContext.isRecovering() is true), does not require or display
 * the old password parameter and does not require that new password differ from
 * old. Mails the user to notify of password change. Redirects user to workspace
 * or return_url if set.
 *
 * @author Sameer Ajmani
 *
 */
public class ChangePasswordForm extends Form
    implements FormProcessListener,
               FormValidationListener {

    final static String CHANGE_PASSWORD_FORM_NAME = "change-password";
    final static String OLD_PASSWORD_PARAM_NAME = "old-password";
    final static String NEW_PASSWORD_PARAM_NAME = "new-password";
    final static String CONFIRM_PASSWORD_PARAM_NAME = "confirm-password";
    final static String RETURN_URL_PARAM_NAME
                            = LoginHelper.RETURN_URL_PARAM_NAME;
    private final UserAuthenticationListener m_listener
                                                 = new UserAuthenticationListener();
    private Hidden m_returnURL;
//    private Hidden m_recovery;
    private Label m_oldPasswordLabel;
    private Password m_oldPassword;
    private Password m_newPassword;
    private Password m_confirmPassword;

    public ChangePasswordForm() {
        this(new BoxPanel());
    }

    @Override
    public void register(final Page page) {
        super.register(page);
        page.addRequestListener(m_listener);
        page.addActionListener((final ActionEvent event) -> {
            PageState state = event.getPageState();
            m_oldPasswordLabel.setVisible(state, true);
            m_oldPassword.setVisible(state, true);
        });
    }

    public ChangePasswordForm(Container panel) {
        super(CHANGE_PASSWORD_FORM_NAME, panel);

        setMethod(Form.POST);
        addValidationListener(this);
        addProcessListener(this);

        // save the recovery credential as a hidden parameter
//        m_recovery = new Hidden(new StringParameter(RecoveryLoginModule.getParamName()));
//        m_recovery.setPassIn(true);
//        add(m_recovery);
        // save the return URL as a hidden parameter
        m_returnURL = new Hidden(new URLParameter(RETURN_URL_PARAM_NAME));
        m_returnURL.setPassIn(true);
        add(m_returnURL);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final Shiro shiro = cdiUtil.findBean(Shiro.class);
        final Optional<User> user = shiro.getUser();

        final Label greeting;
        if (user.isPresent()) {
             greeting = new Label(LoginHelper.getMessage(
                "login.changePasswordForm.greeting",
                new Object[]{String.format("%s %s",
                                           user.get().getGivenName(),
                                           user.get().getFamilyName())}));
        } else {
            greeting = new Label(LoginHelper.getMessage(
                "login.changePasswordForm.greeting",
                new Object[]{String.format("%s %s",
                                           "",
                                           "")}));
        }
        greeting.setFontWeight(Label.BOLD);
        greeting.setClassAttr("greeting");
        add(greeting);

        add(new Label(LoginHelper.getMessage(
            "login.changePasswortForm.introText")));

        // old password
        m_oldPasswordLabel = new Label(LoginHelper.getMessage(
            "login.changePasswordForm.oldPasswordLabel"));
        add(m_oldPasswordLabel);
        m_oldPassword = new Password(OLD_PASSWORD_PARAM_NAME);
        // don't use NotNullValidationListener because
        // old password may be null during password recovery
        add(m_oldPassword);

        // new password
        Object[] params = new Object[]{PasswordValidationListener.MIN_LENGTH};
        add(new Label(LoginHelper.getMessage(
            "login.changePasswordForm.newPasswordLabel", params)));
        m_newPassword = new Password(NEW_PASSWORD_PARAM_NAME);
        m_newPassword.addValidationListener(new PasswordValidationListener());
        add(m_newPassword);

        // confirm new password
        add(new Label(LoginHelper.getMessage(
            "login.changePasswordForm.confirmPasswordLabel")));
        m_confirmPassword = new Password(CONFIRM_PASSWORD_PARAM_NAME);
        // don't use PasswordValidationListener to avoid duplicate errors
        m_confirmPassword.addValidationListener(new NotNullValidationListener());
        add(m_confirmPassword);

        // submit
        add(new Submit(LoginHelper.getMessage("login.changePasswordForm.submit")),
            ColumnPanel.CENTER | ColumnPanel.FULL_WIDTH);
    }

    @Override
    public void validate(final FormSectionEvent event)
        throws FormProcessException {
        PageState state = event.getPageState();
        FormData data = event.getFormData();
        try {
            // get user object
            if (!m_listener.isLoggedIn(state)) {
                // this error should never appear
                data.addError(LoginHelper.localize(
                    "login.changePasswordForm.noUserError",
                    state.getRequest()));
                return;
            }
//            User user = m_listener.getUser(state);

            // get parameter values
            String oldPassword = (String) m_oldPassword.getValue(state);
            String newPassword = (String) m_newPassword.getValue(state);
            String confirmPassword = (String) m_confirmPassword.getValue(state);

            //check oldPassword
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final Shiro shiro = cdiUtil.findBean(Shiro.class);
            final UserManager userManager = cdiUtil.findBean(UserManager.class);

            final User user = shiro.getUser().get();
            if (!userManager.verifyPassword(user, oldPassword)) {
                data.addError(OLD_PASSWORD_PARAM_NAME, LoginHelper.getMessage(
                              "login.changePasswordForm.badPasswordError"));
            }

            // check new password
            if (newPassword.equals(oldPassword)) {
                data.addError(NEW_PASSWORD_PARAM_NAME, LoginHelper.localize(
                              "login.changePasswordForm.mustDifferError",
                              state.getRequest()));
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                data.addError(CONFIRM_PASSWORD_PARAM_NAME, LoginHelper.localize(
                              "login.changePasswordForm.mustMatchError",
                              state.getRequest()));
                return;
            }
        } finally {
            if (!data.isValid()) {
                // clear passwords from form data
                m_oldPassword.setValue(state, "");
                m_newPassword.setValue(state, "");
                m_confirmPassword.setValue(state, "");
            }
        }
    }

    @Override
    public void process(final FormSectionEvent event)
        throws FormProcessException {
        PageState state = event.getPageState();
        FormData data = event.getFormData();

        // get user object
        if (!m_listener.isLoggedIn(state)) {
            // this error should never appear (checked in validate)
            data.addError(LoginHelper.localize(
                "login.changePasswordForm.noUserError",
                state.getRequest()));
            return;
        }

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final UserManager userManager = cdiUtil.findBean(UserManager.class);
        final Shiro shiro = cdiUtil.findBean(Shiro.class);
        final User user = shiro.getUser().get();

        final String newPassword = (String) m_newPassword.getValue(state);
        userManager.updatePassword(user, newPassword);

        final HttpServletRequest req = state.getRequest();

        final String path = UI.getWorkspaceURL(req);

        final URL fallback = URL.there(req, path);

        throw new ReturnSignal(req, fallback);
    }

}
