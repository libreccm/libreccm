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
package org.libreccm.core;

import static org.hamcrest.Matchers.*;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


import org.apache.commons.codec.binary.Base64;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.arquillian.persistence.CreateSchema;
import org.libreccm.tests.categories.IntegrationTest;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
    private transient UserRepository userRepository;

    @Inject
    private transient UserManager userManager;

    @PersistenceContext
    private transient EntityManager entityManager;

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
                    "LibreCCM-org.libreccm.core.UserManagerTest.war")
            .addPackage(User.class.getPackage())
            .addPackage(org.libreccm.web.Application.class.getPackage())
            .addPackage(org.libreccm.categorization.Category.class.
                getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class.getPackage()).
            addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.jpa.utils.MimeTypeConverter.class
                .getPackage())
            .addPackage(org.libreccm.testutils.EqualsVerifier.class.
                getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addAsLibraries(libs)
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "WEB-INF/web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml");

    }

    @Test
    @InSequence(10)
    public void userRepoIsInjected() {
        assertThat(userRepository, is(not(nullValue())));
    }

    @Test
    @InSequence(20)
    public void userManagerIsInjected() {
        assertThat(userManager, is(not(nullValue())));
    }

    @Test
    @InSequence(30)
    public void entityManagerIsInjected() {
        assertThat(entityManager, is(not(nullValue())));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/core/UserRepositoryTest/data.yml")
    @InSequence(100)
    public void updatePassword() throws NoSuchAlgorithmException,
                                        UnsupportedEncodingException {

        final User jdoe = userRepository.findById(-10L);

        assertThat(jdoe, is(not(nullValue())));
        assertThat(jdoe.getScreenName(), is("jdoe"));

        final String newPassword = "foobar";

        userManager.updatePassword(jdoe, newPassword);

        final Base64 base64 = new Base64();
        final User user = entityManager.find(User.class, -10L);
        final byte[] passwordBytes = newPassword.getBytes(
            StandardCharsets.UTF_8);
        final String salt = user.getSalt();
        final byte[] saltBytes = base64.decode(salt);

        assertThat(saltBytes.length, is(userManager.getSaltLength()));

        final MessageDigest digest = MessageDigest.getInstance(userManager
            .getHashAlgorithm());

        final byte[] saltedPassword = new byte[passwordBytes.length
                                                   + saltBytes.length];
        System.arraycopy(passwordBytes,
                         0,
                         saltedPassword,
                         0,
                         passwordBytes.length);
        System.arraycopy(saltBytes,
                         0,
                         saltedPassword,
                         passwordBytes.length,
                         saltBytes.length);
        final byte[] hashedBytes = digest.digest(saltedPassword);

        final String hashed = base64.encodeToString(hashedBytes);
        assertThat(user.getPassword(), is(equalTo(hashed)));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/UserManagerTest/verify-password.yml")
    @InSequence(200)
    public void verifyPasswordForUser() {
        final User user = userRepository.findById(-10L);

        //userManager.updatePassword(user, "foobar");
        final boolean result = userManager.verifyPasswordForUser(user, "foobar");

        assertThat(result, is(true));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/UserManagerTest/verify-password.yml")
    @InSequence(300)
    public void verifyPasswordForScreenname() throws UserNotFoundException {
        final boolean result = userManager.verifyPasswordForScreenname(
            "jdoe", "foobar");

        assertThat(result, is(true));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/UserManagerTest/verify-password.yml")
    @InSequence(400)
    public void verifyPasswordForEmail() throws UserNotFoundException {
        final boolean result = userManager.verifyPasswordForEmail(
            "john.doe@example.com", "foobar");

        assertThat(result, is(true));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/UserManagerTest/verify-password.yml")
    @InSequence(500)
    public void verifyPasswordForUserWrongPassword() {
        final User user = userRepository.findById(-10L);

        final boolean result = userManager.verifyPasswordForUser(user, "wrong");

        assertThat(result, is(false));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/UserManagerTest/verify-password.yml")
    @InSequence(600)
    public void verifyPasswordForScreennameWrongPassword() throws
        UserNotFoundException {
        final boolean result = userManager.verifyPasswordForScreenname(
            "jdoe", "wrong");

        assertThat(result, is(false));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/UserManagerTest/verify-password.yml")
    @InSequence(400)
    public void verifyPasswordForEmailWrongPassword() throws
        UserNotFoundException {
        final boolean result = userManager.verifyPasswordForEmail(
            "john.doe@example.com", "wrong");

        assertThat(result, is(false));
    }

    @Test(expected = UserNotFoundException.class)
    @ShouldThrowException(UserNotFoundException.class)
    @UsingDataSet(
        "datasets/org/libreccm/core/UserManagerTest/verify-password.yml")
    @InSequence(700)
    public void verifyPasswordForScreennameNoUser() throws UserNotFoundException {
        userManager.verifyPasswordForScreenname("nobody", "foobar");
    }

    @Test(expected = UserNotFoundException.class)
    @ShouldThrowException(UserNotFoundException.class)
    @UsingDataSet(
        "datasets/org/libreccm/core/UserManagerTest/verify-password.yml")
    @InSequence(800)
    public void verifyPasswordForEmailNoUser() throws UserNotFoundException {
        userManager.verifyPasswordForEmail("nobody@example.com", "foobar");
    }

}
