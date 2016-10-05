/*
 * Copyright (C) 2016 LibreCCM Foundation.
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

import com.arsdigita.kernel.security.SecurityConfig;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import org.libreccm.configuration.ConfigurationManager;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.crypto.hash.format.DefaultHashFormatFactory;
import org.apache.shiro.crypto.hash.format.HashFormat;
import org.apache.shiro.crypto.hash.format.HashFormatFactory;
import org.apache.shiro.crypto.hash.format.Shiro1CryptFormat;
import org.apache.shiro.util.ByteSource;
import org.libreccm.core.CoreConstants;

/**
 * This class manages the generation and delation of {@link OneTimeAuthToken}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class OneTimeAuthManager {

    @Inject
    private EntityManager entityManager;

    @Inject
    private ConfigurationManager confManager;

    /**
     * Creates a new one time authentication token which for the provided user
     * and the provided purpose. The length of the token and how long the token
     * is valid are configured by the {@link OneTimeAuthConfig}.
     * 
     * This method generates the token <em>and</em> saves it in the database.
     * 
     * Please note: The returned token contains the not hashed 
     * authentication key/token string. The string is saved as hash in the 
     * database (using the same hash algorithm as for the passwords). This 
     * allows the caller to put the token string into an email (or an other 
     * message) and send it to the user.
     *
     * @param user    The user for which the one time authentication token is
     *                generated.
     * @param purpose The purpose for which the token is generated.
     *
     * @return The one time authentication token with the not hashed token.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_SYSTEM)
    @Transactional(Transactional.TxType.REQUIRED)
    public OneTimeAuthToken createForUser(
        final User user, final OneTimeAuthTokenPurpose purpose) {
        if (user == null || purpose == null) {
            throw new IllegalArgumentException(
                "user and purpose and mandatory for creating a one "
                    + "time auth token.");
        }

        final OneTimeAuthConfig config = confManager.findConfiguration(
            OneTimeAuthConfig.class);

        // Token with cleartext token string
        final OneTimeAuthToken tmpToken = new OneTimeAuthToken();
        tmpToken.setUser(user);
        tmpToken.setPurpose(purpose);

        final String tokenStr = RandomStringUtils.randomAlphanumeric(config.
            getTokenLength());
        tmpToken.setToken(tokenStr);

        final LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        final LocalDateTime valid = now.plusSeconds(config.getTokenValid());
        final Date validUntil = Date.from(valid.toInstant(ZoneOffset.UTC));

        tmpToken.setValidUntil(validUntil);

        final OneTimeAuthToken token = new OneTimeAuthToken();
        token.setUser(user);
        token.setPurpose(purpose);
        token.setValidUntil(validUntil);

        //Get configuration values for hashing from SecurityConfig
        final SecurityConfig securityConfig = confManager.findConfiguration(
            SecurityConfig.class);
        final String hashAlgo = securityConfig.getHashAlgorithm();
        final int iterations = securityConfig.getHashIterations();

        //Create the hash using Shiro's SimpleHash class
        final SimpleHash hash = new SimpleHash(hashAlgo,
                                               tokenStr.toCharArray(),
                                               generateSalt(),
                                               iterations);
        //We want to use the Shiro1 format for storing the password. This
        //format includes the algorithm used, the salt, the number of 
        //iterations used and the hashed password in special formatted string.
        final HashFormatFactory hashFormatFactory
                                    = new DefaultHashFormatFactory();
        final HashFormat hashFormat = hashFormatFactory.getInstance(
            Shiro1CryptFormat.class.getName());
        token.setToken(hashFormat.format(hash));

        entityManager.persist(token);

        return tmpToken;
    }

    /**
     * Retrieves the one time auth token for the provided user and purpose. This
     * method does <strong>not</strong> not check of the token is still valid!
     *
     * @param user    The user for which the token is retrieved.
     * @param purpose The purpose of the token to retrieve.
     *
     * @return The one time auth token for the provided user and purpose or
     *         {@code null} if there is no such token.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_SYSTEM)
    public List<OneTimeAuthToken> retrieveForUser(
        final User user, final OneTimeAuthTokenPurpose purpose) {
        if (user == null || purpose == null) {
            throw new IllegalArgumentException(
                "user and purpose and mandatory for retrieving a one "
                    + "time auth token.");
        }

        final TypedQuery<OneTimeAuthToken> query = entityManager
            .createNamedQuery("OneTimeAuthToken.findByUserAndPurpose",
                              OneTimeAuthToken.class);
        query.setParameter("user", user);
        query.setParameter("purpose", purpose);

        return query.getResultList();
    }

    /**
     * Checks of there is a {@link OneTimeAuthToken} which has not been expired
     * for the provided user and purpose.
     *
     * @param user    The user.
     * @param purpose The purpose of the token.
     *
     * @return {@code true} if there is a valid token for the provided user and
     *         purpose, {@code false} if not.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_SYSTEM)
    public boolean validTokenExistsForUser(
        final User user, final OneTimeAuthTokenPurpose purpose) {
        if (user == null || purpose == null) {
            throw new IllegalArgumentException(
                "user and purpose and mandatory for validiting a one time "
                    + "auth token.");
        }

        final List<OneTimeAuthToken> tokens = retrieveForUser(user, purpose);
        if (tokens == null || tokens.isEmpty()) {
            return false;
        } else {
            boolean result = false;
            for (OneTimeAuthToken token : tokens) {
                if (isValid(token)) {
                    result = true;
                    break;
                }
            }
            return result;
        }
    }

    /**
     * Checks of the provided @link OneTimeAuthToken} is not expired.
     *
     * @param token The token to validate.
     *
     * @return {@code true} if the token is valid, {@code false} if not.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_SYSTEM)
    public boolean isValid(final OneTimeAuthToken token) {
        if (token == null) {
            throw new IllegalArgumentException("Can't validate a token null");
        }

        final LocalDateTime validUntil = LocalDateTime.
            ofInstant(token.getValidUntil().toInstant(),
                      ZoneOffset.UTC);
        final LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        return validUntil.isAfter(now);
    }

    /**
     * Verifies a submitted token against a token from the database.
     *
     *
     * @param token          The token against which the submitted token is
     *                       verified.
     * @param submittedToken The submitted token.
     *
     * @return {@code true} if the submitted token is valid and matches {@link token},
     * {@code false} if not.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_SYSTEM)
    public boolean verify(final OneTimeAuthToken token,
                          final String submittedToken) {
        if (token == null || submittedToken == null) {
            throw new IllegalArgumentException(
                "token or submittedToken can't be null");
        }

        if (!isValid(token)) {
            return false;
        }

        //The token is stored as hash (like the passwords. We
        //the facilities provided by Shiro to verify the submitted token
        //Create a new Shiro PasswordMatcher instance
        final PasswordMatcher matcher = new PasswordMatcher();
        //Get the PasswordService instance from the matcher (the PasswordService
        //class provides the methods we need here).
        final PasswordService service = matcher.getPasswordService();

        return service.passwordsMatch(submittedToken, token.getToken());

//        return token.getToken().equals(submittedToken);
    }

    /**
     * Invaliades (deletes) a {@link OneTimeAuthToken}.
     *
     * @param token The token to invalidate.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_SYSTEM)
    @Transactional(Transactional.TxType.REQUIRED)
    public void invalidate(final OneTimeAuthToken token) {
        if (token == null) {
            throw new IllegalArgumentException("Can't invalidate a token null");
        }

        //Ensure that we have a none detached instance
        final OneTimeAuthToken delete = entityManager.find(
            OneTimeAuthToken.class, token.getTokenId());

        entityManager.remove(delete);
    }

    /**
     * Helper method for generating a random salt. The length of the generated
     * salt is configured in the {@link LegacySecurityConfig}.
     *
     * @return A new random salt.
     */
    private ByteSource generateSalt() {
        final SecurityConfig securityConfig = confManager.findConfiguration(
            SecurityConfig.class);
        final int generatedSaltSize = securityConfig.getSaltLength();

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
