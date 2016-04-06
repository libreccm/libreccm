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
import org.libreccm.tests.categories.IntegrationTest;

import java.io.File;

import javax.inject.Inject;
import javax.servlet.ServletContext;

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
public class ChallengeManagerTest {

    @Inject
    private ChallengeManager challengeManager;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserManager userManager;

    @Inject
    private ServletContext servletContext;

    public ChallengeManagerTest() {
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
                    "LibreCCM-org.libreccm.security.ChallengeManagerTest.war")
            .addPackage(org.libreccm.security.OneTimeAuthManager.class.
                getPackage())
            .addPackage(org.libreccm.core.CcmObject.class.getPackage())
            .addPackage(org.libreccm.categorization.Categorization.class.
                getPackage())
            .addPackage(org.libreccm.cdi.utils.CdiUtil.class.getPackage())
            .addPackage(
                org.libreccm.configuration.ConfigurationManager.class.
                getPackage())
            .addClass(com.arsdigita.kernel.KernelConfig.class)
            .addClass(com.arsdigita.kernel.security.SecurityConfig.class)
            .addPackage(org.libreccm.l10n.LocalizedString.class.getPackage())
            .addPackage(org.libreccm.web.CcmApplication.class.getPackage())
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
        assertThat(challengeManager, is(not(nullValue())));
    }

    @Test
    @InSequence(20)
    public void isServletContextInjected() {
        assertThat(servletContext, is(not(nullValue())));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/ChallengeManagerTest/"
                    + "after-create-email-verification.xml",
        excludeColumns = {"token_id", "token", "valid_until"})
    @InSequence(1100)
    public void createEmailVerification() {
        final String path = String.format("%s/%s/register/verify-email",
                                          servletContext.getVirtualServerName(),
                                          servletContext.getContextPath());
        final String expected = String.format(
            "Please follow the following link to finish the email verfication "
                + "process:\n"
                + "\n"
                + "%s"
                + "\n\n"
                + "Please be aware that your verification token expires"
                + "at",
            path);

        final User user = userRepository.findByName("mmuster");
        final String mail = challengeManager.createEmailVerification(user);

        assertThat(mail, is(not(nullValue())));
        assertThat(mail.isEmpty(), is(false));
        assertThat(mail.startsWith(expected), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(1200)
    public void createEmailVerificationNullUser() {
        challengeManager.createEmailVerification(null);
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/ChallengeManagerTest/finish-email-verification.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/ChallengeManagerTest/"
                    + "after-finish-email-verification.xml")
    @InSequence(1300)
    public void finishEmailVerification() throws ChallengeFailedException {
        final User user = userRepository.findByName("mmuster");
        challengeManager.finishEmailVerification(
            user,
            "biXOpuxIPXuRgx9jhk1PzZVIeKGaTmg2qTKoTQ4tl9iiweQ0e5mfmdFI1KjDwjPi");
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/ChallengeManagerTest/finish-email-verification.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/ChallengeManagerTest/"
                    + "data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(1400)
    public void finishEmailVerificationNullUser()
        throws ChallengeFailedException {

        challengeManager.finishEmailVerification(
            null,
            "biXOpuxIPXuRgx9jhk1PzZVIeKGaTmg2qTKoTQ4tl9iiweQ0e5mfmdFI1KjDwjPi");
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/ChallengeManagerTest/finish-email-verification.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/ChallengeManagerTest/"
                    + "data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(1500)
    public void finishEmailVerificationNullToken()
        throws ChallengeFailedException {

        final User user = userRepository.findByName("mmuster");
        challengeManager.finishEmailVerification(
            user, null);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/ChallengeManagerTest/"
                    + "after-create-account-activation.xml",
        excludeColumns = {"token_id", "token", "valid_until"})
    @InSequence(2100)
    public void createAccountActivation() {
        final String path = String.format("%s/%s/register/activate-account",
                                          servletContext.getVirtualServerName(),
                                          servletContext.getContextPath());
        final String expected = String.format(
            "Please follow the following link to enable your new account:\n"
                + "\n"
                + "%s"
                + "\n\n"
                + "Please be aware that you must activate your account before",
            path);

        final User user = userRepository.findByName("mmuster");
        final String mail = challengeManager.createAccountActivation(user);

        assertThat(mail, is(not(nullValue())));
        assertThat(mail.isEmpty(), is(false));
        assertThat(mail.startsWith(expected), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(2200)
    public void createAccountActivationNullUser() {
        challengeManager.createAccountActivation(null);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/"
                      + "finish-account-activation.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/ChallengeManagerTest/"
                    + "after-finish-account-activation.xml")
    @InSequence(2300)
    public void finishAccountActivation() throws ChallengeFailedException {
        final User user = userRepository.findByName("mmuster");
        challengeManager.finishAccountActivation(
            user,
            "biXOpuxIPXuRgx9jhk1PzZVIeKGaTmg2qTKoTQ4tl9iiweQ0e5mfmdFI1KjDwjPi");
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/"
                      + "finish-account-activation.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/ChallengeManagerTest/"
                    + "finish-account-activation.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(2400)
    public void finishAccountActivationNullUser() throws
        ChallengeFailedException {
        challengeManager.finishAccountActivation(
            null,
            "biXOpuxIPXuRgx9jhk1PzZVIeKGaTmg2qTKoTQ4tl9iiweQ0e5mfmdFI1KjDwjPi");
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/"
                      + "finish-account-activation.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/ChallengeManagerTest/"
                    + "finish-account-activation.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(2400)
    public void finishAccountActivationNullToken() throws
        ChallengeFailedException {

        final User user = userRepository.findByName("mmuster");
        challengeManager.finishAccountActivation(
            user, null);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/ChallengeManagerTest/"
                    + "after-create-password-recovery.xml",
        excludeColumns = {"token_id", "token", "valid_until"})
    @InSequence(3100)
    public void createPasswordRecover() {
        final String path = String.format("%s/%s/register/recover-password",
                                          servletContext.getVirtualServerName(),
                                          servletContext.getContextPath());
        final String expected = String.format(
            "Please follow the following link to complete the password recover "
                + "process:\n"
                + "\n"
                + "%s"
                + "\n\n"
                + "Please be aware that you must complete the process until",
            path);

        final User user = userRepository.findByName("mmuster");
        final String mail = challengeManager.createPasswordRecover(user);

        assertThat(mail, is(not(nullValue())));
        assertThat(mail.isEmpty(), is(false));
        assertThat(mail.startsWith(expected), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(3200)
    public void createPasswordRecoverNullUser() {
        challengeManager.createPasswordRecover(null);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/"
                      + "finish-password-recovery.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/ChallengeManagerTest/"
                    + "after-finish-password-recovery.xml",
        excludeColumns = "password")
    @InSequence(3300)
    public void finishPasswordRecover() throws ChallengeFailedException {
        final User user = userRepository.findByName("mmuster");
        challengeManager.finishPasswordRecover(
            user,
            "biXOpuxIPXuRgx9jhk1PzZVIeKGaTmg2qTKoTQ4tl9iiweQ0e5mfmdFI1KjDwjPi",
            "new-password");

        final User after = userRepository.findByName("mmuster");
        assertThat(userManager.verifyPassword(after, "new-password"), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/"
                      + "finish-password-recovery.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/ChallengeManagerTest/"
                    + "finish-password-recovery.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(3400)
    public void finishPasswordRecoverNullUser() throws ChallengeFailedException {
        challengeManager.finishPasswordRecover(
            null,
            "biXOpuxIPXuRgx9jhk1PzZVIeKGaTmg2qTKoTQ4tl9iiweQ0e5mfmdFI1KjDwjPi",
            "new-password");
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/"
                      + "finish-password-recovery.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/ChallengeManagerTest/"
                    + "finish-password-recovery.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(3400)
    public void finishPasswordRecoverNullToken()
        throws ChallengeFailedException {
        final User user = userRepository.findByName("mmuster");
        challengeManager.finishPasswordRecover(
            user, null, "new-password");
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/"
                      + "finish-password-recovery.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/ChallengeManagerTest/"
                    + "finish-password-recovery.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(3500)
    public void finishPasswordRecoverNullPassword()
        throws ChallengeFailedException {
        final User user = userRepository.findByName("mmuster");
        challengeManager.finishPasswordRecover(
            user,
            "biXOpuxIPXuRgx9jhk1PzZVIeKGaTmg2qTKoTQ4tl9iiweQ0e5mfmdFI1KjDwjPi",
            null);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/"
                      + "finish-password-recovery.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/ChallengeManagerTest/"
                    + "finish-password-recovery.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(3600)
    public void finishPasswordRecoverEmptyPassword()
        throws ChallengeFailedException {
        final User user = userRepository.findByName("mmuster");
        challengeManager.finishPasswordRecover(
            user,
            "biXOpuxIPXuRgx9jhk1PzZVIeKGaTmg2qTKoTQ4tl9iiweQ0e5mfmdFI1KjDwjPi",
            "");
    }

}
