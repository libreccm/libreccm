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
import org.libreccm.core.CcmObject;
import org.libreccm.core.CcmObjectRepository;
import org.libreccm.tests.categories.IntegrationTest;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import static org.libreccm.testutils.DependenciesHelpers.*;

import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.libreccm.categorization.CategorizationConstants;
import org.libreccm.core.CoreConstants;

import java.util.List;
import org.jboss.arquillian.persistence.TestExecutionPhase;

/**
 * Integration tests (run in a Application Server by Arquillian} for the
 * {@link PermissionManager}.
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
public class PermissionManagerTest {

    @Inject
    private PermissionManager permissionManager;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private CcmObjectRepository ccmObjectRepository;

    @Inject
    private EntityManager entityManager;

    @Inject
    private Shiro shiro;

    public PermissionManagerTest() {
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
                    "LibreCCM-org.libreccm.security.PermissionManagerTest.war").
            addPackage(org.libreccm.categorization.Categorization.class
                .getPackage())
            .addPackage(org.libreccm.configuration.Configuration.class
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
            .addPackage(com.arsdigita.kernel.KernelConfig.class.getPackage()).
            addPackage(com.arsdigita.kernel.security.SecurityConfig.class
                .getPackage())
            .addPackage(org.libreccm.cdi.utils.CdiUtil.class.getPackage())
            .addClass(org.libreccm.portation.Portable.class)
            .addAsLibraries(getModuleDependencies())
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsWebInfResource("META-INF/beans.xml", "beans.xml");
    }

    /**
     * Verifies that a {@link PermissionManager} is injected.
     */
    @Test
    @InSequence(100)
    public void permissionManagerIsInjected() {
        assertThat(permissionManager, is(not(nullValue())));
    }

    /**
     * Verifies that a {@link RoleRepository} is injected.
     */
    @Test
    @InSequence(110)
    public void roleRepositoryIsInjected() {
        assertThat(roleRepository, is(not(nullValue())));
    }

    /**
     * Verifies that a {@link CcmObjectRepository} is injected.
     */
    @Test
    @InSequence(120)
    public void ccmObjectRepositoryIsInjected() {
        assertThat(ccmObjectRepository, is(not(nullValue())));
    }

    /**
     * Verifies that the
     * {@link PermissionManager#grantPrivilege(java.lang.String, org.libreccm.security.Role)}
     * and
     * {@link PermissionManager#grantPrivilege(java.lang.String, org.libreccm.security.Role, org.libreccm.core.CcmObject)}
     * create valid permissions.
     */
    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/PermissionManagerTest/"
                    + "after-grant.yml",
        excludeColumns = {"permission_id"})
    @InSequence(200)
    public void grantPermission() {
        final Role role2 = roleRepository.findByName("role2").get();
        final CcmObject object3 = ccmObjectRepository.findById(-20003L).get();

        shiro.getSystemUser().execute(() -> {
            permissionManager.grantPrivilege("privilege2", role2, object3);
            permissionManager.grantPrivilege("privilege3", role2);
        });
    }

    /**
     * Verifies that
     * {@link PermissionManager#grantPrivilege(java.lang.String, org.libreccm.security.Role)}
     * and
     * {@link PermissionManager#grantPrivilege(java.lang.String, org.libreccm.security.Role, org.libreccm.core.CcmObject)}
     * don't create duplicate permissions if an permission already exists.
     */
    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/PermissionManagerTest/"
                    + "data.yml")
    @InSequence(210)
    public void grantPermissionAgain() {
        final Role role1 = roleRepository.findByName("role1").get();
        final CcmObject object1 = ccmObjectRepository.findById(-20001L).get();

        shiro.getSystemUser().execute(() -> {
            permissionManager.grantPrivilege("privilege1", role1);
            permissionManager.grantPrivilege("privilege2", role1, object1);
        });
    }

    /**
     * Verifies that permissions are granted recursivly when a permission is
     * granted if the object has properties which are annotated with
     * {@link RecursivePermissions}.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/security/PermissionManagerTest/"
                      + "data-recursivly.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/PermissionManagerTest/"
                    + "after-grant-recursivly.yml",
        excludeColumns = {"permission_id"})
    @InSequence(211)
    public void grantPermissionRecursively() {
        final Role role1 = roleRepository.findByName("role1").get();
        final CcmObject category1 = ccmObjectRepository.findById(-20001L).get();

        shiro.getSystemUser().execute(() -> {
            permissionManager.grantPrivilege("privilege4", role1, category1);
        });
    }

    /**
     * Verifies that
     * {@link PermissionManager#grantPrivilege(java.lang.String, org.libreccm.security.Role)}
     * throws a {@link IllegalArgumentException} if called with {@code null} for
     * the privilege to grant.
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(220)
    public void grantPermissionPrivilegeNull() throws Throwable {
        final Role role1 = roleRepository.findByName("role1").get();

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.grantPrivilege(null, role1));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    /**
     * Verifies that
     * {@link PermissionManager#grantPrivilege(java.lang.String, org.libreccm.security.Role, org.libreccm.core.CcmObject)}
     * throws a {@link IllegalArgumentException} if called with {@code null} for
     * the privilege to grant.
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(225)
    public void grantPermissionOnObjectPrivilegeNull() throws Throwable {
        final Role role1 = roleRepository.findByName("role1").get();
        final CcmObject object1 = ccmObjectRepository.findById(-20001L).get();

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.grantPrivilege(null, role1, object1));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    /**
     * Verifies that
     * {@link PermissionManager#grantPrivilege(java.lang.String, org.libreccm.security.Role)}
     * throws a {@link IllegalArgumentException} if called with an empty for a
     * privilege to grant.
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(230)
    public void grantPermissionEmptyPrivilege() throws Throwable {
        final Role role1 = roleRepository.findByName("role1").get();

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.grantPrivilege("", role1));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    /**
     * Verifies that
     * {@link PermissionManager#grantPrivilege(java.lang.String, org.libreccm.security.Role, org.libreccm.core.CcmObject)}
     * throws a {@link IllegalArgumentException} if called with an empty for a
     * privilege to grant.
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(235)
    public void grantPermissionOnObjectEmptyPrivilege() throws Throwable {
        final Role role1 = roleRepository.findByName("role1").get();
        final CcmObject object1 = ccmObjectRepository.findById(-20001L).get();

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.grantPrivilege("", role1, object1));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    /**
     * Verifies that
     * {@link PermissionManager#grantPrivilege(java.lang.String, org.libreccm.security.Role}
     * throws a {@link IllegalArgumentException} if called with {@code null} for
     * the role parameter
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(240)
    public void grantPermissionToRoleNull() throws Throwable {
        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.grantPrivilege("privilege", null));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    /**
     * Verifies that
     * {@link PermissionManager#grantPrivilege(java.lang.String, org.libreccm.security.Role, org.libreccm.core.CcmObject)}
     * throws a {@link IllegalArgumentException} if called with {@code null} for
     * the role parameter
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(240)
    public void grantPermissionOnObjectToRoleNull() throws Throwable {
        final CcmObject object1 = ccmObjectRepository.findById(-20001L).get();

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.grantPrivilege("privilege",
                                                       null,
                                                       object1));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    /**
     * Verifies that
     * {@link PermissionManager#grantPrivilege(java.lang.String, org.libreccm.security.Role, org.libreccm.core.CcmObject)}
     * throws a {@link IllegalArgumentException} if called with {@code null} for
     * the object on which the privilege should be granted.
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(250)
    public void grantPermissionNullObject() throws Throwable {
        final Role role1 = roleRepository.findByName("role1").get();

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.grantPrivilege("privilege1",
                                                       role1,
                                                       null));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    /**
     * Verifies that
     * {@link PermissionManager#revokePrivilege(java.lang.String, org.libreccm.security.Role)}
     * and
     * {@link PermissionManager#revokePrivilege(java.lang.String, org.libreccm.security.Role, org.libreccm.core.CcmObject)}
     * delete the appropriate permissions from the database.
     */
    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/PermissionManagerTest/"
                    + "after-revoke.yml",
        excludeColumns = {"permission_id"})
    @InSequence(300)
    public void revokePermission() {
        final Role role1 = roleRepository.findByName("role1").get();
        final CcmObject object1 = ccmObjectRepository.findById(-20001L).get();

        shiro.getSystemUser().execute(() -> {
            permissionManager.revokePrivilege("privilege1", role1);
            permissionManager.revokePrivilege("privilege2", role1, object1);
        });
    }

    /**
     * Verifies that
     * {@link PermissionManager#revokePrivilege(java.lang.String, org.libreccm.security.Role)}
     * does <em>not</em> throw an exception if no appropriate permission for the
     * provided parameters exists.
     *
     * @throws Throwable
     */
    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/PermissionManagerTest/"
                    + "data.yml")
    @InSequence(310)
    public void revokeNotExistingPermission() throws Throwable {
        final Role role1 = roleRepository.findByName("role1").get();

        shiro.getSystemUser().execute(
            () -> permissionManager.revokePrivilege("privilege999", role1));
    }

    /**
     * Verifies that inherited permissions are revoked when they revoked from
     * the object on which the permissions were granted.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/security/PermissionManagerTest/"
                      + "after-grant-recursivly.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/PermissionManagerTest/"
                    + "after-revoke-recursivly.yml")
    @InSequence(311)
    public void revokePermissionRecursivly() {
        final Role role1 = roleRepository.findByName("role1").get();
        final CcmObject category1 = ccmObjectRepository.findById(-20001L).get();

        shiro.getSystemUser().execute(() -> {
            permissionManager.revokePrivilege("privilege4", role1, category1);
        });
    }

    /**
     * Verifies that
     * {@link PermissionManager#revokePrivilege(java.lang.String, org.libreccm.security.Role, org.libreccm.core.CcmObject)}
     * does <em>not</em> throw an exception if no appropriate permission for the
     * provided parameters exists.
     *
     */
    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/PermissionManagerTest/"
                    + "data.yml")
    @InSequence(310)
    public void revokeNotExistingPermissionOnObject() {
        final Role role1 = roleRepository.findByName("role1").get();
        final CcmObject object1 = ccmObjectRepository.findById(-20001L).get();

        shiro.getSystemUser().execute(
            () -> permissionManager.revokePrivilege("privilege999",
                                                    role1,
                                                    object1));
    }

    /**
     * Verifies that
     * {@link PermissionManager#revokePrivilege(java.lang.String, org.libreccm.security.Role)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the privilege to revoke.
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(320)
    public void revokePermissionPrivilegeNull() throws Throwable {
        final Role role1 = roleRepository.findByName("role1").get();

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.revokePrivilege(null, role1));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    /**
     * Verifies that
     * {@link PermissionManager#revokePrivilege(java.lang.String, org.libreccm.security.Role, org.libreccm.core.CcmObject)}.
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the privilege to revoke.
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(320)
    public void revokePermissionOnObjectPrivilegeNull() throws Throwable {
        final Role role1 = roleRepository.findByName("role1").get();
        final CcmObject object1 = ccmObjectRepository.findById(-20001L).get();

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.
                    revokePrivilege(null, role1, object1));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    /**
     * Verifies that
     * {@link PermissionManager#revokePrivilege(java.lang.String, org.libreccm.security.Role)}
     * throws an {@link IllegalArgumentException} if called with an empty string
     * for the privilege to revoke.
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(330)
    public void revokePermissionEmptyPrivilege() throws Throwable {
        final Role role1 = roleRepository.findByName("role1").get();

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.revokePrivilege("", role1));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    /**
     * Verifies that
     * {@link PermissionManager#revokePrivilege(java.lang.String, org.libreccm.security.Role, org.libreccm.core.CcmObject)}
     * throws an {@link NullPointerException} if called with {@code null} for
     * the privilege to revoke.
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(320)
    public void revokePermissionOnObjectEmptyPrivilege() throws Throwable {
        final Role role1 = roleRepository.findByName("role1").get();
        final CcmObject object1 = ccmObjectRepository.findById(-20001L).get();

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.revokePrivilege("", role1, object1));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    /**
     * Verifies that
     * {@link PermissionManager#revokePrivilege(java.lang.String, org.libreccm.security.Role)}
     * throws an {@link NullPointerException} if called with {@code null} for
     * the role to revoke the permission from.
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/PermissionManagerTest/"
        + "data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(340)
    public void revokePermissionFromRoleNull() throws Throwable {
        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.revokePrivilege("privilege1", null));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    /**
     * Verifies that
     * {@link PermissionManager#revokePrivilege(java.lang.String, org.libreccm.security.Role, org.libreccm.core.CcmObject)}
     * throws an {@link NullPointerException} if called with {@code null} for
     * the role to revoke the permission from.
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(345)
    public void revokePermissionOnObjectFromRoleNull() throws Throwable {
        final CcmObject object1 = ccmObjectRepository.findById(-20001L).get();

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.revokePrivilege("privilege1",
                                                        null,
                                                        object1));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    /**
     * Verifies that
     * {@link PermissionManager#revokePrivilege(java.lang.String, org.libreccm.security.Role, org.libreccm.core.CcmObject)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the object to revoke the permission from.
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(350)
    public void revokePermissionNullObject() throws Throwable {
        final Role role1 = roleRepository.findByName("role1").get();

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.revokePrivilege("privilege2",
                                                        role1,
                                                        null));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    /**
     * Verifies that
     * {@link PermissionManager#copyPermissions(org.libreccm.core.CcmObject, org.libreccm.core.CcmObject)}
     * correctly copies permissions between two objects.
     *
     * @throws Throwable
     */
    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/PermissionManagerTest/"
                    + "after-copy.yml",
        excludeColumns = {"permission_id"})
    @InSequence(400)
    public void copyPermissions() throws Throwable {
        final CcmObject object2 = ccmObjectRepository.findById(-20002L).get();
        final CcmObject object3 = ccmObjectRepository.findById(-20003L).get();

        shiro.getSystemUser().execute(
            () -> permissionManager.copyPermissions(object2, object3));
    }

    /**
     * Verifies that
     * {@link PermissionManager#copyPermissions(org.libreccm.core.CcmObject, org.libreccm.core.CcmObject)}
     * throw an {@link IllegalArgumentException} if called with {@code null} for
     * the source object.
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(410)
    public void copyPermissionsNullSource() throws Throwable {
        final CcmObject object3 = ccmObjectRepository.findById(-20003L).get();

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.copyPermissions(null, object3));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    /**
     * Verifies that
     * {@link PermissionManager#copyPermissions(org.libreccm.core.CcmObject, org.libreccm.core.CcmObject)}
     * throw an {@link IllegalArgumentException} if called with {@code null} for
     * the target object.
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(420)
    public void copyPermissionsNullTarget() throws Throwable {
        final CcmObject object2 = ccmObjectRepository.findById(-20002L).get();

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.copyPermissions(object2, null));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    /**
     * Verifies if
     * {@link PermissionManager#listDefiniedPrivileges(java.lang.Class)} returns
     * the expected value.
     */
    @Test
    @InSequence(500)
    public void verifyListPrivileges() {

        final List<String> corePrivileges = permissionManager
            .listDefiniedPrivileges(CoreConstants.class);
        final List<String> catPrivileges = permissionManager
            .listDefiniedPrivileges(CategorizationConstants.class);

        assertThat(corePrivileges, is(not(nullValue())));
        assertThat(corePrivileges.isEmpty(), is(false));
        assertThat(corePrivileges.size(), is(2));
        assertThat(corePrivileges, contains(CoreConstants.PRIVILEGE_ADMIN,
                                            CoreConstants.PRIVILEGE_SYSTEM));

        assertThat(catPrivileges, is(not(nullValue())));
        assertThat(catPrivileges.isEmpty(), is(false));
        assertThat(catPrivileges.size(), is(3));
        assertThat(catPrivileges,
                   contains(CategorizationConstants.PRIVILEGE_MANAGE_CATEGORY,
                            CategorizationConstants.PRIVILEGE_MANAGE_CATEGORY_OBJECTS,
                            CategorizationConstants.PRIVILEGE_MANAGE_DOMAINS));

    }

}
