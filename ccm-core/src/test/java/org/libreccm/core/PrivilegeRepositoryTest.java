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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.tests.categories.IntegrationTest;

import java.io.File;

import javax.inject.Inject;

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
public class PrivilegeRepositoryTest {

    @Inject
    private transient PrivilegeRepository privilegeRepository;

    public PrivilegeRepositoryTest() {
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

    @Test
    @UsingDataSet("datasets/org/libreccm/core/PrivilegeRepositoryTest/"
                      + "data.yml")
    @InSequence(10)
    public void retrievePrivilege() {
        final Privilege admin = privilegeRepository.retrievePrivilege("admin");
        final Privilege read = privilegeRepository.retrievePrivilege("read");
        final Privilege write = privilegeRepository.retrievePrivilege("write");

        assertThat(admin, is(not(nullValue())));
        assertThat(read, is(not(nullValue())));
        assertThat(write, is(not(nullValue())));

        assertThat(admin.getLabel(), is(equalTo("admin")));
        assertThat(read.getLabel(), is(equalTo("read")));
        assertThat(write.getLabel(), is(equalTo("write")));
    }

    @Test(expected = UnknownPrivilegeException.class)
    @UsingDataSet("datasets/org/libreccm/core/PrivilegeRepositoryTest/"
                      + "data.yml")
    @ShouldThrowException(UnknownPrivilegeException.class)
    @InSequence(20)
    public void retrieveNotExitingPrivilege() {
        privilegeRepository.retrievePrivilege("publish");
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/core/PrivilegeRepositoryTest/"
                      + "data.yml")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/core/"
                                    + "PrivilegeRepositoryTest/after-create.yml",
                        excludeColumns = {"privilege_id"})
    @InSequence(30)
    public void createNewPrivilege() {
        privilegeRepository.createPrivilege("publish");
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/core/PrivilegeRepositoryTest/"
                      + "data.yml")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/core/"
                        + "PrivilegeRepositoryTest/after-delete.yml",
                        excludeColumns = {"privilege_id"})
    @InSequence(40)
    public void deletePrivilege() {
        privilegeRepository.deletePrivilege("write");
    }

    @Test(expected = UnknownPrivilegeException.class)
    @UsingDataSet("datasets/org/libreccm/core/PrivilegeRepositoryTest/"
                      + "data.yml")
    @ShouldThrowException(UnknownPrivilegeException.class)
    @InSequence(41)
    public void deleteNullPrivilege() {
        privilegeRepository.deletePrivilege(null);
    }
    
    @Test
    @UsingDataSet("datasets/org/libreccm/core/PermissionRepositoryTest/"
                      + "data.yml")
    @InSequence(50)
    public void checkIsPermissionInUse() {
        assertThat(privilegeRepository.isPrivilegeInUse("admin"), is(true));
        assertThat(privilegeRepository.isPrivilegeInUse("write"), is(true));
        assertThat(privilegeRepository.isPrivilegeInUse("read"), is(true));
        assertThat(privilegeRepository.isPrivilegeInUse("used"), is(false));
    }
    
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/core/PermissionRepositoryTest/"
                      + "data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(60)
    public void deleteInUsePrivilege() {
        privilegeRepository.deletePrivilege("admin");
    }

}
