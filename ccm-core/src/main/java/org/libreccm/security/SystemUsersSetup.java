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
import org.libreccm.core.CoreConstants;
import org.libreccm.core.EmailAddress;
import org.libreccm.modules.InstallEvent;

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

    private static final String ADMIN_NAME = "admin.name";
    private static final String ADMIN_FAMILY_NAME = "admin.family_name";
    private static final String ADMIN_GIVEN_NAME = "admin.given_name";
    private static final String ADMIN_EMAIL_ADDRESS = "admin.email_address";
    private static final String ADMIN_PASSWORD = "admin.password";

    //Default password is "libreccm"
    private static final String DEFAULT_ADMIN_PW
                                    = "$shiro1$SHA-512$500000$MFPkVikNoRrBZ8R8CxQIHA==$ybEECtSPukmXDbV27a3LnWktFsh9lQl2ZYqCUtV0NF9G35Rt0+Tzp1msNLBQUVv15SrsdFgBSfhgWfZFyTva+Q==";

    private final EntityManager entityManager;

    public SystemUsersSetup(final InstallEvent event) {
        this.entityManager = event.getEntityManager();
    }

    public void setupSystemUsers() {
        LOGGER.info("Creating system users...");
        createAdmin();
        createPublicUser();
    }

    private void createAdmin() {
        LOGGER.info("Creating admin user...");

        final Properties integrationProps = getIntegrationProps();
        final String adminName = integrationProps.getProperty(ADMIN_NAME,
                                                              "admin");
        final String adminFamilyName = integrationProps.getProperty(
            ADMIN_FAMILY_NAME, "LibreCCM");
        final String adminGivenName = integrationProps.getProperty(
            ADMIN_GIVEN_NAME, "System Administrator");
        final String adminEmailAddress = integrationProps.getProperty(
            ADMIN_EMAIL_ADDRESS, "admin@libreccm.example");
        final String adminPassword = integrationProps.getProperty(
            ADMIN_PASSWORD, DEFAULT_ADMIN_PW);;

        final User admin = new User();
        admin.setName(adminName);
        admin.setFamilyName(adminFamilyName);
        admin.setGivenName(adminGivenName);
        final EmailAddress adminEmail = new EmailAddress();
        adminEmail.setAddress(adminEmailAddress);
        admin.setPrimaryEmailAddress(adminEmail);
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

    private Properties getIntegrationProps() {
        try (final InputStream inputStream = getClass().getResourceAsStream(
            CoreConstants.INTEGRATION_PROPS)) {
            final Properties properties = new Properties();
            if (inputStream == null) {
                LOGGER.warn("No integration properties available.");
                properties.load(inputStream);
            }
            return properties;
        } catch (IOException ex) {
            LOGGER.warn("Failed to load integration properties from bundle. "
                            + "Using empty integration properties.", ex);
            return new Properties();
        }
    }

}
