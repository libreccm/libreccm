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
    private static final String LOGIN_CONTEXT = "Register";

    @Inject
    private transient CcmSessionContext sessionContext;

//    @Inject
//    private transient UserRepository userRepository;

    public void login(final String username, final String password)
        throws LoginException {

        final CallbackHandler callbackHandler = new LoginCallbackHandler(
            username, password);
        final LoginContext loginContext = new LoginContext(
            LOGIN_CONTEXT,
            callbackHandler);
        loginContext.login();
        final Subject subject = loginContext.getSubject();

        final Set<UserPrincipal> principals = subject.getPrincipals(
            UserPrincipal.class);
        if (principals.isEmpty()) {
            throw new LoginException("No principal set");
        } else {
            final Iterator<UserPrincipal> iterator = principals.iterator();
            final UserPrincipal principal = iterator.next();
            final User user = principal.getUser();
            
            sessionContext.setCurrentParty(user);
        }
    }
    
    private static class LoginCallbackHandler implements CallbackHandler {

        private final transient String username;
        private final transient String password;

        public LoginCallbackHandler(final String username,
                                    final String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        @SuppressWarnings("PMD.UseVarargs") //Can't use varargs here
        public void handle(final Callback[] callbacks)
            throws IOException, UnsupportedCallbackException {

            for (final Callback callback : callbacks) {
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
