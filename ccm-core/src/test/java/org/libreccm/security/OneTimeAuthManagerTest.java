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

import org.apache.shiro.subject.ExecutionException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.inject.Inject;

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
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.tests.categories.IntegrationTest;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import static org.libreccm.testutils.DependenciesHelpers.*;

import org.jboss.arquillian.persistence.CleanupUsingScript;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema("create_ccm_core_schema.sql")
@CleanupUsingScript({"cleanup.sql"})
public class OneTimeAuthManagerTest {

    @Inject
    private OneTimeAuthManager oneTimeAuthManager;

    @Inject
    private UserRepository userRepository;

    @Inject
    private Shiro shiro;

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
        return ShrinkWrap
            .create(WebArchive.class,
                    "LibreCCM-org.libreccm.security.OneTimeAuthManagerTest.war")
            .addPackage(org.libreccm.security.OneTimeAuthManager.class
                .getPackage())
            .addPackage(org.libreccm.core.CcmObject.class.getPackage())
            .addPackage(org.libreccm.categorization.Categorization.class
                .getPackage())
            .addPackage(org.libreccm.configuration.ConfigurationManager.class
                .getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class.getPackage())
            .addPackage(org.libreccm.web.CcmApplication.class.getPackage())
            .addPackage(org.libreccm.workflow.Workflow.class.getPackage())
            .addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.jpa.utils.MimeTypeConverter.class
                .getPackage())
            .addClass(com.arsdigita.kernel.security.SecurityConfig.class)
            .addPackage(org.libreccm.testutils.EqualsVerifier.class
                .getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addPackage(org.libreccm.cdi.utils.CdiUtil.class.getPackage())
            .addClass(com.arsdigita.kernel.KernelConfig.class)
            .addClass(com.arsdigita.kernel.security.SecurityConfig.class)
            .addClass(org.libreccm.portation.Portable.class)
            .addAsLibraries(getModuleDependencies())
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsWebInfResource("META-INF/beans.xml", "beans.xml");
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
        final User mmuster = userRepository.findByName("mmuster").get();
        final OneTimeAuthToken token = shiro.getSystemUser().execute(() -> {
            return oneTimeAuthManager.createForUser(
                mmuster,
                OneTimeAuthTokenPurpose.EMAIL_VERIFICATION);
        });

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
    public void createTokenNullUser() throws Throwable {
        try {
            shiro.getSystemUser().execute(
                () -> oneTimeAuthManager.createForUser(
                    null, OneTimeAuthTokenPurpose.RECOVER_PASSWORD));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(300)
    public void createTokenNullPurpose() throws Throwable {
        final User user = new User();
        try {
            shiro.getSystemUser().execute(() -> oneTimeAuthManager
                .createForUser(user, null));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @InSequence(400)
    public void retrieveTokenForUser() {
        final User jdoe = userRepository.findByName("jdoe").get();

        final List<OneTimeAuthToken> result = shiro.getSystemUser().execute(
            () -> {
                return oneTimeAuthManager.retrieveForUser(
                    jdoe, OneTimeAuthTokenPurpose.EMAIL_VERIFICATION);
            });

        assertThat(result, is(not(nullValue())));
        assertThat(result, is(not(empty())));

        final OneTimeAuthToken token = result.get(0);
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
        final User mmuster = userRepository.findByName("mmuster").get();

        final List<OneTimeAuthToken> result = shiro.getSystemUser().execute(
            () -> {
                return oneTimeAuthManager.retrieveForUser(
                    mmuster, OneTimeAuthTokenPurpose.EMAIL_VERIFICATION);
            });

        assertThat(result, is(empty()));
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(600)
    public void retrieveTokenNullUser() throws Throwable {
        try {
            shiro.getSystemUser().execute(
                () -> oneTimeAuthManager.retrieveForUser(
                    null, OneTimeAuthTokenPurpose.RECOVER_PASSWORD));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(700)
    public void retrieveTokenNullPurpose() throws Throwable {
        final User mmuster = userRepository.findByName("mmuster").get();

        try {
            shiro.getSystemUser().execute(
                () -> oneTimeAuthManager.retrieveForUser(mmuster, null));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @InSequence(800)
    public void validTokenExistsForUser() {
        final User user = userRepository.findByName("jdoe").get();

        shiro.getSystemUser().execute(
            () -> {
                assertThat(
                    oneTimeAuthManager.validTokenExistsForUser(
                        user, OneTimeAuthTokenPurpose.EMAIL_VERIFICATION),
                    is(true));
            });
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @InSequence(900)
    public void validTokenDoesNotExist() {
        final User user = userRepository.findByName("mmuster").get();

        shiro.getSystemUser().execute(
            () -> {
                assertThat(
                    oneTimeAuthManager.validTokenExistsForUser(
                        user, OneTimeAuthTokenPurpose.EMAIL_VERIFICATION),
                    is(false));
            });
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(1000)
    public void validTokenNullUser() throws Throwable {
        try {
            shiro.getSystemUser().execute(
                () -> oneTimeAuthManager.validTokenExistsForUser(
                    null, OneTimeAuthTokenPurpose.EMAIL_VERIFICATION));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(1100)
    public void validTokenNullPurpose() throws Throwable {
        try {
            final User user = userRepository.findByName("mmuster").get();
            shiro.getSystemUser().execute(
                () -> oneTimeAuthManager.validTokenExistsForUser(user, null));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @InSequence(1200)
    public void isValid() {
        final User jdoe = userRepository.findByName("jdoe").get();

        final List<OneTimeAuthToken> result = shiro.getSystemUser().execute(
            () -> {
                return oneTimeAuthManager.retrieveForUser(
                    jdoe, OneTimeAuthTokenPurpose.EMAIL_VERIFICATION);
            });
        assertThat(result, is(not(empty())));
        shiro.getSystemUser().execute(
            () -> {
                assertThat(oneTimeAuthManager.isValid(result.get(0)),
                           is(true));
            });
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @InSequence(1300)
    public void isInvalid() {
        final User jdoe = userRepository.findByName("jdoe").get();

        final List<OneTimeAuthToken> result = shiro.getSystemUser().execute(
            () -> {
                return oneTimeAuthManager.retrieveForUser(
                    jdoe, OneTimeAuthTokenPurpose.EMAIL_VERIFICATION);
            });

        assertThat(result, is(not(empty())));
        final OneTimeAuthToken token = result.get(0);

        final LocalDateTime date = LocalDateTime
            .now(ZoneOffset.UTC).minus(1800, ChronoUnit.SECONDS);
        token.setValidUntil(Date.from(date.toInstant(ZoneOffset.UTC)));

        shiro.getSystemUser().execute(
            () -> {
                assertThat(oneTimeAuthManager.isValid(token), is(false));
            });

    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(1400)
    public void isValidNullToken() throws Throwable {
        try {
            shiro.getSystemUser().execute(
                () -> oneTimeAuthManager.isValid(null));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/OneTimeAuthManagerTest/"
                    + "after-invalidate.xml")
    @InSequence(1500)
    public void invalidateToken() {
        final User jdoe = userRepository.findByName("jdoe").get();

        final List<OneTimeAuthToken> result = shiro.getSystemUser().execute(
            () -> {
                return oneTimeAuthManager.retrieveForUser(
                    jdoe, OneTimeAuthTokenPurpose.EMAIL_VERIFICATION);
            });

        assertThat(result, is(not(empty())));
        shiro.getSystemUser().execute(
            () -> oneTimeAuthManager.invalidate(result.get(0)));
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(1400)
    public void invalidateNullToken() {
        shiro.getSystemUser().execute(
            () -> oneTimeAuthManager.invalidate(null));
    }

}
