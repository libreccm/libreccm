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
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.form.Password;
import com.arsdigita.globalization.GlobalizedMessage;

import org.apache.logging.log4j.util.Strings;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.User;
import org.libreccm.security.UserManager;
import org.libreccm.security.UserRepository;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 * Form for setting the password of a user.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class PasswordSetForm extends Form {

    private static final String NEW_PASSWORD = "new_password";
    private static final String PASSWORD_CONFIRM = "password_confirm";

    private final Password newPassword;
    private final Password passwordConfirm;
    private final SaveCancelSection saveCancelSection;

    public PasswordSetForm(
        final UserAdmin userAdmin,
        final ParameterSingleSelectionModel<String> selectedUserId) {

        super("password_set_form");

        newPassword = new Password(NEW_PASSWORD);
        newPassword.setLabel(new GlobalizedMessage(
            "ui.admin.user_set_password.new_password.label", ADMIN_BUNDLE));
        add(newPassword);

        passwordConfirm = new Password(PASSWORD_CONFIRM);
        passwordConfirm.setLabel(new GlobalizedMessage(
            "ui.admin.user_set_password.confirm_password.label",
            ADMIN_BUNDLE
        ));
        add(passwordConfirm);

        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);

        addValidationListener(e -> {
            final PageState state = e.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final FormData data = e.getFormData();

                final String passwordData = data.getString(NEW_PASSWORD);
                final String confirmData = data.getString(PASSWORD_CONFIRM);

                if (Strings.isEmpty(passwordData)) {
                    data.addError(
                        NEW_PASSWORD,
                        new GlobalizedMessage(
                            "ui.admin.set_password.new_password.error.not_empty",
                            ADMIN_BUNDLE));
                    return;
                }

                if (Strings.isEmpty(confirmData)) {
                    data.addError(
                        PASSWORD_CONFIRM,
                        new GlobalizedMessage(
                            "ui.admin.set_password.password_confirm.error.not_empty",
                            ADMIN_BUNDLE));
                    return;
                }

                if (!passwordData.equals(confirmData)) {
                    data.addError(new GlobalizedMessage(
                        "ui.admin.user_set_password.error.do_not_match",
                        ADMIN_BUNDLE));
                }
            }
        });
        
        addProcessListener(e -> {
            final PageState state = e.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final String userIdStr = selectedUserId.getSelectedKey(state);
                final String password = (String) newPassword.getValue(state);

                final UserRepository userRepository = CdiUtil.createCdiUtil()
                    .findBean(UserRepository.class);
                final User user = userRepository.findById(Long.parseLong(
                    userIdStr));

                final UserManager userManager = CdiUtil.createCdiUtil()
                    .findBean(
                        UserManager.class);
                userManager.updatePassword(user, password);
            }
            userAdmin.closePasswordSetForm(state);
        });
    }

}
