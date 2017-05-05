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

import com.arsdigita.ui.admin.AdminUiConstants;

import com.vaadin.data.provider.AbstractDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.security.User;

import java.util.Arrays;
import java.util.ResourceBundle;
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

        final ResourceBundle bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       UI.getCurrent().getLocale());

        userName = new TextField(bundle
            .getString("ui.admin.user_edit.username.label"));
        userName.setRequiredIndicatorVisible(true);

        familyName = new TextField(bundle
            .getString("ui.admin.user_edit.familyname.label"));

        givenName = new TextField(bundle
            .getString("ui.admin.user_edit.givenname.label"));

        emailAddress = new TextField(bundle
            .getString("ui.admin.user_edit.emailAddress.label"));
        emailAddress.setRequiredIndicatorVisible(true);

        passwordOptions = new RadioButtonGroup<PasswordOptions>(
            bundle.getString("ui.admin.user_edit.password_options.label"),
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
        passwordOptions.setItemCaptionGenerator(
            (final PasswordOptions item) -> {
                switch (item) {
                    case GENERATE_AND_SEND:
                        return bundle.getString(
                            "ui.admin.user_edit.password_options.generate_and_send");
                    case SET:
                        return bundle.getString(
                            "ui.admin.user_edit.password_options.set");
                    default:
                        throw new UnexpectedErrorException(String.format(
                            "Unexpected value '%s' for password options.",
                            item.toString()));
                }
            });

        password = new PasswordField(bundle
            .getString("ui.admin.user_edit.password.label"));
        password.setRequiredIndicatorVisible(true);

        passwordConfirmation = new PasswordField(bundle
            .getString("ui.admin.user_set_password_confirm.label"));
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
            submit.setCaption(bundle.getString(
                "ui.admin.user.createpanel.header"));
        } else {
            submit.setCaption(bundle.getString("ui.admin.save"));
        }

        final Button cancel = new Button(bundle.getString("ui.admin.cancel"));

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
            panel.setCaption(bundle
                .getString("ui.admin.user.createpanel.header"));
        } else {
            panel.setCaption(bundle
                .getString("ui.admin.user_details.edit"));
        }

        setContent(panel);
    }

}
