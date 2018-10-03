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
import org.junit.After;
import org.junit.AfterClass;

import static org.junit.Assert.*;

import static org.libreccm.testutils.DependenciesHelpers.*;

import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.tests.categories.IntegrationTest;

import java.util.Optional;
import org.jboss.arquillian.persistence.TestExecutionPhase;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema("create_ccm_core_schema.sql")
@CleanupUsingScript(value = {"cleanup.sql"},
                    phase = TestExecutionPhase.BEFORE)
public class GroupRepositoryTest {

    private static final String ADMINS = "admins";
    private static final String USERS = "users";
    private static final String EDITORS = "editors";
    private static final String NONE = "none";

    @Inject
    private GroupRepository groupRepository;

    @PersistenceContext
    private EntityManager entityManager;

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
        return ShrinkWrap
            .create(WebArchive.class,
                    "LibreCCM-org.libreccm.security.UserRepositoryTest.war")
            .addPackage(org.libreccm.security.User.class.getPackage())
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
            .addPackage(org.libreccm.testutils.EqualsVerifier.class
                .getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addAsLibraries(getModuleDependencies())
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
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

    private void checkGroups(final Optional<Group> admins,
                             final Optional<Group> users,
                             final Optional<Group> editors,
                             final Optional<Group> none) {
        assertThat(admins.isPresent(), is(true));
        assertThat(admins.get().getPartyId(), is(-10L));
        assertThat(admins.get().getName(), is(equalTo(ADMINS)));

        assertThat(users.isPresent(), is(true));
        assertThat(users.get().getPartyId(), is(-20L));
        assertThat(users.get().getName(), is(equalTo(USERS)));

        assertThat(editors.isPresent(), is(true));
        assertThat(editors.get().getPartyId(), is(-30L));
        assertThat(editors.get().getName(), is(equalTo(EDITORS)));

        assertThat(none.isPresent(), is(false));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/GroupRepositoryTest/data.yml")
    @InSequence(100)
    public void findGroupById() {
        final Optional<Group> admins = groupRepository.findById(-10L);
        final Optional<Group> users = groupRepository.findById(-20L);
        final Optional<Group> editors = groupRepository.findById(-30L);
        final Optional<Group> none = groupRepository.findById(-999L);

        checkGroups(admins, users, editors, none);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/GroupRepositoryTest/data.yml")
    @InSequence(200)
    public void findGroupByName() {
        final Optional<Group> admins = groupRepository.findByName(ADMINS);
        final Optional<Group> users = groupRepository.findByName(USERS);
        final Optional<Group> editors = groupRepository.findByName(EDITORS);
        final Optional<Group> none = groupRepository.findByName(NONE);

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
        final Group group = groupRepository.findById(-30L).get();
        group.setName("authors");

        groupRepository.save(group);
    }

    @Test(expected = NullPointerException.class)
    @ShouldThrowException(NullPointerException.class)
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
        final Group group = groupRepository.findByName(USERS).get();

        groupRepository.delete(group);
    }

    @Test(expected = IllegalArgumentException.class)
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(800)
    public void deleteNullValue() {
        groupRepository.delete(null);
    }

}
