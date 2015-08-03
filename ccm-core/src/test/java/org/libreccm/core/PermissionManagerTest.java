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

import java.io.File;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.PersistenceTest;
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

import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
public class PermissionManagerTest {

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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
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
                        String.format("LibreCCM-%s.war",
                                      PermissionManagerTest.class.getName()))
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
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                          + "data.json")
    @InSequence(110)
    public void isPermittedGrantedByAdminPrivilege() {
        fail();
    }
    
    @Test
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                          + "data.json")
    @InSequence(120)
    public void isPermittedGrantedByDirectPermission() {
        fail();
    }
    
    @Test
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                          + "data.json")
    @InSequence(130)
    public void isPermittedGrantedByGroup() {
        fail();
    }
    
    @Test
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                          + "data.json")
    @InSequence(140)
    public void isPermittedPublicUserGranted() {
        fail();
    }
    
    @Test
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                          + "data.json")
    @InSequence(150)
    public void isPermittedPublicUserDenied() {
        fail();
    }
    
    @Test(expected = UnauthorizedAcccessException.class)
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                          + "data.json")
    @ShouldThrowException(UnauthorizedAcccessException.class)
    @InSequence(210)
    public void checkPermittedGrantedByAdminPrivilege() {
        fail();
    }
    
    @Test(expected = UnauthorizedAcccessException.class)
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                          + "data.json")
    @ShouldThrowException(UnauthorizedAcccessException.class)
    @InSequence(220)
    public void checkPermittedGrantedByDirectPermission() {
        fail();
    }
    
    @Test(expected = UnauthorizedAcccessException.class)
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                          + "data.json")
    @ShouldThrowException(UnauthorizedAcccessException.class)
    @InSequence(230)
    public void checkPermittedGrantedByGroup() {
        fail();
    }
    
    @Test(expected = UnauthorizedAcccessException.class)
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                          + "data.json")
    @ShouldThrowException(UnauthorizedAcccessException.class)
    @InSequence(240)
    public void checkPermittedPublicUserGranted() {
        fail();
    }
    
    @Test(expected = UnauthorizedAcccessException.class)
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                          + "data.json")
    @ShouldThrowException(UnauthorizedAcccessException.class)
    @InSequence(250)
    public void checkPermittedPublicUserDenied() {
        fail();
    }
}
