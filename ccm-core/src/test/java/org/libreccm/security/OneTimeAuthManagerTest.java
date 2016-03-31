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

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.CreateSchema;
import org.jboss.arquillian.persistence.PersistenceTest;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
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
import org.libreccm.categorization.Categorization;
import org.libreccm.core.CcmObject;
import org.libreccm.jpa.EntityManagerProducer;
import org.libreccm.jpa.utils.MimeTypeConverter;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.tests.categories.IntegrationTest;
import org.libreccm.testutils.EqualsVerifier;
import org.libreccm.web.CcmApplication;
import org.libreccm.workflow.Workflow;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema("create_ccm_core_schema.sql")
public class OneTimeAuthManagerTest {

    @Inject
    private OneTimeAuthManager oneTimeAuthManager;

    @Inject
    private UserRepository userRepository;

    @Inject
    private EntityManager entityManager;

    public OneTimeAuthManagerTest() {

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
                    "LibreCCM-org.libreccm.security.OneTimeAuthManagerTest.war")
            .addPackage(org.libreccm.security.OneTimeAuthManager.class.
                getPackage())
            .addPackage(org.libreccm.core.CcmObject.class.getPackage())
            .addPackage(org.libreccm.categorization.Categorization.class.
                getPackage())
            .addPackage(
                org.libreccm.configuration.ConfigurationManager.class.
                getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class.getPackage()).
            addPackage(org.libreccm.web.CcmApplication.class.getPackage())
            .addPackage(org.libreccm.workflow.Workflow.class.getPackage())
            .addPackage(org.libreccm.jpa.EntityManagerProducer.class.
                getPackage())
            .addPackage(org.libreccm.jpa.utils.MimeTypeConverter.class.
                getPackage())
            .addPackage(org.libreccm.testutils.EqualsVerifier.class.
                getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class.
                getPackage())
            .addAsLibraries(libs)
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "WEB-INF/web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml");
    }

    @Test
    @InSequence(10)
    public void isManagerInjected() {
        assertThat(oneTimeAuthManager, is(not(nullValue())));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/OneTimeAuthManagerTest/"
                    + "after-create.xml",
        excludeColumns = {"token_id", "token", "valid_until"})
    @InSequence(100)
    public void createTokenForUser() {
        final User mmuster = userRepository.findByName("mmuster");
        final OneTimeAuthToken token = oneTimeAuthManager.createForUser(
            mmuster,
            OneTimeAuthTokenPurpose.EMAIL_VERIFICATION);

        final LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        final LocalDateTime tokenValidUntil = LocalDateTime.ofInstant(
            token.getValidUntil().toInstant(), ZoneOffset.UTC);

        assertThat(String.format(
            "tokenValidUntil = \"%s\" is not after now = \"%s\"",
            tokenValidUntil.toString(),
            now.toString()),
                   tokenValidUntil.isAfter(now),
                   is(true));

        final long diff = now.until(tokenValidUntil, ChronoUnit.SECONDS);
        assertThat(diff, is(greaterThan(3590L)));
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(200)
    public void createTokenNullUser() {
        oneTimeAuthManager.createForUser(
            null, OneTimeAuthTokenPurpose.RECOVER_PASSWORD);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(300)
    public void createTokenNullPurpose() {
        final User user = new User();
        oneTimeAuthManager.createForUser(user, null);
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @InSequence(400)
    public void retrieveTokenForUser() {
        final User jdoe = userRepository.findByName("jdoe");

        final Optional<OneTimeAuthToken> result = oneTimeAuthManager.
            retrieveForUser(
                jdoe, OneTimeAuthTokenPurpose.EMAIL_VERIFICATION);

        assertThat(result.isPresent(), is(true));

        final OneTimeAuthToken token = result.get();
        assertThat(token.getUser(), is(not(nullValue())));
        assertThat(token.getUser().getName(), is(equalTo("jdoe")));
        assertThat(token.getToken(), is(equalTo(
                   "biXOpuxIPXuRgx9jhk1PzZVIeKGaTmg2qTKoTQ4tl9iiweQ0e5mfmdFI1KjDwjPi")));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @InSequence(500)
    public void retrieveNotExistingTokenForUser() {
        final User mmuster = userRepository.findByName("mmuster");

        final Optional<OneTimeAuthToken> result = oneTimeAuthManager.
            retrieveForUser(
                mmuster, OneTimeAuthTokenPurpose.EMAIL_VERIFICATION);

        assertThat(result.isPresent(), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(600)
    public void retrieveTokenNullUser() {
        oneTimeAuthManager.retrieveForUser(
            null, OneTimeAuthTokenPurpose.RECOVER_PASSWORD);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(700)
    public void retrieveTokenNullPurpose() {
        final User mmuster = userRepository.findByName("mmuster");

        oneTimeAuthManager.retrieveForUser(mmuster, null);
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @InSequence(800)
    public void validTokenExistsForUser() {
        final User user = userRepository.findByName("jdoe");

        assertThat(
            oneTimeAuthManager.validTokenExistsForUser(
                user, OneTimeAuthTokenPurpose.EMAIL_VERIFICATION),
            is(true));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @InSequence(900)
    public void validTokenDoesNotExist() {
        final User user = userRepository.findByName("mmuster");

        assertThat(
            oneTimeAuthManager.validTokenExistsForUser(
                user, OneTimeAuthTokenPurpose.EMAIL_VERIFICATION),
            is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(1000)
    public void validTokenNullUser() {
        oneTimeAuthManager.validTokenExistsForUser(
            null, OneTimeAuthTokenPurpose.EMAIL_VERIFICATION);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(1100)
    public void validTokenNullPurpose() {
        final User user = userRepository.findByName("mmuster");
        oneTimeAuthManager.validTokenExistsForUser(
            user, null);
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @InSequence(1200)
    public void isValid() {
        final User jdoe = userRepository.findByName("jdoe");

        final Optional<OneTimeAuthToken> result = oneTimeAuthManager.
            retrieveForUser(
                jdoe, OneTimeAuthTokenPurpose.EMAIL_VERIFICATION);

        assertThat(result.isPresent(), is(true));
        assertThat(oneTimeAuthManager.isValid(result.get()), is(true));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @InSequence(1300)
    public void isInvalid() {
        final User jdoe = userRepository.findByName("jdoe");

        final Optional<OneTimeAuthToken> result = oneTimeAuthManager.
            retrieveForUser(
                jdoe, OneTimeAuthTokenPurpose.EMAIL_VERIFICATION);

        assertThat(result.isPresent(), is(true));
        final OneTimeAuthToken token = result.get();

        final LocalDateTime date = LocalDateTime
            .now(ZoneOffset.UTC).minus(1800, ChronoUnit.SECONDS);
        token.setValidUntil(Date.from(date.toInstant(ZoneOffset.UTC)));

        assertThat(oneTimeAuthManager.isValid(token), is(false));

    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(1400)
    public void isValidNullToken() {
        oneTimeAuthManager.isValid(null);
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/OneTimeAuthManagerTest/"
                    + "after-invalidate.xml")
    @InSequence(1500)
    public void invalidateToken() {
        final User jdoe = userRepository.findByName("jdoe");

        final Optional<OneTimeAuthToken> result = oneTimeAuthManager.
            retrieveForUser(
                jdoe, OneTimeAuthTokenPurpose.EMAIL_VERIFICATION);

        assertThat(result.isPresent(), is(true));
        oneTimeAuthManager.invalidate(result.get());
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(1400)
    public void invalidateNullToken() {
        oneTimeAuthManager.invalidate(null);
    }

}
