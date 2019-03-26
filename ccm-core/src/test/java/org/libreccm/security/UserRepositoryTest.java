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

import org.junit.Test;

import static org.hamcrest.Matchers.*;

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

import static org.junit.Assert.*;

import static org.libreccm.testutils.DependenciesHelpers.*;

import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.core.EmailAddress;
import org.libreccm.tests.categories.IntegrationTest;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.arquillian.persistence.TestExecutionPhase;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_core_schema.sql"})
@CleanupUsingScript(value = {"cleanup.sql"},
                    phase = TestExecutionPhase.BEFORE)
public class UserRepositoryTest {

    private static final String NOBODY = "nobody";
    private static final String JOE = "joe";
    private static final String MMUSTER = "mmuster";
    private static final String JDOE = "jdoe";

    @Inject
    private UserRepository userRepository;

    @Inject
    private Shiro shiro;

    @PersistenceContext
    private EntityManager entityManager;

    public UserRepositoryTest() {
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
                    "LibreCCM-org.libreccm.security.UserRepositoryTest.war")
            .addPackage(org.libreccm.security.User.class.getPackage())
            .addPackage(org.libreccm.core.CcmObject.class.getPackage())
            .addPackage(org.libreccm.categorization.Categorization.class
                .getPackage())
            .addPackage(org.libreccm.configuration.Configuration.class
                .getPackage())
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
            .addClass(com.arsdigita.kernel.security.SecurityConfig.class)
            .addClass(com.arsdigita.kernel.KernelConfig.class)
            .addPackage(org.libreccm.cdi.utils.CdiUtil.class.getPackage())
            .addClass(org.libreccm.imexport.Exportable.class)
            .addAsLibraries(getModuleDependencies())
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsWebInfResource("META-INF/beans.xml", "beans.xml");
    }

    @Test
    public void repoIsInjected() {
        assertThat(userRepository, is(not(nullValue())));
    }

    @Test
    public void entityManagerIsInjected() {
        assertThat(entityManager, is(not(nullValue())));
    }

    private void checkUsers(final Optional<User> jdoe,
                            final Optional<User> mmuster,
                            final Optional<User> joe,
                            final Optional<User> nobody) {
        assertThat(jdoe.isPresent(), is(true));
        assertThat(jdoe.get().getPartyId(), is(-10L));
        assertThat(jdoe.get().getName(), is(equalTo(JDOE)));
        assertThat(jdoe.get().getFamilyName(), is(equalTo("Doe")));
        assertThat(jdoe.get().getGivenName(), is(equalTo("John")));
        assertThat(jdoe.get().getPassword(),
                   is(equalTo(
                       "$shiro1$SHA-512$500000$7xkDcZUN0/whJInHIvGsDw==$WhelBVmJU/cLV7lAkMOrE5B/mqCW0bUuid1WX+xBwzzAaekC5bYn9eeOFGJWhiDgmaC50ZCUmM96/iGsRoc4uA==")));

        assertThat(mmuster.isPresent(), is(true));
        assertThat(mmuster.get().getPartyId(), is(-20L));
        assertThat(mmuster.get().getName(), is(equalTo(MMUSTER)));
        assertThat(mmuster.get().getFamilyName(), is(equalTo("Mustermann")));
        assertThat(mmuster.get().getGivenName(), is(equalTo("Max")));
        assertThat(mmuster.get().getPassword(),
                   is(equalTo(
                       "$shiro1$SHA-512$500000$Y7CnccN1h25sR7KCElMOXg==$CVLWBhetodaEzzhDfGjRcCFZtSW02xOnjH7xhBx0lbxO66grKIt6LWmXoUhLEydce1JZ7cbzNLYOxIwwTeqi5Q==")));

        assertThat(joe.isPresent(), is(true));
        assertThat(joe.get().getPartyId(), is(-30L));
        assertThat(joe.get().getName(), is(equalTo(JOE)));
        assertThat(joe.get().getFamilyName(), is(equalTo("Public")));
        assertThat(joe.get().getGivenName(), is(equalTo("Joe")));
        assertThat(joe.get().getPassword(),
                   is(equalTo(
                       "$shiro1$SHA-512$500000$RUCYXAQt+XzUmj3x8oG5gw==$qU+lX160Jc6sNUOI9X85wlf2lzn4/hLJNURtjmw9LOYJ7vAqUFFmhyNCMxpzuHIpzeMELr+A0XReoSmtcZnOOw==")));

        assertThat(nobody.isPresent(), is(false));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/UserRepositoryTest/data.yml")
    @InSequence(100)
    public void findUserById() {
        final Optional<User> jdoe = userRepository.findById(-10L);
        final Optional<User> mmuster = userRepository.findById(-20L);
        final Optional<User> joe = userRepository.findById(-30L);
        final Optional<User> nobody = userRepository.findById(-999L);

        checkUsers(jdoe, mmuster, joe, nobody);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/UserRepositoryTest/data.yml")
    @InSequence(200)
    public void findUserByScreenName() {
        final Optional<User> jdoe = userRepository.findByName(JDOE);
        final Optional<User> mmuster = userRepository.findByName(MMUSTER);
        final Optional<User> joe = userRepository.findByName(JOE);
        final Optional<User> nobody = userRepository.findByName(NOBODY);

        checkUsers(jdoe, mmuster, joe, nobody);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/UserRepositoryTest/data.yml")
    @InSequence(300)
    public void findUserByEmail() {
        final Optional<User> jdoe = userRepository.findByEmailAddress(
            "john.doe@example.com");
        final Optional<User> mmuster1 = userRepository.findByEmailAddress(
            "max.mustermann@example.org");
        final Optional<User> joe = userRepository.findByEmailAddress(
            "joe.public@example.com");
        final Optional<User> nobody = userRepository
            .findByEmailAddress("nobody@example.org");

        checkUsers(jdoe, mmuster1, joe, nobody);
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/UserRepositoryTest/data-email-duplicate.yml")
    @InSequence(350)
    public void findByEmailAddressDuplicate() {
        final User user = userRepository.findByEmailAddress(
            "max.mustermann@example.org").get();

        assertThat(user.getPartyId(), is(-30L));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/UserRepositoryTest/data.yml")
    @InSequence(400)
    public void findAllUsers() {
        final List<User> users = userRepository.findAll();

        assertThat(users.size(), is(3));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/UserRepositoryTest/data.yml")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/security/"
                                    + "UserRepositoryTest/after-save-new.yml",
                        excludeColumns = {"party_id"}
    )
    @InSequence(500)
    public void saveNewUser() {
        final User user = new User();

        final EmailAddress emailAddress = new EmailAddress();
        emailAddress.setAddress("jane.doe@example.org");
        emailAddress.setBouncing(false);
        emailAddress.setVerified(false);

        user.setName("jane");
        user.setGivenName("Jane");
        user.setFamilyName("Doe");
        user.setPrimaryEmailAddress(emailAddress);
        user.setPassword(
            "$shiro1$SHA-512$500000$24lA090z7GKYr4VFlZ6t4A==$/heoTHPA5huT1UfJ8Q+waXEG6AjUKhFYLFrj7KW/l0/z9O+QkiZTtfPfbcPblgjcEvrROMEIoQY4Z65S7rFLQg==");
        user.setPasswordResetRequired(false);

        shiro.getSystemUser().execute(() -> userRepository.save(user));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/UserRepositoryTest/data.yml")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/security/"
                                    + "UserRepositoryTest/after-save-changed.yml",
                        excludeColumns = {"party_id"})
    @InSequence(600)
    public void saveChangedUser() {
        final User user = userRepository.findById(-10L).get();

        //foo456
        user.setPassword(
            "$shiro1$SHA-512$500000$AH1llRaMHE8W31Q7VG6jsA==$XXgKeyDCsrN23NvszQ5wt+uViQUlVqTAM+05LrE7Bd9sc0eaJT8HlAGvSdY+rqTLbiGm9YS4pohzoUt1x3kmKg==");

        final EmailAddress emailAddress = new EmailAddress();
        emailAddress.setAddress("jd@example.com");
        emailAddress.setBouncing(false);
        emailAddress.setVerified(true);
        user.setPrimaryEmailAddress(emailAddress);

        shiro.getSystemUser().execute(() -> userRepository.save(user));
    }

    @Test(expected = NullPointerException.class)
    @ShouldThrowException(NullPointerException.class)
    @InSequence(700)
    public void saveNullValue() {
        shiro.getSystemUser().execute(() -> userRepository.save(null));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/UserRepositoryTest/data.yml")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/security/"
                                    + "UserRepositoryTest/after-delete.yml",
                        excludeColumns = {"party_id"})
    @InSequence(800)
    public void deleteUser() {
        final User user = userRepository.findByName("mmuster").get();

        shiro.getSystemUser().execute(() -> userRepository.delete(user));
    }

    @Test(expected = IllegalArgumentException.class)
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(900)
    public void deleteNullValue() {
        shiro.getSystemUser().execute(() -> userRepository.delete(null));
    }

}
