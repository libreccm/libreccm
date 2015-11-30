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
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.URLParameter;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.ui.UI;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.URL;
import com.arsdigita.web.ReturnSignal;
import java.util.concurrent.Callable;

import static com.arsdigita.ui.login.LoginConstants.*;

import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.libreccm.cdi.utils.CdiLookupException;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Shiro;
import org.libreccm.security.User;
import org.libreccm.security.UserManager;
import org.libreccm.security.UserRepository;

/**
 * Creates a new user. Collects user's basic info, such as email, password,
 * first name, last name, etc; then tries to create the user in the database. If
 * returnURL is passed in to the form, then redirects to that URL; otherwise
 * redirects to the user workspace.
 *
 *
 * @author Michael Bryzek
 * @author Roger Hsueh
 * @author Sameer Ajmani
 *
 * @version $Id$
 *
 *
 */
public class UserNewForm extends UserForm implements FormInitListener,
                                                     FormProcessListener,
                                                     FormValidationListener {

    private static final Logger s_log = Logger.getLogger(UserNewForm.class);

    static final String FORM_NAME = "user-new";

    private Hidden m_loginName;
    private Hidden m_returnURL;
    private Hidden m_persistent;

    public UserNewForm() {
        this(new ColumnPanel(2));
    }

    @Override
    protected User getUser(final PageState state) {
        return null; // don't load any data into form
    }

    public UserNewForm(final Container panel) {
        super(FORM_NAME, panel, true);

        addInitListener(this);
        addValidationListener(this);
        addProcessListener(this);

        // save return URL
        m_returnURL = new Hidden(new URLParameter(
                LoginHelper.RETURN_URL_PARAM_NAME));
        m_returnURL.setPassIn(true);
        add(m_returnURL);

        // save email address or screen name
        m_loginName = new Hidden(new StringParameter(FORM_LOGIN));
        m_loginName.setPassIn(true);
        add(m_loginName);

        // save persistent flag
        ArrayParameter cookieP = new ArrayParameter(FORM_PERSISTENT_LOGIN_P);
        m_persistent = new Hidden(cookieP);
        m_persistent.setPassIn(true);
        add(m_persistent);
    }

    @Override
    public void init(final FormSectionEvent event)
            throws FormProcessException {
        PageState state = event.getPageState();
        // clear passwords from form data
        m_password.setValue(state, "");
        m_confirm.setValue(state, "");
        String loginName = (String) m_loginName.getValue(state);
        if (loginName != null) {
            if (KernelConfig.getConfig().emailIsPrimaryIdentifier()) {
                m_email.setValue(state, loginName);
            } else {
                m_screenName.setValue(state, loginName);
            }
        }
    }

    @Override
    public void process(final FormSectionEvent event)
            throws FormProcessException {
        PageState state = event.getPageState();

        final InternetAddress address = (InternetAddress) m_email
                .getValue(state);
        final String email = address.getAddress();

        // TODO: set additional emails
        final String password = (String) m_password.getValue(state);
        final String firstName = (String) m_firstName.getValue(state);
        final String lastName = (String) m_lastName.getValue(state);
        final String screenName;
        if (KernelConfig.getConfig().emailIsPrimaryIdentifier()) {
            screenName = null;
        } else {
            screenName = (String) m_screenName.getValue(state);
        }

        final Exception[] formExceptions = new Exception[]{null};

        final Shiro shiro;
        try {
            final CdiUtil cdiUtil = new CdiUtil();
            shiro = cdiUtil.findBean(Shiro.class);
        } catch (CdiLookupException ex) {
            throw new UncheckedWrapperException(ex);
        }

        shiro.getSystemUser().execute(new Callable<Void>() {
            @Override
            public Void call() throws Exception {

                final UserManager userManager;
                try {
                    final CdiUtil cdiUtil = new CdiUtil();
                    userManager = cdiUtil.findBean(UserManager.class);
                } catch (CdiLookupException ex) {
                    throw new UncheckedWrapperException(ex);
                }

                userManager.createUser(firstName,
                                       lastName,
                                       screenName,
                                       email,
                                       password);

                return null;
            }
        });

        try {
            final String loginName;
            if (KernelConfig.getConfig().emailIsPrimaryIdentifier()) {
                loginName = email;
            } else {
                loginName = screenName;
            }

            final CdiUtil cdiUtil = new CdiUtil();
            final Subject subject = cdiUtil.findBean(Subject.class);

            if (subject.isAuthenticated()) {
                subject.logout();
            }

            final UsernamePasswordToken token = new UsernamePasswordToken(
                    loginName, password);
            subject.login(token);
        } catch (CdiLookupException | AuthenticationException ex) {
            s_log.error("login failed for new user", ex);
            throw new FormProcessException(ex);
        }

        // redirect to workspace or return URL, if specified
        final HttpServletRequest req = state.getRequest();
        final String url = UI.getWorkspaceURL();
        final URL fallback = com.arsdigita.web.URL.there(req, url);
        throw new ReturnSignal(req, fallback);
    }

}
