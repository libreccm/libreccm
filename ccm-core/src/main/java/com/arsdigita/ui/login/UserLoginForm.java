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

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.ElementComponent;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.Password;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.EmailParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.URLParameter;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.kernel.security.SecurityConfig;
import com.arsdigita.ui.UI;
import com.arsdigita.util.UncheckedWrapperException;

import static com.arsdigita.ui.login.LoginConstants.*;

import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.ReturnSignal;
import com.arsdigita.web.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.libreccm.cdi.utils.CdiUtil;

import org.apache.shiro.subject.Subject;
import org.libreccm.configuration.ConfigurationManager;

/**
 * A Bebop form that accepts login and password from the user and attempts to
 * authenticate and then log in the user.
 *
 * Depending on security configuration it may generate a link to a NewUser
 * registration form, where a new user may register itself. LoginServlet has to
 * ensure that this page is created appropriately and is available.
 *
 * According to documentation in r1230 Simple SSO implementation: /ccm/register
 * first tries to do SSO login, falling back to normal form-based login. Set
 * waf.sso_login=true only after you make sure webapp can *only* be accessed
 * through the frontend webserver doing the authentication.
 *
 * To make this work with Tomcat/mod_jk/Apache HTTPD: - use latest mod_jk
 * (tested with 1.2.15) - add attribute Connector@tomcatAuthentication="false"
 * to JK definition in server.xml
 *
 * @author Roger Hsueh
 * @author Michael Bryzek
 * @author Sameer Ajmani
 *
 * @version $Id$
 */
