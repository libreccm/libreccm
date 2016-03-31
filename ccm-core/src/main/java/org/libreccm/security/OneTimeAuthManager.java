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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.libreccm.configuration.ConfigurationManager;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.apache.commons.lang.RandomStringUtils;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class OneTimeAuthManager {

    @Inject
    private EntityManager entityManager;

    @Inject
    private ConfigurationManager configurationManager;

    /**
     * Creates a new one time auth token which for the provided user and the
     * provided purpose. The length of the token and how long the token is valid
     * are configured by the {@link OneTimeAuthConfig}.
     *
     * This method generates the token <em>and</em> saves it in the database.
     *
     * @param user The user for which the one time auth token is generated.
     * @param purpose The purpose for which the token is generated.
     *
     * @return The one time auth token.
     */
    public OneTimeAuthToken createForUser(
            final User user, final OneTimeAuthTokenPurpose purpose) {
        if (user == null || purpose == null) {
            throw new IllegalArgumentException(
                    "user and purpose and mandatory for creating a one "
                            + "time auth token.");
        }

        final OneTimeAuthConfig config = configurationManager.findConfiguration(
                OneTimeAuthConfig.class);

        final OneTimeAuthToken token = new OneTimeAuthToken();
        token.setUser(user);
        token.setPurpose(purpose);

        final String tokenStr = RandomStringUtils.randomAscii(config.
                getTokenLength());
        token.setToken(tokenStr);

        final LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        final LocalDateTime valid = now.plusSeconds(config.getTokenValid());
        final Date validUntil = Date.from(valid.toInstant(ZoneOffset.UTC));

        token.setValidUntil(validUntil);

        entityManager.persist(token);

        return token;
    }

    /**
     * Retrieves the one time auth token for the provided user and purpose. This
     * method does <strong>not</strong> not check of the token is still valid!
     *
     * @param user The user for which the token is retrieved.
     * @param purpose The purpose of the token to retrieve.
     *
     * @return The one time auth token for the provided user and purpose or
     * {@code null} if there is no such token.
     */
    public Optional<OneTimeAuthToken> retrieveForUser(
            final User user, final OneTimeAuthTokenPurpose purpose) {
        if (user == null || purpose == null) {
            throw new IllegalArgumentException(
                    "user and purpose and mandatory for retrieving a one "
                            + "time auth token.");
        }

        final TypedQuery query = entityManager.createNamedQuery(
                "OneTimeAuthToken.findByUserAndPurpose", OneTimeAuthToken.class);
        query.setParameter("user", user);
        query.setParameter("purpose", purpose);

        final List<OneTimeAuthToken> queryResult = query.getResultList();
        if (queryResult.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(queryResult.get(0));
        }
    }

    /**
     * Checks of there is a valid one time auth token for the provided user and
     * purpose.
     *
     * @param user The user.
     * @param purpose The purpose of the token.
     *
     * @return {@code true} if there is a valid token for the provided user and
     * purpose, {@code false} if not.
     */
    public boolean validTokenExistsForUser(
            final User user, final OneTimeAuthTokenPurpose purpose) {
        if (user == null || purpose == null) {
            throw new IllegalArgumentException(
                    "user and purpose and mandatory for validiting a one time "
                            + "auth token.");
        }

        final Optional<OneTimeAuthToken> token = retrieveForUser(user, purpose);
        if (token.isPresent()) {
            return isValid(token.get());
        } else {
            return false;
        }
    }

    /**
     * Validates a {@link OneTimeAuthToken}.
     * 
     * @param token The token to valid.
     * 
     * @return {@code true} if the token is valid, {@code false} if not.
     */
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
     * Invalides (deletes) a {@link OneTimeAuthToken}.
     * 
     * @param token The token to invalidate.
     */
    public void invalidate(final OneTimeAuthToken token) {
        if (token == null) {
            throw new IllegalArgumentException("Can't invalidate a token null");
        }
        
        entityManager.remove(token);
    }

}
