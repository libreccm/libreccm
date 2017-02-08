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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.tests.categories.IntegrationTest;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.libreccm.core.CcmObject;
import org.libreccm.core.CcmObjectRepository;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import static org.libreccm.testutils.DependenciesHelpers.*;

import org.jboss.arquillian.persistence.CleanupUsingScript;

import java.util.Optional;

/**
 * Tests for the {@link RoleRepository}. Note. We are not enabling the
 * {@link AuthorizationInterceptor} for this test.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_core_schema.sql"})
@CleanupUsingScript({"cleanup.sql"})
public class RoleRepositoryTest {

    private static final String ADMINISTRATOR = "administrator";
    private static final String USER = "user";
    private static final String READER = "reader";

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private CcmObjectRepository ccmObjRepo;

    @PersistenceContext
    private EntityManager entityManager;

    public RoleRepositoryTest() {
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
                    "LibreCCM-org.libreccm.security.RoleRepositoryTest.war")
            .addPackage(org.libreccm.security.User.class.getPackage())
            .addPackage(org.libreccm.core.CcmObject.class.getPackage())
            .addPackage(org.libreccm.categorization.Categorization.class
                .getPackage())
            .addPackage(
                org.libreccm.configuration.ConfigurationManager.class
                .getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class.getPackage()).
            addPackage(org.libreccm.web.CcmApplication.class.getPackage())
            .addPackage(org.libreccm.workflow.Workflow.class.getPackage())
            .addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.jpa.utils.MimeTypeConverter.class
                .getPackage())
            .addPackage(org.libreccm.testutils.EqualsVerifier.class.
                getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addClass(org.libreccm.portation.Portable.class)
            .addAsLibraries(getModuleDependencies())
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    /**
     * Verify that a {@link RoleRepository} instance is injected into
     * {@link #roleRepository}.
     */
    @Test
    public void repoIsInjected() {
        assertThat(roleRepository, is(not(nullValue())));
    }

    /**
     * Verify that a {@link EntityManager} instance is injected into
     * {@link #entityManager}.
     */
    @Test
    public void entityManagerIsInjected() {
        assertThat(entityManager, is(not(nullValue())));
    }

    /**
     * Helper method encapsulating some checks done multiple test methods.
     *
     * @param administrator The administrator role.
     * @param user          The user role.
     * @param reader        The reader role.
     */
    private void checkRoles(final Optional<Role> administrator,
                            final Optional<Role> user,
                            final Optional<Role> reader) {
        assertThat(administrator.isPresent(), is(true));
        assertThat(administrator.get().getRoleId(), is(-10L));
        assertThat(administrator.get().getName(), is(equalTo(ADMINISTRATOR)));

        assertThat(user.isPresent(), is(true));
        assertThat(user.get().getRoleId(), is(-20L));
        assertThat(user.get().getName(), is(equalTo(USER)));

        assertThat(reader.isPresent(), is(true));
        assertThat(reader.get().getRoleId(), is(-30L));
        assertThat(reader.get().getName(), is(equalTo(READER)));
    }

    /**
     * Tries to find several {@link Role}s by their {@link Role#roleId} using
     * {@link RoleRepository#findById(java.lang.Object)}.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/security/RoleRepositoryTest/data.yml")
    @InSequence(100)
    public void findRoleById() {
        final Optional<Role> administrator = roleRepository.findById(-10L);
        final Optional<Role> user = roleRepository.findById(-20L);
        final Optional<Role> reader = roleRepository.findById(-30L);

        checkRoles(administrator, user, reader);
    }

    /**
     * Tries to find several {@link Role}s by their unique {@link Role#name}
     * using {@link RoleRepository#findByName(java.lang.String)}.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/security/RoleRepositoryTest/data.yml")
    @InSequence(200)
    public void findRoleByName() {
        final Optional<Role> administrator = roleRepository.findByName(ADMINISTRATOR);
        final Optional<Role> user = roleRepository.findByName(USER);
        final Optional<Role> reader = roleRepository.findByName(READER);

        checkRoles(administrator, user, reader);
    }

    /**
     * Tries to find all {@link Role}s using {@link RoleRepository#findAll()}.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/security/RoleRepositoryTest/data.yml")
    @InSequence(300)
    public void findAllRoles() {
        final List<Role> roles = roleRepository.findAll();

        assertThat(roles.size(), is(3));
    }

    /**
     * Tests the {@link RoleRepository#findByPrivilege(java.lang.String)} method
     * and the named query used by this method. Note: We are using the dataset
     * from the {@link PermissionManagerTest} here because it contains roles,
     * permissions and objects.
     */
    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @InSequence(310)
    public void findByPrivilege() {
        final List<Role> rolesWithPrivilege1 = roleRepository.findByPrivilege(
            "privilege1");
        final List<Role> rolesWithPrivilege2 = roleRepository.findByPrivilege(
            "privilege2");
        final List<Role> empty = roleRepository.findByPrivilege("privilege3");

        assertThat(rolesWithPrivilege1.size(), is(1));
        assertThat(rolesWithPrivilege2.size(), is(2));
        assertThat(empty.isEmpty(), is(true));

        assertThat(rolesWithPrivilege1.get(0).getName(), is(equalTo("role1")));
        assertThat(rolesWithPrivilege2.get(0).getName(), is(equalTo("role1")));
        assertThat(rolesWithPrivilege2.get(1).getName(), is(equalTo("role2")));

    }

    /**
     * Tests the {@link RoleRepository#findByPrivilege(java.lang.String, org.libreccm.core.CcmObject)
     * } method and the named query used by this method. Note: We are using the
     * dataset from the {@link PermissionManagerTest} here because it contains
     * roles, permissions and objects.
     */
    @Test
    @UsingDataSet(
        "datasets/org/libreccm/security/PermissionManagerTest/data.yml")
    @InSequence(310)
    public void findByPrivilegeAndObject() {
        final CcmObject object1 = ccmObjRepo.findById(-20001L).get();
        final CcmObject object2 = ccmObjRepo.findById(-20002L).get();
        final CcmObject object3 = ccmObjRepo.findById(-20003L).get();

        final List<Role> rolesWithPrivilege1 = roleRepository.findByPrivilege(
            "privilege1", object1);
        final List<Role> rolesWithPrivilege2 = roleRepository.findByPrivilege(
            "privilege2", object1);
        final List<Role> empty1 = roleRepository.findByPrivilege("privilege3",
                                                                 object1);
        final List<Role> empty2 = roleRepository.findByPrivilege("privilege1",
                                                                 object3);

        assertThat(rolesWithPrivilege1.size(), is(0));
        assertThat(rolesWithPrivilege2.size(), is(1));
        assertThat(empty1.isEmpty(), is(true));
        assertThat(empty2.isEmpty(), is(true));

        assertThat(rolesWithPrivilege2.get(0).getName(), is(equalTo("role1")));

    }

    /**
     * Tries to save a new {@link Role} by using
     * {@link RoleRepository#save(org.libreccm.security.Role)}.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/security/RoleRepositoryTest/data.yml")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/security/"
                                    + "RoleRepositoryTest/after-save-new.yml",
                        excludeColumns = {"role_id"})
    @InSequence(400)
    public void saveNewRole() {
        final Role role = new Role();
        role.setName("editor");

        roleRepository.save(role);
    }

    /**
     * Tries to save a updated {@link Role} by using
     * {@link RoleRepository#save(org.libreccm.security.Role)}.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/security/RoleRepositoryTest/data.yml")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/security/"
                                    + "RoleRepositoryTest/after-save-changed.yml",
                        excludeColumns = {"role_id"})
    @InSequence(500)
    public void saveChangedRole() {
        final Role role = roleRepository.findById(-20L).get();
        role.setName("writer");

        roleRepository.save(role);
    }

    /**
     * Verifies that {@link RoleRepository#save(org.libreccm.security.Role)}
     * throws a {@link IllegalArgumentException} is called with {@code null} for
     * the {@link Role} to save.
     */
    @Test(expected = IllegalArgumentException.class)
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(600)
    public void saveNullValue() {
        roleRepository.save(null);
    }

    /**
     * Tries a delete a {@link Role} by using
     * {@link RoleRepository#delete(org.libreccm.security.Role)}.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/security/RoleRepositoryTest/data.yml")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/security/"
                                    + "RoleRepositoryTest/after-delete.yml",
                        excludeColumns = {"role_id"})
    @InSequence(700)
    public void deleteRole() {
        final Role role = roleRepository.findByName(USER).get();

        roleRepository.delete(role);
    }

    /**
     * Verifies that {@link RoleRepository#delete(org.libreccm.security.Role)}
     * throws an {@link IllegalArgumentException} is called with {@code null}
     * for the {@link Role} to delete.
     */
    @Test(expected = IllegalArgumentException.class)
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(800)
    public void deleteNullValue() {
        roleRepository.delete(null);
    }

}
