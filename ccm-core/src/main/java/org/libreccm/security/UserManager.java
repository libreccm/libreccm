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
package org.libreccm.security;

import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.kernel.security.SecurityConfig;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.crypto.hash.format.DefaultHashFormatFactory;
import org.apache.shiro.crypto.hash.format.HashFormat;
import org.apache.shiro.crypto.hash.format.HashFormatFactory;
import org.apache.shiro.crypto.hash.format.Shiro1CryptFormat;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.CoreConstants;
import org.libreccm.core.EmailAddress;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.validation.executable.ValidateOnExecution;
import java.io.Serializable;

/**
 * Provides various operations for user objects.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class UserManager implements Serializable {
    private static final long serialVersionUID = -5457302841422810115L;

    @Inject
    private UserRepository userRepository;

    @Inject
    private Subject subject;

    @Inject
    private Shiro shiro;

    @Inject
    private PermissionChecker permissionChecker;

    @Inject
    private ConfigurationManager confManager;

    /**
     * Creates a new user and saves the user in the database. The method also
     * creates the password hash.
     *
     * @param givenName    The given name of the new user.
     * @param familyName   The family name of the new user.
     * @param name         The name of the new user.
     * @param emailAddress The email address of the new user.
     * @param password     The password of the new user. The password is hashed
     *                     using the algorithm configured in the
     *                     {@link SecurityConfig}.
     *
     * @return The new user.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @ValidateOnExecution
    @Transactional(Transactional.TxType.REQUIRED)
    public User createUser(final String givenName,
                           final String familyName,
//                           @Pattern(regexp = "[a-zA-Z0-9\\-_]*")
                           final String name,
                           final String emailAddress,
                           final String password) {
        final User user = new User();
        user.setGivenName(givenName);
        user.setFamilyName(familyName);
        user.setName(name);
        final EmailAddress email = new EmailAddress();
        email.setAddress(emailAddress);
        user.setPrimaryEmailAddress(email);
        email.setVerified(true);
        if (password == null) {
            user.setPassword(null);
        } else {
            user.setPassword(hashPassword(password));
        }

        userRepository.save(user);

        return user;
    }

    /**
     * Updates the password of a user. This method allows {@code null} as
     * password value. If a user has no password in the database this means that
     * the user can't login or that the authentication for this user is done by
     * an external system. Only the user itself or user to which the
     * {@code admin} privilege has been granted can update the password of user.
     *
     * @param user        The user which password should be upgraded.
     * @param newPassword The new password. The password is hashed using the
     *                    algorithm configured in the {@link SecurityConfig}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void updatePassword(@NotNull final User user,
                               final String newPassword) {
        // We can't use the authorisation annotations here because we have two 
        // options. First we check if the current subject is the user whos
        // password is updated. If not we check if the current subject has admin
        // privileges.
        final String userIdentifier;
        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);
        if (kernelConfig.emailIsPrimaryIdentifier()) {
            userIdentifier = user.getPrimaryEmailAddress().getAddress();
        } else {
            userIdentifier = user.getName();
        }

        if (subject.isAuthenticated()
                && userIdentifier.equals(subject.getPrincipal())) {
            user.setPassword(hashPassword(newPassword));
            shiro.getSystemUser().execute(() -> userRepository.save(user));
        } else {
            permissionChecker.checkPermission(CoreConstants.PRIVILEGE_ADMIN);
            user.setPassword(hashPassword(newPassword));
            shiro.getSystemUser().execute(() -> userRepository.save(user));
        }
    }

    /**
     * Verifies the password of a user. This can be useful if you want to verify
     * the password of a user already logged in again.
     *
     * @param user     The user against which the password is verified.
     * @param password The password to verify.
     *
     * @return {@code true} if the provided passworda matches the password from
     *         the database, {@code false} otherwise.
     */
    public boolean verifyPassword(final User user, final String password) {
        //Create a new Shiro PasswordMatcher instance
        final PasswordMatcher matcher = new PasswordMatcher();
        //Get the PasswordService instance from the matcher (the PasswordService
        //class provides the methods we need here).
        final PasswordService service = matcher.getPasswordService();

        return service.passwordsMatch(password, user.getPassword());
    }

    /**
     * Helper method for creating the hash of a password.
     *
     * @param password The password to hash.
     *
     * @return The hashed password.b
     */
    private String hashPassword(final String password) {
        //Get the values from the SecurityConfig
        final SecurityConfig securityConfig = SecurityConfig.getConfig();
        final String hashAlgo = securityConfig.getHashAlgorithm();
        final int iterations = securityConfig.getHashIterations();

        //Create the hash using Shiro's SimpleHash class
        final SimpleHash hash = new SimpleHash(hashAlgo,
                                               password.toCharArray(),
                                               generateSalt(),
                                               iterations);

        //We want to use the Shiro1 format for storing the password. This
        //format includes the algorithm used, the salt, the number of 
        //iterations used and the hashed password in special formatted string.
        final HashFormatFactory hashFormatFactory
                                    = new DefaultHashFormatFactory();
        final HashFormat hashFormat = hashFormatFactory.getInstance(
            Shiro1CryptFormat.class.getName());

        return hashFormat.format(hash);
    }

    /**
     * Helper method for generating a random salt. The length of the generated
     * salt is configured in the {@link LegacySecurityConfig}.
     *
     * @return A new random salt.
     */
    private ByteSource generateSalt() {
        final int generatedSaltSize = SecurityConfig.getConfig().getSaltLength();

        if (generatedSaltSize % 8 != 0) {
            throw new IllegalArgumentException(
                "Salt length is not a multipe of 8");
        }

        final SecureRandomNumberGenerator generator
                                              = new SecureRandomNumberGenerator();
        final int byteSize = generatedSaltSize / 8; //generatedSaltSize is in *bits* - convert to byte size:
        return generator.nextBytes(byteSize);
    }

}
