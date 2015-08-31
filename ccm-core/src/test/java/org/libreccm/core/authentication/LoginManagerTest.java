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
package org.libreccm.core.authentication;

import static org.hamcrest.Matchers.*;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.CreateSchema;
import org.jboss.arquillian.persistence.PersistenceTest;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.After;
import org.junit.AfterClass;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.core.CcmObject;
import org.libreccm.core.CcmSessionContext;
import org.libreccm.core.EmailAddress;
import org.libreccm.core.Subject;
import org.libreccm.core.User;
import org.libreccm.tests.categories.IntegrationTest;

import java.io.File;

import javax.inject.Inject;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_core_schema.sql"})
public class LoginManagerTest {

    @Inject
    private transient LoginManager loginManager;

    @Inject
    private transient CcmSessionContext ccmSessionContext;

    public LoginManagerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp()  {
         final String[] config = new String[]{
            String.format("Register:%s:requisite",
                          LocalLoginModule.class.getName())};
        final LoginConfigBuilder loginConfigBuilder = new LoginConfigBuilder(
            config);
        Configuration.setConfiguration(loginConfigBuilder.build());
    }

    @After
    public void tearDown() {
    }

    @Deployment
    public static WebArchive createDeployment() {
        final PomEquippedResolveStage pom = Maven
            .resolver()
            .loadPomFromFile("pom.xml");
        final PomEquippedResolveStage dependencies = pom
            .importCompileAndRuntimeDependencies();
        final File[] libs = dependencies.resolve().withTransitivity().asFile();

        for (File lib : libs) {
            System.err.printf("Adding file '%s' to test archive...%n",
                              lib.getName());
        }

        return ShrinkWrap
            .create(WebArchive.class,
                    "LibreCCM-org.libreccm.core.authentication.LoginManagerTest.war")
            .addPackage(CcmObject.class.getPackage())
            .addPackage(org.libreccm.web.Application.class.getPackage())
            .addPackage(org.libreccm.categorization.Category.class.
                getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class.getPackage())
            .addPackage(org.libreccm.cdi.utils.CdiUtil.class.getPackage())
            .addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.jpa.utils.MimeTypeConverter.class
                .getPackage())
            .addPackage(org.libreccm.testutils.EqualsVerifier.class.
                getPackage())
            .addPackage(org.libreccm.core.authentication.LoginManager.class
                .getPackage())
            .addPackage(com.arsdigita.kernel.KernelConfig.class.getPackage())
            .addPackage(com.arsdigita.runtime.AbstractConfig.class.getPackage())
            .addPackage(com.arsdigita.util.parameter.AbstractParameter.class
                .getPackage())
            .addPackage(com.arsdigita.util.UncheckedWrapperException.class
                .getPackage())
            .addPackage(com.arsdigita.xml.XML.class
                .getPackage())
            .addPackage(com.arsdigita.xml.formatters.DateTimeFormatter.class
                .getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addPackage(com.arsdigita.web.CCMApplicationContextListener.class
                .getPackage())
            .addAsLibraries(libs)
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsResource(
                "configtests/com/arsdigita/kernel/KernelConfigTest/ccm-core.config",
                "ccm-core.config")
            .addAsWebInfResource(
                "configtests/com/arsdigita/kernel/KernelConfigTest/registry.properties",
                "conf/registry/registry.properties")
            .addAsWebInfResource(
                "configtests/com/arsdigita/kernel/KernelConfigTest/kernel.properties",
                "conf/registry/ccm-core/kernel.properties")
            .addAsResource(
                "com/arsdigita/kernel/KernelConfig_parameter.properties",
                "com/arsdigita/kernel/KernelConfig_parameter.properties")
            .addAsWebInfResource("test-web.xml", "WEB-INF/web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml");
    }

    @InSequence(1)
    public void isLoginManagerInjected() {
        assertThat(loginManager, is(not(nullValue())));
    }

    @InSequence(2)
    public void isCcmSessionContextInjected() {
        assertThat(ccmSessionContext, is(not(nullValue())));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/authentication/LoginManagerTest/data.yml")
    @InSequence(10)
    public void loginValidCredentials() throws LoginException {
        loginManager.login("jdoe@example.com", "foobar");

        assertThat(ccmSessionContext.getCurrentSubject(), is(not(nullValue())));
        final EmailAddress emailAddress = new EmailAddress();
        emailAddress.setAddress("jdoe@example.com");
        emailAddress.setBouncing(false);
        emailAddress.setVerified(true);
        
        final Subject subject = ccmSessionContext.getCurrentSubject();
        assertThat(subject, is(instanceOf(User.class)));
        
        final User user = (User) subject;
        assertThat(user.getEmailAddresses(), contains(equalTo(emailAddress)));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/authentication/LoginManagerTest/data.yml")
    @InSequence(20)
    public void loginWrongCredentials() throws LoginException {
        try {
            loginManager.login("jdoe@example.com", "wrong-pw");
        } catch (LoginException ex) {
            assertThat(ccmSessionContext.getCurrentSubject(), is(nullValue()));
            return;
        }

        fail("No login exception was thrown.");
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/authentication/LoginManagerTest/data.yml")
    @InSequence(30)
    public void loginEmptyPassword() {
        try {
            loginManager.login("jdoe@example.com", "");
        } catch (LoginException ex) {
            assertThat(ccmSessionContext.getCurrentSubject(), is(nullValue()));
            return;
        }

        fail("No login exception was thrown.");
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/authentication/LoginManagerTest/data.yml")
    @InSequence(40)
    public void loginEmptyUserName() {
        try {
            loginManager.login("", "correct-pw");
        } catch (LoginException ex) {
            assertThat(ccmSessionContext.getCurrentSubject(), is(nullValue()));
            return;
        }

        fail("No login exception was thrown.");
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/authentication/LoginManagerTest/data.yml")
    @InSequence(50)
    public void loginNullPassword() {
        try {
            loginManager.login("jdoe@example.com", null);
        } catch (LoginException ex) {
            assertThat(ccmSessionContext.getCurrentSubject(), is(nullValue()));
            return;
        }

        fail("No login exception was thrown.");
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/authentication/LoginManagerTest/data.yml")
    @InSequence(60)
    public void loginNullUsername() {
        try {
            loginManager.login(null, "correct-pw");
        } catch (LoginException ex) {
            assertThat(ccmSessionContext.getCurrentSubject(), is(nullValue()));
            return;
        }

        fail("No login exception was thrown.");
    }

}
