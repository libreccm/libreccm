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

import org.apache.shiro.subject.ExecutionException;

import java.io.File;

import javax.inject.Inject;

import org.hibernate.exception.ConstraintViolationException;

import static org.hamcrest.Matchers.*;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.CreateSchema;
import org.jboss.arquillian.persistence.PersistenceTest;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.test.spi.ArquillianProxyException;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.tests.categories.IntegrationTest;

import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_core_schema.sql"})
public class UserManagerTest {

    @Inject
    private UserManager userManager;

    @Inject
    private UserRepository userRepository;

    @Inject
    private Shiro shiro;

    public UserManagerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Deployment
    public static WebArchive createDeployment() {
        final PomEquippedResolveStage pom = Maven
            .resolver()
            .loadPomFromFile("pom.xml");
        final PomEquippedResolveStage dependencies = pom.
            importCompileAndRuntimeDependencies();
        final File[] libs = dependencies.resolve().withTransitivity().asFile();

        for (File lib : libs) {
            System.err.printf("Adding file '%s' to test archive...%n",
                              lib.getName());
        }

        return ShrinkWrap
            .create(WebArchive.class,
                    "LibreCCM-org.libreccm.security.UserManagerTest.war")
            .addPackage(org.libreccm.categorization.Categorization.class
                .getPackage())
            .addPackage(org.libreccm.cdi.utils.CdiUtil.class.getPackage())
            .addPackage(org.libreccm.configuration.Configuration.class
                .getPackage())
            .addPackage(org.libreccm.core.CcmObject.class.getPackage())
            .addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.jpa.utils.MimeTypeConverter.class
                .getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class.getPackage())
            .addPackage(org.libreccm.security.User.class.getPackage())
            .addPackage(org.libreccm.web.CcmApplication.class.getPackage())
            .addPackage(org.libreccm.workflow.Workflow.class.getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addPackage(org.libreccm.testutils.EqualsVerifier.class
                .getPackage())
            .addPackage(com.arsdigita.kernel.KernelConfig.class.getPackage())
            .addPackage(com.arsdigita.kernel.security.SecurityConfig.class
                .getPackage())
            .addPackage(com.arsdigita.util.UncheckedWrapperException.class
                .getPackage())
            .addPackage(org.libreccm.cdi.utils.CdiUtil.class.getPackage())
            .addAsLibraries(libs)
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsWebInfResource("META-INF/beans.xml", "beans.xml");
    }

    @Test
    @InSequence(100)
    public void userManagerIsInjected() {
        assertThat(userManager, is(not(nullValue())));
    }

    @Test
    @InSequence(110)
    public void userRepositoryIsInjected() {
        assertThat(userRepository, is(not(nullValue())));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/UserManagerTest/data.yml")
    @InSequence(200)
    public void verifyPassword() {
        final User jdoe = userRepository.findByName("jdoe");
        final User mmuster = userRepository.findByName("mmuster");
        final User joe = userRepository.findByName("joe");

        assertThat(userManager.verifyPassword(jdoe, "foo123"), is(true));
        assertThat(userManager.verifyPassword(mmuster, "foo123"), is(true));
        assertThat(userManager.verifyPassword(joe, "foo123"), is(true));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/UserManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/UserManagerTest/"
                    + "after-create-user.yml",
        excludeColumns = {"party_id", "password"})
    @InSequence(300)
    public void createUser() {
        shiro.getSystemUser().execute(
            () -> userManager.createUser("Jane",
                                         "Doe",
                                         "jane",
                                         "jane.doe@example.org",
                                         "foo456"));

        final User jane2 = userRepository.findByName("jane");
        assertThat(userManager.verifyPassword(jane2, "foo456"), is(true));
    }

    @Test(expected = ArquillianProxyException.class)
    @UsingDataSet("datasets/org/libreccm/security/UserManagerTest/data.yml")
    @ShouldThrowException(ConstraintViolationException.class)
    @InSequence(400)
    public void createUserWithInValidName() throws Throwable {
        try {
            shiro.getSystemUser().execute(
                () -> userManager.createUser("Jane",
                                             "Doe",
                                             "j#ne",
                                             "jane.doe@example.org",
                                             "foo456"));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
        fail();
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/UserManagerTest/data.yml")
    @InSequence(500)
    public void updatePassword() {
        final User jdoe = userRepository.findByName("jdoe");
        shiro.getSystemUser().execute(
            () -> userManager.updatePassword(jdoe, "foo456"));

        final User jdoe2 = userRepository.findByName("jdoe");
        assertThat(userManager.verifyPassword(jdoe, "foo456"), is(true));
        assertThat(userManager.verifyPassword(jdoe2, "foo456"), is(true));

        assertThat(userManager.verifyPassword(jdoe, "foo123"), is(false));
        assertThat(userManager.verifyPassword(jdoe2, "foo123"), is(false));
    }

    @Test(expected = ArquillianProxyException.class)
    @UsingDataSet("datasets/org/libreccm/security/UserManagerTest/data.yml")
    @ShouldThrowException(ConstraintViolationException.class)
    @InSequence(600)
    public void updatePasswordNullUser() throws Throwable {
        try {
            shiro.getSystemUser().execute(
                () -> userManager.updatePassword(null, "foo"));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
        fail();
    }

}
