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
package org.libreccm.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.core.CcmObject;
import org.libreccm.core.CoreConstants;
import org.libreccm.modules.InstallEvent;
import org.libreccm.security.ApplicationRoleSetup;
import org.libreccm.security.Permission;
import org.libreccm.security.Role;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.persistence.EntityManager;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class AbstractCcmApplicationSetup {

    private static final Logger LOGGER = LogManager.getLogger(
        AbstractCcmApplicationSetup.class);

    private final EntityManager entityManager;
    private final ApplicationRoleSetup appRoleSetup;

    public AbstractCcmApplicationSetup(final InstallEvent event) {
        this.entityManager = event.getEntityManager();
        appRoleSetup = new ApplicationRoleSetup(entityManager);
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected Properties getIntegrationProps() {
        try (InputStream inputStream = getClass().getResourceAsStream(
            CoreConstants.INTEGRATION_PROPS)) {
            final Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException ex) {
            LOGGER.warn(
                "Failed to load integration properties. Using empty properties.",
                ex);
            return new Properties();
        }
    }

    public Role createRole(final String name) {
        return appRoleSetup.createRole(name);
    }

    public void grantPermission(final Role role, final String privilege) {
        appRoleSetup.grantPermission(role, privilege);
    }

    public void grantPermission(final Role role, 
                                final String privilege,
                                final CcmObject ccmObject) {
        appRoleSetup.grantPermission(role, privilege, ccmObject);
    }

    public abstract void setup();

}
