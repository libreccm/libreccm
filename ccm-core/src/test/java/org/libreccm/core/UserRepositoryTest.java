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

import java.io.File;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;

import static org.hamcrest.Matchers.*;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.tests.categories.IntegrationTest;

import java.util.List;

import javax.persistence.Query;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
public class UserRepositoryTest {

    private static final String NOBODY = "nobody";
    private static final String JOE = "joe";
    private static final String MMUSTER = "mmuster";
    private static final String JDOE = "jdoe";

    @Inject
    private transient UserRepository userRepository;

    @PersistenceContext
    private transient EntityManager entityManager;

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
                    "LibreCCM-org.libreccm.core.UserRepositoryTest.war")
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
    public void repoIsInjected() {
        assertThat(userRepository, is(not(nullValue())));
    }

    @Test
    public void entityManagerIsInjected() {
        assertThat(entityManager, is(not(nullValue())));
    }

    private void checkUsers(final User jdoe,
                            final User mmuster,
                            final User joe,
                            final User nobody) {
        assertThat(jdoe, is(not(nullValue())));
        assertThat(jdoe.getSubjectId(), is(-10L));
        assertThat(jdoe.getScreenName(), is(JDOE));
        assertThat(jdoe.getName().getFamilyName(), is(equalTo("Doe")));
        assertThat(jdoe.getName().getMiddleName(), is(nullValue()));
        assertThat(jdoe.getName().getGivenName(), is(equalTo("John")));
        assertThat(jdoe.getHashAlgorithm(), is("MD5"));
        assertThat(jdoe.getPassword(), is("604622dc8a888eb093454ebd77ca1675"));
        assertThat(jdoe.getSalt(), is("axg8ira8fa"));

        assertThat(mmuster, is(not(nullValue())));
        assertThat(mmuster.getSubjectId(), is(-20L));
        assertThat(mmuster.getScreenName(), is(equalTo(MMUSTER)));
        assertThat(mmuster.getName().getFamilyName(), is(equalTo("Mustermann")));
        assertThat(mmuster.getName().getMiddleName(), is(nullValue()));
        assertThat(mmuster.getName().getGivenName(), is(equalTo("Max")));
        assertThat(mmuster.getHashAlgorithm(), is(equalTo("SHA-512")));
        assertThat(mmuster.getPassword(), is(equalTo(
                   "1c9626af429a6291766d15cbfb38689bd8d49450520765973de70aecaf644b7d4fda711266ba9ec8fb6df30c8ab391d40330829aa85adf371bcde6b4c9bc01e6")));
        assertThat(mmuster.getSalt(), is(equalTo("fjiajhigafgapoa")));

        assertThat(joe, is(not(nullValue())));
        assertThat(joe.getSubjectId(), is(-30L));
        assertThat(joe.getScreenName(), is(equalTo(JOE)));
        assertThat(joe.getName().getFamilyName(), is(equalTo("Public")));
        assertThat(joe.getName().getMiddleName(), is(nullValue()));
        assertThat(joe.getName().getGivenName(), is(equalTo("Joe")));
        assertThat(joe.getHashAlgorithm(), is(equalTo("SHA-512")));
        assertThat(joe.getPassword(), is(equalTo(
                   "4e39eba7f2927182a532cd8700bf251e58d4b0359fbb832e6af21db7501d7a49e6d8b950e0d4b15b1841af0f786c8edaa0c09ef7f474804254f7e895969d2975")));
        assertThat(joe.getSalt(), is(equalTo("axg8ira8fa")));

        assertThat(nobody, is(nullValue()));

    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/UserRepositoryTest/data.json")
    @InSequence(100)
    public void findUserById() {
        final User jdoe = userRepository.findById(-10L);
        final User mmuster = userRepository.findById(-20L);
        final User joe = userRepository.findById(-30L);
        final User nobody = userRepository.findById(-999L);

        checkUsers(jdoe, mmuster, joe, nobody);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/core/UserRepositoryTest/data.json")
    @InSequence(200)
    public void findUserByScreenName() {
        final User jdoe = userRepository.findByScreenName(JDOE);
        final User mmuster = userRepository.findByScreenName(MMUSTER);
        final User joe = userRepository.findByScreenName(JOE);
        final User nobody = userRepository.findByScreenName(NOBODY);

        checkUsers(jdoe, mmuster, joe, nobody);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/core/UserRepositoryTest/data.json")
    @InSequence(300)
    public void findUserByEmail() {
        final User jdoe = userRepository.findByEmailAddress(
            "john.doe@example.com");
        final User mmuster1 = userRepository.findByEmailAddress(
            "max.mustermann@example.org");
        final User mmuster2 = userRepository
            .findByEmailAddress("mm@example.com");
        final User joe = userRepository.findByEmailAddress(
            "joe.public@example.com");
        final User nobody = userRepository
            .findByEmailAddress("nobody@example.org");

        checkUsers(jdoe, mmuster1, joe, nobody);

        assertThat(mmuster2, is(equalTo(mmuster1)));
    }

    @Test(expected = MultipleMatchingUserException.class)
    @ShouldThrowException(MultipleMatchingUserException.class)
    @UsingDataSet(
        "datasets/org/libreccm/core/UserRepositoryTest/data-email-duplicate.json")
    @InSequence(350)
    public void findByEmailAddressDuplicate() {
        userRepository.findByEmailAddress("max.mustermann@example.org");
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/core/UserRepositoryTest/data.json")
    @InSequence(400)
    public void findAllUsers() {
        final List<User> users = userRepository.findAll();

        assertThat(users.size(), is(3));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/core/UserRepositoryTest/data.json")
    @ShouldMatchDataSet(value
                            = "datasets/org/libreccm/core/UserRepositoryTest/after-save-new.json",
                        excludeColumns = {"subject_id"})
    @InSequence(500)
    public void saveNewUser() {
        final User user = new User();

        final PersonName personName = new PersonName();
        personName.setGivenName("Jane");
        personName.setMiddleName("Anna");
        personName.setFamilyName("Doe");
        personName.setTitlePre("Dr.");
        user.setName(personName);

        final EmailAddress emailAddress = new EmailAddress();
        emailAddress.setAddress("jane.doe@example.org");
        emailAddress.setBouncing(false);
        emailAddress.setVerified(false);
        user.addEmailAddress(emailAddress);

        user.setScreenName("jane");
        user.setPassword(
            "32d2a830fb03f201bda975ae70a62c207716705a049e054cf6701de1cec546d3a9e03a094be2e98e4d125af996ebbfa5a7754754a1e9d2fe063a0d9921cb201d");
        user.setHashAlgorithm("SHA-512");
        user.setSalt("maifgaoapafga9");
        user.setPasswordResetRequired(false);

        userRepository.save(user);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/core/UserRepositoryTest/data.json")
    @ShouldMatchDataSet(value
                            = "datasets/org/libreccm/core/UserRepositoryTest/after-save-changed.json",
                        excludeColumns = {"subject_id"})
    @InSequence(600)
    public void saveChangedUser() {
        final User user = userRepository.findById(-10L);

        user.getName().setTitlePre("Dr.");

        user.setHashAlgorithm("SHA-512");
        user.setPassword(
            "19f69a0f8eab3e6124d1b40ca2ae1fc3ece311cf86dde4e9560521e881fb8f063817cf1da1234144825f40fc9b9acd1563cafcb35fb8533544a1b6c3615160e3");
        user.setSalt("fafjiaddfja0a");

        final EmailAddress emailAddress = new EmailAddress();
        emailAddress.setAddress("jd@example.com");
        emailAddress.setBouncing(false);
        emailAddress.setVerified(true);
        user.addEmailAddress(emailAddress);

        final EmailAddress old = user.getEmailAddresses().get(0);
        user.removeEmailAddress(old);

        userRepository.save(user);
    }

    @Test(expected = IllegalArgumentException.class)
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(700)
    public void saveNullValue() {
        userRepository.save(null);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/core/UserRepositoryTest/data.json")
    @ShouldMatchDataSet(value
                            = "datasets/org/libreccm/core/UserRepositoryTest/after-delete.json",
                        excludeColumns = {"subject_id"})
    @InSequence(800)
    public void deleteUser() {
        final User user = userRepository.findByScreenName("mmuster");

        userRepository.delete(user);
    }

    @Test(expected = IllegalArgumentException.class)
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(900)
    public void deleteNullValue() {
        userRepository.delete(null);
    }

}
