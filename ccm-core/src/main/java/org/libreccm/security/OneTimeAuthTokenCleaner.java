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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.configuration.ConfigurationManager;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 * This EJB uses the {@link TimerService} to run a cleanup task periodically to
 * remove all expired {@link OneTimeAuthToken}s. The task period is the same as
 * the time a {@link OneTimeAuthToken} is valid.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
//@Startup
@Singleton
//@Stateless
public class OneTimeAuthTokenCleaner {

    private static final Logger LOGGER = LogManager.getLogger(
        OneTimeAuthTokenCleaner.class);

    @Resource
    private TimerService timerService;

    @Inject
    private ConfigurationManager configurationManager;

    @Inject
    private EntityManager entityManager;

    @Inject
    private OneTimeAuthManager oneTimeAuthManager;
    
    @Inject
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        LOGGER.debug("Initialising OneTimeAuthTokenCleaner...");
        final OneTimeAuthConfig config = configurationManager.findConfiguration(
            OneTimeAuthConfig.class);

        final long interval = config.getTokenValid() * 1000;
//        final long interval = 60 * 60 * 1000;

        LOGGER.debug("Creating interval for {} s.", interval / 1000);
//        LOGGER.debug("First run cleaning process will be executed in {} s.",
//                     interval / 1000);
        timerService.createIntervalTimer(interval,
                                         interval,
                                         new TimerConfig());
    }

    @Timeout
    @Transactional(Transactional.TxType.REQUIRED)
    public void cleanupTokens() {
        LOGGER.debug("Cleaning up one time auth tokens...");
        final TypedQuery<OneTimeAuthToken> query = entityManager.createQuery(
            "SELECT t FROM OneTimeAuthToken t", OneTimeAuthToken.class);
        final List<OneTimeAuthToken> tokens = query.getResultList();

        LOGGER.debug("Found {} one time auth tokens.", tokens.size());
        if (LOGGER.isDebugEnabled()) {
            final LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
            LOGGER.debug("Current time (UTC) is: {}", now);
            tokens.forEach(t -> {
                if (oneTimeAuthManager.isValid(t)) {
                    LOGGER.debug("OneTimeAuthToken with id {} is still valid. "
                                     + "Expires at {}.",
                                 t.getTokenId(),
                                 t.getValidUntil());
                } else {
                    LOGGER.debug("OneTimeAuthToken with id {} is invalid. "
                                     + "Expires at {} UTC.",
                                 t.getTokenId(),
                                 t.getValidUntil());
                }
            });
        }

        tokens.stream()
            .filter((token) -> (!oneTimeAuthManager.isValid(token)))
            .forEach((token) -> {
                LOGGER.debug("Token with id {} expired at {} UTC. "
                                 + "Invalidating token.",
                             token.getTokenId(), token.getValidUntil());
                oneTimeAuthManager.invalidate(token);
                if (token.getPurpose()
                    == OneTimeAuthTokenPurpose.ACCOUNT_ACTIVATION) {
                    final User user = token.getUser();
                    userRepository.delete(user);
                }
            });
    }

}
