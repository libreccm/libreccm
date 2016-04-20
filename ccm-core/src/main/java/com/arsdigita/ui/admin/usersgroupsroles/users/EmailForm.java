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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.globalization.GlobalizedMessage;

import org.apache.logging.log4j.util.Strings;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.EmailAddress;
import org.libreccm.security.User;
import org.libreccm.security.UserRepository;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 * Form for editing and adding email addresses to a user.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class EmailForm extends Form {

    private static final String ADDRESS = "address";
    private static final String VERIFIED = "verified";
    private static final String BOUNCING = "bouncing";

    private final TextField address;
    private final CheckboxGroup verified;
    private final CheckboxGroup bouncing;
    private final SaveCancelSection saveCancelSection;

    public EmailForm(
        final UserAdmin userAdmin,
        final ParameterSingleSelectionModel<String> selectedUserId,
        final ParameterSingleSelectionModel<String> selectedEmailAddress) {

        super("email_form");

        address = new TextField(ADDRESS);
        address.setLabel(new GlobalizedMessage(
            "ui.admin.user.email_form.address", ADMIN_BUNDLE));
        add(address);

        verified = new CheckboxGroup(VERIFIED);
        verified.addOption(
            new Option("true",
                       new Label(new GlobalizedMessage(
                           "ui.admin.user.email_form.verified",
                           ADMIN_BUNDLE))));
        add(verified);

        bouncing = new CheckboxGroup(BOUNCING);
        bouncing.addOption(
            new Option("true",
                       new Label(new GlobalizedMessage(
                           "ui.admin.user.email_form.bouncing",
                           ADMIN_BUNDLE))));
        add(bouncing);

        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);

        addInitListener(e -> {
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
                    address.setValue(state, email.getAddress());
                    if (email.isVerified()) {
                        verified.setValue(state, "true");
                    }
                    if (email.isBouncing()) {
                        bouncing.setValue(state, "true");
                    }
                }
            }
        });

        addValidationListener(e -> {
            final PageState state = e.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final FormData data = e.getFormData();

                final String addressData = data.getString(ADDRESS);

                if (Strings.isEmpty(addressData)) {
                    data.addError(ADDRESS, new GlobalizedMessage(
                                  "ui.admin.user.email_form.address.not_empty",
                                  ADMIN_BUNDLE));
                }
            }
        });

        addProcessListener(e -> {
            final PageState state = e.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final FormData data = e.getFormData();

                final String selected = selectedEmailAddress.getSelectedKey(
                    state);
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
                    email.setAddress(data.getString(ADDRESS));

                    final String[] verifiedValues = (String[]) data
                        .get(VERIFIED);
                    if (verifiedValues != null && verifiedValues.length > 0) {
                        if ("true".equals(verifiedValues[0])) {
                            email.setVerified(true);
                        } else {
                            email.setVerified(false);
                        }
                    } else {
                        email.setVerified(false);
                    }

                    final String[] bouncingValues = (String[]) data
                        .get(BOUNCING);
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
            }

            userAdmin.closeEmailForm(e.getPageState());
        });
    }

}
