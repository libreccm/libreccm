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
public class GroupManagerTest {

    @Inject
    private transient GroupManager groupManager;

    @Inject
    private transient UserRepository userRepository;

    @Inject
    private transient GroupRepository groupRepository;

    public GroupManagerTest() {
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
                    "LibreCCM-org.libreccm.core.GroupManagerTest.war")
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

    /**
     * Verify the
     * {@link GroupManager#isMemberOfGroup(org.libreccm.core.User, org.libreccm.core.Group)}
     * method.
     */
    @Test
    @InSequence(10)
    @UsingDataSet("datasets/org/libreccm/core/GroupManagerTest/"
                      + "users-groups.yml")
    public void isMemberOfGroup() {
        final User jdoe = userRepository.findByScreenName("jdoe");
        final Group admins = groupRepository.findByGroupName("admins");
        final Group users = groupRepository.findByGroupName("users");
        final Group authors = groupRepository.findByGroupName("authors");

        assertThat(groupManager.isMemberOfGroup(jdoe, admins), is(false));
        assertThat(groupManager.isMemberOfGroup(jdoe, users), is(true));
        assertThat(groupManager.isMemberOfGroup(jdoe, authors), is(true));
    }

    /**
     * Verify that
     * {@link GroupManager#isMemberOfGroup(org.libreccm.core.User, org.libreccm.core.Group)}
     * throws an {@link IllegalArgumentException} if the provided {@code user}
     * is {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(20)
    @UsingDataSet("datasets/org/libreccm/core/GroupManagerTest/"
                      + "users-groups.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void isMemberOfGroupNullUser() {
        final Group admins = groupRepository.findByGroupName("admins");

        groupManager.isMemberOfGroup(null, admins);
    }

    /**
     * Verify that
     * {@link GroupManager#isMemberOfGroup(org.libreccm.core.User, org.libreccm.core.Group)}
     * throws an {@link IllegalArgumentException} if the provided {@code group}
     * is {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(30)
    @UsingDataSet("datasets/org/libreccm/core/GroupManagerTest/"
                      + "users-groups.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void isMemberOfGroupNullGroup() {
        final User jdoe = userRepository.findByScreenName("jdoe");

        groupManager.isMemberOfGroup(jdoe, null);
    }

    /**
     * Verify that the
     * {@link GroupManager#addUserToGroup(org.libreccm.core.User, org.libreccm.core.Group)}
     * method adds an user to a group and stores the changed group and user
     * correctly to the database.
     */
    @Test
    @InSequence(40)
    @UsingDataSet("datasets/org/libreccm/core/GroupManagerTest/"
                      + "users-groups.yml")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/core/GroupManagerTest/"
                                    + "after-add-to-group.yml",
                        excludeColumns = {"membership_id"})
    public void addUserToGroup() {
        final User jdoe = userRepository.findByScreenName("jdoe");
        final Group admins = groupRepository.findByGroupName("admins");

        groupManager.addUserToGroup(jdoe, admins);

        assertThat(groupManager.isMemberOfGroup(jdoe, admins), is(true));
    }

    /**
     * Verify that
     * {@link GroupManager#addUserToGroup(org.libreccm.core.User, org.libreccm.core.Group)}
     * throws an {@link IllegalArgumentException} if the provided {@code user}
     * is {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(50)
    @UsingDataSet("datasets/org/libreccm/core/GroupManagerTest/"
                      + "users-groups.yml")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/core/GroupManagerTest/"
                                    + "users-groups.yml",
                        excludeColumns = {"membership_id"})
    @ShouldThrowException(IllegalArgumentException.class)
    public void addUserToGroupNullUser() {
        final Group admins = groupRepository.findByGroupName("admins");

        groupManager.addUserToGroup(null, admins);
    }

    /**
     * Verify that
     * {@link GroupManager#addUserToGroup(org.libreccm.core.User, org.libreccm.core.Group)}
     * throws an {@link IllegalArgumentException} if the provided {@code group}
     * is {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(60)
    @UsingDataSet("datasets/org/libreccm/core/GroupManagerTest/"
                      + "users-groups.yml")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/core/GroupManagerTest/"
                                    + "users-groups.yml",
                        excludeColumns = {"membership_id"})
    @ShouldThrowException(IllegalArgumentException.class)
    public void addUserToGroupNullGroup() {
        final User jdoe = userRepository.findByScreenName("jdoe");

        groupManager.addUserToGroup(jdoe, null);
    }

    /**
     * Verify that the
     * {@link GroupManager#addUserToGroup(org.libreccm.core.User, org.libreccm.core.Group)}
     * does nothing if the provided user is already a member of the provided
     * group.
     */
    @Test
    @InSequence(70)
    @UsingDataSet("datasets/org/libreccm/core/GroupManagerTest/"
                      + "users-groups.yml")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/core/GroupManagerTest/"
                                    + "users-groups.yml",
                        excludeColumns = {"membership_id"})
    public void addUserToGroupAlreadyMember() {
        final User jdoe = userRepository.findByScreenName("jdoe");
        final Group authors = groupRepository.findByGroupName("authors");

        groupManager.addUserToGroup(jdoe, authors);
    }

    /**
     * Verify that
     * {@link GroupManager#removeUserFromGroup(org.libreccm.core.User, org.libreccm.core.Group)}
     * really removes a user from a group and stores the changed user and group
     * in the database.
     */
    @Test
    @InSequence(80)
    @UsingDataSet("datasets/org/libreccm/core/GroupManagerTest/"
                      + "users-groups.yml")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/core/GroupManagerTest/"
                                    + "after-remove-from-group.yml",
                        excludeColumns = {"membership_id"})
    public void removeUserFromGroup() {
        final User jdoe = userRepository.findByScreenName("jdoe");
        final Group authors = groupRepository.findByGroupName("authors");

        groupManager.removeUserFromGroup(jdoe, authors);

        assertThat(groupManager.isMemberOfGroup(jdoe, authors), is(false));
    }

    /**
     * Verify that
     * {@link GroupManager#removeUserFromGroup(org.libreccm.core.User, org.libreccm.core.Group)}
     * throws an {@link IllegalArgumentException} if the provided {@code user}
     * is {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(90)
    @UsingDataSet("datasets/org/libreccm/core/GroupManagerTest/"
                      + "users-groups.yml")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/core/GroupManagerTest/"
                                    + "users-groups.yml",
                        excludeColumns = {"membership_id"})
    @ShouldThrowException(IllegalArgumentException.class)
    public void removeUserFromGroupNullUser() {
        final Group authors = groupRepository.findByGroupName("authors");

        groupManager.removeUserFromGroup(null, authors);
    }

    /**
     * Verify that
     * {@link GroupManager#removeUserFromGroup(org.libreccm.core.User, org.libreccm.core.Group)}
     * throws an {@link IllegalArgumentException} if the provided {@code group}
     * is {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(100)
    @UsingDataSet("datasets/org/libreccm/core/GroupManagerTest/"
                      + "users-groups.yml")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/core/GroupManagerTest/"
                                    + "users-groups.yml",
                        excludeColumns = {"membership_id"})
    @ShouldThrowException(IllegalArgumentException.class)
    public void removeUserFromGroupNullGroup() {
        final User jdoe = userRepository.findByScreenName("jdoe");

        groupManager.removeUserFromGroup(jdoe, null);
    }

    /**
     * Verify that
     * {@link GroupManager#removeUserFromGroup(org.libreccm.core.User, org.libreccm.core.Group)}
     * does nothing if the provided {@code user} is not a member of the provided
     * {@code group}.
     */
    @Test
    @InSequence(110)
    @UsingDataSet("datasets/org/libreccm/core/GroupManagerTest/"
                      + "users-groups.yml")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/core/GroupManagerTest/"
                                    + "users-groups.yml",
                        excludeColumns = {"membership_id"})
    public void removeUserFromGroupNotMember() {
        final User jdoe = userRepository.findByScreenName("jdoe");
        final Group admins = groupRepository.findByGroupName("admins");

        groupManager.removeUserFromGroup(jdoe, admins);
    }

}