public class UserLoginForm extends Form implements LoginConstants,
                                                   FormInitListener,
                                                   FormValidationListener,
                                                   FormProcessListener {

    private static final Logger LOGGER = LogManager.getLogger(
        UserLoginForm.class);

    // package friendly static form name makes writing HttpUnitTest easier
    final static String FORM_NAME = "user-login";
    private final CheckboxGroup m_isPersistent;
    private final Hidden m_timestamp;
    private final Hidden m_returnURL;
    private TextField m_loginName;
    private final Password m_password;
    private final boolean m_autoRegistrationOn;
    private final SecurityConfig securityConfig;// = SecurityConfig.getConfig();

    /**
     * Default constructor delegates to a constructor which creates a LoginForm
     * without a link to a newUserRegistrationForm.
     */
    public UserLoginForm() {
        this(true);
    }

    public UserLoginForm(Container panel) {
        this(panel, true);
    }

    public UserLoginForm(boolean autoRegistrationOn) {
        this(new BoxPanel(), autoRegistrationOn);
    }

    /**
     * Constructor which does the real work, other constructors delegate to it.
     *
     * @param panel
     * @param autoRegistrationOn
     */
    public UserLoginForm(final Container panel,
                         final boolean autoRegistrationOn) {
        super(FORM_NAME, panel);

//        final ConfigurationManager confManager = CDI.current().select(
//                ConfigurationManager.class).get();
        final BeanManager beanManager = CDI.current().getBeanManager();
        final Set<Bean<?>> beans = beanManager.getBeans(
            ConfigurationManager.class);
        final Iterator<Bean<?>> iterator = beans.iterator();
        final ConfigurationManager confManager;
        if (iterator.hasNext()) {
            @SuppressWarnings("unchecked")
            final Bean<ConfigurationManager> bean
                                             = (Bean<ConfigurationManager>) iterator
                .next();
            final CreationalContext<ConfigurationManager> ctx = beanManager.
                createCreationalContext(bean);

            confManager = (ConfigurationManager) beanManager.getReference(
                bean, ConfigurationManager.class, ctx);
        } else {
            throw new UncheckedWrapperException(
                "Failed to lookup ConfigurationManager");
        }
        securityConfig = confManager.findConfiguration(SecurityConfig.class);

        setMethod(Form.POST);
        addInitListener(this);
        addValidationListener(this);
        addProcessListener(this);

        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);

        m_autoRegistrationOn = autoRegistrationOn;

        m_timestamp = new Hidden(new StringParameter(FORM_TIMESTAMP));
        add(m_timestamp);

        m_returnURL = new Hidden(new URLParameter(
            LoginHelper.RETURN_URL_PARAM_NAME));
        m_returnURL.setPassIn(true);
        add(m_returnURL);

        setupLogin();

        add(new Label(LoginHelper.getMessage(
            "login.userRegistrationForm.password")));
        m_password = new Password(new StringParameter(FORM_PASSWORD));
        // Since new users should not enter a password, allow null.
        //m_password.addValidationListener(new NotNullValidationListener());
        add(m_password);

        SimpleContainer cookiePanel = new BoxPanel(BoxPanel.HORIZONTAL);
        m_isPersistent = new CheckboxGroup(FORM_PERSISTENT_LOGIN_P);
        Label optLabel = new Label(LoginHelper.getMessage(
            "login.userRegistrationForm.cookieOption"));
        Option opt = new Option(FORM_PERSISTENT_LOGIN_P_DEFAULT, optLabel);
        m_isPersistent.addOption(opt);
        if (kernelConfig.isRememberLoginEnabled()) {
            m_isPersistent.setOptionSelected(FORM_PERSISTENT_LOGIN_P_DEFAULT);
        }
        cookiePanel.add(m_isPersistent);

        cookiePanel.add(new DynamicLink(
            "login.userRegistrationForm.explainCookieLink",
            LoginServlet.getCookiesExplainPageURL()));
        add(cookiePanel);

        add(new Submit(SUBMIT), ColumnPanel.CENTER | ColumnPanel.FULL_WIDTH);

        if (securityConfig.isPasswordRecoveryEnabled()) {
            add(new DynamicLink("login.userRegistrationForm.forgotPasswordLink",
                                LoginServlet.getRecoverPasswordPageURL()));
        }

        if (m_autoRegistrationOn) {
            add(new DynamicLink("login.userRegistrationForm.newUserRegister",
                                LoginServlet.getNewUserPageURL()));
        }

        add(new ElementComponent("subsite:promptToEnableCookiesMsg",
                                 LoginServlet.SUBSITE_NS_URI));
    }

    /**
     * Sets up the login form parameters
     */
    private void setupLogin() {
        SimpleContainer loginMessage = new SimpleContainer(
            "subsite:loginPromptMsg",
            LoginServlet.SUBSITE_NS_URI);

        final KernelConfig kernelConfig = KernelConfig.getConfig();

        if (kernelConfig.emailIsPrimaryIdentifier()) {
            loginMessage.setClassAttr("email");
        } else {
            loginMessage.setClassAttr("screenName");
        }

        add(loginMessage);

        if (kernelConfig.emailIsPrimaryIdentifier()) {
            add(new Label(LoginHelper.getMessage(
                "login.userRegistrationForm.email")));
            m_loginName = new TextField(new EmailParameter(FORM_LOGIN));
            addInitListener(new EmailInitListener((EmailParameter) m_loginName.
                getParameterModel()));
        } else {
            add(new Label(LoginHelper.getMessage(
                "login.userRegistrationForm.screenName")));
            m_loginName = new TextField(new StringParameter(FORM_LOGIN));
            addInitListener(new ScreenNameInitListener(
                (StringParameter) m_loginName.
                getParameterModel()));
        }
        m_loginName.addValidationListener(new NotNullValidationListener());
        add(m_loginName);
    }

    /**
     *
     * @param event
     *
     * @throws FormProcessException
     */
    @Override
    public void init(FormSectionEvent event)
        throws FormProcessException {
        LOGGER.info("In init");

        final KernelConfig kernelConfig = KernelConfig.getConfig();

        if (kernelConfig.isSsoEnabled()) {
            // try SSO login
            LOGGER.info("trying SSO");
//            try {
            throw new UnsupportedOperationException(
                "SSO currently not supported");
//                Web.getUserContext().loginSSO();
//                s_log.info("loginSSO ok, now processing redirect_url");
//                process(event);
//                return;
//            } catch (LoginException le) {
//                // continue with standard form-based login
//                s_log.debug("SSO failed", le);
//            }
        }
//        try {
//            // create timestamp
//            String value = Credential.create(FORM_TIMESTAMP,
//                                             1000 * TIMESTAMP_LIFETIME_SECS).
//                    toString();
//            m_timestamp.setValue(event.getPageState(), value);
//        } catch (CredentialException e) {
//            s_log.debug("Could not create timestamp", e);
//            throw new FormProcessException(LoginGlobalizationUtil.globalize(
//                    "login.userLoginForm.couldnt_create_timestamp"));
//        }
    }

    /**
     *
     * @param event
     *
     * @throws FormProcessException
     */
    @Override
    public void validate(FormSectionEvent event)
        throws FormProcessException {

        LOGGER.debug("In validate");

        FormData data = event.getFormData();
        PageState state = event.getPageState();
        try {
            // check timestamp
//            try {
//                Credential.parse((String) m_timestamp.getValue(state));
//            } catch (CredentialException e) {
//                s_log.info("Invalid credential");
//
//                final String path = LoginServlet.getLoginExpiredPageURL();
//                final URL_MSG url = com.arsdigita.web.URL_MSG.there(state.getRequest(),
//                                                            path);
//
//                throw new RedirectSignal(url, false);
//            }
            // log in the user
            if (m_loginName.getValue(state) != null) {
                loginUser(event);
            }
        } finally {
            if (!data.isValid()) {
                // clear password from form data
                m_password.setValue(state, "");
            }
        }
    }

    /**
     *
     * @param event
     *
     * @throws FormProcessException
     */
    @Override
    public void process(final FormSectionEvent event)
        throws FormProcessException {
        LOGGER.debug("In process");

        final PageState state = event.getPageState();
        final HttpServletRequest req = state.getRequest();

        // Redirect to workspace or return URL_MSG, if specified.
        final String path = UI.getUserRedirectURL(req);

        final URL url = com.arsdigita.web.URL.there(req, path);

        throw new ReturnSignal(req, url);
    }

    /**
     * Logs in the user using the username, password, and "Remember this login?"
     * request stored in the given form event. Subclasses can override this
     * method or just one of the specific case handlers (onLoginSuccess,
     * onBadPassword, onAccountNotFound, onLoginException).
     *
     * @param event
     *
     * @throws FormProcessException if there is an unexpected login error
     *
     */
    protected void loginUser(final FormSectionEvent event)
        throws FormProcessException {
        PageState state = event.getPageState();

        final CdiUtil cdiUtil = new CdiUtil();
        final Subject subject = cdiUtil.findBean(Subject.class);

        final UsernamePasswordToken token = new UsernamePasswordToken(
            m_loginName.getValue(state).toString(),
            (String) m_password.getValue(state)
        );
        token.setRememberMe(getPersistentLoginValue(state, false));
        try {
            subject.login(token);
        } catch (AuthenticationException ex) {
            onLoginFail(event, ex);
        }

        LOGGER.debug("User {} logged in successfully.", token.getUsername());
    }

    /**
     * Executed when login succeeds. Default implementation does nothing.
     *
     * @param event
     *
     * @throws com.arsdigita.bebop.FormProcessException
     *
     */
    protected void onLoginSuccess(final FormSectionEvent event)
        throws FormProcessException {
        // do nothing
    }

    /**
     *
     * @param event
     * @param ex
     *
     * @throws FormProcessException
     */
