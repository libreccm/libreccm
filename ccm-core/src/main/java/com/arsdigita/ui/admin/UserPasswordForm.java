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
package com.arsdigita.ui.admin;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.Password;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.mail.Mail;

import static com.arsdigita.ui.admin.AdminConstants.*;

import com.arsdigita.ui.login.PasswordValidationListener;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiLookupException;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.User;

/**
 * Form used to update a user's password. It just provides form elements to
 * enter the new password and confirm it. If the user doesn't have an
 * authentication record, it will be created as part of setting the new value of
 * the password.
 *
 * @version $Id$
 */
class UserPasswordForm extends Form
    implements FormInitListener,
               FormProcessListener,
               FormValidationListener {

    // Logging
    private static final Logger s_log = Logger.getLogger(UserPasswordForm.class
        .getName());

    // Constants
    final static String PASSWORD_FORM_NAME = "password-update";
    final static String NEW_PASSWORD_PARAM_NAME = "password-new";
    final static String CONFIRM_PASSWORD_PARAM_NAME = "password-confirm";

    private UserBrowsePane m_userBrowsePane;

    private TextField m_question;
    private TextField m_answer;
    private TextField m_ssoLogin;

    /**
     * Constructor.
     */
    public UserPasswordForm(UserBrowsePane pane) {
        super(PASSWORD_FORM_NAME);

        m_userBrowsePane = pane;

        setMethod(Form.POST);

        addInitListener(this);
        addValidationListener(this);
        addProcessListener(this);

        // Password
        Password newPassword = new Password(NEW_PASSWORD_PARAM_NAME);
        newPassword.addValidationListener(new PasswordValidationListener());
        add(PASSWORD_FORM_LABEL_PASSWORD);
        add(newPassword);

        // Password confirmation
        Password confirmPassword = new Password(CONFIRM_PASSWORD_PARAM_NAME);
        confirmPassword.addValidationListener(new NotNullValidationListener());
        add(PASSWORD_FORM_LABEL_CONFIRMATION_PASSWORD);
        add(confirmPassword);

        // Password question
        m_question
        = new TextField(new StringParameter(USER_FORM_INPUT_QUESTION));
        m_question.setSize(50);
        m_question.addValidationListener(new NotEmptyValidationListener());
        add(PASSWORD_FORM_LABEL_QUESTION);
        add(m_question);

        // Password answer
        m_answer = new TextField(new StringParameter(USER_FORM_INPUT_ANSWER));
        m_answer.setSize(50);
        add(PASSWORD_FORM_LABEL_ANSWER);
        add(m_answer);

        m_ssoLogin = new TextField(new StringParameter(USER_FORM_INPUT_SSO));
        m_ssoLogin.setSize(50);
        add(USER_FORM_LABEL_SSO);
        add(m_ssoLogin);

        // submit button
        add(new Label(""));
        add(new Submit(PASSWORD_FORM_SUBMIT));
    }

    /**
     * Initialize the form
     */
    @Override
    public void init(final FormSectionEvent event) {

        final PageState state = event.getPageState();

//        final CdiUtil cdiUtil = new CdiUtil();
//        final UserRepository userRepository;
//        try {
//            userRepository = cdiUtil.findBean(UserRepository.class);
//        } catch(CdiLookupException ex) {
//            throw new UncheckedWrapperException(ex);
//        }
//        final User user = userRepository.findById((Long) state.getValue(
//            USER_ID_PARAM));
//        
        
//        m_question.setValue(state, user.getPasswordQuestion());
//        m_ssoLogin.setValue(state, user.getSsoLogin());

        m_answer.setValue(state, "");
    }

    /**
     * Validate the form input.
     */
    @Override
    public void validate(final FormSectionEvent event)
        throws FormProcessException {
        PageState state = event.getPageState();
        FormData data = event.getFormData();
        HttpServletRequest req = state.getRequest();

//        final CdiUtil cdiUtil = new CdiUtil();
//        final UserRepository userRepository;
//        try {
//            userRepository = cdiUtil.findBean(UserRepository.class);
//        } catch(CdiLookupException ex) {
//            throw new UncheckedWrapperException(ex);
//        }
//        final User user = userRepository.findById((Long) state.getValue(
//            USER_ID_PARAM));
//        if (user == null) {
//            return;
//        }

        try {
            // get parameter values
            String newPassword = (String) data.get(NEW_PASSWORD_PARAM_NAME);
            String confirmPassword = (String) data.get(
                CONFIRM_PASSWORD_PARAM_NAME);

            // check new password
            if (!newPassword.equals(confirmPassword)) {
                data.addError(CONFIRM_PASSWORD_PARAM_NAME,
                              (String) USER_FORM_ERROR_PASSWORD_NOT_MATCH
                              .localize(req));
                return;
            }
        } finally {
            if (!data.isValid()) {
                // clear passwords from form data
                data.put(NEW_PASSWORD_PARAM_NAME, "");
                data.put(CONFIRM_PASSWORD_PARAM_NAME, "");
            }
        }

        // If the password answer is anything but null, make sure it
        // contains some non-whitespace characters
        String answer = (String) m_answer.getValue(state);
        if (answer != null && answer.length() > 0 && answer.trim().length() == 0) {
            data.addError(USER_FORM_INPUT_ANSWER,
                          (String) USER_FORM_ERROR_ANSWER_NULL.localize(req));
        }
    }

    /**
     * Process the form
     */
    public void process(FormSectionEvent event)
        throws FormProcessException {
        
        final PageState state = event.getPageState();
        final FormData data = event.getFormData();

        final CdiUtil cdiUtil = new CdiUtil();
//        final UserRepository userRepository;
//        final UserManager userManager;
//        try {
//            userRepository = cdiUtil.findBean(UserRepository.class);
//            userManager = cdiUtil.findBean(UserManager.class);
//        } catch(CdiLookupException ex) {
//            throw new UncheckedWrapperException(ex);
//        }
//        final User user = userRepository.findById((Long) state.getValue(
//            USER_ID_PARAM));
//        if (user == null) {
//            throw new FormProcessException(GlobalizationUtil.globalize(
//                "ui.admin.user.userpasswordform.retrieving_user_failed"));
//        }
        
//        userManager.updatePassword(user, (String) data.get(NEW_PASSWORD_PARAM_NAME));
//        user.setPasswordQuestion((String) m_question.getValue(state));
//        final String answer = (String) m_answer.getValue(state);
//        if (answer != null && answer.length() > 0) {
//            user.setPasswordAnswer(answer);
//        }
//        user.setSsoLogin((String) m_ssoLogin.getValue(state));
//        
//        userRepository.save(user);
        
        BigDecimal id = (BigDecimal) state.getValue(USER_ID_PARAM);

        s_log.debug("Committed password change");

//        notifyUser(user);
        m_userBrowsePane.displayUserInfoPanel(state);
    }

    /**
     * Notify user of the change to their password.
     *
     * TODO:
     * <ol>
     * <li>Message should be from the syadmin</li>
     * <li>Globalize the subject and content</li>
     * </ol>
     *
     * @param user is the User who's password was just changed.
     */
    private void notifyUser(final User user) {
        String to = user.getEmailAddresses().get(0).getAddress();
        String from = to;
        String subject = "Your password has been changed";
        String nl = System.getProperty("line.separator");

        StringBuffer sb = new StringBuffer();
        sb.append("Dear ");
        sb.append(user.getGivenName());
        sb.append(":");
        sb.append(nl).append(nl);
        sb.append("Your password has been changed by the ");
        sb.append("system administrator.");
        sb.append(nl);

        try {
            Mail.send(to, from, subject, sb.toString());
        } catch (javax.mail.MessagingException e) {
            s_log.error("Failed to notify user of password change");
        }
    }

}
