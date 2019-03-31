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
@CreateSchema({"001_create_schema.sql", 
               "002_create_ccm_core_tables.sql", 
               "003_init_hibernate_sequence.sql"})
@CleanupUsingScript(value = {"999_cleanup.sql"},
                    phase = TestExecutionPhase.BEFORE)
public class RoleManagerTest {

    @Inject
    private RoleManager roleManager;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private PartyRepository partyRepository;

    @Inject
    private Shiro shiro;

    public RoleManagerTest() {
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
                    "LibreCCM-org.libreccm.security.RoleManagerTest.war")
            .addPackage(org.libreccm.categorization.Categorization.class
                .getPackage())
            .addPackage(org.libreccm.configuration.Configuration.class
                .getPackage())
            .addPackage(org.libreccm.core.CcmObject.class.getPackage())
            .addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.jpa.utils.MimeTypeConverter.class
                .getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class.getPackage())
            .addPackage(org.libreccm.security.User.class.getPackage())
            .addPackage(org.libreccm.web.CcmApplication.class.getPackage())
            .addPackage(org.libreccm.workflow.Workflow.class.getPackage())
            .addPackage(org.libreccm.testutils.EqualsVerifier.class
                .getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addPackage(com.arsdigita.kernel.KernelConfig.class.getPackage())
            .addPackage(com.arsdigita.kernel.security.SecurityConfig.class
                .getPackage())
            .addPackage(org.libreccm.cdi.utils.CdiUtil.class.getPackage())
            .addClass(org.libreccm.imexport.Exportable.class)
            .addAsLibraries(getModuleDependencies())
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsWebInfResource("META-INF/beans.xml", "beans.xml");
    }

    @Test
    @InSequence(100)
    public void roleManagerIsInjected() {
        assertThat(roleManager, is(not(nullValue())));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/RoleManagerTest/data.yml")
    @ShouldMatchDataSet(
        excludeColumns = {"membership_id", "uuid"},
        value = "datasets/org/libreccm/security/RoleManagerTest/after-add.yml"
    )
    @InSequence(200)
    public void assignRoleToParty() {
        final Role role1 = roleRepository.findByName("role1").get();
        final Role role3 = roleRepository.findByName("role3").get();

        final Party joe = partyRepository.findByName("joe").get();
        final Party group1 = partyRepository.findByName("group1").get();

        shiro.getSystemUser().execute(() -> {
            roleManager.assignRoleToParty(role1, joe);
            roleManager.assignRoleToParty(role3, group1);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/RoleManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(210)
    public void assignRoleNullToParty() throws Throwable {
        final Party party = partyRepository.findByName("jdoe").get();

        try {
            shiro.getSystemUser().execute(
                () -> roleManager.assignRoleToParty(null, party));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/RoleManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(220)
    public void assignRoleToPartyNull() throws Throwable {
        final Role role = roleRepository.findByName("role1").get();

        try {
            shiro.getSystemUser().execute(
                () -> roleManager.assignRoleToParty(role, null));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/RoleManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/RoleManagerTest/data.yml")
    @InSequence(230)
    public void assignRoleToPartyAgain() {
        final Party jdoe = partyRepository.findByName("jdoe").get();
        final Role role1 = roleRepository.findByName("role1").get();

        shiro.getSystemUser().execute(
            () -> roleManager.assignRoleToParty(role1, jdoe));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/RoleManagerTest/data.yml")
    @ShouldMatchDataSet(
        value
            = "datasets/org/libreccm/security/RoleManagerTest/after-remove.yml",
        excludeColumns = {"membership_id"})
    @InSequence(300)
    public void removeRoleFromParty() {
        final Role role1 = roleRepository.findByName("role1").get();
        final Role role2 = roleRepository.findByName("role2").get();

        final Party jdoe = partyRepository.findByName("jdoe").get();
        final Party group1 = partyRepository.findByName("group1").get();

        shiro.getSystemUser().execute(() -> {
            roleManager.removeRoleFromParty(role1, jdoe);
            roleManager.removeRoleFromParty(role2, group1);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/RoleManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(310)
    public void removeRoleNullFromParty() throws Throwable {
        final Party party = partyRepository.findByName("jdoe").get();

        try {
            shiro.getSystemUser().execute(
                () -> roleManager.removeRoleFromParty(null, party));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/RoleManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(220)
    public void removeRoleFromPartyNull() throws Throwable {
        final Role role = roleRepository.findByName("role1").get();
        try {
            shiro.getSystemUser().execute(
                () -> roleManager.removeRoleFromParty(role, null));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/RoleManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/RoleManagerTest/data.yml")
    @InSequence(330)
    public void removeNotAssignedRoleFromParty() {
        final Role role2 = roleRepository.findByName("role2").get();
        final Party jdoe = partyRepository.findByName("jdoe").get();

        shiro.getSystemUser().execute(
            () -> roleManager.removeRoleFromParty(role2, jdoe));
    }

}
