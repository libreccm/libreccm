/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.libreccm.admin.ui.usersgroupsroles;

import com.vaadin.data.provider.AbstractDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.security.User;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class UserEditor extends Window {

    private static final long serialVersionUID = 7024424532574023431L;

    private enum PasswordOptions {

        GENERATE_AND_SEND,
        SET

    }

    private final User user;

    private TextField userName;
    private TextField familyName;
    private TextField givenName;
    private TextField emailAddress;
    private RadioButtonGroup<PasswordOptions> passwordOptions;
    private PasswordField password;
    private PasswordField passwordConfirmation;

    public UserEditor() {
        user = null;

        addWidgets();
    }

    public UserEditor(final User user) {

        this.user = user;

        addWidgets();
    }

    private void addWidgets() {

        userName = new TextField("User name");
        userName.setRequiredIndicatorVisible(true);

        familyName = new TextField("Family name");

        givenName = new TextField("Given name");

        emailAddress = new TextField("emailAddress");
        emailAddress.setRequiredIndicatorVisible(true);

        passwordOptions = new RadioButtonGroup<PasswordOptions>(
            "Generate password or set manually?",
            new AbstractDataProvider<PasswordOptions, String>() {

            private static final long serialVersionUID = 1L;

            @Override
            public boolean isInMemory() {
                return true;
            }

            @Override
            public int size(final Query<PasswordOptions, String> query) {
                return PasswordOptions.values().length;
            }

            @Override
            public Stream<PasswordOptions> fetch(
                final Query<PasswordOptions, String> query) {
                return Arrays.stream(PasswordOptions.values());
            }

        });

        password = new PasswordField("Password");
        password.setRequiredIndicatorVisible(true);

        passwordConfirmation = new PasswordField("Confirm password");
        passwordConfirmation.setRequiredIndicatorVisible(true);

        passwordOptions.addValueChangeListener(event -> {
            switch (event.getValue()) {
                case GENERATE_AND_SEND:
                    password.setEnabled(false);
                    password.setVisible(false);
                    passwordConfirmation.setEnabled(false);
                    passwordConfirmation.setVisible(false);
                    break;
                case SET:
                    password.setEnabled(true);
                    password.setVisible(true);
                    passwordConfirmation.setEnabled(true);
                    passwordConfirmation.setVisible(true);
                    break;
                default:
                    throw new UnexpectedErrorException(String.format(
                        "Unexpected value '%s' for password options.",
                        event.getValue().toString()));
            }
        });

        passwordOptions.setValue(PasswordOptions.GENERATE_AND_SEND);

        final Button submit = new Button();
        if (user == null) {
            submit.setCaption("Create new user");
        } else {
            submit.setCaption("Save");
        }

        final Button cancel = new Button("Cancel");

        final HorizontalLayout buttons = new HorizontalLayout(submit, cancel);
        
        final FormLayout formLayout = new FormLayout(userName,
                                                     familyName,
                                                     givenName,
                                                     emailAddress,
                                                     passwordOptions,
                                                     password,
                                                     passwordConfirmation);
        
        final VerticalLayout layout = new VerticalLayout(formLayout, buttons);

        final Panel panel = new Panel(layout);
        if (user == null) {
            panel.setCaption("Create new user");
        } else {
            panel.setCaption("Edit user");
        }

        setContent(panel);
    }

}
