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


import com.vaadin.cdi.CDIView;
import org.libreccm.ui.AbstractLoginView;


/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@CDIView(value = LoginView.VIEWNAME,
         uis = {AdminUIVaadin.class})
//public class LoginView extends CustomComponent implements View {

public class LoginView extends AbstractLoginView {

    private static final long serialVersionUID = 997966222985596011L;

    public static final String VIEWNAME = "adminlogin";

    @Override
    protected String getTargetView() {
        return AdminView.VIEWNAME;
    }
    
//    @Inject
//    private ConfigurationManager confManager;
//
//    @Inject
//    private GlobalizationHelper globalizationHelper;
//
//    @Inject
//    private Subject subject;
//
//    private ResourceBundle bundle;
//
//    private final Panel loginPanel;
//    private final FormLayout formLayout;
//    private final TextField userName;
//    private final TextField password;
//    private final Button submitButton;
//
//    public LoginView() {
//
//        formLayout = new FormLayout();
//        formLayout.setSizeFull();
//        formLayout.setMargin(true);
//
//        userName = new TextField();
//        userName.setCaption("User name");
//        formLayout.addComponent(userName);
//
//        password = new PasswordField("Password");
//        formLayout.addComponent(password);
//
//        submitButton = new Button("Login");
//        submitButton.addClickListener(event -> login(event));
//        submitButton.setEnabled(false);
//        formLayout.addComponent(submitButton);
//
//        userName.addValueChangeListener(event -> {
//            if (userName.getValue() != null
//                    && !userName.getValue().trim().isEmpty()
//                    && password.getValue() != null
//                    && !password.getValue().trim().isEmpty()) {
//                submitButton.setEnabled(true);
//                submitButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
//            }
//        });
//
//        password.addValueChangeListener(event -> {
//            if (userName.getValue() != null
//                    && !userName.getValue().trim().isEmpty()
//                    && password.getValue() != null
//                    && !password.getValue().trim().isEmpty()) {
//                submitButton.setEnabled(true);
//                submitButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
//            }
//        });
//
////        userName.addFocusListener(event -> {
////            if (userName.getValue() != null
////                    && !userName.getValue().trim().isEmpty()
////                    && password.getValue() != null
////                    && !password.getValue().trim().isEmpty()) {
////                submitButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
////            }
////        });
////        userName.addBlurListener(event -> {
////            if (userName.getValue() != null
////                    && !userName.getValue().trim().isEmpty()
////                    && password.getValue() != null
////                    && !password.getValue().trim().isEmpty()) {
////                submitButton.removeClickShortcut();
////            }
////        });
//        password.addFocusListener(event -> {
//            submitButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
//        });
//        password.addBlurListener(event -> {
//            submitButton.removeClickShortcut();
//        });
//
//        loginPanel = new Panel("Login", formLayout);
//        loginPanel.setWidth("27em");
//
//        final VerticalLayout viewLayout = new VerticalLayout(new Header(),
//                                                             loginPanel);
//
//        viewLayout.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);
//
//        super.setCompositionRoot(viewLayout);
//    }
//
//    @PostConstruct
//    private void postConstruct() {
//        bundle = ResourceBundle.getBundle(
//            "com.arsdigita.ui.login.LoginResources",
//            globalizationHelper.getNegotiatedLocale());
//    }
//
//    private void login(final Button.ClickEvent event) {
//        final UsernamePasswordToken token = new UsernamePasswordToken(
//            userName.getValue(),
//            password.getValue());
//        token.setRememberMe(true);
//
//        try {
//            subject.login(token);
//        } catch (AuthenticationException ex) {
//            submitButton.setComponentError(
//                new UserError(bundle.getString("login.error.loginFail")));
//            Notification.show(bundle.getString("login.error.loginFail"),
//                              Notification.Type.ERROR_MESSAGE);
//            password.setValue("");
//            return;
//        }
//
//        getUI().getNavigator().navigateTo(AdminView.VIEWNAME);
//    }
//
//    @Override
//    public void enter(final ViewChangeListener.ViewChangeEvent event) {
//
//        final KernelConfig kernelConfig = confManager
//            .findConfiguration(KernelConfig.class
//            );
//        loginPanel
//            .setCaption(bundle.getString("login.userRegistrationForm.title"));
//        if (kernelConfig.emailIsPrimaryIdentifier()) {
//            userName.setCaption(bundle
//                .getString("login.userRegistrationForm.email"));
//        } else {
//            userName.setCaption(bundle
//                .getString("login.userRegistrationForm.screenName"));
//        }
//        password.setCaption(
//            bundle.getString("login.userRegistrationForm.password"));
//
//        submitButton.setCaption(bundle
//            .getString("login.userRegistrationForm.title"));
//    }
}
