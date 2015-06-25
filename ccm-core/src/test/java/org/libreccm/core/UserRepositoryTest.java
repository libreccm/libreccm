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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.tests.categories.IntegrationTest;

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

    @Test
    @UsingDataSet(
            "datasets/org/libreccm/core/UserRepositoryTest/data.json")
    @InSequence(10)
    public void findUserByScreenName() {
        final User jdoe = userRepository.findByScreenName(JDOE);
        final User mmuster = userRepository.findByScreenName(MMUSTER);
        final User joe = userRepository.findByScreenName(JOE);
        final User nobody = userRepository.findByScreenName(NOBODY);

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
                   "axg8ira8fa")));
        assertThat(joe.getSalt(), is(equalTo("fjiajhigafgapoa")));

        assertThat(nobody, is(nullValue()));
    }

    @Test
    @UsingDataSet(
            "datasets/org/libreccm/core/UserRepositoryTest/data.json")
    @InSequence(20)
    public void findUserByEmail() {
        final User jdoe = userRepository.findByEmailAddress("john.doe@example.com");
        final User mmuster1 = userRepository.findByEmailAddress("max.mustermann@example.org");
        final User mmuster2 = userRepository.findByEmailAddress("mm@example.com");
        final User joe = userRepository.findByEmailAddress("joe.public@example.com");
        final User nobody = userRepository.findByScreenName("nobody@example.org");

        assertThat(jdoe, is(not(nullValue())));
        assertThat(jdoe.getSubjectId(), is(-10L));
        assertThat(jdoe.getScreenName(), is(JDOE));
        assertThat(jdoe.getName().getFamilyName(), is(equalTo("Doe")));
        assertThat(jdoe.getName().getMiddleName(), is(nullValue()));
        assertThat(jdoe.getName().getGivenName(), is(equalTo("John")));
        assertThat(jdoe.getHashAlgorithm(), is("MD5"));
        assertThat(jdoe.getPassword(), is("604622dc8a888eb093454ebd77ca1675"));
        assertThat(jdoe.getSalt(), is("axg8ira8fa"));

        assertThat(mmuster1, is(not(nullValue())));
        assertThat(mmuster1.getSubjectId(), is(-20L));
        assertThat(mmuster1.getScreenName(), is(equalTo(MMUSTER)));
        assertThat(mmuster1.getName().getFamilyName(), is(equalTo("Mustermann")));
        assertThat(mmuster1.getName().getMiddleName(), is(nullValue()));
        assertThat(mmuster1.getName().getGivenName(), is(equalTo("Max")));
        assertThat(mmuster1.getHashAlgorithm(), is(equalTo("SHA-512")));
        assertThat(mmuster1.getPassword(), is(equalTo(
                   "1c9626af429a6291766d15cbfb38689bd8d49450520765973de70aecaf644b7d4fda711266ba9ec8fb6df30c8ab391d40330829aa85adf371bcde6b4c9bc01e6")));
        assertThat(mmuster1.getSalt(), is(equalTo("fjiajhigafgapoa")));

        assertThat(mmuster2, is(equalTo(mmuster1)));
        
        assertThat(joe, is(not(nullValue())));
        assertThat(joe.getSubjectId(), is(-30L));
        assertThat(joe.getScreenName(), is(equalTo(JOE)));
        assertThat(joe.getName().getFamilyName(), is(equalTo("Public")));
        assertThat(joe.getName().getMiddleName(), is(nullValue()));
        assertThat(joe.getName().getGivenName(), is(equalTo("Joe")));
        assertThat(joe.getHashAlgorithm(), is(equalTo("SHA-512")));
        assertThat(joe.getPassword(), is(equalTo(
                   "axg8ira8fa")));
        assertThat(joe.getSalt(), is(equalTo("fjiajhigafgapoa")));

        assertThat(nobody, is(nullValue()));

    }

}
