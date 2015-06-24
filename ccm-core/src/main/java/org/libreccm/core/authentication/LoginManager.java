/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.core.authentication;

import org.libreccm.core.CcmSessionContext;
import org.libreccm.core.User;
import org.libreccm.core.UserRepository;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

/**
 * Provides methods for authenticating a user and for logging out a user.
 *
 * Under the hood JAAS is used for authentication.
 *
 * If a user is authenticated successfully the user object is stored in the
 * session scoped bean {@link CcmSessionContext}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class LoginManager {

    /**
     * Name of the register login context.
     */
    private static final String REGISTER_LOGIN_CONTEXT = "RegisterLoginContext";

    @Inject
    private transient CcmSessionContext sessionContext;

    @Inject
    private transient UserRepository userRepository;

    public void login(final String username, final String password)
        throws LoginException {

        final CallbackHandler callbackHandler = new LoginCallbackHandler(
            username, password);
        final LoginContext loginContext = new LoginContext(
            REGISTER_LOGIN_CONTEXT,
            callbackHandler);
        loginContext.login();
        final Subject subject = loginContext.getSubject();

        final Set<SubjectPrincipal> principals = subject.getPrincipals(
            SubjectPrincipal.class);
        if (principals.isEmpty()) {
            throw new LoginException("No principal set");
        } else {
            final Iterator<SubjectPrincipal> iterator = principals.iterator();
            final SubjectPrincipal principal = iterator.next();
            final User user = userRepository.findById(principal.getSubjectId());
            
            sessionContext.setCurrentParty(user);
        }
    }

    private class LoginCallbackHandler implements CallbackHandler {

        private final String username;
        private final String password;

        public LoginCallbackHandler(final String username,
                                    final String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public void handle(final Callback[] callbacks)
            throws IOException, UnsupportedCallbackException {

            for (Callback callback : callbacks) {
                if (callback instanceof NameCallback) {
                    ((NameCallback) callback).setName(username);
                } else if (callback instanceof PasswordCallback) {
                    ((PasswordCallback) callback).setPassword(password
                        .toCharArray());
                } else {
                    throw new UnsupportedCallbackException(callback);
                }
            }
        }

    }

}
