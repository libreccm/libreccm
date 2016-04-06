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
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.ChallengeManager;
import org.libreccm.security.User;
import org.libreccm.security.UserRepository;

import javax.mail.MessagingException;

import static com.arsdigita.ui.login.LoginConstants.*;
import static com.arsdigita.ui.login.LoginServlet.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class RecoverPasswordForm extends Form {

    private static final String EMAIL = "email";

    private BoxPanel formPanel;
    private TextField email;
    private SaveCancelSection saveCancelSection;
    private BoxPanel finishedMessagePanel;

    public RecoverPasswordForm() {
        super("recover-password");

        addWidgets();
        addListeners();
    }

    private void addWidgets() {
        formPanel = new BoxPanel(BoxPanel.VERTICAL);

        email = new TextField(EMAIL);
        email.setLabel(new GlobalizedMessage(
            "login.form.recover_password.email.label",
            LOGIN_BUNDLE));
        email.setHint(new GlobalizedMessage(
            "login.form.recover_password.email.hint",
            LOGIN_BUNDLE));
        email.setMaxLength(256);
        email.setSize(48);
        email.addValidationListener(new NotEmptyValidationListener());
        email.addValidationListener(new StringLengthValidationListener(256));
        formPanel.add(email);

        saveCancelSection = new SaveCancelSection();
        formPanel.add(saveCancelSection);

        add(formPanel);

        finishedMessagePanel = new BoxPanel(BoxPanel.VERTICAL);
        finishedMessagePanel.add(new Label(new GlobalizedMessage(
            "login.form.recover_password.finished_message", LOGIN_BUNDLE)));
        final Link link = new Link(
            new Label(
                new GlobalizedMessage(
                    "login.form.recover_password.finished_message.link",
                    LOGIN_BUNDLE)),
            LOGIN_PAGE_URL + RESET_USER_PASSWORD_PATH_INFO);
        finishedMessagePanel.add(link);
    }

    private void addListeners() {
//        addValidationListener(e -> {
//            final PageState state = e.getPageState();
//
//            if (saveCancelSection.getSaveButton().isSelected(state)) {
//                final FormData data = e.getFormData();
//
//                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
//                final UserRepository userRepository = cdiUtil.findBean(
//                    UserRepository.class);
//
//                final User user = userRepository.findByEmailAddress(
//                    (String) data.get(EMAIL));
//                if (user == null) {
//                    data.addError(new GlobalizedMessage(
//                        "login.form.recover_password.error", LOGIN_BUNDLE));
//                }
//            }
//        });

        addProcessListener(e -> {
            final PageState state = e.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final FormData data = e.getFormData();

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final UserRepository userRepository = cdiUtil.findBean(
                    UserRepository.class);

                final User user = userRepository.findByEmailAddress(
                    (String) data.get(EMAIL));
//                if (user == null) {
//                    throw new FormProcessException(
//                        "No user for provided email address found. This should "
//                            + "not happen because we checked this in the "
//                            + "validation listener.",
//                        new GlobalizedMessage(
//                            "login.form.recover_password.error", LOGIN_BUNDLE));
//                }

                // We don't show an error message if there is no matching user 
                // account. This way we don't provide an attacker with 
                // the valuable information that there is user account for 
                // a particular email address.
                if (user != null) {
                    final ChallengeManager challengeManager = cdiUtil.findBean(
                        ChallengeManager.class);
                    try {
                        challengeManager.sendPasswordRecover(user);
                    } catch (MessagingException ex) {
                        throw new FormProcessException(
                            "Failed to send password recovery instructions.",
                            new GlobalizedMessage(
                                "login.form.recover_password.error.send_challenge_failed",
                                LOGIN_BUNDLE),
                            ex);
                    }
                }

                formPanel.setVisible(state, false);
                finishedMessagePanel.setVisible(state, true);
                data.clear();
            }
        }
        );
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.setVisibleDefault(formPanel, true);
        page.setVisibleDefault(finishedMessagePanel, false);
    }

}
