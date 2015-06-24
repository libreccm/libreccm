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


import java.io.UnsupportedEncodingException;
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
    
    /**
     * Update the password of an user.
     * 
     * @param user The user whose password is to be updated.
     * @param password The new password.
     */
    public void updatePassword(final User user, final String password) {

        try {
            final Random random = new Random(System.currentTimeMillis());
            final byte[] passwordBytes = password.getBytes("UTF-8");
            final byte[] salt = new byte[getSaltLength()];
            random.nextBytes(salt);

            final byte[] saltedPassword = new byte[passwordBytes.length
                                                       + salt.length];
            System.arraycopy(passwordBytes,
                             0,
                             saltedPassword,
                             0,
                             passwordBytes.length);
            System.arraycopy(salt,
                             0,
                             saltedPassword,
                             passwordBytes.length,
                             salt.length);
            final MessageDigest digest = MessageDigest.getInstance(
                getHashAlgorithm());
            final byte[] hashedBytes = digest.digest(saltedPassword);
            
            final String hashedPassword = new String(hashedBytes, "UTF-8");

            user.setPassword(hashedPassword);
            userRepository.save(user);
            
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(String.format(
                "Configured hash algorithm '%s 'is not available.",
                getHashAlgorithm()), ex);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("UTF-8 charset is not supported.");
        }
    }

    /**
     * Gets the hash algorithm to use.
     * 
     * ToDo: Make configurable.
     * 
     * @return At the moment SHA-512, will be made configurable.
     */
    private String getHashAlgorithm() {
        return "SHA-512";
    }

    /**
     * Returns the length for the salt (number of bytes).
     * 
     * ToDo: Make configurable.
     * 
     * @return At the moment 256. Will be made configurable.
     */
    private int getSaltLength() {
        return 256;
    }

}
