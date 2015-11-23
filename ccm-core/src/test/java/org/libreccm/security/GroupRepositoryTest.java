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

import java.io.File;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
import org.libreccm.categorization.Categorization;
import org.libreccm.core.CcmObject;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.tests.categories.IntegrationTest;
import org.libreccm.web.CcmApplication;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema("create_ccm_core_schema.sql")
public class GroupRepositoryTest {

    private static final String ADMINS = "admins";
    private static final String USERS = "users";
    private static final String EDITORS = "editors";
    private static final String NONE = "none";

    @Inject
    private transient GroupRepository groupRepository;

    @PersistenceContext
    private transient EntityManager entityManager;

    public GroupRepositoryTest() {

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
                        "LibreCCM-org.libreccm.security.UserRepositoryTest.war")
                .addPackage(User.class.getPackage())
                .addPackage(CcmObject.class.getPackage())
                .addPackage(Categorization.class.getPackage())
                .addPackage(LocalizedString.class.getPackage())
                .addPackage(CcmApplication.class.getPackage())
                .addPackage(org.libreccm.jpa.EntityManagerProducer.class
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
    public void repoIsInjected() {
        assertThat(groupRepository, is(not(nullValue())));
    }

    @Test
    @InSequence(20)
    public void entityManagerIsInjected() {
        assertThat(entityManager, is(not(nullValue())));
    }

    private void checkGroups(final Group admins,
                             final Group users,
                             final Group editors,
                             final Group none) {
        assertThat(admins, is(not(nullValue())));
        assertThat(admins.getPartyId(), is(-10L));
        assertThat(admins.getName(), is(equalTo(ADMINS)));

        assertThat(users, is(not(nullValue())));
        assertThat(users.getPartyId(), is(-20L));
        assertThat(users.getName(), is(equalTo(USERS)));

        assertThat(editors, is(not(nullValue())));
        assertThat(editors.getPartyId(), is(-30L));
        assertThat(editors.getName(), is(equalTo(EDITORS)));

        assertThat(none, is(nullValue()));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/GroupRepositoryTest/data.yml")
    @InSequence(100)
    public void findGroupById() {
        final Group admins = groupRepository.findById(-10L);
        final Group users = groupRepository.findById(-20L);
        final Group editors = groupRepository.findById(-30L);
        final Group none = groupRepository.findById(-999L);

        checkGroups(admins, users, editors, none);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/GroupRepositoryTest/data.yml")
    @InSequence(200)
    public void findGroupByName() {
        final Group admins = groupRepository.findByName(ADMINS);
        final Group users = groupRepository.findByName(USERS);
        final Group editors = groupRepository.findByName(EDITORS);
        final Group none = groupRepository.findByName(NONE);

        checkGroups(admins, users, editors, none);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/GroupRepositoryTest/data.yml")
    @InSequence(300)
    public void findAllGroups() {
        final List<Group> groups = groupRepository.findAll();

        assertThat(groups.size(), is(3));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/GroupRepositoryTest/data.yml")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/security/"
                                        + "GroupRepositoryTest/after-save-new.yml",
                        excludeColumns = {"party_id"})
    @InSequence(400)
    public void saveNewGroup() {
        final Group authors = new Group();
        authors.setName("authors");

        groupRepository.save(authors);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/GroupRepositoryTest/data.yml")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/security/"
                                        + "GroupRepositoryTest/after-save-changed.yml",
                        excludeColumns = {"party_id"})
    @InSequence(500)
    public void saveChangedGroup() {
        final Group group = groupRepository.findById(-30L);
        group.setName("authors");

        groupRepository.save(group);
    }

    @Test(expected = IllegalArgumentException.class)
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(600)
    public void saveNullValue() {
        groupRepository.save(null);
    }
    
    @Test
    @UsingDataSet("datasets/org/libreccm/security/GroupRepositoryTest/data.yml")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/security/"
                                        + "GroupRepositoryTest/after-delete.yml",
                        excludeColumns = {"party_id"})
    @InSequence(700)
    public void deleteUser() {
        final Group group = groupRepository.findByName(USERS);
        
        groupRepository.delete(group);
    }
    
    
    @Test(expected = IllegalArgumentException.class)
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(800)
    public void deleteNullValue() {
        groupRepository.delete(null);
    }

}
