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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

/**
 * This {@code LoginModule} provides common methods for {@code LoginModule}s
 * using a username/password combination to authenticate users. If provides
 * common methods. It tries to fetch username and password from shared data
 * provided by the calling {@link LoginContext} if possible. Otherwise is
 * queries the user using {@link Callback}s. Username and password are stored in
 * the shared data for use by other {@code LoginModule}s.
 *
 * This class in a reworked version of
 * {@code org.arsdigita.kernel.security.AbstractPasswordLoginModule} developed by Sameer
 * Ajmani (according to the JavaDoc). The main differences is that the new
 * version uses generics and multi-catch for exceptions. Also the code,
 * especially if clauses have been reworked to match the conventions enforced by
 * PMD and other style checkers. Also the methods {@code getPassword} and
 * {@code getUsername} have been renamed to {@code retrievePassword} and
 * {@code retrieveUsername} because this class is not a Java Bean and the values
 * are not Java Bean Properties.
 *
 * This class is abstract. The methods
 * {@link #checkPassword(java.lang.String, char[])}, {@link #commit()}, {@link #abort()}
 * and {@link #logout()} are left to implement by sub classes.
 *
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class AbstractPasswordLoginModule implements LoginModule {

    private static final Logger LOGGER = LogManager.getLogger(AbstractPasswordLoginModule.class);
    /**
     * Key for username in shared data map.
     */
    private static final String NAME_KEY = "javax.security.auth.login.name";
    /**
     * Key for password in shared data map.
     */
    private static final String PASSWORD_KEY
                                    = "javax.security.auth.login.password";

    /**
     * Fields set by the {@link #initialize(javax.security.auth.Subject, javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)
     * method.
     * We only set the fields we use in this class.
     */
    private transient CallbackHandler callbackHandler;
    private transient Map<String, ?> sharedState;

    /**
     * {@inheritDoc }
     *
     * @param subject         {@inheritDoc }
     * @param callbackHandler {@inheritDoc }
     * @param sharedState     {@inheritDoc }
     * @param options         {@inheritDoc }
     */
    @Override
    public void initialize(final Subject subject,
                           final CallbackHandler callbackHandler,
                           final Map<String, ?> sharedState,
                           final Map<String, ?> options) {
        LOGGER.debug("Initalizing...");
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        LOGGER.debug("Initalized...");
    }

    /**
     * Implementation of the {@link LoginModule#login()} method. Retrieves the
     * username and the password using the {@link #retrieveUsername()} and
     * {@link #retrievePassword()} methods and call the
     * {@link #checkPassword(java.lang.String, char[])} method.
     *
     * @return The return value of the
     *         {@link #checkPassword(java.lang.String, char[])} method.
     *
     * @throws LoginException If something goes wrong.
     */
    @Override
    public boolean login() throws LoginException {
        LOGGER.debug("Trying to authenticate user...");
        return checkPassword(retrieveUsername(), retrievePassword());
    }

    /**
     * {@inheritDoc }
     *
     * @return {@inheritDoc }
     *
     * @throws LoginException {@inheritDoc }
     */
    @Override
    public abstract boolean commit() throws LoginException;

    /**
     * {@inheritDoc }
     *
     * @return {@inheritDoc }
     *
     * @throws LoginException {@inheritDoc }
     */
    @Override
    public abstract boolean abort() throws LoginException;

    /**
     * {@inheritDoc }
     *
     * @return {@inheritDoc }
     *
     * @throws LoginException {@inheritDoc }
     */
    @Override
    public abstract boolean logout() throws LoginException;

    /**
     * Attempts to read username from shared data map; otherwise retreives it
     * using a NameCallback.
     *
     * @return the username.
     *
     * @throws LoginException if an error occurs.
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
    private String retrieveUsername() throws LoginException {
        String username = (String) sharedState.get(NAME_KEY);
        if (username == null) {
            try {
                final NameCallback callback = new NameCallback("Username: ");
                callbackHandler.handle(new Callback[]{callback});
                username = callback.getName();
            } catch (IOException | UnsupportedCallbackException ex) {
                throw new LoginException("Could not get Username");
            }
        }

        return username;
    }

    /**
     * Attempts to read password from shared data map; otherwise retreives it
     * using a PasswordCallback.
     *
     * @return the password.
     *
     * @throws LoginException if an error occurs.
     */
    @SuppressWarnings("PMD.StackTraceLost")
    private String retrievePassword() throws LoginException {
        String password = (String) sharedState.get(PASSWORD_KEY);

        if (password == null) {
            try {
                final PasswordCallback callback = new PasswordCallback(
                    "Password: ",
                    false);
                callbackHandler.handle(new Callback[]{callback});
                password = new String(callback.getPassword());
            } catch (UnsupportedCallbackException | IOException ex) {
                throw new LoginException("Could not get password");
            }
        }

        return password;
    }

    /**
     * Checks whether the given username/password combination is valid.
     *
     * @param username the username to check.
     * @param password the password to check.
     *
     * @return {@code true} if the username/password combination is valid,
     *         {@code false} to otherwise.
     *
     * @throws LoginException If an error occurs.
     */
    protected abstract boolean checkPassword(String username, String password)
        throws LoginException;

}
