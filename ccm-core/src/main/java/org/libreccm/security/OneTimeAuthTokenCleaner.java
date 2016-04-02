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

import org.libreccm.configuration.ConfigurationManager;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Singleton
@Startup
public class OneTimeAuthTokenCleaner {

    @Resource
    private TimerService timerService;

    @Inject
    private ConfigurationManager configurationManager;

    @Inject
    EntityManager entityManager;

    @Inject
    private OneTimeAuthManager oneTimeAuthManager;

    @PostConstruct
    public void init() {
        final OneTimeAuthConfig config = configurationManager.findConfiguration(
            OneTimeAuthConfig.class);

        final long interval = config.getTokenValid() * 1000;

        timerService.createIntervalTimer(interval, interval, new TimerConfig());
    }

    @Timeout
    public void cleanupTokens() {
        final TypedQuery<OneTimeAuthToken> query = entityManager.createQuery(
            "SELECT t FROM OneTimeAuthToken t", OneTimeAuthToken.class);
        final List<OneTimeAuthToken> tokens = query.getResultList();

        tokens.stream()
            .filter((token) -> (!oneTimeAuthManager.isValid(token)))
            .forEach((token) -> {
                oneTimeAuthManager.invalidate(token);
            });
    }

}