//    protected void onBadPassword(final FormSectionEvent event,
//                                 final FailedLoginException ex)
//            throws FormProcessException {
//        onLoginFail(event, ex);
//    }
    /**
     * Executed when login fails with a bad password or when autoLoginOn is set
     * to false and the user doesn't exist. Default implementation marks
     * password parameter with an error message.
     *
     * @param event
     * @param ex
     *
     * @throws com.arsdigita.bebop.FormProcessException
     *
     */
    protected void onLoginFail(final FormSectionEvent event,
                               final AuthenticationException ex)
        throws FormProcessException {
        LOGGER.debug("Login fail");
        event.getFormData().addError(ERROR_LOGIN_FAIL);
    }

    /**
     * Executed when login fails for an unrecognized problem. Default
     * implementation logs the error and throws FormProcessException.
     *
     * @param event
     * @param ex
     *
     * @throws com.arsdigita.bebop.FormProcessException
     */
//    protected void onLoginException(final FormSectionEvent event,
//                                    final LoginException ex)
//            throws FormProcessException {
//        // unexpected error happened during login
//        s_log.error("Login failed", ex);
//        throw new FormProcessException(ex);
//    }
    /**
     * Determines whether a persistent cookie is requested in the given form.
     * FORM_PERSISTENT_LOGIN_P whose value is equal to "1". If there is no such
     * field in the form data, returns the specified default value.
     *
     * @param state
     * @param defaultValue
     *
     * @return true if the specified formdata has a field named
     *
     *
     */
    protected boolean getPersistentLoginValue(final PageState state,
                                              final boolean defaultValue) {
        // Problem:
        // getValue(state) returns an Object of type StringArray, if the
        // Checkbox is marked.
        // It returns an object of type String if it is not marked / left empty.
        // Additionally, in some circumstances it may return null
        // ODD!!

        Object persistentLoginValue = m_isPersistent.getValue(state);

        String value;

        if (persistentLoginValue == null) {
            return defaultValue;
        }

        if (persistentLoginValue instanceof String[]) {
            value = ((String[]) persistentLoginValue)[0];
        } else if (persistentLoginValue instanceof String) {
            value = (String) persistentLoginValue;
        } else {
            value = "0";
        }

        return "1".equals(value);
    }

    /**
     *
     * @param state
     */
    protected void redirectToNewUserPage(final PageState state) {

        String url = LoginServlet.getNewUserPageURL();

        ParameterMap map = new ParameterMap();
        map.setParameter(LoginHelper.RETURN_URL_PARAM_NAME,
                         m_returnURL.getValue(state));
        map.setParameter(FORM_PERSISTENT_LOGIN_P,
                         m_isPersistent.getValue(state));
        map.setParameter(FORM_EMAIL,
                         m_loginName.getValue(state));

        final URL dest = com.arsdigita.web.URL.there(state.getRequest(),
                                                     url,
                                                     map);

        throw new RedirectSignal(dest, true);

    }

}
