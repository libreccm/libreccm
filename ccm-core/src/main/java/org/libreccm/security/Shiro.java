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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;

/**
 * This application scoped CDI bean acts as bridge between CDI and Shiro. It
 * initialises the Shiro environment and provides the Shiro
 * {@link SecurityManager} and the current Shiro {@link Subject} via CDI
 * producer methods.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationScoped
public class Shiro {

    private static final Logger LOGGER = LogManager.getLogger(
            Shiro.class);

    /**
     * Path to the Shiro INI file.
     */
    private static final String INI_FILE = "classpath:shiro.ini";

    /**
     * The Shiro {@code SecurityManager}.
     */
    private SecurityManager securityManager;

    /**
     * Initialises Shiro. The CDI container will call this method after creating
     * an instance of this bean.
     */
    @PostConstruct
    public void init() {
        LOGGER.debug("Shiro initialising...");
        securityManager = new IniSecurityManagerFactory(INI_FILE)
                .createInstance();
        LOGGER.debug("Shiro SecurityManager created sucessfully.");
        SecurityUtils.setSecurityManager(securityManager);
        LOGGER.debug("Shiro initialised successfully.");
    }

    /**
     * Provides access Shiro's {@link SecurityManager}.
     *
     * @return The Shiro {@link SecurityManager}.
     */
    @Produces
    @Named("securityManager")
    public SecurityManager getSecurityManager() {
        return securityManager;
    }

    /**
     * Provides access the the current Shiro {@link Subject}.
     *
     * @return The current {@link Subject}.
     *
     */
    @Produces
    public Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    public Subject getPublicUser() {
        return buildInternalSubject("public-user");
    }

    public Subject getSystemUser() {
        return buildInternalSubject("system-user");
    }

    private Subject buildInternalSubject(final String userName) {
        final PrincipalCollection principals = new SimplePrincipalCollection(
                userName, "CcmShiroRealm");
        final Subject publicUser = new Subject.Builder()
                .principals(principals)
                .authenticated(true)
                .buildSubject();

        return publicUser;
    }

}
