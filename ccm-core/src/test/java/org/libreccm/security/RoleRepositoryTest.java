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
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.After;
import org.junit.AfterClass;
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

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_core_schema.sql"})
public class RoleRepositoryTest {

    private static final String ADMINISTRATOR = "administrator";
    private static final String USER = "user";
    private static final String READER = "reader";

    @Inject
    private transient RoleRepository roleRepository;

    @PersistenceContext
    private transient EntityManager entityManager;

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
                    "LibreCCM-org.libreccm.security.RoleRepositoryTest.war")
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
    public void repoIsInjected() {
        assertThat(roleRepository, is(not(nullValue())));
    }

    @Test
    public void entityManagerIsInjected() {
        assertThat(entityManager, is(not(nullValue())));
    }

    private void checkRoles(final Role administrator,
                            final Role user,
                            final Role reader) {
        assertThat(administrator, is(not((nullValue()))));
        assertThat(administrator.getRoleId(), is(-10L));
        assertThat(administrator.getName(), is(equalTo(ADMINISTRATOR)));

        assertThat(user, is(not((nullValue()))));
        assertThat(user.getRoleId(), is(-20L));
        assertThat(user.getName(), is(equalTo(USER)));

        assertThat(reader, is(not((nullValue()))));
        assertThat(reader.getRoleId(), is(-30L));
        assertThat(reader.getName(), is(equalTo(READER)));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/RoleRepositoryTest/data.yml")
    @InSequence(100)
    public void findRoleById() {
        final Role administrator = roleRepository.findById(-10L);
        final Role user = roleRepository.findById(-20L);
        final Role reader = roleRepository.findById(-30L);

        checkRoles(administrator, user, reader);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/RoleRepositoryTest/data.yml")
    @InSequence(200)
    public void findRoleByName() {
        final Role administrator = roleRepository.findByName(ADMINISTRATOR);
        final Role user = roleRepository.findByName(USER);
        final Role reader = roleRepository.findByName(READER);

        checkRoles(administrator, user, reader);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/RoleRepositoryTest/data.yml")
    @InSequence(300)
    public void findAllRoles() {
        final List<Role> roles = roleRepository.findAll();

        assertThat(roles.size(), is(3));
    }

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

    @Test
    @UsingDataSet("datasets/org/libreccm/security/RoleRepositoryTest/data.yml")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/security/"
                                    + "RoleRepositoryTest/after-save-changed.yml",
                        excludeColumns = {"role_id"})
    @InSequence(500)
    public void saveChangedRole() {
        final Role role = roleRepository.findById(-20L);
        role.setName("writer");

        roleRepository.save(role);
    }

    @Test(expected = IllegalArgumentException.class)
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(600)
    public void saveNullValue() {
        roleRepository.save(null);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/RoleRepositoryTest/data.yml")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/security/"
                                    + "RoleRepositoryTest/after-delete.yml",
                        excludeColumns = {"role_id"})
    @InSequence(700)
    public void deleteRole() {
        final Role role = roleRepository.findByName(USER);
        
        roleRepository.delete(role);
    }
    
    @Test(expected = IllegalArgumentException.class)
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(800)
    public void deleteNullValue() {
        roleRepository.delete(null);
    }

}
