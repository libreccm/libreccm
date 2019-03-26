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

import org.apache.shiro.subject.ExecutionException;

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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import static org.libreccm.testutils.DependenciesHelpers.*;

import org.jboss.arquillian.persistence.CleanupUsingScript;
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
public class GroupManagerTest {

    @Inject
    private GroupManager groupManager;

    @Inject
    private GroupRepository groupRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private Shiro shiro;

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
        return ShrinkWrap
            .create(WebArchive.class,
                    "LibreCCM-org.libreccm.security.GroupManagerTest.war")
            .addPackage(org.libreccm.categorization.Categorization.class
                .getPackage())
            .addPackage(
                org.libreccm.configuration.ConfigurationManager.class
                    .getPackage())
            .addPackage(org.libreccm.core.CcmObject.class.getPackage())
            .addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.jpa.utils.MimeTypeConverter.class
                .getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class.getPackage()).
            addPackage(org.libreccm.security.User.class.getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addPackage(org.libreccm.testutils.EqualsVerifier.class
                .getPackage())
            .addPackage(org.libreccm.web.CcmApplication.class.getPackage())
            .addPackage(org.libreccm.workflow.Workflow.class.getPackage())
            .addPackage(com.arsdigita.kernel.security.SecurityConfig.class
                .getPackage())
            .addPackage(org.libreccm.cdi.utils.CdiUtil.class.getPackage())
            .addClass(com.arsdigita.kernel.KernelConfig.class)
            .addClass(com.arsdigita.kernel.security.SecurityConfig.class)
            .addClass(org.libreccm.imexport.Exportable.class)
            .addAsLibraries(getModuleDependencies())
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsWebInfResource("META-INF/beans.xml", "beans.xml");
    }

    @Test
    @InSequence(100)
    public void groupManagerIsInjected() {
        assertThat(groupManager, is(not(nullValue())));
    }

    @Test
    @InSequence(110)
    public void groupRepositoryIsInjected() {
        assertThat(groupRepository, is(not(nullValue())));
    }

    @Test
    @InSequence(120)
    public void userRepositoryIsInjected() {
        assertThat(userRepository, is(not(nullValue())));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/GroupManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/GroupManagerTest/after-add.yml",
        excludeColumns = {"membership_id"})
    @InSequence(200)
    public void addUserToGroup() {
        final Group admins = groupRepository.findByName("admins").get();
        final Group editors = groupRepository.findByName("editors").get();

        final User jdoe = userRepository.findByName("jdoe").get();
        final User mmuster = userRepository.findByName("mmuster").get();

        shiro.getSystemUser().execute(() -> {
            groupManager.addMemberToGroup(mmuster, admins);
            groupManager.addMemberToGroup(jdoe, editors);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/GroupManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(210)
    public void addNullUserToGroup() throws Throwable {
        final Group admins = groupRepository.findByName("admins").get();

        try {
            shiro.getSystemUser().execute(
                () -> groupManager.addMemberToGroup(null, admins));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/GroupManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(220)
    public void addUserToGroupNull() throws Throwable {
        final User jdoe = userRepository.findByName("jdoe").get();

        try {
            shiro.getSystemUser().execute(
                () -> groupManager.addMemberToGroup(jdoe, null));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/GroupManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/GroupManagerTest/data.yml")
    @InSequence(230)
    public void addUserToGroupAgain() {
        final Group admins = groupRepository.findByName("admins").get();
        final User jdoe = userRepository.findByName("jdoe").get();

        shiro.getSystemUser().execute(
            () -> groupManager.addMemberToGroup(jdoe, admins));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/GroupManagerTest/data.yml")
    @ShouldMatchDataSet("datasets/org/libreccm/security/GroupManagerTest/"
                            + "after-remove.yml")
    @InSequence(300)
    public void removeUserFromGroup() {
        final Group admins = groupRepository.findByName("admins").get();
        final Group users = groupRepository.findByName("users").get();

        final User jdoe = userRepository.findByName("jdoe").get();
        final User mmuster = userRepository.findByName("mmuster").get();

        assertThat(admins.getMemberships().size(), is(1));
        assertThat(users.getMemberships().size(), is(2));

        shiro.getSystemUser().execute(() -> {
            groupManager.removeMemberFromGroup(jdoe, admins);
            groupManager.removeMemberFromGroup(mmuster, users);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/GroupManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(310)
    public void removeUserNullFromGroup() throws Throwable {
        final Group admins = groupRepository.findByName("admins").get();

        try {
            shiro.getSystemUser().execute(
                () -> groupManager.removeMemberFromGroup(null, admins));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/GroupManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(320)
    public void removeUserFromGroupNull() throws Throwable {
        final User jdoe = userRepository.findByName("jdoe").get();

        try {
            shiro.getSystemUser().execute(
                () -> groupManager.removeMemberFromGroup(jdoe, null));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/GroupManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/GroupManagerTest/data.yml")
    @InSequence(330)
    public void removeUserGroupNotAMember() {
        final Group admins = groupRepository.findByName("admins").get();
        final User mmuster = userRepository.findByName("mmuster").get();

        shiro.getSystemUser().execute(
            () -> groupManager.removeMemberFromGroup(mmuster, admins));
    }

}
