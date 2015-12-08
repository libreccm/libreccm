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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.core.CcmCore;
import org.libreccm.core.EmailAddress;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.persistence.EntityManager;

/**
 * Class used by {@link CcmCore#install(org.libreccm.modules.InstallEvent)} to
 * create the system users.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SystemUsersSetup {

    private static final Logger LOGGER = LogManager.getLogger(
        SystemUsersSetup.class);
    
    private static final String DEFAULT_ADMIN_PW = "$shiro1$SHA-512$500000$MFPkVikNoRrBZ8R8CxQIHA==$UvgO2K+poSRGw5co63P3ygpWsX7H9N0TgqdrZPBqdXv6Q+/OCL/qOocVbg65/Yjv5hyri6A3zhw7K8mEgpISoA==";

    private final EntityManager entityManager;

    public SystemUsersSetup(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void setupSystemUsers() {
        LOGGER.info("Creating system users...");
        createAdmin();
        createPublicUser();
    }

    private void createAdmin() {
        LOGGER.info("Creating admin user...");
        final User admin = new User();
        admin.setName("admin");
        admin.setFamilyName("LibreCCM");
        admin.setGivenName("System Administrator");
        final EmailAddress adminEmail = new EmailAddress();
        adminEmail.setAddress("admin@localhost");
        admin.setPrimaryEmailAddress(adminEmail);

        String adminPassword = DEFAULT_ADMIN_PW;
        final InputStream inputStream = getClass().getResourceAsStream(
            "/integration.properties");
        if (inputStream == null) {
            LOGGER.warn("No integration.properties file found. Using default "
                            + "password (see documentation)");
        } else {
            final Properties properties = new Properties();
            try {
                properties.load(inputStream);
                final String password = properties.getProperty("admin.password");
                if((password != null) && !password.isEmpty()) {
                    adminPassword = password;
                }
            } catch (IOException ex) {
                LOGGER.warn("Failed to load integration.properties. "
                                + "Using default password.",
                            ex);
            }
        }
        admin.setPassword(adminPassword);

        final Role adminRole = new Role();
        adminRole.setName("system-administrator");

        final RoleMembership membership = new RoleMembership();
        membership.setRole(adminRole);
        membership.setMember(admin);
        
        final Permission adminPermission = new Permission();
        adminPermission.setGrantee(adminRole);
        adminPermission.setGrantedPrivilege("*");

        admin.addRoleMembership(membership);
        adminRole.addMembership(membership);

        entityManager.persist(admin);
        entityManager.persist(adminRole);
        entityManager.persist(membership);
        entityManager.persist(adminPermission);
    }
    
    private void createPublicUser() {
        final User user = new User();
        user.setName("public-user");
        user.setFamilyName("LibreCCM");
        user.setGivenName("Public User");
        final EmailAddress email = new EmailAddress();
        email.setAddress("public-user@localhost");
        user.setPrimaryEmailAddress(email);

        entityManager.persist(user);
    }

}
