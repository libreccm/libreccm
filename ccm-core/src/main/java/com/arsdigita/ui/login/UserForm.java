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

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.Password;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.EmailParameter;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.util.UncheckedWrapperException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiLookupException;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.User;

/**
 * Common code for user new / add / edit forms.
 *
 * @author Admin UI Team
 * @version $Id$
 *
 */
public abstract class UserForm extends Form
    implements LoginConstants, FormInitListener, FormValidationListener {

    private static final Logger s_log = Logger.getLogger(UserForm.class
        .getName());

    private boolean m_newUser;

    protected TextField m_firstName;
    protected TextField m_lastName;
    protected TextField m_email;
    protected TextField m_screenName;
    //protected TextField m_additional;
    protected Password m_password;
    protected Password m_confirm;
    protected TextField m_question;
    protected TextField m_answer;

    protected Label m_securitySectionHeader = new Label(LoginHelper
        .getMessage("login.userNewForm.securitySectionHeader"), false);
    protected Label m_securityBlurb = new Label(LoginHelper
        .getMessage("login.userNewForm.securityBlurb"));
    protected Label m_passwordBlurb = new Label(LoginHelper
        .getMessage("login.userNewForm.passwordBlurb"));
    protected Label m_passwordLabel = new Label(PASSWORD);
    protected Label m_confirmationLabel = new Label(PASSWORD_CONFIRMATION);
    protected Label m_questionBlurb = new Label(LoginHelper
        .getMessage("login.userNewForm.questionBlurb"));
    protected Label m_questionLabel = new Label(PASSWORD_QUESTION);
    protected Label m_answerLabel = new Label(PASSWORD_ANSWER);
    protected PasswordValidationListener m_passwordValidationListener
                                             = new PasswordValidationListener();
    protected NotEmptyValidationListener m_confirmationNotEmptyValidationListener
                                         = new NotEmptyValidationListener();
    protected Submit m_submit = new Submit(SUBMIT);
    protected Label m_firstNameLabel = new Label(FIRST_NAME);
    protected Label m_lastNameLabel = new Label(LAST_NAME);
    protected Label m_urlLabel = new Label(URL);
    protected Label m_screenNameLabel = new Label(SCREEN_NAME);
    protected Label m_emailLabel = new Label(PRIMARY_EMAIL);

    protected Container m_profilePart = new FormSection();
    protected Container m_securityPart = new FormSection();
    protected Container m_submitPart = new FormSection();

    /**
     * Create a UserForm with the given name and panel.
     *
     */
    public UserForm(String name, Container panel, boolean newUser) {
        super(name, panel);

        m_newUser = newUser;

        setMethod(Form.POST);
        addInitListener(this);
        addValidationListener(this);

        if (m_newUser) {
            m_profilePart.add(new Label(LoginHelper
                .getMessage("login.userNewForm.aboutYouSectionHeader"),
                                        false), ColumnPanel.FULL_WIDTH);
        }

        // SDM #163373: add length checking for first/last names.  We do
        // this with both maximum length parameters in the user/add form and
        // with validation of the value that come in for processing.
        m_firstName = new TextField(new StringParameter(FORM_FIRST_NAME));
        m_firstName.setMaxLength(MAX_NAME_LEN);
        m_firstName.setSize(20);
        m_firstName.addValidationListener(new NotEmptyValidationListener());
        m_firstName.addValidationListener(new StringLengthValidationListener(
            MAX_NAME_LEN));

        m_profilePart.add(m_firstNameLabel);
        m_profilePart.add(m_firstName);

        m_lastName = new TextField(new StringParameter(FORM_LAST_NAME));
        m_lastName.setMaxLength(MAX_NAME_LEN);
        m_lastName.setSize(25);
        m_lastName.addValidationListener(new NotEmptyValidationListener());
        m_lastName.addValidationListener(new StringLengthValidationListener(
            MAX_NAME_LEN));

        m_profilePart.add(m_lastNameLabel);
        m_profilePart.add(m_lastName);

        m_profilePart.add(m_screenNameLabel);
        m_screenName = new TextField(new StringParameter(FORM_SCREEN_NAME));
        m_screenName.addValidationListener(new NotEmptyValidationListener());
        m_profilePart.add(m_screenName);

        // Primary email address
        m_email = new TextField(new EmailParameter(FORM_EMAIL));
        m_email.addValidationListener(new NotEmptyValidationListener());

        m_profilePart.add(m_emailLabel);
        m_profilePart.add(m_email);

        // TODO: support additional emails
        // Additional email addresses
        //m_additional = new TextField(new EmailParameter
        //                             (FORM_ADDITIONAL_EMAIL));
        //add(new Label(ADDITIONAL_EMAIL));
        //add(m_additional);
        // SDM #162740: disable user bio for now, as there
        // is no support for User Bio in the kernel level.
        // add(new Label(BIO));
        // TextArea bioText = new TextArea(new StringParameter(FORM_BIO));
        // bioText.setCols(50);
        // bioText.setRows(10);
        // add(bioText);
        // add(new Label(""));
        if (m_newUser) {
            m_securityPart.add(new Label(LoginHelper
                .getMessage("login.userNewForm.securitySectionHeader"),
                                         false), ColumnPanel.FULL_WIDTH);

            m_securityPart.add(new Label(LoginHelper
                .getMessage("login.userNewForm.securityBlurb")),
                               ColumnPanel.FULL_WIDTH);

            m_securityPart.add(new Label(LoginHelper
                .getMessage("login.userNewForm.passwordBlurb")),
                               ColumnPanel.FULL_WIDTH);

            // Password
            m_password = new Password(new StringParameter(FORM_PASSWORD));
            m_password.addValidationListener(new PasswordValidationListener());

            m_securityPart.add(m_passwordLabel);
            m_securityPart.add(m_password);

            // Password confirmation
            m_confirm = new Password(new StringParameter(
                FORM_PASSWORD_CONFIRMATION));
            m_confirm.addValidationListener(new NotEmptyValidationListener());

            m_securityPart.add(m_confirmationLabel);
            m_securityPart.add(m_confirm);

            m_securityPart.add(new Label(LoginHelper
                .getMessage("login.userNewForm.questionBlurb")),
                               ColumnPanel.FULL_WIDTH);

            // Password question
            m_question = new TextField(new StringParameter(
                FORM_PASSWORD_QUESTION));
            m_question.setSize(30);
            m_question.addValidationListener(new NotEmptyValidationListener());

            m_securityPart.add(m_questionLabel);
            m_securityPart.add(m_question);

            // Password answer
            m_answer = new TextField(new StringParameter(FORM_PASSWORD_ANSWER));
            m_answer.setSize(30);
            m_answer.addValidationListener(new NotEmptyValidationListener());

            m_securityPart.add(m_answerLabel);
            m_securityPart.add(m_answer);
        }

        // Submit
        m_submitPart.add(m_submit, ColumnPanel.CENTER | ColumnPanel.FULL_WIDTH);

        add(m_profilePart);
        add(m_securityPart);
        add(m_submitPart);
    }

    /**
     * Initializes this form with data from the user.
     *
     * @param event
     *
     * @throws com.arsdigita.bebop.FormProcessException
     *
     */
    @Override
    public void init(FormSectionEvent event)
        throws FormProcessException {
        PageState state = event.getPageState();

        User user = getUser(state);
        if (user == null) {
            throw new FormProcessException(LoginGlobalizationUtil.globalize(
                "login.userForm.couldnt_load_user"));
        }
        m_firstName.setValue(state, user.getGivenName());
        m_lastName.setValue(state, user.getFamilyName());

        InternetAddress address;
        try {
            address = new InternetAddress(user.getEmailAddresses().get(0)
                .toString());
        } catch (AddressException e) {
            String[] errorMsg = new String[1];
            errorMsg[0] = user.getEmailAddresses().get(0).toString();
            throw new FormProcessException(
                "Email address is bad: " + user.getEmailAddresses().get(0),
                LoginHelper.getMessage("login.error.badEmail", errorMsg)
            );
        }

        m_email.setValue(state, address);
        m_screenName.setValue(state, user.getName());

    }

    /**
     * Gets the current user for initialising the form.
     *
     * @param state
     * @return the current user, if the form should not be initialised with user
     *         data.
     */
    protected abstract User getUser(final PageState state);

    /**
     * Validates this form. Verifies that the password and password-confirm
     * fields match. If not it adds an error to the password-confirm field. Also
     * verifies that primary email address and screen name are unique among all
     * users.
     *
     * @param event
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void validate(final FormSectionEvent event)
        throws FormProcessException {
        PageState state = event.getPageState();
        FormData data = event.getFormData();
        try {
            if (m_newUser) {
                // Verify that password and confirmation match
                String password = (String) m_password.getValue(state);
                String confirm = (String) m_confirm.getValue(state);

                if ((password != null) && (confirm != null)
                        && !password.equals(confirm)) {
                    data.addError(FORM_PASSWORD_CONFIRMATION,
                                  ERROR_MISMATCH_PASSWORD);
                }
            }

            String email = null;
            if (m_email.getValue(state) != null) {
                InternetAddress address = (InternetAddress) m_email
                    .getValue(state);
                email = address.getAddress();
            }

            final String screenName = (String) m_screenName.getValue(state);

            // If this query returns with any rows we have a duplicate
            // screen name, email address, or both.  Check the results and
            // produce appropriate error messages.
            final boolean checkPrimaryEmail = KernelConfig.getConfig()
                .emailIsPrimaryIdentifier();

//            final UserRepository userRepo;
//            try {
//                final CdiUtil cdiUtil = new CdiUtil();
//                userRepo = cdiUtil.findBean(
//                    UserRepository.class);
//            } catch (CdiLookupException ex) {
//                throw new FormProcessException(ex);
//            }

//            final User userByEmail = userRepo.findByEmailAddress(email);
//            if (userByEmail != null && checkPrimaryEmail) {
//                data.addError(FORM_EMAIL, ERROR_DUPLICATE_EMAIL);
//            }
//
//            final User userByScreenname = userRepo.findByScreenName(screenName);
//            if (userByScreenname != null) {
//                data.addError(FORM_SCREEN_NAME, ERROR_DUPLICATE_SN);
//            }

        } finally {
            // if the form has errors, clear the password fields so we don't
            // send the passwords back over the network
            if (m_newUser && !data.isValid()) {
                m_password.setValue(state, "");
                m_confirm.setValue(state, "");
            }
        }
    }

}
