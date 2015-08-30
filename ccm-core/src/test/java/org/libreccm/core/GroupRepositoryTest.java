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
import org.libreccm.tests.categories.IntegrationTest;

import java.io.File;

import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_core_schema.sql"})
public class GroupRepositoryTest {

    @Inject
    private transient GroupRepository groupRepository;

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

    private void verifyGroups(final Group admins,
                              final Group users,
                              final Group authors,
                              final Group none) {
        assertThat(admins, is(not(nullValue())));
        assertThat(admins.getName(), is(equalTo("admins")));

        assertThat(users, is(not(nullValue())));
        assertThat(users.getName(), is(equalTo("users")));

        assertThat(authors, is(not(nullValue())));
        assertThat(authors.getName(), is(equalTo("authors")));

        assertThat(none, is(nullValue()));

    }

    @Test
    @InSequence
    @UsingDataSet("datasets/org/libreccm/core/GroupRepositoryTest/data.yml")
    public void findGroupById() {
        final Group admins = groupRepository.findById(-10L);
        final Group users = groupRepository.findById(-20L);
        final Group authors = groupRepository.findById(-30L);
        final Group none = groupRepository.findById(-999L);

        verifyGroups(admins, users, authors, none);
    }

    @Test
    @InSequence(20)
    @UsingDataSet("datasets/org/libreccm/core/GroupRepositoryTest/data.yml")
    public void findGroupByName() {
        final Group admins = groupRepository.findByGroupName("admins");
        final Group users = groupRepository.findByGroupName("users");
        final Group authors = groupRepository.findByGroupName("authors");
        final Group none = groupRepository.findByGroupName("none");

        verifyGroups(admins, users, authors, none);
    }

    @Test
    @InSequence(30)
    @UsingDataSet("datasets/org/libreccm/core/GroupRepositoryTest/data.yml")
    @ShouldMatchDataSet(value
                        = "datasets/org/libreccm/core/GroupRepositoryTest/after-save-new.yml",
                        excludeColumns = "subject_id")
    public void saveNewGroup() {
        final Group publishers = new Group();
        publishers.setName("publishers");

        groupRepository.save(publishers);
    }

    @Test
    @InSequence(40)
    @UsingDataSet("datasets/org/libreccm/core/GroupRepositoryTest/data.yml")
    @ShouldMatchDataSet(value
                            = "datasets/org/libreccm/core/GroupRepositoryTest/after-save-changed.yml",
                        excludeColumns = {"subject_id"})
    public void saveChangedGroup() {
        final Group group = groupRepository.findByGroupName("authors");
        group.setName("editors");

        groupRepository.save(group);
    }

    @Test(expected = IllegalArgumentException.class)
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(50)
    public void saveNullValue() {
        groupRepository.save(null);
    }
    
    @Test
    @InSequence(60)
    @UsingDataSet("datasets/org/libreccm/core/GroupRepositoryTest/data.yml")
    @ShouldMatchDataSet(
        "datasets/org/libreccm/core/GroupRepositoryTest/after-delete.yml")
    public void deleteGroup() {
        final Group group = groupRepository.findByGroupName("users");

        groupRepository.delete(group);
    }

}
