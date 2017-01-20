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


import javax.inject.Inject;
import javax.servlet.ServletContext;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.libreccm.testutils.DependenciesHelpers.*;

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

    @Inject
    private Shiro shiro;

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
        return ShrinkWrap
            .create(WebArchive.class,
                    "LibreCCM-org.libreccm.security.ChallengeManagerTest.war")
            .addClass(com.arsdigita.runtime.CCMResourceManager.class)
            .addPackage(com.arsdigita.util.Assert.class.getPackage())
            .addClass(com.arsdigita.util.servlet.HttpHost.class)
            .addPackage(com.arsdigita.web.URL.class.getPackage())
            .addPackage(org.libreccm.security.OneTimeAuthManager.class
                .getPackage())
            .addPackage(org.libreccm.core.CcmObject.class.getPackage())
            .addPackage(org.libreccm.categorization.Categorization.class
                .getPackage())
            .addPackage(org.libreccm.cdi.utils.CdiUtil.class.getPackage())
            .addPackage(
                org.libreccm.configuration.ConfigurationManager.class
                .getPackage())
            .addClass(com.arsdigita.kernel.KernelConfig.class)
            .addClass(com.arsdigita.kernel.security.SecurityConfig.class)
            .addPackage(org.libreccm.l10n.LocalizedString.class.getPackage())
            .addPackage(org.libreccm.web.CcmApplication.class.getPackage())
            .addPackage(org.libreccm.workflow.Workflow.class.getPackage())
            .addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.jpa.utils.MimeTypeConverter.class
                .getPackage())
            .addPackage(org.libreccm.testutils.EqualsVerifier.class
                .getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addPackage(org.libreccm.cdi.utils.CdiUtil.class.getPackage())
            .addClass(org.libreccm.portation.Portable.class)
            .addClass(com.arsdigita.kernel.KernelConfig.class)
            .addClass(com.arsdigita.kernel.security.SecurityConfig.class)
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
//        final String path = String.format("%s/%s/register/verify-email",
//                                          servletContext.getVirtualServerName(),
//                                          servletContext.getContextPath());
//        final String expected = String.format(
//            "Please follow the following link to finish the email verfication "
//                + "process:\n"
//                + "\n"
//                + "%s"
//                + "\n\n"
//                + "Please be aware that your verification token expires"
//                + "at",
//            path);

        final User user = userRepository.findByName("mmuster");
        final String mail = shiro.getSystemUser().execute(() -> {
            return challengeManager.createEmailVerification(user);
        });

        assertThat(mail, is(not(nullValue())));
        assertThat(mail.isEmpty(), is(false));
//        assertThat(
//            String
//            .format("Mail is expected to start with \"%s\" but is \"%s\".",
//                    expected,
//                    mail),
//            mail.startsWith(expected), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(1200)
    public void createEmailVerificationNullUser() throws Throwable {
        try {
            shiro.getSystemUser().execute(
                () -> challengeManager.createEmailVerification(null));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
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
        shiro.getSystemUser().execute(() -> {
            challengeManager.finishEmailVerification(
                user,
                "biXOpuxIPXuRgx9jhk1PzZVIeKGaTmg2qTKoTQ4tl9iiweQ0e5mfmdFI1KjDwjPi");
            return null;
        });
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
        throws Throwable {

        try {
            shiro.getSystemUser().execute(() -> {
                challengeManager.finishEmailVerification(
                    null,
                    "biXOpuxIPXuRgx9jhk1PzZVIeKGaTmg2qTKoTQ4tl9iiweQ0e5mfmdFI1KjDwjPi");
                return null;
            });
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
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
        throws Throwable {

        final User user = userRepository.findByName("mmuster");
        try {
            shiro.getSystemUser().execute(() -> {
                challengeManager.finishEmailVerification(user, null);
                return null;
            });
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/ChallengeManagerTest/"
                    + "after-create-account-activation.xml",
        excludeColumns = {"token_id", "token", "valid_until"})
    @InSequence(2100)
    public void createAccountActivation() {
//        final String path = String.format("%s/%s/register/activate-account",
//                                          servletContext.getVirtualServerName(),
//                                          servletContext.getContextPath());
//        final String expected = String.format(
//            "Please follow the following link to enable your new account:\n"
//                + "\n"
//                + "%s"
//                + "\n\n"
//                + "Please be aware that you must activate your account before",
//            path);

        final User user = userRepository.findByName("mmuster");
        final String mail = shiro.getSystemUser().execute(() -> {
            return challengeManager.createAccountActivation(user);
        });

        assertThat(mail, is(not(nullValue())));
        assertThat(mail.isEmpty(), is(false));
//        assertThat(mail.startsWith(expected), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(2200)
    public void createAccountActivationNullUser() throws Throwable {
        try {
            shiro.getSystemUser().execute(
                () -> challengeManager.createAccountActivation(null));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/"
                      + "finish-account-activation.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/ChallengeManagerTest/"
                    + "after-finish-account-activation.xml")
    @InSequence(2300)
    public void finishAccountActivation() throws Throwable {
        final User user = userRepository.findByName("mmuster");
        try {
            shiro.getSystemUser().execute(() -> {
                challengeManager.finishAccountActivation(
                    user,
                    "biXOpuxIPXuRgx9jhk1PzZVIeKGaTmg2qTKoTQ4tl9iiweQ0e5mfmdFI1KjDwjPi");
                return null;
            });
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/"
                      + "finish-account-activation.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/ChallengeManagerTest/"
                    + "finish-account-activation.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(2400)
    public void finishAccountActivationNullUser() throws Throwable {
        try {
            shiro.getSystemUser().execute(() -> {
                challengeManager.finishAccountActivation(
                    null,
                    "biXOpuxIPXuRgx9jhk1PzZVIeKGaTmg2qTKoTQ4tl9iiweQ0e5mfmdFI1KjDwjPi");
                return null;
            });
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/"
                      + "finish-account-activation.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/ChallengeManagerTest/"
                    + "finish-account-activation.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(2400)
    public void finishAccountActivationNullToken() throws Throwable {

        try {
            final User user = userRepository.findByName("mmuster");
            shiro.getSystemUser().execute(() -> {
                challengeManager.finishAccountActivation(user, null);
                return null;
            });
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/ChallengeManagerTest/"
                    + "after-create-password-recovery.xml",
        excludeColumns = {"token_id", "token", "valid_until"})
    @InSequence(3100)
    public void createPasswordRecover() {
//        final String path = String.format("%s/%s/register/recover-password",
//                                          servletContext.getVirtualServerName(),
//                                          servletContext.getContextPath());
//        final String expected = String.format(
//            "Please follow the following link to complete the password recover "
//                + "process:\n"
//                + "\n"
//                + "%s"
//                + "\n\n"
//                + "Please be aware that you must complete the process until",
//            path);

        final User user = userRepository.findByName("mmuster");
        final String mail = shiro.getSystemUser().execute(() -> {
            return challengeManager.createPasswordRecover(user);
        });

        assertThat(mail, is(not(nullValue())));
        assertThat(mail.isEmpty(), is(false));
//        assertThat(mail.startsWith(expected), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(3200)
    public void createPasswordRecoverNullUser() throws Throwable {
        try {
            shiro.getSystemUser().execute(() -> {
                challengeManager.createPasswordRecover(null);
                return null;
            });
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
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
        shiro.getSystemUser().execute(() -> {
            challengeManager.finishPasswordRecover(
                user,
                "biXOpuxIPXuRgx9jhk1PzZVIeKGaTmg2qTKoTQ4tl9iiweQ0e5mfmdFI1KjDwjPi",
                "new-password");
            return null;
        });

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
    public void finishPasswordRecoverNullUser() throws Throwable {
        try {
            shiro.getSystemUser().execute(() -> {
                challengeManager.finishPasswordRecover(
                    null,
                    "biXOpuxIPXuRgx9jhk1PzZVIeKGaTmg2qTKoTQ4tl9iiweQ0e5mfmdFI1KjDwjPi",
                    "new-password");
                return null;
            });
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/"
                      + "finish-password-recovery.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/ChallengeManagerTest/"
                    + "finish-password-recovery.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(3400)
    public void finishPasswordRecoverNullToken() throws Throwable {

        final User user = userRepository.findByName("mmuster");
        try {
            shiro.getSystemUser().execute(() -> {
                challengeManager.finishPasswordRecover(
                    user, null, "new-password");
                return null;
            });
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/"
                      + "finish-password-recovery.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/ChallengeManagerTest/"
                    + "finish-password-recovery.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(3500)
    public void finishPasswordRecoverNullPassword() throws Throwable {
        final User user = userRepository.findByName("mmuster");

        try {
            shiro.getSystemUser().execute(() -> {
                challengeManager.finishPasswordRecover(
                    user,
                    "biXOpuxIPXuRgx9jhk1PzZVIeKGaTmg2qTKoTQ4tl9iiweQ0e5mfmdFI1KjDwjPi",
                    null);
                return null;
            });
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/ChallengeManagerTest/"
                      + "finish-password-recovery.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/ChallengeManagerTest/"
                    + "finish-password-recovery.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(3600)
    public void finishPasswordRecoverEmptyPassword() throws Throwable {
        final User user = userRepository.findByName("mmuster");
        try {
            shiro.getSystemUser().execute(() -> {
                challengeManager.finishPasswordRecover(
                    user,
                    "biXOpuxIPXuRgx9jhk1PzZVIeKGaTmg2qTKoTQ4tl9iiweQ0e5mfmdFI1KjDwjPi",
                    "");
                return null;
            });
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

}
