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

import com.arsdigita.kernel.KernelConfig;

import org.libreccm.core.User;
import org.libreccm.core.UserRepository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.inject.Inject;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.LoginException;

/**
 * Checks a username and a password in the database.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class LocalLoginModule extends PasswordLoginModule {

    /**
     * {@link UserRepository} instance for getting user accounts from the
     * database.
     */
    @Inject
    private transient UserRepository userRepository;

    private transient Subject subject;

    @Override
    public void initialize(final Subject subject,
                           final CallbackHandler callbackHandler,
                           final Map<String, ?> sharedState,
                           final Map<String, ?> options) {
        super.initialize(subject, callbackHandler, sharedState, options);
        this.subject = subject;
    }

    @Override
    public boolean commit() throws LoginException {
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        return true;
    }

    /**
     * Checks the provided password against the (hashed) password in the
     * database.
     *
     * @param username The username identifying the user account the verify.
     * @param password The password to verify.
     *
     * @return {@code true} if the password matches the password in the
     * database, {@code false} if not or if there is no user account identified
     * by the provided user name.
     *
     * @throws LoginException If an error occurs in the process.
     */
    @Override
    protected boolean checkPassword(final String username,
                                    final String password)
            throws LoginException {

        //Depending on the configured user identifier retrieve the user account
        //using the screen name or the email address.
        final User user;
        if (KernelConfig.getConfig().emailIsPrimaryIdentifier()) {
            user = userRepository.findByEmailAddress(username);
        } else {
            user = userRepository.findByScreenName(username);
        }

        //If no matching user is found report this by throwing an exception.
        if (user == null) {
            throw new AccountNotFoundException(String.format(
                    "No user account identified by '%s' found.", username));
        }

        // Verify the password. The algorithm used for hashing is stored in the 
        // database so we need to retrieve the correct MessageDigest instance 
        // first.
        try {
            final MessageDigest digest = MessageDigest.getInstance(user
                    .getHashAlgorithm());
            final String saltedPassword = String.format("%s%s",
                                                        password,
                                                        user.getSalt());
            final String passwordHash = new String(digest.digest(
                    saltedPassword.getBytes()));

            if (passwordHash.equals(user.getPassword())) {
                subject.getPrincipals().add(new UserPrincipal(user));
                return true;
            } else {
                return false;
            }
        } catch (NoSuchAlgorithmException ex) {
            throw new LoginException(String.format(
                    "Failed to validate password because the password stored for "
                    + "user '%s' in the database is hashed with algorithm '%s' "
                            + "which is not avialable.",
                    username, user.getHashAlgorithm()));
        }

    }

}
