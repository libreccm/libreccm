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
package org.libreccm.core;

import static org.libreccm.core.CoreConstants.*;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * This class provides complex operations on {@link User} objects like updating
 * the password. To use this class add an injection point to your class.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class UserManager {

    /**
     * {@link UserRepository} for interacting with the database. The method
     * takes care of hashing the password with random salt.
     *
     */
    @Inject
    private transient UserRepository userRepository;

    private byte[] generateHash(final byte[] password, final byte[] salt) {
        final byte[] saltedPassword = new byte[password.length + salt.length];

        System.arraycopy(password, 0, saltedPassword, 0, password.length);
        System.arraycopy(salt, 0, saltedPassword, password.length, salt.length);

        try {
            final MessageDigest digest = MessageDigest.getInstance(
                getHashAlgorithm());

            final byte[] hashedPassword = digest.digest(saltedPassword);

            return hashedPassword;
        } catch (NoSuchAlgorithmException ex) {
            throw new PasswordHashingFailedException(
                "Failed to generate hash for password", ex);
        }
    }

    /**
     * Update the password of an user.
     *
     * @param user     The user whose password is to be updated.
     * @param password The new password.
     */
    public void updatePassword(final User user, final String password) {
        final Random random = new Random(System.currentTimeMillis());
        final byte[] passwordBytes = password.getBytes(
            StandardCharsets.UTF_8);
        final byte[] salt = new byte[getSaltLength()];
        random.nextBytes(salt);

        final byte[] hashedBytes = generateHash(passwordBytes, salt);

        final Base64 base64 = new Base64();
        final String hashedPassword = base64.encodeToString(hashedBytes);
        final String saltStr = base64.encodeToString(salt);

        user.setPassword(hashedPassword);
        user.setSalt(saltStr);
        userRepository.save(user);
    }

    /**
     * Verify a password for the a specific user.
     *
     * @param user     The user whose password is to be checked.
     * @param password The password to verify.
     *
     * @return {@code true} if the provided password matches the password
     *         stored, {@code false} if not.
     */
    public boolean verifyPasswordForUser(final User user,
                                         final String password) {
        final Base64 base64 = new Base64();

        final byte[] hashed = generateHash(
            password.getBytes(StandardCharsets.UTF_8),
            base64.decode(user.getSalt()));

        final String hashedPassword = base64.encodeAsString(hashed);

        return hashedPassword.equals(user.getPassword());

    }

    public boolean verifyPasswordForScreenname(final String screenname,
                                               final String password)
        throws UserNotFoundException {

        final User user = userRepository.findByScreenName(screenname);

        if (user == null) {
            throw new UserNotFoundException(String.format(
                "No user identified by screenname '%s' found.", screenname));
        } else {
            return verifyPasswordForUser(user, password);
        }
    }

    public boolean verifyPasswordForEmail(final String emailAddress,
                                          final String password)
        throws UserNotFoundException {

        final User user = userRepository.findByEmailAddress(emailAddress);

        if (user == null) {
            throw new UserNotFoundException(String.format(
                "No user identified by email address '%s' found.", emailAddress));
        } else {
            return verifyPasswordForUser(user, password);
        }
    }

    /**
     * Gets the hash algorithm to use.
     *
     * ToDo: Make configurable.
     *
     * @return At the moment SHA-512, will be made configurable.
     */
    public String getHashAlgorithm() {
        return "SHA-512";
    }

    /**
     * Returns the length for the salt (number of bytes).
     *
     * ToDo: Make configurable.
     *
     * @return At the moment 256. Will be made configurable.
     */
    public int getSaltLength() {
        return 256;
    }

}
