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

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_core_schema.sql"})
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
                    "LibreCCM-org.libreccm.security.PermissionManagerTest.war")
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
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addPackage(org.libreccm.testutils.EqualsVerifier.class
                .getPackage())
            .addPackage(org.libreccm.web.CcmApplication.class.getPackage())
            .addPackage(org.libreccm.workflow.Workflow.class.getPackage())
            .addPackage(com.arsdigita.kernel.KernelConfig.class.getPackage())
            .addPackage(com.arsdigita.kernel.security.SecurityConfig.class
                .getPackage())
            .addPackage(com.arsdigita.util.UncheckedWrapperException.class
                .getPackage())
            .addPackage(org.libreccm.cdi.utils.CdiUtil.class.getPackage())
            .addAsLibraries(getModuleDependencies())
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsWebInfResource("META-INF/beans.xml", "beans.xml");
    }

    @Test
    @InSequence(100)
    public void permissionManagerIsInjected() {
        assertThat(permissionManager, is(not(nullValue())));
    }

    @Test
    @InSequence(110)
    public void roleRepositoryIsInjected() {
        assertThat(roleRepository, is(not(nullValue())));
    }

    @Test
    @InSequence(120)
    public void ccmObjectRepositoryIsInjected() {
        assertThat(ccmObjectRepository, is(not(nullValue())));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/PermissionManagerTest/"
                    + "after-grant.yml",
        excludeColumns = {"permission_id"})
    @InSequence(200)
    public void grantPermission() {
        final Role role2 = roleRepository.findByName("role2");
        final CcmObject object3 = ccmObjectRepository.findById(-20003L);

        shiro.getSystemUser().execute(() -> {
            permissionManager.grantPrivilege("privilege2", role2, object3);
            permissionManager.grantPrivilege("privilege3", role2);
        });
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/PermissionManagerTest/"
                    + "data.yml")
    @InSequence(210)
    public void grantPermissionAgain() {
        final Role role1 = roleRepository.findByName("role1");
        final CcmObject object1 = ccmObjectRepository.findById(-20001L);

        shiro.getSystemUser().execute(() -> {
            permissionManager.grantPrivilege("privilege1", role1);
            permissionManager.grantPrivilege("privilege2", role1, object1);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(220)
    public void grantPermissionPrivilegeNull() throws Throwable {
        final Role role1 = roleRepository.findByName("role1");

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.grantPrivilege(null, role1));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(225)
    public void grantPermissionOnObjectPrivilegeNull() throws Throwable {
        final Role role1 = roleRepository.findByName("role1");
        final CcmObject object1 = ccmObjectRepository.findById(-20001L);

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.grantPrivilege(null, role1, object1));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(230)
    public void grantPermissionEmptyPrivilege() throws Throwable {
        final Role role1 = roleRepository.findByName("role1");

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.grantPrivilege("", role1));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(235)
    public void grantPermissionOnObjectEmptyPrivilege() throws Throwable {
        final Role role1 = roleRepository.findByName("role1");
        final CcmObject object1 = ccmObjectRepository.findById(-20001L);

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.grantPrivilege("", role1, object1));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

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

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(240)
    public void grantPermissionOnObjectToRoleNull() throws Throwable {
        final CcmObject object1 = ccmObjectRepository.findById(-20001L);

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.grantPrivilege("privilege",
                                                       null,
                                                       object1));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(250)
    public void grantPermissionNullObject() throws Throwable {
        final Role role1 = roleRepository.findByName("role1");

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.grantPrivilege("privilege1",
                                                       role1,
                                                       null));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/PermissionManagerTest/"
                    + "after-revoke.yml",
        excludeColumns = {"permission_id"})
    @InSequence(300)
    public void revokePermission() {
        final Role role1 = roleRepository.findByName("role1");
        final CcmObject object1 = ccmObjectRepository.findById(-20001L);

        shiro.getSystemUser().execute(() -> {
            permissionManager.revokePrivilege("privilege1", role1);
            permissionManager.revokePrivilege("privilege2", role1, object1);
        });
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/PermissionManagerTest/"
                    + "data.yml")
    @InSequence(310)
    public void revokeNotExistingPermission() throws Throwable {
        final Role role1 = roleRepository.findByName("role1");

        shiro.getSystemUser().execute(
            () -> permissionManager.revokePrivilege("privilege999", role1));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/PermissionManagerTest/"
                    + "data.yml")
    @InSequence(310)
    public void revokeNotExistingPermissionOnObject() {
        final Role role1 = roleRepository.findByName("role1");
        final CcmObject object1 = ccmObjectRepository.findById(-20001L);

        shiro.getSystemUser().execute(
            () -> permissionManager.revokePrivilege("privilege999",
                                                    role1,
                                                    object1));
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(320)
    public void revokePermissionPrivilegeNull() throws Throwable {
        final Role role1 = roleRepository.findByName("role1");

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.revokePrivilege(null, role1));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(320)
    public void revokePermissionOnObjectPrivilegeNull() throws Throwable {
        final Role role1 = roleRepository.findByName("role1");
        final CcmObject object1 = ccmObjectRepository.findById(-20001L);

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.revokePrivilege(null, role1, object1));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(330)
    public void revokePermissionEmptyPrivilege() throws Throwable {
        final Role role1 = roleRepository.findByName("role1");

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.revokePrivilege("", role1));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test(expected
              = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(320)
    public void revokePermissionOnObjectEmptyPrivilege() throws Throwable {
        final Role role1 = roleRepository.findByName("role1");
        final CcmObject object1 = ccmObjectRepository.findById(-20001L);

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.revokePrivilege("", role1, object1));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test(expected
              = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
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

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(345)
    public void revokePermissionOnObjectFromRoleNull() throws Throwable {
        final CcmObject object1 = ccmObjectRepository.findById(-20001L);

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.revokePrivilege("privilege1",
                                                        null,
                                                        object1));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test(expected
              = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(350)
    public void revokePermissionNullObject() throws Throwable {
        final Role role1 = roleRepository.findByName("role1");

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.revokePrivilege("privilege2",
                                                        role1,
                                                        null));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/PermissionManagerTest/"
                    + "after-copy.yml",
        excludeColumns = {"permission_id"})
    @InSequence(400)
    public void copyPermissions() throws Throwable {
        final CcmObject object2 = ccmObjectRepository.findById(-20002L);
        final CcmObject object3 = ccmObjectRepository.findById(-20003L);

        shiro.getSystemUser().execute(
            () -> permissionManager.copyPermissions(object2, object3));
    }

    @Test(expected
              = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(410)
    public void copyPermissionsNullSource() throws Throwable {
        final CcmObject object3 = ccmObjectRepository.findById(-20003L);

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.copyPermissions(null, object3));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    @Test(expected
              = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(420)
    public void copyPermissionsNullTarget() throws Throwable {
        final CcmObject object2 = ccmObjectRepository.findById(-20002L);

        try {
            shiro.getSystemUser().execute(
                () -> permissionManager.copyPermissions(object2, null));
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

}
