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
import org.libreccm.security.User;
import org.libreccm.security.UserRepository;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 * Form for editing the properties of a user. There separate forms for some
 * properties like the password, the group memberships or the role memberships.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class UserEditForm extends Form {

    private static final String USER_NAME = "username";
    private static final String FAMILY_NAME = "familyName";
    private static final String BANNED = "banned";
    private static final String GIVEN_NAME = "givenName";

    private final TextField userName;
    private final TextField familyName;
    private final TextField givenName;
    private final CheckboxGroup banned;
    private final CheckboxGroup passwordResetRequired;
    private final SaveCancelSection saveCancelSection;

    public UserEditForm(
        final UserAdmin userAdmin,
        final ParameterSingleSelectionModel<String> selectedUserId) {

        super("userEditForm");

        userName = new TextField(USER_NAME);
        userName.setLabel(new GlobalizedMessage(
            "ui.admin.user_edit.username.label", ADMIN_BUNDLE));
        add(userName);

        familyName = new TextField(FAMILY_NAME);
        familyName.setLabel(new GlobalizedMessage(
            "ui.admin.user_edit.familyname.label", ADMIN_BUNDLE));
        add(familyName);

        givenName = new TextField(GIVEN_NAME);
        givenName.setLabel(new GlobalizedMessage(
            "ui.admin.user_edit.givenname.label", ADMIN_BUNDLE));
        add(givenName);

        banned = new CheckboxGroup(BANNED);
        banned.addOption(new Option(
            "banned",
            new Label(new GlobalizedMessage("ui.admin.user_edit.banned.label",
                                            ADMIN_BUNDLE))));
        add(banned);

        passwordResetRequired = new CheckboxGroup(
            "password_reset_required");
        passwordResetRequired.addOption(new Option(
            "password_reset_required",
            new Label(new GlobalizedMessage(
                "ui.admin.user_edit.password_reset_required.label",
                ADMIN_BUNDLE))
        ));
        add(passwordResetRequired);

        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);

        addInitListener(e -> {
            final PageState state = e.getPageState();

            final String userIdStr = selectedUserId.getSelectedKey(state);
            final UserRepository userRepository = CdiUtil.createCdiUtil()
                .findBean(UserRepository.class);
            final User user = userRepository.findById(Long.parseLong(userIdStr))
                .get();

            userName.setValue(state, user.getName());
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

        addValidationListener(e -> {
            final PageState state = e.getPageState();
            final FormData data = e.getFormData();

            final String userNameData = data.getString(USER_NAME);
            if (Strings.isEmpty(userNameData)) {
                data.addError(
                    USER_NAME,
                    new GlobalizedMessage(
                        "ui.admin.user_edit.username.error.not_empty",
                        ADMIN_BUNDLE));
            }

            final String familyNameData = data.getString(FAMILY_NAME);
            if (Strings.isEmpty(familyNameData)) {
                data.addError(
                    FAMILY_NAME,
                    new GlobalizedMessage(
                        "ui.admin.user_edit.familyname.error_not_empty",
                        ADMIN_BUNDLE));
            }

            final String givenNameData = data.getString(GIVEN_NAME);
            if (Strings.isEmpty(givenNameData)) {
                data.addError(
                    GIVEN_NAME,
                    new GlobalizedMessage(
                        "ui.admin.user_edit.givenname.error.not_empty",
                        ADMIN_BUNDLE));
            }
        });

        addProcessListener(e -> {
            final PageState state = e.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final String userIdStr = selectedUserId.getSelectedKey(state);
                final UserRepository userRepository = CdiUtil.createCdiUtil()
                    .findBean(UserRepository.class);
                final User user = userRepository.findById(Long.parseLong(
                    userIdStr)).get();

                if (!user.getName().equals(userName.getValue(state))) {
                    user.setName((String) userName.getValue(state));
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
            userAdmin.closeUserEditForm(state);
        });
    }

}
