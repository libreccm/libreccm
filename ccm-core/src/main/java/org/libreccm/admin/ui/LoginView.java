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
package org.libreccm.admin.ui;

import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.ui.admin.AdminUiConstants;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.GlobalizationHelper;

import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@CDIView(LoginView.VIEWNAME)
public class LoginView extends CustomComponent implements View {

    private static final long serialVersionUID = 997966222985596011L;

    public static final String VIEWNAME = "login";

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private Subject subject;

    private ResourceBundle bundle;

    private final FormLayout layout;
    private final TextField userName;
    private final TextField password;
    private final Button submitButton;

    public LoginView() {

        layout = new FormLayout();
        layout.setSizeFull();

        userName = new TextField();
        userName.setCaption("User name");
        layout.addComponent(userName);

        password = new PasswordField("Password");
        layout.addComponent(password);

        submitButton = new Button("Login");
        submitButton.addClickListener(event -> login(event));
        layout.addComponent(submitButton);

        setCompositionRoot(layout);
    }

    @PostConstruct
    private void postConstruct() {
        bundle = ResourceBundle.getBundle(
            "com.arsdigita.ui.login.LoginResources",
            globalizationHelper.getNegotiatedLocale());

    }

    private void login(final Button.ClickEvent event) {
        final UsernamePasswordToken token = new UsernamePasswordToken(
            userName.getValue(),
            password.getValue());
        token.setRememberMe(true);

        try {
            subject.login(token);
        } catch (AuthenticationException ex) {
            layout.setComponentError(
                new UserError(bundle.getString("login.error.loginFail")));
            return;
        }

        getUI().getNavigator().navigateTo(AdminView.VIEWNAME);
    }

    @Override
    public void enter(final ViewChangeListener.ViewChangeEvent event) {

        final KernelConfig kernelConfig = confManager
            .findConfiguration(KernelConfig.class);
        if (kernelConfig.emailIsPrimaryIdentifier()) {
            userName.setCaption(bundle
                .getString("login.userRegistrationForm.email"));
        } else {
            userName.setCaption(bundle
                .getString("login.userRegistrationForm.screenName"));
        }
        password.setCaption(
            bundle.getString("login.userRegistrationForm.password"));

        submitButton.setCaption(bundle
            .getString("login.userRegistrationForm.title"));
    }

}
